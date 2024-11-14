package de.mpg.mpdl.inge.service.pubman.impl;

import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLog;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogItemDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogItemDetailDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.ContextService;
import de.mpg.mpdl.inge.service.pubman.ImportService;
import de.mpg.mpdl.inge.service.pubman.importprocess.ImportAsyncService;
import de.mpg.mpdl.inge.service.pubman.importprocess.ImportCommonService;
import de.mpg.mpdl.inge.service.util.GrantUtil;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Primary
public class ImportServiceImpl implements ImportService {

  private final AuthorizationService authorizationService;
  private final ContextService contextService;
  private final ImportAsyncService importAsyncService;
  private final ImportCommonService importCommonService;

  public ImportServiceImpl(AuthorizationService authorizationService, ContextService contextService, ImportAsyncService importAsyncService,
      ImportCommonService importCommonService) {
    this.authorizationService = authorizationService;
    this.contextService = contextService;
    this.importAsyncService = importAsyncService;
    this.importCommonService = importCommonService;
  }

  @Transactional(rollbackFor = Throwable.class)
  @Override
  public void deleteImportLog(Integer importLogId, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException {
    AccountUserDbVO accountUserDbVO = getUser(token);

    ImportLogDbVO importLogDbVO = this.importCommonService.getImportLog(importLogId, accountUserDbVO);
    if (null == importLogDbVO) {
      throw new IngeApplicationException("Invalid importLogId");
    }

    checkUserAccess(importLogDbVO, accountUserDbVO);

    this.importCommonService.deleteImportLog(importLogDbVO);
  }

  @Override
  public void deleteImportedItems(Integer importLogId, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException {
    AccountUserDbVO accountUserDbVO = getUser(token);

    ImportLogDbVO importLogDbVO = this.importCommonService.getImportLog(importLogId, accountUserDbVO);
    if (null == importLogDbVO) {
      throw new IngeApplicationException("Invalid importLogId");
    }

    checkUserAccess(importLogDbVO, accountUserDbVO);

    if (!importLogDbVO.getStatus().equals(ImportLog.Status.FINISHED)) {
      throw new IngeApplicationException("Status must be FINISHED");
    }

    int anz = this.importCommonService.countImportedLogItems(importLogDbVO);
    if (0 == anz) {
      throw new IngeApplicationException("There are no imported items to delete");
    }

    this.importCommonService.initializeDelete(importLogDbVO);
    this.importAsyncService.doAsyncDelete(importLogDbVO, token);
  }

  @Override
  public List<ImportLogItemDetailDbVO> getImportLogItemDetails(Integer importLogItemId, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException {
    AccountUserDbVO accountUserDbVO = getUser(token);

    ImportLogItemDbVO importLogItemDbVO = this.importCommonService.getImportLogItem(importLogItemId);
    if (null == importLogItemDbVO) {
      throw new IngeApplicationException("Invalid importLogItemId");
    }

    checkUserAccess(importLogItemDbVO.getParent(), accountUserDbVO);

    List<ImportLogItemDetailDbVO> importLogItemDetailDbVOs = this.importCommonService.getImportLogItemDetails(importLogItemDbVO);

    return importLogItemDetailDbVOs;
  }

  @Override
  public List<ImportLogItemDbVO> getImportLogItems(Integer importLogId, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException {
    AccountUserDbVO accountUserDbVO = getUser(token);

    ImportLogDbVO importLogDbVO = this.importCommonService.getImportLog(importLogId, accountUserDbVO);
    if (null == importLogDbVO) {
      throw new IngeApplicationException("Invalid importLogId");
    }

    checkUserAccess(importLogDbVO, accountUserDbVO);

    List<ImportLogItemDbVO> importLogItemDbVOs = this.importCommonService.getImportLogItems(importLogDbVO);

    return importLogItemDbVOs;
  }

  @Override
  public List<ImportLogDbVO> getImportLogs(String token) throws AuthenticationException, IngeApplicationException {
    AccountUserDbVO accountUserDbVO = getUser(token);

    List<ImportLogDbVO> importLogDbVOs = this.importCommonService.getUserImportLogs(accountUserDbVO.getObjectId());

    return importLogDbVOs;
  }

  @Override
  public List<ImportLogDbVO> getImportLogsForModerator(String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException, IngeTechnicalException {
    AccountUserDbVO accountUserDbVO = getUser(token);

    List<String> moderatorContexts = new ArrayList();
    List<GrantVO> grantVOs = accountUserDbVO.getGrantList();
    for (GrantVO grantVO : grantVOs) {
      if (grantVO.getRole().equals(GrantVO.PredefinedRoles.MODERATOR.frameworkValue())) {
        String contextId = grantVO.getObjectRef();
        ContextDbVO contextDbVO = this.contextService.get(contextId, token);
        if (contextDbVO.getState().equals(ContextDbVO.State.OPENED)) {
          moderatorContexts.add(contextId);
        }
      }
    }

    List<ImportLogDbVO> importLogDbVOs = new ArrayList();
    for (String moderatorContext : moderatorContexts) {
      importLogDbVOs.addAll(this.importCommonService.getContextImportLogs(moderatorContext));
    }

    return importLogDbVOs;
  }

  @Override
  public void submitImportedItems(Integer importLogId, ImportLog.SubmitModus submitModus, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException, IngeTechnicalException {
    AccountUserDbVO accountUserDbVO = getUser(token);

    ImportLogDbVO importLogDbVO = this.importCommonService.getImportLog(importLogId, accountUserDbVO);
    if (null == importLogDbVO) {
      throw new IngeApplicationException("Invalid importLogId");
    }

    checkUserAccess(importLogDbVO, accountUserDbVO);

    if (!importLogDbVO.getStatus().equals(ImportLog.Status.FINISHED)) {
      throw new IngeApplicationException("Status must be FINISHED");
    }

    int anz = this.importCommonService.countImportedLogItems(importLogDbVO);
    if (0 == anz) {
      throw new IngeApplicationException("There are no imported items to submit");
    }

    ContextDbVO contextDbVO = this.contextService.get(importLogDbVO.getContextId(), token);
    if (!contextDbVO.getState().equals(ContextDbVO.State.OPENED)) {
      throw new IngeApplicationException("Import context is not opened");
    }

    switch (submitModus) {
      case SUBMIT:
        if (!ContextDbVO.Workflow.STANDARD.equals(contextDbVO.getWorkflow()) //
            || !GrantUtil.hasRole(accountUserDbVO, GrantVO.PredefinedRoles.DEPOSITOR, contextDbVO.getObjectId()) //
            || GrantUtil.hasRole(accountUserDbVO, GrantVO.PredefinedRoles.MODERATOR, contextDbVO.getObjectId())) {
          throw new IngeApplicationException("User is not Depositor and/or Workflow ist not Standard");
        }
        break;
      case SUBMIT_AND_RELEASE:
        if (!ContextDbVO.Workflow.STANDARD.equals(contextDbVO.getWorkflow()) //
            || !GrantUtil.hasRole(accountUserDbVO, GrantVO.PredefinedRoles.MODERATOR, contextDbVO.getObjectId())) {
          throw new IngeApplicationException("User is not Moderator and/or Workflow ist not Standard");
        }
        break;
      case RELEASE:
        if (!ContextDbVO.Workflow.SIMPLE.equals(contextDbVO.getWorkflow()) //
            || !GrantUtil.hasRole(accountUserDbVO, GrantVO.PredefinedRoles.DEPOSITOR, contextDbVO.getObjectId()) //
                && !GrantUtil.hasRole(accountUserDbVO, GrantVO.PredefinedRoles.MODERATOR, contextDbVO.getObjectId())) {
          throw new IngeApplicationException("User is not Depositor or Moderator and/or Workflow ist not Simple");
        }
        break;
      default:
        throw new IngeApplicationException("Invalid submitModus");
    }

    this.importCommonService.initializeSubmit(importLogDbVO, submitModus);
    this.importAsyncService.doAsyncSubmit(importLogDbVO, submitModus, token);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private void checkUserAccess(ImportLogDbVO importLogDbVO, AccountUserDbVO accountUserDbVO) throws AuthorizationException {
    if (null != importLogDbVO && !accountUserDbVO.getObjectId().equals(importLogDbVO.getUserId())) {
      throw new AuthorizationException("given user is not allowed to access the import.");
    }
  }

  private AccountUserDbVO getUser(String token) throws AuthenticationException, IngeApplicationException {
    AccountUserDbVO accountUserDbVO = this.authorizationService.getUserAccountFromToken(token);

    return accountUserDbVO;
  }
}
