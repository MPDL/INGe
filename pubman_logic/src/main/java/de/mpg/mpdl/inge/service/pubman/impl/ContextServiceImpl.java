package de.mpg.mpdl.inge.service.pubman.impl;

import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.mpg.mpdl.inge.dao.ContextDao;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.service.exceptions.AaException;
import de.mpg.mpdl.inge.service.exceptions.ValidationException;
import de.mpg.mpdl.inge.service.pubman.ContextService;
import de.mpg.mpdl.inge.services.IngeServiceException;

@Service
public class ContextServiceImpl implements ContextService {

  @Autowired
  private ContextDao<QueryBuilder> contextDao;

  @Override
  public ContextVO create(ContextVO contextVO, String userToken) throws IngeServiceException,
      AaException, ValidationException {
    contextDao.create(contextVO.getReference().getObjectId(), contextVO);
    return contextDao.get(contextVO.getReference().getObjectId());
  }

  @Override
  public ContextVO update(ContextVO contextVO, String userToken) throws IngeServiceException,
      AaException, ValidationException {
    contextDao.update(contextVO.getReference().getObjectId(), contextVO);
    return contextDao.get(contextVO.getReference().getObjectId());
  }

  @Override
  public void delete(String id, String userToken) throws IngeServiceException, AaException {
    contextDao.delete(id);

  }

  @Override
  public ContextVO get(String id, String userToken) throws IngeServiceException, AaException {
    return contextDao.get(id);
  }

  @Override
  public SearchRetrieveResponseVO<ContextVO> search(SearchRetrieveRequestVO<QueryBuilder> srr,
      String userToken) throws IngeServiceException, AaException {
    return contextDao.search(srr);
  }



}
