package de.mpg.mpdl.inge.service.pubman;

import java.util.List;

import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogHeaderDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;

public interface PubItemBatchServiceNeu {

  public BatchProcessLogHeaderDbVO deletePubItems(List<String> itemIds, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException;
}
