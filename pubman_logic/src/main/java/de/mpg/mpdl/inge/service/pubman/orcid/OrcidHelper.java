package de.mpg.mpdl.inge.service.pubman.orcid;

import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.search.ResponseBody;
import de.mpg.mpdl.inge.db.repository.OrcidAuthorizationRepository;
import de.mpg.mpdl.inge.db.repository.OrcidRepository;
import de.mpg.mpdl.inge.es.dao.impl.ElasticSearchGenericDAOImpl;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.db.valueobjects.OrcidAuthorizationDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.OrcidDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
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

  private static final int MAX_ITEMS_PER_SEARCH = Integer.parseInt(PropertyReader.getProperty(PropertyReader.ORCID_MAX_ITEMS_PER_SEARCH));


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

  public static JSONObject createWorksInOrcid(String orcid, String body, String accessToken) throws TechnicalException {
    String createUrl = PropertyReader.getProperty(PropertyReader.ORCID_API_URL) + "/" + orcid + "/works";
    logger.info("ORCID: createUrl: {} with AccessToken {}", createUrl, accessToken);

    try {
      HttpClient httpClient = HttpClient.newHttpClient();
      HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(URI.create(createUrl))
          .header("Accept", "application/vnd.orcid+json").header("Content-Type", "application/json");

      if (accessToken != null && !accessToken.isBlank()) {
        requestBuilder.header("Authorization", "Bearer " + accessToken);
      }

      HttpRequest httpRequest =
          requestBuilder.POST(HttpRequest.BodyPublishers.ofString(body != null ? body : "", StandardCharsets.UTF_8)).build();

      HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

      if (httpResponse.statusCode() >= 200 && httpResponse.statusCode() < 300) {
        String responseBody = httpResponse.body();
        JSONObject jsonObject = new JSONObject(responseBody);
        return jsonObject;
      } else {
        logger.error("ORCID: error creating ORCID work: HTTP status {} - response: {}", httpResponse.statusCode(), httpResponse.body());
        throw new TechnicalException("Error creating ORCID work: HTTP status " + httpResponse.statusCode() + " - " + httpResponse.body());
      }
    } catch (TechnicalException e) {
      throw e;
    } catch (Exception e) {
      logger.error("ORCID: Exception creating ORCID work", e);
      throw new TechnicalException("Exception creating ORCID work: " + e.getMessage(), e);
    }
  }

  public static JSONObject updateWorkInOrcid(String orcid, String body, String accessToken, String putCode) throws TechnicalException {
    String updateUrl = PropertyReader.getProperty(PropertyReader.ORCID_API_URL) + "/" + orcid + "/work/" + putCode;

    logger.info("ORCID: updateUrl: {} with AccessToken {}", updateUrl, accessToken);

    try {
      HttpClient httpClient = HttpClient.newHttpClient();
      HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(URI.create(updateUrl))
          .header("Accept", "application/vnd.orcid+json").header("Content-Type", "application/json");
      if (accessToken != null && !accessToken.isBlank()) {
        requestBuilder.header("Authorization", "Bearer " + accessToken);
      }

      HttpRequest httpRequest =
          requestBuilder.PUT(HttpRequest.BodyPublishers.ofString(body != null ? body : "", StandardCharsets.UTF_8)).build();

      HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

      if (httpResponse.statusCode() >= 200 && httpResponse.statusCode() < 300) {
        String responseBody = httpResponse.body();
        JSONObject jsonObject = (responseBody != null && !responseBody.isBlank()) ? new JSONObject(responseBody) : new JSONObject();
        return jsonObject;
      } else if (httpResponse.statusCode() == 404) {
        logger.warn("ORCID: work not found (404) for putCode {}: {}", putCode, httpResponse.body());
        String responseBody = httpResponse.body();
        JSONObject jsonObject;
        try {
          jsonObject = (responseBody != null && !responseBody.isBlank()) ? new JSONObject(responseBody) : new JSONObject();
        } catch (Exception e) {
          jsonObject = new JSONObject();
        }
        if (!jsonObject.has("response-code")) {
          jsonObject.put("response-code", 404);
        }
        if (!jsonObject.has("developer-message") && responseBody != null && !responseBody.isBlank()) {
          jsonObject.put("developer-message", responseBody);
        }
        return jsonObject;
      } else {
        logger.error("ORCID: error updating ORCID work: HTTP status {} - response: {}", httpResponse.statusCode(), httpResponse.body());
        throw new TechnicalException("Error updating ORCID work: HTTP status " + httpResponse.statusCode() + " - " + httpResponse.body());
      }
    } catch (TechnicalException e) {
      throw e;
    } catch (Exception e) {
      logger.error("ORCID: exception updating ORCID work", e);
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

    return orcidAuthorizationDbVO;
  }

  public static OrcidAuthorizationDbVO finishAuthorizationDbVO(OrcidAuthorizationDbVO orcidAuthorizationDbVO,
      OrcidAuthorizationRepository orcidAuthorizationRepository) {
    orcidAuthorizationDbVO.setStatus(OrcidAuthorizationDbVO.Status.EMAIL_SENT_BIBO.toString());
    orcidAuthorizationDbVO.setDateEmailSentBibo(new Date());

    if (orcidAuthorizationRepository != null) {
      orcidAuthorizationDbVO = orcidAuthorizationRepository.saveAndFlush(orcidAuthorizationDbVO);
    }

    return orcidAuthorizationDbVO;
  }

  public static OrcidAuthorizationDbVO updateAuthorizationDbVO(OrcidAuthorizationDbVO orcidAuthorizationDbVO, String code,
      OrcidAuthorizationRepository orcidAuthorizationRepository) {
    orcidAuthorizationDbVO.setCodeReceived(code);
    orcidAuthorizationDbVO.setStatus(OrcidAuthorizationDbVO.Status.CODE_RECEIVED.toString());
    orcidAuthorizationDbVO.setDateCodeReceived(new Date());

    orcidAuthorizationDbVO = orcidAuthorizationRepository.saveAndFlush(orcidAuthorizationDbVO);

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

    orcidAuthorizationDbVO = orcidAuthorizationRepository.saveAndFlush(orcidAuthorizationDbVO);

    return orcidAuthorizationDbVO;
  }

  public static JSONObject createOrcidQueryJsonNode(String coneIdAuthor, Date dateModified) throws IngeApplicationException {
    if (coneIdAuthor == null || coneIdAuthor.trim().isEmpty()) {
      throw new IngeApplicationException("coneIdAuthor must not be empty");
    }

    String normalizedPersonId = coneIdAuthor.trim();
    if (normalizedPersonId.contains("/persons/resource/")) {
      normalizedPersonId = normalizedPersonId.substring(normalizedPersonId.indexOf("/persons/resource/"));
    } else if (!normalizedPersonId.startsWith("/")) {
      normalizedPersonId = "/" + normalizedPersonId;
    }

    JSONObject rootNode = new JSONObject();

    // query
    JSONObject queryNode = new JSONObject();
    JSONObject boolNode = new JSONObject();
    JSONArray mustArray = new JSONArray();
    queryNode.put("bool", boolNode);
    boolNode.put("must", mustArray);
    rootNode.put("query", queryNode);

    // 1. term: metadata.creators.person.identifier.id
    JSONObject termObj = new JSONObject();
    JSONObject termInner = new JSONObject();
    termInner.put("metadata.creators.person.identifier.id", normalizedPersonId);
    termObj.put("term", termInner);
    mustArray.put(termObj);

    // 2. range: modificationDate (optional)
    if (dateModified != null) {
      JSONObject rangeObj = new JSONObject();
      JSONObject rangeInner = new JSONObject();
      JSONObject modDateObj = new JSONObject();
      modDateObj.put("gte", dateModified.getTime());
      rangeInner.put("modificationDate", modDateObj);
      rangeObj.put("range", rangeInner);
      mustArray.put(rangeObj);
    }

    // 3. bool: should: bool: must: versionState RELEASED, must_not: publicState WITHDRAWN
    JSONObject stateBoolObj = new JSONObject();
    JSONObject stateInnerBool = new JSONObject();
    JSONArray shouldArray = new JSONArray();
    stateInnerBool.put("should", shouldArray);
    stateBoolObj.put("bool", stateInnerBool);

    JSONObject shouldInnerBoolObj = new JSONObject();
    JSONObject shouldInnerBool = new JSONObject();
    JSONArray releaseMust = new JSONArray();
    JSONObject versionStateTerm = new JSONObject();
    JSONObject versionStateInner = new JSONObject();
    versionStateInner.put("versionState", "RELEASED");
    versionStateTerm.put("term", versionStateInner);
    releaseMust.put(versionStateTerm);

    JSONArray withdrawnMustNot = new JSONArray();
    JSONObject publicStateTerm = new JSONObject();
    JSONObject publicStateInner = new JSONObject();
    publicStateInner.put("publicState", "WITHDRAWN");
    publicStateTerm.put("term", publicStateInner);
    withdrawnMustNot.put(publicStateTerm);

    shouldInnerBool.put("must", releaseMust);
    shouldInnerBool.put("must_not", withdrawnMustNot);
    shouldInnerBoolObj.put("bool", shouldInnerBool);
    shouldArray.put(shouldInnerBoolObj);
    mustArray.put(stateBoolObj);

    // size, from
    rootNode.put("size", MAX_ITEMS_PER_SEARCH); // TODO: eventuell über eine Scroll Anfrage
    rootNode.put("from", 0);

    // sort
    JSONArray sortArray = new JSONArray();
    JSONObject sortObj = new JSONObject();
    JSONObject lastModObj = new JSONObject();
    lastModObj.put("order", "desc");
    sortObj.put("lastModificationDate", lastModObj);
    sortArray.put(sortObj);
    rootNode.put("sort", sortArray);

    return rootNode;
  }

  public static SearchRetrieveResponseVO<ItemVersionVO> doOrcidQuery(PubItemService pubItemService, String coneIdAuthor, Date dateModified)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    JSONObject searchSource = createOrcidQueryJsonNode(coneIdAuthor, dateModified);
    logger.info("ORCID: query: {}", searchSource);

    try {
      String searchSourceText = searchSource.toString();
      SearchRequest srequ = SearchRequest.of(sr -> sr.withJson(new StringReader(searchSourceText)));

      ResponseBody<?> responseBody = pubItemService.searchDetailed(srequ, -1, null);
      if (responseBody != null) {
        return ElasticSearchGenericDAOImpl.getSearchRetrieveResponseFromElasticSearchResponse((ResponseBody) responseBody,
            ItemVersionVO.class);
      } else {
        return new SearchRetrieveResponseVO<>();
      }
    } catch (Exception e) {
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

  public static Map<String, JSONObject> extractWorksBySourceWorkId(String orcidJson) throws IngeTechnicalException {
    if (orcidJson == null || orcidJson.trim().isEmpty()) {
      return new LinkedHashMap<>();
    }
    try {
      JSONObject rootNode = new JSONObject(orcidJson);
      return extractWorksBySourceWorkId(rootNode);
    } catch (Exception e) {
      throw new IngeTechnicalException("Error parsing ORCID JSON string", e);
    }
  }

  public static Map<String, JSONObject> extractWorksBySourceWorkId(JSONObject rootNode) {
    Map<String, JSONObject> worksBySourceWorkId = new LinkedHashMap<>();
    if (rootNode == null) {
      return worksBySourceWorkId;
    }

    if (rootNode.has("bulk")) {
      JSONArray bulkArray = rootNode.optJSONArray("bulk");
      if (bulkArray != null) {
        for (int i = 0; i < bulkArray.length(); i++) {
          JSONObject itemWrapper = bulkArray.optJSONObject(i);
          if (itemWrapper != null) {
            JSONObject workNode = itemWrapper.has("work") ? itemWrapper.optJSONObject("work") : itemWrapper;
            if (workNode != null) {
              String sourceWorkId = getSourceWorkId(workNode);
              if (sourceWorkId != null && !sourceWorkId.isEmpty()) {
                worksBySourceWorkId.put(sourceWorkId, workNode);
              }
            }
          }
        }
      }
    } else if (rootNode.has("work")) {
      JSONObject workNode = rootNode.optJSONObject("work");
      if (workNode != null) {
        String sourceWorkId = getSourceWorkId(workNode);
        if (sourceWorkId != null && !sourceWorkId.isEmpty()) {
          worksBySourceWorkId.put(sourceWorkId, workNode);
        }
      }
    } else if (rootNode.has("external-ids")) {
      String sourceWorkId = getSourceWorkId(rootNode);
      if (sourceWorkId != null && !sourceWorkId.isEmpty()) {
        worksBySourceWorkId.put(sourceWorkId, rootNode);
      }
    }

    return worksBySourceWorkId;
  }

  public static Map<String, JSONObject> getWorksBySourceWorkId(String orcidJson) throws IngeTechnicalException {
    return extractWorksBySourceWorkId(orcidJson);
  }

  public static String getSourceWorkId(JSONObject workNode) {
    if (workNode == null) {
      return null;
    }
    JSONObject extIds = workNode.optJSONObject("external-ids");
    if (extIds == null) {
      return null;
    }
    Object extIdObj = extIds.opt("external-id");
    if (extIdObj instanceof JSONArray extIdList) {
      for (int i = 0; i < extIdList.length(); i++) {
        JSONObject extId = extIdList.optJSONObject(i);
        if (extId != null) {
          String type = extId.optString("external-id-type", null);
          if ("source-work-id".equalsIgnoreCase(type)) {
            return extId.optString("external-id-value", null);
          }
        }
      }
    } else if (extIdObj instanceof JSONObject extId) {
      String type = extId.optString("external-id-type", null);
      if ("source-work-id".equalsIgnoreCase(type)) {
        return extId.optString("external-id-value", null);
      }
    }
    return null;
  }

  public static void setPutCode(JSONObject workNode, Object putCode) {
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

  public static String convertWorksToOrcidJson(Map<String, JSONObject> worksBySourceWorkId) throws IngeTechnicalException {
    return convertWorksToOrcidJson(worksBySourceWorkId != null ? worksBySourceWorkId.values() : null);
  }

  public static String convertWorksToOrcidJson(Collection<JSONObject> works) throws IngeTechnicalException {
    try {
      JSONObject root = new JSONObject();
      JSONArray bulkArray = new JSONArray();
      if (works != null) {
        for (JSONObject work : works) {
          if (work != null) {
            JSONObject itemWrapper = new JSONObject();
            itemWrapper.put("work", work);
            bulkArray.put(itemWrapper);
          }
        }
      }
      root.put("bulk", bulkArray);
      return root.toString(2);
    } catch (Exception e) {
      throw new IngeTechnicalException("Error converting works to ORCID JSON string", e);
    }
  }

  public static String updatePutCodesInOrcidJson(String orcidJson, Map<String, ?> putCodesBySourceWorkId) throws IngeTechnicalException {
    if (orcidJson == null || orcidJson.trim().isEmpty() || putCodesBySourceWorkId == null || putCodesBySourceWorkId.isEmpty()) {
      return orcidJson;
    }

    try {
      JSONObject root = new JSONObject(orcidJson);
      Map<String, JSONObject> worksMap = extractWorksBySourceWorkId(root);
      for (Map.Entry<String, ?> entry : putCodesBySourceWorkId.entrySet()) {
        JSONObject workNode = worksMap.get(entry.getKey());
        if (workNode != null && entry.getValue() != null) {
          setPutCode(workNode, entry.getValue());
        }
      }
      return root.toString(2);
    } catch (Exception e) {
      throw new IngeTechnicalException("Error updating put-codes in ORCID JSON", e);
    }
  }

  public static List<OrcidResponse> parseOrcidResponses(JSONObject orcidAnswer) {
    if (orcidAnswer == null) {
      return Collections.emptyList();
    }

    if (orcidAnswer.has("bulk")) {
      JSONArray bulkArray = orcidAnswer.optJSONArray("bulk");
      if (bulkArray == null) {
        return Collections.emptyList();
      }

      List<OrcidResponse> results = new ArrayList<>(bulkArray.length());

      for (int i = 0; i < bulkArray.length(); i++) {
        JSONObject node = bulkArray.optJSONObject(i);
        if (node == null) {
          continue;
        }

        if (node.has("work")) {
          JSONObject workObj = node.optJSONObject("work");
          if (workObj != null) {
            OrcidResponse item = new OrcidResponse(OrcidResponse.Type.WORK, i, workObj);
            if (workObj.has("put-code") && !workObj.isNull("put-code")) {
              item.setPutCode(workObj.optLong("put-code"));
            }
            results.add(item);
          }
        } else if (node.has("error")) {
          JSONObject errorObj = node.optJSONObject("error");
          if (errorObj != null) {
            results.add(parseOrcidErrorAnswer(errorObj, i));
          }
        }
      }

      return results;
    }

    if (orcidAnswer.has("error")) {
      JSONObject errorObj = orcidAnswer.optJSONObject("error");
      if (errorObj != null) {
        return Collections.singletonList(parseOrcidErrorAnswer(errorObj, 0));
      }
    }

    if (orcidAnswer.has("response-code") || orcidAnswer.has("error-code") || orcidAnswer.has("developer-message")
        || orcidAnswer.has("user-message")) {
      return Collections.singletonList(parseOrcidErrorAnswer(orcidAnswer, 0));
    }

    if (orcidAnswer.has("work")) {
      JSONObject workObj = orcidAnswer.optJSONObject("work");
      if (workObj != null) {
        OrcidResponse item = new OrcidResponse(OrcidResponse.Type.WORK, 0, workObj);
        if (workObj.has("put-code") && !workObj.isNull("put-code")) {
          item.setPutCode(workObj.optLong("put-code"));
        }
        return Collections.singletonList(item);
      }
    }

    if (orcidAnswer.has("put-code") && !orcidAnswer.isNull("put-code")) {
      OrcidResponse item = new OrcidResponse(OrcidResponse.Type.WORK, 0, orcidAnswer);
      item.setPutCode(orcidAnswer.optLong("put-code"));
      return Collections.singletonList(item);
    }

    return Collections.emptyList();
  }

  private static OrcidResponse parseOrcidErrorAnswer(JSONObject errorObj, int index) {
    OrcidResponse orcidResponse = new OrcidResponse(OrcidResponse.Type.ERROR, index, errorObj);
    if (errorObj.has("response-code") && !errorObj.isNull("response-code")) {
      orcidResponse.setResponseCode(errorObj.optInt("response-code"));
    }
    if (errorObj.has("error-code") && !errorObj.isNull("error-code")) {
      orcidResponse.setErrorCode(errorObj.optInt("error-code"));
    }
    orcidResponse.setUserMessage(errorObj.optString("user-message", null));
    orcidResponse.setDeveloperMessage(errorObj.optString("developer-message", null));
    orcidResponse.setMoreInfo(errorObj.optString("more-info", null));

    return orcidResponse;
  }

  public static OrcidDbVO saveOrcidDbRecord(OrcidRepository orcidRepository, OrcidAuthorizationDbVO verifiedOrcidAuthorization,
      String itemId, String putCode, OrcidResponse orcidResponse, OrcidDbVO.Status status) {
    return saveOrcidDbRecord(orcidRepository, verifiedOrcidAuthorization, itemId, putCode, orcidResponse, status, null);
  }

  public static OrcidDbVO saveOrcidDbRecord(OrcidRepository orcidRepository, OrcidAuthorizationDbVO verifiedOrcidAuthorization,
      String itemId, String putCode, OrcidResponse orcidResponse, OrcidDbVO.Status status, Map<String, OrcidDbVO> existingOrcidMap) {
    if (orcidRepository == null || itemId == null) {
      return null;
    }
    OrcidDbVO orcidDbVO = null;
    if (existingOrcidMap != null) {
      orcidDbVO = existingOrcidMap.get(itemId);
    } else {
      String coneIdAuthor = verifiedOrcidAuthorization != null ? verifiedOrcidAuthorization.getConeIdAuthor() : null;
      orcidDbVO = orcidRepository.findByConeIdAuthorAndItemId(coneIdAuthor, itemId);
    }
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
    } else if (orcidResponse != null && orcidResponse.isWork() && orcidResponse.getPutCode() != null) {
      orcidDbVO.setPutCode(String.valueOf(orcidResponse.getPutCode()));
    } else {
      orcidDbVO.setPutCode(null);
    }

    if (orcidResponse != null) {
      if (orcidResponse.getResponseCode() != null) {
        orcidDbVO.setResponseCode(String.valueOf(orcidResponse.getResponseCode()));
      }
      if (orcidResponse.getDeveloperMessage() != null) {
        orcidDbVO.setDeveloperMessage(orcidResponse.getDeveloperMessage());
      }
    }

    OrcidDbVO saved = orcidRepository.saveAndFlush(orcidDbVO);
    if (existingOrcidMap != null) {
      existingOrcidMap.put(itemId, saved);
    }
    return saved;
  }

  public static OrcidDbVO saveOrcidDbRecord(OrcidRepository orcidRepository, OrcidAuthorizationDbVO verifiedOrcidAuthorization,
      String itemId, String putCode, JSONObject answer, OrcidDbVO.Status status) {
    return saveOrcidDbRecord(orcidRepository, verifiedOrcidAuthorization, itemId, putCode, answer, status, null);
  }

  public static OrcidDbVO saveOrcidDbRecord(OrcidRepository orcidRepository, OrcidAuthorizationDbVO verifiedOrcidAuthorization,
      String itemId, String putCode, JSONObject answer, OrcidDbVO.Status status, Map<String, OrcidDbVO> existingOrcidMap) {
    List<OrcidResponse> orcidResponse = parseOrcidResponses(answer);
    if (!orcidResponse.isEmpty()) {
      OrcidResponse item = orcidResponse.get(0);
      return saveOrcidDbRecord(orcidRepository, verifiedOrcidAuthorization, itemId, putCode, item, status, existingOrcidMap);
    }

    OrcidDbVO orcidDbVO = null;
    if (existingOrcidMap != null) {
      orcidDbVO = existingOrcidMap.get(itemId);
    } else {
      String coneIdAuthor = verifiedOrcidAuthorization != null ? verifiedOrcidAuthorization.getConeIdAuthor() : null;
      orcidDbVO = orcidRepository.findByConeIdAuthorAndItemId(coneIdAuthor, itemId);
    }
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
    } else {
      orcidDbVO.setPutCode(null);
    }

    if (answer != null) {
      if (answer.has("response-code") && !answer.isNull("response-code")) {
        orcidDbVO.setResponseCode(String.valueOf(answer.get("response-code")));
      }
      if (answer.has("developer-message") && !answer.isNull("developer-message")) {
        orcidDbVO.setDeveloperMessage(answer.getString("developer-message"));
      }
    }

    OrcidDbVO saved = orcidRepository.saveAndFlush(orcidDbVO);
    if (existingOrcidMap != null) {
      existingOrcidMap.put(itemId, saved);
    }
    return saved;
  }

  public static void saveOrcidDbRecords(OrcidRepository orcidRepository, OrcidAuthorizationDbVO verifiedOrcidAuthorization,
      SearchRetrieveResponseVO<ItemVersionVO> searchResult, List<OrcidResponse> orcidResponses, OrcidDbVO.Status status) {
    saveOrcidDbRecords(orcidRepository, verifiedOrcidAuthorization, searchResult, orcidResponses, status, null);
  }

  public static void saveOrcidDbRecords(OrcidRepository orcidRepository, OrcidAuthorizationDbVO verifiedOrcidAuthorization,
      SearchRetrieveResponseVO<ItemVersionVO> searchResult, List<OrcidResponse> orcidResponses, OrcidDbVO.Status status,
      Map<String, OrcidDbVO> existingOrcidMap) {
    if (searchResult != null && searchResult.getRecords() != null && !searchResult.getRecords().isEmpty()) {
      for (int i = 0; i < searchResult.getRecords().size(); i++) {
        SearchRetrieveRecordVO<ItemVersionVO> record = searchResult.getRecords().get(i);
        ItemVersionVO itemVersionVO = record != null ? record.getData() : null;
        if (itemVersionVO != null && itemVersionVO.getObjectId() != null) {
          String itemId = itemVersionVO.getObjectId();
          OrcidResponse orcidResponse = (orcidResponses != null && i < orcidResponses.size()) ? orcidResponses.get(i) : null;
          if (orcidResponse != null) {
            OrcidDbVO orcidDbVO = null;
            if (existingOrcidMap != null) {
              orcidDbVO = existingOrcidMap.get(itemId);
            } else {
              orcidDbVO = orcidRepository.findByConeIdAuthorAndItemId(verifiedOrcidAuthorization.getConeIdAuthor(), itemId);
            }
            if (orcidDbVO == null) {
              orcidDbVO = new OrcidDbVO();
              orcidDbVO.setOrcidAuthorizationDbVO(verifiedOrcidAuthorization);
              orcidDbVO.setItemId(itemId);
            }
            orcidDbVO.setStatus(status.toString());
            orcidDbVO.setDate(new Date());

            if (orcidResponse.isWork()) {
              if (orcidResponse.getPutCode() != null) {
                orcidDbVO.setPutCode(String.valueOf(orcidResponse.getPutCode()));
              }
            } else if (orcidResponse.isError()) {
              if (orcidResponse.getResponseCode() != null) {
                orcidDbVO.setResponseCode(String.valueOf(orcidResponse.getResponseCode()));
              }
              orcidDbVO.setDeveloperMessage(orcidResponse.getDeveloperMessage());
            }

            OrcidDbVO saved = orcidRepository.saveAndFlush(orcidDbVO);
            if (existingOrcidMap != null) {
              existingOrcidMap.put(itemId, saved);
            }

            logger.info("ORCID: {}", saved.toInfoString());
          }
        }
      }
    }
  }

  public static void sendEmail(EmailService emailService, String recipientAddress, String senderAddress, String replyToAddress,
      String subject, String text) throws TechnicalException {
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
    String[] replyToAddresses = (replyToAddress != null ? new String[] {replyToAddress} : null);

    emailService.sendHtmlMail(smtpHost, withAuth, usr, pwd, sender, recipientsAddresses, recipientsCCAddresses, recipientsBCCAddresses,
        replyToAddresses, subject, text);
  }
}
