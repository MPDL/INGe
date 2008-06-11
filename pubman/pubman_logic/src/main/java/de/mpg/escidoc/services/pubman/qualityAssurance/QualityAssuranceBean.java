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

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.xml.rpc.ServiceException;

import org.jboss.annotation.ejb.RemoteBinding;

import de.fiz.escidoc.common.exceptions.application.invalid.InvalidXmlException;
import de.fiz.escidoc.common.exceptions.application.missing.MissingMethodParameterException;
import de.fiz.escidoc.common.exceptions.application.notfound.ContextNotFoundException;
import de.fiz.escidoc.common.exceptions.application.security.AuthenticationException;
import de.fiz.escidoc.common.exceptions.application.security.AuthorizationException;
import de.fiz.escidoc.common.exceptions.application.security.SecurityException;
import de.fiz.escidoc.common.exceptions.system.SystemException;
import de.fiz.escidoc.om.ContextHandlerRemote;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.logging.LogMethodDurationInterceptor;
import de.mpg.escidoc.services.common.logging.LogStartEndInterceptor;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.ContextVO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.MemberListVO;
import de.mpg.escidoc.services.common.valueobjects.PubItemResultVO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.Filter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.FrameworkContextTypeFilter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.OwnerFilter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.PubCollectionStatusFilter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.RoleFilter;
import de.mpg.escidoc.services.common.valueobjects.ItemVO.State;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.services.pubman.PubItemSearching;
import de.mpg.escidoc.services.pubman.QualityAssurance;
import de.mpg.escidoc.services.pubman.exceptions.ExceptionHandler;
import de.mpg.escidoc.services.pubman.searching.ParseException;
import de.mpg.escidoc.services.pubman.searching.PubItemSearchingBean;

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
    
    /**
     * A XmlTransforming instance.
     */
    @EJB
    private XmlTransforming xmlTransforming;
    
   

    public List<PubItemVO> searchForQAWorkspace(String contextobjId, String state) throws ParseException, TechnicalException, ServiceException, MissingMethodParameterException, ContextNotFoundException, InvalidXmlException, AuthenticationException, AuthorizationException, SystemException, RemoteException
    {
        
        
        ContextHandlerRemote contextHandler = ServiceLocator.getContextHandler();
        
        /*  
        FilterTaskParamVO filter = new FilterTaskParamVO();
        Filter f1 = filter.new PubCollectionStatusFilter();
        filter.getFilterList().add(f1);
        
        Filter f2 = filter.new ObjectTypeFilter("item");
        filter.getFilterList().add(f2);
        String xmlFilter = xmlTransforming.transformToFilterTaskParam(filter);
       */
        String xmlFilter = "<param><filter name=\"object-type\">item</filter><filter name=\"public-status\">"+state.toLowerCase()+"</filter></param>";
        //String xmlFilter = "<param><filter name=\"object-type\">container</filter></param>";
        
        String memberList = contextHandler.retrieveMembers(contextobjId, xmlFilter);
        MemberListVO memberListVO = xmlTransforming.transformToMemberList(memberList);
        return memberListVO.getPubItemVOList();
           

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
            // Peter Broszeit: PubCollectionStatusFilter added.
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
}
