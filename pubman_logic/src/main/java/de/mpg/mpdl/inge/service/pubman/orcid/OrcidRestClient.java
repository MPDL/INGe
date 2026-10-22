package de.mpg.mpdl.inge.service.pubman.orcid;

import de.mpg.mpdl.inge.db.repository.OrcidAuthorizationRepository;
import de.mpg.mpdl.inge.model.db.valueobjects.OrcidAuthorizationDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;

public interface OrcidRestClient {

  OrcidAuthorizationDbVO requestTokenAndOrcid(OrcidAuthorizationDbVO orcidAuthorizationDbVO, String code,
      OrcidAuthorizationRepository orcidAuthorizationRepository) throws TechnicalException;

  JSONObject createWorksInOrcid(String orcid, String body, String accessToken) throws TechnicalException;

  JSONObject updateWorkInOrcid(String orcid, String body, String accessToken, String putCode) throws TechnicalException;

  List<OrcidResponse> parseOrcidResponses(JSONObject orcidAnswer);

  boolean is401Error(Throwable e);

  boolean contains401Error(List<OrcidResponse> responses);

  Map<String, JSONObject> getWorksBySourceWorkId(String orcidJson) throws IngeTechnicalException;

  void setPutCode(JSONObject workNode, Object putCode);
}
