package de.mpg.mpdl.inge.service.pubman;

import java.util.List;

import de.mpg.mpdl.inge.inge_validation.exception.ItemInvalidException;
import de.mpg.mpdl.inge.model.exception.IngeEsServiceException;
import de.mpg.mpdl.inge.model.valueobjects.VersionHistoryEntryVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.service.exceptions.AaException;

public interface PubItemService extends GenericService<PubItemVO> {
  public PubItemVO submitPubItem(String pubItemId, String message, String authenticationToken)
      throws IngeEsServiceException, AaException, ItemInvalidException;

  public PubItemVO releasePubItem(String pubItemId, String message, String authenticationToken)
      throws IngeEsServiceException, AaException, ItemInvalidException;

  public PubItemVO withdrawPubItem(String pubItemId, String message, String authenticationToken)
      throws IngeEsServiceException, AaException, ItemInvalidException;

  public PubItemVO revisePubItem(String pubItemId, String message, String authenticationToken)
      throws IngeEsServiceException, AaException, ItemInvalidException;

  public List<VersionHistoryEntryVO> getVersionHistory(String pubItemId, String authenticationToken)
      throws IngeEsServiceException, AaException;

  public void reindex();
}
