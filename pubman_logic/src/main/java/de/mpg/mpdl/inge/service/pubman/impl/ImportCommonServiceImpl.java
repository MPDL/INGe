package de.mpg.mpdl.inge.service.pubman.impl;

import de.mpg.mpdl.inge.db.repository.ImportLogItemDetailRepository;
import de.mpg.mpdl.inge.db.repository.ImportLogItemRepository;
import de.mpg.mpdl.inge.db.repository.ImportLogRepository;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLog;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogItemDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogItemDetailDbVO;
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

  public ImportCommonServiceImpl(ImportLogRepository importLogRepository, ImportLogItemRepository importLogItemRepository,
      ImportLogItemDetailRepository importLogItemDetailRepository) {
    this.importLogRepository = importLogRepository;
    this.importLogItemRepository = importLogItemRepository;
    this.importLogItemDetailRepository = importLogItemDetailRepository;
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public ImportLogDbVO createImportLog(String userId, ImportLogDbVO.Format format) {

    ImportLogDbVO importLogDbVO = new ImportLogDbVO(userId, format);

    importLogDbVO = this.importLogRepository.saveAndFlush(importLogDbVO);

    return importLogDbVO;
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public ImportLogItemDbVO createImportLogItem(ImportLogDbVO importLogDbVO, String message) {

    ImportLogItemDbVO importLogItemDbVO = new ImportLogItemDbVO(importLogDbVO, message);

    importLogItemDbVO = this.importLogItemRepository.saveAndFlush(importLogItemDbVO);

    return importLogItemDbVO;
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public ImportLogItemDetailDbVO createImportLogItemDetail(ImportLogItemDbVO importLogItemDbVO, ImportLog.ErrorLevel errorLevel,
      String message) {

    ImportLogItemDetailDbVO importLogItemDetailDbVO = new ImportLogItemDetailDbVO(importLogItemDbVO, errorLevel, message);

    importLogItemDetailDbVO = this.importLogItemDetailRepository.saveAndFlush(importLogItemDetailDbVO);

    return importLogItemDetailDbVO;
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public ImportLogDbVO finishImportLog(ImportLogDbVO importLogDbVO) {
    importLogDbVO.setEndDate(new Date());
    importLogDbVO.setStatus(ImportLog.Status.FINISHED);

    importLogDbVO = this.importLogRepository.saveAndFlush(importLogDbVO);

    return importLogDbVO;
  }

  @Transactional(rollbackFor = Throwable.class)
  @Override
  public ImportLogDbVO reopenImportLog(ImportLogDbVO importLogDbVO) {
    importLogDbVO.setEndDate(null);
    importLogDbVO.setStatus(ImportLog.Status.PENDING);

    importLogDbVO = this.importLogRepository.saveAndFlush(importLogDbVO);

    return importLogDbVO;
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public ImportLogDbVO updateImportLog(ImportLogDbVO importLogDbVO, Integer percentage) {
    importLogDbVO.setPercentage(percentage);

    importLogDbVO = this.importLogRepository.saveAndFlush(importLogDbVO);

    return importLogDbVO;
  }

  @Transactional(rollbackFor = Throwable.class)
  @Override
  public ImportLogItemDbVO finishImportLogItem(ImportLogItemDbVO importLogItemDbVO) {
    importLogItemDbVO.setEndDate(new Date());
    importLogItemDbVO.setStatus(ImportLog.Status.FINISHED);

    importLogItemDbVO = this.importLogItemRepository.saveAndFlush(importLogItemDbVO);

    return importLogItemDbVO;
  }

  @Transactional(rollbackFor = Throwable.class)
  @Override
  public ImportLogItemDbVO suspendImportLogItem(ImportLogItemDbVO importLogItemDbVO) {
    importLogItemDbVO.setEndDate(new Date());
    importLogItemDbVO.setStatus(ImportLog.Status.SUSPENDED);

    importLogItemDbVO = this.importLogItemRepository.saveAndFlush(importLogItemDbVO);

    return importLogItemDbVO;
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public ImportLogItemDbVO resetItemId(ImportLogItemDbVO importLogItemDbVO) {
    importLogItemDbVO.setItemId(null);

    importLogItemDbVO = this.importLogItemRepository.saveAndFlush(importLogItemDbVO);

    return importLogItemDbVO;
  }
}
