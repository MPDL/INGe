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

  @Override
  @Async
  public void doAsyncDelete(ImportLogDbVO importLogDbVO, List<ImportLogItemDbVO> importedLogItemDbVOs, String token) {
    for (ImportLogItemDbVO importLogItemDbVO : importedLogItemDbVOs) {
      importLogItemDbVO = this.importLogItemRepository.findById(importLogItemDbVO.getId()).get(); // in the meanwhile the parents have been changed
      this.importCommonService.setSuspensionForDelete(importLogDbVO, importLogItemDbVO);
    }

    this.importCommonService.updateImportLog(importLogDbVO, ImportLogDbVO.PERCENTAGE_DELETE_SUSPEND);

    int counter = 0;
    for (ImportLogItemDbVO importLogItemDbVO : importedLogItemDbVOs) {
      importLogItemDbVO = this.importLogItemRepository.findById(importLogItemDbVO.getId()).get(); // in the meanwhile the parents have been changed
      this.importCommonService.createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE,
          ImportLog.Messsage.import_process_delete_item.name());

      try {
        this.importCommonService.doDelete(importLogDbVO, importLogItemDbVO, token);
      } catch (Exception e) {
        this.importCommonService.doFailDelete(importLogDbVO, importLogItemDbVO, e.toString());
      }

      counter++;
      this.importCommonService.updateImportLog(importLogDbVO,
          ImportLogDbVO.PERCENTAGE_DELETE_END * counter / importedLogItemDbVOs.size() + ImportLogDbVO.PERCENTAGE_DELETE_SUSPEND);
    }

    this.importCommonService.finishDelete(importLogDbVO);
  }

  @Override
  @Async
  public void doAsyncSubmit(ImportLogDbVO importLogDbVO, List<ImportLogItemDbVO> importedLogItemDbVOs, ImportLog.SubmitModus submitModus,
      String token) {
    for (ImportLogItemDbVO importLogItemDbVO : importedLogItemDbVOs) {
      importLogItemDbVO = this.importLogItemRepository.findById(importLogItemDbVO.getId()).get(); // in the meanwhile the parents have been changed
      this.importCommonService.setSuspensionForDelete(importLogDbVO, importLogItemDbVO);
    }

    this.importCommonService.updateImportLog(importLogDbVO, ImportLogDbVO.PERCENTAGE_SUBMIT_SUSPEND);

    int counter = 0;
    for (ImportLogItemDbVO importLogItemDbVO : importedLogItemDbVOs) {
      importLogItemDbVO = this.importLogItemRepository.findById(importLogItemDbVO.getId()).get(); // in the meanwhile the parents have been changed

      switch (submitModus) {
        case SUBMIT:
          this.importCommonService.createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE,
              ImportLog.Messsage.import_process_submit_item.name());
          break;
        case SUBMIT_AND_RELEASE:
          this.importCommonService.createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE,
              ImportLog.Messsage.import_process_submit_relase_item.name());
          break;
        case RELEASE:
          this.importCommonService.createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE,
              ImportLog.Messsage.import_process_relase_item.name());
          break;
      }

      try {
        this.importCommonService.doSubmit(importLogDbVO, importLogItemDbVO, submitModus, token);
      } catch (Exception e) {
        this.importCommonService.doFailSubmit(importLogDbVO, importLogItemDbVO, submitModus, e.toString());
      }

      counter++;
      this.importCommonService.updateImportLog(importLogDbVO,
          ImportLogDbVO.PERCENTAGE_SUBMIT_END * counter / importedLogItemDbVOs.size() + ImportLogDbVO.PERCENTAGE_SUBMIT_SUSPEND);
    }

    this.importCommonService.finishSubmit(importLogDbVO, submitModus);
  }
}
