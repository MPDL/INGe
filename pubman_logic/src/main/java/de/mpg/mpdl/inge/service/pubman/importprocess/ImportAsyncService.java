package de.mpg.mpdl.inge.service.pubman.importprocess;

import de.mpg.mpdl.inge.model.db.valueobjects.ImportLog;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogDbVO;

public interface ImportAsyncService {

  void doAsyncDelete(ImportLogDbVO importLogDbVO, String token);

  void doAsyncSubmit(ImportLogDbVO importLogDbVO, ImportLog.SubmitModus submitModus, String token);
}
