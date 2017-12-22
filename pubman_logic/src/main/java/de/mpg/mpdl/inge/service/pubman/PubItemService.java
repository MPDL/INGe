package de.mpg.mpdl.inge.service.pubman;

import java.util.Date;
import java.util.List;

import de.mpg.mpdl.inge.model.db.valueobjects.PubItemVersionDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.VersionHistoryEntryVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;

public interface PubItemService extends GenericService<PubItemVersionDbVO, String> {
  public PubItemVersionDbVO submitPubItem(String pubItemId, Date modificationDate, String message, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public PubItemVersionDbVO releasePubItem(String pubItemId, Date modificationDate, String message, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public PubItemVersionDbVO withdrawPubItem(String pubItemId, Date modificationDate, String message, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public PubItemVersionDbVO revisePubItem(String pubItemId, Date modificationDate, String message, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public List<VersionHistoryEntryVO> getVersionHistory(String pubItemId, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

}
