package de.mpg.mpdl.inge.service.pubman.importprocess;

import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLog;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogItemDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogItemDetailDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.importprocess.processor.FormatProcessor;
import java.util.List;
import java.util.Map;

public interface ImportCommonService {

  int countImportedLogItems(ImportLogDbVO importLogDbVO);

  ImportLogItemDbVO createImportLogItem(ImportLogDbVO importLogDbVO, ImportLog.ErrorLevel errorLevel, String message);

  ImportLogItemDetailDbVO createImportLogItemDetail(ImportLogItemDbVO importLogItemDbVO, ImportLog.ErrorLevel errorLevel, String message);

  void createItem(ItemVersionVO itemVersionVO, String localTag, ImportLogItemDbVO importLogItemDbVO, String token)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  void deleteImportLog(ImportLogDbVO importLogDbVO);

  void doDelete(ImportLogItemDbVO importLogItemDbVO, String token)
      throws AuthenticationException, AuthorizationException, IngeApplicationException, IngeTechnicalException;

  void doFailDelete(ImportLogItemDbVO importLogItemDbVO, String message);

  void doFailImport(ImportLogDbVO importLogDbVO, String message, boolean newDetail);

  void doFailSubmit(ImportLogItemDbVO importLogItemDbVO, ImportLog.SubmitModus submitModus, String message);

  void doSubmit(ImportLogDbVO importLogDbVO, ImportLogItemDbVO importLogItemDbVO, ImportLog.SubmitModus submitModus, String token)
      throws AuthenticationException, AuthorizationException, IngeApplicationException, IngeTechnicalException;

  void finishDelete(ImportLogDbVO importLogDbVO);

  void finishImport(ImportLogDbVO importLogDbVO);

  void finishImportLogItem(ImportLogItemDbVO importLogItemDbVO);

  void finishSubmit(ImportLogDbVO importLogDbVO, ImportLog.SubmitModus submitModus);

  List<ImportLogDbVO> getContextImportLogs(String contextId);

  String getExceptionMessage(Throwable exception);

  FormatProcessor getFormatProcessor(ImportLogDbVO importLogDbVO, ImportLogDbVO.Format format);

  ImportLogDbVO getImportLog(Integer importLogId, AccountUserDbVO accountUserDbVO);

  ImportLogItemDbVO getImportLogItem(Integer importLogItemId);

  List<ImportLogItemDetailDbVO> getImportLogItemDetails(ImportLogItemDbVO importLogItemDbVO);

  List<ImportLogItemDbVO> getImportLogItems(ImportLogDbVO importLogDbVO);

  List<ImportLogItemDbVO> getImportedLogItems(ImportLogDbVO importLogDbVO);

  List<ImportLogDbVO> getUserImportLogs(String userId);

  void initializeDelete(ImportLogDbVO importLogDbVO);

  ImportLogDbVO initializeImport(AccountUserDbVO accountUserDbVO, ImportLogDbVO.Format format, String importName, String contextId);

  void initializeSubmit(ImportLogDbVO importLogDbVO, ImportLog.SubmitModus submitModus);

  ItemVersionVO prepareItem(ImportLogItemDbVO importLogItemDbVO, ImportLogDbVO.Format format, Map<String, String> formatConfiguration,
      ContextDbVO contextDbVO, String singleItem);

  void setPercentageInImportLog(ImportLogDbVO importLogDbVO, Integer percentage);

  void setSuspensionForDelete(ImportLogDbVO importLogDbVO, ImportLogItemDbVO importLogItemDbVO);

  void setSuspensionForSubmit(ImportLogDbVO importLogDbVO, ImportLogItemDbVO importLogItemDbVO, ImportLog.SubmitModus submitModus);

  void suspendImportLogItem(ImportLogItemDbVO importLogItemDbVO);
}
