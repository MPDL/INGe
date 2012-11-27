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
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */

package de.mpg.escidoc.pubman.contextList;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.pubman.util.PubContextVOPresentation;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.referenceobjects.ItemRO;
import de.mpg.escidoc.services.common.valueobjects.ContextVO.State;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.ItemRefFilter;
import de.mpg.escidoc.services.common.valueobjects.GrantVO;
import de.mpg.escidoc.services.common.valueobjects.GrantVO.PredefinedRoles;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Keeps all attributes that are used for the whole session by the CollectionList.
 * @author:  Thomas Diebäcker, created 12.10.2007
 * @version: $Revision$ $LastChangedDate$
 */
public class ContextListSessionBean extends FacesBean
{
    public static final String BEAN_NAME = "ContextListSessionBean";
    private static Logger logger = Logger.getLogger(ContextListSessionBean.class);

    private List<PubContextVOPresentation> depositorContextList= new ArrayList<PubContextVOPresentation>();
    private List<PubContextVOPresentation> moderatorContextList= new ArrayList<PubContextVOPresentation>();
    private List<PubContextVOPresentation> yearbookContextList= new ArrayList<PubContextVOPresentation>();
    private List<PubContextVOPresentation> yearbookModeratorContextList= new ArrayList<PubContextVOPresentation>();
    private List<PubContextVOPresentation> allPrivilegedContextList= new ArrayList<PubContextVOPresentation>();
    private LoginHelper loginHelper;

   // private UIXIterator contextIterator = new UIXIterator();

    /**
     * Public constructor.
     */
    public ContextListSessionBean()
    {
    }

    @Override
    public void init()
    {
        this.loginHelper = (LoginHelper)getSessionBean(LoginHelper.class);
        try {
            retrieveAllContextsForUser();
        }
        catch (Exception e)
        {
            logger.error("Could not create context list.", e);
        }
    }

    /**
     * Returns a reference to the scoped data bean (the ItemControllerSessionBean).
     * @return a reference to the scoped data bean
     */
    protected ItemControllerSessionBean getItemControllerSessionBean()
    {
        return (ItemControllerSessionBean)getSessionBean(ItemControllerSessionBean.class);
    }

    public List<PubContextVOPresentation> getDepositorContextList()
    {
        List<PubContextVOPresentation> newDepositorContextList = new ArrayList<PubContextVOPresentation> ();
        if ( getOpenContextsAvailable() )
        {
            for (PubContextVOPresentation context : this.depositorContextList)
            {
                if (context.getState() == State.OPENED)
                {
                    newDepositorContextList.add(context);
                }
            }
            setDepositorContextList(newDepositorContextList);
        }
        return this.depositorContextList;
    }
    
    public boolean getOpenContextsAvailable()
    {
        State state = State.CLOSED;
        for (PubContextVOPresentation context : this.depositorContextList)
        {
            if (context.getState() == State.OPENED)
            {
                state = State.OPENED;
            }
        }
        if (state == State.OPENED)
        {
            return true;
        }
        else 
        {
            return false;
        }
    }

    public int getDepositorContextListSize()
    {
        if (depositorContextList==null)
        {
            return 0;
        }
        else
        {
            return depositorContextList.size();
        }

    }

    public void setDepositorContextList(List<PubContextVOPresentation> contextList)
    {
        this.depositorContextList = contextList;
    }

    public PubContextVOPresentation getSelectedDepositorContext()
    {
        for (PubContextVOPresentation coll : depositorContextList) {
            if (coll.getSelected())
            {
                return coll;
            }
        }
        return null;
    }

    public List<PubContextVOPresentation> getModeratorContextList()
    {
        return moderatorContextList;
    }

    public int getModeratorContextListSize()
    {
        if (moderatorContextList==null)
        {
            return 0;
        }
        else
        {
            return moderatorContextList.size();
        }

    }

    public void setModeratorContextList(List<PubContextVOPresentation> moderatorContextList)
    {
        this.moderatorContextList = moderatorContextList;
    }

    /*
    public UIXIterator getContextIterator()
    {
        return contextIterator;
    }

    public void setContextIterator(UIXIterator contextIterator)
    {
        this.contextIterator = contextIterator;
    }
    */

    public void setYearbookContextList(List<PubContextVOPresentation> yearbookContextList)
    {
        this.yearbookContextList = yearbookContextList;
    }

    public List<PubContextVOPresentation> getYearbookContextList()
    {
        return yearbookContextList;
    }

    public int getYearbookContextListSize()
    {
        if (yearbookContextList==null)
        {
            return 0;
        }
        else
        {
            return yearbookContextList.size();
        }

    }

    public int getYearbookModeratorContextListSize()
    {
        if (yearbookModeratorContextList==null)
        {
            return 0;
        }
        else
        {
            return yearbookModeratorContextList.size();
        }

    }

    public void setYearbookModeratorContextList(
            List<PubContextVOPresentation> yearbookModeratorContextList) {
        this.yearbookModeratorContextList = yearbookModeratorContextList;
    }

    public List<PubContextVOPresentation> getYearbookModeratorContextList() {
        return yearbookModeratorContextList;
    }



    //TODO NBU: this method needs to be moved elsewhere here only to avoid common logic modification at present
    /**
     * @Retrieves A list of all contexts for which user has granted privileges @see LoginHelper.getUserGrants
     * @throws SecurityException
     * @throws TechnicalException
     */
    private void retrieveAllContextsForUser() throws SecurityException, TechnicalException
    {
        if (this.loginHelper.isLoggedIn() && this.loginHelper.getAccountUser().getGrants() != null){
            try
            {
                // Create filter
                FilterTaskParamVO filter = new FilterTaskParamVO();
                ItemRefFilter itmRefFilter = filter.new ItemRefFilter();
                filter.getFilterList().add(itmRefFilter);
                
                boolean hasGrants = false;

                for (GrantVO grant:this.loginHelper.getAccountUser().getGrants())
                {
                    if ( grant.getObjectRef() != null)
                    {
                        itmRefFilter.getIdList().add(new ItemRO(grant.getObjectRef()));
                        hasGrants=true;
                    }
                }
                
                // ... and transform filter to xml
                if (hasGrants){
                    XmlTransformingBean xmlTransforming = new XmlTransformingBean();

                    // Get context list
                    String contextList = ServiceLocator.getContextHandler(this.loginHelper.getAccountUser().getHandle()).retrieveContexts(filter.toMap());
                    // ... and transform to PubCollections.
                    this.allPrivilegedContextList= CommonUtils.convertToPubCollectionVOPresentationList(xmlTransforming.transformToContextList(contextList));
                }

                this.depositorContextList = new ArrayList<PubContextVOPresentation>();
                this.moderatorContextList = new ArrayList<PubContextVOPresentation>();
                this.yearbookContextList = new ArrayList<PubContextVOPresentation>();
                this.yearbookModeratorContextList = new ArrayList<PubContextVOPresentation>();

                for (PubContextVOPresentation context:this.allPrivilegedContextList)
                {
                    //TODO NBU: change this dummy looping once AccountUserVO provides method for isDepositor(ObjectRef)
                    //At present it only provides this function for Moderator and Privileged viewer

                    for (GrantVO grant:this.loginHelper.getAccountUser().getGrants())
                    {
                        if ((grant.getObjectRef() != null) && !grant.getObjectRef().equals(""))
                        {

                            if (!grant.getObjectRef().equals("") && grant.getObjectRef().equals(context.getReference().getObjectId()) &&
                                    grant.getRole().equals(PredefinedRoles.DEPOSITOR.frameworkValue()) &&
                                    context.getType().toLowerCase().equals(("PubMan".toLowerCase())))
                            {

                                this.depositorContextList.add(context);
                            }
                            if (!grant.getObjectRef().equals("") && grant.getObjectRef().equals(context.getReference().getObjectId()) &&
                                    grant.getRole().equals(PredefinedRoles.MODERATOR.frameworkValue()) &&
                                    context.getType().toLowerCase().equals(("PubMan".toLowerCase())))
                            {

                                this.moderatorContextList.add(context);
                            }

                            if (!grant.getObjectRef().equals("") && grant.getObjectRef().equals(context.getReference().getObjectId())  &&
                                    grant.getRole().equals(PredefinedRoles.DEPOSITOR.frameworkValue()) &&
                                    context.getType().toLowerCase().equals(("Yearbook".toLowerCase())))
                            {
                                this.yearbookContextList.add(context);
                            }

                            if (!grant.getObjectRef().equals("") && grant.getObjectRef().equals(context.getReference().getObjectId())  &&
                                    grant.getRole().equals(PredefinedRoles.MODERATOR.frameworkValue()) &&
                                    context.getType().toLowerCase().equals(("Yearbook".toLowerCase())))
                            {
                                this.yearbookModeratorContextList.add(context);
                            }
                        }
                    }
                }

            }
            catch (Exception e)
            {
                // No business exceptions expected.
                throw new TechnicalException(e);
            }
        }
    }


}
