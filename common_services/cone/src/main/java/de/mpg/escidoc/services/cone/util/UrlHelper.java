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
* Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.services.cone.util;

import java.io.UnsupportedEncodingException;

/**
 * Helper class for URL handling.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class UrlHelper
{
    /**
     * Hide constructor of util class.
     */
    private UrlHelper()
    {}
    
    /**
     * Transforms broken ISO-8859-1 strings into correct UTF-8 strings.
     * 
     * @param brokenValue
     * @return hopefully fixed string.
     */
    public static String fixURLEncoding(String input)
    {
        if (input != null)
        {
            try
            {
                String utf8 = new String(input.getBytes("ISO-8859-1"), "UTF-8");
                if (utf8.equals(input) || utf8.contains("�") || utf8.length() == input.length())
                {
                    return input;
                }
                else
                {
                    return utf8;
                }
            }
            catch (UnsupportedEncodingException e)
            {
                throw new RuntimeException(e);
            }
        }
        else
        {
            return null;
        }
    }
}
