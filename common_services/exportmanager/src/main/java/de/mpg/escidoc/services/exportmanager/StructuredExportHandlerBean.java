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

package de.mpg.escidoc.services.exportmanager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.jboss.annotation.ejb.RemoteBinding;
import org.xml.sax.SAXException;

import de.mpg.escidoc.services.exportmanager.StructuredExportHandler;
import de.mpg.escidoc.services.exportmanager.StructuredExportXSLTNotFoundException;
import de.mpg.escidoc.services.exportmanager.StructuredExportManagerException;

/**
* This class provides the ejb implementation of the {@link StructuredExportHandler} interface.
* 
* @author Vladislav Makarenko  (initial creation)
* @author $Author: vmakarenko $ (last modification)
* @version $Revision:$,  $LastChangedDate:$
*/

@Stateless
@Remote
@RemoteBinding(jndiBinding = StructuredExportHandler.SERVICE_NAME)

public class StructuredExportHandlerBean implements StructuredExportHandler
{
	
	// private static final long serialVersionUID = 1L;
    
    /**
     * Logger for this class.
     */
    private static Logger logger = Logger.getLogger(StructuredExportHandlerBean.class);
    
    /**
     * {@inheritDoc}
     */
	public String explainFormats() throws StructuredExportManagerException,
			UnsupportedEncodingException, IOException {
		
        return new StructuredExport().explainFormats();
	}

    /**
     * {@inheritDoc}
     */
	public String[] getFormatsList() throws StructuredExportManagerException {
		
        return new StructuredExport().getFormatsList();
	}

    /**
     * {@inheritDoc}
     */
	public byte[] getOutput(String itemList, String exportFormat)
			throws StructuredExportXSLTNotFoundException,
			StructuredExportManagerException {
		
        byte[] ret = null;
        StructuredExport exportService = new StructuredExport();
        ret  = exportService.getOutput(itemList, exportFormat);
            
        logger.debug("getOutput result: " + new String(ret) );
        
        return ret;
	}
}
