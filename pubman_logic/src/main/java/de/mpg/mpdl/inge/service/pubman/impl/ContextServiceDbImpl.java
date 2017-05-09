package de.mpg.mpdl.inge.service.pubman.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.index.query.QueryBuilder;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import de.mpg.mpdl.inge.dao.ContextDao;
import de.mpg.mpdl.inge.db.repository.ContextRepository;
import de.mpg.mpdl.inge.inge_validation.exception.ItemInvalidException;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.service.exceptions.AaException;
import de.mpg.mpdl.inge.service.pubman.ContextService;
import de.mpg.mpdl.inge.service.util.EntityTransformer;
import de.mpg.mpdl.inge.services.IngeServiceException;

@Service
@Primary
public class ContextServiceDbImpl implements ContextService {

  private final static Logger logger = LogManager.getLogger();

  @Autowired
  private ContextDao<QueryBuilder> contextDao;

  @Autowired
  private ContextRepository contextRepository;

  @PersistenceContext
  EntityManager entityManager;

  @Override
  public ContextVO create(ContextVO contextVO, String authenticationToken)
      throws IngeServiceException, AaException, ItemInvalidException {
    de.mpg.mpdl.inge.db.model.valueobjects.ContextDbVO contextToSave =
        EntityTransformer.transformToNew(contextVO);
    contextRepository.save(contextToSave);
    ContextVO contextToReturn = EntityTransformer.transformToOld(contextToSave);
    contextDao.create(contextVO.getReference().getObjectId(), contextToReturn);
    return contextToReturn;
  }

  @Override
  public ContextVO update(ContextVO contextVO, String authenticationToken)
      throws IngeServiceException, AaException, ItemInvalidException {

    de.mpg.mpdl.inge.db.model.valueobjects.ContextDbVO contextToSave =
        EntityTransformer.transformToNew(contextVO);
    contextRepository.save(contextToSave);
    ContextVO contextToReturn = EntityTransformer.transformToOld(contextToSave);
    contextDao.update(contextVO.getReference().getObjectId(), contextVO);
    return contextToReturn;
  }

  @Override
  public void delete(String id, String authenticationToken) throws IngeServiceException,
      AaException {
    contextRepository.delete(id);
    contextDao.delete(id);

  }

  @Override
  public ContextVO get(String id, String authenticationToken) throws IngeServiceException,
      AaException {
    return EntityTransformer.transformToOld(contextRepository.findOne(id));
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
}
