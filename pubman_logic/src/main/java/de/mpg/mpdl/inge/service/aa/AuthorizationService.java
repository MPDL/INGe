package de.mpg.mpdl.inge.service.aa;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.mpg.mpdl.inge.es.connector.ModelMapper;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO.State;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PublicationAdminDescriptorVO.Workflow;
import de.mpg.mpdl.inge.service.exceptions.AaException;
import de.mpg.mpdl.inge.service.pubman.UserAccountService;
import de.mpg.mpdl.inge.services.IngeServiceException;
import de.mpg.mpdl.inge.util.ResourceUtil;


@Service
public class AuthorizationService {


  private Map<String, Map<String, List<Map<String, Object>>>> aaMap;

  @Autowired
  private UserAccountService userAccountService;

  @Autowired
  ModelMapper modelMapper;

  @Autowired
  public AuthorizationService(ModelMapper modelMapper) {
    try {
      aaMap =
          modelMapper.readValue(ResourceUtil.getResourceAsFile("aa.json",
              AuthorizationService.class.getClassLoader()), Map.class);
    } catch (Exception e) {
      throw new RuntimeException("Problem with parsing aa.json file.", e);
    }
  }



  // @Around("execution(public * de.mpg.mpdl.inge.service.pubman.PubItemService.create(PubItemVO, String)) && args(pubItemVo, userToken)")
  // private void checkPubItemServiceCreate(ProceedingJoinPoint jp, PubItemVO pubItem,
  // String userToken) throws Throwable {
  // AccountUserVO userAccount = checkLoginRequired(userToken);
  // checkRole(userAccount, "depositor", pubItem.getContext().getObjectId());
  // }



  public void checkPubItemAa(PubItemVO pubItem, ContextVO context, AccountUserVO userAccount,
      String methodName) throws AaException {

    checkAa("de.mpg.mpdl.inge.service.pubman.PubItemService", methodName, userAccount, pubItem
        .getOwner().getObjectId(),
        pubItem.getContext().getObjectId().replace("/ir/context/escidoc:", "pure_"), pubItem
            .getVersion().getState(), context.getAdminDescriptor().getWorkflow());

  }



  private void checkAa(String serviceName, String methodName, AccountUserVO userAccount,
      String currentOwnerId, String currentRoleMatch, State currentVersionState,
      Workflow currentWorkflow) throws AaException {
    List<Map<String, Object>> allowedMap =
        aaMap.get("de.mpg.mpdl.inge.service.pubman.PubItemService").get(methodName);

    if (allowedMap != null) {
      Exception lastExceptionOfAll = null;
      for (Map<String, Object> rules : allowedMap) {
        Exception lastExceptionOfRule = null;
        for (Entry<String, Object> rule : rules.entrySet()) {

          try {
            switch (rule.getKey()) {
              case "owner": {
                checkIsOwner(userAccount, currentOwnerId);
                break;
              }
              case "rolename": {
                if (rules.containsKey("rolematch")) {
                  checkRole(userAccount, (String) rule.getValue(), currentRoleMatch);
                } else {
                  checkRole(userAccount, (String) rule.getValue());
                }
                break;
              }
              case "version-state": {
                checkVersionState(currentVersionState,
                    ((List<String>) rule.getValue()).toArray(new String[] {}));
              }
              case "workflow": {
                checkWorkflow(currentWorkflow, (String) rule.getValue());
                break;
              }

            }
          } catch (AaException e) {
            lastExceptionOfRule = e;
            lastExceptionOfAll = e;
            break;
          }

          if (lastExceptionOfRule == null) {
            return;
          }
        }


      }
      throw new AaException(lastExceptionOfAll);


    }



  }

  public AccountUserVO checkLoginRequired(String userToken) throws AaException {

    try {
      return userAccountService.get(userToken);
    } catch (IngeServiceException e) {
      throw new AaException("You have to be logged in with a valid token", e);
    }
  }

  private void checkRole(AccountUserVO providedUserAccountVO, String requiredRoleName,
      String... requiredMatchIds) throws AaException {
    for (GrantVO grant : providedUserAccountVO.getGrants()) {
      if (grant.getRole().equals(requiredRoleName) && (requiredMatchIds == null
          || Arrays.stream(requiredMatchIds).anyMatch(id -> id.equals(grant.getObjectRef())))) {
        return;
      }

    }
    throw new AaException("User " + providedUserAccountVO.getName() + " (" + providedUserAccountVO.getReference().getObjectId() +") " + " has no "
        + requiredRoleName + " grant for " + Arrays.toString(requiredMatchIds));
  }

  private void checkIsOwner(AccountUserVO providedUserAccountVO, String ownerId) throws AaException {
    if (!providedUserAccountVO.getReference().getObjectId().equals(ownerId)) {
      throw new AaException("User " + providedUserAccountVO.getName() + " ("
          + providedUserAccountVO.getReference().getObjectId() + ") "
          + " is required to be an owner of object");
    }
  }


  private void checkVersionState(State currentVersionState, String... requiredStates)
      throws AaException {
    if (!Arrays.stream(requiredStates).anyMatch(state -> state.equals(currentVersionState))) {
      throw new AaException("Item is not in one of the required version states: " + Arrays.toString(requiredStates));
    }

  }

  private void checkWorkflow(Workflow currentWorkflow, String requiredWorkflow) throws AaException {
    if (!currentWorkflow.toString().equals(requiredWorkflow)) {
      throw new AaException("Context is set to workflow " + currentWorkflow
          + ". Required workflow: " + requiredWorkflow);
    }
  }


}
