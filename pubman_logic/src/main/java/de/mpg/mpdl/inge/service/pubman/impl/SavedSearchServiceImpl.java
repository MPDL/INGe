package de.mpg.mpdl.inge.service.pubman.impl;

import de.mpg.mpdl.inge.db.repository.IdentifierProviderServiceImpl;
import de.mpg.mpdl.inge.db.repository.SavedSearchRepository;
import de.mpg.mpdl.inge.es.dao.GenericDaoEs;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.SavedSearchDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.aa.Principal;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.SavedSearchService;
import java.util.Date;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SavedSearchServiceImpl extends GenericServiceImpl<SavedSearchDbVO, String> implements SavedSearchService {

  private static final Logger logger = LogManager.getLogger(SavedSearchServiceImpl.class);


  @Autowired
  private AuthorizationService aaService;

  @Autowired
  private IdentifierProviderServiceImpl idProviderService;

  @Autowired
  private SavedSearchRepository savedSearchRepository;


  @Transactional(readOnly = true, rollbackFor = Throwable.class)
  public List<SavedSearchDbVO> getAll(String token)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    Principal principal = this.aaService.checkLoginRequired(token);
    this.checkAa("getAll", principal);
    return savedSearchRepository.findByCreatorObjectIdOrderByLastModificationDateDesc(principal.getUserAccount().getObjectId());
  }

  @Override
  protected SavedSearchDbVO createEmptyDbObject() {
    return new SavedSearchDbVO();
  }

  @Override
  protected List<String> updateObjectWithValues(SavedSearchDbVO givenObject, SavedSearchDbVO objectToBeUpdated, AccountUserDbVO userAccount,
      boolean create) throws IngeApplicationException {
    if (null == givenObject.getName() || givenObject.getName().trim().isEmpty()) {
      throw new IngeApplicationException("A name is required");
    }
    if (null == givenObject.getSearchForm()) {
      throw new IngeApplicationException("A search form is required");
    }
    objectToBeUpdated.setName(givenObject.getName());
    objectToBeUpdated.setSearchForm(givenObject.getSearchForm());

    if (create) {
      objectToBeUpdated.setObjectId(this.idProviderService.getNewId(IdentifierProviderServiceImpl.ID_PREFIX.SAVED_SEARCH));
    }

    return null;
  }

  @Override
  protected JpaRepository<SavedSearchDbVO, String> getDbRepository() {
    return this.savedSearchRepository;
  }

  @Override
  protected GenericDaoEs<SavedSearchDbVO> getElasticDao() {
    return null;
  }

  @Override
  protected String getObjectId(SavedSearchDbVO savedSearchDbVO) {
    return savedSearchDbVO.getObjectId();
  }

  @Override
  protected Date getModificationDate(SavedSearchDbVO savedSearchDbVO) {
    return savedSearchDbVO.getLastModificationDate();
  }



  /*
  @Transactional(rollbackFor = Throwable.class)
  public SavedSearchDbVO saveSearch(SavedSearchDbVO searchToSave, String token) throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    Principal principal = this.aaService.checkLoginRequired(token);
    updateWithTechnicalMetadata(searchToSave, principal.getUserAccount(), true);
    try {
    savedSearchRepository.saveAndFlush(searchToSave);
    } catch (DataAccessException e) {
      handleDBException(e);
    }
    return searchToSave;
  }
  
  @Transactional(rollbackFor = Throwable.class)
  public SavedSearchDbVO editSavedSearch(SavedSearchDbVO searchToSave, String token) throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    Principal principal = this.aaService.checkLoginRequired(token);
  
    SavedSearchDbVO existingSearch = savedSearchRepository.findById(searchToSave.getObjectId()).orElse(null);
    if(existingSearch==null) {
      throw new IngeTechnicalException("Search with id " + searchToSave.getObjectId() + " not found.");
    }
  
    existingSearch.setName(searchToSave.getName());
    existingSearch.setSearchForm(searchToSave.getSearchForm());
  
    updateWithTechnicalMetadata(searchToSave, principal.getUserAccount(), false);
    try {
      savedSearchRepository.saveAndFlush(searchToSave);
    } catch (DataAccessException e) {
      handleDBException(e);
    }
    return searchToSave;
  }
  
  @Transactional(readOnly = true, rollbackFor = Throwable.class)
  public SavedSearchDbVO getSavedSearch(String id, String token) throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    //Principal principal = this.aaService.checkLoginRequired(token);
    return savedSearchRepository.findById(id).orElse(null);
  }
  
  
  
  @Transactional(readOnly = true, rollbackFor = Throwable.class)
  public void deleteSavedSearch(String id, String token) throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    Principal principal = this.aaService.checkLoginRequired(token);
    SavedSearchDbVO searchToDelete = savedSearchRepository.findById(id).orElse(null);
    this.aaService.
    if(searchToDelete==null) {
      throw new IngeTechnicalException("Search with id " + searchToDelete.getObjectId() + " not found.");
    }
  
    savedSearchRepository.deleteById(id);
  }
  
   */
}
