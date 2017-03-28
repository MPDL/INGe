package de.mpg.mpdl.inge.services;

import de.mpg.mpdl.inge.model.valueobjects.SearchQueryVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;

public interface SearchInterface<QueryObject> {

  public SearchRetrieveResponseVO searchForPubItems(SearchQueryVO<QueryObject> searchQuery)
      throws IngeServiceException;

}
