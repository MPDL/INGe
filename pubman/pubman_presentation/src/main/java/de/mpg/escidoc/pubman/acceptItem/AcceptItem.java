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

package de.mpg.escidoc.pubman.acceptItem;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.ErrorPage;
import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.ItemListSessionBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.depositorWS.DepositorWS;
import de.mpg.escidoc.pubman.viewItem.ViewItemFull;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;

/**
 * Fragment class for editing PubItems. This class provides all functionality for accepting a
 * PubItem including methods for depending dynamic UI components.
 *
 * @author: Michael Franke, 2007-10-31
 * @author: $Author: mfranke $
 * @version: $Revision: 858 $ $LastChangedDate: 2007-08-09 16:51:42 +0200 (Do, 09 Aug 2007) $
 * Revised by FrM: 09.08.2007
 *  * Checkstyled, commented, cleaned.
 */
public class AcceptItem extends FacesBean
{
    private static Logger logger = Logger.getLogger(AcceptItem.class);
    // Faces navigation string
    public static final String LOAD_ACCEPTITEM = "loadAcceptItem";
    public static final String JSP_NAME = "AcceptItemPage.jsp"; //DiT: to avoid JSF-Navigation

    private String acceptanceComment = null;

    private String valMessage = null;
    private String creators;
    
    private String navigationStringToGoBack;

    /**
     * Public constructor.
     */
    public AcceptItem()
    {
        this.init();
    }

    /**
     * Callback method that is called whenever a page containing this page fragment is navigated to, either directly via
     * a URL, or indirectly via page navigation.
     * Creators handling added by FrM.
     */
    public final void init()
    {
        // Perform initializations inherited from our superclass
        super.init();
        
        // Fill creators property.
        StringBuffer creators = new StringBuffer();
        for (CreatorVO creator : getPubItem().getMetadata().getCreators())
        {
            if (creators.length() > 0)
            {
                creators.append("; ");
            }
            if (creator.getType() == CreatorVO.CreatorType.PERSON)
            {
                creators.append(creator.getPerson().getFamilyName());
                if (creator.getPerson().getGivenName() != null)
                {
                    creators.append(", ");
                    creators.append(creator.getPerson().getGivenName());
                }
            }
            else if (creator.getType() == CreatorVO.CreatorType.ORGANIZATION)
            {
                if(creator.getOrganization().getName()!= null)
                {
                    creators.append(creator.getOrganization().getName().getValue());
                }
            }
        }
        this.creators = creators.toString();

        if (logger.isDebugEnabled())
        {
            if (this.getPubItem() != null && this.getPubItem().getVersion() != null)
            {
                logger.debug("Item that is being accepted: " + this.getPubItem().getVersion().getObjectId());
            }
            else
            {
                logger.error("NO ITEM GIVEN");
            }
        }
    }

    /**
     * Deliveres a reference to the currently edited item.
     * This is a shortCut for the method in the ItemController.
     * @return the item that is currently edited
     */
    public final PubItemVO getPubItem()
    {
        return (this.getItemControllerSessionBean().getCurrentPubItem());
    }

    /**
     * Accepts the item.
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public final String accept()
    {
    	FacesContext fc = FacesContext.getCurrentInstance();
    	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
    	String retVal;
        String navigateTo = getSessionBean().getNavigationStringToGoBack();
        if(navigateTo == null)
        {
        	navigateTo = ViewItemFull.LOAD_VIEWITEM;
        }

        logger.debug("Now acceptting, then go to " + navigateTo);
        
        retVal = this.getItemControllerSessionBean().acceptCurrentPubItem(acceptanceComment, navigateTo);
        
        // redirect to the view item page afterwards (if no error occured)
        if(retVal.compareTo(ErrorPage.LOAD_ERRORPAGE) != 0)
        {
        	try 
            {
    			fc.getExternalContext().redirect(request.getContextPath() + "/faces/viewItemFullPage.jsp?itemId=" + this.getItemControllerSessionBean().getCurrentPubItem().getVersion().getObjectId());
    		} 
            catch (IOException e) {
    			logger.error("Could not redirect to View Item Page", e);
    		}
        }
        
        
        if (retVal.compareTo(ErrorPage.LOAD_ERRORPAGE) != 0)
        {
            this.showMessage(DepositorWS.MESSAGE_SUCCESSFULLY_ACCEPTED);
        }

        return retVal;
    }

    /**
     * Cancels the editing.
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public final String cancel()
    {
        return DepositorWS.LOAD_DEPOSITORWS;
    }

    /**
     * Shows the given Message below the itemList after next Reload of the DepositorWS.
     * @param message the message to be displayed
     * @param keepMessage stores this message in FacesBean and displays it once (e.g. for a reload)
     */
    private void showMessage(final String message)
    {
        String localMessage = getMessage(message);
        this.getItemListSessionBean().setMessage(localMessage);
    }
    
    /**
     * Adds and removes messages on this page, if any.
     * @author Michael Franke
     */
    public void handleMessage() 
    {

        String message = this.getSessionBean().getMessage();
        
        this.valMessage = message;
        
        // keep the message just once
        this.getSessionBean().setMessage(null);
    }
    
    /**
     * Returns a reference to the scoped data bean (the ItemControllerSessionBean).
     * @return a reference to the scoped data bean
     */
    public final ItemControllerSessionBean getItemControllerSessionBean()
    {
        return (ItemControllerSessionBean)getSessionBean(ItemControllerSessionBean.class);
    }

    /**
     * Returns the ItemListSessionBean.
     * @return a reference to the scoped data bean (ItemListSessionBean)
     */
    protected final ItemListSessionBean getItemListSessionBean()
    {
        return (ItemListSessionBean)getSessionBean(ItemListSessionBean.class);
    }

    /**
     * Returns the AcceptItemSessionBean.
     * @return a reference to the scoped data bean (AcceptItemSessionBean)
     */
    protected final AcceptItemSessionBean getSessionBean()
    {
        return (AcceptItemSessionBean)getSessionBean(AcceptItemSessionBean.class);
    }

    public String getAcceptanceComment() {
		return acceptanceComment;
	}

	public void setAcceptanceComment(String acceptanceComment) {
		this.acceptanceComment = acceptanceComment;
	}

	public String getValMessage() {
		return valMessage;
	}

	public void setValMessage(String valMessage) {
		this.valMessage = valMessage;
	}

	public final String getNavigationStringToGoBack()
    {
        return navigationStringToGoBack;
    }

    public final void setNavigationStringToGoBack(final String navigationStringToGoBack)
    {
        this.navigationStringToGoBack = navigationStringToGoBack;
    }

    public String getCreators()
    {
        return creators;
    }

    public void setCreators(String creators)
    {
        this.creators = creators;
    }

}
