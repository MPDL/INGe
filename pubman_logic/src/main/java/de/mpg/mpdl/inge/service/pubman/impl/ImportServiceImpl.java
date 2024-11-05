package de.mpg.mpdl.inge.service.pubman.impl;

import de.mpg.mpdl.inge.db.repository.ImportLogItemDetailRepository;
import de.mpg.mpdl.inge.db.repository.ImportLogItemRepository;
import de.mpg.mpdl.inge.db.repository.ImportLogRepository;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLog;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogItemDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogItemDetailDbVO;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.ImportService;
import de.mpg.mpdl.inge.service.pubman.importprocess.ImportAsyncService;
import de.mpg.mpdl.inge.service.pubman.importprocess.ImportCommonService;
import java.util.List;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Primary
public class ImportServiceImpl implements ImportService {

  private final AuthorizationService authorizationService;
  private final ImportAsyncService importAsyncService;
  private final ImportCommonService importCommonService;
  private final ImportLogRepository importLogRepository;
  private final ImportLogItemRepository importLogItemRepository;
  private final ImportLogItemDetailRepository importLogItemDetailRepository;

  public ImportServiceImpl(AuthorizationService authorizationService, ImportAsyncService importAsyncService,
      ImportCommonService importCommonService, ImportLogRepository importLogRepository, ImportLogItemRepository importLogItemRepository,
      ImportLogItemDetailRepository importLogItemDetailRepository) {
    this.authorizationService = authorizationService;
    this.importAsyncService = importAsyncService;
    this.importCommonService = importCommonService;
    this.importLogRepository = importLogRepository;
    this.importLogItemRepository = importLogItemRepository;
    this.importLogItemDetailRepository = importLogItemDetailRepository;
  }

  @Override
  public List<ImportLogDbVO> getImportLogs(String token) throws AuthenticationException, IngeApplicationException {

    AccountUserDbVO accountUserDbVO = getUser(token);

    List<ImportLogDbVO> importLogDbVOs = this.importLogRepository.findAllByUserId(accountUserDbVO.getObjectId());

    return importLogDbVOs;
  }

  @Override
  public List<ImportLogItemDbVO> getImportLogItems(Integer importLogId, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException {

    AccountUserDbVO accountUserDbVO = getUser(token);

    ImportLogDbVO importLogDbVO = getImportLog(importLogId, accountUserDbVO);
    if (null == importLogDbVO) {
      throw new IngeApplicationException("Invalid importLogId");
    }

    checkUserAccess(importLogDbVO, accountUserDbVO);

    List<ImportLogItemDbVO> importLogItemDbVOs = this.importLogItemRepository.findByParent(importLogDbVO);

    return importLogItemDbVOs;
  }

  @Override
  public List<ImportLogItemDetailDbVO> getImportLogItemDetails(Integer importLogItemId, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException {

    AccountUserDbVO accountUserDbVO = getUser(token);

    ImportLogItemDbVO importLogItemDbVO = getImportLogItem(importLogItemId, accountUserDbVO);
    if (null == importLogItemDbVO) {
      throw new IngeApplicationException("Invalid importLogItemId");
    }

    checkUserAccess(importLogItemDbVO.getParent(), accountUserDbVO);

    List<ImportLogItemDetailDbVO> importLogItemDetailDbVOs = this.importLogItemDetailRepository.findByImportLogItem(importLogItemDbVO);

    return importLogItemDetailDbVOs;
  }

  @Transactional(rollbackFor = Throwable.class)
  @Override
  public void deleteImportLog(Integer importLogId, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException {

    AccountUserDbVO accountUserDbVO = getUser(token);

    ImportLogDbVO importLogDbVO = getImportLog(importLogId, accountUserDbVO);
    if (null == importLogDbVO) {
      throw new IngeApplicationException("Invalid importLogId");
    }

    checkUserAccess(importLogDbVO, accountUserDbVO);

    this.importLogRepository.delete(importLogDbVO);
  }

  @Override
  public void deleteImportedItems(Integer importLogId, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException {
    AccountUserDbVO accountUserDbVO = getUser(token);

    ImportLogDbVO importLogDbVO = getImportLog(importLogId, accountUserDbVO);
    if (null == importLogDbVO) {
      throw new IngeApplicationException("Invalid importLogId");
    }
    if (!importLogDbVO.getStatus().equals(ImportLog.Status.FINISHED)) {
      throw new IngeApplicationException("Status must be FINISHED");
    }
    List<ImportLogItemDbVO> importLogItemDbVOs = this.importLogItemRepository.findByParentAndItemId(importLogDbVO);
    if (null == importLogItemDbVOs || importLogItemDbVOs.isEmpty()) {
      throw new IngeApplicationException("There are no imported items to delete");
    }

    checkUserAccess(importLogDbVO, accountUserDbVO);

    this.importCommonService.initializeDelete(importLogDbVO);
    this.importAsyncService.doAsyncDelete(importLogDbVO, importLogItemDbVOs, token);
  }

  ////////////////////////////////////////////////////
  ////////////////////////////////////////////////////

  private AccountUserDbVO getUser(String token) throws AuthenticationException, IngeApplicationException {
    AccountUserDbVO accountUserDbVO = this.authorizationService.getUserAccountFromToken(token);

    return accountUserDbVO;
  }

  private void checkUserAccess(ImportLogDbVO importLogDbVO, AccountUserDbVO accountUserDbVO) throws AuthorizationException {
    if (null != importLogDbVO && !accountUserDbVO.getObjectId().equals(importLogDbVO.getUserId())) {
      throw new AuthorizationException("given user is not allowed to access the import.");
    }
  }

  private ImportLogDbVO getImportLog(Integer importLogId, AccountUserDbVO accountUserDbVO) {

    ImportLogDbVO importLogDbVO = this.importLogRepository.findById(importLogId).orElse(null);

    return importLogDbVO;
  }

  private ImportLogItemDbVO getImportLogItem(Integer importLogItemId, AccountUserDbVO accountUserDbVO) {

    ImportLogItemDbVO importLogItemDbVO = this.importLogItemRepository.findById(importLogItemId).orElse(null);

    return importLogItemDbVO;
  }
}
