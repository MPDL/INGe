package de.mpg.mpdl.inge.service.aa;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.mpg.mpdl.inge.es.connector.ModelMapper;
import de.mpg.mpdl.inge.model.exception.IngeServiceException;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.service.exceptions.AaException;
import de.mpg.mpdl.inge.service.pubman.UserAccountService;
import de.mpg.mpdl.inge.util.ResourceUtil;


@Service
public class AuthorizationService {


  private Map<String, Object> aaMap;

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

  public QueryBuilder modifyQueryForAa(String serviceName, QueryBuilder query,
      AccountUserVO userAccount) throws AaException {

    QueryBuilder filterQuery = getAaFilterQuery(serviceName, userAccount);

    if (filterQuery != null) {
      BoolQueryBuilder completeQuery = QueryBuilders.boolQuery();
      completeQuery.must(query);
      completeQuery.filter(filterQuery);
      return completeQuery;
    }

    return query;

  }


  private QueryBuilder getAaFilterQuery(String serviceName, AccountUserVO userAccount) {

    Map<String, Map<String, Object>> serviceMap =
        (Map<String, Map<String, Object>>) aaMap.get(serviceName);
    List<String> order = (List<String>) serviceMap.get("technical").get("order");
    Map<String, String> indices = (Map<String, String>) serviceMap.get("technical").get("indices");
    List<Map<String, Object>> allowedMap = (List<Map<String, Object>>) serviceMap.get("get");


    BoolQueryBuilder bqb = QueryBuilders.boolQuery();
    if (allowedMap != null) {

      rulesLoop: for (Map<String, Object> rules : allowedMap) {

        BoolQueryBuilder subQb = QueryBuilders.boolQuery();

        for (Entry<String, Object> rule : rules.entrySet()) {
          switch (rule.getKey()) {
            case "user": {
              boolean userMatch = false;

              if (userAccount != null) {

                Map<String, String> userMap = (Map<String, String>) rule.getValue();

                if (userMap.containsKey("field_user_id_match")) {
                  String value = (String) userMap.get("field_user_id_match");

                  subQb.must(QueryBuilders.termQuery(indices.get(value), userAccount.getReference()
                      .getObjectId()));
                  userMatch = true;

                }

                if (userMap.containsKey("role") || userMap.containsKey("grant_type")
                    || userMap.containsKey("field_grant_id_match")) {


                  BoolQueryBuilder grantQueryBuilder = QueryBuilders.boolQuery();
                  for (GrantVO grant : userAccount.getGrants()) {
                    if (grant.getRole().equalsIgnoreCase((String) userMap.get("role"))
                        && (userMap.get("grant_type") == null || userMap.get("grant_type")
                            .equalsIgnoreCase(grant.getGrantType()))) {
                      userMatch = true;
                      if (userMap.get("field_grant_id_match") != null) {
                        grantQueryBuilder
                            .should(QueryBuilders.termQuery(
                                indices.get(userMap.get("field_grant_id_match")),
                                grant.getObjectRef()));
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
                String value = (String) rule.getValue();
                subQb.must(QueryBuilders.termQuery(index, value));
              }


              break;
            }
          }
        }

        if (subQb.hasClauses()) {
          bqb.should(subQb);
        } else {
          // Allowed to see everything
          return null;
        }

      }

    }
    if (bqb.hasClauses()) {
      return bqb;
    }
    return null;
  }



  public AccountUserVO checkLoginRequired(String authenticationToken) throws AaException {

    try {
      return userAccountService.get(authenticationToken);
    } catch (IngeServiceException e) {
      throw new AaException("You have to be logged in with a valid token", e);
    }
  }



  public void checkAuthorization(String serviceName, String methodName, Object... objects)
      throws AaException {

    Map<String, Map<String, Object>> serviceMap =
        (Map<String, Map<String, Object>>) aaMap.get(serviceName);
    List<String> order = (List<String>) serviceMap.get("technical").get("order");
    List<Map<String, Object>> allowedMap = (List<Map<String, Object>>) serviceMap.get(methodName);

    if(allowedMap==null)
    {
      throw new AaException("No rules for service " + serviceName + ", method " + methodName);
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
                    throw new AaException("Expected one of " + valuesToCompare + " for field " + key
                        + " (" + keyValue + ")");
                  }
                } else {
                  String value = getFieldValueOrString(order, objects, (String) rule.getValue());
                  check = (keyValue != null && keyValue.equalsIgnoreCase(value));
                  if (!check) {
                    throw new AaException(
                        "Expected value [" + value + "] for field " + key + " (" + keyValue + ")");
                  }
                }


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
      if(lastExceptionOfAll==null)
      {
        return;
      }
      else
      {
      throw new AaException(lastExceptionOfAll);
      }


    }

  }

  private void checkUser(Map<String, Object> ruleMap, List<String> order, Object[] objects)
      throws AaException {

    AccountUserVO userAccount = (AccountUserVO) objects[order.indexOf("user")];

    if (userAccount == null) {
      throw new AaException("You have to be logged in.");
    }

    String userIdFieldMatch = (String) ruleMap.get("field_user_id_match");

    if (userIdFieldMatch != null) {
      String expectedUserId = getFieldValueOrString(order, objects, userIdFieldMatch);

      if (expectedUserId == null
          || !expectedUserId.equals(userAccount.getReference().getObjectId())) {
        throw new AaException("User is not owner of object.");
      }
    }


    if (ruleMap.containsKey("role") || ruleMap.containsKey("grant_type")
        || ruleMap.containsKey("field_grant_id_match")) {
      boolean check = false;
      String role = (String) ruleMap.get("role");
      String grantType = (String) ruleMap.get("grant_type");
      String grantFieldMatch = (String) ruleMap.get("field_grant_id_match");



      for (GrantVO grant : userAccount.getGrants()) {
        check =
            (role == null || role.equals(grant.getRole()))
                && (grantType == null || grantType.equals(grant.getGrantType()))
                && (grant.getObjectRef() == null || grant.getObjectRef().equals(
                    getFieldValueOrString(order, objects, grantFieldMatch)));

        if (check) {
          return;
        }
      }

      if (!check) {
        throw new AaException("Expected user with role [" + role + "], grant-type [" + grantType
            + "] on object [" + grantFieldMatch + "]");
      }

    }


  }


  private String getFieldValueOrString(List<String> order, Object[] objects, String field)
      throws AaException {
    if (field.contains(".")) {
      String[] fieldHierarchy = field.split("\\.");
      Object object = objects[order.indexOf(fieldHierarchy[0])];
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

  private String getFieldValueViaGetter(Object object, String field) throws AaException {
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
      throw new AaException("Error while calling getter in object", e);
    }
    return null;

  }


}
