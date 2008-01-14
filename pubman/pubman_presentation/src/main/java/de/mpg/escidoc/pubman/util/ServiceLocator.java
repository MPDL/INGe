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

package de.mpg.escidoc.pubman.util;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.rpc.ServiceException;
import org.apache.log4j.Logger;
import de.fiz.escidoc.om.ContextHandlerRemote;
import de.fiz.escidoc.om.ContextHandlerRemoteServiceLocator;
import de.fiz.escidoc.om.ItemHandlerRemote;
import de.fiz.escidoc.om.ItemHandlerRemoteServiceLocator;
import de.mpg.escidoc.services.common.XmlTransforming;

/**
 * Class for locationg and fetching several services.
 * 
 * @author: Tobias Schraut, created 10.01.2007
 * @version: $Revision: 1587 $ $LastChangedDate: 2007-11-20 10:54:36 +0100 (Tue, 20 Nov 2007) $ Revised by ScT: 22.08.2007
 */
public class ServiceLocator
{
    /**
     * Logging instance for technical logging.
     */
    @SuppressWarnings("unused")
    private Logger logger = Logger.getLogger(getClass());

    /**
     * gets the pubman tranforing service
     * 
     * @return XmlTransforming an instance of the transforing servive
     * @throws ServiceNotAvailableException
     */
    public XmlTransforming getPubManTransforming() throws ServiceNotAvailableException
    {
        return (XmlTransforming)lookupService(XmlTransforming.SERVICE_NAME);
    }

    /**
     * Gets the context handler service
     * 
     * @return ContextHandlerRemote an instance of the context handler service
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
     * Gets the item handler service
     * 
     * @return ItemHandlerRemote an instance of the item handler service
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
