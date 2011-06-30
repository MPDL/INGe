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
* Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.services.aa.web.client;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;

import de.mpg.escidoc.services.aa.AuthenticationVO;
import de.mpg.escidoc.services.aa.AuthenticationVO.Role;
import de.mpg.escidoc.services.aa.AuthenticationVO.Type;

/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class BasicAaClient extends FinalClient
{

    @Override
    protected AuthenticationVO finalizeAuthentication(HttpServletRequest request, HttpServletResponse response) throws Exception
    {

        AuthenticationVO authenticationVO = new AuthenticationVO();
        authenticationVO.setType(Type.USER);
        if (testLogin(request, response))
        {
            authenticationVO.setFullName("System Administrator");
            Role role = authenticationVO.new Role();
            role.setKey("escidoc:role-system-administrator");
            authenticationVO.getRoles().add(role);
        }
        else
        {
            authenticationVO.setFullName("Outsider");
        }
        
        return authenticationVO;
    }
    
    private boolean testLogin(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        String auth = request.getHeader("authorization");
        if (auth == null)
        {
            response.addHeader("WWW-Authenticate", "Basic realm=\"Validation Service\"");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        else
        {
            auth = auth.substring(6);
            String cred = new String(Base64.decodeBase64(auth.getBytes()));
            if (cred.contains(":"))
            {
    
                String[] userPass = cred.split(":");
                String userName = "admin";
                String password = "nimda";
    
                if (!userPass[0].equals(userName) || !userPass[1].equals(password))
                {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return false;
                }
                else
                {
                    return true;
                }
            }
            else
            {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return false;
            }
        }
    }
}
