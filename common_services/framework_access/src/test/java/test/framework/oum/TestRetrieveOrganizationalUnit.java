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
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */ 
package test.framework.oum;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.junit.Test;

import test.framework.TestBase;
import de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Test cases for the basic service OrganizationalUnitHandler.
 * 
 * @author Peter Broszeit (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * @revised by FrW: 10.03.2008
 */
public class TestRetrieveOrganizationalUnit extends TestBase
{
    private Logger logger = Logger.getLogger(getClass());
    
    /**
     * Test method for {@link de.escidoc.www.services.oum.OrganizationalUnitHandlerRemote#retrieveOrganizationalUnits(java.util.Map)}.
     */
    @Test
    public void retrieveOrganizationalUnits() throws Exception
    {
        long zeit = -System.currentTimeMillis();
        String unit = ServiceLocator.getOrganizationalUnitHandler().retrieveOrganizationalUnits(filterMap);
        zeit += System.currentTimeMillis();
        logger.info("retrieveOrganizationalUnit(" + ")->" + zeit + "ms");
        logger.debug("OrganizationalUnit(" + ")=" + unit);
        assertNotNull(unit);
    }

    /**
     * Test method for {@link de.escidoc.www.services.oum.OrganizationalUnitHandlerRemote#retrieve(java.lang.String)}.
     */
    @Test
    public void retrieveOrganizationalUnit() throws Exception
    {
        logger.info("Framework: " + ServiceLocator.getFrameworkUrl());
        String id = PropertyReader.getProperty("framework.organizational_unit.id");
        long zeit = -System.currentTimeMillis();
        String unit = ServiceLocator.getOrganizationalUnitHandler().retrieve(id);
        zeit += System.currentTimeMillis();
        logger.info("retrieveOrganizationalUnit(" + id + ")->" + zeit + "ms");
        logger.debug("OrganizationalUnit(" + id + ")=" + unit);
        assertNotNull(unit);
    }

    /**
     * Test method for {@link de.escidoc.www.services.oum.OrganizationalUnitHandlerRemote#retrieve(java.lang.String)}.
     */
    @Test
    public void retrieveOrganizationalUnitWithSysAdmin() throws Exception
    {
        String id = PropertyReader.getProperty("framework.organizational_unit.id");
        long zeit = -System.currentTimeMillis();
        String unit = ServiceLocator.getOrganizationalUnitHandler(loginSystemAdministrator()).retrieve(id);
        zeit += System.currentTimeMillis();
        logger.info("retrieveOrganizationalUnitWithSysAdmin(" + id + ")->" + zeit + "ms");
        logger.debug("OrganizationalUnit(" + id + ")=" + unit);
        assertNotNull(unit);
    }

    /**
     * Test method for {@link de.escidoc.www.services.oum.OrganizationalUnitHandlerRemote#retrieve(java.lang.String)}.
     */
    @Test(expected = OrganizationalUnitNotFoundException.class)
    public void retrieveOrganizationalUnitNotExisting() throws Exception
    {
        String id = "escidoc:persistentX";
        long zeit = -System.currentTimeMillis();
        String unit = ServiceLocator.getOrganizationalUnitHandler().retrieve(id);
        zeit += System.currentTimeMillis();
        logger.info("retrieveOrganizationalUnitNotExisting(" + id + ")->" + zeit + "ms");
        assertTrue(unit, false);
    }

    /**
     * Test method for {@link de.escidoc.www.services.oum.OrganizationalUnitHandlerRemote#retrieveParents(java.lang.String)}.
     */
    @Test
    public void retrieveParents() throws Exception
    {
        String id = PropertyReader.getProperty("framework.organizational_unit.id");
        long zeit = -System.currentTimeMillis();
        String parents = ServiceLocator.getOrganizationalUnitHandler().retrieveParentObjects(id);
        zeit += System.currentTimeMillis();
        logger.info("retrieveParents(" + id + ")->" + zeit + "ms");
        logger.debug("Parents(" + id + ")=" + parents);
        assertNotNull(parents);
    }

    /**
     * Test method for {@link de.escidoc.www.services.oum.OrganizationalUnitHandlerRemote#retrieveChildren(java.lang.String)}.
     */
    @Test
    public void retrieveChildren() throws Exception
    {
        String id = PropertyReader.getProperty("framework.organizational_unit.id");
        long zeit = -System.currentTimeMillis();
        String children = ServiceLocator.getOrganizationalUnitHandler().retrieveChildObjects(id);
        zeit += System.currentTimeMillis();
        logger.info("retrieveChildren(" + id + ")->" + zeit + "ms");
        logger.debug("Children(" + id + ")=" + children);
        assertNotNull(children);
    }

    /**
     * Test method for
     * {@link de.escidoc.www.services.oum.OrganizationalUnitHandlerRemote#retrieveOrganizationalUnits(java.lang.String)}.
     */
    @Test
    public void retrieveOrganizationalUnitsByID() throws Exception
    {
        String id = PropertyReader.getProperty("framework.organizational_unit.id");
        filterMap.put("id", new String[]{id});
        long zeit = -System.currentTimeMillis();
        String units = ServiceLocator.getOrganizationalUnitHandler().retrieveOrganizationalUnits(filterMap);
        zeit += System.currentTimeMillis();
        logger.info("retrieveOrganizationalUnitsByID(" + id + ")->" + zeit + "ms");
        logger.debug("OrganizationalUnitsByID(" + id + ")=" + units);
        assertNotNull(units);
    }

    /**
     * Test method for
     * {@link de.escidoc.www.services.oum.OrganizationalUnitHandlerRemote#retrieveOrganizationalUnits(java.lang.String)}.
     */
    @Test
    public void retrieveTopLevelOrganizationalUnits() throws Exception
    {
        long zeit = -System.currentTimeMillis();
        String units = ServiceLocator.getOrganizationalUnitHandler().retrieveOrganizationalUnits(filterMap);
        zeit += System.currentTimeMillis();
        logger.info("retrieveTopLevelOrganizationalUnits(" + filterMap + ")->" + zeit + "ms");
        logger.debug("TopLevelorganizationalUnits(" + filterMap + ")=" + units);
        assertNotNull(units);
    }

    /**
     * Test method for {@link de.escidoc.www.services.oum.OrganizationalUnitHandler#retrievePathList(java.lang.String)}.
     */
    @Test
    public void retrieveOrganizationalUnitPathList() throws Exception
    {
        String id = PropertyReader.getProperty("framework.organizational_unit.id");
        long zeit = -System.currentTimeMillis();
        String pathlist = ServiceLocator.getOrganizationalUnitHandler().retrievePathList(id);
        zeit += System.currentTimeMillis();
        logger.info("retrieveOrganizationalUnitPathList(" + id + ")->" + zeit + "ms");
        logger.debug("OrganizationalUnitPathList(" + id + ")=" + pathlist);
        assertNotNull(pathlist);
    }
}
