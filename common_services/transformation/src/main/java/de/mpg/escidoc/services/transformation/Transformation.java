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

package de.mpg.escidoc.services.transformation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import de.mpg.escidoc.services.transformation.exceptions.TransformationNotSupportedException;
import de.mpg.escidoc.services.transformation.valueObjects.Format;


/**
 * The transformations interface.
 * @author kleinfe1 (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public interface Transformation
{
 
    /**
     * Annotation to identify the Transformation modules.
     * @author kleinfe1 (initial creation)
     * @author $Author$ (last modification)
     * @version $Revision$ $LastChangedDate$
     *
     */
    @Retention( RetentionPolicy.RUNTIME )
    public @interface TransformationModule {}
 
    /**
     * The name of the EJB service.
     */
    public static final String SERVICE_NAME = "ejb/de/mpg/escidoc/services/transformation/TransformationBean";
    
    /**
     * Get all possible source formats. 
     * @return Format[]: list of possible source formats as value object
     * @throws RuntimeException
     */
    public Format[] getSourceFormats() throws RuntimeException;
    
    /**
     * Get all possible source formats for a target format.
     * 
     * @param Format : the target format
     * @return Format[]: list of possible source formats as value object
     * @throws RuntimeException
     */
    public Format[] getSourceFormats(Format trg) throws RuntimeException;
    
    /**
     * Get all possible source formats. 
     * @return String: list of possible source formats as xml
     * @throws RuntimeException
     */
    public String getSourceFormatsAsXml() throws RuntimeException;
    
    /**
     * Get all possible target formats for a source format.
     * @param src  A source value object 
     * @return Format[]: list of possible target formats as value object
     * @throws RuntimeException
     */
    public Format[] getTargetFormats(Format src) throws RuntimeException;
    
    /**
     * Get all possible target formats for a source format. 
     * @param srcFormatName  The name of the source format
     * @param srcType  The type of the source
     * @param srcEncoding  The sources encoding
     * @return String: list of possible target formats as xml
     * @throws RuntimeException
     */
    public String getTargetFormatsAsXml(String srcFormatName, String srcType, String srcEncoding)
            throws RuntimeException;
    
    /**
     * Transforms a source object into a target object.
     * @param src  The textual representation of the item to transform
     * @param srcFormatName  The name of the source format
     * @param srcType  The type of the source
     * @param srcEncoding  The sources encoding
     * @param trgFormatName  The name of the target format
     * @param trgType  The type of the target
     * @param trgEncoding  The target encoding
     * @param service  The service for the transformation (at the moment only eSciDoc)
     * @return byte[]  target object
     * @throws TransformationNotSupportedException
     * @throws RuntimeException
     */
    public byte[] transform(byte[] src, String srcFormatName, String srcType, String srcEncoding, 
            String trgFormatName, String trgType, String trgEncoding, String service)
            throws TransformationNotSupportedException, RuntimeException;
    
    /**
     * Transforms a source object into a target object.
     * @param src  The textual representation of the item to transform
     * @param srcFormat  Value object describing the source format
     * @param trgFormat  Value object describing the target format
     * @param service  The service for the transformation (at the moment only eSciDoc)
     * @return  byte []  target object
     * @throws TransformationNotSupportedException
     * @throws RuntimeException
     */
    public byte[] transform(byte[] src, Format srcFormat, Format trgFormat, String service)
            throws TransformationNotSupportedException, RuntimeException;
    
    
}
