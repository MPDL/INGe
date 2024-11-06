package de.mpg.mpdl.inge.service.pubman.impl;

import de.mpg.mpdl.inge.db.repository.ImportLogItemDetailRepository;
import de.mpg.mpdl.inge.db.repository.ImportLogItemRepository;
import de.mpg.mpdl.inge.db.repository.ImportLogRepository;
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
import de.mpg.mpdl.inge.service.util.GrantUtil;
import java.util.List;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Primary
public class ImportServiceImpl implements ImportService {

  private final AuthorizationService authorizationService;
  private final ContextService contextService;
  private final ImportAsyncService importAsyncService;
  private final ImportCommonService importCommonService;
  private final ImportLogRepository importLogRepository;
  private final ImportLogItemRepository importLogItemRepository;
  private final ImportLogItemDetailRepository importLogItemDetailRepository;

  public ImportServiceImpl(AuthorizationService authorizationService, ContextService contextService, ImportAsyncService importAsyncService,
      ImportCommonService importCommonService, ImportLogRepository importLogRepository, ImportLogItemRepository importLogItemRepository,
      ImportLogItemDetailRepository importLogItemDetailRepository) {
    this.authorizationService = authorizationService;
    this.contextService = contextService;
    this.importAsyncService = importAsyncService;
    this.importCommonService = importCommonService;
    this.importLogRepository = importLogRepository;
    this.importLogItemRepository = importLogItemRepository;
    this.importLogItemDetailRepository = importLogItemDetailRepository;
  }

  @Transactional(rollbackFor = Throwable.class)
  @Override
  public void deleteImportLog(Integer importLogId, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException {
    AccountUserDbVO accountUserDbVO = getUser(token);

    ImportLogDbVO importLogDbVO = getImportLog(importLogId, accountUserDbVO);
    if (null == importLogDbVO) {
      throw new IngeApplicationException("Invalid importLogId");
    }

    checkUserAccess(importLogDbVO, accountUserDbVO);

    this.importLogRepository.delete(importLogDbVO);
  }

  @Override
  public void deleteImportedItems(Integer importLogId, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException {
    AccountUserDbVO accountUserDbVO = getUser(token);

    ImportLogDbVO importLogDbVO = getImportLog(importLogId, accountUserDbVO);
    if (null == importLogDbVO) {
      throw new IngeApplicationException("Invalid importLogId");
    }

    checkUserAccess(importLogDbVO, accountUserDbVO);

    if (!importLogDbVO.getStatus().equals(ImportLog.Status.FINISHED)) {
      throw new IngeApplicationException("Status must be FINISHED");
    }

    List<ImportLogItemDbVO> importedLogItemDbVOs = getImportedLogItemDbVOs(importLogDbVO);
    if (null == importedLogItemDbVOs || importedLogItemDbVOs.isEmpty()) {
      throw new IngeApplicationException("There are no imported items to delete");
    }

    this.importCommonService.initializeDelete(importLogDbVO);
    this.importAsyncService.doAsyncDelete(importLogDbVO, importedLogItemDbVOs, token);
  }

  @Override
  public List<ImportLogItemDetailDbVO> getImportLogItemDetails(Integer importLogItemId, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException {
    AccountUserDbVO accountUserDbVO = getUser(token);

    ImportLogItemDbVO importLogItemDbVO = this.importLogItemRepository.findById(importLogItemId).orElse(null);
    if (null == importLogItemDbVO) {
      throw new IngeApplicationException("Invalid importLogItemId");
    }

    checkUserAccess(importLogItemDbVO.getParent(), accountUserDbVO);

    List<ImportLogItemDetailDbVO> importLogItemDetailDbVOs = this.importLogItemDetailRepository.findByImportLogItem(importLogItemDbVO);

    return importLogItemDetailDbVOs;
  }

  @Override
  public List<ImportLogItemDbVO> getImportLogItems(Integer importLogId, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException {
    AccountUserDbVO accountUserDbVO = getUser(token);

    ImportLogDbVO importLogDbVO = getImportLog(importLogId, accountUserDbVO);
    if (null == importLogDbVO) {
      throw new IngeApplicationException("Invalid importLogId");
    }

    checkUserAccess(importLogDbVO, accountUserDbVO);

    List<ImportLogItemDbVO> importLogItemDbVOs = this.importLogItemRepository.findByParent(importLogDbVO);

    return importLogItemDbVOs;
  }

  @Override
  public List<ImportLogDbVO> getImportLogs(String token) throws AuthenticationException, IngeApplicationException {
    AccountUserDbVO accountUserDbVO = getUser(token);

    List<ImportLogDbVO> importLogDbVOs = this.importLogRepository.findAllByUserId(accountUserDbVO.getObjectId());

    return importLogDbVOs;
  }

  @Override
  public void submitImportedItems(Integer importLogId, ImportLog.SubmitModus submitModus, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException, IngeTechnicalException {
    AccountUserDbVO accountUserDbVO = getUser(token);

    ImportLogDbVO importLogDbVO = getImportLog(importLogId, accountUserDbVO);
    if (null == importLogDbVO) {
      throw new IngeApplicationException("Invalid importLogId");
    }

    checkUserAccess(importLogDbVO, accountUserDbVO);

    if (!importLogDbVO.getStatus().equals(ImportLog.Status.FINISHED)) {
      throw new IngeApplicationException("Status must be FINISHED");
    }

    List<ImportLogItemDbVO> importedLogItemDbVOs = getImportedLogItemDbVOs(importLogDbVO);
    if (null == importedLogItemDbVOs || importedLogItemDbVOs.isEmpty()) {
      throw new IngeApplicationException("There are no imported items to submit");
    }

    ContextDbVO contextDbVO = this.contextService.get(importLogDbVO.getContextId(), token);
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
    this.importAsyncService.doAsyncSubmit(importLogDbVO, importedLogItemDbVOs, submitModus, token);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private void checkUserAccess(ImportLogDbVO importLogDbVO, AccountUserDbVO accountUserDbVO) throws AuthorizationException {
    if (null != importLogDbVO && !accountUserDbVO.getObjectId().equals(importLogDbVO.getUserId())) {
      throw new AuthorizationException("given user is not allowed to access the import.");
    }
  }

  private ImportLogDbVO getImportLog(Integer importLogId, AccountUserDbVO accountUserDbVO) {
    ImportLogDbVO importLogDbVO = this.importLogRepository.findById(importLogId).orElse(null);

    return importLogDbVO;
  }

  private List<ImportLogItemDbVO> getImportedLogItemDbVOs(ImportLogDbVO importLogDbVO) {
    List<ImportLogItemDbVO> importedLogItemDbVOs = this.importLogItemRepository.findByParentAndItemId(importLogDbVO);

    return importedLogItemDbVOs;
  }

  private AccountUserDbVO getUser(String token) throws AuthenticationException, IngeApplicationException {
    AccountUserDbVO accountUserDbVO = this.authorizationService.getUserAccountFromToken(token);

    return accountUserDbVO;
  }
}
