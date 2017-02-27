/*
 * 
 * CDDL HEADER START
 * 
 * The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with
 * the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or
 * http://www.escidoc.org/license. See the License for the specific language governing permissions
 * and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License
 * file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with
 * the fields enclosed by brackets "[]" replaced with your own identifying information: Portions
 * Copyright [yyyy] [name of copyright owner]
 * 
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */
package de.mpg.mpdl.inge.pubman.qualityAssurance;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.xml.rpc.ServiceException;

import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException;
import de.escidoc.www.services.om.ItemHandler;
import de.mpg.mpdl.inge.framework.ServiceLocator;
import de.mpg.mpdl.inge.model.referenceobjects.ItemRO;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.TaskParamVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransforming;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.model.xmltransforming.logging.LogMethodDurationInterceptor;
import de.mpg.mpdl.inge.model.xmltransforming.logging.LogStartEndInterceptor;
import de.mpg.mpdl.inge.pubman.QualityAssurance;
import de.mpg.mpdl.inge.pubman.exceptions.ExceptionHandler;
import de.mpg.mpdl.inge.pubman.exceptions.PubItemNotFoundException;
import de.mpg.mpdl.inge.pubman.exceptions.PubItemStatusInvalidException;
import de.mpg.mpdl.inge.pubman.logging.ApplicationLog;
import de.mpg.mpdl.inge.pubman.logging.PMLogicMessages;

/**
 * EJB implementation of the QualityAssurance interface
 * 
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */

@Remote(QualityAssurance.class)
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@Interceptors({LogStartEndInterceptor.class, LogMethodDurationInterceptor.class})
public class QualityAssuranceBean implements QualityAssurance {

  /**
   * A XmlTransforming instance.
   */
  @EJB
  private XmlTransforming xmlTransforming;

//  public List<PubItemVO> searchForQAWorkspace(String contextobjId, String state, AccountUserVO user)
//      throws TechnicalException, ServiceException, MissingMethodParameterException,
//      ContextNotFoundException, InvalidXmlException, AuthenticationException,
//      AuthorizationException, SystemException, RemoteException, URISyntaxException {
//
//    ItemHandler itemHandler = ServiceLocator.getItemHandler(user.getHandle());
//
//    FilterTaskParamVO filter = new FilterTaskParamVO();
//
//    Filter f1 = filter.new ItemStatusFilter(State.valueOf(state));
//    filter.getFilterList().add(f1);
//
//    Filter f3 =
//        filter.new FrameworkItemTypeFilter(
//            PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication"));
//    filter.getFilterList().add(f3);
//
//    Filter f4 = filter.new ContextFilter(contextobjId);
//    filter.getFilterList().add(f4);
//
//    // every public state except withdrawn
//    Filter f5 = filter.new ItemPublicStatusFilter(State.IN_REVISION);
//    filter.getFilterList().add(f5);
//    Filter f6 = filter.new ItemPublicStatusFilter(State.PENDING);
//    filter.getFilterList().add(f6);
//    Filter f7 = filter.new ItemPublicStatusFilter(State.SUBMITTED);
//    filter.getFilterList().add(f7);
//    Filter f8 = filter.new ItemPublicStatusFilter(State.RELEASED);
//    filter.getFilterList().add(f8);
//
//    Filter f9 = filter.new LimitFilter("0");
//    filter.getFilterList().add(f9);
//
//    xmlTransforming.transformToFilterTaskParam(filter);
//
//    String xmlItemList = itemHandler.retrieveItems(new HashMap<String, String[]>()); // todo
//    List<PubItemVO> pubItemList = xmlTransforming.transformToPubItemList(xmlItemList);
//
//    return pubItemList;
//  }

//  /**
//   * {@inheritDoc}
//   */
//  public List<ContextVO> retrievePubContextsForModerator(AccountUserVO user)
//      throws SecurityException, TechnicalException {
//    if (user == null) {
//      throw new IllegalArgumentException(getClass()
//          + ".getPubCollectionListForDepositing: user is null.");
//    }
//    if (user.getReference() == null || user.getReference().getObjectId() == null) {
//      throw new IllegalArgumentException(getClass()
//          + ".getPubCollectionListForDepositing: user reference does not contain an objectId");
//    }
//
//    try {
//      // Create filter
//      FilterTaskParamVO filterParam = new FilterTaskParamVO();
//
//      RoleFilter roleFilter =
//          filterParam.new RoleFilter("escidoc:role-moderator", user.getReference());
//      filterParam.getFilterList().add(roleFilter);
//      FrameworkContextTypeFilter typeFilter = filterParam.new FrameworkContextTypeFilter("PubMan");
//      filterParam.getFilterList().add(typeFilter);
//
//      /*
//       * PubCollectionStatusFilter statusFilter = filterParam.new
//       * PubCollectionStatusFilter(ContextVO.State.OPENED);
//       * filterParam.getFilterList().add(statusFilter);
//       */
//
//      // ... and transform filter to xml
//      xmlTransforming.transformToFilterTaskParam(filterParam);
//
//      HashMap<String, String[]> filterMap = filterParam.toMap();
//
//      // Get context list
//      String contextList =
//          ServiceLocator.getContextHandler(user.getHandle()).retrieveContexts(filterMap);
//      // ... and transform to PubCollections.
//      return xmlTransforming.transformToContextList(contextList);
//
//    } catch (Exception e) {
//      // No business exceptions expected.
//      ExceptionHandler.handleException(e, "PubItemDepositing.getPubCollectionListForDepositing");
//      throw new TechnicalException(e);
//    }
//  }

  /**
   * {@inheritDoc}
   */
  // TODO: TaskParamVO ersetzen (siehe PubItemDepositingBean, PubItemPublishingBean)
  public PubItemVO revisePubItem(ItemRO pubItemRef, String comment, AccountUserVO user)
      throws ServiceException, TechnicalException, PubItemStatusInvalidException,
      SecurityException, PubItemNotFoundException {
    
    if (pubItemRef == null) {
      throw new IllegalArgumentException(getClass() + ".submitPubItem: pubItem is null.");
    }
    
    if (user == null) {
      throw new IllegalArgumentException(getClass() + ".submitPubItem: user is null.");
    }

    ItemHandler itemHandler;
    try {
      itemHandler = ServiceLocator.getItemHandler(user.getHandle());
    } catch (Exception e) {
      throw new TechnicalException(e);
    }

    PubItemVO pubItemActual = null;
    try {
      TaskParamVO taskParam = new TaskParamVO(pubItemRef.getModificationDate(), comment);
      itemHandler.revise(pubItemRef.getObjectId(), xmlTransforming.transformToTaskParam(taskParam));
      
      String item = itemHandler.retrieve(pubItemRef.getObjectId());
      pubItemActual = xmlTransforming.transformToPubItem(item);
      
      ApplicationLog.info(PMLogicMessages.PUBITEM_REVISED, new Object[] {pubItemRef.getObjectId(),
          user.getUserid()});
    } catch (InvalidStatusException e) {
      throw new PubItemStatusInvalidException(pubItemRef, e);
    } catch (ItemNotFoundException e) {
      throw new PubItemNotFoundException(pubItemRef, e);
    } catch (Exception e) {
      ExceptionHandler.handleException(e, "QualityAssuranceBean.revisePubItem");
    }
    
    return pubItemActual;
  }

//  /**
//   * {@inheritDoc}
//   */
//  public List<ContextVO> retrieveYearbookContexts(AccountUserVO user) throws SecurityException,
//      TechnicalException {
//    if (user == null) {
//      throw new IllegalArgumentException(getClass()
//          + ".getPubCollectionListForDepositing: user is null.");
//    }
//    if (user.getReference() == null || user.getReference().getObjectId() == null) {
//      throw new IllegalArgumentException(getClass()
//          + ".getPubCollectionListForDepositing: user reference does not contain an objectId");
//    }
//
//    try {
//      // Create filter
//      FilterTaskParamVO filterParam = new FilterTaskParamVO();
//
//      RoleFilter roleFilter =
//          filterParam.new RoleFilter("escidoc:role-depositor", user.getReference());
//      filterParam.getFilterList().add(roleFilter);
//      FrameworkContextTypeFilter typeFilter =
//          filterParam.new FrameworkContextTypeFilter("yearbook");
//      filterParam.getFilterList().add(typeFilter);
//
//      /*
//       * PubCollectionStatusFilter statusFilter = filterParam.new
//       * PubCollectionStatusFilter(ContextVO.State.OPENED);
//       * filterParam.getFilterList().add(statusFilter);
//       */
//
//      // ... and transform filter to xml
//      xmlTransforming.transformToFilterTaskParam(filterParam);
//
//      HashMap<String, String[]> filterMap = filterParam.toMap();
//
//      // Get context list
//      String contextList =
//          ServiceLocator.getContextHandler(user.getHandle()).retrieveContexts(filterMap);
//      // ... and transform to PubCollections.
//      return xmlTransforming.transformToContextList(contextList);
//
//    } catch (Exception e) {
//      // No business exceptions expected.
//      ExceptionHandler.handleException(e, "PubItemDepositing.retrieveYearbookContexts");
//      throw new TechnicalException(e);
//    }
//  }

//  /**
//   * {@inheritDoc}
//   */
//  public List<ContextVO> retrieveYearbookContextForModerator(AccountUserVO user)
//      throws SecurityException, TechnicalException {
//    if (user == null) {
//      throw new IllegalArgumentException(getClass()
//          + ".getPubCollectionListForDepositing: user is null.");
//    }
//    if (user.getReference() == null || user.getReference().getObjectId() == null) {
//      throw new IllegalArgumentException(getClass()
//          + ".getPubCollectionListForDepositing: user reference does not contain an objectId");
//    }
//
//    try {
//      // Create filter
//      FilterTaskParamVO filterParam = new FilterTaskParamVO();
//
//      RoleFilter roleFilter =
//          filterParam.new RoleFilter("escidoc:role-moderator", user.getReference());
//      filterParam.getFilterList().add(roleFilter);
//      FrameworkContextTypeFilter typeFilter =
//          filterParam.new FrameworkContextTypeFilter("yearbook");
//      filterParam.getFilterList().add(typeFilter);
//
//      /*
//       * PubCollectionStatusFilter statusFilter = filterParam.new
//       * PubCollectionStatusFilter(ContextVO.State.OPENED);
//       * filterParam.getFilterList().add(statusFilter);
//       */
//
//      // ... and transform filter to xml
//      xmlTransforming.transformToFilterTaskParam(filterParam);
//
//      HashMap<String, String[]> filterMap = filterParam.toMap();
//
//      // Get context list
//      String contextList =
//          ServiceLocator.getContextHandler(user.getHandle()).retrieveContexts(filterMap);
//      // ... and transform to PubCollections.
//      return xmlTransforming.transformToContextList(contextList);
//
//    } catch (Exception e) {
//      // No business exceptions expected.
//      ExceptionHandler.handleException(e, "PubItemDepositing.getPubCollectionListForDepositing");
//      throw new TechnicalException(e);
//    }
//  }
}
