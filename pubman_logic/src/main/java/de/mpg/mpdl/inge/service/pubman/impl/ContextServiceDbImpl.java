package de.mpg.mpdl.inge.service.pubman.impl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.mpg.mpdl.inge.db.repository.ContextRepository;
import de.mpg.mpdl.inge.db.repository.IdentifierProviderServiceImpl;
import de.mpg.mpdl.inge.db.repository.IdentifierProviderServiceImpl.ID_PREFIX;
import de.mpg.mpdl.inge.es.dao.ContextDaoEs;
import de.mpg.mpdl.inge.es.dao.GenericDaoEs;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.aa.Principal;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.ContextService;
import de.mpg.mpdl.inge.service.pubman.ReindexListener;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
@Primary
public class ContextServiceDbImpl extends GenericServiceImpl<ContextDbVO, String> implements ContextService, ReindexListener {
  private static final Logger logger = Logger.getLogger(ContextServiceDbImpl.class);

  public final static String INDEX_OBJECT_ID = "objectId";
  public final static String INDEX_STATE = "state";
  public final static String INDEX_AFILLIATIONS_OBJECT_ID = "responsibleAffiliations.objectId";

  @Autowired
  private AuthorizationService aaService;

  @Autowired
  private IdentifierProviderServiceImpl idProviderService;

  @Autowired
  private ContextDaoEs contextDao;

  @Autowired
  private ContextRepository contextRepository;

  @PersistenceContext
  EntityManager entityManager;



  @Override
  @Transactional(rollbackFor = Throwable.class)
  public ContextDbVO open(String id, Date modificationDate, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    return changeState(id, modificationDate, authenticationToken, ContextDbVO.State.OPENED);
  }


  @Override
  @Transactional(rollbackFor = Throwable.class)
  public ContextDbVO close(String id, Date modificationDate, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    return changeState(id, modificationDate, authenticationToken, ContextDbVO.State.CLOSED);
  }

  private ContextDbVO changeState(String id, Date modificationDate, String authenticationToken, ContextDbVO.State state)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    Principal principal = aaService.checkLoginRequired(authenticationToken);
    ContextDbVO contextDbToBeUpdated = contextRepository.findById(id).orElse(null);
    if (contextDbToBeUpdated == null) {
      throw new IngeApplicationException("Context with given id " + id + " not found.");
    }


    checkEqualModificationDate(modificationDate, contextDbToBeUpdated.getLastModificationDate());

    checkAa((state == ContextDbVO.State.OPENED ? "open" : "close"), principal, contextDbToBeUpdated);

    contextDbToBeUpdated.setState(state);
    updateWithTechnicalMetadata(contextDbToBeUpdated, principal.getUserAccount(), false);

    try {
      contextDbToBeUpdated = contextRepository.saveAndFlush(contextDbToBeUpdated);
    } catch (DataAccessException e) {
      handleDBException(e);
    }

    getElasticDao().createImmediately(contextDbToBeUpdated.getObjectId(), contextDbToBeUpdated);
    return contextDbToBeUpdated;
  }



  @Override
  protected ContextDbVO createEmptyDbObject() {
    return new ContextDbVO();
  }



  @Override
  protected List<String> updateObjectWithValues(ContextDbVO givenContext, ContextDbVO toBeUpdatedContext, AccountUserDbVO userAccount,
      boolean createNew) throws IngeTechnicalException, IngeApplicationException {

    toBeUpdatedContext.setAllowedGenres(givenContext.getAllowedGenres());
    toBeUpdatedContext.setAllowedSubjectClassifications(givenContext.getAllowedSubjectClassifications());
    toBeUpdatedContext.setContactEmail(givenContext.getContactEmail());
    toBeUpdatedContext.setWorkflow(givenContext.getWorkflow());

    toBeUpdatedContext.setDescription(givenContext.getDescription());
    toBeUpdatedContext.setName(givenContext.getName());

    toBeUpdatedContext.setResponsibleAffiliations(givenContext.getResponsibleAffiliations());

    if (givenContext.getName() == null || givenContext.getName().trim().isEmpty()) {
      throw new IngeApplicationException("A name is required");
    }
    if (givenContext.getWorkflow() == null) {
      throw new IngeApplicationException("A workflow is required");
    }

    if (givenContext.getResponsibleAffiliations() == null || givenContext.getResponsibleAffiliations().isEmpty()) {
      throw new IngeApplicationException("A responsible affiliation is required");
    }



    if (createNew) {
      toBeUpdatedContext.setObjectId(idProviderService.getNewId(ID_PREFIX.CONTEXT));
      toBeUpdatedContext.setState(ContextDbVO.State.OPENED);
    }

    return null;

  }



  @Override
  protected JpaRepository<ContextDbVO, String> getDbRepository() {
    return contextRepository;
  }



  @Override
  protected String getObjectId(ContextDbVO object) {
    return object.getObjectId();
  }



  @Override
  protected GenericDaoEs<ContextDbVO> getElasticDao() {
    return contextDao;
  }


  @Override
  protected Date getModificationDate(ContextDbVO object) {
    return object.getLastModificationDate();
  }


  @Override
  @JmsListener(containerFactory = "queueContainerFactory", destination = "reindex-ContextDbVO")
  public void reindexListener(String id) throws IngeTechnicalException {
    reindex(id, false);

  }


}
