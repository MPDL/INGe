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

package de.mpg.escidoc.services.pubman;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.rpc.ServiceException;
import de.fiz.escidoc.om.ContextHandlerRemote;
import de.fiz.escidoc.om.ContextHandlerRemoteServiceLocator;
import de.fiz.escidoc.om.ItemHandlerRemote;
import de.fiz.escidoc.om.ItemHandlerRemoteServiceLocator;
import de.mpg.escidoc.services.common.XmlTransforming;

/**
 * Delivers different kinds of service instances.
 * 
 * @author Johannes Mueller (initial creation)
 * @author $Author: jmueller $ (last modification)
 * @version $Revision: 422 $ $LastChangedDate: 2007-11-07 12:15:06 +0100 (Wed, 07 Nov 2007) $
 * @revised by MuJ: 19.09.2007
 */
public class ServiceLocator
{
    /**
     * Delivers an {@link XmlTransforming} instance.
     * 
     * @return The {@link XmlTransforming} instance.
     * @throws ServiceNotAvailableException
     */
    public XmlTransforming getXmlTransforming() throws ServiceNotAvailableException
    {
        return (XmlTransforming)lookupService(XmlTransforming.SERVICE_NAME);
    }

    /**
     * Delivers an {@link ContextHandlerRemote} instance.
     * 
     * @return The {@link ContextHandlerRemote} instance.
     */
    public ContextHandlerRemote getContextHandler()
    {
        try
        {
            ContextHandlerRemoteServiceLocator contextlocator = new ContextHandlerRemoteServiceLocator();
            return contextlocator.getContextHandlerService();
        }
        catch (ServiceException e)
        {
            throw new ServiceNotAvailableException(ContextHandlerRemote.class.getName(), e);
        }
    }

    /**
     * Delivers an {@link ItemHandlerRemote} instance.
     * 
     * @return The {@link ItemHandlerRemote} instance.
     */
    public ItemHandlerRemote getItemHandler()
    {
        try
        {
            ItemHandlerRemoteServiceLocator itemlocator = new ItemHandlerRemoteServiceLocator();
            return itemlocator.getItemHandlerService();
        }
        catch (ServiceException e)
        {
            throw new ServiceNotAvailableException(ItemHandlerRemote.class.getName(), e);
        }
    }

    /**
     * Looks up a service with the given name.
     * 
     * @param servicename the name of the service
     * @return The service instance.
     * @throws ServiceNotAvailableException
     */
    private Object lookupService(String servicename) throws ServiceNotAvailableException
    {
        try
        {
            InitialContext context = new InitialContext();
            return context.lookup(servicename);
        }
        catch (NamingException e)
        {
            throw new ServiceNotAvailableException(servicename, e);
        }
    }
}
