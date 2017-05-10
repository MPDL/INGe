package de.mpg.mpdl.inge.service.aa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
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
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceImpl;
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
          modelMapper.readValue(
              ResourceUtil.getResourceAsStream("aa.json",
                  AuthorizationService.class.getClassLoader()), Map.class);
    } catch (Exception e) {
      throw new RuntimeException("Problem with parsing aa.json file.", e);
    }
  }



  // @Around("execution(public * de.mpg.mpdl.inge.service.pubman.PubItemService.create(PubItemVO,
  // String)) && args(pubItemVo, authenticationToken)")
  // private void checkPubItemServiceCreate(ProceedingJoinPoint jp, PubItemVO pubItem,
  // String authenticationToken) throws Throwable {
  // AccountUserVO userAccount = checkLoginRequired(authenticationToken);
  // checkRole(userAccount, "depositor", pubItem.getContext().getObjectId());
  // }



  public void checkPubItemAa(PubItemVO pubItem, ContextVO context, AccountUserVO userAccount,
      String methodName) throws AaException {

    checkAa("de.mpg.mpdl.inge.service.pubman.PubItemService", methodName, userAccount, pubItem
        .getOwner().getObjectId(),
        pubItem.getContext().getObjectId().replace("/ir/context/escidoc:", "pure_"), pubItem
            .getVersion().getState(), context.getAdminDescriptor().getWorkflow());

  }

  public QueryBuilder modifyPubItemQueryForAa(QueryBuilder query, AccountUserVO userAccount)
      throws AaException {

    QueryBuilder filterQuery =
        getAaFilterQuery("de.mpg.mpdl.inge.service.pubman.PubItemService", userAccount);

    if (filterQuery != null) {
      BoolQueryBuilder completeQuery = QueryBuilders.boolQuery();
      completeQuery.must(query);
      completeQuery.filter(filterQuery);
      return completeQuery;
    }

    return query;

  }


  private QueryBuilder getAaFilterQuery(String serviceName, AccountUserVO userAccount) {
    List<Map<String, Object>> allowedMap = aaMap.get(serviceName).get("get");

    /*
     * if (userAccount == null) { return
     * QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("version.state", "RELEASED")); }
     */

    BoolQueryBuilder bqb = QueryBuilders.boolQuery();
    if (allowedMap != null) {

      for (Map<String, Object> rules : allowedMap) {
        List<GrantVO> matchedGrants = new ArrayList<GrantVO>();
        if (rules.containsKey("rolename") && userAccount != null) {
          for (GrantVO grant : userAccount.getGrants()) {
            if (grant.getRole().equals((String) rules.get("rolename"))) {
              matchedGrants.add(grant);
              break;
            }
          }

        }

        // If only role is set and matches everything can be retrieved
        if (!matchedGrants.isEmpty() && rules.size() == 1) {
          return null;
        } else {
          QueryBuilder subQuery = getAaSubQuery(rules, userAccount, matchedGrants);
          if (subQuery != null) {
            bqb.should(subQuery);
          }
        }

      }
      return bqb;
    }
    return null;
  }


  private QueryBuilder getAaSubQuery(Map<String, Object> ruleMap, AccountUserVO userAccount,
      List<GrantVO> matchedGrants) {
    BoolQueryBuilder currentQb = QueryBuilders.boolQuery();
    for (Entry<String, Object> rule : ruleMap.entrySet()) {
      switch (rule.getKey()) {
        case "owner": {
          if (userAccount != null) {
            currentQb.must(QueryBuilders.termQuery(PubItemServiceImpl.INDEX_OWNER_OBJECT_ID,
                userAccount.getReference().getObjectId()));
          } else {
            return null;
          }

          break;
        }
        case "rolematch": {
          BoolQueryBuilder contextQb = QueryBuilders.boolQuery();

          for (GrantVO grant : matchedGrants) {
            if (ruleMap.get("rolematch").equals(grant.getGrantType())) {
              contextQb.should(QueryBuilders.termQuery(PubItemServiceImpl.INDEX_CONTEXT_OBEJCT_ID,
                  grant.getObjectRef()));
            }
          }
          if (contextQb.hasClauses()) {
            currentQb.must(contextQb);
          } else {
            return null;
          }
          break;
        }

        case "version-state": {
          BoolQueryBuilder versionStateQb = QueryBuilders.boolQuery();
          for (String versionState : (List<String>) rule.getValue()) {
            versionStateQb.should(QueryBuilders.termQuery(PubItemServiceImpl.INDEX_VERSION_STATE,
                versionState));
          }
          currentQb.must(versionStateQb);
          break;

        }
        case "public-state": {
          BoolQueryBuilder publicStateQb = QueryBuilders.boolQuery();
          for (String publicState : (List<String>) rule.getValue()) {
            publicStateQb.should(QueryBuilders.termQuery(PubItemServiceImpl.INDEX_PUBLIC_STATE,
                publicState));
          }

          currentQb.must(publicStateQb);
          break;
        }

      }
    }
    return currentQb;
  }



  private void checkAa(String serviceName, String methodName, AccountUserVO userAccount,
      String currentOwnerId, String currentRoleMatch, State currentVersionState,
      Workflow currentWorkflow) throws AaException {
    List<Map<String, Object>> allowedMap = aaMap.get(serviceName).get(methodName);

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
                break;
              }
              case "public-state": {
                checkPublicState(currentVersionState,
                    ((List<String>) rule.getValue()).toArray(new String[] {}));
                break;
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

         
        }

        if (lastExceptionOfRule == null) {
          return;
        }
      }
      throw new AaException(lastExceptionOfAll);


    }



  }

  public AccountUserVO checkLoginRequired(String authenticationToken) throws AaException {

    try {
      return userAccountService.get(authenticationToken);
    } catch (IngeServiceException e) {
      throw new AaException("You have to be logged in with a valid token", e);
    }
  }

  public void checkUserAccountExists(AccountUserVO userAccount) throws AaException {

    if (userAccount == null)
      throw new AaException("You have to be logged in with a valid token");

  }

  private void checkRole(AccountUserVO providedUserAccountVO, String requiredRoleName,
      String... requiredMatchIds) throws AaException {
    checkUserAccountExists(providedUserAccountVO);
    for (GrantVO grant : providedUserAccountVO.getGrants()) {
      if (grant.getRole().equals(requiredRoleName) && (requiredMatchIds == null
          || Arrays.stream(requiredMatchIds).anyMatch(id -> id.equals(grant.getObjectRef())))) {
        return;
      }

    }
    throw new AaException("User " + providedUserAccountVO.getName() + " ("
        + providedUserAccountVO.getReference().getObjectId() + ") " + " has no " + requiredRoleName
        + " grant for " + Arrays.toString(requiredMatchIds));
  }

  private void checkIsOwner(AccountUserVO providedUserAccountVO, String ownerId) throws AaException {
    checkUserAccountExists(providedUserAccountVO);
    if (!providedUserAccountVO.getReference().getObjectId().equals(ownerId)) {
      throw new AaException("User " + providedUserAccountVO.getName() + " ("
          + providedUserAccountVO.getReference().getObjectId() + ") "
          + " is required to be an owner of object");
    }
  }


  private void checkVersionState(State currentVersionState, String... requiredStates)
      throws AaException {
    if (!Arrays.stream(requiredStates).anyMatch(state -> state.equalsIgnoreCase(currentVersionState.name()))) {
      throw new AaException(
          "Item is not in one of the required version states: " + Arrays.toString(requiredStates));
    }

  }

  private void checkPublicState(State currentPublicState, String... requiredStates)
      throws AaException {
    if (!Arrays.stream(requiredStates).anyMatch(state -> state.equalsIgnoreCase(currentPublicState.name()))) {
      throw new AaException(
          "Item is not in one of the required public states: " + Arrays.toString(requiredStates));
    }

  }

  private void checkWorkflow(Workflow currentWorkflow, String requiredWorkflow) throws AaException {
    if (!currentWorkflow.name().equalsIgnoreCase(requiredWorkflow)) {
      throw new AaException("Context is set to workflow " + currentWorkflow
          + ". Required workflow: " + requiredWorkflow);
    }
  }


}
