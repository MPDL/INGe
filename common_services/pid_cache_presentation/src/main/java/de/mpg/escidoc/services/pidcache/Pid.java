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
public class Pid
{
    private String url;
    private String identifier;
    
    /**
     * Default constructor
     */
    public Pid() 
    {
		
	}
    
    /**
     * Constructor with parameters
     * 
     * @param identifier
     * @param url
     */
    public Pid(String identifier, String url)
    {
    	this.identifier = identifier;
    	this.url = url;
    }
    
    /**
     * @return the url
     */
    public String getUrl()
    {
        return url;
    }
    
    /**
     * @param url the url to set
     */
    public void setUrl(String url)
    {
        this.url = url;
    }
    
    /**
     * @return the identifier
     */
    public String getIdentifier()
    {
        return identifier;
    }
    
    /**
     * @param identifier the identifier to set
     */
    public void setIdentifier(String identifier)
    {
        this.identifier = identifier;
    }
    
    /**
     * True if PID exists at GWDG.
     * @return
     * @throws Exception
     */
    public boolean exists() throws Exception
    {
    	GwdgPidService gwdgPidService = new GwdgPidService();
    	xmltransforming xmltransforming = new xmltransforming();
    	String pidXml = gwdgPidService.retrieve(identifier);
    	try 
    	{
			xmltransforming.transFormToPid(pidXml);
		}
		catch (Exception e) 
		{
			return false;
		}
    	return true;
    }
    
    /**
     * True if URL of the PID is not already allocated to another PID.
     * @return
     * @throws Exception
     */
    public boolean hasFreeUrl() throws Exception
    {
    	PidCacheService pidCacheService = new PidCacheService();
    	xmltransforming xmltransforming = new xmltransforming();
    	try 
    	{
			xmltransforming.transFormToPid(pidCacheService.search(url));
		}
		catch (Exception e) 
		{
			return true;
		}
    	return false;
    }
    
    /**
     * Transform the PID into an XML string.
     * @return
     */
    public String asXmlString()
    {
    	xmltransforming xmltransforming = new xmltransforming();
    	return xmltransforming.transformtoPidXml(this);
    }
    
    
}
