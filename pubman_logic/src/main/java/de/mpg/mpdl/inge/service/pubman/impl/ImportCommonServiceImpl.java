package de.mpg.mpdl.inge.service.pubman.impl;

import de.mpg.mpdl.inge.db.repository.ImportLogItemDetailRepository;
import de.mpg.mpdl.inge.db.repository.ImportLogItemRepository;
import de.mpg.mpdl.inge.db.repository.ImportLogRepository;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLog;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogItemDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogItemDetailDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.pubman.importprocess.ImportCommonService;
import java.util.Date;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Primary
public class ImportCommonServiceImpl implements ImportCommonService {

  private final ImportLogRepository importLogRepository;
  private final ImportLogItemRepository importLogItemRepository;
  private final ImportLogItemDetailRepository importLogItemDetailRepository;
  private final PubItemService pubItemService;

  public ImportCommonServiceImpl(ImportLogRepository importLogRepository, ImportLogItemRepository importLogItemRepository,
      ImportLogItemDetailRepository importLogItemDetailRepository, PubItemService pubItemService) {
    this.importLogRepository = importLogRepository;
    this.importLogItemRepository = importLogItemRepository;
    this.importLogItemDetailRepository = importLogItemDetailRepository;
    this.pubItemService = pubItemService;
  }

  @Override
  public ImportLogDbVO createImportLog(String userId, ImportLogDbVO.Format format) {
    ImportLogDbVO importLogDbVO = new ImportLogDbVO(userId, format);

    importLogDbVO = this.importLogRepository.saveAndFlush(importLogDbVO);

    return importLogDbVO;
  }

  @Override
  public ImportLogItemDetailDbVO createImportLogItemDetail(ImportLogItemDbVO importLogItemDbVO, ImportLog.ErrorLevel errorLevel,
      String message) {
    ImportLogItemDetailDbVO importLogItemDetailDbVO = new ImportLogItemDetailDbVO(importLogItemDbVO, errorLevel, message);

    importLogItemDetailDbVO = this.importLogItemDetailRepository.saveAndFlush(importLogItemDetailDbVO);

    return importLogItemDetailDbVO;
  }

  @Transactional(rollbackFor = Throwable.class)
  @Override
  public void doDelete(ImportLogDbVO importLogDbVO, ImportLogItemDbVO importLogItemDbVO, String token)
      throws AuthenticationException, AuthorizationException, IngeApplicationException, IngeTechnicalException {
    this.pubItemService.delete(importLogItemDbVO.getItemId(), token);
    createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE, ImportLog.Messsage.import_process_delete_successful.name());
    createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE, ImportLog.Messsage.import_process_remove_identifier.name());
    resetItemId(importLogItemDbVO);
    finishImportLogItem(importLogItemDbVO);
  }

  @Transactional(rollbackFor = Throwable.class)
  @Override
  public void doFailDelete(ImportLogDbVO importLogDbVO, ImportLogItemDbVO importLogItemDbVO, String message) {
    createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.WARNING, ImportLog.Messsage.import_process_delete_failed.name());
    createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.WARNING, message);
    importLogDbVO = importLogItemDbVO.getParent(); // in the meanwhile the parent has been changed with another errorlevel
    finishImportLogItem(importLogItemDbVO);
  }

  @Transactional(rollbackFor = Throwable.class)
  @Override
  public void doFailSubmit(ImportLogDbVO importLogDbVO, ImportLogItemDbVO importLogItemDbVO, ImportLog.SubmitModus submitModus,
      String message) {
    switch (submitModus) {
      case SUBMIT:
        createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.WARNING, ImportLog.Messsage.import_process_submit_failed.name());
        createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.WARNING, message);
        break;
      case SUBMIT_AND_RELEASE:
        createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.WARNING,
            ImportLog.Messsage.import_process_submit_release_failed.name());
        createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.WARNING, message);
        break;
      case RELEASE:
        createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.WARNING, ImportLog.Messsage.import_process_release_failed.name());
        createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.WARNING, message);
        break;
    }
    importLogDbVO = importLogItemDbVO.getParent(); // in the meanwhile the parent has been changed with another errorlevel

    finishImportLogItem(importLogItemDbVO);
  }

  @Transactional(rollbackFor = Throwable.class)
  @Override
  public void doSubmit(ImportLogDbVO importLogDbVO, ImportLogItemDbVO importLogItemDbVO, ImportLog.SubmitModus submitModus, String token)
      throws AuthenticationException, AuthorizationException, IngeApplicationException, IngeTechnicalException {
    ItemVersionVO itemVersionVO = pubItemService.get(importLogItemDbVO.getItemId(), token);

    switch (submitModus) {
      case SUBMIT:
        this.pubItemService.submitPubItem(importLogItemDbVO.getItemId(), itemVersionVO.getModificationDate(),
            "Batch submit from import " + importLogDbVO.getName(), token);
        createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE, ImportLog.Messsage.import_process_submit_successful.name());
        break;
      case SUBMIT_AND_RELEASE:
        this.pubItemService.submitPubItem(importLogItemDbVO.getItemId(), itemVersionVO.getModificationDate(),
            "Batch submit (and release) from import " + importLogDbVO.getName(), token);
        ItemVersionVO itemVersionVO_ = pubItemService.get(importLogItemDbVO.getItemId(), token);
        this.pubItemService.releasePubItem(importLogItemDbVO.getItemId(), itemVersionVO.getModificationDate(),
            "Batch (submit and) release from import " + importLogDbVO.getName(), token);
        createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE,
            ImportLog.Messsage.import_process_submit_release_successful.name());
        break;
      case RELEASE:
        this.pubItemService.releasePubItem(importLogItemDbVO.getItemId(), itemVersionVO.getModificationDate(),
            "Batch release from import " + importLogDbVO.getName(), token);
        createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE,
            ImportLog.Messsage.import_process_release_successful.name());
        break;
    }

    finishImportLogItem(importLogItemDbVO);
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void finishDelete(ImportLogDbVO importLogDbVO) {
    ImportLogItemDbVO importLogItemDbVO =
        createImportLogItem(importLogDbVO, ImportLog.ErrorLevel.FINE, ImportLog.Messsage.import_process_delete_finished.name());

    finishImportLogItem(importLogItemDbVO);
    finishImportLog(importLogDbVO);
    updateImportLog(importLogDbVO, ImportLogDbVO.PERCENTAGE_COMPLETED);
  }

  @Transactional(rollbackFor = Throwable.class)
  @Override
  public void finishSubmit(ImportLogDbVO importLogDbVO, ImportLog.SubmitModus submitModus) {
    ImportLogItemDbVO importLogItemDbVO = null;

    switch (submitModus) {
      case SUBMIT:
        importLogItemDbVO =
            createImportLogItem(importLogDbVO, ImportLog.ErrorLevel.FINE, ImportLog.Messsage.import_process_submit_finished.name());
        break;
      case SUBMIT_AND_RELEASE:
        importLogItemDbVO =
            createImportLogItem(importLogDbVO, ImportLog.ErrorLevel.FINE, ImportLog.Messsage.import_process_submit_release_finished.name());
        break;
      case RELEASE:
        importLogItemDbVO =
            createImportLogItem(importLogDbVO, ImportLog.ErrorLevel.FINE, ImportLog.Messsage.import_process_release_finished.name());
        break;
    }

    finishImportLogItem(importLogItemDbVO);
    finishImportLog(importLogDbVO);
    updateImportLog(importLogDbVO, ImportLogDbVO.PERCENTAGE_COMPLETED);
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void initializeDelete(ImportLogDbVO importLogDbVO) {
    reopenImportLog(importLogDbVO);
    updateImportLog(importLogDbVO, ImportLogDbVO.PERCENTAGE_ZERO);

    ImportLogItemDbVO importLogItemDbVO =
        createImportLogItem(importLogDbVO, ImportLog.ErrorLevel.FINE, ImportLog.Messsage.import_process_delete_items.name());
    createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE,
        ImportLog.Messsage.import_process_initialize_delete_process.name());

    finishImportLogItem(importLogItemDbVO);
    updateImportLog(importLogDbVO, ImportLogDbVO.PERCENTAGE_DELETE_START);
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void initializeSubmit(ImportLogDbVO importLogDbVO, ImportLog.SubmitModus submitModus) {
    reopenImportLog(importLogDbVO);
    updateImportLog(importLogDbVO, ImportLogDbVO.PERCENTAGE_ZERO);

    ImportLogItemDbVO importLogItemDbVO = null;
    switch (submitModus) {
      case SUBMIT:
        importLogItemDbVO =
            createImportLogItem(importLogDbVO, ImportLog.ErrorLevel.FINE, ImportLog.Messsage.import_process_submit_items.name());
        createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE,
            ImportLog.Messsage.import_process_initialize_submit_process.name());
        break;
      case SUBMIT_AND_RELEASE:
        importLogItemDbVO =
            createImportLogItem(importLogDbVO, ImportLog.ErrorLevel.FINE, ImportLog.Messsage.import_process_submit_release_items.name());
        createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE,
            ImportLog.Messsage.import_process_initialize_submit_release_process.name());
        break;
      case RELEASE:
        importLogItemDbVO =
            createImportLogItem(importLogDbVO, ImportLog.ErrorLevel.FINE, ImportLog.Messsage.import_process_release_items.name());
        createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE,
            ImportLog.Messsage.import_process_initialize_release_process.name());
        break;
    }

    finishImportLogItem(importLogItemDbVO);
    updateImportLog(importLogDbVO, ImportLogDbVO.PERCENTAGE_SUBMIT_START);
  }

  @Transactional(rollbackFor = Throwable.class)
  @Override
  public void setSuspensionForDelete(ImportLogDbVO importLogDbVO, ImportLogItemDbVO importLogItemDbVO) {
    createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE, ImportLog.Messsage.import_process_schedule_delete.name());
    suspendImportLogItem(importLogItemDbVO);
  }

  @Transactional(rollbackFor = Throwable.class)
  @Override
  public void setSuspensionForSubmit(ImportLogDbVO importLogDbVO, ImportLogItemDbVO importLogItemDbVO, ImportLog.SubmitModus submitModus) {
    switch (submitModus) {
      case SUBMIT:
        createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE, ImportLog.Messsage.import_process_schedule_submit.name());
        break;
      case SUBMIT_AND_RELEASE:
        createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE,
            ImportLog.Messsage.import_process_schedule_submit_release.name());
        break;
      case RELEASE:
        createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE, ImportLog.Messsage.import_process_schedule_release.name());
        break;
    }

    suspendImportLogItem(importLogItemDbVO);
  }

  @Override
  public void updateImportLog(ImportLogDbVO importLogDbVO, Integer percentage) {
    importLogDbVO.setPercentage(percentage);

    importLogDbVO = this.importLogRepository.saveAndFlush(importLogDbVO);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private ImportLogItemDbVO createImportLogItem(ImportLogDbVO importLogDbVO, ImportLog.ErrorLevel errorLevel, String message) {
    ImportLogItemDbVO importLogItemDbVO = new ImportLogItemDbVO(importLogDbVO, errorLevel, message);

    importLogItemDbVO = this.importLogItemRepository.saveAndFlush(importLogItemDbVO);

    return importLogItemDbVO;
  }

  private void finishImportLog(ImportLogDbVO importLogDbVO) {
    importLogDbVO.setEndDate(new Date());
    importLogDbVO.setStatus(ImportLog.Status.FINISHED);

    importLogDbVO = this.importLogRepository.saveAndFlush(importLogDbVO);
  }

  private void finishImportLogItem(ImportLogItemDbVO importLogItemDbVO) {
    importLogItemDbVO.setEndDate(new Date());
    importLogItemDbVO.setStatus(ImportLog.Status.FINISHED);

    importLogItemDbVO = this.importLogItemRepository.saveAndFlush(importLogItemDbVO);
  }

  private void reopenImportLog(ImportLogDbVO importLogDbVO) {
    importLogDbVO.setEndDate(null);
    importLogDbVO.setStatus(ImportLog.Status.PENDING);

    importLogDbVO = this.importLogRepository.saveAndFlush(importLogDbVO);
  }

  private void resetItemId(ImportLogItemDbVO importLogItemDbVO) {
    importLogItemDbVO.setItemId(null);

    importLogItemDbVO = this.importLogItemRepository.saveAndFlush(importLogItemDbVO);
  }

  private void suspendImportLogItem(ImportLogItemDbVO importLogItemDbVO) {
    importLogItemDbVO.setEndDate(new Date());
    importLogItemDbVO.setStatus(ImportLog.Status.SUSPENDED);

    importLogItemDbVO = this.importLogItemRepository.saveAndFlush(importLogItemDbVO);
  }
}
