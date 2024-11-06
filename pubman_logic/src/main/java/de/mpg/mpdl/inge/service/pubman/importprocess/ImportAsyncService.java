package de.mpg.mpdl.inge.service.pubman.importprocess;

import de.mpg.mpdl.inge.model.db.valueobjects.ImportLog;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogItemDbVO;
import java.util.List;

public interface ImportAsyncService {

  void doAsyncDelete(ImportLogDbVO importLogDbVO, List<ImportLogItemDbVO> importedLogItemDbVOs, String token);

  void doAsyncSubmit(ImportLogDbVO importLogDbVO, List<ImportLogItemDbVO> importedLogItemDbVOs, ImportLog.SubmitModus submitModus,
      String token);
}
