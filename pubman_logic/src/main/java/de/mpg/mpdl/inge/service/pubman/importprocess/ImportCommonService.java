package de.mpg.mpdl.inge.service.pubman.importprocess;

import de.mpg.mpdl.inge.model.db.valueobjects.ImportLog;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogItemDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogItemDetailDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import org.springframework.transaction.annotation.Transactional;

public interface ImportCommonService {

  @Transactional(rollbackFor = Throwable.class)
  void initializeDelete(ImportLogDbVO importLogDbVO);

  @Transactional(rollbackFor = Throwable.class)
  void setSuspensionForDelete(ImportLogItemDbVO importLogItemDbVO);

  @Transactional(rollbackFor = Throwable.class)
  void doDelete(ImportLogDbVO importLogDbVO, ImportLogItemDbVO importLogItemDbVO, String token)
      throws AuthenticationException, AuthorizationException, IngeApplicationException, IngeTechnicalException;

  @Transactional(rollbackFor = Throwable.class)
  void doFailDelete(ImportLogDbVO importLogDbVO, ImportLogItemDbVO importLogItemDbVO, String message);

  @Transactional(rollbackFor = Throwable.class)
  void finishDelete(ImportLogDbVO importLogDbVO);

  ImportLogDbVO createImportLog(String userId, ImportLogDbVO.Format format);

  ImportLogDbVO updateImportLog(ImportLogDbVO importLogDbVO, Integer percentage);

  ImportLogItemDetailDbVO createImportLogItemDetail(ImportLogItemDbVO importLogItemDbVO, ImportLog.ErrorLevel errorLevel, String message);
}
