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

import java.io.InputStream;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.mpg.escidoc.services.aa.AuthenticationVO;
import de.mpg.escidoc.services.aa.AuthenticationVO.Role;
import de.mpg.escidoc.services.aa.AuthenticationVO.Type;
import de.mpg.escidoc.services.aa.Config;
import de.mpg.escidoc.services.common.util.ResourceUtil;

/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class IpAaClient extends FinalClient
{

    @Override
    protected AuthenticationVO finalizeAuthentication(HttpServletRequest request, HttpServletResponse response) throws Exception
    {

        Properties ips = new Properties();
        InputStream ipStream = ResourceUtil.getResourceAsStream(Config.getProperty("escidoc.aa.ip.table"));
        ips.loadFromXML(ipStream);
        ipStream.close();
        
        String clientIp = request.getRemoteAddr();
        
        String[] roles = ips.getProperty(clientIp).split(",");
        
        AuthenticationVO authenticationVO = new AuthenticationVO();
        authenticationVO.setType(Type.ATTRIBUTE);
        authenticationVO.setFullName(clientIp);
        for (String roleKey : roles)
        {
            Role role = authenticationVO.new Role();
            role.setKey(roleKey);
            authenticationVO.getRoles().add(role);
        }
        return authenticationVO;
    }
    
}
