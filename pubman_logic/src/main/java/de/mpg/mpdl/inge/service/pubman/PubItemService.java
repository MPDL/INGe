package de.mpg.mpdl.inge.service.pubman;

import de.mpg.mpdl.inge.inge_validation.exception.ItemInvalidException;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.service.exceptions.AaException;
import de.mpg.mpdl.inge.services.IngeServiceException;

public interface PubItemService extends GenericService<PubItemVO> {

  public PubItemVO submitPubItem(String pubItemId, String message, String authenticationToken)
      throws IngeServiceException, AaException, ItemInvalidException;

  public PubItemVO releasePubItem(String pubItemId, String message, String authenticationToken)
      throws IngeServiceException, AaException, ItemInvalidException;

  public PubItemVO withdrawPubItem(String pubItemId, String message, String authenticationToken)
      throws IngeServiceException, AaException, ItemInvalidException;

  public PubItemVO revisePubItem(String pubItemId, String message, String authenticationToken)
      throws IngeServiceException, AaException, ItemInvalidException;
}
