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

package de.mpg.escidoc.services.citationmanager;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.apache.log4j.Logger;
import org.jboss.annotation.ejb.RemoteBinding;

import net.sf.jasperreports.engine.JRException;
import java.io.IOException;



/**
 * EJB implementation of interface {@link CitationStyleHandler}. It will use an external package.
 * It can be considered as a wrapper of the external package.
 * 
 * @author Galina Stancheva (initial creation)
 * @author $Author: vdm $ (last modification)
 * @version $Revision: 146 $ $LastChangedDate: 2007-07-17 12:19:12 +0200 (Di, 17 Jul 2007) $
 * Revised by StG: 24.08.2007
 */
@Stateless
@Remote
@RemoteBinding(jndiBinding = CitationStyleHandler.SERVICE_NAME)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class CitationStyleHandlerBean implements CitationStyleHandler
{ 
    /**
     * Logger for this class.
     */
    private static Logger logger = Logger.getLogger(CitationStyleHandlerBean.class);
    ProcessCitationStyles citStylesMan = new ProcessCitationStyles();

    
    
    /**
     * {@inheritDoc}
     * @throws CitationStyleManagerException 
     */
     public String explainStyles() throws CitationStyleManagerException
     {           
    	 String exportStyles = null;
    	 exportStyles = citStylesMan.explainStyles();                 
    	 return exportStyles;
     }

     /**
      * {@inheritDoc}
     * @throws IOException 
      */
    public byte[] getOutput(String citationStyle, String outputFormat, String itemList)
        throws JRException, CitationStyleManagerException, IOException
    {
        logger.debug("CitationStyleHandlerBean getOutput with citationStyle " + citationStyle);
        logger.debug("CitationStyleHandlerBean getOutput with outputFormat " + outputFormat);
       
        byte[] exportData = null;
        exportData = citStylesMan.getOutput(citationStyle, outputFormat, itemList);
        return exportData;
    }

    /**
     * {@inheritDoc}
    * @throws CitationStyleManagerException 
     */
	public boolean isCitationStyle(String citationStyle)
			throws CitationStyleManagerException {
		return citStylesMan.isCitationStyle(citationStyle);
	}
}
