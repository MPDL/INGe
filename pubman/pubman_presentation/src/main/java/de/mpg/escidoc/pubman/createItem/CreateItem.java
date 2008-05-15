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

package de.mpg.escidoc.pubman.createItem;

import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.ItemListSessionBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.contextList.ContextListSessionBean;
import de.mpg.escidoc.pubman.editItem.EditItem;
import de.mpg.escidoc.pubman.editItem.EditItemSessionBean;
import de.mpg.escidoc.pubman.util.PubContextVOPresentation;
import de.mpg.escidoc.services.common.valueobjects.ContextVO;

/**
 * Fragment class for CreateItem.
 * 
 * @author: Thomas Diebäcker, created 11.10.2007
 * @author: $Author: mfranke $ last modification
 * @version: $Revision: 1683 $ $LastChangedDate: 2007-12-17 10:30:45 +0100 (Mo, 17 Dez 2007) $ 
 */
public class CreateItem extends FacesBean
{
    public static final String BEAN_NAME = "CreateItem";
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(CreateItem.class);
    
    // Faces navigation string
    public final static String LOAD_CREATEITEM = "loadCreateItem";

    /**
     * Public constructor.
     */
    public CreateItem()
    {
        this.init();
    }
    
    /**
     * Callback method that is called whenever a page containing this page fragment is navigated to, either directly via
     * a URL, or indirectly via page navigation. 
     */
    public void init()
    {
        super.init();

    }
    
    public String confirmSelection()
    {
        return EditItem.LOAD_EDITITEM;
    }

    /**
     * Starts a new submission.
     *
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String newSubmission()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("New Submission");
        }
        
        // first clear the EditItemSessionBean
        this.getEditItemSessionBean().clean();
        
        // the clean the ItemControllerSessionBean (if there is already an item)
        this.getItemControllerSessionBean().setCurrentPubItem(null);
        
        // if there is only one context for this user we can skip the CreateItem-Dialog and
        // create the new item directly
        if (this.getContextListSessionBean().getContextList().size() == 0)
        {
            logger.warn("The user does not have privileges for any context.");
            return null;
        }
        if (this.getContextListSessionBean().getContextList().size() == 1)
        {
            ContextVO contextVO = this.getContextListSessionBean().getContextList().get(0);
            if (logger.isDebugEnabled())
            {
                logger.debug("The user has only privileges for one context (ID: "
                        + contextVO.getReference().getObjectId() + ")");
            }
            return this.getItemControllerSessionBean().createNewPubItem(EditItem.LOAD_EDITITEM,
                    contextVO.getReference());
        }
        else
        {
            // more than one context exists for this user; let him choose the right one
            if (logger.isDebugEnabled())
            {
                logger.debug("The user has privileges for "
                        + this.getContextListSessionBean().getContextList().size() + " different contexts.");
            }
            return this.getItemControllerSessionBean().createNewPubItem(CreateItem.LOAD_CREATEITEM,
            		this.getContextListSessionBean().getContextList().get(0).getReference());
        }
    }

    /**
     * Returns the ContextListSessionBean.
     *
     * @return a reference to the scoped data bean (ContextListSessionBean)
     */
    protected ContextListSessionBean getContextListSessionBean()
    {
        return (ContextListSessionBean) getSessionBean(ContextListSessionBean.class);
    }

    /**
     * Returns the ContextListSessionBean.
     *
     * @return a reference to the scoped data bean (ContextListSessionBean)
     */
    protected ContextListSessionBean getSessionBean()
    {
        return (ContextListSessionBean) getSessionBean(ContextListSessionBean.class);
    }

    /**
     * Returns the ItemListSessionBean.
     * @return a reference to the scoped data bean (ItemListSessionBean)
     */
    protected ItemListSessionBean getItemListSessionBean()
    {
        return (ItemListSessionBean) getSessionBean(ItemListSessionBean.class);
    }
    
    /**
     * Returns the ItemListSessionBean.
     * @return a reference to the scoped data bean (ItemListSessionBean)
     */
    protected EditItemSessionBean getEditItemSessionBean()
    {
        return (EditItemSessionBean) getSessionBean(EditItemSessionBean.class);
    }

    /**
     * Returns a reference to the scoped data bean (the ItemControllerSessionBean). 
     * @return a reference to the scoped data bean
     */
    protected ItemControllerSessionBean getItemControllerSessionBean()
    {
        return (ItemControllerSessionBean)getSessionBean(ItemControllerSessionBean.class);
    }

	public List<PubContextVOPresentation> getCurrentCollectionList() {
		return getSessionBean().getContextList();
	}

}
