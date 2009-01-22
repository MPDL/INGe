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

package de.mpg.escidoc.services.transformationImpl.transformations.thirdPartyFormats;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

import net.sf.saxon.TransformerFactoryImpl;

import de.mpg.escidoc.services.common.util.ResourceUtil;

/**
 * Handles all transformations for third party metadata records.
 * @author Friederike Kleinfercher (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class ThirdPartyTransformation 
{
    private final Logger logger = Logger.getLogger(ThirdPartyTransformation.class);
    
    private final String METADATA_XSLT_LOCATION ="resources/transformations/thirdParty/xslt";
    
    /**
     * Public constructor.
     */
    public ThirdPartyTransformation()
    {}
    
    /**
     * Metadata transformation method.
     * @param formatFrom
     * @param formatTo
     * @param itemXML
     * @return transformed metadata as String
     */
    public String xsltTransform(String formatFrom, String formatTo, String itemXML) throws RuntimeException
    {
        String xsltUri = formatFrom.toLowerCase() + "2" + formatTo.toLowerCase() + ".xsl";
        
        TransformerFactory factory = new TransformerFactoryImpl();
        factory.setURIResolver(new LocalURIResolver(this.METADATA_XSLT_LOCATION));
        StringWriter writer = new StringWriter();
        
        try
        {
            ClassLoader cl = this.getClass().getClassLoader();
            InputStream in = cl.getResourceAsStream(this.METADATA_XSLT_LOCATION + "/" + xsltUri);
            Transformer transformer = factory.newTransformer(new StreamSource(in));

            //TODO!
//          transformer.setParameter("external_organization_id",
//                PropertyReader.getProperty("escidoc.pubman.external.organisation.id"));
    
            StringReader xmlSource = new StringReader(itemXML);
            transformer.transform(new StreamSource(xmlSource), new StreamResult(writer));
        }
        catch (TransformerException e)
        {
            this.logger.error("An error occurred during a third party metadata transformation.", e);
            throw new RuntimeException();
        }
        
        return writer.toString();
    }
    
    public boolean checkXsltTransformation(String formatFrom, String formatTo)
    {
        String xsltUri = formatFrom.toLowerCase().trim() + "2" + formatTo.toLowerCase().trim() + ".xsl";
        boolean check = false;
        
        try {
            
            File transformFile = ResourceUtil.getResourceAsFile(this.METADATA_XSLT_LOCATION +"/"+xsltUri);
            System.out.println("File: " + transformFile);
            check = true;
            
        }
        catch (FileNotFoundException e){this.logger.warn("No transformation file from format: " + formatFrom + " to format: " + formatTo);}

        return check;
    }
}
