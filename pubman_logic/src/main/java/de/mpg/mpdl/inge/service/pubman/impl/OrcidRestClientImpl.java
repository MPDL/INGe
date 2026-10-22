package de.mpg.mpdl.inge.service.pubman.impl;

import de.mpg.mpdl.inge.db.repository.OrcidAuthorizationRepository;
import de.mpg.mpdl.inge.model.db.valueobjects.OrcidAuthorizationDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.service.pubman.orcid.OrcidResponse;
import de.mpg.mpdl.inge.service.pubman.orcid.OrcidRestClient;
import de.mpg.mpdl.inge.service.pubman.orcid.OrcidTransformationService;
import de.mpg.mpdl.inge.util.PropertyReader;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrcidRestClientImpl implements OrcidRestClient {

  private static final Logger logger = LogManager.getLogger(OrcidRestClientImpl.class);

  private final HttpClient httpClient;
  private final OrcidTransformationService orcidTransformationService;

  @Autowired
  public OrcidRestClientImpl(OrcidTransformationService orcidTransformationService) {
    this(HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(30)).followRedirects(HttpClient.Redirect.NORMAL).build(),
        orcidTransformationService);
  }

  public OrcidRestClientImpl(HttpClient httpClient, OrcidTransformationService orcidTransformationService) {
    this.httpClient = httpClient != null ? httpClient
        : HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(30)).followRedirects(HttpClient.Redirect.NORMAL).build();
    this.orcidTransformationService = orcidTransformationService;
  }

  @Override
  public OrcidAuthorizationDbVO requestTokenAndOrcid(OrcidAuthorizationDbVO orcidAuthorizationDbVO, String code,
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
      HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(tokenUrl)).header("Accept", "application/json")
          .header("Content-Type", "application/x-www-form-urlencoded")
          .POST(HttpRequest.BodyPublishers.ofString(formBody, StandardCharsets.UTF_8)).build();

      HttpResponse<String> httpResponse = this.httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

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

  private OrcidAuthorizationDbVO updateAuthorizationDbVO(OrcidAuthorizationDbVO orcidAuthorizationDbVO, String accessToken,
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

    return orcidAuthorizationDbVO;
  }

  @Override
  public JSONObject createWorksInOrcid(String orcid, String body, String accessToken) throws TechnicalException {
    String createUrl = PropertyReader.getProperty(PropertyReader.ORCID_API_URL) + "/" + orcid + "/works";
    logger.info("ORCID: createUrl: {}", createUrl);

    try {
      HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(URI.create(createUrl))
          .header("Accept", "application/vnd.orcid+json").header("Content-Type", "application/json");

      if (accessToken != null && !accessToken.isBlank()) {
        requestBuilder.header("Authorization", "Bearer " + accessToken);
      }

      HttpRequest httpRequest =
          requestBuilder.POST(HttpRequest.BodyPublishers.ofString(body != null ? body : "", StandardCharsets.UTF_8)).build();

      HttpResponse<String> httpResponse = this.httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

      if (httpResponse.statusCode() >= 200 && httpResponse.statusCode() < 300) {
        String responseBody = httpResponse.body();
        return new JSONObject(responseBody);
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

  @Override
  public JSONObject updateWorkInOrcid(String orcid, String body, String accessToken, String putCode) throws TechnicalException {
    String updateUrl = PropertyReader.getProperty(PropertyReader.ORCID_API_URL) + "/" + orcid + "/work/" + putCode;

    logger.info("ORCID: updateUrl: {} with AccessToken {}", updateUrl, accessToken);

    try {
      HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(URI.create(updateUrl))
          .header("Accept", "application/vnd.orcid+json").header("Content-Type", "application/json");
      if (accessToken != null && !accessToken.isBlank()) {
        requestBuilder.header("Authorization", "Bearer " + accessToken);
      }

      HttpRequest httpRequest =
          requestBuilder.PUT(HttpRequest.BodyPublishers.ofString(body != null ? body : "", StandardCharsets.UTF_8)).build();

      HttpResponse<String> httpResponse = this.httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

      if (httpResponse.statusCode() >= 200 && httpResponse.statusCode() < 300) {
        String responseBody = httpResponse.body();
        return (responseBody != null && !responseBody.isBlank()) ? new JSONObject(responseBody) : new JSONObject();
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
      logger.error("ORCID: Exception updating ORCID work", e);
      throw new TechnicalException("Exception updating ORCID work: " + e.getMessage(), e);
    }
  }

  @Override
  public List<OrcidResponse> parseOrcidResponses(JSONObject orcidAnswer) {
    if (this.orcidTransformationService != null) {
      return this.orcidTransformationService.parseOrcidResponses(orcidAnswer);
    }
    return Collections.emptyList();
  }

  @Override
  public boolean is401Error(Throwable e) {
    Throwable current = e;
    while (current != null) {
      String msg = current.getMessage();
      if (msg != null && (msg.contains("401") || msg.toLowerCase().contains("unauthorized") || msg.contains("invalid_token"))) {
        return true;
      }
      current = current.getCause();
    }
    return false;
  }

  @Override
  public boolean contains401Error(List<OrcidResponse> responses) {
    if (responses == null) {
      return false;
    }
    for (OrcidResponse resp : responses) {
      if (resp != null && resp.isError()) {
        if (resp.getResponseCode() != null && resp.getResponseCode() == 401) {
          return true;
        }
        if (resp.getUserMessage() != null && resp.getUserMessage().contains("401")) {
          return true;
        }
        if (resp.getDeveloperMessage() != null && (resp.getDeveloperMessage().contains("401")
            || resp.getDeveloperMessage().toLowerCase().contains("unauthorized") || resp.getDeveloperMessage().contains("invalid_token"))) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public Map<String, JSONObject> getWorksBySourceWorkId(String orcidJson) throws IngeTechnicalException {
    if (this.orcidTransformationService != null) {
      return this.orcidTransformationService.getWorksBySourceWorkId(orcidJson);
    }
    return new LinkedHashMap<>();
  }

  @Override
  public void setPutCode(JSONObject workNode, Object putCode) {
    if (this.orcidTransformationService != null) {
      this.orcidTransformationService.setPutCode(workNode, putCode);
    }
  }

}
