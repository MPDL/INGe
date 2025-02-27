package de.mpg.mpdl.inge.service.pubman.impl;

import de.mpg.mpdl.inge.db.repository.ImportLogItemDetailRepository;
import de.mpg.mpdl.inge.db.repository.ImportLogItemRepository;
import de.mpg.mpdl.inge.db.repository.ImportLogRepository;
import de.mpg.mpdl.inge.inge_validation.ItemValidatingService;
import de.mpg.mpdl.inge.inge_validation.data.ValidationReportItemVO;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.inge_validation.util.ValidationPoint;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLog;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogItemDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogItemDetailDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.util.EntityTransformer;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.ItemTransformingService;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.pubman.importprocess.ImportCommonService;
import de.mpg.mpdl.inge.service.pubman.importprocess.processor.BibtexProcessor;
import de.mpg.mpdl.inge.service.pubman.importprocess.processor.BmcProcessor;
import de.mpg.mpdl.inge.service.pubman.importprocess.processor.EdocProcessor;
import de.mpg.mpdl.inge.service.pubman.importprocess.processor.EndnoteProcessor;
import de.mpg.mpdl.inge.service.pubman.importprocess.processor.EscidocProcessor;
import de.mpg.mpdl.inge.service.pubman.importprocess.processor.FormatProcessor;
import de.mpg.mpdl.inge.service.pubman.importprocess.processor.MabProcessor;
import de.mpg.mpdl.inge.service.pubman.importprocess.processor.Marc21Processor;
import de.mpg.mpdl.inge.service.pubman.importprocess.processor.MarcXmlProcessor;
import de.mpg.mpdl.inge.service.pubman.importprocess.processor.RisProcessor;
import de.mpg.mpdl.inge.service.pubman.importprocess.processor.WosProcessor;
import de.mpg.mpdl.inge.service.util.PubItemUtil;
import de.mpg.mpdl.inge.transformation.TransformerFactory;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Primary
public class ImportCommonServiceImpl implements ImportCommonService {

  private static final Logger logger = LogManager.getLogger(ImportCommonServiceImpl.class);

  private final ImportLogRepository importLogRepository;
  private final ImportLogItemRepository importLogItemRepository;
  private final ImportLogItemDetailRepository importLogItemDetailRepository;
  private final PubItemService pubItemService;
  private final ItemTransformingService itemTransformingService;
  private final ItemValidatingService itemValidatingService;

  public ImportCommonServiceImpl(ImportLogRepository importLogRepository, ImportLogItemRepository importLogItemRepository,
      ImportLogItemDetailRepository importLogItemDetailRepository, PubItemService pubItemService,
      ItemTransformingService itemTransformingService, ItemValidatingService itemValidatingService) {
    this.importLogRepository = importLogRepository;
    this.importLogItemRepository = importLogItemRepository;
    this.importLogItemDetailRepository = importLogItemDetailRepository;
    this.pubItemService = pubItemService;
    this.itemTransformingService = itemTransformingService;
    this.itemValidatingService = itemValidatingService;
  }

  @Override
  public int countImportedLogItems(ImportLogDbVO importLogDbVO) {
    int anz = this.importLogItemRepository.countByParentAndItemId(importLogDbVO);

    return anz;
  }

  @Transactional(rollbackFor = Throwable.class)
  public ImportLogDbVO createImportLog(String userId, ImportLogDbVO.Format format, String importName, String contextId) {

    ImportLogDbVO importLogDbVO = new ImportLogDbVO(userId, format, importName, contextId);
    importLogDbVO = this.importLogRepository.saveAndFlush(importLogDbVO);

    return importLogDbVO;
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public ImportLogItemDbVO createImportLogItem(ImportLogDbVO importLogDbVO, ImportLog.ErrorLevel errorLevel, String message) {
    importLogDbVO.setErrorLevel(errorLevel);
    importLogDbVO = this.importLogRepository.saveAndFlush(importLogDbVO);

    ImportLogItemDbVO importLogItemDbVO = new ImportLogItemDbVO(importLogDbVO, errorLevel, message);
    importLogItemDbVO.setEndDate(new Date());
    importLogItemDbVO.setStatus(ImportLog.Status.FINISHED);
    importLogItemDbVO = this.importLogItemRepository.saveAndFlush(importLogItemDbVO);

    return importLogItemDbVO;
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public ImportLogItemDetailDbVO createImportLogItemDetail(ImportLogItemDbVO importLogItemDbVO, ImportLog.ErrorLevel errorLevel,
      String message) {
    ImportLogDbVO importLogDbVO = importLogItemDbVO.getParent();
    importLogDbVO.setErrorLevel(errorLevel);
    importLogDbVO = this.importLogRepository.saveAndFlush(importLogDbVO);

    importLogItemDbVO.setParent(importLogDbVO);
    importLogItemDbVO.setErrorLevel(errorLevel);
    importLogItemDbVO = this.importLogItemRepository.saveAndFlush(importLogItemDbVO);

    ImportLogItemDetailDbVO importLogItemDetailDbVO = new ImportLogItemDetailDbVO(importLogItemDbVO, errorLevel, message);
    importLogItemDetailDbVO = this.importLogItemDetailRepository.saveAndFlush(importLogItemDetailDbVO);

    return importLogItemDetailDbVO;
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void createItem(ItemVersionVO itemVersionVO, String localTag, ImportLogItemDbVO importLogItemDbVO, String token)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    itemVersionVO.getObject().getLocalTags().add("multiple_import");
    itemVersionVO.getObject().getLocalTags().add(localTag.toString());
    createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE, "Local Tag: " + localTag);
    createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE, ImportLog.Message.import_process_save_item.name());
    ItemVersionVO savedPubItem = this.pubItemService.create(itemVersionVO, token);
    setItemIdInImportLogItem(importLogItemDbVO, savedPubItem.getObjectId());
    createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE, ImportLog.Message.import_process_item_imported.name());
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void deleteImportLog(ImportLogDbVO importLogDbVO) {
    this.importLogRepository.delete(importLogDbVO);
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void doDelete(ImportLogItemDbVO importLogItemDbVO, String token)
      throws AuthenticationException, AuthorizationException, IngeApplicationException, IngeTechnicalException {
    this.pubItemService.delete(importLogItemDbVO.getItemId(), token);
    createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE, ImportLog.Message.import_process_delete_successful.name());
    createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE, ImportLog.Message.import_process_remove_identifier.name());
    resetItemId(importLogItemDbVO);
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void doFailDelete(ImportLogItemDbVO importLogItemDbVO, String message) {
    createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.ERROR, ImportLog.Message.import_process_delete_failed.name());
    createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.ERROR, message);
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void doFailImport(ImportLogDbVO importLogDbVO, String message, boolean newDetail) {
    if (false == newDetail) {
      createImportLogItem(importLogDbVO, ImportLog.ErrorLevel.FATAL, message);
      createImportLogItem(importLogDbVO, ImportLog.ErrorLevel.FATAL, ImportLog.Message.import_process_failed.name());
    } else {
      ImportLogItemDbVO importLogItemDbVO =
          createImportLogItem(importLogDbVO, ImportLog.ErrorLevel.FATAL, ImportLog.Message.import_process_failed.name());
      createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FATAL, message);
    }
    finishImportLog(importLogDbVO);
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void doFailSubmit(ImportLogItemDbVO importLogItemDbVO, ImportLog.SubmitModus submitModus, String message) {
    switch (submitModus) {
      case SUBMIT:
        createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.ERROR, ImportLog.Message.import_process_submit_failed.name());
        createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.ERROR, message);
        break;
      case SUBMIT_AND_RELEASE:
        createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.ERROR,
            ImportLog.Message.import_process_submit_release_failed.name());
        createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.ERROR, message);
        break;
      case RELEASE:
        createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.ERROR, ImportLog.Message.import_process_release_failed.name());
        createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.ERROR, message);
        break;
    }
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void doSubmit(ImportLogDbVO importLogDbVO, ImportLogItemDbVO importLogItemDbVO, ImportLog.SubmitModus submitModus, String token)
      throws AuthenticationException, AuthorizationException, IngeApplicationException, IngeTechnicalException {
    ItemVersionVO itemVersionVO = pubItemService.get(importLogItemDbVO.getItemId(), token);

    switch (submitModus) {
      case SUBMIT:
        this.pubItemService.submitPubItem(importLogItemDbVO.getItemId(), itemVersionVO.getModificationDate(),
            "Batch submit from import " + importLogDbVO.getName(), token);
        createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE, ImportLog.Message.import_process_submit_successful.name());
        break;
      case SUBMIT_AND_RELEASE:
        this.pubItemService.submitPubItem(importLogItemDbVO.getItemId(), itemVersionVO.getModificationDate(),
            "Batch submit (and release) from import " + importLogDbVO.getName(), token);
        ItemVersionVO itemVersionVO_ = pubItemService.get(importLogItemDbVO.getItemId(), token);
        this.pubItemService.releasePubItem(importLogItemDbVO.getItemId(), itemVersionVO.getModificationDate(),
            "Batch (submit and) release from import " + importLogDbVO.getName(), token);
        createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE,
            ImportLog.Message.import_process_submit_release_successful.name());
        break;
      case RELEASE:
        this.pubItemService.releasePubItem(importLogItemDbVO.getItemId(), itemVersionVO.getModificationDate(),
            "Batch release from import " + importLogDbVO.getName(), token);
        createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE, ImportLog.Message.import_process_release_successful.name());
        break;
    }
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void finishDelete(ImportLogDbVO importLogDbVO) {
    createImportLogItem(importLogDbVO, ImportLog.ErrorLevel.FINE, ImportLog.Message.import_process_delete_finished.name());
    finishImportLog(importLogDbVO);
  }

  @Transactional(rollbackFor = Throwable.class)
  @Override
  public void finishImport(ImportLogDbVO importLogDbVO) {
    ImportLogItemDbVO importLogItemDbVO =
        createImportLogItem(importLogDbVO, ImportLog.ErrorLevel.FINE, ImportLog.Message.import_process_finished.name());
    finishImportLog(importLogDbVO);
  }

  @Transactional(rollbackFor = Throwable.class)
  public void finishImportLog(ImportLogDbVO importLogDbVO) {
    importLogDbVO.setEndDate(new Date());
    importLogDbVO.setStatus(ImportLog.Status.FINISHED);
    importLogDbVO.setPercentage(ImportLogDbVO.PERCENTAGE_COMPLETED);

    importLogDbVO = this.importLogRepository.saveAndFlush(importLogDbVO);
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void finishImportLogItem(ImportLogItemDbVO importLogItemDbVO) {
    importLogItemDbVO.setEndDate(new Date());
    importLogItemDbVO.setStatus(ImportLog.Status.FINISHED);

    importLogItemDbVO = this.importLogItemRepository.saveAndFlush(importLogItemDbVO);
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void finishSubmit(ImportLogDbVO importLogDbVO, ImportLog.SubmitModus submitModus) {
    switch (submitModus) {
      case SUBMIT:
        createImportLogItem(importLogDbVO, ImportLog.ErrorLevel.FINE, ImportLog.Message.import_process_submit_finished.name());
        break;
      case SUBMIT_AND_RELEASE:
        createImportLogItem(importLogDbVO, ImportLog.ErrorLevel.FINE, ImportLog.Message.import_process_submit_release_finished.name());
        break;
      case RELEASE:
        createImportLogItem(importLogDbVO, ImportLog.ErrorLevel.FINE, ImportLog.Message.import_process_release_finished.name());
        break;
    }

    finishImportLog(importLogDbVO);
  }

  @Override
  public List<ImportLogDbVO> getContextImportLogs(String contextId) {
    List<Object[]> results = this.importLogRepository.findAllByContextId(contextId);
    List<ImportLogDbVO> importLogDbVOs = getImportLogDbVOs(results);

    return importLogDbVOs;
  }

  @Override
  public String getExceptionMessage(Throwable exception) {
    StringWriter stringWriter = new StringWriter();
    stringWriter.write(exception.getClass().getSimpleName());

    if (null != exception.getMessage()) {
      stringWriter.write(": ");
      stringWriter.write(exception.getMessage());
    }
    stringWriter.write("\n");

    StackTraceElement[] stackTraceElements = exception.getStackTrace();
    stringWriter.write("\tat ");
    stringWriter.write(stackTraceElements[0].getClassName());
    stringWriter.write(".");
    stringWriter.write(stackTraceElements[0].getMethodName());
    stringWriter.write("(");
    stringWriter.write(stackTraceElements[0].getFileName());
    stringWriter.write(":");
    stringWriter.write(stackTraceElements[0].getLineNumber() + "");
    stringWriter.write(")\n");

    if (null != exception.getCause()) {
      stringWriter.write(this.getExceptionMessage(exception.getCause()));
    }

    return stringWriter.toString();
  }

  @Override
  public FormatProcessor getFormatProcessor(ImportLogDbVO importLogDbVO, ImportLogDbVO.Format format) {
    FormatProcessor formatProcessor = null;
    switch (format) {
      case BIBTEX_STRING:
        formatProcessor = new BibtexProcessor();
        break;
      case BMC_XML:
        formatProcessor = new BmcProcessor();
        break;
      case EDOC_XML:
        formatProcessor = new EdocProcessor();
        break;
      case ENDNOTE_STRING:
        formatProcessor = new EndnoteProcessor();
        break;
      case ESCIDOC_ITEM_V3_XML:
        formatProcessor = new EscidocProcessor();
        break;
      case MAB_STRING:
        formatProcessor = new MabProcessor();
        break;
      case MARC_XML:
        formatProcessor = new MarcXmlProcessor();
        break;
      case MARC_21_STRING:
        formatProcessor = new Marc21Processor();
        break;
      case RIS_STRING:
        formatProcessor = new RisProcessor();
        break;
      case WOS_STRING:
        formatProcessor = new WosProcessor();
        break;
      default:
        formatProcessor = null;
    }

    if (null != formatProcessor) {
      formatProcessor.setEncoding("UTF-8");
    }

    if (!this.itemTransformingService.isTransformationExisting(TransformerFactory.FORMAT.valueOf(format.name()),
        TransformerFactory.getInternalFormat())) {
      formatProcessor = null;
    }

    return formatProcessor;
  }

  @Override
  public ImportLogDbVO getImportLog(Integer importLogId, AccountUserDbVO accountUserDbVO, boolean withAnzItems) {
    ImportLogDbVO importLogDbVO = null;

    if (withAnzItems) {
      List<Object[]> results = this.importLogRepository.findByIdWithAnzItems(importLogId);
      List<ImportLogDbVO> importLogDbVOs = getImportLogDbVOs(results);
      if (null != importLogDbVOs && importLogDbVOs.size() == 1) {
        importLogDbVO = importLogDbVOs.get(0);
      }
    } else {
      importLogDbVO = this.importLogRepository.findById(importLogId).orElse(null);
    }

    return importLogDbVO;
  }

  @Override
  public ImportLogItemDbVO getImportLogItem(Integer importLogItemId) {
    ImportLogItemDbVO importLogItemDbVO = this.importLogItemRepository.findById(importLogItemId).orElse(null);

    return importLogItemDbVO;
  }

  @Override
  public List<ImportLogItemDetailDbVO> getImportLogItemDetails(ImportLogItemDbVO importLogItemDbVO) {
    List<ImportLogItemDetailDbVO> importLogItemDetailDbVOs = this.importLogItemDetailRepository.findByImportLogItem(importLogItemDbVO);

    return importLogItemDetailDbVOs;
  }

  @Override
  public List<ImportLogItemDbVO> getImportLogItems(ImportLogDbVO importLogDbVO) {
    List<Object[]> results = this.importLogItemRepository.findByParent(importLogDbVO.getId());
    List<ImportLogItemDbVO> importLogItemDbVOs = new ArrayList<ImportLogItemDbVO>();

    for (Object[] objects : results) {
      Integer importLogItemId = Integer.parseInt(objects[0].toString());
      ImportLog.Status status = ImportLog.Status.valueOf(objects[1].toString());
      ImportLog.ErrorLevel errorLevel = ImportLog.ErrorLevel.valueOf(objects[2].toString());
      Date startDate = (Date) objects[3];
      Date endDate = (Date) objects[4];
      //      Integer parentId = Integer.parseInt(objects[5].toString());
      String message = objects[6].toString();
      String itemId = (null == objects[7] ? null : objects[7].toString());
      Long anzDetails = Long.parseLong(objects[8].toString());

      ImportLogItemDbVO importLogItemDbVO = new ImportLogItemDbVO();
      importLogItemDbVO.setId(importLogItemId);
      importLogItemDbVO.setStatus(status);
      importLogItemDbVO.setErrorLevel(errorLevel);
      importLogItemDbVO.setStartDate(startDate);
      importLogItemDbVO.setEndDate(endDate);
      //      importLogItemDbVO.setParent(importLogDbVO);
      importLogItemDbVO.setMessage(message);
      importLogItemDbVO.setItemId(itemId);
      importLogItemDbVO.setAnzDetails(anzDetails);

      importLogItemDbVOs.add(importLogItemDbVO);
    }

    return importLogItemDbVOs;
  }

  @Override
  public List<ImportLogItemDbVO> getImportedLogItems(ImportLogDbVO importLogDbVO) {
    List<ImportLogItemDbVO> importedLogItemDbVOs = this.importLogItemRepository.findByParentAndItemId(importLogDbVO);

    return importedLogItemDbVOs;
  }

  @Override
  public List<ImportLogDbVO> getUserImportLogs(String userId) {
    List<Object[]> results = this.importLogRepository.findAllByUserId(userId);
    List<ImportLogDbVO> importLogDbVOs = getImportLogDbVOs(results);

    return importLogDbVOs;
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void initializeDelete(ImportLogDbVO importLogDbVO) {
    reopenImportLog(importLogDbVO);
    setPercentageInImportLog(importLogDbVO, ImportLogDbVO.PERCENTAGE_ZERO);

    ImportLogItemDbVO importLogItemDbVO =
        createImportLogItem(importLogDbVO, ImportLog.ErrorLevel.FINE, ImportLog.Message.import_process_delete_items.name());
    createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE,
        ImportLog.Message.import_process_initialize_delete_process.name());

    setPercentageInImportLog(importLogDbVO, ImportLogDbVO.PERCENTAGE_DELETE_START);
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public ImportLogDbVO initializeImport(AccountUserDbVO accountUserDbVO, ImportLogDbVO.Format format, String importName, String contextId) {
    ImportLogDbVO importLogDbVO = createImportLog(accountUserDbVO.getObjectId(), format, importName, contextId);
    createImportLogItem(importLogDbVO, ImportLog.ErrorLevel.FINE, ImportLog.Message.import_process_started.name());

    return importLogDbVO;
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void initializeSubmit(ImportLogDbVO importLogDbVO, ImportLog.SubmitModus submitModus) {
    reopenImportLog(importLogDbVO);
    setPercentageInImportLog(importLogDbVO, ImportLogDbVO.PERCENTAGE_ZERO);

    ImportLogItemDbVO importLogItemDbVO = null;
    switch (submitModus) {
      case SUBMIT:
        importLogItemDbVO =
            createImportLogItem(importLogDbVO, ImportLog.ErrorLevel.FINE, ImportLog.Message.import_process_submit_items.name());
        createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE,
            ImportLog.Message.import_process_initialize_submit_process.name());
        break;
      case SUBMIT_AND_RELEASE:
        importLogItemDbVO =
            createImportLogItem(importLogDbVO, ImportLog.ErrorLevel.FINE, ImportLog.Message.import_process_submit_release_items.name());
        createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE,
            ImportLog.Message.import_process_initialize_submit_release_process.name());
        break;
      case RELEASE:
        importLogItemDbVO =
            createImportLogItem(importLogDbVO, ImportLog.ErrorLevel.FINE, ImportLog.Message.import_process_release_items.name());
        createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE,
            ImportLog.Message.import_process_initialize_release_process.name());
        break;
    }

    setPercentageInImportLog(importLogDbVO, ImportLogDbVO.PERCENTAGE_SUBMIT_START);
  }

  @Transactional(rollbackFor = Throwable.class)
  @Override
  public ItemVersionVO prepareItem(ImportLogItemDbVO importLogItemDbVO, ImportLogDbVO.Format format,
      Map<String, String> formatConfiguration, ContextDbVO contextDbVO, String singleItem) {
    createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE, ImportLog.Message.import_process_source_data_found.name());
    createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE, singleItem);
    createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE, ImportLog.Message.import_process_start_transformation.name());

    ItemVersionVO itemVersionVO = null;
    String escidocXml = null;
    try {
      escidocXml = this.itemTransformingService.transformFromTo(TransformerFactory.FORMAT.valueOf(format.name()),
          TransformerFactory.getInternalFormat(), singleItem, formatConfiguration);
      createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE, escidocXml);

      itemVersionVO = EntityTransformer.transformToNew(XmlTransformingService.transformToPubItem(escidocXml));
      itemVersionVO.getObject().setContext(contextDbVO);
      itemVersionVO.setObjectId(null);
      createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE, ImportLog.Message.import_process_transformation_done.name());

      // Simple Validation
      createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE, ImportLog.Message.import_process_default_validation.name());
      try {
        PubItemUtil.cleanUpItem(itemVersionVO);
        this.itemValidatingService.validate(itemVersionVO, ValidationPoint.SIMPLE);
        createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE,
            ImportLog.Message.import_process_default_validation_successful.name());

        // Standard Validation
        createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE, ImportLog.Message.import_process_release_validation.name());
        try {
          this.itemValidatingService.validate(itemVersionVO, ValidationPoint.STANDARD);
          createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE,
              ImportLog.Message.import_process_release_validation_successful.name());
          createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE, ImportLog.Message.import_process_generate_item.name());
        } catch (ValidationException e) { // Standard Validation
          createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.WARNING,
              ImportLog.Message.import_process_release_validation_failed.name());
          for (ValidationReportItemVO item : e.getReport().getItems()) {
            createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.WARNING, item.getContent());
          }
        }

      } catch (ValidationException e) { // Simple Validation
        createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.WARNING,
            ImportLog.Message.import_process_default_validation_failed.name());
        for (ValidationReportItemVO item : e.getReport().getItems()) {
          createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.PROBLEM, item.getContent());
        }
        createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.WARNING,
            ImportLog.Message.import_process_item_not_imported.name());
        itemVersionVO = null;
      }

    } catch (Exception e) {
      logger.error("Error while multiple import", e);
      createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.ERROR, getExceptionMessage(e));
      createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.ERROR, ImportLog.Message.import_process_item_not_imported.name());
      itemVersionVO = null;
    }

    return itemVersionVO;
  }

  @Transactional(rollbackFor = Throwable.class)
  public void reopenImportLog(ImportLogDbVO importLogDbVO) {
    importLogDbVO.setEndDate(null);
    importLogDbVO.setStatus(ImportLog.Status.PENDING);

    importLogDbVO = this.importLogRepository.saveAndFlush(importLogDbVO);
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void repareBrokenImports(Date criticalDate) {
    List<ImportLogDbVO> importLogDbVOs = this.importLogRepository.findBrokenImports(criticalDate);

    for (ImportLogDbVO importLogDbVO : importLogDbVOs) {
      logger.warn("Unfinished import detected (" + importLogDbVO.getId() + "). Finishing it with status FATAL.");
      ImportLogItemDbVO importLogItemDbVO =
          createImportLogItem(importLogDbVO, ImportLog.ErrorLevel.FATAL, ImportLog.Message.import_process_aborted_unexpectedly.name());
      createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FATAL, ImportLog.Message.import_process_failed.name());

      this.finishImportLog(importLogDbVO);
    }
  }

  @Transactional(rollbackFor = Throwable.class)
  public void resetItemId(ImportLogItemDbVO importLogItemDbVO) {
    importLogItemDbVO.setItemId(null);

    importLogItemDbVO = this.importLogItemRepository.saveAndFlush(importLogItemDbVO);
  }

  @Transactional(rollbackFor = Throwable.class)
  public void setItemIdInImportLogItem(ImportLogItemDbVO importLogItemDbVO, String itemId) {
    importLogItemDbVO.setItemId(itemId);

    importLogItemDbVO = this.importLogItemRepository.saveAndFlush(importLogItemDbVO);
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void setPercentageInImportLog(ImportLogDbVO importLogDbVO, Integer percentage) {
    importLogDbVO.setPercentage(percentage);

    importLogDbVO = this.importLogRepository.saveAndFlush(importLogDbVO);
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void setSuspensionForDelete(ImportLogDbVO importLogDbVO, ImportLogItemDbVO importLogItemDbVO) {
    createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE, ImportLog.Message.import_process_schedule_delete.name());
    suspendImportLogItem(importLogItemDbVO);
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void setSuspensionForSubmit(ImportLogDbVO importLogDbVO, ImportLogItemDbVO importLogItemDbVO, ImportLog.SubmitModus submitModus) {
    switch (submitModus) {
      case SUBMIT:
        createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE, ImportLog.Message.import_process_schedule_submit.name());
        break;
      case SUBMIT_AND_RELEASE:
        createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE,
            ImportLog.Message.import_process_schedule_submit_release.name());
        break;
      case RELEASE:
        createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE, ImportLog.Message.import_process_schedule_release.name());
        break;
    }

    suspendImportLogItem(importLogItemDbVO);
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void suspendImportLogItem(ImportLogItemDbVO importLogItemDbVO) {
    importLogItemDbVO.setEndDate(new Date());
    importLogItemDbVO.setStatus(ImportLog.Status.SUSPENDED);

    importLogItemDbVO = this.importLogItemRepository.saveAndFlush(importLogItemDbVO);
  }

  private List<ImportLogDbVO> getImportLogDbVOs(List<Object[]> results) {
    List<ImportLogDbVO> importLogDbVOs = new ArrayList<ImportLogDbVO>();

    for (Object[] objects : results) {
      Integer importLogId = Integer.parseInt(objects[0].toString());
      ImportLog.Status status = ImportLog.Status.valueOf(objects[1].toString());
      ImportLog.ErrorLevel errorLevel = ImportLog.ErrorLevel.valueOf(objects[2].toString());
      Date startDate = (Date) objects[3];
      Date endDate = (Date) objects[4];
      String userId = objects[5].toString();
      String name = objects[6].toString();
      ImportLogDbVO.Format format = ImportLogDbVO.Format.valueOf(objects[7].toString());
      String contextId = objects[8].toString();
      Integer percentage = Integer.parseInt(objects[9].toString());
      Long anzItems = Long.parseLong(objects[10].toString());

      ImportLogDbVO importLogDbVO = new ImportLogDbVO();
      importLogDbVO.setId(importLogId);
      importLogDbVO.setStatus(status);
      importLogDbVO.setErrorLevel(errorLevel);
      importLogDbVO.setStartDate(startDate);
      importLogDbVO.setEndDate(endDate);
      importLogDbVO.setUserId(userId);
      importLogDbVO.setName(name);
      importLogDbVO.setFormat(format);
      importLogDbVO.setContextId(contextId);
      importLogDbVO.setPercentage(percentage);
      importLogDbVO.setAnzItems(anzItems);

      importLogDbVOs.add(importLogDbVO);
    }
    return importLogDbVOs;
  }
}
