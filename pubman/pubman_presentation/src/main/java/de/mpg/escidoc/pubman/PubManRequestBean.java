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
* Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/
package de.mpg.escidoc.pubman;

import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.services.framework.PropertyReader;

public class PubManRequestBean extends FacesBean
{
    public static final String BEAN_NAME = "PubManRequestBean";
    private static Logger logger = Logger.getLogger(PubManRequestBean.class);
    private String helpAnchor = "";
    private String requestedPage = "";
    
    /**
     * Default constructor.
     */
    public PubManRequestBean()
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
       FacesContext fc = FacesContext.getCurrentInstance();
       if(fc.getExternalContext().getRequestPathInfo() != null)
       {
           this.helpAnchor = fc.getExternalContext().getRequestPathInfo().replace("/", "");
           this.requestedPage = this.helpAnchor.replaceAll(".jsp", "");
           this.helpAnchor = "#" +this.helpAnchor.replaceAll(".jsp", "");
       }
       
    }
    
    
    //     Getters and Setters
    
    public String getHelpAnchor() {
        return helpAnchor;
    }

    public void setHelpAnchor(String helpAnchor) {
        this.helpAnchor = helpAnchor;
    }

    public String getRequestedPage() {
        return requestedPage;
    }

    public void setRequestedPage(String requestedPage) {
        this.requestedPage = requestedPage;
    }
    
    
    /**
     * Reads the policy URL from the properties file.
     * @return policyUrl as String
     */
    public String getPolicyUrl()
    {
        String url = "";
        try
        {
            url = PropertyReader.getProperty("escidoc.pubman.policy.url");
        }
        catch (Exception e)
        {
            logger.error("Could not read property: 'escidoc.pubman.policy.url' from properties file.", e);
        }

        return url;
    }
    
    
    /**
     * Reads the contact URL from the properties file.
     * @return contactUrl as String
     */
    public String getContactUrl()
    {
        String url = "";
        try
        {
            url = PropertyReader.getProperty("escidoc.pubman.contact.url");
        }
        catch (Exception e)
        {
            logger.error("Could not read property: 'escidoc.pubman.contact.url' from properties file.", e);
        }

        return url;
    }
    
    
}
