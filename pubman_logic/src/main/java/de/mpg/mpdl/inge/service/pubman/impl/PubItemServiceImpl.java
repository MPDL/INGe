package de.mpg.mpdl.inge.service.pubman.impl;

import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.mpg.mpdl.inge.dao.PubItemDao;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.service.exceptions.AaException;
import de.mpg.mpdl.inge.service.exceptions.ValidationException;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.services.IngeServiceException;

@Service
public class PubItemServiceImpl implements PubItemService {

  @Autowired
  private PubItemDao<QueryBuilder> pubItemDao;

  @Override
  public PubItemVO create(PubItemVO pubItemVO, String userToken) throws IngeServiceException,
      AaException, ValidationException {
    pubItemDao.create(pubItemVO.getVersion().getObjectId(), pubItemVO);
    return pubItemDao.get(pubItemVO.getVersion().getObjectId());
  }

  @Override
  public PubItemVO update(PubItemVO pubItemVO, String userToken) throws IngeServiceException,
      AaException, ValidationException {
    pubItemDao.update(pubItemVO.getVersion().getObjectId(), pubItemVO);
    return pubItemDao.get(pubItemVO.getVersion().getObjectId());
  }

  @Override
  public void delete(String id, String userToken) throws IngeServiceException, AaException {
    pubItemDao.delete(id);

  }

  @Override
  public PubItemVO get(String id, String userToken) throws IngeServiceException, AaException {
    return pubItemDao.get(id);
  }

  @Override
  public SearchRetrieveResponseVO<PubItemVO> search(SearchRetrieveRequestVO<QueryBuilder> srr,
      String userToken) throws IngeServiceException, AaException {
    return pubItemDao.search(srr);
  }

  @Override
  public PubItemVO submitPubItem(String pubItemId, String message, String userToken)
      throws IngeServiceException, AaException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public PubItemVO releasePubItem(String pubItemId, String message, String userToken)
      throws IngeServiceException, AaException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public PubItemVO withdrawPubItem(String pubItemId, String message, String userToken)
      throws IngeServiceException, AaException {
    // TODO Auto-generated method stub
    return null;
  }



}
