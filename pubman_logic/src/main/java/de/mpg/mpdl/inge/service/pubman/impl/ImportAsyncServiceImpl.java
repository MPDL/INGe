package de.mpg.mpdl.inge.service.pubman.impl;

import de.mpg.mpdl.inge.db.repository.ImportLogItemRepository;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLog;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogItemDbVO;
import de.mpg.mpdl.inge.service.pubman.importprocess.ImportAsyncService;
import de.mpg.mpdl.inge.service.pubman.importprocess.ImportCommonService;
import java.util.List;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Primary
public class ImportAsyncServiceImpl implements ImportAsyncService {

  private final ImportCommonService importCommonService;
  private final ImportLogItemRepository importLogItemRepository;

  public ImportAsyncServiceImpl(ImportCommonService importCommonService, ImportLogItemRepository importLogItemRepository) {
    this.importCommonService = importCommonService;
    this.importLogItemRepository = importLogItemRepository;
  }

  @Async
  public void doAsyncDelete(ImportLogDbVO importLogDbVO, List<ImportLogItemDbVO> importLogItemDbVOs, String token) {
    for (ImportLogItemDbVO importLogItemDbVO : importLogItemDbVOs) {
      this.importCommonService.setSuspensionForDelete(importLogItemDbVO);
    }

    this.importCommonService.updateImportLog(importLogDbVO, ImportLogDbVO.PERCENTAGE_DELETE_SUSPEND);

    int counter = 0;
    for (ImportLogItemDbVO importLogItemDbVO : importLogItemDbVOs) {
      this.importCommonService.createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE,
          ImportLog.Messsage.import_process_delete_item.name());
      try {
        this.importCommonService.doDelete(importLogDbVO, importLogItemDbVO, token);
      } catch (Exception e) {
        this.importCommonService.doFailDelete(importLogDbVO, importLogItemDbVO, e.toString());
      }
      counter++;
      this.importCommonService.updateImportLog(importLogDbVO,
          ImportLogDbVO.PERCENTAGE_DELETE_END * counter / importLogItemDbVOs.size() + ImportLogDbVO.PERCENTAGE_DELETE_SUSPEND);
    }

    this.importCommonService.finishDelete(importLogDbVO);
  }
}
