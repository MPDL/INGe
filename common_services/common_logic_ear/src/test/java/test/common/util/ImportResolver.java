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

package test.common.util;

import java.io.InputStream;
import java.io.Reader;

import org.apache.log4j.Logger;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import de.mpg.escidoc.services.common.util.ResourceUtil;

/**
 * TODO Description
 *
 * @author sieders (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class ImportResolver implements LSResourceResolver
{
    private static final Logger logger = Logger.getLogger(ImportResolver.class);
    
    @Override
    public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId,
            String baseURI)
    {

        System.out.println(systemId);
        
        try
        {
            LSInput result = new ImportInput();
            result.setBaseURI(baseURI);
            result.setByteStream(ResourceUtil.getResourceAsStream(systemId));
            result.setCertifiedText(false);
            result.setEncoding("UTF-8");
            result.setSystemId(systemId);
            return result;
        }
        catch (Exception e)
        {
            logger.warn("Included schema not found: " + systemId);
            return null;
        }

    }
    
    public class ImportInput implements LSInput
    {

        String baseURI;
        InputStream byteStream;
        boolean certifiedText;
        Reader characterStream;
        String encoding;
        String publicId;
        String stringData;
        String systemId;
        
        public String getBaseURI()
        {
            return baseURI;
        }
        public void setBaseURI(String baseURI)
        {
            this.baseURI = baseURI;
        }
        public InputStream getByteStream()
        {
            return byteStream;
        }
        public void setByteStream(InputStream byteStream)
        {
            this.byteStream = byteStream;
        }
        public boolean getCertifiedText()
        {
            return certifiedText;
        }
        public void setCertifiedText(boolean certifiedText)
        {
            this.certifiedText = certifiedText;
        }
        public Reader getCharacterStream()
        {
            return characterStream;
        }
        public void setCharacterStream(Reader characterStream)
        {
            this.characterStream = characterStream;
        }
        public String getEncoding()
        {
            return encoding;
        }
        public void setEncoding(String encoding)
        {
            this.encoding = encoding;
        }
        public String getPublicId()
        {
            return publicId;
        }
        public void setPublicId(String publicId)
        {
            this.publicId = publicId;
        }
        public String getStringData()
        {
            return stringData;
        }
        public void setStringData(String stringData)
        {
            this.stringData = stringData;
        }
        public String getSystemId()
        {
            return systemId;
        }
        public void setSystemId(String systemId)
        {
            this.systemId = systemId;
        }
        
        
        
    }
}

