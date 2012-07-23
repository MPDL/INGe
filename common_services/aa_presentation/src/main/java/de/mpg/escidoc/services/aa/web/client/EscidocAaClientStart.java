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

package de.mpg.escidoc.services.aa.web.client;

import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.mpg.escidoc.services.aa.Config;

/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class EscidocAaClientStart extends StartClient
{

    @Override
    protected String startAuthentication(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        String tan = request.getParameter("tan");
        String from = request.getParameter("target");
        String aaInstanceUrl = Config.getProperty("escidoc.aa.instance.url");
        
        if (request.getParameter("eSciDocUserHandle") != null)
        {
            return aaInstanceUrl + "clientReturn?target="
                    + from
                    + "&tan=" + URLEncoder.encode(tan, "ISO-8859-1")
                    + "&eSciDocUserHandle=" + URLEncoder.encode(request.getParameter("eSciDocUserHandle"), "ISO-8859-1");
        }
        else
        {
            return Config.getProperty("escidoc.framework_access.login.url")
                + "/aa/login"
                + "?target=" + aaInstanceUrl + "clientReturn"
                + URLEncoder.encode(URLEncoder.encode("?target="
                + from
                + "&tan=" + URLEncoder.encode(tan, "ISO-8859-1"), "ISO-8859-1"), "ISO-8859-1");
        }
    }
    
}
