package de.mpg.mpdl.inge.service.pubman.impl;

import de.mpg.mpdl.inge.db.repository.ImportLogItemDetailRepository;
import de.mpg.mpdl.inge.db.repository.ImportLogItemRepository;
import de.mpg.mpdl.inge.db.repository.ImportLogRepository;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLog;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogItemDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogItemDetailDbVO;
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
  @Transactional(rollbackFor = Throwable.class)
  public void initializeDelete(ImportLogDbVO importLogDbVO) {
    reopenImportLog(importLogDbVO);
    updateImportLog(importLogDbVO, ImportLogDbVO.PERCENTAGE_ZERO);
    ImportLogItemDbVO importLogItemDbVO = createImportLogItem(importLogDbVO, ImportLog.Messsage.import_process_delete_items.name());
    createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE,
        ImportLog.Messsage.import_process_initialize_delete_process.name());
    finishImportLogItem(importLogItemDbVO);
    updateImportLog(importLogDbVO, ImportLogDbVO.PERCENTAGE_DELETE_START);
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void setSuspensionForDelete(ImportLogItemDbVO importLogItemDbVO) {
    createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE, ImportLog.Messsage.import_process_schedule_delete.name());
    suspendImportLogItem(importLogItemDbVO);
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
    finishImportLogItem(importLogItemDbVO);
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void finishDelete(ImportLogDbVO importLogDbVO) {
    ImportLogItemDbVO importLogItemDbVO = createImportLogItem(importLogDbVO, ImportLog.Messsage.import_process_delete_finished.name());
    finishImportLogItem(importLogItemDbVO);
    finishImportLog(importLogDbVO);
    updateImportLog(importLogDbVO, ImportLogDbVO.PERCENTAGE_COMPLETED);
  }

  @Override
  public ImportLogDbVO createImportLog(String userId, ImportLogDbVO.Format format) {

    ImportLogDbVO importLogDbVO = new ImportLogDbVO(userId, format);

    importLogDbVO = this.importLogRepository.saveAndFlush(importLogDbVO);

    return importLogDbVO;
  }

  @Override
  public ImportLogDbVO updateImportLog(ImportLogDbVO importLogDbVO, Integer percentage) {
    importLogDbVO.setPercentage(percentage);

    importLogDbVO = this.importLogRepository.saveAndFlush(importLogDbVO);

    return importLogDbVO;
  }

  private ImportLogItemDbVO createImportLogItem(ImportLogDbVO importLogDbVO, String message) {

    ImportLogItemDbVO importLogItemDbVO = new ImportLogItemDbVO(importLogDbVO, message);

    importLogItemDbVO = this.importLogItemRepository.saveAndFlush(importLogItemDbVO);

    return importLogItemDbVO;
  }

  @Override
  public ImportLogItemDetailDbVO createImportLogItemDetail(ImportLogItemDbVO importLogItemDbVO, ImportLog.ErrorLevel errorLevel,
      String message) {

    ImportLogItemDetailDbVO importLogItemDetailDbVO = new ImportLogItemDetailDbVO(importLogItemDbVO, errorLevel, message);

    importLogItemDetailDbVO = this.importLogItemDetailRepository.saveAndFlush(importLogItemDetailDbVO);

    return importLogItemDetailDbVO;
  }

  private ImportLogDbVO finishImportLog(ImportLogDbVO importLogDbVO) {
    importLogDbVO.setEndDate(new Date());
    importLogDbVO.setStatus(ImportLog.Status.FINISHED);

    importLogDbVO = this.importLogRepository.saveAndFlush(importLogDbVO);

    return importLogDbVO;
  }

  private ImportLogDbVO reopenImportLog(ImportLogDbVO importLogDbVO) {
    importLogDbVO.setEndDate(null);
    importLogDbVO.setStatus(ImportLog.Status.PENDING);

    importLogDbVO = this.importLogRepository.saveAndFlush(importLogDbVO);

    return importLogDbVO;
  }

  private ImportLogItemDbVO finishImportLogItem(ImportLogItemDbVO importLogItemDbVO) {
    importLogItemDbVO.setEndDate(new Date());
    importLogItemDbVO.setStatus(ImportLog.Status.FINISHED);

    importLogItemDbVO = this.importLogItemRepository.saveAndFlush(importLogItemDbVO);

    return importLogItemDbVO;
  }

  private ImportLogItemDbVO suspendImportLogItem(ImportLogItemDbVO importLogItemDbVO) {
    importLogItemDbVO.setEndDate(new Date());
    importLogItemDbVO.setStatus(ImportLog.Status.SUSPENDED);

    importLogItemDbVO = this.importLogItemRepository.saveAndFlush(importLogItemDbVO);

    return importLogItemDbVO;
  }

  private ImportLogItemDbVO resetItemId(ImportLogItemDbVO importLogItemDbVO) {
    importLogItemDbVO.setItemId(null);

    importLogItemDbVO = this.importLogItemRepository.saveAndFlush(importLogItemDbVO);

    return importLogItemDbVO;
  }
}
