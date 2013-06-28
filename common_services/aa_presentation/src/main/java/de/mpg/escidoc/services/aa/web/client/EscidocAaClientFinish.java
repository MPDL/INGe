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

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;

import de.escidoc.www.services.aa.RoleHandler;
import de.escidoc.www.services.aa.UserAccountHandler;
import de.mpg.escidoc.services.aa.AuthenticationVO;
import de.mpg.escidoc.services.aa.AuthenticationVO.Grant;
import de.mpg.escidoc.services.aa.AuthenticationVO.Role;
import de.mpg.escidoc.services.aa.AuthenticationVO.Type;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.GrantVO;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;
import de.mpg.escidoc.services.framework.AdminHelper;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * TODO Description
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */

public class EscidocAaClientFinish extends FinalClient
{

    @Override
    protected AuthenticationVO finalizeAuthentication(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        String escidocUserHandle = request.getParameter("eSciDocUserHandle");
        
        if (escidocUserHandle != null)
        {
            try
            {
                escidocUserHandle = new String(Base64.decodeBase64(escidocUserHandle.getBytes()));
                UserAccountHandler userAccountHandler = ServiceLocator.getUserAccountHandler(escidocUserHandle);
                String accountData = userAccountHandler.retrieveCurrentUser();
                AccountUserVO accountUserVO = new XmlTransformingBean().transformToAccountUser(accountData);
                String grantData = userAccountHandler.retrieveCurrentGrants(accountUserVO.getReference().getObjectId());
                List<GrantVO> grants = new XmlTransformingBean().transformToGrantVOList(grantData);
                if(grants!=null)
                {
                	  accountUserVO.getGrants().addAll(grants);
                }
              
                
                AuthenticationVO authenticationVO = new AuthenticationVO();
                authenticationVO.setType(Type.USER);
                authenticationVO.setUsername(accountUserVO.getUserid());
                authenticationVO.setUserId(accountUserVO.getReference().getObjectId());
                authenticationVO.setFullName(accountUserVO.getName());

                for (GrantVO grantVO : accountUserVO.getGrants())
                {
                    if (grantVO.getObjectRef() == null)
                    {
                        Role role = authenticationVO.new Role();
                        
                        role.setKey(grantVO.getRole());
                        authenticationVO.getRoles().add(role);
                        authenticationVO.getRoles().add(role);
                    }
                    else
                    {
                        Grant grant = authenticationVO.new Grant();
                        grant.setKey(grantVO.getRole());
                        grant.setValue(grantVO.getObjectRef());
                        authenticationVO.getGrants().add(grant);
                    }
                }
                return authenticationVO;
            }
            catch (Exception e)
            {
                throw new ServletException(e);
            }
        }
        return null;
    }
}
