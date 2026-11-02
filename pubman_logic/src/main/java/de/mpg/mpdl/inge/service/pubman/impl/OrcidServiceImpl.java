package de.mpg.mpdl.inge.service.pubman.impl;

import de.mpg.mpdl.inge.db.repository.OrcidAuthorizationRepository;
import de.mpg.mpdl.inge.inge_validation.OrcidValidatingService;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.OrcidAuthorizationDbVO;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.OrcidService;
import de.mpg.mpdl.inge.service.pubman.orcid.OrcidAuthorizationService;
import de.mpg.mpdl.inge.service.pubman.orcid.OrcidEmailService;
import de.mpg.mpdl.inge.service.pubman.orcid.OrcidRestClient;
import de.mpg.mpdl.inge.service.pubman.orcid.OrcidSyncService;
import de.mpg.mpdl.inge.util.PropertyReader;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class OrcidServiceImpl implements OrcidService {

  private static final Logger logger = LogManager.getLogger(OrcidServiceImpl.class);

  private final AuthorizationService authorizationService;
  private final OrcidAuthorizationRepository orcidAuthorizationRepository;
  private final OrcidEmailService orcidEmailService;
  private final OrcidValidatingService orcidValidatingService;
  private final OrcidSyncService orcidSyncService;
  private final OrcidRestClient orcidRestClient;
  private final OrcidAuthorizationService orcidAuthorizationService;

  @Autowired
  public OrcidServiceImpl(AuthorizationService authorizationService, OrcidAuthorizationRepository orcidAuthorizationRepository,
      OrcidValidatingService orcidValidatingService, OrcidSyncService orcidSyncService, OrcidRestClient orcidRestClient,
      OrcidAuthorizationService orcidAuthorizationService, OrcidEmailService orcidEmailService) {
    this.authorizationService = authorizationService;
    this.orcidAuthorizationRepository = orcidAuthorizationRepository;
    this.orcidValidatingService = orcidValidatingService;
    this.orcidRestClient = orcidRestClient;
    this.orcidSyncService = orcidSyncService;
    this.orcidAuthorizationService = orcidAuthorizationService;
    this.orcidEmailService = orcidEmailService;
  }

  @Override
  public OrcidAuthorizationDbVO getOrcidAuthorization(String secret, String code, String errorDescription)
      throws IngeApplicationException, TechnicalException {
    String secret_ = secret;
    String error = null;
    if (secret != null && secret.contains("?error=")) {
      int errorIndex = secret.indexOf("?error=");
      secret_ = secret.substring(0, errorIndex);
      error = secret.substring(errorIndex + "?error=".length());
    }

    OrcidAuthorizationDbVO orcidAuthorizationDbVO = this.orcidAuthorizationRepository.findBySecret(secret_);

    if (orcidAuthorizationDbVO == null) {
      throw new IngeApplicationException("OrcidAuthorizationDbVO not found for secret: " + secret_);
    }

    if (error != null) {
      orcidAuthorizationDbVO.setDateError(new Date());
      if (error.equals("access_denied")) {
        orcidAuthorizationDbVO.setErrorCodes(OrcidAuthorizationDbVO.Status.ACCESS_DENIED.toString());
        orcidAuthorizationDbVO.setStatus(OrcidAuthorizationDbVO.Status.ACCESS_DENIED.toString());
        String emailAuthor = orcidAuthorizationDbVO.getEmailAuthor();
        String emailSender = PropertyReader.getProperty(PropertyReader.ORCID_EMAIL_SENDER);
        String subjectAuthor = PropertyReader.getProperty(PropertyReader.ORCID_EMAIL_SUBJECT_NO_CONSENT_FOR_AUTHOR);
        String textAuthor = this.orcidEmailService.createOrcidEmailNoConsentAuthorText(orcidAuthorizationDbVO.getNameAuthor(),
            orcidAuthorizationDbVO.getOrcidAuthor(), orcidAuthorizationDbVO.getEmailLink());
        this.orcidEmailService.sendEmail(emailAuthor, emailSender, null, subjectAuthor, textAuthor);

        String emailBibo = orcidAuthorizationDbVO.getEmailBibo();
        String subjectBibo = PropertyReader.getProperty(PropertyReader.ORCID_EMAIL_SUBJECT_NO_CONSENT_FOR_BIBO);
        String textBibo = this.orcidEmailService.createOrcidEmailNoConsentBiboText(orcidAuthorizationDbVO.getNameAuthor());
        this.orcidEmailService.sendEmail(emailBibo, emailSender, null, subjectBibo, textBibo);
      } else {
        orcidAuthorizationDbVO.setErrorCodes(errorDescription);
        orcidAuthorizationDbVO.setStatus(OrcidAuthorizationDbVO.Status.ERROR.toString());
      }
      orcidAuthorizationDbVO = this.orcidAuthorizationRepository.saveAndFlush(orcidAuthorizationDbVO);
      return orcidAuthorizationDbVO;
    }

    orcidAuthorizationDbVO = this.orcidAuthorizationService.updateAuthorizationDbVO(orcidAuthorizationDbVO, code);
    orcidAuthorizationDbVO = this.orcidRestClient.requestTokenAndOrcid(orcidAuthorizationDbVO, code, this.orcidAuthorizationRepository);

    String emailAuthor = orcidAuthorizationDbVO.getEmailAuthor();
    String emailSender = PropertyReader.getProperty(PropertyReader.ORCID_EMAIL_SENDER);
    String subjectAuthor = PropertyReader.getProperty(PropertyReader.ORCID_EMAIL_SUBJECT_CONSENT_FOR_AUTHOR);
    String textAuthor = this.orcidEmailService.createOrcidEmailConsentAuthorText(orcidAuthorizationDbVO.getName());
    this.orcidEmailService.sendEmail(emailAuthor, emailSender, null, subjectAuthor, textAuthor);

    String emailBibo = orcidAuthorizationDbVO.getEmailBibo();
    String subjectBibo = PropertyReader.getProperty(PropertyReader.ORCID_EMAIL_SUBJECT_CONSENT_FOR_BIBO);
    String textBibo = this.orcidEmailService.createOrcidEmailConsentBiboText(orcidAuthorizationDbVO.getName());
    this.orcidEmailService.sendEmail(emailBibo, emailSender, null, subjectBibo, textBibo);

    orcidAuthorizationDbVO = this.orcidAuthorizationService.finishAuthorizationDbVO(orcidAuthorizationDbVO);

    return orcidAuthorizationDbVO;
  }

  @Override
  public OrcidAuthorizationDbVO createAndSendEmailLink(String token, String coneIdAuthor, String orcidAuthor, String nameAuthor,
      String emailBibo, String emailAuthor)
      throws AuthenticationException, IngeApplicationException, TechnicalException, ValidationException {

    AccountUserDbVO accountUserDbVO = this.authorizationService.getUserAccountFromToken(token);
    this.authorizationService.checkLoginRequiredWithRole(token, GrantVO.PredefinedRoles.ORCID_ADMIN.frameworkValue());

    this.orcidValidatingService.validate(coneIdAuthor, orcidAuthor, nameAuthor, emailBibo, emailAuthor);

    OrcidAuthorizationDbVO orcidAuthorizationDbVO = this.orcidAuthorizationRepository.findByConeIdAuthor(coneIdAuthor);

    if (orcidAuthorizationDbVO != null) {
      orcidAuthorizationDbVO.setErrorCodes(OrcidAuthorizationDbVO.ErrorCode.ERROR_CONE_ID_ALREADY_EXITS.toString());
      return orcidAuthorizationDbVO;
    }

    String scopeRequested = PropertyReader.getProperty(PropertyReader.ORCID_SCOPE);
    String secret = this.orcidAuthorizationService.generateSecret();
    String redirectUri = PropertyReader.getProperty(PropertyReader.ORCID_REDIRECT_URL_REST)
        + PropertyReader.getProperty(PropertyReader.ORCID_REDIRECT_URL_URL) + "?secret=" + secret;
    String emailLink = this.orcidAuthorizationService.createEmailLink(scopeRequested, redirectUri);
    String userIdBibo = accountUserDbVO.getObjectId();

    orcidAuthorizationDbVO = this.orcidAuthorizationService.createOrcidAuthorizationDbVO(userIdBibo, coneIdAuthor, orcidAuthor, nameAuthor,
        secret, scopeRequested, emailLink, emailBibo, emailAuthor, redirectUri);
    orcidAuthorizationDbVO.setDateEmailSentLink(new Date());
    orcidAuthorizationDbVO.setStatus(OrcidAuthorizationDbVO.Status.EMAIL_SENT_LINK.toString());
    orcidAuthorizationDbVO = this.orcidAuthorizationRepository.saveAndFlush(orcidAuthorizationDbVO);

    String subjectAuthor = PropertyReader.getProperty(PropertyReader.ORCID_EMAIL_SUBJECT_FOR_AUTHOR);
    String textAuthor = this.orcidEmailService.createOrcidEmailAuthorText(nameAuthor, orcidAuthor, emailLink);
    this.orcidEmailService.sendEmail(emailAuthor, emailBibo, emailBibo, subjectAuthor, textAuthor);

    String emailSender = PropertyReader.getProperty(PropertyReader.ORCID_EMAIL_SENDER);
    String subjectBibo = PropertyReader.getProperty(PropertyReader.ORCID_EMAIL_SUBJECT_FOR_BIBO);
    String textBibo = this.orcidEmailService.createOrcidEmailBiboText();
    this.orcidEmailService.sendEmail(emailBibo, emailSender, null, subjectBibo, textBibo);

    return orcidAuthorizationDbVO;
  }

  @Override
  public void sendItemsToOrcid() {
    this.orcidSyncService.sendItemsToOrcid();
  }

}
