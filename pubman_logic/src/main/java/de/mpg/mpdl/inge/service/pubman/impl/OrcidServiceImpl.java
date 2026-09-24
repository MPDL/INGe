package de.mpg.mpdl.inge.service.pubman.impl;

import de.mpg.mpdl.inge.db.repository.OrcidAuthorizationRepository;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.OrcidAuthorizationDbVO;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.EmailService;
import de.mpg.mpdl.inge.service.pubman.OrcidService;
import de.mpg.mpdl.inge.util.PropertyReader;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class OrcidServiceImpl implements OrcidService {

  private static final Logger logger = LogManager.getLogger(OrcidServiceImpl.class);

  private final AuthorizationService authorizationService;
  private final OrcidAuthorizationRepository orcidAuthorizationRepository;
  private final EmailService emailService;

  public OrcidServiceImpl(AuthorizationService authorizationService, OrcidAuthorizationRepository orcidAuthorizationRepository,
      EmailService emailService) {
    this.authorizationService = authorizationService;
    this.orcidAuthorizationRepository = orcidAuthorizationRepository;
    this.emailService = emailService;
  }

  @Override
  public OrcidAuthorizationDbVO sendEmailLink(String token, String coneIdAuthor, String orcidAuthor, String nameAuthor, String emailBibo,
      String emailAuthor) throws AuthenticationException, IngeApplicationException, TechnicalException {
    AccountUserDbVO accountUserDbVO = getUser(token);

    OrcidAuthorizationDbVO orcidAuthorizationDbVO = this.orcidAuthorizationRepository.findByConeIdAuthor(coneIdAuthor);

    if (orcidAuthorizationDbVO != null) {
      orcidAuthorizationDbVO.setErrorCodes(OrcidAuthorizationDbVO.ErrorCode.ERROR_CONE_ID_ALREADY_EXITS.toString());
      return orcidAuthorizationDbVO;
    }

    String scopeRequested = PropertyReader.getProperty(PropertyReader.ORCID_SCOPE);
    String genSecret = generateSecret();
    String redirectUri = PropertyReader.getProperty(PropertyReader.ORCID_REDIRECT_URL_REST)
        + PropertyReader.getProperty(PropertyReader.ORCID_REDIRECT_URL_URL) + "?secret=" + genSecret;
    String emailLink = createEmailLink(scopeRequested, redirectUri);
    String userIdBibo = accountUserDbVO.getObjectId();

    orcidAuthorizationDbVO = createOrcidAuthorizationDbVO(userIdBibo, coneIdAuthor, orcidAuthor, nameAuthor, genSecret, scopeRequested,
        emailLink, emailBibo, emailAuthor, redirectUri);

    String subject = PropertyReader.getProperty(PropertyReader.ORCID_EMAIL_SUBJECT_FOR_LINK);
    String text = PropertyReader.getProperty(PropertyReader.ORCID_EMAIL_TEXT_FOR_LINK) + "\n" + emailLink;

    sendEmail(emailBibo, emailBibo, subject, text);

    orcidAuthorizationDbVO.setDateEmailSentLink(new Date());
    orcidAuthorizationDbVO.setStatus(OrcidAuthorizationDbVO.Status.EMAIL_SENT_LINK);

    orcidAuthorizationDbVO = this.orcidAuthorizationRepository.saveAndFlush(orcidAuthorizationDbVO);

    return orcidAuthorizationDbVO;
  }

  @Override
  public OrcidAuthorizationDbVO createOrcidAuthorization(String secret, String code) throws IngeApplicationException, TechnicalException {
    OrcidAuthorizationDbVO orcidAuthorizationDbVO = this.orcidAuthorizationRepository.findBySecret(secret);

    if (orcidAuthorizationDbVO == null) {
      throw new IngeApplicationException("OrcidAuthorizationDbVO not found for secret: " + secret);
    }

    orcidAuthorizationDbVO = updateAuthorizationDbVO(orcidAuthorizationDbVO, code);
    orcidAuthorizationDbVO = requestTokenAndOrcid(orcidAuthorizationDbVO, code);

    String emailBibo = orcidAuthorizationDbVO.getEmailBibo();
    String emailSender = PropertyReader.getProperty(PropertyReader.ORCID_EMAIL_SENDER_FOR_BIBO);
    String subject = PropertyReader.getProperty(PropertyReader.ORCID_EMAIL_SUBJECT_FOR_BIBO);
    String text = PropertyReader.getProperty(PropertyReader.ORCID_EMAIL_TEXT_FOR_BIBO);

    sendEmail(emailSender, emailBibo, subject, text);

    orcidAuthorizationDbVO = finishAuthorizationDbVO(orcidAuthorizationDbVO);

    return orcidAuthorizationDbVO;
  }

  public OrcidAuthorizationDbVO requestTokenAndOrcid(OrcidAuthorizationDbVO orcidAuthorizationDbVO, String code) throws TechnicalException {

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
        orcidAuthorizationDbVO = updateAuthorizationDbVO(orcidAuthorizationDbVO, jsonObject.getString("access_token"),
            jsonObject.getString("refresh_token"), jsonObject.getString("scope"), jsonObject.getString("name"),
            jsonObject.getString("orcid"), jsonObject.getString("token_type"), String.valueOf(jsonObject.get("expires_in")));

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

  public OrcidAuthorizationDbVO createOrcidAuthorizationDbVO(String userIdBibo, String coneIdAuthor, String orcidAuthor, String nameAuthor,
      String secret, String scopeRequested, String emailLink, String emailBibo, String emailAuthor, String redirectUri) {

    OrcidAuthorizationDbVO orcidAuthorizationDbVO = new OrcidAuthorizationDbVO(userIdBibo, coneIdAuthor, orcidAuthor, nameAuthor, secret,
        scopeRequested, emailLink, emailBibo, emailAuthor, redirectUri);
    orcidAuthorizationDbVO = this.orcidAuthorizationRepository.saveAndFlush(orcidAuthorizationDbVO);

    logger.info("Created new orcidAuthorizationDbVO: " + orcidAuthorizationDbVO);

    return orcidAuthorizationDbVO;
  }

  public OrcidAuthorizationDbVO finishAuthorizationDbVO(OrcidAuthorizationDbVO orcidAuthorizationDbVO) {
    orcidAuthorizationDbVO.setStatus(OrcidAuthorizationDbVO.Status.EMAIL_SENT_BIBO);
    orcidAuthorizationDbVO.setDateEmailSentBibo(new Date());

    orcidAuthorizationDbVO = this.orcidAuthorizationRepository.saveAndFlush(orcidAuthorizationDbVO);

    logger.info("Finished orcidAuthorizationDbVO: " + orcidAuthorizationDbVO);

    return orcidAuthorizationDbVO;
  }

  public OrcidAuthorizationDbVO updateAuthorizationDbVO(OrcidAuthorizationDbVO orcidAuthorizationDbVO, String code) {
    orcidAuthorizationDbVO.setCodeReceived(code);
    orcidAuthorizationDbVO.setStatus(OrcidAuthorizationDbVO.Status.CODE_RECEIVED);
    orcidAuthorizationDbVO.setDateCodeReceived(new Date());

    orcidAuthorizationDbVO = this.orcidAuthorizationRepository.saveAndFlush(orcidAuthorizationDbVO);

    logger.info("Udated orcidAuthorizationDbVO: " + orcidAuthorizationDbVO);

    return orcidAuthorizationDbVO;
  }

  public OrcidAuthorizationDbVO updateAuthorizationDbVO(OrcidAuthorizationDbVO orcidAuthorizationDbVO, String accessToken,
      String refreshToken, String scope, String name, String orcid, String tokenType, String expiresIn) {
    orcidAuthorizationDbVO.setAccessToken(accessToken);
    orcidAuthorizationDbVO.setRefreshToken(refreshToken);
    orcidAuthorizationDbVO.setScope(scope);
    orcidAuthorizationDbVO.setName(name);
    orcidAuthorizationDbVO.setOrcid(orcid);
    orcidAuthorizationDbVO.setTokenType(tokenType);
    orcidAuthorizationDbVO.setExpiresIn(expiresIn);
    orcidAuthorizationDbVO.setStatus(OrcidAuthorizationDbVO.Status.TOKEN_RECEIVED);
    orcidAuthorizationDbVO.setDateTokenReceived(new Date());
    orcidAuthorizationDbVO.setVerified(true);

    StringBuilder sb = new StringBuilder();
    if (!orcidAuthorizationDbVO.getNameAuthor().equalsIgnoreCase(name)) {
      sb.append(OrcidAuthorizationDbVO.ErrorCode.WARN_DIFFERENT_NAME.toString());
    }
    if (!orcidAuthorizationDbVO.getOrcidAuthor().equalsIgnoreCase(orcid)) {
      if (!sb.isEmpty()) {
        sb.append(" " + OrcidAuthorizationDbVO.ErrorCode.WARN_DIFFERENT_ORCID.toString());
      } else {
        sb.append(OrcidAuthorizationDbVO.ErrorCode.WARN_DIFFERENT_ORCID.toString());
      }
    }

    if (!sb.isEmpty()) {
      orcidAuthorizationDbVO.setErrorCodes(sb.toString());
    }

    orcidAuthorizationDbVO = this.orcidAuthorizationRepository.saveAndFlush(orcidAuthorizationDbVO);

    logger.info("Udated orcidAuthorizationDbVO: " + orcidAuthorizationDbVO);

    return orcidAuthorizationDbVO;
  }

  String createEmailLink(String scopeRequested, String redirectUri) {
    //    https://sandbox.orcid.org/oauth/authorize?scope=/read-limited%20/person/update%20/activities/update%20/authenticate&response_type=code&redirect_uri=https://pure.mpg.de/rest/orcid/createOrcidAuthentication?secret=xxx&client_id=xxx

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

  String generateSecret() {
    return UUID.randomUUID().toString();
  }

  private AccountUserDbVO getUser(String token) throws AuthenticationException, IngeApplicationException {
    AccountUserDbVO accountUserDbVO = this.authorizationService.getUserAccountFromToken(token);

    return accountUserDbVO;
  }

  private void sendEmail(String recipientAdress, String senderAdress, String subject, String text) throws TechnicalException {
    String smtpHost = PropertyReader.getProperty(PropertyReader.ORCID_EMAIL_MAILSERVERNAME);
    String withAuth = "false";
    String usr = null;
    String pwd = null;
    String senderAddress = senderAdress;
    String[] recipientsAdresses = new String[] {recipientAdress};
    String[] recipientsCCAdresses = null;
    String[] recipientsBCCAdresses = null;
    String[] replyToAdresses = null;

    this.emailService.sendHtmlMail(smtpHost, withAuth, usr, pwd, senderAddress, recipientsAdresses, recipientsCCAdresses,
        recipientsBCCAdresses, replyToAdresses, subject, text);
  }

  public static void main(String[] args) {
    OrcidServiceImpl orcidService = new OrcidServiceImpl(null, null, null);

    String scopeRequested = PropertyReader.getProperty(PropertyReader.ORCID_SCOPE);
    String genSecret = orcidService.generateSecret();
    String redirectUri = PropertyReader.getProperty(PropertyReader.ORCID_REDIRECT_URL_REST)
        + PropertyReader.getProperty(PropertyReader.ORCID_REDIRECT_URL_URL) + "?secret=" + genSecret;
    String emailLink = orcidService.createEmailLink(scopeRequested, redirectUri);

    System.out.println(emailLink);
  }
}
