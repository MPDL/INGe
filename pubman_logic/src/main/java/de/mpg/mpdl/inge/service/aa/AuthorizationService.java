package de.mpg.mpdl.inge.service.aa;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQuery;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.exception.PubManException;
import de.mpg.mpdl.inge.model.util.MapperFactory;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.ContextService;
import de.mpg.mpdl.inge.service.pubman.OrganizationService;
import de.mpg.mpdl.inge.service.pubman.UserAccountService;
import de.mpg.mpdl.inge.util.ResourceUtil;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


/**
 * Evaluates the declarative authorization rules in {@code aa.json}.
 *
 * <p>
 * Rules are grouped by service class and method name. The {@code technical.order} array maps the
 * object names used in a rule to the positional objects supplied by the service. Each method
 * contains alternative rule sets: every condition in one rule set must match, while matching any
 * rule set authorizes the request.
 * </p>
 *
 * <p>
 * The service has two execution paths:
 * </p>
 * <ul>
 * <li>{@link #checkAuthorization(String, String, Object...)} authorizes an individual service
 * invocation against its runtime objects.</li>
 * <li>{@link #modifyQueryForAa(String, Query, Object...)} adds an Elasticsearch filter for
 * {@code get} rules, so searches return only authorized objects.</li>
 * </ul>
 *
 * <p>
 * User rules can require a role, ownership, an IP range, or a grant-object match. A scalar
 * {@code field_grant_id_match} preserves the legacy single-field behavior. A list of field names is
 * supported for invocation authorization and requires all listed fields to match the same grant.
 * List-valued grant matches are not supported by the search-filter path.
 * </p>
 */
@Service
public class AuthorizationService {

  private static final Logger logger = LogManager.getLogger(AuthorizationService.class);

  private final Map<String, Object> aaMap;

  @Autowired
  private UserAccountService userAccountService;

  final ObjectMapper modelMapper = MapperFactory.getObjectMapper();

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
    REVISE("revise"),
    ADD_NEW_DOI("addNewDoi");

  private String methodName;

  AccessType(String methodName) {
      this.methodName = methodName;
    }

  public String getMethodName() {
    return this.methodName;
  }

  public void setMethodName(String methodName) {
    this.methodName = methodName;
  }

  }

  public AuthorizationService() {
    try {
      this.aaMap =
          this.modelMapper.readValue(ResourceUtil.getResourceAsStream("aa.json", AuthorizationService.class.getClassLoader()), Map.class);
    } catch (Exception e) {
      throw new RuntimeException("Problem with parsing aa.json file.", e);
    }
  }

  public AccountUserDbVO getUserAccountFromToken(String token) throws AuthenticationException, IngeApplicationException {
    Principal principal = checkLoginRequired(token);

    AccountUserDbVO accountUserDbVO = principal.getUserAccount();

    if (null == accountUserDbVO) {
      throw new IngeApplicationException("Invalid user", PubManException.Reason.PERMISSION_DENIED);
    }

    return accountUserDbVO;
  }

  public String getUserIpListIdFromToken(String token) throws AuthenticationException {
    Principal principal = checkLoginRequired(token);
    DecodedJWT decodedJwt = this.userAccountService.verifyToken(principal.getJwToken());
    String userIp = decodedJwt.getHeaderClaim("ip").asString();

    if (null != userIp) {
      IpListProvider.IpRange userIpRange = this.ipListProvider.getMatch(userIp);
      if (null != userIpRange) {
        return userIpRange.getId();
      }
    }

    return null;
  }

  /**
   * Adds the authorization filter derived from a service's {@code get} rules to a search query.
   *
   * <p>
   * The input query is not replaced: the generated authorization query is added as a filter. This
   * path translates scalar grant-object rules into Elasticsearch queries. It must remain in sync
   * with, but is distinct from, the runtime authorization path.
   * </p>
   */
  public Query modifyQueryForAa(String serviceName, Query query, Object... objects)
      throws AuthenticationException, AuthorizationException, IngeApplicationException, IngeTechnicalException {

    Query filterQuery = getAaFilterQuery(serviceName, objects);

    if (null != filterQuery) {
      BoolQuery.Builder completeQuery = new BoolQuery.Builder();
      //BoolQueryBuilder completeQuery = QueryBuilders.boolQuery();
      if (null != query) {
        completeQuery.must(query);
      }
      completeQuery.filter(filterQuery);
      return completeQuery.build()._toQuery();
    }

    return query;
  }

  /**
   * Builds an Elasticsearch filter from the alternatives declared for a service's {@code get}
   * operation. Each allowed rule set becomes a {@code should} clause; the conditions inside it are
   * combined with {@code must}.
   *
   * <p>
   * This implementation expects scalar {@code field_grant_id_match} values. Invocation-only rules
   * such as {@code addGrants} do not use this method.
   * </p>
   */
  private Query getAaFilterQuery(String serviceName, Object... objects)
      throws AuthorizationException, IngeApplicationException, IngeTechnicalException, AuthenticationException {

    Map<String, Map<String, Object>> serviceMap = (Map<String, Map<String, Object>>) this.aaMap.get(serviceName);

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
    if (null == allowedMap) {
      throw new AuthorizationException("No rules for service " + serviceName + ", method " + "get",
          PubManException.Reason.PERMISSION_DENIED);
    }

    // everybody can see anything
    if (allowedMap.isEmpty()) {
      return null;
    }

    for (Map<String, Object> rules : allowedMap) {

      BoolQuery.Builder subQb = new BoolQuery.Builder();
      boolean userMatch = false;

      // Everybody is allowed to see everything
      rulesLoop: for (Map.Entry<String, Object> rule : rules.entrySet()) {
        switch (rule.getKey()) {
          case "user": {
            if (null != userAccount) {
              Map<String, String> userMap = (Map<String, String>) rule.getValue();

              if (userMap.containsKey("field_user_id_match")) {
                String value = userMap.get("field_user_id_match");
                AccountUserDbVO finalUserAccount = userAccount;
                subQb.must(TermQuery.of(t -> t.field(indices.get(value)).value(finalUserAccount.getObjectId()))._toQuery());
                userMatch = true;
              }

              if (userMap.containsKey("role") || userMap.containsKey("field_grant_id_match")
                  || userMap.containsKey("field_ctx_ou_id_match")) {
                BoolQuery.Builder grantQueryBuilder = new BoolQuery.Builder();
                for (GrantVO grant : userAccount.getGrantList()) {
                  if (grant.getRole().equalsIgnoreCase(userMap.get("role"))) {
                    userMatch = true;
                    if (null != userMap.get("field_grant_id_match")) {
                      // If grant is of type "ORGANIZATION", get all parents of organization up to firstLevel as potential matches
                      if (null != grant.getObjectRef() && grant.getObjectRef().startsWith("ou")) {
                        List<String> parents = this.ouService.getIdPath(grant.getObjectRef()); // enthält auch eigene Ou
                        parents.remove(parents.size() - 1); // remove root
                        List<FieldValue> grantFieldMatchValues = parents.stream().map(FieldValue::of).collect(Collectors.toList());
                        grantQueryBuilder.should(TermsQuery
                            .of(t -> t.field(indices.get(userMap.get("field_grant_id_match"))).terms(te -> te.value(grantFieldMatchValues)))
                            ._toQuery());
                      } else {
                        grantQueryBuilder.should(TermQuery
                            .of(t -> t.field(indices.get(userMap.get("field_grant_id_match"))).value(grant.getObjectRef()))._toQuery());
                      }
                    } else if (null != userMap.get("field_ctx_ou_id_match")) {
                      if (null != grant.getObjectRef() && grant.getObjectRef().startsWith("ctx")) {
                        ContextDbVO ctx = this.ctxService.get(grant.getObjectRef(), null);
                        String ouId = ctx.getResponsibleAffiliations().get(0).getObjectId(); // Ou des Kontextes
                        grantQueryBuilder
                            .should(TermQuery.of(t -> t.field(indices.get(userMap.get("field_ctx_ou_id_match"))).value(ouId))._toQuery());
                      }
                    }
                  }
                }
                BoolQuery grantQuery = grantQueryBuilder.build();
                if (null != grantQuery.should() && !grantQuery.should().isEmpty()) {
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

            if (null == index) {
              throw new AuthorizationException("No index in aa.json defined for: " + key, PubManException.Reason.PERMISSION_DENIED);
            }

            if (rule.getValue() instanceof Collection<?>) {
              List<String> valuesToCompare = (List<String>) rule.getValue();
              if (1 < valuesToCompare.size()) {
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
              if (null != value) {
                subQb.must(TermQuery.of(t -> t.field(index).value(value.toString()))._toQuery());
              }
            }
            break;
          }
        }
      }

      BoolQuery subQ = subQb.build();
      if (null != subQ.must() && !subQ.must().isEmpty()) {
        bqb.should(subQ._toQuery());
      }
      // User matches and no more rules -> User can see everything
      else if (userMatch) {
        return null;
      }
    }

    BoolQuery bq = bqb.build();
    if (null != bq.should() && !bq.should().isEmpty()) {
      return bq._toQuery();
    }

    throw new AuthorizationException("This search requires a login", PubManException.Reason.PERMISSION_DENIED);
  }



  public Principal checkLoginRequired(String authenticationToken) throws AuthenticationException {
    return new Principal(this.userAccountService.get(authenticationToken), authenticationToken);
  }

  public Principal checkLoginRequiredWithRole(String authenticationToken, String... roles) throws AuthenticationException {
    Principal p = new Principal(this.userAccountService.get(authenticationToken), authenticationToken);
    List<String> rolesList = Arrays.asList(roles);
    boolean match =
        p.getUserAccount() != null && p.getUserAccount().getGrantList().stream().anyMatch(grant -> rolesList.contains(grant.getRole()));
    if (!match) {
      throw new AuthenticationException("Authentication as admin user required", PubManException.Reason.PERMISSION_DENIED);
    }
    return p;
  }

  /**
   * Authorizes one service invocation.
   *
   * <p>
   * Rule sets are evaluated in declaration order. The first rule set whose conditions all match
   * grants access; if none matches, the last authorization or authentication failure is propagated.
   * </p>
   */
  public void checkAuthorization(String serviceName, String methodName, Object... objects)
      throws AuthorizationException, AuthenticationException, IngeTechnicalException, IngeApplicationException {
    Map<String, Map<String, Object>> serviceMap = (Map<String, Map<String, Object>>) this.aaMap.get(serviceName);
    if (null == serviceMap) {
      throw new AuthorizationException("No rules for service " + serviceName, PubManException.Reason.PERMISSION_DENIED);
    }
    List<String> order = (List<String>) serviceMap.get("technical").get("order");
    List<Map<String, Object>> allowedMap = (List<Map<String, Object>>) serviceMap.get(methodName);

    if (null == allowedMap) {
      throw new AuthorizationException("No rules for service " + serviceName + ", method " + methodName,
          PubManException.Reason.PERMISSION_DENIED);
    } else {
      Exception lastExceptionOfAll = null;
      for (Map<String, Object> rules : allowedMap) {
        Exception lastExceptionOfRule = null;
        for (Map.Entry<String, Object> rule : rules.entrySet()) {
          try {
            switch (rule.getKey()) {
              case "user": {
                checkUser((Map<String, Object>) rule.getValue(), order, objects);
                break;
              }
              default: {
                String key = rule.getKey();
                Object keyValueObject = getFieldValueOrString(order, objects, key);
                String keyValue = null != keyValueObject ? keyValueObject.toString() : null;
                boolean check = false;
                if (rule.getValue() instanceof Collection<?>) {
                  List<String> valuesToCompare = (List<String>) rule.getValue();
                  check = valuesToCompare.stream().anyMatch(val -> null != keyValue && null != val && val.equalsIgnoreCase(keyValue));
                  if (!check) {
                    throw new AuthorizationException("Expected one of " + valuesToCompare + " for field " + key + " (" + keyValue + ")",
                        PubManException.Reason.PERMISSION_DENIED);
                  }
                } else {
                  Object val = getFieldValueOrString(order, objects, rule.getValue().toString());
                  String value = null;
                  if (null != val) {
                    value = val.toString();
                  }
                  check = (null != keyValue && keyValue.equalsIgnoreCase(value));
                  if (!check) {
                    throw new AuthorizationException("Expected value [" + value + "] for field " + key + " (" + keyValue + ")",
                        PubManException.Reason.PERMISSION_DENIED);
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
        if (null == lastExceptionOfRule) {
          return;
        }
      }

      if (null == lastExceptionOfAll) {
      } else {
        if (lastExceptionOfAll instanceof AuthorizationException)
          throw (AuthorizationException) lastExceptionOfAll;
        else if (lastExceptionOfAll instanceof AuthenticationException) {
          throw (AuthenticationException) lastExceptionOfAll;
        }
      }
    }
  }

  /**
   * Evaluates the {@code user} portion of a rule against the authenticated principal.
   *
   * <p>
   * When {@code field_grant_id_match} is a list, each listed field must resolve to an object
   * identifier covered by the same matching role grant. This is used for compound scopes, such as
   * requiring both a target user's organization and a referenced context's organization to be in
   * the local administrator's scope.
   * </p>
   */
  private void checkUser(Map<String, Object> ruleMap, List<String> order, Object[] objects)
      throws AuthorizationException, AuthenticationException, IngeTechnicalException, IngeApplicationException {
    Principal principal = (Principal) objects[order.indexOf("user")];
    if (null == principal) {
      throw new AuthenticationException("You have to be logged in with username/password or ip address.",
          PubManException.Reason.PERMISSION_DENIED);
    }
    AccountUserDbVO userAccount = principal.getUserAccount();
    String ipMatch = (String) ruleMap.get("ip_match");
    if (null != ipMatch) {
      DecodedJWT decodedJwt = this.userAccountService.verifyToken(principal.getJwToken());
      if (null != decodedJwt.getHeaderClaim("ip")) {
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
            IpListProvider.IpRange ouIpRange = this.ipListProvider.get(ouId);
            if (ouIpRange != null && ouIpRange.matches(userIp)) {
              check = true;
              break;
            }
          }
          if (!check) {
            throw new AuthenticationException(
                "The current user's ip adress " + userIp + " does not match required ip range of organization with id " + ouIdToBeMatched,
                PubManException.Reason.PERMISSION_DENIED);
          }
        } catch (Exception e) {
          throw new AuthenticationException("Error while matching IPs", e, PubManException.Reason.PERMISSION_DENIED);
        }
      } else {
        throw new AuthenticationException("Token contains no IP, but IP match is required", PubManException.Reason.PERMISSION_DENIED);
      }
    } else if (null == userAccount) {
      throw new AuthenticationException("You have to be logged in with username/password.", PubManException.Reason.PERMISSION_DENIED);
    }

    String userIdFieldMatch = (String) ruleMap.get("field_user_id_match");
    if (null != userIdFieldMatch) {
      Object userId = getFieldValueOrString(order, objects, userIdFieldMatch);
      String expectedUserId = (null != userId ? userId.toString() : null);
      if (null == expectedUserId || !expectedUserId.equals(userAccount.getObjectId())) {
        throw new AuthorizationException("User is not owner of object.", PubManException.Reason.PERMISSION_DENIED);
      }
    }

    if (ruleMap.containsKey("role") || ruleMap.containsKey("field_grant_id_match")) {
      boolean check = false;
      String role = (String) ruleMap.get("role");
      Object grantFieldMatchObject = ruleMap.get("field_grant_id_match");
      List<String> grantFieldMatches = getGrantFieldMatches(grantFieldMatchObject);
      boolean multipleGrantFieldMatches = grantFieldMatchObject instanceof Collection<?>;

      for (GrantVO grant : userAccount.getGrantList()) {
        if (null != role && !role.equals(grant.getRole())) {
          continue;
        }

        if (grantFieldMatches.isEmpty()) {
          check = true;
          break;
        }

        boolean matchesAllFields = true;
        for (String grantFieldMatch : grantFieldMatches) {
          List<String> grantFieldMatchValues = resolveGrantFieldMatchValues(order, objects, grantFieldMatch, multipleGrantFieldMatches);
          if (grantFieldMatchValues.isEmpty() || null == grant.getObjectRef()
              || grantFieldMatchValues.stream().noneMatch(id -> id.equals(grant.getObjectRef()))) {
            matchesAllFields = false;
            break;
          }
        }

        if (matchesAllFields) {
          check = true;
          break;
        }
      }

      if (!check) {
        throw new AuthorizationException("Expected user with role [" + role + "], on object [" + grantFieldMatches + "]",
            PubManException.Reason.PERMISSION_DENIED);
      }
    }

    // Ein angemeldeter Benutzer mit einem Kontext für eine Ou O darf nur dann einen vorgegebenen Benutzer sehen, wenn der Benutzer zur selben Ou O gehört.
    if (ruleMap.containsKey("role") && ruleMap.containsKey("field_ctx_ou_id_match")) {
      boolean check = false;
      String role = (String) ruleMap.get("role");
      String ctxOuFieldMatch = (String) ruleMap.get("field_ctx_ou_id_match");
      Object val = getFieldValueOrString(order, objects, ctxOuFieldMatch);
      if (null == val) {
        throw new AuthorizationException("getFieldValue for " + ctxOuFieldMatch + " returned null!",
            PubManException.Reason.PERMISSION_DENIED);
      }

      String ctxOuFieldMatchValue = val.toString(); // Ou des vorgegebenen Benutzers
      if (!ctxOuFieldMatchValue.startsWith("ou")) {
        throw new AuthorizationException("ctxOuFieldMatchValue " + ctxOuFieldMatch + " does not start with ou!",
            PubManException.Reason.PERMISSION_DENIED);
      }

      for (GrantVO grant : userAccount.getGrantList()) {
        if (null != grant.getObjectRef() && grant.getObjectRef().startsWith("ctx")) {
          ContextDbVO ctx = this.ctxService.get(grant.getObjectRef(), null);
          if (null == ctx) {
            throw new AuthorizationException("context for " + ctxOuFieldMatchValue + " returned null!",
                PubManException.Reason.PERMISSION_DENIED);
          }
          if (ctx.getResponsibleAffiliations().isEmpty()) {
            throw new AuthorizationException("context " + ctx.getObjectId() + " has no affiliations!",
                PubManException.Reason.PERMISSION_DENIED);
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
            "Expected user with role [" + role + "], on object [" + ctxOuFieldMatchValue + "] (" + ctxOuFieldMatch + ")",
            PubManException.Reason.PERMISSION_DENIED);
      }
    }
  }


  /**
   * Resolves a dotted rule field using {@code technical.order}, or returns a literal rule value.
   *
   * <p>
   * The context-affiliation special case preserves the historic scalar behavior by returning the
   * first responsible affiliation. Multi-field grant matching resolves all affiliations through
   * {@link #getMultipleGrantFieldValue(List, Object[], String)} instead.
   * </p>
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
      if (null == object) {
        return null;
        // hart codiert, da getFieldValueViaGetter nicht mit arrays funktioniert
      } else if (object.getClass().equals(ContextDbVO.class) && 2 == fieldHierarchy.length
          && "responsibleAffiliations".equals(fieldHierarchy[1])) {
        ContextDbVO ro = (ContextDbVO) object;
        return ro.getResponsibleAffiliations().get(0).getObjectId();
      } else {
        return getFieldValueViaGetter(object, field.substring(field.indexOf(".") + 1));
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
          if (null == value) {
            return null;
          }
          if (1 == fieldHierarchy.length) {
            return value;
          } else {
            return getFieldValueViaGetter(value, field.substring(field.indexOf(".") + 1));
          }
        }
      }
    } catch (Exception e) {
      throw new AuthorizationException("Error while calling getter in object", e, PubManException.Reason.PERMISSION_DENIED);
    }

    return null;
  }

  /**
   * Converts either the legacy scalar configuration or the multi-field list configuration to field
   * names.
   */
  private List<String> getGrantFieldMatches(Object grantFieldMatchObject) {
    if (grantFieldMatchObject == null) {
      return Collections.emptyList();
    }
    if (grantFieldMatchObject instanceof Collection<?>) {
      return ((Collection<?>) grantFieldMatchObject).stream().filter(Objects::nonNull).map(Object::toString).collect(Collectors.toList());
    }
    return List.of(grantFieldMatchObject.toString());
  }

  /**
   * Resolves the identifiers accepted by a grant-object match, including the organization path.
   *
   * <p>
   * Scalar configuration intentionally follows the legacy resolution exactly. List configuration
   * additionally supports all responsible affiliations of a context.
   * </p>
   */
  private List<String> resolveGrantFieldMatchValues(List<String> order, Object[] objects, String grantFieldMatch,
      boolean multipleGrantFieldMatches) throws AuthorizationException, IngeTechnicalException, IngeApplicationException {
    Object val = multipleGrantFieldMatches ? getMultipleGrantFieldValue(order, objects, grantFieldMatch)
        : getFieldValueOrString(order, objects, grantFieldMatch);
    if (val == null) {
      logger.warn("getFieldValue for " + grantFieldMatch + " returned null!");
      return Collections.emptyList();
    }

    if (!multipleGrantFieldMatches) {
      String grantFieldMatchValue = val.toString();
      if (!grantFieldMatchValue.startsWith("ou")) {
        return List.of(grantFieldMatchValue);
      }

      List<String> parents = this.ouService.getIdPath(grantFieldMatchValue);
      parents.remove(parents.size() - 1); // remove root
      return parents;
    }

    List<String> grantFieldMatchValues = new ArrayList<>();
    if (val instanceof Collection<?>) {
      for (Object element : (Collection<?>) val) {
        grantFieldMatchValues.addAll(resolveGrantFieldMatchValuesFromValue(element));
      }
    } else {
      grantFieldMatchValues.addAll(resolveGrantFieldMatchValuesFromValue(val));
    }
    return grantFieldMatchValues;
  }

  /** Resolves all context affiliations only for list-valued grant-match configuration. */
  private Object getMultipleGrantFieldValue(List<String> order, Object[] objects, String grantFieldMatch) throws AuthorizationException {
    String[] fieldHierarchy = grantFieldMatch.split("\\.");
    if (fieldHierarchy.length != 2 || !"responsibleAffiliations".equals(fieldHierarchy[1])) {
      return getFieldValueOrString(order, objects, grantFieldMatch);
    }

    int objectIndex = order.indexOf(fieldHierarchy[0]);
    if (objectIndex < 0 || objects[objectIndex] == null || !objects[objectIndex].getClass().equals(ContextDbVO.class)) {
      return getFieldValueOrString(order, objects, grantFieldMatch);
    }

    return ((ContextDbVO) objects[objectIndex]).getResponsibleAffiliations();
  }

  private List<String> resolveGrantFieldMatchValuesFromValue(Object value) throws AuthorizationException, IngeApplicationException {
    if (value == null) {
      return Collections.emptyList();
    }

    String resolvedValue;
    if (value instanceof String) {
      resolvedValue = value.toString();
    } else {
      Object objectId = getFieldValueViaGetter(value, "objectId");
      resolvedValue = null != objectId ? objectId.toString() : value.toString();
    }
    List<String> grantFieldMatchValues = new ArrayList<>();
    grantFieldMatchValues.add(resolvedValue);

    if (resolvedValue.startsWith("ou")) {
      // If grant is of type "ORGANIZATION", get all parents of organization up to firstLevel as potential matches
      List<String> parents = this.ouService.getIdPath(resolvedValue);
      if (!parents.isEmpty()) {
        parents.remove(parents.size() - 1); // remove root
        grantFieldMatchValues.addAll(parents);
      }
    }

    return grantFieldMatchValues;
  }
}
