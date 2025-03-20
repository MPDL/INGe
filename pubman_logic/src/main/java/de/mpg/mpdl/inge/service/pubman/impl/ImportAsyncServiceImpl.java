package de.mpg.mpdl.inge.service.pubman.impl;

import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLog;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogItemDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.service.pubman.importprocess.ImportAsyncService;
import de.mpg.mpdl.inge.service.pubman.importprocess.ImportCommonService;
import de.mpg.mpdl.inge.service.pubman.importprocess.processor.FormatProcessor;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Primary
public class ImportAsyncServiceImpl implements ImportAsyncService {

  private static final Logger logger = LogManager.getLogger(ImportAsyncServiceImpl.class);

  private final ImportCommonService importCommonService;

  public ImportAsyncServiceImpl(ImportCommonService importCommonService) {
    this.importCommonService = importCommonService;
  }

  @Override
  @Async
  public void doAsyncDelete(ImportLogDbVO importLogDbVO, String token) {
    List<ImportLogItemDbVO> importedLogItemDbVOs = this.importCommonService.getImportedLogItems(importLogDbVO);
    for (ImportLogItemDbVO importLogItemDbVO : importedLogItemDbVOs) {
      this.importCommonService.setSuspensionForDelete(importLogDbVO, importLogItemDbVO);
    }

    this.importCommonService.setPercentageInImportLog(importLogDbVO, ImportLogDbVO.PERCENTAGE_DELETE_SUSPEND);

    int counter = 1;
    for (ImportLogItemDbVO importLogItemDbVO : importedLogItemDbVOs) {
      importLogItemDbVO = this.importCommonService.getImportLogItem(importLogItemDbVO.getId()); // in the meanwhile the parent has been changed
      this.importCommonService.createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE,
          ImportLog.Message.import_process_delete_item.name());

      try {
        this.importCommonService.doDelete(importLogItemDbVO, token);
      } catch (Exception e) {
        this.importCommonService.doFailDelete(importLogItemDbVO, this.importCommonService.getExceptionMessage(e));
        importLogDbVO = importLogItemDbVO.getParent(); // in the meanwhile the parent has been changed
      }

      this.importCommonService.finishImportLogItem(importLogItemDbVO);

      counter++;
      this.importCommonService.setPercentageInImportLog(importLogDbVO,
          ImportLogDbVO.PERCENTAGE_DELETE_END * counter / importedLogItemDbVOs.size() + ImportLogDbVO.PERCENTAGE_DELETE_SUSPEND);

      //      pause();
    }

    this.importCommonService.finishDelete(importLogDbVO);
  }

  @Override
  @Async
  public void doAsyncImport(ImportLogDbVO importLogDbVO, FormatProcessor formatProcessor, ImportLogDbVO.Format format,
      Map<String, String> formatConfiguration, ContextDbVO contextDbVO, String token) {
    int counter = 1;
    int itemCount = 1;

    this.importCommonService.createImportLogItem(importLogDbVO, ImportLog.ErrorLevel.FINE,
        ImportLog.Message.import_process_start_import.name());

    try {
      if (formatProcessor.hasNext()) {
        itemCount = formatProcessor.getLength();
      }

      String localTag = getLocalTag(importLogDbVO);

      while (formatProcessor.hasNext()) {
        ImportLogItemDbVO importLogItemDbVO = null;
        try {
          importLogItemDbVO = this.importCommonService.createImportLogItem(importLogDbVO, ImportLog.ErrorLevel.FINE,
              ImportLog.Message.import_process_import_item.name());

          this.importCommonService.suspendImportLogItem(importLogItemDbVO);

          String singleItem = formatProcessor.next();
          if (null != singleItem && !singleItem.trim().isEmpty()) {

            ItemVersionVO itemVersionVO =
                this.importCommonService.prepareItem(importLogItemDbVO, format, formatConfiguration, contextDbVO, singleItem);
            importLogDbVO = importLogItemDbVO.getParent(); // in the meanwhile the parent has been changed

            if (null != itemVersionVO) {
              this.importCommonService.createItem(itemVersionVO, localTag, importLogItemDbVO, token);
            }

            this.importCommonService.setPercentageInImportLog(importLogDbVO,
                ImportLogDbVO.PERCENTAGE_IMPORT_END * counter / itemCount + ImportLogDbVO.PERCENTAGE_IMPORT_START);

            this.importCommonService.finishImportLogItem(importLogItemDbVO);

            //          pause();
          }
        } catch (Exception e) {
          logger.error("Error during import", e);
          if (importLogItemDbVO != null) {
            this.importCommonService.createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.ERROR,
                this.importCommonService.getExceptionMessage(e));
            this.importCommonService.finishImportLogItem(importLogItemDbVO);
            importLogDbVO = importLogItemDbVO.getParent(); // in the meanwhile the parent has been changed
          }
        } finally {
          counter++;
        }
      }

      this.importCommonService.finishImport(importLogDbVO);

    } catch (Exception e) {
      this.importCommonService.doFailImport(importLogDbVO, this.importCommonService.getExceptionMessage(e), true);
    }

    formatProcessor.getSourceFile().delete();
  }

  @Override
  @Async
  public void doAsyncSubmit(ImportLogDbVO importLogDbVO, ImportLog.SubmitModus submitModus, String token) {
    List<ImportLogItemDbVO> importedLogItemDbVOs = this.importCommonService.getImportedLogItems(importLogDbVO);
    for (ImportLogItemDbVO importLogItemDbVO : importedLogItemDbVOs) {
      this.importCommonService.setSuspensionForSubmit(importLogDbVO, importLogItemDbVO, submitModus);
    }

    this.importCommonService.setPercentageInImportLog(importLogDbVO, ImportLogDbVO.PERCENTAGE_SUBMIT_SUSPEND);

    int counter = 1;
    for (ImportLogItemDbVO importLogItemDbVO : importedLogItemDbVOs) {
      importLogItemDbVO = this.importCommonService.getImportLogItem(importLogItemDbVO.getId()); // in the meanwhile the parent has been changed
      switch (submitModus) {
        case SUBMIT:
          this.importCommonService.createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE,
              ImportLog.Message.import_process_submit_item.name());
          break;
        case SUBMIT_AND_RELEASE:
          this.importCommonService.createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE,
              ImportLog.Message.import_process_submit_release_item.name());
          break;
        case RELEASE:
          this.importCommonService.createImportLogItemDetail(importLogItemDbVO, ImportLog.ErrorLevel.FINE,
              ImportLog.Message.import_process_release_item.name());
          break;
      }

      try {
        this.importCommonService.doSubmit(importLogDbVO, importLogItemDbVO, submitModus, token);
      } catch (Exception e) {
        this.importCommonService.doFailSubmit(importLogItemDbVO, submitModus, this.importCommonService.getExceptionMessage(e));
        importLogDbVO = importLogItemDbVO.getParent(); // in the meanwhile the parent has been changed
      }

      this.importCommonService.finishImportLogItem(importLogItemDbVO);

      counter++;
      this.importCommonService.setPercentageInImportLog(importLogDbVO,
          ImportLogDbVO.PERCENTAGE_SUBMIT_END * counter / importedLogItemDbVOs.size() + ImportLogDbVO.PERCENTAGE_SUBMIT_SUSPEND);

      //      pause();
    }

    this.importCommonService.finishSubmit(importLogDbVO, submitModus);
  }

  /// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  /// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  private String getLocalTag(ImportLogDbVO importLogDbVO) {
    SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    String startDateFormatted = DATE_FORMAT.format(importLogDbVO.getStartDate());
    StringBuilder localTag = new StringBuilder();
    localTag.append(importLogDbVO.getName());
    localTag.append(" ");
    localTag.append(startDateFormatted);

    return localTag.toString();
  }

//  private void pause() {
//    try {
//      logger.info("Pause");
//      Thread.sleep(1000 * 15);
//    } catch (InterruptedException e) {
//      logger.error(e);
//    }
//    logger.info("Pause beendet");
//  }
}
