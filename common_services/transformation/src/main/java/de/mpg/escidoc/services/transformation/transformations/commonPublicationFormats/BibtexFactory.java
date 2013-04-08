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

package de.mpg.escidoc.services.transformation.transformations.commonPublicationFormats;

import java.util.Map;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.framework.PropertyReader;

/**
 * TODO Description
 *
 * @author walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class BibtexFactory
{
    private static final String DEFAUL_BIBTEX_CLASS = "de.mpg.escidoc.services.transformation.transformations.commonPublicationFormats.Bibtex";
    private static final Logger logger = Logger.getLogger(BibtexFactory.class);
    
    private BibtexFactory () {
        
    }
    
    public static BibtexInterface getBibtexImplementation (Map<String, String> configuration)
    {
        String bibtex;
        try
        {
            bibtex = PropertyReader.getProperty("escidoc.transformation.bibtex.class");
            if (bibtex == null)
            {
                throw new Exception("Classpath is null");
            }
        }
        catch (Exception e)
        {
            logger.warn("Property \"escidoc.transformation.bibtex.class\" not found, taking default BibTex class: "
                    + DEFAUL_BIBTEX_CLASS);
            bibtex = DEFAUL_BIBTEX_CLASS;
        }
        try
        {
            
            Object bibtexImpl = Class.forName(bibtex).newInstance();
            if (bibtexImpl instanceof BibtexInterface)
            {
                ((BibtexInterface)bibtexImpl).setConfiguration(configuration);
                return (BibtexInterface) bibtexImpl;
            }
            else
            {
                throw new RuntimeException("Instantiated Bibtex class (" + bibtexImpl.getClass().getName()
                        + ") does not implement the BibtexInterface.");
            }
        }
        catch (Exception e)
        {
            logger.error("Unable to instantiate BibTex.", e);
            return null;
        }
    }
}
