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

package de.mpg.escidoc.services.transformation.transformations.citationFormats;

import java.util.Arrays;
import java.util.List;

import javax.naming.InitialContext;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.citationmanager.CitationStyleHandler;
import de.mpg.escidoc.services.citationmanager.CitationStyleHandlerBean;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;
import de.mpg.escidoc.services.transformation.exceptions.TransformationNotSupportedException;
import de.mpg.escidoc.services.transformation.valueObjects.Format;

/**
 * Implements transformations for citation styles.
 * @author Friederike Kleinfercher (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class CitationTransformation
{
    private final Logger logger = Logger.getLogger(CitationTransformation.class);
    
    private final String typeHTML = "text/html";
    private final String typeRTF1 = "text/richtext";
    private final String typeRTF2 = "application/rtf";
    private final String typeODT =  "application/vnd.oasis.opendocument.text";
    private final String typePDF =  "application/pdf";
    private final String typeSnippet = "snippet";
    
    /**
     * Public constructor.
     */
    public CitationTransformation()
    {
    }
    
    /**
     * Transformation in APA style.
     * @param src
     * @param srcFormat
     * @param trgFormat
     * @param service
     * @return transformed item as byte[]
     * @throws TransformationNotSupportedException
     * @throws RuntimeException
     */

    public byte[] transformEscdocToApa(byte[] src, Format srcFormat, Format trgFormat, String service)
        throws TransformationNotSupportedException, RuntimeException
    {
        byte[] apa = null;
        
        try 
        {
            //InitialContext initialContext = new InitialContext();
            XmlTransforming xmlTransforming = new XmlTransformingBean();
            CitationStyleHandler citeHandler = new CitationStyleHandlerBean();
            PubItemVO itemVO = xmlTransforming.transformToPubItem(new String(src));
            List<PubItemVO> pubitemList = Arrays.asList(itemVO);
            String itemList = xmlTransforming.transformToItemList(pubitemList);
            
            String type = this.getOutputFormat(trgFormat.getType());
            if (type == null)
            {
                this.logger.warn("Transformation not supported: /n " + srcFormat.getName() + ", " + srcFormat.getType() 
                        + ", " + srcFormat.getEncoding() + "/n" + trgFormat.getName() + ", " + trgFormat.getType() 
                        + ", " + trgFormat.getEncoding());
                throw new TransformationNotSupportedException();
            }
            else
            {
                apa = citeHandler.getOutput("APA", type, itemList);
            }          
        }
        catch (Exception e)
        {
            this.logger.error("An error occurred during a citation transformation.", e);
            throw new RuntimeException(e);
        }
        
        return apa;
    }
    
    /**
     * Transformation in AJP style.
     * @param src
     * @param srcFormat
     * @param trgFormat
     * @param service
     * @return transformed item as byte[]
     * @throws TransformationNotSupportedException
     * @throws RuntimeException
     */
    public byte[] transformEscidocToAjp(byte[] src, Format srcFormat, Format trgFormat, String service)
        throws TransformationNotSupportedException, RuntimeException
    {
        byte[] ajp = null;
        
        try 
        {
            InitialContext initialContext = new InitialContext();
            XmlTransforming xmlTransforming = (XmlTransforming) initialContext.lookup(XmlTransforming.SERVICE_NAME);
            CitationStyleHandler citeHandler = (CitationStyleHandler) initialContext
                    .lookup(CitationStyleHandler.SERVICE_NAME);
            PubItemVO itemVO = xmlTransforming.transformToPubItem(new String(src));
            List<PubItemVO> pubitemList = Arrays.asList(itemVO);
            String itemList = xmlTransforming.transformToItemList(pubitemList);
            
            String type = this.getOutputFormat(trgFormat.getType());
            if (type == null)
            {
                this.logger.warn("Transformation not supported: /n " + srcFormat.getName() + ", " + srcFormat.getType() 
                        + ", " + srcFormat.getEncoding() + "/n" + trgFormat.getName() + ", " + trgFormat.getType() 
                        + ", " + trgFormat.getEncoding());
                throw new TransformationNotSupportedException();
            }
            else
            {
                ajp = citeHandler.getOutput("AJP", type, itemList);
            }          
        }
        catch (Exception e)
        {
            this.logger.error("An error occurred during a citation transformation.", e);
            throw new RuntimeException(e);
        }
        
        return ajp;
    }
    
    private String getOutputFormat(String type)
    {
        if (type.toLowerCase().equals(this.typeHTML)) 
        { 
            return "html"; 
        }
        if (type.toLowerCase().equals(this.typeODT)) 
        { 
            return "odt"; 
        }
        if (type.toLowerCase().equals(this.typePDF)) 
        { 
            return "pdf";
        }
        if (type.toLowerCase().equals(this.typeRTF1) || type.toLowerCase().equals(this.typeRTF2)) 
        { 
            return "rtf"; 
        }
        if (type.toLowerCase().equals(this.typeSnippet)) 
        { 
            return "snippet"; 
        }
        
        return null;
    }
}
