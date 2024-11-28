package de.mpg.mpdl.inge.service.pubman;

import de.mpg.mpdl.inge.model.db.valueobjects.ImportLog;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogItemDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogItemDetailDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface ImportService {

  void deleteImportLog(Integer importLogId, String token) throws AuthenticationException, IngeApplicationException, AuthorizationException;

  void deleteImportedItems(Integer importLogId, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException;

  void doImport(String importName, String contextId, ImportLogDbVO.Format format, InputStream fileStream, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException, IngeTechnicalException;

  Map<String, List<String>> getAllFormatParameter(ImportLogDbVO.Format format, String token)
      throws AuthenticationException, IngeApplicationException, IngeTechnicalException;

  Map<String, String> getDefaultFormatParameter(ImportLogDbVO.Format format, String token)
      throws AuthenticationException, IngeApplicationException, IngeTechnicalException;

  List<ImportLogItemDetailDbVO> getImportLogItemDetails(Integer importLogDetailId, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException;

  List<ImportLogItemDbVO> getImportLogItems(Integer importLogId, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException;

  List<ImportLogDbVO> getImportLogs(String token) throws AuthenticationException, IngeApplicationException;

  List<ImportLogDbVO> getImportLogsForModerator(String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException, IngeTechnicalException;

  void submitImportedItems(Integer importLogId, ImportLog.SubmitModus submitModus, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException, IngeTechnicalException;
}
