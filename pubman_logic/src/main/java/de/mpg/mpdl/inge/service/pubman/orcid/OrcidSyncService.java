package de.mpg.mpdl.inge.service.pubman.orcid;

import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.db.valueobjects.OrcidAuthorizationDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.OrcidDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;

public interface OrcidSyncService {

  record ItemCategorization(List<SearchRetrieveRecordVO<ItemVersionVO>> newRecords,
      List<SearchRetrieveRecordVO<ItemVersionVO>> updateRecords) {}

  void sendItemsToOrcid();

  ItemCategorization categorizeRecords(List<SearchRetrieveRecordVO<ItemVersionVO>> records, Map<String, OrcidDbVO> existingOrcidMap);

  String convertToOrcid(SearchRetrieveResponseVO<ItemVersionVO> searchResult) throws IngeTechnicalException;

  OrcidDbVO saveOrcidDbRecord(OrcidAuthorizationDbVO verifiedOrcidAuthorization, String itemId, String putCode, OrcidResponse orcidResponse,
      OrcidDbVO.Status status, Map<String, OrcidDbVO> existingOrcidMap);

  OrcidDbVO saveOrcidDbRecord(OrcidAuthorizationDbVO verifiedOrcidAuthorization, String itemId, String putCode, JSONObject answer,
      OrcidDbVO.Status status, Map<String, OrcidDbVO> existingOrcidMap);

  void saveOrcidDbRecords(OrcidAuthorizationDbVO verifiedOrcidAuthorization, SearchRetrieveResponseVO<ItemVersionVO> searchResult,
      List<OrcidResponse> orcidResponses, OrcidDbVO.Status status, Map<String, OrcidDbVO> existingOrcidMap);
}
