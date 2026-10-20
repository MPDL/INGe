package de.mpg.mpdl.inge.service.pubman.orcid;

import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.search.ResponseBody;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.mpg.mpdl.inge.db.repository.OrcidAuthorizationRepository;
import de.mpg.mpdl.inge.db.repository.OrcidRepository;
import de.mpg.mpdl.inge.es.dao.impl.ElasticSearchGenericDAOImpl;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.db.valueobjects.OrcidAuthorizationDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.OrcidDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.util.MapperFactory;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.EmailService;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.transformation.TransformerFactory;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.transformation.results.TransformerStreamResult;
import de.mpg.mpdl.inge.transformation.sources.TransformerVoSource;
import de.mpg.mpdl.inge.transformation.transformers.SearchRetrieveResponseToItemOrcid;
import de.mpg.mpdl.inge.util.PropertyReader;
import de.mpg.mpdl.inge.util.ResourceUtil;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public final class OrcidHelper {

  private static final Logger logger = LogManager.getLogger(OrcidHelper.class);

  private OrcidHelper() {
    // Utility class
  }

  public static String generateSecret() {
    return UUID.randomUUID().toString();
  }

  public static String createEmailLink(String scopeRequested, String redirectUri) {
    String orcidlink =
        PropertyReader.getProperty(PropertyReader.ORCID_URL) + PropertyReader.getProperty(PropertyReader.ORCID_AUTHORIZE_URL);
    String formattedScope = scopeRequested != null ? scopeRequested.replace(" ", "%20") : "";
    String scope = "scope=" + formattedScope;
    String responseType = "response_type=" + PropertyReader.getProperty(PropertyReader.ORCID_RESPONSE_TYPE);
    String redirect = "redirect_uri=" + redirectUri;
    String clientId = "client_id=" + PropertyReader.getProperty(PropertyReader.ORCID_CLIENT_ID);

    StringBuilder sb = new StringBuilder();
    sb.append(orcidlink);
    sb.append("?");
    sb.append(scope);
    sb.append("&");
    sb.append(responseType);
    sb.append("&");
    sb.append(redirect);
    sb.append("&");
    sb.append(clientId);

    return sb.toString();
  }

  public static String createEmailText(String nameAuthor, String orcidAuthor, String emailLink) throws TechnicalException {
    try {
      String template = ResourceUtil.getResourceAsString("orcid_email.txt", OrcidHelper.class.getClassLoader());
      template = template.replace("$NAME_AUTHOR", nameAuthor != null ? nameAuthor : "");
      template = template.replace("$ORCID_AUTHOR", orcidAuthor != null ? orcidAuthor : "");
      template = template.replace("$EMAIL_LINK", emailLink != null ? emailLink : "");
      return template;
    } catch (IOException e) {
      logger.error("Error reading orcid_email.txt", e);
      throw new TechnicalException("Error reading orcid_email.txt", e);
    }
  }

  public static OrcidAuthorizationDbVO requestTokenAndOrcid(OrcidAuthorizationDbVO orcidAuthorizationDbVO, String code,
      OrcidAuthorizationRepository orcidAuthorizationRepository) throws TechnicalException {

    String tokenUrl = PropertyReader.getProperty(PropertyReader.ORCID_URL) + PropertyReader.getProperty(PropertyReader.ORCID_TOKEN_URL);
    String clientId = PropertyReader.getProperty(PropertyReader.ORCID_CLIENT_ID);
    String clientSecret = PropertyReader.getProperty(PropertyReader.ORCID_CLIENT_SECRET);
    String redirectUri = orcidAuthorizationDbVO.getRedirectUri();

    Map<String, String> formData = new LinkedHashMap<>();
    formData.put("client_id", clientId);
    formData.put("client_secret", clientSecret);
    formData.put("grant_type", "authorization_code");
    formData.put("code", code);
    formData.put("redirect_uri", redirectUri);

    StringBuilder formBodyBuilder = new StringBuilder();
    for (Map.Entry<String, String> entry : formData.entrySet()) {
      if (!formBodyBuilder.isEmpty()) {
        formBodyBuilder.append("&");
      }
      formBodyBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
      formBodyBuilder.append("=");
      formBodyBuilder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
    }
    String formBody = formBodyBuilder.toString();

    try {
      HttpClient httpClient = HttpClient.newHttpClient();
      HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(tokenUrl)).header("Accept", "application/json")
          .header("Content-Type", "application/x-www-form-urlencoded")
          .POST(HttpRequest.BodyPublishers.ofString(formBody, StandardCharsets.UTF_8)).build();

      HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

      if (httpResponse.statusCode() >= 200 && httpResponse.statusCode() < 300) {
        String responseBody = httpResponse.body();
        JSONObject jsonObject = new JSONObject(responseBody);
        orcidAuthorizationDbVO =
            updateAuthorizationDbVO(orcidAuthorizationDbVO, jsonObject.getString("access_token"), jsonObject.getString("refresh_token"),
                jsonObject.getString("scope"), jsonObject.getString("name"), jsonObject.getString("orcid"),
                jsonObject.getString("token_type"), String.valueOf(jsonObject.get("expires_in")), orcidAuthorizationRepository);

        return orcidAuthorizationDbVO;
      } else {
        logger.error("Error requesting ORCID token: HTTP status {} - response: {}", httpResponse.statusCode(), httpResponse.body());
        throw new TechnicalException(
            "Error requesting ORCID token: HTTP status " + httpResponse.statusCode() + " - " + httpResponse.body());
      }
    } catch (TechnicalException e) {
      throw e;
    } catch (Exception e) {
      logger.error("Exception requesting ORCID token", e);
      throw new TechnicalException("Exception requesting ORCID token: " + e.getMessage(), e);
    }
  }

  public static JSONObject createWork(String orcid, String body, String accessToken) throws TechnicalException {
    String createUrl = "https://api.sandbox.orcid.org/v3.0/" + orcid + "/works"; //TODO aus pubman.properties
    logger.info("Creating Orcid work: " + createUrl);
    //    logger.info("Creating Orcid work body: " + body);
    logger.info("Creating Orcid work access token: " + accessToken);

    try {
      HttpClient httpClient = HttpClient.newHttpClient();
      HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(URI.create(createUrl))
          .header("Accept", "application/vnd.orcid+json").header("Content-Type", "application/json");

      if (accessToken != null && !accessToken.isBlank()) {
        requestBuilder.header("Authorization", "Bearer " + accessToken);
      }

      HttpRequest httpRequest =
          requestBuilder.POST(HttpRequest.BodyPublishers.ofString(body != null ? body : "", StandardCharsets.UTF_8)).build();
      logger.info("Creating Orcid work request: " + httpRequest);

      HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

      if (httpResponse.statusCode() >= 200 && httpResponse.statusCode() < 300) {
        String responseBody = httpResponse.body();
        JSONObject jsonObject = new JSONObject(responseBody);
        return jsonObject;
      } else {
        logger.error("Error creating ORCID work: HTTP status {} - response: {}", httpResponse.statusCode(), httpResponse.body());
        throw new TechnicalException("Error creating ORCID work: HTTP status " + httpResponse.statusCode() + " - " + httpResponse.body());
      }
    } catch (TechnicalException e) {
      throw e;
    } catch (Exception e) {
      logger.error("Exception creating ORCID work", e);
      throw new TechnicalException("Exception creating ORCID work: " + e.getMessage(), e);
    }
  }

  public static JSONObject updateWork(String orcid, String body, String accessToken, String putCode) throws TechnicalException {
    String updateUrl = "https://api.sandbox.orcid.org/v3.0/" + orcid + "/work/" + putCode; //TODO aus pubman.properties
    logger.info("Update Orcid work: " + updateUrl);
    logger.info("Update Orcid work body: " + body);
    logger.info("Update Orcid work access token: " + accessToken);
    logger.info("Update Orcid work putcode: " + putCode);

    try {
      HttpClient httpClient = HttpClient.newHttpClient();
      HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(URI.create(updateUrl))
          .header("Accept", "application/vnd.orcid+json").header("Content-Type", "application/json");
      if (accessToken != null && !accessToken.isBlank()) {
        requestBuilder.header("Authorization", "Bearer " + accessToken);
      }

      HttpRequest httpRequest =
          requestBuilder.PUT(HttpRequest.BodyPublishers.ofString(body != null ? body : "", StandardCharsets.UTF_8)).build();
      logger.info("Update Orcid work request: " + httpRequest);

      HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

      if (httpResponse.statusCode() >= 200 && httpResponse.statusCode() < 300) {
        String responseBody = httpResponse.body();
        JSONObject jsonObject = (responseBody != null && !responseBody.isBlank()) ? new JSONObject(responseBody) : new JSONObject();
        return jsonObject;
      } else {
        logger.error("Error updating ORCID work: HTTP status {} - response: {}", httpResponse.statusCode(), httpResponse.body());
        throw new TechnicalException("Error updating ORCID work: HTTP status " + httpResponse.statusCode() + " - " + httpResponse.body());
      }
    } catch (TechnicalException e) {
      throw e;
    } catch (Exception e) {
      logger.error("Exception updating ORCID work", e);
      throw new TechnicalException("Exception updating ORCID work: " + e.getMessage(), e);
    }
  }

  public static OrcidAuthorizationDbVO createOrcidAuthorizationDbVO(OrcidAuthorizationRepository orcidAuthorizationRepository,
      String userIdBibo, String coneIdAuthor, String orcidAuthor, String nameAuthor, String secret, String scopeRequested, String emailLink,
      String emailBibo, String emailAuthor, String redirectUri) {

    OrcidAuthorizationDbVO orcidAuthorizationDbVO = new OrcidAuthorizationDbVO(userIdBibo, coneIdAuthor, orcidAuthor, nameAuthor, secret,
        scopeRequested, emailLink, emailBibo, emailAuthor, redirectUri);
    if (orcidAuthorizationRepository != null) {
      orcidAuthorizationDbVO = orcidAuthorizationRepository.saveAndFlush(orcidAuthorizationDbVO);
    }

    logger.info("Updated new orcidAuthorizationDbVO: " + orcidAuthorizationDbVO);

    return orcidAuthorizationDbVO;
  }

  public static OrcidAuthorizationDbVO finishAuthorizationDbVO(OrcidAuthorizationDbVO orcidAuthorizationDbVO,
      OrcidAuthorizationRepository orcidAuthorizationRepository) {
    orcidAuthorizationDbVO.setStatus(OrcidAuthorizationDbVO.Status.EMAIL_SENT_BIBO.toString());
    orcidAuthorizationDbVO.setDateEmailSentBibo(new Date());

    if (orcidAuthorizationRepository != null) {
      orcidAuthorizationDbVO = orcidAuthorizationRepository.saveAndFlush(orcidAuthorizationDbVO);
    }

    logger.info("Finished orcidAuthorizationDbVO: " + orcidAuthorizationDbVO);

    return orcidAuthorizationDbVO;
  }

  public static OrcidAuthorizationDbVO updateAuthorizationDbVO(OrcidAuthorizationDbVO orcidAuthorizationDbVO, String code,
      OrcidAuthorizationRepository orcidAuthorizationRepository) {
    orcidAuthorizationDbVO.setCodeReceived(code);
    orcidAuthorizationDbVO.setStatus(OrcidAuthorizationDbVO.Status.CODE_RECEIVED.toString());
    orcidAuthorizationDbVO.setDateCodeReceived(new Date());

    if (orcidAuthorizationRepository != null) {
      orcidAuthorizationDbVO = orcidAuthorizationRepository.saveAndFlush(orcidAuthorizationDbVO);
    }

    logger.info("Udated orcidAuthorizationDbVO: " + orcidAuthorizationDbVO);

    return orcidAuthorizationDbVO;
  }

  public static OrcidAuthorizationDbVO updateAuthorizationDbVO(OrcidAuthorizationDbVO orcidAuthorizationDbVO, String accessToken,
      String refreshToken, String scope, String name, String orcid, String tokenType, String expiresIn,
      OrcidAuthorizationRepository orcidAuthorizationRepository) {
    orcidAuthorizationDbVO.setAccessToken(accessToken);
    orcidAuthorizationDbVO.setRefreshToken(refreshToken);
    orcidAuthorizationDbVO.setScope(scope);
    orcidAuthorizationDbVO.setName(name);
    orcidAuthorizationDbVO.setOrcid(orcid);
    orcidAuthorizationDbVO.setTokenType(tokenType);
    orcidAuthorizationDbVO.setExpiresIn(expiresIn);
    orcidAuthorizationDbVO.setStatus(OrcidAuthorizationDbVO.Status.TOKEN_RECEIVED.toString());
    orcidAuthorizationDbVO.setDateTokenReceived(new Date());
    orcidAuthorizationDbVO.setErrorCodes(null);
    orcidAuthorizationDbVO.setDateError(null);
    orcidAuthorizationDbVO.setVerified(true);

    StringBuilder sb = new StringBuilder();
    if (orcidAuthorizationDbVO.getNameAuthor() != null && !orcidAuthorizationDbVO.getNameAuthor().equalsIgnoreCase(name)) {
      sb.append(OrcidAuthorizationDbVO.ErrorCode.WARN_DIFFERENT_NAME.toString());
    }
    if (orcidAuthorizationDbVO.getOrcidAuthor() != null && !orcidAuthorizationDbVO.getOrcidAuthor().equalsIgnoreCase(orcid)) {
      if (!sb.isEmpty()) {
        sb.append(" " + OrcidAuthorizationDbVO.ErrorCode.WARN_DIFFERENT_ORCID.toString());
      } else {
        sb.append(OrcidAuthorizationDbVO.ErrorCode.WARN_DIFFERENT_ORCID.toString());
      }
    }

    if (!sb.isEmpty()) {
      orcidAuthorizationDbVO.setErrorCodes(sb.toString());
    }

    if (orcidAuthorizationRepository != null) {
      orcidAuthorizationDbVO = orcidAuthorizationRepository.saveAndFlush(orcidAuthorizationDbVO);
    }

    logger.info("Udated orcidAuthorizationDbVO: " + orcidAuthorizationDbVO);

    return orcidAuthorizationDbVO;
  }

  public static JsonNode createOrcidQueryJsonNode(String coneIdAuthor, Date dateModified) throws IngeApplicationException {
    if (coneIdAuthor == null || coneIdAuthor.trim().isEmpty()) {
      throw new IngeApplicationException("coneIdAuthor must not be empty");
    }

    String normalizedPersonId = coneIdAuthor.trim();
    if (normalizedPersonId.contains("/persons/resource/")) {
      normalizedPersonId = normalizedPersonId.substring(normalizedPersonId.indexOf("/persons/resource/"));
    } else if (!normalizedPersonId.startsWith("/")) {
      normalizedPersonId = "/" + normalizedPersonId;
    }

    ObjectMapper mapper = MapperFactory.getObjectMapper();
    ObjectNode rootNode = mapper.createObjectNode();

    // query
    ObjectNode queryNode = rootNode.putObject("query");
    ObjectNode boolNode = queryNode.putObject("bool");
    ArrayNode mustArray = boolNode.putArray("must");

    // 1. term: metadata.creators.person.identifier.id
    ObjectNode termObj = mapper.createObjectNode();
    termObj.putObject("term").put("metadata.creators.person.identifier.id", normalizedPersonId);
    mustArray.add(termObj);

    // 2. range: modificationDate (optional)
    if (dateModified != null) {
      ObjectNode rangeObj = mapper.createObjectNode();
      rangeObj.putObject("range").putObject("modificationDate").put("gte", dateModified.getTime());
      mustArray.add(rangeObj);
    }

    // 3. bool: should: bool: must: versionState RELEASED, must_not: publicState WITHDRAWN
    ObjectNode stateBoolObj = mapper.createObjectNode();
    ObjectNode stateInnerBool = stateBoolObj.putObject("bool");
    ArrayNode shouldArray = stateInnerBool.putArray("should");
    ObjectNode shouldInnerBoolObj = mapper.createObjectNode();
    ObjectNode shouldInnerBool = shouldInnerBoolObj.putObject("bool");
    ArrayNode releaseMust = shouldInnerBool.putArray("must");
    ObjectNode versionStateTerm = mapper.createObjectNode();
    versionStateTerm.putObject("term").put("versionState", "RELEASED");
    releaseMust.add(versionStateTerm);
    ArrayNode withdrawnMustNot = shouldInnerBool.putArray("must_not");
    ObjectNode publicStateTerm = mapper.createObjectNode();
    publicStateTerm.putObject("term").put("publicState", "WITHDRAWN");
    withdrawnMustNot.add(publicStateTerm);
    shouldArray.add(shouldInnerBoolObj);
    mustArray.add(stateBoolObj);

    // size, from
    rootNode.put("size", 10000); // TODO: eventuell über eine Scroll Anfrage
    rootNode.put("from", 0);

    // sort
    ArrayNode sortArray = rootNode.putArray("sort");
    ObjectNode sortObj = mapper.createObjectNode();
    sortObj.putObject("lastModificationDate").put("order", "desc");
    sortArray.add(sortObj);

    return rootNode;
  }

  public static SearchRetrieveResponseVO<ItemVersionVO> doOrcidQuery(PubItemService pubItemService, String coneIdAuthor, Date dateModified,
      String token) throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    JsonNode searchSource = createOrcidQueryJsonNode(coneIdAuthor, dateModified);
    logger.info("Orcid query: " + searchSource);

    try {
      String searchSourceText = MapperFactory.getObjectMapper().writeValueAsString(searchSource);
      SearchRequest srequ = SearchRequest.of(sr -> sr.withJson(new StringReader(searchSourceText)));
      logger.info("searchSourceText: " + searchSourceText);

      ResponseBody<ObjectNode> responseBody = pubItemService != null ? pubItemService.searchDetailed(srequ, -1, token) : null;
      if (responseBody != null) {
        return ElasticSearchGenericDAOImpl.getSearchRetrieveResponseFromElasticSearchResponse((ResponseBody) responseBody,
            ItemVersionVO.class);
      } else {
        return new SearchRetrieveResponseVO<>();
      }
    } catch (IOException e) {
      throw new IngeTechnicalException("Error creating search request or converting Elasticsearch response", e);
    }
  }

  public static String convertToOrcid(SearchRetrieveResponseVO<ItemVersionVO> searchResult) throws IngeTechnicalException {
    try {
      SearchRetrieveResponseToItemOrcid transformer = new SearchRetrieveResponseToItemOrcid();
      transformer.setSourceFormat(TransformerFactory.FORMAT.SEARCH_RESULT_VO);
      transformer.setTargetFormat(TransformerFactory.FORMAT.ORCID);
      TransformerVoSource source = new TransformerVoSource(searchResult != null ? searchResult : new SearchRetrieveResponseVO<>());
      StringWriter writer = new StringWriter();
      TransformerStreamResult result = new TransformerStreamResult(writer);
      transformer.transform(source, result);
      return writer.toString();
    } catch (TransformationException e) {
      throw new IngeTechnicalException("Error transforming SearchRetrieveResponse to ORCID format", e);
    }
  }

  public static Map<String, ObjectNode> extractWorksBySourceWorkId(String orcidJson) throws IngeTechnicalException {
    if (orcidJson == null || orcidJson.trim().isEmpty()) {
      return new LinkedHashMap<>();
    }
    try {
      JsonNode rootNode = MapperFactory.getObjectMapper().readTree(orcidJson);
      return extractWorksBySourceWorkId(rootNode);
    } catch (Exception e) {
      throw new IngeTechnicalException("Error parsing ORCID JSON string", e);
    }
  }

  public static Map<String, ObjectNode> extractWorksBySourceWorkId(JsonNode rootNode) {
    Map<String, ObjectNode> worksBySourceWorkId = new LinkedHashMap<>();
    if (rootNode == null) {
      return worksBySourceWorkId;
    }

    JsonNode bulkNode = rootNode.get("bulk");
    if (bulkNode != null && bulkNode.isArray()) {
      for (JsonNode itemWrapper : bulkNode) {
        JsonNode workNode = itemWrapper.has("work") ? itemWrapper.get("work") : itemWrapper;
        if (workNode instanceof ObjectNode) {
          String sourceWorkId = getSourceWorkId(workNode);
          if (sourceWorkId != null && !sourceWorkId.isEmpty()) {
            worksBySourceWorkId.put(sourceWorkId, (ObjectNode) workNode);
          }
        }
      }
    } else if (rootNode.has("work")) {
      JsonNode workNode = rootNode.get("work");
      if (workNode instanceof ObjectNode) {
        String sourceWorkId = getSourceWorkId(workNode);
        if (sourceWorkId != null && !sourceWorkId.isEmpty()) {
          worksBySourceWorkId.put(sourceWorkId, (ObjectNode) workNode);
        }
      }
    } else if (rootNode instanceof ObjectNode && rootNode.has("external-ids")) {
      String sourceWorkId = getSourceWorkId(rootNode);
      if (sourceWorkId != null && !sourceWorkId.isEmpty()) {
        worksBySourceWorkId.put(sourceWorkId, (ObjectNode) rootNode);
      }
    }

    return worksBySourceWorkId;
  }

  public static Map<String, ObjectNode> getWorksBySourceWorkId(String orcidJson) throws IngeTechnicalException {
    return extractWorksBySourceWorkId(orcidJson);
  }

  public static Map<String, ObjectNode> getWorksBySourceWorkId(JsonNode rootNode) {
    return extractWorksBySourceWorkId(rootNode);
  }

  public static String getSourceWorkId(JsonNode workNode) {
    if (workNode == null) {
      return null;
    }
    JsonNode extIds = workNode.get("external-ids");
    if (extIds == null) {
      return null;
    }
    JsonNode extIdList = extIds.get("external-id");
    if (extIdList == null) {
      return null;
    }
    if (extIdList.isArray()) {
      for (JsonNode extId : extIdList) {
        String type = extId.has("external-id-type") ? extId.get("external-id-type").asText() : null;
        if ("source-work-id".equalsIgnoreCase(type)) {
          return extId.has("external-id-value") ? extId.get("external-id-value").asText() : null;
        }
      }
    } else if (extIdList.isObject()) {
      String type = extIdList.has("external-id-type") ? extIdList.get("external-id-type").asText() : null;
      if ("source-work-id".equalsIgnoreCase(type)) {
        return extIdList.has("external-id-value") ? extIdList.get("external-id-value").asText() : null;
      }
    }
    return null;
  }

  public static void setPutCode(ObjectNode workNode, Object putCode) {
    if (workNode == null || putCode == null) {
      return;
    }
    if (putCode instanceof Number) {
      workNode.put("put-code", ((Number) putCode).longValue());
    } else if (putCode instanceof String) {
      String str = ((String) putCode).trim();
      try {
        workNode.put("put-code", Long.parseLong(str));
      } catch (NumberFormatException e) {
        workNode.put("put-code", str);
      }
    } else {
      workNode.put("put-code", putCode.toString());
    }
  }

  public static String convertWorksToOrcidJson(Map<String, ObjectNode> worksBySourceWorkId) throws IngeTechnicalException {
    return convertWorksToOrcidJson(worksBySourceWorkId != null ? worksBySourceWorkId.values() : null);
  }

  public static String convertWorksToOrcidJson(Collection<ObjectNode> works) throws IngeTechnicalException {
    try {
      ObjectMapper mapper = MapperFactory.getObjectMapper();
      ObjectNode root = mapper.createObjectNode();
      ArrayNode bulkArray = root.putArray("bulk");
      if (works != null) {
        for (ObjectNode work : works) {
          if (work != null) {
            ObjectNode itemWrapper = mapper.createObjectNode();
            itemWrapper.set("work", work);
            bulkArray.add(itemWrapper);
          }
        }
      }
      return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
    } catch (Exception e) {
      throw new IngeTechnicalException("Error converting works to ORCID JSON string", e);
    }
  }

  public static String convertWorksToString(Map<String, ObjectNode> worksBySourceWorkId) throws IngeTechnicalException {
    return convertWorksToOrcidJson(worksBySourceWorkId);
  }

  public static String updatePutCodesInOrcidJson(String orcidJson, Map<String, ?> putCodesBySourceWorkId) throws IngeTechnicalException {
    if (orcidJson == null || orcidJson.trim().isEmpty() || putCodesBySourceWorkId == null || putCodesBySourceWorkId.isEmpty()) {
      return orcidJson;
    }
    try {
      ObjectMapper mapper = MapperFactory.getObjectMapper();
      JsonNode root = mapper.readTree(orcidJson);
      Map<String, ObjectNode> worksMap = extractWorksBySourceWorkId(root);
      for (Map.Entry<String, ?> entry : putCodesBySourceWorkId.entrySet()) {
        ObjectNode workNode = worksMap.get(entry.getKey());
        if (workNode != null && entry.getValue() != null) {
          setPutCode(workNode, entry.getValue());
        }
      }
      return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
    } catch (Exception e) {
      throw new IngeTechnicalException("Error updating put-codes in ORCID JSON", e);
    }
  }

  public static List<OrcidBulkItem> parseBulkResponse(JSONObject answer) {
    if (answer == null || !answer.has("bulk")) {
      return Collections.emptyList();
    }

    JSONArray bulkArray = answer.optJSONArray("bulk");
    if (bulkArray == null) {
      return Collections.emptyList();
    }

    List<OrcidBulkItem> results = new ArrayList<>(bulkArray.length());

    for (int i = 0; i < bulkArray.length(); i++) {
      JSONObject node = bulkArray.optJSONObject(i);
      if (node == null) {
        continue;
      }

      if (node.has("work")) {
        JSONObject workObj = node.optJSONObject("work");
        if (workObj != null) {
          OrcidBulkItem item = new OrcidBulkItem(OrcidBulkItem.Type.WORK, i, workObj);
          if (workObj.has("put-code") && !workObj.isNull("put-code")) {
            item.setPutCode(workObj.optLong("put-code"));
          }
          results.add(item);
        }
      } else if (node.has("error")) {
        JSONObject errorObj = node.optJSONObject("error");
        if (errorObj != null) {
          OrcidBulkItem item = new OrcidBulkItem(OrcidBulkItem.Type.ERROR, i, errorObj);
          if (errorObj.has("response-code") && !errorObj.isNull("response-code")) {
            item.setResponseCode(errorObj.optInt("response-code"));
          }
          if (errorObj.has("error-code") && !errorObj.isNull("error-code")) {
            item.setErrorCode(errorObj.optInt("error-code"));
          }
          item.setUserMessage(errorObj.optString("user-message", null));
          item.setDeveloperMessage(errorObj.optString("developer-message", null));
          item.setMoreInfo(errorObj.optString("more-info", null));
          results.add(item);
        }
      }
    }

    return results;
  }

  public static OrcidDbVO saveOrcidDbRecord(OrcidRepository orcidRepository, OrcidAuthorizationDbVO verifiedOrcidAuthorization,
      String itemId, String putCode, JSONObject answer, OrcidDbVO.Status status) {
    if (orcidRepository == null || itemId == null) {
      return null;
    }
    String coneIdAuthor = verifiedOrcidAuthorization != null ? verifiedOrcidAuthorization.getConeIdAuthor() : null;
    OrcidDbVO orcidDbVO = orcidRepository.findByConeIdAuthorAndItemId(coneIdAuthor, itemId);
    if (orcidDbVO == null) {
      orcidDbVO = new OrcidDbVO();
      orcidDbVO.setOrcidAuthorizationDbVO(verifiedOrcidAuthorization);
      orcidDbVO.setItemId(itemId);
    }
    if (status != null) {
      orcidDbVO.setStatus(status.toString());
    }
    orcidDbVO.setDate(new Date());

    if (putCode != null) {
      orcidDbVO.setPutCode(putCode);
    } else if (answer != null && answer.has("put-code") && !answer.isNull("put-code")) {
      orcidDbVO.setPutCode(String.valueOf(answer.get("put-code")));
    }

    if (answer != null) {
      if (answer.has("response-code") && !answer.isNull("response-code")) {
        orcidDbVO.setResponseCode(String.valueOf(answer.get("response-code")));
      }
      if (answer.has("developer-message") && !answer.isNull("developer-message")) {
        orcidDbVO.setDeveloperMessage(answer.getString("developer-message"));
      }
    }

    return orcidRepository.saveAndFlush(orcidDbVO);
  }

  public static void saveOrcidDbRecords(OrcidRepository orcidRepository, OrcidAuthorizationDbVO verifiedOrcidAuthorization,
      SearchRetrieveResponseVO<ItemVersionVO> searchResult, List<OrcidBulkItem> bulkItems, OrcidDbVO.Status status) {
    if (searchResult != null && searchResult.getRecords() != null && !searchResult.getRecords().isEmpty()) {
      for (int i = 0; i < searchResult.getRecords().size(); i++) {
        SearchRetrieveRecordVO<ItemVersionVO> record = searchResult.getRecords().get(i);
        ItemVersionVO itemVersionVO = record != null ? record.getData() : null;
        if (itemVersionVO != null && itemVersionVO.getObjectId() != null) {
          String itemId = itemVersionVO.getObjectId();
          OrcidBulkItem bulkItem = (bulkItems != null && i < bulkItems.size()) ? bulkItems.get(i) : null;
          if (bulkItem != null && orcidRepository != null) {
            OrcidDbVO orcidDbVO = orcidRepository.findByConeIdAuthorAndItemId(verifiedOrcidAuthorization.getConeIdAuthor(), itemId);
            if (orcidDbVO == null) {
              orcidDbVO = new OrcidDbVO();
              orcidDbVO.setOrcidAuthorizationDbVO(verifiedOrcidAuthorization);
              orcidDbVO.setItemId(itemId);
            }
            orcidDbVO.setStatus(status.toString());
            orcidDbVO.setDate(new Date());

            if (bulkItem.isWork()) {
              if (bulkItem.getPutCode() != null) {
                orcidDbVO.setPutCode(String.valueOf(bulkItem.getPutCode()));
              }
            } else if (bulkItem.isError()) {
              if (bulkItem.getResponseCode() != null) {
                orcidDbVO.setResponseCode(String.valueOf(bulkItem.getResponseCode()));
              }
              orcidDbVO.setDeveloperMessage(bulkItem.getDeveloperMessage());
            }

            orcidRepository.saveAndFlush(orcidDbVO);
          }
        }
      }
    }
  }

  public static void sendEmail(EmailService emailService, String recipientAddress, String senderAddress, String subject, String text)
      throws TechnicalException {
    if (emailService == null) {
      return;
    }
    String smtpHost = PropertyReader.getProperty(PropertyReader.ORCID_EMAIL_MAILSERVERNAME);
    String withAuth = "false";
    String usr = null;
    String pwd = null;
    String sender = senderAddress;
    String[] recipientsAddresses = new String[] {recipientAddress};
    String[] recipientsCCAddresses = null;
    String[] recipientsBCCAddresses = null;
    String[] replyToAddresses = null;

    emailService.sendHtmlMail(smtpHost, withAuth, usr, pwd, sender, recipientsAddresses, recipientsCCAddresses, recipientsBCCAddresses,
        replyToAddresses, subject, text);
  }
}
