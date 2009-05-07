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
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.services.transformation.transformations;

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
public class IdentityTransformation implements Transformation
{
    /**
     * Don't return anything here.
     * 
     * @return Empty array
     */
    public Format[] getSourceFormats()
    {
        return new Format[]{};
    }

    /**
     * Always return the target format.
     * 
     * @param trg Target format
     * @return Identity
     */
    public Format[] getSourceFormats(Format trg)
    {
        return new Format[]{trg};
    }

    /**
     * Not implemented.
     * 
     * @return Nothing, never
     */
    @Deprecated
    public String getSourceFormatsAsXml()
    {
        throw new RuntimeException("Not implpemented");
    }

    /**
     * Always return the source format.
     * 
     * @param src Source format
     * 
     * @return Identity
     */
    public Format[] getTargetFormats(Format src)
    {
        return new Format[]{src};
    }

    /**
     * Not implemented.
     * 
     * @param srcFormatName Source format name
     * @param srcType Source mime type
     * @param srcEncoding Source encoding
     * 
     * @return Nothing, never
     * 
     */
    @Deprecated
    public String getTargetFormatsAsXml(String srcFormatName, String srcType, String srcEncoding)
    {
        throw new RuntimeException("Not implpemented");
    }

    /**
     * {@inheritDoc}
     */
    public byte[] transform(byte[] src, String srcFormatName, String srcType, String srcEncoding, String trgFormatName,
            String trgType, String trgEncoding, String service) throws TransformationNotSupportedException,
            RuntimeException
    {
        Format srcFormat = new Format(srcFormatName, srcType, srcEncoding);
        Format trgFormat = new Format(trgFormatName, trgType, trgEncoding);
        return transform(src, srcFormat, trgFormat, service);
    }

    /**
     * {@inheritDoc}
     */
    public byte[] transform(byte[] src, Format srcFormat, Format trgFormat, String service)
        throws TransformationNotSupportedException, RuntimeException
    {
        if (srcFormat != null && srcFormat.equals(trgFormat))
        {
            return src;
        }
        else if (srcFormat == null)
        {
            throw new TransformationNotSupportedException("Source format is null");
        }
        else
        {
            throw new TransformationNotSupportedException("Source format and target format differ");
        }
    }
}
