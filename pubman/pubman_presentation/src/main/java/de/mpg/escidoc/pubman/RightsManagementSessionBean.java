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

import org.apache.log4j.Logger;
import com.sun.rave.web.ui.appbase.AbstractSessionBean;
import de.mpg.escidoc.services.framework.PropertyReader;

/**
 * Handels all rights of the current user and handels disabled features described in the escidoc.properties file.
 * 
 * @author: (First draft version) Thomas Diebäcker, created 25.07.2007
 * @version: $Revision: 1587 $ $LastChangedDate: 2007-11-20 10:54:36 +0100 (Tue, 20 Nov 2007) $
 * Revised by DiT: 14.08.2007
 */
public class RightsManagementSessionBean extends AbstractSessionBean
{
    public static final String BEAN_NAME = "RightsManagementSessionBean";
    private static Logger logger = Logger.getLogger(ItemControllerSessionBean.class);
    
    // prefix for disableing functions in properties file (has to be followed by ".<functionname>")
    public static final String PROPERTY_PREFIX_FOR_DISABLEING_FUNCTIONS = "escidoc.pubman_presentation.disable";
    // constant for the string that marks a function as disabled
    private static final String DISABLED_STRING = "true";    
    
    // Public constructor
    public RightsManagementSessionBean()
    {
    }

    /**
     * Checks if a given function is marked as disabled in the escidoc properties file.
     * @param function the function that should be checked
     * @return true if the function should be disabled
     */
    public boolean isDisabled(String function)
    {
        String propertyValue = null;
        
        try
        {
            propertyValue = PropertyReader.getProperty(function);
        }
        catch (Exception e)
        {
            logger.warn("Propertyfile not readable for property '" + function + "'");
        }
        
        if (propertyValue == null)
        {
            propertyValue = "false";
        }
        
        return (propertyValue.compareToIgnoreCase(RightsManagementSessionBean.DISABLED_STRING) == 0);
    }
}
