package de.mpg.mpdl.inge.service.pubman.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import de.mpg.mpdl.inge.db.repository.OrcidAuthorizationRepository;
import de.mpg.mpdl.inge.db.repository.OrcidRepository;
import de.mpg.mpdl.inge.inge_validation.OrcidValidatingService;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.db.valueobjects.OrcidAuthorizationDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.OrcidDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.EmailService;
import de.mpg.mpdl.inge.service.pubman.OrcidService;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.pubman.orcid.OrcidBulkItem;
import de.mpg.mpdl.inge.service.pubman.orcid.OrcidHelper;
import de.mpg.mpdl.inge.util.PropertyReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class OrcidServiceImpl implements OrcidService {

  private static final Logger logger = LogManager.getLogger(OrcidServiceImpl.class);

  private final AuthorizationService authorizationService;
  private final OrcidAuthorizationRepository orcidAuthorizationRepository;
  private final EmailService emailService;
  private final OrcidValidatingService orcidValidatingService;
  private final PubItemService pubItemService;
  private final OrcidRepository orcidRepository;

  @Autowired
  public OrcidServiceImpl(AuthorizationService authorizationService, OrcidAuthorizationRepository orcidAuthorizationRepository,
      EmailService emailService, OrcidValidatingService orcidValidatingService, PubItemService pubItemService,
      OrcidRepository orcidRepository) {
    this.authorizationService = authorizationService;
    this.orcidAuthorizationRepository = orcidAuthorizationRepository;
    this.emailService = emailService;
    this.orcidValidatingService = orcidValidatingService;
    this.pubItemService = pubItemService;
    this.orcidRepository = orcidRepository;
  }

  public OrcidServiceImpl(AuthorizationService authorizationService, OrcidAuthorizationRepository orcidAuthorizationRepository,
      EmailService emailService, OrcidValidatingService orcidValidatingService, PubItemService pubItemService) {
    this(authorizationService, orcidAuthorizationRepository, emailService, orcidValidatingService, pubItemService, null);
  }

  public OrcidServiceImpl(AuthorizationService authorizationService, OrcidAuthorizationRepository orcidAuthorizationRepository,
      EmailService emailService, OrcidValidatingService orcidValidatingService) {
    this(authorizationService, orcidAuthorizationRepository, emailService, orcidValidatingService, null, null);
  }

  @Override
  public OrcidAuthorizationDbVO sendEmailLink(String token, String coneIdAuthor, String orcidAuthor, String nameAuthor, String emailBibo,
      String emailAuthor) throws AuthenticationException, IngeApplicationException, TechnicalException, ValidationException {

    AccountUserDbVO accountUserDbVO = getUser(token);
    this.authorizationService.checkLoginRequiredWithRole(token, GrantVO.PredefinedRoles.ORCID_ADMIN.frameworkValue());

    this.orcidValidatingService.validate(coneIdAuthor, orcidAuthor, nameAuthor, emailBibo, emailAuthor);

    OrcidAuthorizationDbVO orcidAuthorizationDbVO = this.orcidAuthorizationRepository.findByConeIdAuthor(coneIdAuthor);

    if (orcidAuthorizationDbVO != null) {
      orcidAuthorizationDbVO.setErrorCodes(OrcidAuthorizationDbVO.ErrorCode.ERROR_CONE_ID_ALREADY_EXITS.toString());
      return orcidAuthorizationDbVO;
    }

    String scopeRequested = PropertyReader.getProperty(PropertyReader.ORCID_SCOPE);
    String genSecret = OrcidHelper.generateSecret();
    String redirectUri = PropertyReader.getProperty(PropertyReader.ORCID_REDIRECT_URL_REST)
        + PropertyReader.getProperty(PropertyReader.ORCID_REDIRECT_URL_URL) + "?secret=" + genSecret;
    String emailLink = OrcidHelper.createEmailLink(scopeRequested, redirectUri);
    String userIdBibo = accountUserDbVO != null ? accountUserDbVO.getObjectId() : null;

    orcidAuthorizationDbVO = OrcidHelper.createOrcidAuthorizationDbVO(this.orcidAuthorizationRepository, userIdBibo, coneIdAuthor,
        orcidAuthor, nameAuthor, genSecret, scopeRequested, emailLink, emailBibo, emailAuthor, redirectUri);

    String subject = PropertyReader.getProperty(PropertyReader.ORCID_EMAIL_SUBJECT_FOR_LINK);
    String text = OrcidHelper.createEmailText(nameAuthor, orcidAuthor, emailLink);

    OrcidHelper.sendEmail(this.emailService, emailBibo, emailBibo, subject, text);

    orcidAuthorizationDbVO.setDateEmailSentLink(new Date());
    orcidAuthorizationDbVO.setStatus(OrcidAuthorizationDbVO.Status.EMAIL_SENT_LINK.toString());

    orcidAuthorizationDbVO = this.orcidAuthorizationRepository.saveAndFlush(orcidAuthorizationDbVO);

    return orcidAuthorizationDbVO;
  }

  @Override
  public OrcidAuthorizationDbVO createOrcidAuthorization(String secret, String code, String errorDescription)
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
      } else {
        orcidAuthorizationDbVO.setErrorCodes(errorDescription);
        orcidAuthorizationDbVO.setStatus(OrcidAuthorizationDbVO.Status.ERROR.toString());
      }
      orcidAuthorizationDbVO = this.orcidAuthorizationRepository.saveAndFlush(orcidAuthorizationDbVO);
      return orcidAuthorizationDbVO;
    }

    orcidAuthorizationDbVO = OrcidHelper.updateAuthorizationDbVO(orcidAuthorizationDbVO, code, this.orcidAuthorizationRepository);
    orcidAuthorizationDbVO = OrcidHelper.requestTokenAndOrcid(orcidAuthorizationDbVO, code, this.orcidAuthorizationRepository);

    String emailBibo = orcidAuthorizationDbVO.getEmailBibo();
    String emailSender = PropertyReader.getProperty(PropertyReader.ORCID_EMAIL_SENDER_FOR_BIBO);
    String subject = PropertyReader.getProperty(PropertyReader.ORCID_EMAIL_SUBJECT_FOR_BIBO);
    String text = PropertyReader.getProperty(PropertyReader.ORCID_EMAIL_TEXT_FOR_BIBO);

    OrcidHelper.sendEmail(this.emailService, emailSender, emailBibo, subject, text);

    orcidAuthorizationDbVO = OrcidHelper.finishAuthorizationDbVO(orcidAuthorizationDbVO, this.orcidAuthorizationRepository);

    return orcidAuthorizationDbVO;
  }

  @Override
  public void getVerifiedOrcidAuthorizations()
      throws AuthenticationException, AuthorizationException, IngeApplicationException, IngeTechnicalException, TechnicalException {
    List<OrcidAuthorizationDbVO> verifiedOrcidAuthorizations = this.orcidAuthorizationRepository.findByVerifiedTrue();
    for (OrcidAuthorizationDbVO verifiedOrcidAuthorization : verifiedOrcidAuthorizations) {
      SearchRetrieveResponseVO<ItemVersionVO> searchResult = OrcidHelper.doOrcidQuery(this.pubItemService,
          verifiedOrcidAuthorization.getConeIdAuthor(), verifiedOrcidAuthorization.getDateLastUpdated(), null);
      // TODO: dateLastUpdated setzen
      SearchRetrieveResponseVO<ItemVersionVO> searchResultNew = new SearchRetrieveResponseVO<>();
      searchResultNew.setRecords(new ArrayList<>());
      SearchRetrieveResponseVO<ItemVersionVO> searchResultUpdate = new SearchRetrieveResponseVO<>();
      searchResultUpdate.setRecords(new ArrayList<>());
      Map<String, String> putCodesByItemId = new LinkedHashMap<>();
      if (searchResult != null && searchResult.getRecords() != null) {
        for (SearchRetrieveRecordVO<ItemVersionVO> record : searchResult.getRecords()) {
          ItemVersionVO itemVersionVO = record != null ? record.getData() : null;
          if (itemVersionVO != null && itemVersionVO.getObjectId() != null) {
            String itemId = itemVersionVO.getObjectId();
            OrcidDbVO orcidDbVO = this.orcidRepository != null
                ? this.orcidRepository.findByConeIdAuthorAndItemId(verifiedOrcidAuthorization.getConeIdAuthor(), itemId)
                : null;
            if (orcidDbVO == null || orcidDbVO.getPutCode() == null) {
              searchResultNew.getRecords().add(record);
            } else {
              searchResultUpdate.getRecords().add(record);
              putCodesByItemId.put(itemId, orcidDbVO.getPutCode());
            }
          }
        }
        if (searchResultNew.getRecords() != null && !searchResultNew.getRecords().isEmpty()) {
          List<SearchRetrieveRecordVO<ItemVersionVO>> allNewRecords = searchResultNew.getRecords();
          int batchSize = 100;
          for (int i = 0; i < allNewRecords.size(); i += batchSize) {
            int toIndex = Math.min(i + batchSize, allNewRecords.size());
            List<SearchRetrieveRecordVO<ItemVersionVO>> batchRecords = allNewRecords.subList(i, toIndex);

            SearchRetrieveResponseVO<ItemVersionVO> batchSearchResultNew = new SearchRetrieveResponseVO<>();
            batchSearchResultNew.setRecords(batchRecords);

            String orcidBodyNew = OrcidHelper.convertToOrcid(batchSearchResultNew);
            JSONObject answerNew =
                OrcidHelper.createWork(verifiedOrcidAuthorization.getOrcid(), orcidBodyNew, verifiedOrcidAuthorization.getAccessToken());
            logger.info("Orcid work created: " + answerNew);

            List<OrcidBulkItem> bulkItemsNew = OrcidHelper.parseBulkResponse(answerNew);

            OrcidHelper.saveOrcidDbRecords(this.orcidRepository, verifiedOrcidAuthorization, batchSearchResultNew, bulkItemsNew,
                OrcidDbVO.Status.CREATE);
          }
        }

        if (searchResultUpdate.getRecords() != null && !searchResultUpdate.getRecords().isEmpty()) {
          String orcidBodyUpdate = OrcidHelper.convertToOrcid(searchResultUpdate);
          Map<String, ObjectNode> updateMap = OrcidHelper.getWorksBySourceWorkId(orcidBodyUpdate);
          for (Map.Entry<String, String> entry : putCodesByItemId.entrySet()) {
            String itemId = entry.getKey();
            String putCode = entry.getValue();
            com.fasterxml.jackson.databind.node.ObjectNode workNode = updateMap.get(itemId);
            if (workNode != null) {
              OrcidHelper.setPutCode(workNode, putCode);
              String singleWorkJson = workNode.toString();
              logger.info("Orcid work body: " + singleWorkJson);
              JSONObject answerUpdate = OrcidHelper.updateWork(verifiedOrcidAuthorization.getOrcid(), singleWorkJson,
                  verifiedOrcidAuthorization.getAccessToken(), putCode);
              logger.info("Orcid work updated: " + answerUpdate);

              OrcidHelper.saveOrcidDbRecord(this.orcidRepository, verifiedOrcidAuthorization, itemId, putCode, answerUpdate,
                  OrcidDbVO.Status.UPDATE);
            }
          }
        }
      }
    }
  }

  private AccountUserDbVO getUser(String token) throws AuthenticationException, IngeApplicationException {
    if (this.authorizationService == null) {
      return null;
    }
    return this.authorizationService.getUserAccountFromToken(token);
  }
}
