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
  public ImportLogDbVO updateImportLog(ImportLogDbVO importLogDbVO, Date endDate, ImportLog.Status status) {
    importLogDbVO.setEndDate(endDate);
    importLogDbVO.setStatus(status);

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

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public ImportLogItemDbVO updateImportLogItem(ImportLogItemDbVO importLogItemDbVO, ImportLog.Status status) {
    importLogItemDbVO.setEndDate(new Date());
    importLogItemDbVO.setStatus(status);

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
