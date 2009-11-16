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

package de.mpg.escidoc.services.transformation.transformations.outputFormats;

import org.apache.log4j.Logger;

/**
 * Implements transformations for citation styles.
 * @author Friederike Kleinfercher (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 1953 $ $LastChangedDate: 2009-05-07 10:40:57 +0200 (Do, 07 Mai 2009) $
 *
 */
public class OutputTransformation
{
    private final Logger logger = Logger.getLogger(OutputTransformation.class);
    
    private final String typeHTML = "text/html";
    private final String typeRTF1 = "text/richtext";
    private final String typeRTF2 = "application/rtf";
    private final String typeODT =  "application/vnd.oasis.opendocument.text";
    private final String typePDF =  "application/pdf";
    private final String typeSnippet = "snippet";
    
    /**
     * Public constructor.
     */
    public OutputTransformation()
    {
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
