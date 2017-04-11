package de.mpg.mpdl.inge.service.pubman;

import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.service.exceptions.AaException;
import de.mpg.mpdl.inge.services.IngeServiceException;

public interface PubItemService extends GenericService<PubItemVO> {

  public PubItemVO submitPubItem(String pubItemId, String message, String userToken)
      throws IngeServiceException, AaException;

  public PubItemVO releasePubItem(String pubItemId, String message, String userToken)
      throws IngeServiceException, AaException;

  public PubItemVO withdrawPubItem(String pubItemId, String message, String userToken)
      throws IngeServiceException, AaException;



}
