package de.mpg.mpdl.inge.services;


import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;

public interface SearchInterface<QueryObject> {

  public SearchRetrieveResponseVO searchForPubItems(SearchRetrieveRequestVO<QueryObject> searchQuery)
      throws IngeServiceException;
  
  public SearchRetrieveResponseVO searchForContexts(SearchRetrieveRequestVO<QueryObject> searchQuery)
      throws IngeServiceException;
  
  public SearchRetrieveResponseVO searchForOrganizations(SearchRetrieveRequestVO<QueryObject> searchQuery)
      throws IngeServiceException;
  


}
