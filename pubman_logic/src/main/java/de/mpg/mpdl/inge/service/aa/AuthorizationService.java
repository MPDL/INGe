package de.mpg.mpdl.inge.service.aa;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQuery;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.util.MapperFactory;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.service.aa.IpListProvider.IpRange;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.ContextService;
import de.mpg.mpdl.inge.service.pubman.OrganizationService;
import de.mpg.mpdl.inge.service.pubman.UserAccountService;
import de.mpg.mpdl.inge.util.ResourceUtil;

@Service
public class AuthorizationService {

  private final static Logger logger = Logger.getLogger(AuthorizationService.class);

  private final Map<String, Object> aaMap;

  @Autowired
  private UserAccountService userAccountService;

  ObjectMapper modelMapper = MapperFactory.getObjectMapper();

  @Autowired
  OrganizationService ouService;

  @Autowired
  ContextService ctxService;

  @Autowired
  @Qualifier("mpgJsonIpListProvider")
  private IpListProvider ipListProvider;


  public enum AccessType
  {
    GET("get"),
    READ_FILE("readFile"),
    SUBMIT("submit"),
    RELEASE("release"),
    DELETE("delete"),
    WITHDRAW("withdraw"),
    EDIT("update"),
    REVISE("revise");

  private String methodName;

  AccessType(String methodName) {
      this.setMethodName(methodName);
    }

  public String getMethodName() {
    return methodName;
  }

  public void setMethodName(String methodName) {
    this.methodName = methodName;
  }

  }

  public AuthorizationService() {
    try {
      aaMap = modelMapper.readValue(ResourceUtil.getResourceAsStream("aa.json", AuthorizationService.class.getClassLoader()), Map.class);
    } catch (Exception e) {
      throw new RuntimeException("Problem with parsing aa.json file.", e);
    }
  }

  public Query modifyQueryForAa(String serviceName, Query query, Object... objects)
      throws AuthenticationException, AuthorizationException, IngeApplicationException, IngeTechnicalException {

    Query filterQuery = getAaFilterQuery(serviceName, objects);

    if (filterQuery != null) {
      BoolQuery.Builder completeQuery = new BoolQuery.Builder();
      //BoolQueryBuilder completeQuery = QueryBuilders.boolQuery();
      if (query != null) {
        completeQuery.must(query);
      }
      completeQuery.filter(filterQuery);
      return completeQuery.build()._toQuery();
    }

    return query;
  }

  private Query getAaFilterQuery(String serviceName, Object... objects)
      throws AuthorizationException, IngeApplicationException, IngeTechnicalException, AuthenticationException {

    Map<String, Map<String, Object>> serviceMap = (Map<String, Map<String, Object>>) aaMap.get(serviceName);

    List<String> order = (List<String>) serviceMap.get("technical").get("order");
    Map<String, String> indices = (Map<String, String>) serviceMap.get("technical").get("indices");
    List<Map<String, Object>> allowedMap = (List<Map<String, Object>>) serviceMap.get("get");

    AccountUserDbVO userAccount;
    try {
      userAccount = ((Principal) objects[order.indexOf("user")]).getUserAccount();
    } catch (NullPointerException e) {
      userAccount = null;
    }

    BoolQuery.Builder bqb = new BoolQuery.Builder();
    if (allowedMap == null) {
      throw new AuthorizationException("No rules for service " + serviceName + ", method " + "get");
    }

    // everybody can see anything
    if (allowedMap.isEmpty()) {
      return null;
    }

    for (Map<String, Object> rules : allowedMap) {

      BoolQuery.Builder subQb = new BoolQuery.Builder();
      boolean userMatch = false;

      // Everybody is allowed to see everything
      rulesLoop: for (Entry<String, Object> rule : rules.entrySet()) {
        switch (rule.getKey()) {
          case "user": {
            if (userAccount != null) {
              Map<String, String> userMap = (Map<String, String>) rule.getValue();

              if (userMap.containsKey("field_user_id_match")) {
                String value = (String) userMap.get("field_user_id_match");
                AccountUserDbVO finalUserAccount = userAccount;
                subQb.must(TermQuery.of(t -> t.field(indices.get(value)).value(finalUserAccount.getObjectId()))._toQuery());
                userMatch = true;
              }

              if (userMap.containsKey("role") || userMap.containsKey("field_grant_id_match")
                  || userMap.containsKey("field_ctx_ou_id_match")) {
                BoolQuery.Builder grantQueryBuilder = new BoolQuery.Builder();
                for (GrantVO grant : userAccount.getGrantList()) {
                  if (grant.getRole().equalsIgnoreCase((String) userMap.get("role"))) {
                    userMatch = true;
                    if (userMap.get("field_grant_id_match") != null) {
                      // If grant is of type "ORGANIZATION", get all parents of organization up to firstLevel as potential matches
                      if (grant.getObjectRef() != null && grant.getObjectRef().startsWith("ou")) {
                        List<String> parents = ouService.getIdPath(grant.getObjectRef()); // enthält auch eigene Ou
                        parents.remove(parents.size() - 1); // remove root
                        List<FieldValue> grantFieldMatchValues =
                            new ArrayList<>(parents.stream().map(i -> FieldValue.of(i)).collect(Collectors.toList()));
                        grantQueryBuilder.should(TermsQuery
                            .of(t -> t.field(indices.get(userMap.get("field_grant_id_match"))).terms(te -> te.value(grantFieldMatchValues)))
                            ._toQuery());
                      } else {
                        grantQueryBuilder.should(TermQuery
                            .of(t -> t.field(indices.get(userMap.get("field_grant_id_match"))).value(grant.getObjectRef()))._toQuery());
                      }
                    } else if (userMap.get("field_ctx_ou_id_match") != null) {
                      if (grant.getObjectRef() != null && grant.getObjectRef().startsWith("ctx")) {
                        ContextDbVO ctx = ctxService.get(grant.getObjectRef(), null);
                        String ouId = ctx.getResponsibleAffiliations().get(0).getObjectId(); // Ou des Kontextes
                        grantQueryBuilder
                            .should(TermQuery.of(t -> t.field(indices.get(userMap.get("field_ctx_ou_id_match"))).value(ouId))._toQuery());
                      }
                    }
                  }
                }
                BoolQuery grantQuery = grantQueryBuilder.build();
                if (grantQuery.should() != null && !grantQuery.should().isEmpty()) {
                  subQb.must(grantQuery._toQuery());
                }
              }
            }

            if (!userMatch) {
              //reset queryBuilder
              subQb = new BoolQuery.Builder();
              break rulesLoop;
            }

            break;
          }

          default: {
            String key = rule.getKey();
            String index = indices.get(key);

            if (index == null) {
              throw new AuthorizationException("No index in aa.json defined for: " + key);
            }

            if (rule.getValue() instanceof Collection<?>) {
              List<String> valuesToCompare = (List<String>) rule.getValue();
              if (valuesToCompare.size() > 1) {
                BoolQuery.Builder valueQueryBuilder = new BoolQuery.Builder();
                for (String val : valuesToCompare) {
                  valueQueryBuilder.should(TermQuery.of(t -> t.field(index).value(val))._toQuery());
                }
                subQb.must(valueQueryBuilder.build()._toQuery());
              } else {
                subQb.must(TermQuery.of(t -> t.field(index).value(valuesToCompare.get(0)))._toQuery());
              }
            } else {
              Object value = getFieldValueOrString(order, objects, (String) rule.getValue());
              if (value != null) {
                subQb.must(TermQuery.of(t -> t.field(index).value(value.toString()))._toQuery());
              }
            }
            break;
          }
        }
      }

      BoolQuery subQ = subQb.build();
      if (subQ.must() != null && !subQ.must().isEmpty()) {
        bqb.should(subQ._toQuery());
      }
      // User matches and no more rules -> User can see everything
      else if (userMatch) {
        return null;
      }
    }

    BoolQuery bq = bqb.build();
    if (bq.should() != null && !bq.should().isEmpty()) {
      return bq._toQuery();
    }

    throw new AuthorizationException("This search requires a login");
  }

  /*
  private QueryBuilder getAaFilterQuery(String serviceName, Object... objects)
      throws AuthorizationException, IngeApplicationException, IngeTechnicalException, AuthenticationException {
  
    Map<String, Map<String, Object>> serviceMap = (Map<String, Map<String, Object>>) aaMap.get(serviceName);
  
    List<String> order = (List<String>) serviceMap.get("technical").get("order");
    Map<String, String> indices = (Map<String, String>) serviceMap.get("technical").get("indices");
    List<Map<String, Object>> allowedMap = (List<Map<String, Object>>) serviceMap.get("get");
  
    AccountUserDbVO userAccount;
    try {
      userAccount = ((Principal) objects[order.indexOf("user")]).getUserAccount();
    } catch (NullPointerException e) {
      userAccount = null;
    }
  
    BoolQueryBuilder bqb = QueryBuilders.boolQuery();
    if (allowedMap == null) {
      throw new AuthorizationException("No rules for service " + serviceName + ", method " + "get");
    }
  
    // everybody can see anything
    if (allowedMap.isEmpty()) {
      return null;
    }
  
    for (Map<String, Object> rules : allowedMap) {
  
      BoolQueryBuilder subQb = QueryBuilders.boolQuery();
      boolean userMatch = false;
  
      // Everybody is allowed to see everything
      rulesLoop: for (Entry<String, Object> rule : rules.entrySet()) {
        switch (rule.getKey()) {
          case "user": {
            if (userAccount != null) {
              Map<String, String> userMap = (Map<String, String>) rule.getValue();
  
              if (userMap.containsKey("field_user_id_match")) {
                String value = (String) userMap.get("field_user_id_match");
                subQb.must(QueryBuilders.termQuery(indices.get(value), userAccount.getObjectId()));
                userMatch = true;
              }
  
              if (userMap.containsKey("role") || userMap.containsKey("field_grant_id_match")) {
                BoolQueryBuilder grantQueryBuilder = QueryBuilders.boolQuery();
                for (GrantVO grant : userAccount.getGrantList()) {
                  if (grant.getRole().equalsIgnoreCase((String) userMap.get("role"))) {
                    userMatch = true;
                    if (userMap.get("field_grant_id_match") != null) {
                      if (grant.getObjectRef() != null && grant.getObjectRef().startsWith("ou_")) {
                        List<String> ouIds = new ArrayList<>();
                        ouIds.add(grant.getObjectRef());
                        List<AffiliationDbVO> childList = new ArrayList<>();
                        searchAllChildOrganizations(ouIds.get(0), childList);
                        grantQueryBuilder.should(QueryBuilders.termsQuery(indices.get(userMap.get("field_grant_id_match")), ouIds));
                      } else {
                        grantQueryBuilder
                            .should(QueryBuilders.termsQuery(indices.get(userMap.get("field_grant_id_match")), grant.getObjectRef()));
                      }
                    }
                  }
                }
  
                if (grantQueryBuilder.hasClauses()) {
                  subQb.must(grantQueryBuilder);
                }
              }
  
              if (userMap.containsKey("field_ou_id_match")) {
                String userOuId = userAccount.getAffiliation().getObjectId();
                List<String> ouIds = new ArrayList<>();
                ouIds.add(userOuId);
                List<AffiliationDbVO> childList = new ArrayList<>();
                searchAllChildOrganizations(ouIds.get(0), childList);
                subQb.must(QueryBuilders.termsQuery(UserAccountServiceImpl.INDEX_AFFIlIATION_OBJECTID, ouIds));
              }
  
            }
            if (!userMatch) {
              //reset queryBuilder
              subQb = QueryBuilders.boolQuery();
              break rulesLoop;
            }
            break;
          }
          default: {
            String key = rule.getKey();
            String index = indices.get(key);
  
            if (index == null) {
              throw new AuthorizationException("No index in aa.json defined for: " + key);
            }
  
            if (rule.getValue() instanceof Collection<?>) {
              List<String> valuesToCompare = (List<String>) rule.getValue();
              if (valuesToCompare.size() > 1) {
                BoolQueryBuilder valueQueryBuilder = QueryBuilders.boolQuery();
                for (String val : valuesToCompare) {
                  valueQueryBuilder.should(QueryBuilders.termQuery(index, val));
                }
                subQb.must(valueQueryBuilder);
              } else {
                subQb.must(QueryBuilders.termQuery(index, valuesToCompare.get(0)));
              }
            } else {
              Object value = getFieldValueOrString(order, objects, (String) rule.getValue());
              if (value != null) {
                subQb.must(QueryBuilders.termQuery(index, value.toString()));
              }
            }
            break;
          }
        }
      }
  
      if (subQb.hasClauses()) {
        bqb.should(subQb);
      }
      // User matches and no more rules -> User can see everything
      else if (userMatch) {
        return null;
      }
    }
  
    if (bqb.hasClauses()) {
      return bqb;
    }
  
    throw new AuthorizationException("This search requires a login");
  }
  */

  public Principal checkLoginRequired(String authenticationToken)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException {
    return new Principal(userAccountService.get(authenticationToken), authenticationToken);
  }

  public void checkAuthorization(String serviceName, String methodName, Object... objects)
      throws AuthorizationException, AuthenticationException, IngeTechnicalException, IngeApplicationException {
    Map<String, Map<String, Object>> serviceMap = (Map<String, Map<String, Object>>) aaMap.get(serviceName);
    if (serviceMap == null) {
      throw new AuthorizationException("No rules for service " + serviceName);
    }
    List<String> order = (List<String>) serviceMap.get("technical").get("order");
    List<Map<String, Object>> allowedMap = (List<Map<String, Object>>) serviceMap.get(methodName);

    if (allowedMap == null) {
      throw new AuthorizationException("No rules for service " + serviceName + ", method " + methodName);
    } else {
      Exception lastExceptionOfAll = null;
      for (Map<String, Object> rules : allowedMap) {
        Exception lastExceptionOfRule = null;
        for (Entry<String, Object> rule : rules.entrySet()) {
          try {
            switch (rule.getKey()) {
              case "user": {
                checkUser((Map<String, Object>) rule.getValue(), order, objects);
                break;
              }
              default: {
                String key = rule.getKey();
                Object keyValueObject = getFieldValueOrString(order, objects, key);
                String keyValue = keyValueObject != null ? keyValueObject.toString() : null;
                boolean check = false;
                if (rule.getValue() instanceof Collection<?>) {
                  List<String> valuesToCompare = (List<String>) rule.getValue();
                  check = valuesToCompare.stream().anyMatch(val -> keyValue != null && val != null && val.equalsIgnoreCase(keyValue));
                  if (!check) {
                    throw new AuthorizationException("Expected one of " + valuesToCompare + " for field " + key + " (" + keyValue + ")");
                  }
                } else {
                  Object val = getFieldValueOrString(order, objects, (String) rule.getValue().toString());
                  String value = null;
                  if (val != null) {
                    value = val.toString();
                  }
                  check = (keyValue != null && keyValue.equalsIgnoreCase(value));
                  if (!check) {
                    throw new AuthorizationException("Expected value [" + value + "] for field " + key + " (" + keyValue + ")");
                  }
                }
                break;
              }
            }
          } catch (AuthorizationException | AuthenticationException e) {
            lastExceptionOfRule = e;
            lastExceptionOfAll = e;
            break;
          }
        }
        if (lastExceptionOfRule == null) {
          return;
        }
      }

      if (lastExceptionOfAll == null) {
        return;
      } else {
        if (lastExceptionOfAll instanceof AuthorizationException)
          throw (AuthorizationException) lastExceptionOfAll;
        else if (lastExceptionOfAll instanceof AuthenticationException) {
          throw (AuthenticationException) lastExceptionOfAll;
        }
      }
    }
  }

  private void checkUser(Map<String, Object> ruleMap, List<String> order, Object[] objects)
      throws AuthorizationException, AuthenticationException, IngeTechnicalException, IngeApplicationException {
    Principal principal = (Principal) objects[order.indexOf("user")];
    if (principal == null) {
      throw new AuthenticationException("You have to be logged in with username/password or ip address.");
    }
    AccountUserDbVO userAccount = principal.getUserAccount();
    String ipMatch = (String) ruleMap.get("ip_match");
    if (ipMatch != null) {
      DecodedJWT decodedJwt = userAccountService.verifyToken(principal.getJwToken());
      if (decodedJwt.getHeaderClaim("ip") != null) {
        try {
          Collection<String> ouIdsToBeMatched = new ArrayList<>();
          Object ouIdToBeMatched = getFieldValueOrString(order, objects, ipMatch);
          if (ouIdToBeMatched instanceof String) {
            ouIdsToBeMatched.add(ouIdToBeMatched.toString());
          } else if (ouIdToBeMatched instanceof Collection) {
            ouIdsToBeMatched = (Collection<String>) ouIdToBeMatched;
          }
          String userIp = decodedJwt.getHeaderClaim("ip").asString();
          boolean check = false;
          for (String ouId : ouIdsToBeMatched) {
            IpRange ouIpRange = ipListProvider.get(ouId);
            if (ouIpRange.matches(userIp)) {
              check = true;
              break;
            }
          }
          if (!check) {
            throw new AuthenticationException(
                "The current user's ip adress " + userIp + " does not match required ip range of organization with id " + ouIdToBeMatched);
          }
        } catch (Exception e) {
          throw new AuthenticationException("Error while matching IPs", e);
        }
      } else {
        throw new AuthenticationException("Token contains no IP, but IP match is required");
      }
    } else if (userAccount == null) {
      throw new AuthenticationException("You have to be logged in with username/password.");
    }

    String userIdFieldMatch = (String) ruleMap.get("field_user_id_match");
    if (userIdFieldMatch != null) {
      Object userId = getFieldValueOrString(order, objects, userIdFieldMatch);
      String expectedUserId = (userId != null ? userId.toString() : null);
      if (expectedUserId == null || !expectedUserId.equals(userAccount.getObjectId())) {
        throw new AuthorizationException("User is not owner of object.");
      }
    }

    if (ruleMap.containsKey("role") || ruleMap.containsKey("field_grant_id_match")) {
      boolean check = false;
      String role = (String) ruleMap.get("role");
      String grantFieldMatch = (String) ruleMap.get("field_grant_id_match");
      String grantFieldMatchValue = null;
      List<String> grantFieldMatchValues = new ArrayList<>();
      if (grantFieldMatch != null) {
        Object val = getFieldValueOrString(order, objects, grantFieldMatch);
        if (val != null) {
          grantFieldMatchValue = val.toString();
          if (!val.toString().startsWith("ou")) {
            grantFieldMatchValues.add(grantFieldMatchValue);
          }
        } else {
          logger.warn("getFieldValue for " + grantFieldMatch + "returned null!");
        }
      }

      if (grantFieldMatch != null && grantFieldMatchValue != null && grantFieldMatchValue.startsWith("ou")) {
        // If grant is of type "ORGANIZATION", get all parents of organization up to firstLevel as potential matches
        List<String> parents = ouService.getIdPath(grantFieldMatchValue); // enthält auch eigene Ou
        parents.remove(parents.size() - 1); // remove root
        grantFieldMatchValues.addAll(parents);
      }

      for (GrantVO grant : userAccount.getGrantList()) {
        check = (role == null || role.equals(grant.getRole())) && (grantFieldMatch == null
            || (grant.getObjectRef() != null && grantFieldMatchValues.stream().anyMatch(id -> id.equals(grant.getObjectRef()))));
        if (check) {
          break;
        }
      }

      if (!check) {
        throw new AuthorizationException(
            "Expected user with role [" + role + "], on object [" + grantFieldMatchValues + "] (" + grantFieldMatch + ")");
      }
    }

    // Ein angemeldeter Benutzer mit einem Kontext für eine Ou O darf nur dann einen vorgegebenen Benutzer sehen, wenn der Benutzer zur selben Ou O gehört.
    if (ruleMap.containsKey("role") && ruleMap.containsKey("field_ctx_ou_id_match")) {
      boolean check = false;
      String role = (String) ruleMap.get("role");
      String ctxOuFieldMatch = (String) ruleMap.get("field_ctx_ou_id_match");
      Object val = getFieldValueOrString(order, objects, ctxOuFieldMatch);
      if (val == null) {
        throw new AuthorizationException("getFieldValue for " + ctxOuFieldMatch + " returned null!");
      }

      String ctxOuFieldMatchValue = val.toString(); // Ou des vorgegebenen Benutzers
      if (!ctxOuFieldMatchValue.startsWith("ou")) {
        throw new AuthorizationException("ctxOuFieldMatchValue " + ctxOuFieldMatch + " does not start with ou!");
      }

      for (GrantVO grant : userAccount.getGrantList()) {
        if (grant.getObjectRef() != null && grant.getObjectRef().startsWith("ctx")) {
          ContextDbVO ctx = ctxService.get(grant.getObjectRef(), null);
          if (ctx == null) {
            throw new AuthorizationException("context for " + ctxOuFieldMatchValue + " returned null!");
          }
          if (ctx.getResponsibleAffiliations().isEmpty()) {
            throw new AuthorizationException("context " + ctx.getObjectId() + " has no affiliations!");
          }
          String ouId = ctx.getResponsibleAffiliations().get(0).getObjectId(); // Ou des Kontextes
          check = role.equals(grant.getRole()) && ctxOuFieldMatchValue.equals(ouId);
          if (check) {
            break;
          }
        }
      }

      if (!check) {
        throw new AuthorizationException(
            "Expected user with role [" + role + "], on object [" + ctxOuFieldMatchValue + "] (" + ctxOuFieldMatch + ")");
      }
    }
  }

  /*
  private void checkUser(Map<String, Object> ruleMap, List<String> order, Object[] objects)
      throws AuthorizationException, AuthenticationException, IngeTechnicalException, IngeApplicationException {
    Principal principal = (Principal) objects[order.indexOf("user")];
    if (principal == null) {
      throw new AuthenticationException("You have to be logged in with username/password or ip address.");
    }
    AccountUserDbVO userAccount = principal.getUserAccount();
    String ipMatch = (String) ruleMap.get("ip_match");
    if (ipMatch != null) {
      DecodedJWT decodedJwt = userAccountService.verifyToken(principal.getJwToken());
      if (decodedJwt.getHeaderClaim("ip") != null) {
        try {
          Collection<String> ouIdsToBeMatched = new ArrayList<>();
          Object ouIdToBeMatched = getFieldValueOrString(order, objects, ipMatch);
          if (ouIdToBeMatched instanceof String) {
            ouIdsToBeMatched.add(ouIdToBeMatched.toString());
          } else if (ouIdToBeMatched instanceof Collection) {
            ouIdsToBeMatched = (Collection<String>) ouIdToBeMatched;
          }
          String userIp = decodedJwt.getHeaderClaim("ip").asString();
          boolean check = false;
          for (String ouId : ouIdsToBeMatched) {
            IpRange ouIpRange = ipListProvider.get(ouId);
            if (ouIpRange.matches(userIp)) {
              check = true;
              break;
            }
          }
          if (!check) {
            throw new AuthenticationException(
                "The current user's ip adress " + userIp + " does not match required ip range of organization with id " + ouIdToBeMatched);
          }
        } catch (Exception e) {
          throw new AuthenticationException("Error while matching IPs", e);
        }
      } else {
        throw new AuthenticationException("Token contains no IP, but IP match is required");
      }
    } else if (userAccount == null) {
      throw new AuthenticationException("You have to be logged in with username/password.");
    }
  
    String userIdFieldMatch = (String) ruleMap.get("field_user_id_match");
    if (userIdFieldMatch != null) {
      Object userId = getFieldValueOrString(order, objects, userIdFieldMatch);
      String expectedUserId = (userId != null ? userId.toString() : null);
      if (expectedUserId == null || !expectedUserId.equals(userAccount.getObjectId())) {
        throw new AuthorizationException("User is not owner of object.");
      }
    }
  
    if (ruleMap.containsKey("role") || ruleMap.containsKey("field_grant_id_match")) {
      boolean check = false;
      String role = (String) ruleMap.get("role");
      String grantFieldMatch = (String) ruleMap.get("field_grant_id_match");
      List<String> grantFieldMatchValues = new ArrayList<>();
      if (grantFieldMatch != null) {
        Object val = getFieldValueOrString(order, objects, grantFieldMatch);
        if (val != null) {
          grantFieldMatchValues.add(val.toString());
        } else {
          logger.warn("getFieldValue for " + grantFieldMatch + "returned null!");
        }
      }
  
      // If grant is of type "ORGANIZATION", get all children of organization as potential matches
      if (grantFieldMatch != null && (!grantFieldMatchValues.isEmpty()) && grantFieldMatchValues.get(0).startsWith("ou")) {
        List<AffiliationDbVO> childList = new ArrayList<>();
        searchAllChildOrganizations(grantFieldMatchValues.get(0), childList);
        grantFieldMatchValues.addAll(childList.stream().map(aff -> aff.getObjectId()).collect(Collectors.toList()));
      }
      for (GrantVO grant : userAccount.getGrantList()) {
        check = (role == null || role.equals(grant.getRole())) && (grantFieldMatch == null
            || (grant.getObjectRef() != null && grantFieldMatchValues.stream().anyMatch(id -> id.equals(grant.getObjectRef()))));
        if (check) {
          break;
        }
      }
      if (!check) {
        throw new AuthorizationException(
            "Expected user with role [" + role + "], on object [" + grantFieldMatchValues + "] (" + grantFieldMatch + ")");
      }
    }
  
    if (ruleMap.containsKey("field_ou_id_match")) {
      String userOuId = principal.getUserAccount().getAffiliation().getObjectId();
      String ouFieldMatch = (String) ruleMap.get("field_ou_id_match");
      if (ouFieldMatch != null) {
        Object val = getFieldValueOrString(order, objects, ouFieldMatch);
        if (val == null) {
          throw new AuthorizationException(
              "User with ou [" + userOuId + "] is not allowed to access object with field " + ouFieldMatch + "[" + val + "]");
        } else {
          List<String> ouIds = new ArrayList<>();
          ouIds.add(userOuId);
          List<AffiliationDbVO> childList = new ArrayList<>();
          searchAllChildOrganizations(ouIds.get(0), childList);
          if (!ouIds.contains(val.toString())) {
            throw new AuthorizationException(
                "User with ou [" + userOuId + "] is not allowed to access object with field " + ouFieldMatch + "[" + val + "]");
          }
        }
      }
    }
  }
  */

  /*
  private void searchAllChildOrganizations(String parentAffiliationId, List<AffiliationDbVO> completeList)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    List<AffiliationDbVO> children = ouService.searchChildOrganizations(parentAffiliationId);
    if (children != null) {
      for (AffiliationDbVO child : children) {
        completeList.add(child);
        searchAllChildOrganizations(child.getObjectId(), completeList);
      }
    }
  }
  */

  private Object getFieldValueOrString(List<String> order, Object[] objects, String field) throws AuthorizationException {
    if (field.contains(".")) {
      String[] fieldHierarchy = field.split("\\.");
      Object object;
      try {
        object = objects[order.indexOf(fieldHierarchy[0])];
      } catch (NullPointerException e) {
        return null;
      }
      if (object == null) {
        return null;
        // hart codiert, da getFieldValueViaGetter nicht mit arrays funktioniert
      } else if (object.getClass().equals(ContextDbVO.class) && fieldHierarchy.length == 2
          && fieldHierarchy[1].equals("responsibleAffiliations")) {
        ContextDbVO ro = (ContextDbVO) object;
        return ro.getResponsibleAffiliations().get(0).getObjectId();
      } else {
        return getFieldValueViaGetter(object, field.substring(field.indexOf(".") + 1, field.length()));
      }
    } else {
      return field;
    }
  }

  private Object getFieldValueViaGetter(Object object, String field) throws AuthorizationException {
    try {
      String[] fieldHierarchy = field.split("\\.");
      for (PropertyDescriptor pd : Introspector.getBeanInfo(object.getClass()).getPropertyDescriptors()) {
        if (pd.getName().equals(fieldHierarchy[0])) {
          Object value = pd.getReadMethod().invoke(object);
          if (value == null) {
            return null;
          }
          if (fieldHierarchy.length == 1) {
            return value;
          } else {
            return getFieldValueViaGetter(value, field.substring(field.indexOf(".") + 1, field.length()));
          }
        }
      }
    } catch (Exception e) {
      throw new AuthorizationException("Error while calling getter in object", e);
    }

    return null;
  }
}
