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
* Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.services.validation.xmltransforming;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.util.IdentityHandler;

/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class ConeContentHandler extends IdentityHandler
{

    private static final Logger logger = Logger.getLogger(ConeContentHandler.class);
    
    /* (non-Javadoc)
     * @see de.mpg.escidoc.services.util.IdentityHandler#processingInstruction(java.lang.String, java.lang.String)
     */
    @Override
    public void processingInstruction(String name, String params) throws SAXException
    {
        if (!"cone".equals(name))
        {
            super.processingInstruction(name, params);
        }
        else
        {
            try
            {
                String url = PropertyReader.getProperty("escidoc.cone.service.url")
                        + "rdf/" + params + "/all";
                HttpClient client = new HttpClient();
                GetMethod method = new GetMethod(url);
                client.executeMethod(method);
                if (method.getStatusCode() == 200)
                {
                    InputStream inputStream = method.getResponseBodyAsStream();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int read;
                    byte[] buffer = new byte[2048];
                    while ((read = inputStream.read(buffer)) != -1)
                    {
                        baos.write(buffer, 0, read);
                    }
                    String response = new String(baos.toByteArray());
                    response = response.replaceAll("<\\?xml[^>]+\\?>", "");
                    super.append(response);
                }
                else
                {
                    logger.warn("CoNE service returned status code " + method.getStatusCode());
                    logger.warn("CoNE data not included");
                }
            }
            catch (Exception e)
            {
                logger.error("Error getting CoNE data", e);
            }
        }
    }
    
    
    
}
