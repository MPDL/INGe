package de.mpg.mpdl.inge.service.pubman.importprocess;

import de.mpg.mpdl.inge.model.db.valueobjects.ImportLog;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogItemDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogItemDetailDbVO;

public interface ImportCommonService {

  ImportLogDbVO createImportLog(String userId, ImportLogDbVO.Format format);

  ImportLogItemDbVO createImportLogItem(ImportLogDbVO importLogDbVO, String message);

  ImportLogItemDetailDbVO createImportLogItemDetail(ImportLogItemDbVO importLogItemDbVO, ImportLog.ErrorLevel errorLevel, String message);

  ImportLogDbVO finishImportLog(ImportLogDbVO importLogDbVO);

  ImportLogDbVO reopenImportLog(ImportLogDbVO importLogDbVO);

  ImportLogDbVO updateImportLog(ImportLogDbVO importLogDbVO, Integer percentage);

  ImportLogItemDbVO finishImportLogItem(ImportLogItemDbVO importLogItemDbVO);

  ImportLogItemDbVO suspendImportLogItem(ImportLogItemDbVO importLogItemDbVO);

  ImportLogItemDbVO resetItemId(ImportLogItemDbVO importLogItemDbVO);
}
