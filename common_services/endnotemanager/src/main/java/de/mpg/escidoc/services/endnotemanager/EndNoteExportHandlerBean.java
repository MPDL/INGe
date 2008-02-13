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

package de.mpg.escidoc.services.endnotemanager;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import org.apache.log4j.Logger;
import org.jboss.annotation.ejb.RemoteBinding;
import de.mpg.escidoc.services.endnotemanager.EndNoteExport;
import de.mpg.escidoc.services.endnotemanager.EndNoteExportXSLTNotFoundException; 
import de.mpg.escidoc.services.endnotemanager.EndNoteManagerException; 

/**
* This class provides the ejb implementation of the {@link EndNoteExportHandler} interface.
* 
* @author Galina Stancheva  (initial creation)
* @author $Author: vdm $ (last modification)
* @version $Revision: 63 $,  $LastChangedDate: 2007-11-13 15:40:28 +0100 (Tue, 13 Nov 2007) $
*/

@Stateless
@Remote
@RemoteBinding(jndiBinding = EndNoteExportHandler.SERVICE_NAME)

public class EndNoteExportHandlerBean implements EndNoteExportHandler
{
	
	// private static final long serialVersionUID = 1L;
    
    
    /**
     * Logger for this class.
     */
    private static Logger logger = Logger.getLogger(EndNoteExportHandlerBean.class);
    

    /**
     * {@inheritDoc}
     */
    public byte[] getOutput(String itemList) throws 
            EndNoteExportXSLTNotFoundException, EndNoteManagerException
    {

        byte[] ret = null;
        EndNoteExport exportService = new EndNoteExport();
        ret  = exportService.getOutput(itemList);
            
        logger.debug("getOutput result: " + new String(ret) );
        
        return ret;
    }
}
