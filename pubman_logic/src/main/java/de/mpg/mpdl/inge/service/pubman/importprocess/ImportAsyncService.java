package de.mpg.mpdl.inge.service.pubman.importprocess;

import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLog;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogDbVO;
import de.mpg.mpdl.inge.service.pubman.importprocess.processor.FormatProcessor;
import java.util.Map;

public interface ImportAsyncService {

  void doAsyncDelete(ImportLogDbVO importLogDbVO, String token);

  void doAsyncImport(ImportLogDbVO importLogDbVO, FormatProcessor formatProcessor, ImportLogDbVO.Format format,
      Map<String, String> formatConfiguration, ContextDbVO contextDbVO, String token);

  void doAsyncSubmit(ImportLogDbVO importLogDbVO, ImportLog.SubmitModus submitModus, String token);
}
