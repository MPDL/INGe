package de.mpg.mpdl.inge.service.pubman.impl;

import java.util.Date;

import org.apache.log4j.Logger;
import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.mpg.mpdl.inge.dao.ContextDao;
import de.mpg.mpdl.inge.dao.PubItemDao;
import de.mpg.mpdl.inge.inge_validation.ItemValidatingService;
import de.mpg.mpdl.inge.inge_validation.exception.ItemInvalidException;
import de.mpg.mpdl.inge.inge_validation.util.ValidationPoint;
import de.mpg.mpdl.inge.model.referenceobjects.AccountUserRO;
import de.mpg.mpdl.inge.model.referenceobjects.ItemRO;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO.State;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.exceptions.AaException;
import de.mpg.mpdl.inge.service.exceptions.ValidationException;
import de.mpg.mpdl.inge.service.identifier.IdentifierProviderServiceImpl;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.services.IngeServiceException;

@Service
public class PubItemServiceImpl implements PubItemService {

  private final static Logger logger = Logger.getLogger(PubItemServiceImpl.class);

  @Autowired
  private PubItemDao<QueryBuilder> pubItemDao;

  @Autowired
  private AuthorizationService aaService;

  @Autowired
  private ContextDao<QueryBuilder> contextDao;

  @Autowired
  private IdentifierProviderServiceImpl idProviderService;

  @Override
  public PubItemVO create(PubItemVO pubItemVO, String userToken) throws IngeServiceException,
      AaException, ValidationException {

    AccountUserVO userAccount = aaService.checkLoginRequired(userToken);
    ContextVO context = contextDao.get(pubItemVO.getContext().getObjectId());

    PubItemVO pubItemToCreate = new PubItemVO(pubItemVO);

    Date currentDate = new Date();
    pubItemToCreate.setContentModel(null);
    pubItemToCreate.setContentModelHref(null);
    pubItemToCreate.setLatestRelease(null);
    pubItemToCreate.setLatestVersion(null);
    pubItemToCreate.setLockStatus(null);


    pubItemToCreate.setContext(context.getReference());
    pubItemToCreate.setOwner(userAccount.getReference());
    pubItemToCreate.setCreationDate(currentDate);
    pubItemToCreate.setPublicStatus(State.PENDING);

    ItemRO itemRO = new ItemRO();
    pubItemToCreate.setVersion(itemRO);
    itemRO.setVersionNumber(1);
    itemRO.setModifiedByRO(userAccount.getReference());
    itemRO.setState(State.PENDING);
    itemRO.setModificationDate(currentDate);

    aaService.checkPubItemAa(pubItemToCreate, context, userAccount, "create");

    try {
      ItemValidatingService.validateItemObject(pubItemToCreate, ValidationPoint.SAVE);
    } catch (de.mpg.mpdl.inge.inge_validation.exception.ValidationException e) {
      throw new IngeServiceException(e);
    } catch (ItemInvalidException e) {
      throw new ValidationException(e.getReport(), e);
    }


    String id = idProviderService.getNewId();
    String fullId = id + "_1";
    itemRO.setObjectId(id);


    pubItemDao.create(fullId, pubItemToCreate);
    logger.info("PubItem " + fullId + "successfully created");

    return pubItemDao.get(fullId);
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
