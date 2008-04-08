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

package de.mpg.escidoc.pubman.submitItem;

import javax.faces.component.html.HtmlInputTextarea;
import javax.faces.component.html.HtmlOutputText;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.ErrorPage;
import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.ItemListSessionBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.depositorWS.DepositorWS;
import de.mpg.escidoc.services.common.valueobjects.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;

/**
 * Fragment class for editing PubItems. This class provides all functionality for editing, saving and submitting a
 * PubItem including methods for depending dynamic UI components.
 *
 * @author: Thomas Diebäcker, created 10.01.2007
 * @author: $Author: mfranke $
 * @version: $Revision: 858 $ $LastChangedDate: 2007-08-09 16:51:42 +0200 (Do, 09 Aug 2007) $
 * Revised by FrM: 09.08.2007
 *  * Checkstyled, commented, cleaned.
 */
public class SubmitItem extends FacesBean
{
    private static Logger logger = Logger.getLogger(SubmitItem.class);
    // Faces navigation string
    public static final String LOAD_SUBMITITEM = "loadSubmitItem";
    public static final String JSP_NAME = "SubmitItemPage.jsp"; //DiT: to avoid JSF-Navigation

    private HtmlInputTextarea submissionComment;

    private HtmlOutputText valMessage = new HtmlOutputText();
    private String creators;
    
    private String navigationStringToGoBack;

    /**
     * Public constructor.
     */
    public SubmitItem()
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
            else if (creator.getType() == CreatorVO.CreatorType.ORGANIZATION && creator.getOrganization().getName() != null)
            {
                creators.append(creator.getOrganization().getName().getValue());
            }
        }
        this.creators = creators.toString();

        if (logger.isDebugEnabled())
        {
            if (this.getPubItem() != null && this.getPubItem().getVersion() != null)
            {
                logger.debug("Item that is being submitted: " + this.getPubItem().getVersion().getObjectId());
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
     * Submits the item.
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public final String submit()
    {
        String retVal;
        String navigateTo = getSessionBean().getNavigationStringToGoBack();
        //retVal = this.getItemControllerSessionBean().saveCurrentPubItem(DepositorWS.LOAD_DEPOSITORWS);
        String comment;

        if (submissionComment.getValue() != null)
        {
            comment = submissionComment.getValue().toString();
        }
        else
        {
            comment = null;
        }

        // Comment is not required, so this is not needed
        /*
        if (comment == null)
        {
            valMessage.setText(this.bundle.getString(DepositorWS.NO_SUBMISSION_COMMENT_GIVEN));
            return null;
        }
        */

        logger.debug("Now submitting, then go to " + navigateTo);
        
        retVal = this.getItemControllerSessionBean().submitCurrentPubItem(comment, navigateTo);
        if (retVal.compareTo(ErrorPage.LOAD_ERRORPAGE) != 0)
        {
            this.showMessage(DepositorWS.MESSAGE_SUCCESSFULLY_SUBMITTED);
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
        
        this.valMessage.setValue(message);
        this.valMessage.setRendered(message != null);
        
        // keep the message just once
        this.getSessionBean().setMessage(null);
    }
    
    /**
     * Returns a reference to the scoped data bean (the ItemControllerSessionBean).
     * @return a reference to the scoped data bean
     */
    public final ItemControllerSessionBean getItemControllerSessionBean()
    {
        return (ItemControllerSessionBean)getBean(ItemControllerSessionBean.class);
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
     * Returns the DepositorWSSessionBean.
     * @return a reference to the scoped data bean (DepositorWSSessionBean)
     */
    protected final SubmitItemSessionBean getSessionBean()
    {
        return (SubmitItemSessionBean)getBean(SubmitItemSessionBean.class);
    }

    public final HtmlInputTextarea getSubmissionComment()
    {
        return submissionComment;
    }

    public final void setSubmissionComment(final HtmlInputTextarea submissionComment)
    {
        this.submissionComment = submissionComment;
    }

    public final HtmlOutputText getValMessage()
    {
        return valMessage;
    }

    public final void setValMessage(final HtmlOutputText valMessage)
    {
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
