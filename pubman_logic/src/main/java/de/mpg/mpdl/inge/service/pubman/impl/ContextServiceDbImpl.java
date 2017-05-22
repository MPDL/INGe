package de.mpg.mpdl.inge.service.pubman.impl;

import java.util.ArrayList;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.elasticsearch.index.query.QueryBuilder;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.mpg.mpdl.inge.db.model.valueobjects.AccountUserDbRO;
import de.mpg.mpdl.inge.db.model.valueobjects.AffiliationDbRO;
import de.mpg.mpdl.inge.db.model.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.db.model.valueobjects.ContextDbVO.State;
import de.mpg.mpdl.inge.db.repository.ContextRepository;
import de.mpg.mpdl.inge.db.repository.IdentifierProviderServiceImpl;
import de.mpg.mpdl.inge.db.repository.IdentifierProviderServiceImpl.ID_PREFIX;
import de.mpg.mpdl.inge.es.dao.ContextDaoEs;
import de.mpg.mpdl.inge.inge_validation.exception.ItemInvalidException;
import de.mpg.mpdl.inge.model.exception.IngeServiceException;
import de.mpg.mpdl.inge.model.referenceobjects.AffiliationRO;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.exceptions.AaException;
import de.mpg.mpdl.inge.service.pubman.ContextService;
import de.mpg.mpdl.inge.service.util.EntityTransformer;

@Service
@Primary
public class ContextServiceDbImpl implements ContextService {
  private final static Logger logger = LogManager.getLogger(ContextServiceDbImpl.class);

  public final static String INDEX_OBJECT_ID = "reference.objectId";
  public final static String INDEX_STATE = "state";

  @Autowired
  private AuthorizationService aaService;

  @Autowired
  private IdentifierProviderServiceImpl idProviderService;

  @Autowired
  private ContextDaoEs<QueryBuilder> contextDao;

  @Autowired
  private ContextRepository contextRepository;

  @PersistenceContext
  EntityManager entityManager;

  @Override
  @Transactional
  public ContextVO create(ContextVO contextVO, String authenticationToken)
      throws IngeServiceException, AaException, ItemInvalidException {

    AccountUserVO userAccount = aaService.checkLoginRequired(authenticationToken);
    ContextDbVO contextToCreate = new ContextDbVO();
    updateContextWithValues(contextVO, contextToCreate, userAccount, true);
    checkAa(EntityTransformer.transformToOld(contextToCreate), userAccount, "create");
    contextToCreate = contextRepository.save(contextToCreate);
    ContextVO contextToReturn = EntityTransformer.transformToOld(contextToCreate);
    contextDao.create(contextToCreate.getObjectId(), contextToReturn);
    return contextToReturn;
  }


  private void updateContextWithValues(ContextVO givenContext, ContextDbVO toBeUpdatedContext,
      AccountUserVO userAccount, boolean createNew) throws AaException, IngeServiceException{
    Date currentDate = new Date();
    AccountUserDbRO mod = new AccountUserDbRO();
    mod.setName(userAccount.getName());
    mod.setObjectId(userAccount.getReference().getObjectId());


    toBeUpdatedContext.setAdminDescriptor(givenContext.getAdminDescriptor());

    toBeUpdatedContext.setDescription(givenContext.getDescription());
    toBeUpdatedContext.setLastModificationDate(currentDate);
    toBeUpdatedContext.setModifier(mod);
    toBeUpdatedContext.setName(givenContext.getName());
    
    if(givenContext.getName()==null || givenContext.getName().trim().isEmpty()) {
      throw new IngeServiceException("A name is required");
    }

    if (givenContext.getResponsibleAffiliations() != null) {
      toBeUpdatedContext.setResponsibleAffiliations(new ArrayList<AffiliationDbRO>());
      for (AffiliationRO aff : givenContext.getResponsibleAffiliations()) {
        AffiliationDbRO newAffRo = new AffiliationDbRO();
        newAffRo.setObjectId(aff.getObjectId());
        toBeUpdatedContext.getResponsibleAffiliations().add(newAffRo);
      }
    }
    toBeUpdatedContext.setType(givenContext.getType());


    if (createNew) {
      toBeUpdatedContext.setCreationDate(currentDate);
      toBeUpdatedContext.setCreator(mod);
      toBeUpdatedContext.setObjectId(idProviderService.getNewId(ID_PREFIX.CONTEXT));
      toBeUpdatedContext.setState(State.CREATED);
    }
  }

  @Override
  @Transactional
  public ContextVO update(ContextVO contextVO, String authenticationToken)
      throws IngeServiceException, AaException, ItemInvalidException {

    AccountUserVO userAccount = aaService.checkLoginRequired(authenticationToken);
    ContextDbVO contextToBeUpdated =
        contextRepository.findOne(contextVO.getReference().getObjectId());
    if (contextToBeUpdated == null) {
      throw new IngeServiceException("Context with given id not found.");
    }
    updateContextWithValues(contextVO, contextToBeUpdated, userAccount, false);

    checkAa(EntityTransformer.transformToOld(contextToBeUpdated), userAccount, "update");
    contextToBeUpdated = contextRepository.save(contextToBeUpdated);

    ContextVO contextToReturn = EntityTransformer.transformToOld(contextToBeUpdated);
    contextDao.update(contextToBeUpdated.getObjectId(), contextToReturn);
    return contextToReturn;
  }

  @Override
  @Transactional
  public void delete(String id, String authenticationToken) throws IngeServiceException,
      AaException {
    AccountUserVO userAccount = aaService.checkLoginRequired(authenticationToken);
    ContextDbVO contextToBeDeleted = contextRepository.findOne(id);
    checkAa(EntityTransformer.transformToOld(contextToBeDeleted), userAccount, "delete");
    contextRepository.delete(id);
    contextDao.delete(id);

  }

  @Override
  @Transactional(readOnly = true)
  public ContextVO get(String id, String authenticationToken) throws IngeServiceException,
      AaException {
    AccountUserVO userAccount = null;
    ContextVO context = EntityTransformer.transformToOld(contextRepository.findOne(id));
    if (authenticationToken != null) {
      userAccount = aaService.checkLoginRequired(authenticationToken);
    }

    checkAa(context, userAccount, "get");
    return context;
  }

  @Override
  public SearchRetrieveResponseVO<ContextVO> search(SearchRetrieveRequestVO<QueryBuilder> srr,
      String authenticationToken) throws IngeServiceException, AaException {
    return contextDao.search(srr);
  }

  public void reindex() {

    Query<de.mpg.mpdl.inge.db.model.valueobjects.ContextDbVO> query =
        (Query<de.mpg.mpdl.inge.db.model.valueobjects.ContextDbVO>) entityManager
            .createQuery("SELECT context FROM ContextVO context");
    query.setReadOnly(true);
    query.setFetchSize(1000);
    query.setCacheable(false);
    ScrollableResults results = query.scroll(ScrollMode.FORWARD_ONLY);

    while (results.next()) {
      try {
        de.mpg.mpdl.inge.db.model.valueobjects.ContextDbVO object =
            (de.mpg.mpdl.inge.db.model.valueobjects.ContextDbVO) results.get(0);
        ContextVO context = EntityTransformer.transformToOld(object);
        logger.info("Reindexing context " + context.getReference().getObjectId());
        contextDao.create(context.getReference().getObjectId(), context);
      } catch (Exception e) {
        logger.error("Error while reindexing ", e);
      }


    }

  }


  @Override
  @Transactional
  public ContextVO open(String id, String authenticationToken) throws IngeServiceException,
      AaException {
    return changeState(id, authenticationToken, State.OPENED);
  }


  @Override
  @Transactional
  public ContextVO close(String id, String authenticationToken) throws IngeServiceException,
      AaException {
    return changeState(id, authenticationToken, State.CLOSED);
  }

  private ContextVO changeState(String id, String authenticationToken, State state)
      throws IngeServiceException, AaException {
    AccountUserVO userAccount = aaService.checkLoginRequired(authenticationToken);
    ContextDbVO contextToBeUpdated = contextRepository.findOne(id);
    if (contextToBeUpdated == null) {
      throw new IngeServiceException("Context with given id " + id + " not found.");
    }

    checkAa(EntityTransformer.transformToOld(contextToBeUpdated), userAccount,
        (state == State.OPENED ? "open" : "close"));

    contextToBeUpdated.setState(state);

    contextToBeUpdated = contextRepository.save(contextToBeUpdated);

    ContextVO contextToReturn = EntityTransformer.transformToOld(contextToBeUpdated);
    contextDao.update(contextToBeUpdated.getObjectId(), contextToReturn);
    return contextToReturn;
  }

  private void checkAa(ContextVO context, AccountUserVO userAccount, String method)
      throws AaException {
    aaService.checkAuthorization("de.mpg.mpdl.inge.service.pubman.ContextService", method, context,
        userAccount);

  }
}
