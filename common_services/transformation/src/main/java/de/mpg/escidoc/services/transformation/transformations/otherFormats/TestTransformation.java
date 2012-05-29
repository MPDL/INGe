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

package de.mpg.escidoc.services.transformation.transformations.otherFormats;

import java.io.StringWriter;

import de.mpg.escidoc.services.transformation.Transformation;
import de.mpg.escidoc.services.transformation.Transformation.TransformationModule;
import de.mpg.escidoc.services.transformation.exceptions.TransformationNotSupportedException;
import de.mpg.escidoc.services.transformation.valueObjects.Format;

/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
@TransformationModule
public class TestTransformation implements Transformation
{
    
    public static final Format SOURCE_FORMAT = new Format("test-src", "text/plain", "UTF-8");
    public static final Format TARGET_FORMAT = new Format("test-trg", "text/plain", "UTF-8");
    
    /* (non-Javadoc)
     * @see de.mpg.escidoc.services.transformation.Transformation#getSourceFormats()
     */
    public Format[] getSourceFormats() throws RuntimeException
    {
        return new Format[]{SOURCE_FORMAT};
    }

    /* (non-Javadoc)
     * @see de.mpg.escidoc.services.transformation.Transformation#getSourceFormats(de.mpg.escidoc.services.transformation.valueObjects.Format)
     */
    public Format[] getSourceFormats(Format trg) throws RuntimeException
    {
        if (TARGET_FORMAT.equals(trg))
        {
            return new Format[]{SOURCE_FORMAT};
        }
        else
        {
            return new Format[]{};
        }
    }

    /* Does not need to be implemented
     */
    public String getSourceFormatsAsXml() throws RuntimeException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see de.mpg.escidoc.services.transformation.Transformation#getTargetFormats(de.mpg.escidoc.services.transformation.valueObjects.Format)
     */
    public Format[] getTargetFormats(Format src) throws RuntimeException
    {
        if (SOURCE_FORMAT.equals(src))
        {
            return new Format[]{TARGET_FORMAT};
        }
        else
        {
            return new Format[]{};
        }
    }

    /* Does not need to be implemented
     */
    public String getTargetFormatsAsXml(String srcFormatName, String srcType, String srcEncoding)
            throws RuntimeException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see de.mpg.escidoc.services.transformation.Transformation#transform(byte[], java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public byte[] transform(byte[] src, String srcFormatName, String srcType, String srcEncoding, String trgFormatName,
            String trgType, String trgEncoding, String service) throws TransformationNotSupportedException,
            RuntimeException
    {
        try
        {
            String source = new String(src, "UTF-8");
            StringWriter writer = new StringWriter();
            for (int i = src.length - 1; i >= 0; i--)
            {
                writer.append(source.charAt(i));
            }
            return writer.toString().getBytes("UTF-8");
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /* (non-Javadoc)
     * @see de.mpg.escidoc.services.transformation.Transformation#transform(byte[], de.mpg.escidoc.services.transformation.valueObjects.Format, de.mpg.escidoc.services.transformation.valueObjects.Format, java.lang.String)
     */
    public byte[] transform(byte[] src, Format srcFormat, Format trgFormat, String service)
            throws TransformationNotSupportedException, RuntimeException
    {
        return transform(src, srcFormat.getName(), srcFormat.getType(), srcFormat.getEncoding(), trgFormat.getName(), trgFormat.getType(), trgFormat.getEncoding(), service);
    }
}
