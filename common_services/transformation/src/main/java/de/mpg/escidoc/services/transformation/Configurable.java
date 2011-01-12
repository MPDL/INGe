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

package de.mpg.escidoc.services.transformation;

import java.util.List;
import java.util.Map;

import de.mpg.escidoc.services.transformation.exceptions.TransformationNotSupportedException;
import de.mpg.escidoc.services.transformation.valueObjects.Format;


/**
 * The Configurable interface.
 * @author kleinfe1 (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 3183 $ $LastChangedDate: 2010-05-27 16:10:51 +0200 (Do, 27 Mai 2010) $
 *
 */
public interface Configurable
{
 
    /**
     * Transforms a source object into a target object.
     * @param src  The textual representation of the item to transform
     * @param srcFormat  Value object describing the source format
     * @param trgFormat  Value object describing the target format
     * @param service  The service for the transformation (at the moment only eSciDoc)
     * @param configuration A Map<String, String> that holds configuration key value pairs
     * @return  byte [] target object
     * @throws TransformationNotSupportedException
     * @throws RuntimeException
     */
    public byte[] transform(byte[] src, Format srcFormat, Format trgFormat, String service, Map<String, String> configuration)
            throws TransformationNotSupportedException, RuntimeException;
    
    public Map<String, String> getConfiguration(Format srcFormat, Format trgFormat) throws Exception;
    
    public List<String> getConfigurationValues(Format srcFormat, Format trgFormat, String key) throws Exception;

}
