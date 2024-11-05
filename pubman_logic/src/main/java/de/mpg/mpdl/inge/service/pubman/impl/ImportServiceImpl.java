package de.mpg.mpdl.inge.service.pubman.impl;

import de.mpg.mpdl.inge.db.repository.ImportLogItemDetailRepository;
import de.mpg.mpdl.inge.db.repository.ImportLogItemRepository;
import de.mpg.mpdl.inge.db.repository.ImportLogRepository;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLog;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogItemDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogItemDetailDbVO;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.ImportService;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.pubman.importprocess.ImportCommonService;
import java.util.Date;
import java.util.List;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Primary
public class ImportServiceImpl implements ImportService {

  private final AuthorizationService authorizationService;
  private final ImportCommonService importCommonService;
  private final ImportLogRepository importLogRepository;
  private final ImportLogItemRepository importLogItemRepository;
  private final ImportLogItemDetailRepository importLogItemDetailRepository;
  private final PubItemService pubItemService;

  public ImportServiceImpl(AuthorizationService authorizationService, ImportCommonService importCommonService,
      ImportLogRepository importLogRepository, ImportLogItemRepository importLogItemRepository,
      ImportLogItemDetailRepository importLogItemDetailRepository, PubItemService pubItemService) {
    this.authorizationService = authorizationService;
    this.importCommonService = importCommonService;
    this.importLogRepository = importLogRepository;
    this.importLogItemRepository = importLogItemRepository;
    this.importLogItemDetailRepository = importLogItemDetailRepository;
    this.pubItemService = pubItemService;
  }

  @Override
  public List<ImportLogDbVO> getImportLogs(String token) throws AuthenticationException, IngeApplicationException {

    AccountUserDbVO accountUserDbVO = getUser(token);

    List<ImportLogDbVO> importLogDbVOs = this.importLogRepository.findAllByUserId(accountUserDbVO.getObjectId());

    return importLogDbVOs;
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
  public List<ImportLogItemDetailDbVO> getImportLogItemDetails(Integer importLogItemId, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException {

    AccountUserDbVO accountUserDbVO = getUser(token);

    ImportLogItemDbVO importLogItemDbVO = getImportLogItem(importLogItemId, accountUserDbVO);
    if (null == importLogItemDbVO) {
      throw new IngeApplicationException("Invalid importLogItemId");
    }
    if (null == importLogItemDbVO.getParent()) {
      throw new IngeApplicationException("Invalid importLogId");
    }

    checkUserAccess(importLogItemDbVO.getParent(), accountUserDbVO);

    List<ImportLogItemDetailDbVO> importLogItemDetailDbVOs = this.importLogItemDetailRepository.findByImportLogItem(importLogItemDbVO);

    return importLogItemDetailDbVOs;
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

  // Transaktion
  public void deleteItems(Integer importLogId, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException {
    AccountUserDbVO accountUserDbVO = getUser(token);

    ImportLogDbVO importLogDbVO = getImportLog(importLogId, accountUserDbVO);
    if (null == importLogDbVO) {
      throw new IngeApplicationException("Invalid importLogId");
    }

    checkUserAccess(importLogDbVO, accountUserDbVO);

    this.importCommonService.updateImportLog(importLogDbVO, null, ImportLog.Status.PENDING);
    this.importCommonService.updateImportLog(importLogDbVO, ImportLogDbVO.PERCENTAGE_ZERO);
    ImportLogItemDbVO importLogItemDbVO1 =
        this.importCommonService.createImportLogItem(importLogDbVO, ImportLog.Messsage.import_process_delete_items.name());
    this.importCommonService.createImportLogItemDetail(importLogItemDbVO1, ImportLog.ErrorLevel.FINE,
        ImportLog.Messsage.import_process_initialize_delete_process.name());
    this.importCommonService.updateImportLogItem(importLogItemDbVO1, ImportLog.Status.FINISHED);
    this.importCommonService.updateImportLog(importLogDbVO, new Date(), ImportLog.Status.FINISHED);
    this.importCommonService.updateImportLog(importLogDbVO, ImportLogDbVO.PERCENTAGE_DELETE_START);

    // Ab hier ASYNCHRON
    List<ImportLogItemDbVO> importLogItemDbVOs = this.importLogItemRepository.findByParentAndItemId(importLogDbVO);
    for (ImportLogItemDbVO importLogItemDbVO : importLogItemDbVOs) {
      this.importCommonService.createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE,
          ImportLog.Messsage.import_process_schedule_delete.name());
      this.importCommonService.updateImportLogItem(importLogItemDbVO, ImportLog.Status.SUSPENDED);
    }

    this.importCommonService.updateImportLog(importLogDbVO, ImportLogDbVO.PERCENTAGE_DELETE_SUSPEND);

    int counter = 0;
    for (ImportLogItemDbVO importLogItemDbVO : importLogItemDbVOs) {
      this.importCommonService.createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE,
          ImportLog.Messsage.import_process_delete_item.name());
      try {
        this.pubItemService.delete(importLogItemDbVO.getItemId(), token);
        this.importCommonService.createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE,
            ImportLog.Messsage.import_process_delete_successful.name());
        this.importCommonService.createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE,
            ImportLog.Messsage.import_process_remove_identifier.name());
        this.importCommonService.resetItemId(importLogItemDbVO);
        this.importCommonService.updateImportLog(importLogDbVO, new Date(), ImportLog.Status.FINISHED);
      } catch (Exception e) {
        this.importCommonService.createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.WARNING,
            ImportLog.Messsage.import_process_delete_failed.name());
        this.importCommonService.createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.WARNING, e.toString());
        this.importCommonService.updateImportLog(importLogDbVO, new Date(), ImportLog.Status.FINISHED);
      }
      counter++;
      this.importCommonService.updateImportLog(importLogDbVO,
          ImportLogDbVO.PERCENTAGE_DELETE_END * counter / importLogItemDbVOs.size() + ImportLogDbVO.PERCENTAGE_DELETE_SUSPEND);
    }

    ImportLogItemDbVO importLogItemDbVO2 =
        this.importCommonService.createImportLogItem(importLogDbVO, ImportLog.Messsage.import_process_delete_finished.name());
    this.importCommonService.updateImportLogItem(importLogItemDbVO2, ImportLog.Status.FINISHED);
    this.importCommonService.updateImportLog(importLogDbVO, new Date(), ImportLog.Status.FINISHED);
    this.importCommonService.updateImportLog(importLogDbVO, ImportLogDbVO.PERCENTAGE_COMPLETED);
  }

  ////////////////////////////////////////////////////
  ////////////////////////////////////////////////////

  private AccountUserDbVO getUser(String token) throws AuthenticationException, IngeApplicationException {
    AccountUserDbVO accountUserDbVO = this.authorizationService.getUserAccountFromToken(token);

    return accountUserDbVO;
  }

  private void checkUserAccess(ImportLogDbVO importLogDbVO, AccountUserDbVO accountUserDbVO) throws AuthorizationException {
    if (null != importLogDbVO && !accountUserDbVO.getObjectId().equals(importLogDbVO.getUserId())) {
      throw new AuthorizationException("given user is not allowed to access the import.");
    }
  }

  private ImportLogDbVO getImportLog(Integer importLogId, AccountUserDbVO accountUserDbVO) {

    ImportLogDbVO importLogDbVO = this.importLogRepository.findById(importLogId).orElse(null);

    return importLogDbVO;
  }

  private ImportLogItemDbVO getImportLogItem(Integer importLogItemId, AccountUserDbVO accountUserDbVO) {

    ImportLogItemDbVO importLogItemDbVO = this.importLogItemRepository.findById(importLogItemId).orElse(null);

    return importLogItemDbVO;
  }
}
