package de.mpg.mpdl.inge.service.pubman;

import java.util.Date;
import java.util.List;

import de.mpg.mpdl.inge.model.exception.IngeServiceException;
import de.mpg.mpdl.inge.model.valueobjects.VersionHistoryEntryVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.service.exceptions.AaException;

public interface PubItemService extends GenericService<PubItemVO> {
  public PubItemVO submitPubItem(String pubItemId, Date modificationDate, String message,
      String authenticationToken) throws IngeServiceException, AaException;

  public PubItemVO releasePubItem(String pubItemId, Date modificationDate, String message,
      String authenticationToken) throws IngeServiceException, AaException;

  public PubItemVO withdrawPubItem(String pubItemId, Date modificationDate, String message,
      String authenticationToken) throws IngeServiceException, AaException;

  public PubItemVO revisePubItem(String pubItemId, Date modificationDate, String message,
      String authenticationToken) throws IngeServiceException, AaException;

  public List<VersionHistoryEntryVO> getVersionHistory(String pubItemId, String authenticationToken)
      throws IngeServiceException, AaException;

  public void reindex();
}
