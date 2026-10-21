package de.mpg.mpdl.inge.service.pubman.impl;

import de.mpg.mpdl.inge.db.repository.OrcidAuthorizationRepository;
import de.mpg.mpdl.inge.db.repository.OrcidRepository;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.db.valueobjects.OrcidAuthorizationDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.OrcidDbVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.pubman.orcid.OrcidHelper;
import de.mpg.mpdl.inge.service.pubman.orcid.OrcidHelper.ItemCategorization;
import de.mpg.mpdl.inge.service.pubman.orcid.OrcidResponse;
import de.mpg.mpdl.inge.service.pubman.orcid.OrcidRestClient;
import de.mpg.mpdl.inge.service.pubman.orcid.OrcidSyncService;
import de.mpg.mpdl.inge.util.PropertyReader;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrcidSyncServiceImpl implements OrcidSyncService {

  private static final Logger logger = LogManager.getLogger(OrcidSyncServiceImpl.class);

  private static final int MAX_ITEMS_PER_REQUEST = Integer.parseInt(PropertyReader.getProperty(PropertyReader.ORCID_MAX_ITEMS_PER_REQUEST));

  private final OrcidAuthorizationRepository orcidAuthorizationRepository;
  private final OrcidRepository orcidRepository;
  private final PubItemService pubItemService;
  private final OrcidRestClient orcidRestClient;

  @Autowired
  public OrcidSyncServiceImpl(OrcidAuthorizationRepository orcidAuthorizationRepository, OrcidRepository orcidRepository,
      PubItemService pubItemService, OrcidRestClient orcidRestClient) {
    this.orcidAuthorizationRepository = orcidAuthorizationRepository;
    this.orcidRepository = orcidRepository;
    this.pubItemService = pubItemService;
    this.orcidRestClient = orcidRestClient != null ? orcidRestClient : new OrcidRestClientImpl();
  }

  public OrcidSyncServiceImpl(OrcidAuthorizationRepository orcidAuthorizationRepository, OrcidRepository orcidRepository,
      PubItemService pubItemService) {
    this(orcidAuthorizationRepository, orcidRepository, pubItemService, new OrcidRestClientImpl());
  }

  @Override
  public void sendItemsToOrcid() {
    logger.info("ORCID: ### Start sendItemsToOrcid() ###");

    if (this.orcidAuthorizationRepository == null) {
      logger.warn("ORCID: orcidAuthorizationRepository is null, aborting sendItemsToOrcid");
      return;
    }

    List<OrcidAuthorizationDbVO> verifiedOrcidAuthorizations = this.orcidAuthorizationRepository.findByVerifiedTrue();
    if (verifiedOrcidAuthorizations == null) {
      logger.info("ORCID: no verified ORCID authorizations found");
      return;
    }
    logger.info("ORCID: found {} verified ORCID authorizations", verifiedOrcidAuthorizations.size());

    for (OrcidAuthorizationDbVO verifiedOrcidAuthorization : verifiedOrcidAuthorizations) {
      try {
        processAuthor(verifiedOrcidAuthorization);
      } catch (Exception e) {
        logger.error("Error synchronizing items for author: {}", verifiedOrcidAuthorization.getConeIdAuthor(), e);
      }
    }

    logger.info("ORCID: ### End sendItemsToOrcid() ###");
  }

  private void processAuthor(OrcidAuthorizationDbVO auth) throws Exception {
    logger.info("ORCID: start processAuthor for {}", auth.getConeIdAuthor());

    Date dateLastUpdated = new Date();

    SearchRetrieveResponseVO<ItemVersionVO> searchResult =
        OrcidHelper.doOrcidQuery(this.pubItemService, auth.getConeIdAuthor(), auth.getDateLastUpdated());

    int anzRecords = 0;
    if (searchResult != null && searchResult.getRecords() != null && !searchResult.getRecords().isEmpty()) {
      anzRecords = searchResult.getRecords().size();
    }

    logger.info("ORCID: found {} records for {}", anzRecords, auth.getConeIdAuthor());

    if (anzRecords > 0) {
      Map<String, OrcidDbVO> existingOrcidMap = loadExistingOrcidRecords(auth.getConeIdAuthor());

      logger.info("ORCID: found {} existing records for {}", existingOrcidMap.size(), auth.getConeIdAuthor());

      ItemCategorization categorized = OrcidHelper.categorizeRecords(searchResult.getRecords(), existingOrcidMap);

      logger.info("ORCID: found {} new records for {}", categorized.newRecords().size(), auth.getConeIdAuthor());
      logger.info("ORCID: found {} update records for {}", categorized.updateRecords().size(), auth.getConeIdAuthor());

      boolean hasNew = !categorized.newRecords().isEmpty();
      boolean hasUpdate = !categorized.updateRecords().isEmpty();

      try {
        if (hasNew) {
          createNewWorks(auth, categorized.newRecords(), existingOrcidMap);
        }

        if (hasUpdate) {
          updateExistingWorks(auth, categorized.updateRecords(), existingOrcidMap);
        }

        if (hasNew || hasUpdate) {
          auth.setDateLastUpdated(dateLastUpdated);
          auth.setStatus(OrcidAuthorizationDbVO.Status.UPDATED.toString());
          if (this.orcidAuthorizationRepository != null) {
            this.orcidAuthorizationRepository.saveAndFlush(auth);
          }

          logger.info("ORCID: finished processing author {}", auth.getConeIdAuthor());
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
    if (this.orcidAuthorizationRepository != null) {
      this.orcidAuthorizationRepository.saveAndFlush(auth);
    }
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

  private void createNewWorks(OrcidAuthorizationDbVO auth, List<SearchRetrieveRecordVO<ItemVersionVO>> newRecords,
      Map<String, OrcidDbVO> existingOrcidMap) throws OrcidAccessDeniedException {

    for (int i = 0; i < newRecords.size(); i += MAX_ITEMS_PER_REQUEST) {
      int toIndex = Math.min(i + MAX_ITEMS_PER_REQUEST, newRecords.size());
      List<SearchRetrieveRecordVO<ItemVersionVO>> batchRecords = newRecords.subList(i, toIndex);

      SearchRetrieveResponseVO<ItemVersionVO> batchSearchNew = new SearchRetrieveResponseVO<>();
      batchSearchNew.setRecords(batchRecords);

      try {
        String orcidBodyNew = OrcidHelper.convertToOrcid(batchSearchNew);

        JSONObject answerNew = this.orcidRestClient.createWorksInOrcid(auth.getOrcid(), orcidBodyNew, auth.getAccessToken());

        List<OrcidResponse> orcidResponsesNew = OrcidHelper.parseOrcidResponses(answerNew);
        logger.info("ORCID: got {} orcidResponsesNew", orcidResponsesNew.size());

        if (OrcidHelper.contains401Error(orcidResponsesNew)) {
          throw new OrcidAccessDeniedException("401 Unauthorized in ORCID response", null);
        }

        OrcidHelper.saveOrcidDbRecords(this.orcidRepository, auth, batchSearchNew, orcidResponsesNew, OrcidDbVO.Status.CREATE,
            existingOrcidMap);
      } catch (OrcidAccessDeniedException e) {
        throw e;
      } catch (Exception e) {
        if (OrcidHelper.is401Error(e)) {
          throw new OrcidAccessDeniedException("401 Unauthorized during ORCID work creation", e);
        }
        logger.error("ORCID: error creating works in ORCID for author {}", auth.getConeIdAuthor(), e);
      }
    }
  }

  private void updateExistingWorks(OrcidAuthorizationDbVO auth, List<SearchRetrieveRecordVO<ItemVersionVO>> updateRecords,
      Map<String, OrcidDbVO> existingOrcidMap) throws OrcidAccessDeniedException {

    SearchRetrieveResponseVO<ItemVersionVO> searchResultUpdate = new SearchRetrieveResponseVO<>();
    searchResultUpdate.setRecords(updateRecords);

    try {
      String orcidBodyUpdate = OrcidHelper.convertToOrcid(searchResultUpdate);
      Map<String, JSONObject> updateMap = OrcidHelper.getWorksBySourceWorkId(orcidBodyUpdate);

      for (SearchRetrieveRecordVO<ItemVersionVO> record : updateRecords) {
        ItemVersionVO itemVersionVO = record != null ? record.getData() : null;
        if (itemVersionVO == null || itemVersionVO.getObjectId() == null) {
          continue;
        }

        String itemId = itemVersionVO.getObjectId();
        OrcidDbVO orcidDbVO = existingOrcidMap.get(itemId);
        String putCode = orcidDbVO != null ? orcidDbVO.getPutCode() : null;

        JSONObject workNode = updateMap.get(itemId);
        if (workNode != null && putCode != null) {
          updateSingleWork(auth, itemId, putCode, workNode, existingOrcidMap);
        }
      }
    } catch (OrcidAccessDeniedException e) {
      throw e;
    } catch (Exception e) {
      if (OrcidHelper.is401Error(e)) {
        throw new OrcidAccessDeniedException("401 Unauthorized during ORCID work update", e);
      }
      logger.error("ORCID: error updating ORCID works for author {}", auth.getConeIdAuthor(), e);
    }
  }

  private void updateSingleWork(OrcidAuthorizationDbVO auth, String itemId, String putCode, JSONObject workNode,
      Map<String, OrcidDbVO> existingOrcidMap) throws OrcidAccessDeniedException {
    OrcidHelper.setPutCode(workNode, putCode);
    String singleWorkJson = workNode.toString();

    try {
      JSONObject answerUpdate = this.orcidRestClient.updateWorkInOrcid(auth.getOrcid(), singleWorkJson, auth.getAccessToken(), putCode);

      List<OrcidResponse> orcidResponses = OrcidHelper.parseOrcidResponses(answerUpdate);
      if (OrcidHelper.contains401Error(orcidResponses)) {
        throw new OrcidAccessDeniedException("401 Unauthorized in ORCID response", null);
      }
      if (!orcidResponses.isEmpty() && orcidResponses.get(0).isError()) {
        OrcidResponse errorResponse = orcidResponses.get(0);
        String devMsg = errorResponse.getDeveloperMessage();
        String devMsgWithPutCode = (devMsg != null && !devMsg.isBlank()) ? devMsg + " (putCode: " + putCode + ")" : "putCode: " + putCode;
        errorResponse.setDeveloperMessage(devMsgWithPutCode);

        OrcidDbVO orcidDbVO = OrcidHelper.saveOrcidDbRecord(this.orcidRepository, auth, itemId, putCode, errorResponse,
            OrcidDbVO.Status.UPDATE, existingOrcidMap);

        logger.info("ORCID: saved orcidDbVO: {}", orcidDbVO.toInfoString());
      } else {
        OrcidDbVO orcidDbVO = OrcidHelper.saveOrcidDbRecord(this.orcidRepository, auth, itemId, putCode, answerUpdate,
            OrcidDbVO.Status.UPDATE, existingOrcidMap);

        logger.info("ORCID: saved orcidDbVO: {}", orcidDbVO.toInfoString());
      }

    } catch (OrcidAccessDeniedException e) {
      throw e;
    } catch (Exception e) {
      if (OrcidHelper.is401Error(e)) {
        throw new OrcidAccessDeniedException("401 Unauthorized during ORCID work update", e);
      }
      logger.error("Error updating ORCID work for item {} with putCode {}", itemId, putCode, e);
    }
  }

  private static class OrcidAccessDeniedException extends Exception {
    public OrcidAccessDeniedException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
