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

package de.mpg.escidoc.services.syndicationmanager;

import java.io.IOException;
import java.net.URISyntaxException;

import com.sun.syndication.io.FeedException;

/**
 * Interface for rss/atom syndications on eSciDoc   
 * @author Vlad Makarenko  (initial creation) 
 * @author $Author: vdm $ (last modification)
 * $Revision:$
 * $LastChangedDate:$ 
 */	


public interface SyndicationHandler {

	/**
     * The name to obtain this service.
     */
    String SERVICE_NAME = "ejb/de/mpg/escidoc/services/syndicationmanager/StructuredExportHandler";
	
	
    /**
     * Returns the XML representation of the specified feeds
     * are available for the service  
     * @return XML string
     * @throws IOException 
     */
    public String explainFeedsXML();
	
    /**
     * Returns the id list of the available feeds
     * @return String[]
     */
    public String[] getFeedList();
    
    /**
     * Returns the list of available formats for the feed
     * @param uri is a feed uri
     * @return String[] list of the feed formats
     */
    public String[] getFeedFormatList(String uri);
    
    /**
     * Generates the feed according to the feed id and feed format 
     * @param uri is feed uri
     * @return
     * @throws FeedException 
     */
    public byte[] getFeed(String uri) throws SyndicationManagerException, IOException, URISyntaxException, FeedException;
	
}
