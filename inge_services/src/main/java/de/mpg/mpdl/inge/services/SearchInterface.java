package de.mpg.mpdl.inge.services;


import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;

public interface SearchInterface<QueryObject> {

  public SearchRetrieveResponseVO<PubItemVO> searchForPubItems(SearchRetrieveRequestVO<QueryObject> searchQuery)
      throws IngeServiceException;
  
  public SearchRetrieveResponseVO<ContextVO> searchForContexts(SearchRetrieveRequestVO<QueryObject> searchQuery)
      throws IngeServiceException;
  
  public SearchRetrieveResponseVO<AffiliationVO> searchForOrganizations(SearchRetrieveRequestVO<QueryObject> searchQuery)
      throws IngeServiceException;
  


}
