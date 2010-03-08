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

package de.mpg.escidoc.services.pidcache;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.PidServiceResponseVO;
import de.mpg.escidoc.services.pidcache.gwdg.GwdgPidService;
import de.mpg.escidoc.services.pidcache.tables.Queue;

/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class Pid extends PidServiceResponseVO
{
    private XmlTransforming xmlTransforming = null;
    /**
     * Default constructor
     * @throws NamingException 
     */
    public Pid() throws NamingException 
    {
		super();
		InitialContext context = new InitialContext();
		xmlTransforming = (XmlTransforming)context.lookup(XmlTransforming.SERVICE_NAME);
	}
    
    /**
     * Constructor with parameters
     * 
     * @param identifier
     * @param url
     * @throws NamingException 
     */
    public Pid(String identifier, String url) throws NamingException
    {
    	this();
    	this.identifier = identifier;
    	this.url = url;
    }
    
    /**
     * True if PID exists at GWDG.
     * @return
     * @throws Exception
     */
//    public boolean exists() throws Exception
//    {
//    	GwdgPidService gwdgPidService = new GwdgPidService();
//    	String pidXml = gwdgPidService.retrieve(this.identifier);
//    	try 
//    	{
//			xmlTransforming.transformToPidServiceResponse(pidXml);
//		}
//		catch (Exception e) 
//		{
//			return false;
//		}
//    	return true;
//    }
}
