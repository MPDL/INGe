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

  ImportLogDbVO createImportLog(String userId, ImportLogDbVO.Format format);

  ImportLogItemDetailDbVO createImportLogItemDetail(ImportLogItemDbVO importLogItemDbVO, ImportLog.ErrorLevel errorLevel, String message);

  @Transactional(rollbackFor = Throwable.class)
  void doDelete(ImportLogItemDbVO importLogItemDbVO, String token)
      throws AuthenticationException, AuthorizationException, IngeApplicationException, IngeTechnicalException;

  @Transactional(rollbackFor = Throwable.class)
  void doFailDelete(ImportLogItemDbVO importLogItemDbVO, String message);

  @Transactional(rollbackFor = Throwable.class)
  void doFailSubmit(ImportLogItemDbVO importLogItemDbVO, ImportLog.SubmitModus submitModus, String message);

  @Transactional(rollbackFor = Throwable.class)
  void doSubmit(ImportLogDbVO importLogDbVO, ImportLogItemDbVO importLogItemDbVO, ImportLog.SubmitModus submitModus, String token)
      throws AuthenticationException, AuthorizationException, IngeApplicationException, IngeTechnicalException;

  @Transactional(rollbackFor = Throwable.class)
  void finishDelete(ImportLogDbVO importLogDbVO);

  @Transactional(rollbackFor = Throwable.class)
  void finishSubmit(ImportLogDbVO importLogDbVO, ImportLog.SubmitModus submitModus);

  @Transactional(rollbackFor = Throwable.class)
  void initializeDelete(ImportLogDbVO importLogDbVO);

  @Transactional(rollbackFor = Throwable.class)
  void initializeSubmit(ImportLogDbVO importLogDbVO, ImportLog.SubmitModus submitModus);

  @Transactional(rollbackFor = Throwable.class)
  void setSuspensionForDelete(ImportLogDbVO importLogDbVO, ImportLogItemDbVO importLogItemDbVO);

  @Transactional(rollbackFor = Throwable.class)
  void setSuspensionForSubmit(ImportLogDbVO importLogDbVO, ImportLogItemDbVO importLogItemDbVO, ImportLog.SubmitModus submitModus);

  void updateImportLog(ImportLogDbVO importLogDbVO, Integer percentage);
}
