/*
 * CDDL HEADER START The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with the License. You can
 * obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License for the
 * specific language governing permissions and limitations under the License. When distributing Covered Code, include
 * this CDDL HEADER in each file and include the License file at license/ESCIDOC.LICENSE. If applicable, add the
 * following below this CDDL HEADER, with the fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner] CDDL HEADER END
 */
/*
 * Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft für wissenschaftlich-technische Information mbH
 * and Max-Planck- Gesellschaft zur Förderung der Wissenschaft e.V. All rights reserved. Use is subject to license
 * terms.
 */
package de.mpg.escidoc.services.cone;



import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.framework.PropertyReader;

/**
 * Factory class to retrieve an appropriate implementation of the {@link Querier} interface.
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class QuerierFactory
{
    private static final Logger logger = Logger.getLogger(QuerierFactory.class);
    private static final String DEFAULT_QUERIER = MulgaraQuerier.class.getName();

    /**
     * Hide constructor for factory class.
     */
    private QuerierFactory()
    {
    }

    /**
     * Retrieve correct {@link Querier} instance defined by property.
     * 
     * @return An instance of the currently used {@link Querier} implementation
     */
    public static Querier newQuerier()
    {
        String querier;
        try
        {
            querier = PropertyReader.getProperty("escidoc.cone.querier.class");
        }
        catch (Exception e)
        {
            logger.warn("Property \"escidoc.cone.querier.class\" not found, taking default querier class: "
                    + DEFAULT_QUERIER);
            querier = DEFAULT_QUERIER;
        }
        try
        {
            
            Object querierImpl = Class.forName(querier).newInstance();
            if (querierImpl instanceof Querier)
            {
                return (Querier) querierImpl;
            }
            else
            {
                throw new RuntimeException("Instantiated querier class (" + querierImpl.getClass().getName()
                        + ") does not implement the Querier interface.");
            }
        }
        catch (Exception e)
        {
            logger.error("Unable to instantiate querier.", e);
            return null;
        }
    }
}
