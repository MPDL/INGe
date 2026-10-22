package de.mpg.mpdl.inge.service.pubman.orcid;

import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import java.util.Date;
import org.json.JSONObject;

public interface OrcidQueryService {

  JSONObject createOrcidQueryJsonNode(String coneIdAuthor, Date dateModified);

  SearchRetrieveResponseVO<ItemVersionVO> doOrcidQuery(String coneIdAuthor, Date dateModified)
      throws IngeTechnicalException, IngeApplicationException;
}
