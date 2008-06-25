/*
*
* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.de/license.
* See the License for the specific language governing permissions
* and limitations under the License.
*
* When distributing Covered Code, include this CDDL HEADER in each
* file and include the License file at license/ESCIDOC.LICENSE.
* If applicable, add the following below this CDDL HEADER, with the
* fields enclosed by brackets "[]" replaced with your own identifying
* information: Portions Copyright [yyyy] [name of copyright owner]
*
* CDDL HEADER END
*/

/*
* Copyright 2006-2008 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 
package de.mpg.escidoc.services.pubman.qualityAssurance;

import java.io.IOException;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;
import org.jboss.annotation.ejb.RemoteBinding;

import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.www.services.om.ContextHandler;
import de.escidoc.www.services.om.ItemHandler;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.logging.LogMethodDurationInterceptor;
import de.mpg.escidoc.services.common.logging.LogStartEndInterceptor;
import de.mpg.escidoc.services.common.referenceobjects.ItemRO;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.ContextVO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.TaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.ValueObject;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.Filter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.FrameworkContextTypeFilter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.ObjectTypeFilter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.PubCollectionStatusFilter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.RoleFilter;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.services.pubman.QualityAssurance;
import de.mpg.escidoc.services.pubman.exceptions.ExceptionHandler;
import de.mpg.escidoc.services.pubman.exceptions.PubItemNotFoundException;
import de.mpg.escidoc.services.pubman.exceptions.PubItemStatusInvalidException;
import de.mpg.escidoc.services.pubman.logging.ApplicationLog;
import de.mpg.escidoc.services.pubman.logging.PMLogicMessages;
import de.mpg.escidoc.services.pubman.searching.ParseException;

/**
 * EJB implementation of the QualityAssurance interface
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */

@Remote
@RemoteBinding(jndiBinding = QualityAssurance.SERVICE_NAME)
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@Interceptors( { LogStartEndInterceptor.class, LogMethodDurationInterceptor.class })
public class QualityAssuranceBean implements QualityAssurance
{
    
    private static final Logger logger = Logger.getLogger(QualityAssuranceBean.class);
    
    /**
     * A XmlTransforming instance.
     */
    @EJB
    private XmlTransforming xmlTransforming;
    
   

    public List<PubItemVO> searchForQAWorkspace(String contextobjId, String state, AccountUserVO user) throws ParseException, TechnicalException, ServiceException, MissingMethodParameterException, ContextNotFoundException, InvalidXmlException, AuthenticationException, AuthorizationException, SystemException, RemoteException, URISyntaxException
    {
        
        
        ContextHandler contextHandler = ServiceLocator.getContextHandler(user.getHandle());
        ItemHandler itemHandler = ServiceLocator.getItemHandler(user.getHandle());
        
          
        FilterTaskParamVO filter = new FilterTaskParamVO();
        Filter f1 = filter.new ItemStatusFilter(PubItemVO.State.valueOf(state));
        filter.getFilterList().add(f1);
        
        try
        {
            Filter f3 = filter.new FrameworkItemTypeFilter(PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication"));
            filter.getFilterList().add(f3);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        
        Filter f4 = filter.new ContextFilter(contextobjId);
        filter.getFilterList().add(f4);
       
        String xmlFilter = xmlTransforming.transformToFilterTaskParam(filter);
       
        logger.debug("Filter: " + xmlFilter);

        String xmlItemList = itemHandler.retrieveItems(xmlFilter);
        List<PubItemVO> pubItemList = xmlTransforming.transformToPubItemList(xmlItemList);
        return pubItemList;
           

    }
    
    /**
     * {@inheritDoc}
     */
    public List<ContextVO> retrievePubContextsForModerator(AccountUserVO user) throws SecurityException, TechnicalException
    {
        if (user == null)
        {
            throw new IllegalArgumentException(getClass() + ".getPubCollectionListForDepositing: user is null.");
        }
        if (user.getReference() == null || user.getReference().getObjectId() == null)
        {
            throw new IllegalArgumentException(getClass() + ".getPubCollectionListForDepositing: user reference does not contain an objectId");
        }

        try
        {
            // Create filter
            FilterTaskParamVO filterParam = new FilterTaskParamVO();

            RoleFilter roleFilter = filterParam.new RoleFilter("Moderator", user.getReference());
            filterParam.getFilterList().add(roleFilter);
            FrameworkContextTypeFilter typeFilter = filterParam.new FrameworkContextTypeFilter("PubMan");
            filterParam.getFilterList().add(typeFilter);
            PubCollectionStatusFilter statusFilter = filterParam.new PubCollectionStatusFilter(ContextVO.State.OPENED);
            filterParam.getFilterList().add(statusFilter);

            // ... and transform filter to xml
            String filterString = xmlTransforming.transformToFilterTaskParam(filterParam);

            // Get context list
            String contextList = ServiceLocator.getContextHandler(user.getHandle()).retrieveContexts(filterString);
            // ... and transform to PubCollections.
            return xmlTransforming.transformToContextList(contextList);

        }
        catch (Exception e)
        {
            // No business exceptions expected.
            ExceptionHandler.handleException(e, "PubItemDepositing.getPubCollectionListForDepositing");
            throw new TechnicalException(e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public PubItemVO revisePubItem(ItemRO pubItemRef, String reviseComment, AccountUserVO user) throws ServiceException, TechnicalException, PubItemStatusInvalidException, SecurityException, PubItemNotFoundException
    {
        if (pubItemRef == null)
        {
            throw new IllegalArgumentException(getClass() + ".submitPubItem: pubItem is null.");
        }
        if (user == null)
        {
            throw new IllegalArgumentException(getClass() + ".submitPubItem: user is null.");
        }
        
        ItemHandler itemHandler;
        PubItemVO pubItemActual = null;
        try
        {
            itemHandler = ServiceLocator.getItemHandler(user.getHandle());
        }
        catch (Exception e)
        {
            throw new TechnicalException(e);
        }
        
        try
        {
            TaskParamVO taskParam = new TaskParamVO(pubItemRef.getModificationDate(), reviseComment);
            itemHandler.revise(pubItemRef.getObjectId(), xmlTransforming.transformToTaskParam(taskParam));
            ApplicationLog.info(PMLogicMessages.PUBITEM_REVISED, new Object[] { pubItemRef.getObjectId(), user.getUserid() });
            
            String item = itemHandler.retrieve(pubItemRef.getObjectId());
            pubItemActual = xmlTransforming.transformToPubItem(item);
        }
        catch(InvalidStatusException e)
        {
            throw new PubItemStatusInvalidException(pubItemRef, e);
        }
        catch (ItemNotFoundException e)
        {
            throw new PubItemNotFoundException(pubItemRef, e);
        }
        catch (Exception e)
        {
            ExceptionHandler.handleException(e, "QualityAssuranceBean.revisePubItem");
        }
        return pubItemActual;
    }
}
