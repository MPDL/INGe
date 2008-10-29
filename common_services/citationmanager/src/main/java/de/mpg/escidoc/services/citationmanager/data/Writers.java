/*
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

/**
*
* Citation Style Processing. 
* Writers.   
*
* @author $Author:$ (last modification)
* @version $Revision:$ $LastChangedDate:$
*
*/
package de.mpg.escidoc.services.citationmanager.data;

import java.io.IOException;

import org.xml.sax.SAXException;

import de.mpg.escidoc.services.citationmanager.CitationStyleManagerException;
import de.mpg.escidoc.services.citationmanager.ProcessCitationStyles;
import de.mpg.escidoc.services.citationmanager.utils.ResourceUtil;
import de.mpg.escidoc.services.citationmanager.utils.Utils;

public class Writers {
	/**
     * Writes LayoutElementsCollection to XML file
     * @param path A path to the CitationStyles directory
     * @param name A name of the CitationStyle
     * @throws IOException
     * @throws SAXException
     * @throws CitationStyleManagerException 
     */
    public static void writeLayoutElementsToXml(ProcessCitationStyles pcs, String name) throws IOException, SAXException, CitationStyleManagerException {
    	Utils.checkPcs(pcs);
    	Utils.checkName(name);
        try {
            pcs.getLec().writeToXml(
            		ResourceUtil.getPathToCitationStyles() 
            		+ "/" + name + "/LayoutElements.xml");
        } catch (IOException e) {
            throw new IOException("Cannot write to XML:" + e);
        } catch (SAXException e) {
            throw new SAXException("SAX error:" + e);
        }
    }

    /**
     * Writes LayoutElementsCollection to XML file
     * @param path A path to the CitationStyles directory
     * @param name A name of the CitationStyle
     * @throws IOException
     * @throws SAXException
     * @throws CitationStyleManagerException 
     */
    public static void writeCitationStyleToXml(ProcessCitationStyles pcs, String name) throws IOException, SAXException, CitationStyleManagerException {
    	Utils.checkPcs(pcs);
    	Utils.checkName(name);
    	
        try {
            pcs.getCsc().writeToXml(
            		ResourceUtil.getPathToCitationStyles() 
            		+ "/" + name + "/" + pcs.CITATION_XML_FILENAME
            );
        } catch (IOException e) {
            throw new IOException("Cannot write to XML:" + e);
        } catch (SAXException e) {
            throw new SAXException("SAX error:" + e);
        }
    }
}
