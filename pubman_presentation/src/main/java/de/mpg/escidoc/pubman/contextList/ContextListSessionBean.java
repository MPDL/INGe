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
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman.contextList;

import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.pubman.util.PubContextVOPresentation;
import de.mpg.escidoc.services.common.referenceobjects.ContextRO;
import de.mpg.escidoc.services.common.valueobjects.ContextVO;
import de.mpg.escidoc.services.pubman.QualityAssurance;

/**
 * Keeps all attributes that are used for the whole session by the CollectionList.
 * @author:  Thomas Diebäcker, created 12.10.2007
 * @version: $Revision: 1587 $ $LastChangedDate: 2007-11-20 10:54:36 +0100 (Di, 20 Nov 2007) $
 */
public class ContextListSessionBean extends FacesBean
{
    public static final String BEAN_NAME = "ContextListSessionBean";
    private static Logger logger = Logger.getLogger(ContextListSessionBean.class);

    private List<PubContextVOPresentation> depositorContextList = new ArrayList<PubContextVOPresentation>();
    private List<PubContextVOPresentation> moderatorContextList = new ArrayList<PubContextVOPresentation>();
    private QualityAssurance qualityAssurance;
    private LoginHelper loginHelper;

    /**
     * Public constructor.
     */
    public ContextListSessionBean()
    {
        
        this.loginHelper = (LoginHelper)getSessionBean(LoginHelper.class);
        this.depositorContextList = this.retrieveDepositorContexts();
        this.moderatorContextList = this.retrieveModeratorContexts();
        
    }

    private List<PubContextVOPresentation> retrieveModeratorContexts()
    {
        List<PubContextVOPresentation> moderatorContexts = new ArrayList<PubContextVOPresentation>();
        try
        {
            
            InitialContext initialContext = new InitialContext();
            this.qualityAssurance = (QualityAssurance) initialContext.lookup(QualityAssurance.SERVICE_NAME);
            moderatorContexts = CommonUtils.convertToPubCollectionVOPresentationList(qualityAssurance.retrievePubContextsForModerator(loginHelper.getAccountUser()));
        }
        catch (Exception e)
        {
            logger.error("Could not create context list.", e);
            moderatorContexts.addAll(this.getDummyCollections(3));
            logger.warn("Continuing with Dummy-Collections.");
        }
        return moderatorContexts;
    }

    /**
     * Retrieves all contexts for the current user.
     * @return the list of ContextVOs
     */
    private List<PubContextVOPresentation> retrieveDepositorContexts()
    {
        List<PubContextVOPresentation> allCollections = new ArrayList<PubContextVOPresentation>();
        
        try
        {
            allCollections = CommonUtils.convertToPubCollectionVOPresentationList(this.getItemControllerSessionBean().retrieveCollections()); 
        }
        catch (Exception e)
        {
            logger.error("Could not create context list.", e);

            allCollections.addAll(this.getDummyCollections(3));            
            
            logger.warn("Continuing with Dummy-Collections.");
        }

        return allCollections;
    }

    private List<PubContextVOPresentation> getDummyCollections(int numberofDummies)
    {
        List<PubContextVOPresentation> dummyCollections = new ArrayList<PubContextVOPresentation>();

        for (int i = 0; i < numberofDummies; i++)
        {
            dummyCollections.add(this.createDummyContext(i + 1));
        }
        
        return dummyCollections;
    }
    
    private PubContextVOPresentation createDummyContext(int number)
    {
        PubContextVOPresentation vo = new PubContextVOPresentation(new ContextVO());
        vo.setName("TestCollection " + number + ". DO NOT TRY TO CREATE ITEMS WITH THIS!");
        vo.setDescription("This is the description of the context No. " + number + ".");
        ContextRO ro = new ContextRO();
        ro.setObjectId("escidoc:dummyCollection" + number);
        vo.setReference(ro);
        
        return vo;
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
        return depositorContextList;
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
    
    
}
