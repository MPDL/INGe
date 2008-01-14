/*
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

package de.mpg.escidoc.pubman;

import java.io.IOException;
import javax.faces.context.FacesContext;
import javax.xml.rpc.ServiceException;
import org.apache.log4j.Logger;
import com.sun.rave.web.ui.appbase.AbstractPageBean;
import com.sun.rave.web.ui.component.Body;
import com.sun.rave.web.ui.component.Form;
import com.sun.rave.web.ui.component.Head;
import com.sun.rave.web.ui.component.Html;
import com.sun.rave.web.ui.component.Link;
import com.sun.rave.web.ui.component.Page;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.xmltransforming.exceptions.UnmarshallingException;

/**
 * Class for the AdvancedSearch Page.
 *
 * @author:  Hugo Niedermaier, created 24.07.2007
 * @version: $Revision: 1572 $ $LastChangedDate: 2007-11-15 15:02:43 +0100 (Thu, 15 Nov 2007) $
 * Revised by NiH: 14.08.2007
 */
public class AdvancedSearchPage extends AbstractPageBean
{
    private static Logger logger = Logger.getLogger(AdvancedSearchPage.class);
    private Page page1 = new Page();
    private Html html1 = new Html();
    private Head head1 = new Head();
    private Link link1 = new Link();
    private Body body1 = new Body();
    private Form form1 = new Form();

    /**
     * Public constructor.
     */
    public AdvancedSearchPage()
    {
    }

    /**
     * Callback method that is called whenever a page containing this page fragment is navigated to, either directly via
     * a URL, or indirectly via page navigation.
     */
    public void init()
    {
        // Perform initializations inherited from our superclass
        super.init();
        LoginHelper loginHelper = (LoginHelper)FacesContext.getCurrentInstance().getApplication().getVariableResolver()
                .resolveVariable(FacesContext.getCurrentInstance(), "LoginHelper");
        if (loginHelper == null)
        {
            loginHelper = new LoginHelper();
        }
        if (loginHelper != null)
        {
            try
            {
                try
                {
                    loginHelper.insertLogin();
                }
                catch (UnmarshallingException e)
                {
                    logger.debug(e.toString());
                }
                catch (TechnicalException e)
                {
                    logger.debug(e.toString());
                }
                catch (ServiceException e)
                {
                    logger.debug(e.toString());
                }
            }
            catch (IOException e1)
            {
                logger.debug(e1.toString());
            }
        }
    }

    public Body getBody1()
    {
        return body1;
    }

    public void setBody1(Body body1)
    {
        this.body1 = body1;
    }

    public Form getForm1()
    {
        return form1;
    }

    public void setForm1(Form form1)
    {
        this.form1 = form1;
    }

    public Head getHead1()
    {
        return head1;
    }

    public void setHead1(Head head1)
    {
        this.head1 = head1;
    }

    public Html getHtml1()
    {
        return html1;
    }

    public void setHtml1(Html html1)
    {
        this.html1 = html1;
    }

    public Link getLink1()
    {
        return link1;
    }

    public void setLink1(Link link1)
    {
        this.link1 = link1;
    }

    public Page getPage1()
    {
        return page1;
    }

    public void setPage1(Page page1)
    {
        this.page1 = page1;
    }
}
