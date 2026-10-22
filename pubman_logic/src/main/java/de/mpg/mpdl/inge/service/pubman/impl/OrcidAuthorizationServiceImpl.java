package de.mpg.mpdl.inge.service.pubman.impl;

import de.mpg.mpdl.inge.db.repository.OrcidAuthorizationRepository;
import de.mpg.mpdl.inge.model.db.valueobjects.OrcidAuthorizationDbVO;
import de.mpg.mpdl.inge.service.pubman.orcid.OrcidAuthorizationService;
import de.mpg.mpdl.inge.util.PropertyReader;
import java.util.Date;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrcidAuthorizationServiceImpl implements OrcidAuthorizationService {

  private OrcidAuthorizationRepository orcidAuthorizationRepository;

  @Autowired
  public OrcidAuthorizationServiceImpl(OrcidAuthorizationRepository orcidAuthorizationRepository) {
    this.orcidAuthorizationRepository = orcidAuthorizationRepository;
  }

  @Override
  public String generateSecret() {
    return UUID.randomUUID().toString();
  }

  @Override
  public String createEmailLink(String scopeRequested, String redirectUri) {
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

  @Override
  public OrcidAuthorizationDbVO createOrcidAuthorizationDbVO(String userIdBibo, String coneIdAuthor, String orcidAuthor, String nameAuthor,
      String secret, String scopeRequested, String emailLink, String emailBibo, String emailAuthor, String redirectUri) {

    OrcidAuthorizationDbVO orcidAuthorizationDbVO = new OrcidAuthorizationDbVO(userIdBibo, coneIdAuthor, orcidAuthor, nameAuthor, secret,
        scopeRequested, emailLink, emailBibo, emailAuthor, redirectUri);
    orcidAuthorizationDbVO = this.orcidAuthorizationRepository.saveAndFlush(orcidAuthorizationDbVO);

    return orcidAuthorizationDbVO;
  }

  @Override
  public OrcidAuthorizationDbVO finishAuthorizationDbVO(OrcidAuthorizationDbVO orcidAuthorizationDbVO) {
    orcidAuthorizationDbVO.setStatus(OrcidAuthorizationDbVO.Status.EMAIL_SENT_BIBO.toString());
    orcidAuthorizationDbVO.setDateEmailSentBibo(new Date());

    orcidAuthorizationDbVO = this.orcidAuthorizationRepository.saveAndFlush(orcidAuthorizationDbVO);

    return orcidAuthorizationDbVO;
  }

  @Override
  public OrcidAuthorizationDbVO updateAuthorizationDbVO(OrcidAuthorizationDbVO orcidAuthorizationDbVO, String code) {
    orcidAuthorizationDbVO.setCodeReceived(code);
    orcidAuthorizationDbVO.setDateCodeReceived(new Date());
    orcidAuthorizationDbVO.setStatus(OrcidAuthorizationDbVO.Status.CODE_RECEIVED.toString());

    orcidAuthorizationDbVO = this.orcidAuthorizationRepository.saveAndFlush(orcidAuthorizationDbVO);

    return orcidAuthorizationDbVO;
  }

}
