
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
* Copyright 2006-2010 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

package de.mpg.escidoc.services.transformation.transformations.otherFormats.msexcel;

import org.xml.sax.helpers.DefaultHandler;

import de.mpg.escidoc.services.transformation.Transformation;
import de.mpg.escidoc.services.transformation.Transformation.TransformationModule;
import de.mpg.escidoc.services.transformation.exceptions.TransformationNotSupportedException;
import de.mpg.escidoc.services.transformation.valueObjects.Format;

/**
 * @author kurt (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$ 
 *
 */
@TransformationModule
public class ExcelTransformation extends DefaultHandler implements Transformation
{
    
    private static final Format MS_EXCEL_XML_FORMAT
        = new Format("ms-excel-xml", "application/xml", "*");
    private static final Format CONE_PERSON_RDF_FORMAT = new Format("eSciDoc-publication-item", "application/xml", "*");
    private static final Format EDOC_FORMAT = new Format("eDoc", "application/xml", "*");
    private static final Format EDOC_FORMAT_AEI = new Format("eDoc-AEI", "application/xml", "*");
    
    private static final String XSLT_PATH = "transformations/otherFormats/xslt/edoc-to-escidoc.xslt";

    /* (non-Javadoc)
     * @see de.mpg.escidoc.services.transformation.Transformation#getSourceFormats()
     */
    public Format[] getSourceFormats() throws RuntimeException
    {
        // TODO Auto-generated method stub
        return new Format[]{};
    }

    /* (non-Javadoc)
     * @see de.mpg.escidoc.services.transformation.Transformation#getSourceFormats(de.mpg.escidoc.services.transformation.valueObjects.Format)
     */
    public Format[] getSourceFormats(Format trg) throws RuntimeException
    {
        // TODO Auto-generated method stub
        return new Format[]{};
    }

    /* (non-Javadoc)
     * @see de.mpg.escidoc.services.transformation.Transformation#getSourceFormatsAsXml()
     */
    public String getSourceFormatsAsXml() throws RuntimeException
    {
        // TODO Auto-generated method stub
        return "";
    }

    /* (non-Javadoc)
     * @see de.mpg.escidoc.services.transformation.Transformation#getTargetFormats(de.mpg.escidoc.services.transformation.valueObjects.Format)
     */
    public Format[] getTargetFormats(Format src) throws RuntimeException
    {
        // TODO Auto-generated method stub
        return new Format[]{};
    }

    /* (non-Javadoc)
     * @see de.mpg.escidoc.services.transformation.Transformation#getTargetFormatsAsXml(java.lang.String, java.lang.String, java.lang.String)
     */
    public String getTargetFormatsAsXml(String srcFormatName, String srcType, String srcEncoding)
            throws RuntimeException
    {
        // TODO Auto-generated method stub
        return "";
    }

    /* (non-Javadoc)
     * @see de.mpg.escidoc.services.transformation.Transformation#transform(byte[], de.mpg.escidoc.services.transformation.valueObjects.Format, de.mpg.escidoc.services.transformation.valueObjects.Format, java.lang.String)
     */
    public byte[] transform(byte[] src, Format srcFormat, Format trgFormat, String service)
            throws TransformationNotSupportedException, RuntimeException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see de.mpg.escidoc.services.transformation.Transformation#transform(byte[], java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public byte[] transform(byte[] src, String srcFormatName, String srcType, String srcEncoding, String trgFormatName,
            String trgType, String trgEncoding, String service) throws TransformationNotSupportedException,
            RuntimeException
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    
    
}
