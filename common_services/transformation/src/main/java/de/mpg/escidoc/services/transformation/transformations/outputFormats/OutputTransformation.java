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

package de.mpg.escidoc.services.transformation.transformations.outputFormats;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.JRTextExporterParameter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
import net.sf.jasperreports.engine.query.JRXPathQueryExecuterFactory;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRXmlUtils;

import org.w3c.dom.Document;

import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.transformation.Util;
import de.mpg.escidoc.services.transformation.Util.Styles;
import de.mpg.escidoc.services.transformation.exceptions.TransformationNotSupportedException;
import de.mpg.escidoc.services.transformation.valueObjects.Format;

/**
 * Implements transformations for output formats.
 * @author Friederike Kleinfercher (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class OutputTransformation
{   
    // Output Formats enum
    public static enum OutFormats { rtf, pdf, html, odt, snippet, txt }; 
    
    
    /**
     * Public constructor.
     */
    public OutputTransformation()
    {
    }
    
    /**
     * This method transforms an item in format snippet into an item in a given output format.
     * @param src
     * @param srcFormat
     * @param trgFormat
     * @param service
     * @return The transformed item
     * @throws TransformationNotSupportedException 
     * @throws JRException 
     * @throws IOException 
     * @throws TechnicalException 
     */
    public byte[] transformSnippetToOutput(byte[] src, Format srcFormat, Format trgFormat, String service) 
        throws TransformationNotSupportedException, JRException, IOException, TechnicalException
    {
        byte[] output = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Map<String, Object> params = new HashMap<String, Object>();
        Document document = null;
        JRExporter exporter = null;   
        Styles style = Util.getStyleInfo(srcFormat);
        
        ByteArrayInputStream bais = new ByteArrayInputStream(src);
        BufferedInputStream bis = new BufferedInputStream(bais);

        document = JRXmlUtils.parse(bis);
       
        String path = OutputUtil.getPathToCitationStyles() + "/jasper" + "/citation-style.jasper";

        System.out.println("PATH: " + path);
        InputStream csj =  ResourceUtil.getResourceAsStream(path);       
        JasperReport jr = (JasperReport)JRLoader.loadObject(csj); 

        params.put(JRXPathQueryExecuterFactory.PARAMETER_XML_DATA_DOCUMENT, document);

        JasperPrint jasperPrint= JasperFillManager.fillReport(jr, params, new JRXmlDataSource(document, jr.getQuery().getText()));  
        
        switch ( OutFormats.valueOf(trgFormat.getName().toLowerCase()) ) 
        {
            case pdf:
                exporter = new JRPdfExporter();
                break;
            case html:
                exporter = new JRHtmlExporter();
                /* Switch off pagination and null pixel alignment for JRHtmlExporter */
                exporter.setParameter(JRHtmlExporterParameter.BETWEEN_PAGES_HTML, "");
                exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, Boolean.FALSE);
                exporter.setParameter(JRHtmlExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.FALSE);
                break;
            case rtf:
                exporter = new JRRtfExporter();
                break;
            case odt:
                exporter = new JROdtExporter();
                break;
            case txt:
                exporter = new JRTextExporter();    
                exporter.setParameter(JRTextExporterParameter.CHARACTER_WIDTH, new Integer(10));
                exporter.setParameter(JRTextExporterParameter.CHARACTER_HEIGHT, new Integer(10));
                exporter.setParameter(JRTextExporterParameter.CHARACTER_ENCODING, "UTF-8");
                break;
            default:    
                throw new TransformationNotSupportedException (
                        "Transformation to format " + trgFormat.getName() + " is not supported");
        }
        
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);

        exporter.exportReport();

        output = baos.toByteArray();
        return output;
    }
}
