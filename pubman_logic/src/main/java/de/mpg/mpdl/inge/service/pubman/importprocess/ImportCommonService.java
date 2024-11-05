package de.mpg.mpdl.inge.service.pubman.importprocess;

import de.mpg.mpdl.inge.model.db.valueobjects.ImportLog;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogItemDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogItemDetailDbVO;
import java.util.Date;

public interface ImportCommonService {

  ImportLogDbVO createImportLog(String userId, ImportLogDbVO.Format format);

  ImportLogItemDbVO createImportLogItem(ImportLogDbVO importLogDbVO, String message);

  ImportLogItemDetailDbVO createImportLogItemDetail(ImportLogItemDbVO importLogItemDbVO, ImportLog.ErrorLevel errorLevel, String message);

  ImportLogDbVO updateImportLog(ImportLogDbVO importLogDbVO, Date endDate, ImportLog.Status status);

  ImportLogDbVO updateImportLog(ImportLogDbVO importLogDbVO, Integer percentage);

  ImportLogItemDbVO updateImportLogItem(ImportLogItemDbVO importLogItemDbVO, ImportLog.Status status);

  ImportLogItemDbVO resetItemId(ImportLogItemDbVO importLogItemDbVO);
}
