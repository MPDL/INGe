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

package de.mpg.escidoc.pubman;

import java.io.IOException;
import java.util.Properties;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.exceptions.PubManVersionNotAvailableException;
import de.mpg.escidoc.services.common.util.CommonUtils;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.comparator.PubItemVOComparator;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.EventVO;

/**
 * ApplicationBean which stores all application wide values.
 *
 * @author: Thomas Diebäcker, created 09.08.2007
 * @version: $Revision: 1700 $ $LastChangedDate: 2007-12-18 16:18:16 +0100 (Di, 18 Dez 2007) $
 * Revised by DiT: 09.08.2007
 */
public class ApplicationBean extends FacesBean
{
    public static final String BEAN_NAME = "ApplicationBean";
    private static Logger logger = Logger.getLogger(ApplicationBean.class);

    private final String APP_TITLE = "Publication Manager";
    private String appTitle = null;
    private String appContext = "";
    
    /** filename of the ear-internal property file */ 
    private static final String PROPERTY_FILENAME = "solution.properties";

    /**
     * Public constructor.
     */
    public ApplicationBean()
    {
        this.init();
    }

    /**
     * This method is called when this bean is initially added to application scope.
     * Typically, this occurs as a result of
     * evaluating a value binding or method binding expression, which utilizes the managed bean facility to instantiate
     * this bean and store it into application scope.
     */
    public void init()
    {
        // Perform initializations inherited from our superclass
        super.init();
    }

    /**
     * Returns an appropriate character encoding based on the Locale defined for the current JavaServer
     * Faces view. If no more suitable encoding can be found, return "UTF-8" as a general purpose default.
     * The default implementation uses the implementation from our superclass, FacesBean.
     *
     * @return the local character encoding
     */
    public String getLocaleCharacterEncoding()
    {
        return System.getProperty("file.encoding"); // super.getLocaleCharacterEncoding();
    }

    /**
     * Returns the title and version of the application, shown in the header.
     *
     * @return applicationtitle, including version
     */
    public String getAppTitle()
    {
        // retrieve version once
        if (this.appTitle == null)
        {
            this.appTitle = this.APP_TITLE;

            try
            {
                this.appTitle += " " + this.getVersion();
                logger.info("Version retrieved.");
            }
            catch (PubManVersionNotAvailableException e)
            {
                // version cannot be retrieved; just show the application title
                logger.warn("The version of the application cannot be retrieved.");
            }
        }

        return appTitle;
    }

    /**
     * Provides the escidoc version string.
     *
     * @return the escidoc version
     * @throws PubManVersionNotAvailableException if escidoc version can not be retrieved.
     */
    private String getVersion() throws PubManVersionNotAvailableException
    {
        try
        {
            Properties properties = CommonUtils.getProperties( PROPERTY_FILENAME );
            return properties.getProperty("escidoc.pubman.version");
        }
        catch (IOException e)
        {
            throw new PubManVersionNotAvailableException(e);
        }
    }

    /**
     * Returns the current application context.
     *
     * @return the application context
     */
    public String getAppContext()
    {
        FacesContext fc = FacesContext.getCurrentInstance();
        this.appContext = fc.getExternalContext().getRequestContextPath() + "/faces/";

        return appContext;
    }

    /**
     * Sets the application context.
     *
     * @param appContext the new application context
     */
    public void setAppContext(String appContext)
    {
        this.appContext = appContext;
    }

}
