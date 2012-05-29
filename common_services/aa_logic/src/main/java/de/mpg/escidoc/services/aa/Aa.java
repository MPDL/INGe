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

package de.mpg.escidoc.services.aa;

import java.io.InputStream;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.aa.crypto.RSAEncoder;
import de.mpg.escidoc.services.aa.util.ResourceUtil;

/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class Aa
{
    private static Logger logger = Logger.getLogger(Aa.class);
    
    private AuthenticationVO authenticationVO = null;
    
    public Aa(HttpServletRequest request) throws Exception
    {
        initConfig(request);
        
        String[] encodedXml = request.getParameterValues("auth");
        if (encodedXml != null)
        {
            String xml = RSAEncoder.rsaDecrypt(encodedXml);
            AuthenticationVO authenticationVO = new AuthenticationVO(xml);
            
            String tan = authenticationVO.getTan();
            if (TanStore.checkTan(tan))
            {
                request.getSession().setAttribute("authentication", authenticationVO);
                this.authenticationVO = authenticationVO;
            }
            else
            {
                request.getSession().removeAttribute("authentication");
            }
        }
    }

    public AuthenticationVO getAuthenticationVO()
    {
        return authenticationVO;
    }
    
    public void initConfig(HttpServletRequest request) throws ServletException
    {
        if (Config.getProperties().isEmpty())
        {
            String context = request.getContextPath();
            if (context != null && context.startsWith("/"))
            {
                String propertyFilename = context.substring(1) + ".properties";
                logger.info("Loading properties from " + propertyFilename);
                try
                {
                    InputStream propertyStream = ResourceUtil.getResourceAsStream(propertyFilename);
                    Config.getProperties().load(propertyStream);
                    //propertyStream.close();
                }
                catch (Exception e)
                {
                    throw new ServletException(e);
                }
            }
        }
    }
    
    /**
     * Create a link to the login page with the referer information and a random TAN.
     * 
     * @param request the servlet request.
     * @return a URL
     * @throws Exception Encoding exception is unlikely.
     */
    public static String getLoginLink(HttpServletRequest request) throws Exception
    {
        String tan;
        do
        {
            tan = TanStore.createTan(request.getSession().getId());
        }
        while (!TanStore.storeTan(tan));
        
        String from = request.getRequestURL().toString();
        
        String page = Config.getProperty("escidoc.aa.instance.url");
        
        return page + "login.jsp?from=" + URLEncoder.encode(from, "ISO-8859-1") + "&tan=" + URLEncoder.encode(tan, "ISO-8859-1");
    }
}
