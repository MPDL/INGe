package de.mpg.mpdl.inge.service.pubman;

import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogItemDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogItemDetailDbVO;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public interface ImportService {

  List<ImportLogDbVO> getImportLogs(String token) throws AuthenticationException, IngeApplicationException;

  List<ImportLogItemDbVO> getImportLogItems(Integer importLogId, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException;

  List<ImportLogItemDetailDbVO> getImportLogItemDetails(Integer importLogDetailId, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException;

  @Transactional(rollbackFor = Throwable.class)
  void deleteImportLog(Integer importLogId, String token) throws AuthenticationException, IngeApplicationException, AuthorizationException;
}
