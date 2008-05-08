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

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.PubContextVOPresentation;
import de.mpg.escidoc.services.common.referenceobjects.ContextRO;
import de.mpg.escidoc.services.common.valueobjects.ContextVO;

/**
 * Keeps all attributes that are used for the whole session by the CollectionList.
 * @author:  Thomas Diebäcker, created 12.10.2007
 * @version: $Revision: 1587 $ $LastChangedDate: 2007-11-20 10:54:36 +0100 (Di, 20 Nov 2007) $
 */
public class ContextListSessionBean extends FacesBean
{
    public static final String BEAN_NAME = "ContextListSessionBean";
    private static Logger logger = Logger.getLogger(ContextListSessionBean.class);

    private List<PubContextVOPresentation> contextList = new ArrayList<PubContextVOPresentation>();

    /**
     * Public constructor.
     */
    public ContextListSessionBean()
    {
        this.contextList = this.retrieveContexts();
    }

    /**
     * Retrieves all contexts for the current user.
     * @return the list of ContextVOs
     */
    private List<PubContextVOPresentation> retrieveContexts()
    {
        List<PubContextVOPresentation> allCollections = new ArrayList<PubContextVOPresentation>();
        
        try
        {
            allCollections = CommonUtils.convertToPubCollectionVOPresentationList(this.getItemControllerSessionBean().retrieveCollections()); 
        }
        catch (Exception e)
        {
            logger.error("Could not create context list." + "\n" + e.toString());

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

    public List<PubContextVOPresentation> getContextList()
    {
        return contextList;
    }

    public void setContextList(List<PubContextVOPresentation> contextList)
    {
        this.contextList = contextList;
    }

    public PubContextVOPresentation getSelectedContext()
    {
    	for (PubContextVOPresentation coll : contextList) {
			if (coll.getSelected())
			{
				return coll;
			}
		}
    	return null;
    }
}
