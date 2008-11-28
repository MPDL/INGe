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
* Copyright 2006-2008 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/
package de.mpg.escidoc.pubman;

import javax.faces.event.ValueChangeEvent;

import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.pubman.util.LoginHelper;

public class PubManSessionBean extends FacesBean
{
	public static final String BEAN_NAME = "PubManSessionBean";
    
    private String locale;
	
	/**
     * Default constructor.
     */
    public PubManSessionBean()
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
       InternationalizationHelper i18nHelper = this.getI18nHelper();
       this.locale = i18nHelper.getLocale();
    }
    
    public void changeLanguage(ValueChangeEvent event)
    {
    	InternationalizationHelper i18nHelper = this.getI18nHelper();
    	if(event != null)
    	{
    		i18nHelper.changeLanguage(event);
    	}
    	this.locale = i18nHelper.getLocale();
    }

    // 	Getters and Setters
    
	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}
	
	/**
     * Returns the LoginHelper.
     *
     * @return a reference to the scoped data bean (LoginHelper)
     */
    protected LoginHelper getLoginHelper()
    {
        return (LoginHelper) getSessionBean(LoginHelper.class);
    }
    
    /**
     * Returns the InternationalizationHelper.
     *
     * @return a reference to the scoped data bean (InternationalizationHelper)
     */
    protected InternationalizationHelper getI18nHelper()
    {
        return (InternationalizationHelper) getSessionBean(InternationalizationHelper.class);
    }
	
}
