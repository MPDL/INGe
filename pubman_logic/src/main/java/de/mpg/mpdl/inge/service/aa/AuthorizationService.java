package de.mpg.mpdl.inge.service.aa;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.json.util.JsonObjectMapperFactory;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.OrganizationService;
import de.mpg.mpdl.inge.service.pubman.UserAccountService;
import de.mpg.mpdl.inge.util.ResourceUtil;


@Service
public class AuthorizationService {

  private Map<String, Object> aaMap;

  @Autowired
  private UserAccountService userAccountService;

  ObjectMapper modelMapper = JsonObjectMapperFactory.getObjectMapper();

  @Autowired
  OrganizationService ouService;

  public AuthorizationService() {

    try {
      aaMap =
          modelMapper.readValue(
              ResourceUtil.getResourceAsStream("aa.json",
                  AuthorizationService.class.getClassLoader()), Map.class);
    } catch (Exception e) {
      throw new RuntimeException("Problem with parsing aa.json file.", e);
    }
  }

  public QueryBuilder modifyQueryForAa(String serviceName, QueryBuilder query, Object... objects)
      throws AuthenticationException, AuthorizationException {

    QueryBuilder filterQuery = getAaFilterQuery(serviceName, objects);

    if (filterQuery != null) {
      BoolQueryBuilder completeQuery = QueryBuilders.boolQuery();
      if (query != null) {
        completeQuery.must(query);
      }
      completeQuery.filter(filterQuery);
      return completeQuery;
    }

    return query;

  }


  private QueryBuilder getAaFilterQuery(String serviceName, Object... objects)
      throws AuthorizationException {
    Map<String, Map<String, Object>> serviceMap =
        (Map<String, Map<String, Object>>) aaMap.get(serviceName);

    List<String> order = (List<String>) serviceMap.get("technical").get("order");
    Map<String, String> indices = (Map<String, String>) serviceMap.get("technical").get("indices");
    List<Map<String, Object>> allowedMap = (List<Map<String, Object>>) serviceMap.get("get");


    AccountUserVO userAccount;
    try {
      userAccount = (AccountUserVO) objects[order.indexOf("user")];
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

                subQb.must(QueryBuilders.termQuery(indices.get(value), userAccount.getReference()
                    .getObjectId()));
                userMatch = true;

              }

              if (userMap.containsKey("role") || userMap.containsKey("field_grant_id_match")) {


                BoolQueryBuilder grantQueryBuilder = QueryBuilders.boolQuery();
                for (GrantVO grant : userAccount.getGrants()) {
                  if (grant.getRole().equalsIgnoreCase((String) userMap.get("role"))) {
                    userMatch = true;
                    if (userMap.get("field_grant_id_match") != null) {
                      grantQueryBuilder.should(QueryBuilders.termQuery(
                          indices.get(userMap.get("field_grant_id_match")), grant.getObjectRef()));
                    }

                  }
                }

                if (grantQueryBuilder.hasClauses()) {
                  subQb.must(grantQueryBuilder);
                }

              }

            }
            if (!userMatch) {
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
              String value = getFieldValueOrString(order, objects, (String) rule.getValue());
              if (value != null) {
                subQb.must(QueryBuilders.termQuery(index, value));
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



  public AccountUserVO checkLoginRequired(String authenticationToken)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException,
      AuthorizationException {
    return userAccountService.get(authenticationToken);
  }



  public void checkAuthorization(String serviceName, String methodName, Object... objects)
      throws AuthorizationException, AuthenticationException, IngeTechnicalException, IngeApplicationException {

    Map<String, Map<String, Object>> serviceMap =
        (Map<String, Map<String, Object>>) aaMap.get(serviceName);
    if(serviceMap==null)
    {
      throw new AuthorizationException("Nor rules for service " + serviceName);
    }
    List<String> order = (List<String>) serviceMap.get("technical").get("order");
    List<Map<String, Object>> allowedMap = (List<Map<String, Object>>) serviceMap.get(methodName);

    if (allowedMap == null) {
      throw new AuthorizationException("No rules for service " + serviceName + ", method " + methodName);
    }

    else {
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
                String keyValue = getFieldValueOrString(order, objects, key);
                boolean check = false;
                if (rule.getValue() instanceof Collection<?>) {
                  List<String> valuesToCompare = (List<String>) rule.getValue();
                  check = valuesToCompare.stream().anyMatch(
                      val -> keyValue != null && val != null && val.equalsIgnoreCase(keyValue));
                  if (!check) {
                    throw new AuthorizationException("Expected one of " + valuesToCompare + " for field " + key
                        + " (" + keyValue + ")");
                  }
                } else {
                  String value = getFieldValueOrString(order, objects, (String) rule.getValue());
                  check = (keyValue != null && keyValue.equalsIgnoreCase(value));
                  if (!check) {
                    throw new AuthorizationException(
                        "Expected value [" + value + "] for field " + key + " (" + keyValue + ")");
                  }
                }


                break;
              }


            }
          } catch (AuthorizationException|AuthenticationException e ) {
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
        if(lastExceptionOfAll instanceof AuthorizationException)
          throw (AuthorizationException)lastExceptionOfAll;
        else if (lastExceptionOfAll instanceof AuthenticationException)
        {
          throw (AuthenticationException) lastExceptionOfAll;
        }
      }


    }

  }

  private void checkUser(Map<String, Object> ruleMap, List<String> order, Object[] objects)
      throws AuthorizationException, AuthenticationException, IngeTechnicalException, IngeApplicationException {

    AccountUserVO userAccount = (AccountUserVO) objects[order.indexOf("user")];

    if (userAccount == null) {
      throw new AuthenticationException("You have to be logged in.");
    }

    String userIdFieldMatch = (String) ruleMap.get("field_user_id_match");

    if (userIdFieldMatch != null) {
      String expectedUserId = getFieldValueOrString(order, objects, userIdFieldMatch);

      if (expectedUserId == null
          || !expectedUserId.equals(userAccount.getReference().getObjectId())) {
        throw new AuthorizationException("User is not owner of object.");
      }
    }


    if (ruleMap.containsKey("role")
        || ruleMap.containsKey("field_grant_id_match")) {
      boolean check = false;
      String role = (String) ruleMap.get("role");
     
      String grantFieldMatch = (String) ruleMap.get("field_grant_id_match");

      List<String> grantFieldMatchValues = new ArrayList<>();
      if (grantFieldMatch != null) {
        grantFieldMatchValues.add(getFieldValueOrString(order, objects, grantFieldMatch));
      }


      // If grant is of type "ORGANIZATION", get all children of organization as potential matches
      if (grantFieldMatch!=null && grantFieldMatchValues.get(0).startsWith("ou")) {
        List<AffiliationVO> childList = new ArrayList<>();
        searchAllChildOrganizations(grantFieldMatchValues.get(0), childList);
        grantFieldMatchValues.addAll(childList.stream().map(aff -> aff.getReference().getObjectId()).collect(Collectors.toList()));

      }


      for (GrantVO grant : userAccount.getGrants()) {
        check = (role == null || role.equals(grant.getRole()))
            && (grantFieldMatch == null || (grant.getObjectRef() != null
                && grantFieldMatchValues.stream().anyMatch(id -> id.equals(grant.getObjectRef()))));

        if (check) {
          return;
        }
      }

      if (!check) {
        throw new AuthorizationException("Expected user with role [" + role + "], on object [" + (grantFieldMatch!=null ? grantFieldMatchValues.get(0) : null) + "]");
      }

    }


  }

  private void searchAllChildOrganizations(String parentAffiliationId,
      List<AffiliationVO> completeList) throws IngeTechnicalException, AuthenticationException,
      AuthorizationException, IngeApplicationException {


    List<AffiliationVO> children = ouService.searchChildOrganizations(parentAffiliationId);
    if (children != null) {
      for (AffiliationVO child : children) {
        completeList.add(child);
        searchAllChildOrganizations(child.getReference().getObjectId(), completeList);
      }
    }
  }

  private String getFieldValueOrString(List<String> order, Object[] objects, String field)
      throws AuthorizationException {
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
      } else {
        return getFieldValueViaGetter(object,
            field.substring(field.indexOf(".") + 1, field.length()));
      }



    } else {
      return field;
    }
  }

  private String getFieldValueViaGetter(Object object, String field) throws AuthorizationException {
    try {
      String[] fieldHierarchy = field.split("\\.");

      for (PropertyDescriptor pd : Introspector.getBeanInfo(object.getClass())
          .getPropertyDescriptors()) {

        if (pd.getName().equals(fieldHierarchy[0])) {
          Object value = pd.getReadMethod().invoke(object);
          if (value == null) {
            return null;
          }

          if (fieldHierarchy.length == 1) {
            return value.toString();

          } else {
            return getFieldValueViaGetter(value,
                field.substring(field.indexOf(".") + 1, field.length()));
          }
        }

      }


    } catch (Exception e) {
      throw new AuthorizationException("Error while calling getter in object", e);
    }
    return null;

  }


}
