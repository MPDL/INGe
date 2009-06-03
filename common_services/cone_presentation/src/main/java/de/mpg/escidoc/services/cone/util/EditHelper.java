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

package de.mpg.escidoc.services.cone.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;

/**
 * Helper class that provides methods for prefilling form entries.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class EditHelper
{
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    
    /**
     * Hide constructor, because this is a helper class.
     */
    private EditHelper()
    {
        
    }
    
    /**
     * Retrieve current date.
     * 
     * @param request The request
     * @return A formatted date string
     */
    public static String getCurrentDate(HttpServletRequest request)
    {
        return DATE_FORMAT.format(new Date());
    }
    
    public static String getCurrentUser(HttpServletRequest request)
    {
        AccountUserVO user = (AccountUserVO) request.getSession().getAttribute("user");
        return user.getReference().getObjectId();
    }
}
