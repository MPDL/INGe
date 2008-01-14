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

import java.util.ResourceBundle;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import org.apache.log4j.Logger;
import com.sun.rave.web.ui.appbase.AbstractFragmentBean;
import com.sun.rave.web.ui.component.StaticText;
import com.sun.rave.web.ui.component.TextArea;
import de.mpg.escidoc.pubman.ErrorPage;
import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.depositorWS.DepositorWS;
import de.mpg.escidoc.pubman.depositorWS.DepositorWSSessionBean;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.services.common.valueobjects.PubItemVO;
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
public class AcceptItem extends AbstractFragmentBean
{
    private static Logger logger = Logger.getLogger(AcceptItem.class);
    // Faces navigation string
    public static final String LOAD_ACCEPTITEM = "loadAcceptItem";
    public static final String JSP_NAME = "AcceptItemPage.jsp"; //DiT: to avoid JSF-Navigation

    private TextArea acceptanceComment;

    private StaticText valMessage = new StaticText();
    private String creators;
    
    private String navigationStringToGoBack;

    //For handling the resource bundles (i18n)
    private Application application = FacesContext.getCurrentInstance().getApplication();
    //get the selected language...
    private InternationalizationHelper i18nHelper = (InternationalizationHelper)application
    .getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), InternationalizationHelper.BEAN_NAME);
    //... and set the refering resource bundle
    private ResourceBundle bundle = ResourceBundle.getBundle(i18nHelper.getSelectedMessagesBundle());

    /**
     * Public constructor.
     */
    public AcceptItem()
    {
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
            if (this.getPubItem() != null && this.getPubItem().getReference() != null)
            {
                logger.debug("Item that is being accepted: " + this.getPubItem().getReference().getObjectId());
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
        String retVal;
        String navigateTo = getSessionBean().getNavigationStringToGoBack();
        //retVal = this.getItemControllerSessionBean().saveCurrentPubItem(DepositorWS.LOAD_DEPOSITORWS);
        String comment;

        if (acceptanceComment.getText() != null)
        {
            comment = acceptanceComment.getText().toString();
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

        logger.debug("Now acceptting, then go to " + navigateTo);
        
        retVal = this.getItemControllerSessionBean().acceptCurrentPubItem(comment, navigateTo);
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
     * @param keepMessage stores this message in SessionBean and displays it once (e.g. for a reload)
     */
    private void showMessage(final String message)
    {
        String localMessage = this.bundle.getString(message);
        this.getDepositorWSSessionBean().setMessage(localMessage);
    }
    
    /**
     * Adds and removes messages on this page, if any.
     * @author Michael Franke
     */
    public void handleMessage() 
    {

        String message = this.getSessionBean().getMessage();
        
        this.valMessage.setText(message);
        this.valMessage.setVisible(message != null);
        
        // keep the message just once
        this.getSessionBean().setMessage(null);
    }
    
    /**
     * Returns a reference to the scoped data bean (the ItemControllerSessionBean).
     * @return a reference to the scoped data bean
     */
    public final ItemControllerSessionBean getItemControllerSessionBean()
    {
        return (ItemControllerSessionBean)getBean(ItemControllerSessionBean.BEAN_NAME);
    }

    /**
     * Returns the DepositorWSSessionBean.
     * @return a reference to the scoped data bean (DepositorWSSessionBean)
     */
    protected final DepositorWSSessionBean getDepositorWSSessionBean()
    {
        return (DepositorWSSessionBean)getBean(DepositorWSSessionBean.BEAN_NAME);
    }

    /**
     * Returns the DepositorWSSessionBean.
     * @return a reference to the scoped data bean (DepositorWSSessionBean)
     */
    protected final AcceptItemSessionBean getSessionBean()
    {
        return (AcceptItemSessionBean)getBean(AcceptItemSessionBean.BEAN_NAME);
    }

    public final TextArea getAcceptanceComment()
    {
        return acceptanceComment;
    }

    public final void setAcceptanceComment(final TextArea acceptanceComment)
    {
        this.acceptanceComment = acceptanceComment;
    }

    public final StaticText getValMessage()
    {
        return valMessage;
    }

    public final void setValMessage(final StaticText valMessage)
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
