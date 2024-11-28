package de.mpg.mpdl.inge.service.pubman.impl;

import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLog;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogItemDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogItemDetailDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.ContextService;
import de.mpg.mpdl.inge.service.pubman.ImportService;
import de.mpg.mpdl.inge.service.pubman.importprocess.ImportAsyncService;
import de.mpg.mpdl.inge.service.pubman.importprocess.ImportCommonService;
import de.mpg.mpdl.inge.service.pubman.importprocess.processor.FormatProcessor;
import de.mpg.mpdl.inge.service.util.GrantUtil;
import de.mpg.mpdl.inge.transformation.SingleTransformer;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.util.PropertyReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class ImportServiceImpl implements ImportService {

  private final AuthorizationService authorizationService;
  private final ContextService contextService;
  private final ImportAsyncService importAsyncService;
  private final ImportCommonService importCommonService;

  public ImportServiceImpl(AuthorizationService authorizationService, ContextService contextService, ImportAsyncService importAsyncService,
      ImportCommonService importCommonService) {
    this.authorizationService = authorizationService;
    this.contextService = contextService;
    this.importAsyncService = importAsyncService;
    this.importCommonService = importCommonService;
  }

  @Override
  public void deleteImportLog(Integer importLogId, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException {
    AccountUserDbVO accountUserDbVO = getUser(token);

    ImportLogDbVO importLogDbVO = this.importCommonService.getImportLog(importLogId, accountUserDbVO);
    if (null == importLogDbVO) {
      throw new IngeApplicationException("Invalid importLogId");
    }

    checkUserAccess(importLogDbVO, accountUserDbVO);

    this.importCommonService.deleteImportLog(importLogDbVO);
  }

  @Override
  public void deleteImportedItems(Integer importLogId, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException {
    AccountUserDbVO accountUserDbVO = getUser(token);

    ImportLogDbVO importLogDbVO = this.importCommonService.getImportLog(importLogId, accountUserDbVO);
    if (null == importLogDbVO) {
      throw new IngeApplicationException("Invalid importLogId");
    }

    checkUserAccess(importLogDbVO, accountUserDbVO);

    if (!importLogDbVO.getStatus().equals(ImportLog.Status.FINISHED)) {
      throw new IngeApplicationException("Status must be FINISHED");
    }

    int anz = this.importCommonService.countImportedLogItems(importLogDbVO);
    if (0 == anz) {
      throw new IngeApplicationException("There are no imported items to delete");
    }

    this.importCommonService.initializeDelete(importLogDbVO);
    this.importAsyncService.doAsyncDelete(importLogDbVO, token);
  }

  @Override
  public void doImport(String importName, String contextId, ImportLogDbVO.Format format, InputStream fileStream, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException, IngeTechnicalException {
    AccountUserDbVO accountUserDbVO = getUser(token);

    if (null == importName || importName.trim().isEmpty()) {
      throw new IngeApplicationException("The import name must not be empty");
    }

    ContextDbVO contextDbVO = this.contextService.get(contextId, token);
    if (null == contextDbVO) {
      throw new IngeApplicationException("Import context must not be empty");
    } else if (!contextDbVO.getState().equals(ContextDbVO.State.OPENED)) {
      throw new IngeApplicationException("Import context is not opened");
    }

    ImportLogDbVO importLogDbVO = this.importCommonService.initializeImport(accountUserDbVO, format, importName, contextId);

    ImportLogItemDbVO importLogItemDbVO = this.importCommonService.createImportLogItem(importLogDbVO, ImportLog.ErrorLevel.FINE,
        ImportLog.Messsage.import_process_validate.name());

    FormatProcessor formatProcessor = null;
    try {
      formatProcessor = this.importCommonService.getFormatProcessor(importLogDbVO, format);
    } catch (Exception e) {
      this.importCommonService.createImportLogItem(importLogDbVO, ImportLog.ErrorLevel.FATAL,
          ImportLog.Messsage.import_process_format_error.name());
      this.importCommonService.doFailImport(importLogDbVO, importLogItemDbVO, this.importCommonService.getExceptionMessage(e));
      return;
    }

    if (null == formatProcessor) {
      this.importCommonService.doFailImport(importLogDbVO, importLogItemDbVO, ImportLog.Messsage.import_process_format_invalid.name());
      return;
    } else {
      this.importCommonService.createImportLogItem(importLogDbVO, ImportLog.ErrorLevel.FINE,
          ImportLog.Messsage.import_process_format_available.name());
    }

    File file = null;
    try {
      file = convertInputStreamToFile(fileStream);
    } catch (IOException e) {
    }

    if (null == file) {
      this.importCommonService.doFailImport(importLogDbVO, importLogItemDbVO,
          ImportLog.Messsage.import_process_inputstream_unavailable.name());
      return;
    } else {
      formatProcessor.setSourceFile(file);
      this.importCommonService.createImportLogItem(importLogDbVO, ImportLog.ErrorLevel.FINE,
          ImportLog.Messsage.import_process_inputstream_available.name());
    }

    this.importCommonService.setPercentageInImportLog(importLogDbVO, ImportLogDbVO.PERCENTAGE_IMPORT_START);

    this.importAsyncService.doAsyncImport(importLogDbVO, formatProcessor, format, contextDbVO, token);
  }

  @Override
  public Map<String, List<String>> getAllFormatParameter(ImportLogDbVO.Format format, String token)
      throws AuthenticationException, IngeApplicationException, IngeTechnicalException {
    AccountUserDbVO accountUserDbVO = getUser(token);

    //    BIBTEX_STRING (CoNE)						SingleTransformer.getAllConfigurationValuesFromProperty(PropertyReader.INGE_TRANSFORMATION_ENDNOTE_CONFIGURATION_FILENAME);
    //    BMC_XML (CoNE, Files to Import),			SingleTransformer.getAllConfigurationValuesFromProperty(PropertyReader.INGE_TRANSFORMATION_BMC2ESCIDOC_CONFIGURATION_FILENAME);
    //    EDOC_XML (CoNE, Schema),					SingleTransformer.getAllConfigurationValuesFromProperty(PropertyReader.INGE_TRANSFORMATION_EDOC_CONFIGURATION_FILENAME);
    //    ENDNOTE_STRING (CoNE, Schema)				SingleTransformer.getAllConfigurationValuesFromProperty(PropertyReader.INGE_TRANSFORMATION_ENDNOTE_CONFIGURATION_FILENAME);
    //    ESCIDOC_ITEM_V3_XML (keine Properties)	null
    //    MAB_STRING (keine Properties),			null
    //    MARC_XML (CoNE),							SingleTransformer.getAllConfigurationValuesFromProperty(PropertyReader.INGE_TRANSFORMATION_MARCXML2ESCIDOC_CONFIGURATION_FILENAME);
    //    MARC_21_STRING (CoNE),                    SingleTransformer.getAllConfigurationValuesFromProperty(PropertyReader.INGE_TRANSFORMATION_MARCXML2ESCIDOC_CONFIGURATION_FILENAME);
    //    RIS_STRING (CoNE, Schema),				SingleTransformer.getAllConfigurationValuesFromProperty(PropertyReader.INGE_TRANSFORMATION_RIS_CONFIGURATION_FILENAME);
    //    WOS_STRING (CoNE, Schema),				SingleTransformer.getAllConfigurationValuesFromProperty(PropertyReader.INGE_TRANSFORMATION_WOS_CONFIGURATION_FILENAME);

    Map<String, List<String>> formatParameter = null;

    try {
      switch (format) {
        case BIBTEX_STRING -> {
          formatParameter = SingleTransformer.getAllConfigurationValuesFromProperty(PropertyReader.INGE_TRANSFORMATION_ENDNOTE_CONFIGURATION_FILENAME);
        }
        case BMC_XML -> {
          formatParameter = SingleTransformer.getAllConfigurationValuesFromProperty(PropertyReader.INGE_TRANSFORMATION_BMC2ESCIDOC_CONFIGURATION_FILENAME);
        }
        case EDOC_XML -> {
          formatParameter = SingleTransformer.getAllConfigurationValuesFromProperty(PropertyReader.INGE_TRANSFORMATION_EDOC_CONFIGURATION_FILENAME);
        }
        case ENDNOTE_STRING -> {
          formatParameter = SingleTransformer.getAllConfigurationValuesFromProperty(PropertyReader.INGE_TRANSFORMATION_ENDNOTE_CONFIGURATION_FILENAME);
        }
        case ESCIDOC_ITEM_V3_XML -> {
          return null;
        }
        case MAB_STRING -> {
          return null;
        }
        case MARC_XML -> {
          formatParameter = SingleTransformer.getAllConfigurationValuesFromProperty(PropertyReader.INGE_TRANSFORMATION_MARCXML2ESCIDOC_CONFIGURATION_FILENAME);
        }
        case MARC_21_STRING -> {
          formatParameter = SingleTransformer.getAllConfigurationValuesFromProperty(PropertyReader.INGE_TRANSFORMATION_MARCXML2ESCIDOC_CONFIGURATION_FILENAME);
        }
        case RIS_STRING -> {
          formatParameter = SingleTransformer.getAllConfigurationValuesFromProperty(PropertyReader.INGE_TRANSFORMATION_RIS_CONFIGURATION_FILENAME);
        }
        case WOS_STRING -> {
          formatParameter = SingleTransformer.getAllConfigurationValuesFromProperty(PropertyReader.INGE_TRANSFORMATION_WOS_CONFIGURATION_FILENAME);
        }
        default -> {
          throw new IngeTechnicalException("Invalid format " + format);
        }
      }
    } catch (TransformationException e) {
      throw new IngeTechnicalException("Error while getting format parameter for " + format, e);
    }

    return formatParameter;
  }

  @Override
  public Map<String, String> getDefaultFormatParameter(ImportLogDbVO.Format format, String token)
      throws AuthenticationException, IngeApplicationException, IngeTechnicalException {
    AccountUserDbVO accountUserDbVO = getUser(token);

    //    BIBTEX_STRING (CoNE)						SingleTransformer.getDefaultConfigurationFromProperty(PropertyReader.INGE_TRANSFORMATION_ENDNOTE_CONFIGURATION_FILENAME);
    //    BMC_XML (CoNE, Files to Import),			SingleTransformer.getDefaultConfigurationFromProperty(PropertyReader.INGE_TRANSFORMATION_BMC2ESCIDOC_CONFIGURATION_FILENAME);
    //    EDOC_XML (CoNE, Schema),					SingleTransformer.getDefaultConfigurationFromProperty(PropertyReader.INGE_TRANSFORMATION_EDOC_CONFIGURATION_FILENAME);
    //    ENDNOTE_STRING (CoNE, Schema)				SingleTransformer.getDefaultConfigurationFromProperty(PropertyReader.INGE_TRANSFORMATION_ENDNOTE_CONFIGURATION_FILENAME);
    //    ESCIDOC_ITEM_V3_XML (keine Properties)	null
    //    MAB_STRING (keine Properties),			null
    //    MARC_XML (CoNE),							SingleTransformer.getDefaultConfigurationFromProperty(PropertyReader.INGE_TRANSFORMATION_MARCXML2ESCIDOC_CONFIGURATION_FILENAME);
    //    MARC_21_STRING (CoNE),                    SingleTransformer.getDefaultConfigurationFromProperty(PropertyReader.INGE_TRANSFORMATION_MARCXML2ESCIDOC_CONFIGURATION_FILENAME);
    //    RIS_STRING (CoNE, Schema),				SingleTransformer.getDefaultConfigurationFromProperty(PropertyReader.INGE_TRANSFORMATION_RIS_CONFIGURATION_FILENAME);
    //    WOS_STRING (CoNE, Schema),				SingleTransformer.getDefaultConfigurationFromProperty(PropertyReader.INGE_TRANSFORMATION_WOS_CONFIGURATION_FILENAME);

    Map<String, String> formatParameter = null;

    try {
      switch (format) {
        case BIBTEX_STRING -> {
          formatParameter = SingleTransformer.getDefaultConfigurationFromProperty(PropertyReader.INGE_TRANSFORMATION_ENDNOTE_CONFIGURATION_FILENAME);
        }
        case BMC_XML -> {
          formatParameter = SingleTransformer.getDefaultConfigurationFromProperty(PropertyReader.INGE_TRANSFORMATION_BMC2ESCIDOC_CONFIGURATION_FILENAME);
        }
        case EDOC_XML -> {
          formatParameter = SingleTransformer.getDefaultConfigurationFromProperty(PropertyReader.INGE_TRANSFORMATION_EDOC_CONFIGURATION_FILENAME);
        }
        case ENDNOTE_STRING -> {
          formatParameter = SingleTransformer.getDefaultConfigurationFromProperty(PropertyReader.INGE_TRANSFORMATION_ENDNOTE_CONFIGURATION_FILENAME);
        }
        case ESCIDOC_ITEM_V3_XML -> {
          return null;
        }
        case MAB_STRING -> {
          return null;
        }
        case MARC_XML -> {
          formatParameter = SingleTransformer.getDefaultConfigurationFromProperty(PropertyReader.INGE_TRANSFORMATION_MARCXML2ESCIDOC_CONFIGURATION_FILENAME);
        }
        case MARC_21_STRING -> {
          formatParameter = SingleTransformer.getDefaultConfigurationFromProperty(PropertyReader.INGE_TRANSFORMATION_MARCXML2ESCIDOC_CONFIGURATION_FILENAME);
        }
        case RIS_STRING -> {
          formatParameter = SingleTransformer.getDefaultConfigurationFromProperty(PropertyReader.INGE_TRANSFORMATION_RIS_CONFIGURATION_FILENAME);
        }
        case WOS_STRING -> {
          formatParameter = SingleTransformer.getDefaultConfigurationFromProperty(PropertyReader.INGE_TRANSFORMATION_WOS_CONFIGURATION_FILENAME);
        }
        default -> {
          throw new IngeTechnicalException("Invalid format " + format);
        }
      }
    } catch (TransformationException e) {
      throw new IngeTechnicalException("Error while getting format parameter for " + format, e);
    }

    return formatParameter;
  }

  @Override
  public List<ImportLogItemDetailDbVO> getImportLogItemDetails(Integer importLogItemId, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException {
    AccountUserDbVO accountUserDbVO = getUser(token);

    ImportLogItemDbVO importLogItemDbVO = this.importCommonService.getImportLogItem(importLogItemId);
    if (null == importLogItemDbVO) {
      throw new IngeApplicationException("Invalid importLogItemId");
    }

    checkUserAccess(importLogItemDbVO.getParent(), accountUserDbVO);

    List<ImportLogItemDetailDbVO> importLogItemDetailDbVOs = this.importCommonService.getImportLogItemDetails(importLogItemDbVO);

    return importLogItemDetailDbVOs;
  }

  @Override
  public List<ImportLogItemDbVO> getImportLogItems(Integer importLogId, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException {
    AccountUserDbVO accountUserDbVO = getUser(token);

    ImportLogDbVO importLogDbVO = this.importCommonService.getImportLog(importLogId, accountUserDbVO);
    if (null == importLogDbVO) {
      throw new IngeApplicationException("Invalid importLogId");
    }

    checkUserAccess(importLogDbVO, accountUserDbVO);

    List<ImportLogItemDbVO> importLogItemDbVOs = this.importCommonService.getImportLogItems(importLogDbVO);

    return importLogItemDbVOs;
  }

  @Override
  public List<ImportLogDbVO> getImportLogs(String token) throws AuthenticationException, IngeApplicationException {
    AccountUserDbVO accountUserDbVO = getUser(token);

    List<ImportLogDbVO> importLogDbVOs = this.importCommonService.getUserImportLogs(accountUserDbVO.getObjectId());

    return importLogDbVOs;
  }

  @Override
  public List<ImportLogDbVO> getImportLogsForModerator(String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException, IngeTechnicalException {
    AccountUserDbVO accountUserDbVO = getUser(token);

    List<String> moderatorContexts = new ArrayList();
    List<GrantVO> grantVOs = accountUserDbVO.getGrantList();
    for (GrantVO grantVO : grantVOs) {
      if (grantVO.getRole().equals(GrantVO.PredefinedRoles.MODERATOR.frameworkValue())) {
        String contextId = grantVO.getObjectRef();
        ContextDbVO contextDbVO = this.contextService.get(contextId, token);
        if (contextDbVO.getState().equals(ContextDbVO.State.OPENED)) {
          moderatorContexts.add(contextId);
        }
      }
    }

    List<ImportLogDbVO> importLogDbVOs = new ArrayList();
    for (String moderatorContext : moderatorContexts) {
      importLogDbVOs.addAll(this.importCommonService.getContextImportLogs(moderatorContext));
    }

    return importLogDbVOs;
  }

  @Override
  public void submitImportedItems(Integer importLogId, ImportLog.SubmitModus submitModus, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException, IngeTechnicalException {
    AccountUserDbVO accountUserDbVO = getUser(token);

    ImportLogDbVO importLogDbVO = this.importCommonService.getImportLog(importLogId, accountUserDbVO);
    if (null == importLogDbVO) {
      throw new IngeApplicationException("Invalid importLogId");
    }

    checkUserAccess(importLogDbVO, accountUserDbVO);

    if (!importLogDbVO.getStatus().equals(ImportLog.Status.FINISHED)) {
      throw new IngeApplicationException("Status must be FINISHED");
    }

    int anz = this.importCommonService.countImportedLogItems(importLogDbVO);
    if (0 == anz) {
      throw new IngeApplicationException("There are no imported items to submit");
    }

    ContextDbVO contextDbVO = this.contextService.get(importLogDbVO.getContextId(), token);
    if (!contextDbVO.getState().equals(ContextDbVO.State.OPENED)) {
      throw new IngeApplicationException("Import context is not opened");
    }

    switch (submitModus) {
      case SUBMIT:
        if (!ContextDbVO.Workflow.STANDARD.equals(contextDbVO.getWorkflow()) //
            || !GrantUtil.hasRole(accountUserDbVO, GrantVO.PredefinedRoles.DEPOSITOR, contextDbVO.getObjectId()) //
            || GrantUtil.hasRole(accountUserDbVO, GrantVO.PredefinedRoles.MODERATOR, contextDbVO.getObjectId())) {
          throw new IngeApplicationException("User is not Depositor and/or Workflow ist not Standard");
        }
        break;
      case SUBMIT_AND_RELEASE:
        if (!ContextDbVO.Workflow.STANDARD.equals(contextDbVO.getWorkflow()) //
            || !GrantUtil.hasRole(accountUserDbVO, GrantVO.PredefinedRoles.MODERATOR, contextDbVO.getObjectId())) {
          throw new IngeApplicationException("User is not Moderator and/or Workflow ist not Standard");
        }
        break;
      case RELEASE:
        if (!ContextDbVO.Workflow.SIMPLE.equals(contextDbVO.getWorkflow()) //
            || !GrantUtil.hasRole(accountUserDbVO, GrantVO.PredefinedRoles.DEPOSITOR, contextDbVO.getObjectId()) //
                && !GrantUtil.hasRole(accountUserDbVO, GrantVO.PredefinedRoles.MODERATOR, contextDbVO.getObjectId())) {
          throw new IngeApplicationException("User is not Depositor or Moderator and/or Workflow ist not Simple");
        }
        break;
      default:
        throw new IngeApplicationException("Invalid submitModus");
    }

    this.importCommonService.initializeSubmit(importLogDbVO, submitModus);
    this.importAsyncService.doAsyncSubmit(importLogDbVO, submitModus, token);
  }

  /// /////////////////////////////////////////////////////////////////////////////////////////////////////////////
  /// /////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private void checkUserAccess(ImportLogDbVO importLogDbVO, AccountUserDbVO accountUserDbVO) throws AuthorizationException {
    if (null != importLogDbVO && !accountUserDbVO.getObjectId().equals(importLogDbVO.getUserId())) {
      throw new AuthorizationException("given user is not allowed to access the import.");
    }
  }

  private File convertInputStreamToFile(InputStream inputStream) throws IOException {
    String TMP_FILE_ROOT_PATH = System.getProperty(PropertyReader.JBOSS_HOME_DIR)
        + PropertyReader.getProperty(PropertyReader.INGE_LOGIC_TEMPORARY_FILESYSTEM_ROOT_PATH);
    Path tmpFilePath = Paths.get(TMP_FILE_ROOT_PATH, "import_" + UUID.randomUUID());
    Files.copy(inputStream, tmpFilePath);
    File file = tmpFilePath.toFile();

    return file;
  }

  private AccountUserDbVO getUser(String token) throws AuthenticationException, IngeApplicationException {
    AccountUserDbVO accountUserDbVO = this.authorizationService.getUserAccountFromToken(token);

    return accountUserDbVO;
  }
}
