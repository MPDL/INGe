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
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.EmailService;
import de.mpg.mpdl.inge.service.pubman.OrcidService;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.pubman.orcid.OrcidHelper;
import de.mpg.mpdl.inge.service.pubman.orcid.OrcidResponse;
import de.mpg.mpdl.inge.util.PropertyReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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

  private static final int MAX_ITEMS_PER_REQUEST = Integer.parseInt(PropertyReader.getProperty(PropertyReader.ORCID_MAX_ITEMS_PER_REQUEST));

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

    OrcidHelper.sendEmail(this.emailService, emailSender, emailBibo, null, subject, text);

    orcidAuthorizationDbVO = OrcidHelper.finishAuthorizationDbVO(orcidAuthorizationDbVO, this.orcidAuthorizationRepository);

    return orcidAuthorizationDbVO;
  }

  @Override
  public OrcidAuthorizationDbVO createAndSendEmailLink(String token, String coneIdAuthor, String orcidAuthor, String nameAuthor,
      String emailBibo, String emailAuthor)
      throws AuthenticationException, IngeApplicationException, TechnicalException, ValidationException {

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

    OrcidHelper.sendEmail(this.emailService, emailAuthor, emailBibo, emailBibo, subject, text);

    orcidAuthorizationDbVO.setDateEmailSentLink(new Date());
    orcidAuthorizationDbVO.setStatus(OrcidAuthorizationDbVO.Status.EMAIL_SENT_LINK.toString());

    orcidAuthorizationDbVO = this.orcidAuthorizationRepository.saveAndFlush(orcidAuthorizationDbVO);

    return orcidAuthorizationDbVO;
  }

  @Override
  // TODO: Logging
  public void sendItemsToOrcid() {
    logger.info("ORCID: ### Start sendItemsToOrcid() ###");

    List<OrcidAuthorizationDbVO> verifiedOrcidAuthorizations = this.orcidAuthorizationRepository.findByVerifiedTrue();
    logger.info("ORCID: found " + verifiedOrcidAuthorizations.size() + " verified ORCID authorizations");

    for (OrcidAuthorizationDbVO verifiedOrcidAuthorization : verifiedOrcidAuthorizations) {
      try {
        processAuthor(verifiedOrcidAuthorization);
      } catch (Exception e) {
        logger.error("Error synchronizing items for author: " + verifiedOrcidAuthorization.getConeIdAuthor(), e);
      }
    }

    logger.info("ORCID: ### End sendItemsToOrcid() ###");
  }

  private void processAuthor(OrcidAuthorizationDbVO auth) throws Exception {
    logger.info("ORCID: start processAuthor for " + auth.getConeIdAuthor());

    Date dateLastUpdated = new Date();

    SearchRetrieveResponseVO<ItemVersionVO> searchResult =
        OrcidHelper.doOrcidQuery(this.pubItemService, auth.getConeIdAuthor(), auth.getDateLastUpdated());

    logger.info("ORCID: found " + searchResult.getRecords().size() + " records for " + auth.getConeIdAuthor());

    if (searchResult != null && searchResult.getRecords() != null && !searchResult.getRecords().isEmpty()) {
      Map<String, OrcidDbVO> existingOrcidMap = loadExistingOrcidRecords(auth.getConeIdAuthor());

      logger.info("ORCID: found " + existingOrcidMap.size() + " existing records for " + auth.getConeIdAuthor());

      ItemCategorization categorized = categorizeRecords(searchResult.getRecords(), existingOrcidMap);

      logger.info("ORCID: found " + categorized.newRecords.size() + " new records for " + auth.getConeIdAuthor());
      logger.info("ORCID: found " + categorized.updateRecords.size() + " update records for " + auth.getConeIdAuthor());

      boolean hasNew = !categorized.newRecords.isEmpty();
      boolean hasUpdate = !categorized.updateRecords.isEmpty();

      try {
        if (hasNew) {
          createNewWorks(auth, categorized.newRecords);
        }

        if (hasUpdate) {
          updateExistingWorks(auth, categorized.updateRecords, existingOrcidMap);
        }

        if (hasNew || hasUpdate) {
          auth.setDateLastUpdated(dateLastUpdated);
          auth.setStatus(OrcidAuthorizationDbVO.Status.UPDATED.toString());
          this.orcidAuthorizationRepository.saveAndFlush(auth);

          logger.info("ORCID: finished processing author " + auth.getConeIdAuthor());
        }
      } catch (OrcidAccessDeniedException e) {
        logger.error("ORCID: access denied (401) for author {}: {}", auth.getConeIdAuthor(), e.getMessage());
        handleAccessDenied(auth);
      }
    }
  }

  private void handleAccessDenied(OrcidAuthorizationDbVO auth) {
    auth.setStatus(OrcidAuthorizationDbVO.Status.ACCESS_DENIED.toString());
    auth.setVerified(false);
    auth.setDateError(new Date());
    this.orcidAuthorizationRepository.saveAndFlush(auth);
  }

  private static boolean is401Error(Throwable e) {
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

  private static boolean contains401Error(List<OrcidResponse> responses) {
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

  private Map<String, OrcidDbVO> loadExistingOrcidRecords(String coneIdAuthor) {
    Map<String, OrcidDbVO> existingOrcidMap = new HashMap<>();
    if (this.orcidRepository != null) {
      List<OrcidDbVO> existingOrcidRecords = this.orcidRepository.findByConeIdAuthor(coneIdAuthor);
      if (existingOrcidRecords != null) {
        for (OrcidDbVO orcidDbVO : existingOrcidRecords) {
          if (orcidDbVO != null && orcidDbVO.getItemId() != null) {
            existingOrcidMap.put(orcidDbVO.getItemId(), orcidDbVO);
          }
        }
      }
    }

    return existingOrcidMap;
  }

  private ItemCategorization categorizeRecords(List<SearchRetrieveRecordVO<ItemVersionVO>> records,
      Map<String, OrcidDbVO> existingOrcidMap) {

    List<SearchRetrieveRecordVO<ItemVersionVO>> newRecords = new ArrayList<>();
    List<SearchRetrieveRecordVO<ItemVersionVO>> updateRecords = new ArrayList<>();

    for (SearchRetrieveRecordVO<ItemVersionVO> record : records) {
      ItemVersionVO itemVersionVO = record != null ? record.getData() : null;

      if (itemVersionVO != null && itemVersionVO.getObjectId() != null) {
        String itemId = itemVersionVO.getObjectId();
        OrcidDbVO orcidDbVO = existingOrcidMap.get(itemId);
        if (orcidDbVO == null || orcidDbVO.getPutCode() == null) {
          newRecords.add(record);
        } else {
          updateRecords.add(record);
        }
      }
    }

    return new ItemCategorization(newRecords, updateRecords);
  }

  private void createNewWorks(OrcidAuthorizationDbVO auth, List<SearchRetrieveRecordVO<ItemVersionVO>> newRecords)
      throws OrcidAccessDeniedException {

    for (int i = 0; i < newRecords.size(); i += MAX_ITEMS_PER_REQUEST) {
      int toIndex = Math.min(i + MAX_ITEMS_PER_REQUEST, newRecords.size());
      List<SearchRetrieveRecordVO<ItemVersionVO>> batchRecords = newRecords.subList(i, toIndex);

      SearchRetrieveResponseVO<ItemVersionVO> batchSearchNew = new SearchRetrieveResponseVO<>();
      batchSearchNew.setRecords(batchRecords);

      try {
        String orcidBodyNew = OrcidHelper.convertToOrcid(batchSearchNew);

        JSONObject answerNew = OrcidHelper.createWorksInOrcid(auth.getOrcid(), orcidBodyNew, auth.getAccessToken());

        List<OrcidResponse> orcidResponsesNew = OrcidHelper.parseOrcidResponses(answerNew);
        logger.info("ORCID: got " + orcidResponsesNew.size() + " orcidResponsesNew");

        if (contains401Error(orcidResponsesNew)) {
          throw new OrcidAccessDeniedException("401 Unauthorized in ORCID response", null);
        }

        OrcidHelper.saveOrcidDbRecords(this.orcidRepository, auth, batchSearchNew, orcidResponsesNew, OrcidDbVO.Status.CREATE);
      } catch (OrcidAccessDeniedException e) {
        throw e;
      } catch (Exception e) {
        if (is401Error(e)) {
          throw new OrcidAccessDeniedException("401 Unauthorized during ORCID work creation", e);
        }
      }
    }
  }

  private void updateExistingWorks(OrcidAuthorizationDbVO auth, List<SearchRetrieveRecordVO<ItemVersionVO>> updateRecords,
      Map<String, OrcidDbVO> existingOrcidMap) throws OrcidAccessDeniedException {

    SearchRetrieveResponseVO<ItemVersionVO> searchResultUpdate = new SearchRetrieveResponseVO<>();
    searchResultUpdate.setRecords(updateRecords);

    try {
      String orcidBodyUpdate = OrcidHelper.convertToOrcid(searchResultUpdate);
      Map<String, ObjectNode> updateMap = OrcidHelper.getWorksBySourceWorkId(orcidBodyUpdate);

      for (SearchRetrieveRecordVO<ItemVersionVO> record : updateRecords) {
        ItemVersionVO itemVersionVO = record != null ? record.getData() : null;
        if (itemVersionVO == null || itemVersionVO.getObjectId() == null) {
          continue;
        }

        String itemId = itemVersionVO.getObjectId();
        OrcidDbVO orcidDbVO = existingOrcidMap.get(itemId);
        String putCode = orcidDbVO != null ? orcidDbVO.getPutCode() : null;

        ObjectNode workNode = updateMap.get(itemId);
        if (workNode != null && putCode != null) {
          updateSingleWork(auth, itemId, putCode, workNode);
        }
      }
    } catch (OrcidAccessDeniedException e) {
      throw e;
    } catch (Exception e) {
      if (is401Error(e)) {
        throw new OrcidAccessDeniedException("401 Unauthorized during ORCID work update", e);
      }
      logger.error("Error updating ORCID works for author " + auth.getConeIdAuthor(), e);
    }
  }

  private void updateSingleWork(OrcidAuthorizationDbVO auth, String itemId, String putCode, ObjectNode workNode)
      throws OrcidAccessDeniedException {
    OrcidHelper.setPutCode(workNode, putCode);
    String singleWorkJson = workNode.toString();

    try {
      JSONObject answerUpdate = OrcidHelper.updateWorkInOrcid(auth.getOrcid(), singleWorkJson, auth.getAccessToken(), putCode);

      List<OrcidResponse> orcidResponses = OrcidHelper.parseOrcidResponses(answerUpdate);
      if (contains401Error(orcidResponses)) {
        throw new OrcidAccessDeniedException("401 Unauthorized in ORCID response", null);
      }
      if (!orcidResponses.isEmpty() && orcidResponses.get(0).isError()) {
        OrcidResponse errorResponse = orcidResponses.get(0);
        String devMsg = errorResponse.getDeveloperMessage();
        String devMsgWithPutCode = (devMsg != null && !devMsg.isBlank()) ? devMsg + " (putCode: " + putCode + ")" : "putCode: " + putCode;
        errorResponse.setDeveloperMessage(devMsgWithPutCode);

        OrcidDbVO orcidDbVO =
            OrcidHelper.saveOrcidDbRecord(this.orcidRepository, auth, itemId, putCode, errorResponse, OrcidDbVO.Status.UPDATE);

        logger.info("ORCID: saved orcidDbVO: " + orcidDbVO.toInfoString());
      } else {
        OrcidDbVO orcidDbVO =
            OrcidHelper.saveOrcidDbRecord(this.orcidRepository, auth, itemId, putCode, answerUpdate, OrcidDbVO.Status.UPDATE);

        logger.info("ORCID: saved orcidDbVO: " + orcidDbVO.toInfoString());
      }

    } catch (OrcidAccessDeniedException e) {
      throw e;
    } catch (Exception e) {
      if (is401Error(e)) {
        throw new OrcidAccessDeniedException("401 Unauthorized during ORCID work update", e);
      }
      logger.error("Error updating ORCID work for item " + itemId + " with putCode " + putCode, e);
    }
  }

  private static class OrcidAccessDeniedException extends Exception {
    public OrcidAccessDeniedException(String message, Throwable cause) {
      super(message, cause);
    }
  }

  private record ItemCategorization(List<SearchRetrieveRecordVO<ItemVersionVO>> newRecords,
      List<SearchRetrieveRecordVO<ItemVersionVO>> updateRecords) {}

  private AccountUserDbVO getUser(String token) throws AuthenticationException, IngeApplicationException {
    return this.authorizationService.getUserAccountFromToken(token);
  }
}
