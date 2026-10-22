package de.mpg.mpdl.inge.service.pubman.orcid;

import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;

public interface OrcidTransformationService {

  String convertToOrcid(SearchRetrieveResponseVO<ItemVersionVO> searchResult) throws IngeTechnicalException;

  List<OrcidResponse> parseOrcidResponses(JSONObject orcidAnswer);

  Map<String, JSONObject> extractWorksBySourceWorkId(String orcidJson) throws IngeTechnicalException;

  Map<String, JSONObject> extractWorksBySourceWorkId(JSONObject rootNode);

  Map<String, JSONObject> getWorksBySourceWorkId(String orcidJson) throws IngeTechnicalException;

  String getSourceWorkId(JSONObject workNode);

  void setPutCode(JSONObject workNode, Object putCode);

}
