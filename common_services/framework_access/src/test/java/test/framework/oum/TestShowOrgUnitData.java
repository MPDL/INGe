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
package test.framework.oum;

import static org.junit.Assert.assertNotNull;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import test.framework.TestBase;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Testcases to show all stored organizational units.
 *
 * @author Peter (initial creation)
 * @author $Author: wfrank $ (last modification)
 * @version $Revision: 314 $ $LastChangedDate: 2007-11-07 13:12:14 +0100 (Wed, 07 Nov 2007) $
 * @revised by FrW: 10.03.2008
 */
public class TestShowOrgUnitData extends TestBase
{
    private static final String FILTER_ALL = "<param></param>";
    private static final String FILTER_TOP_LEVEL = "<param><filter name=\"top-level-organizational-units\"/></param>";

    private Logger logger = Logger.getLogger(getClass());

    private void showUnits(String units,String line) throws Exception
    {
        final String xPath = "//organizational-unit-list/organizational-unit";
        final String attributes[] = {"objid"};
        final String nodes[] = {"abbreviation","name","uri","description","postcode","country","region","address","city","telephone","fax","email"};
        Document doc = getDocument(units, false);
        NodeList list = selectNodeList(doc, xPath);
        logger.info(list.getLength() + " Organizational Units:");
        for (int n=1; n<=list.getLength(); ++n)
        {
            logger.info(line);
            for (int i=0; i<attributes.length; ++i)
            {
                logger.info(attributes[i] + "=" + getAttributeValue(doc, xPath, attributes[i]));
            }
            String id = getAttributeValue(list.item(n-1), xPath + "[" + n + "]", "objid");
            Node node = selectSingleNode(doc, xPath + "[" + n + "]" + "/properties/public-status");
            logger.info("status=" + node.getTextContent());
            for (int i=0; i<nodes.length; ++i)
            {
                node = selectSingleNode(doc, xPath + "[" + n + "]" + "/organization-details/" + nodes[i]);
                logger.info(nodes[i] + "=" + node.getTextContent());
            }
            String children = ServiceLocator.getOrganizationalUnitHandler().retrieveChildObjects(id);
            logger.debug("children=" + children);
            showUnits(children,line+LINE);
        }
    }

    /* (non-Javadoc)
     * @see test.framework.TestBase#setUp()
     */
    public void setUp() throws Exception
    {
        userHandle = loginSystemAdministrator();
    }

    /**
     * Shows all toplevel organizational units and there children.
     */
    @Test
    public void showOrganizationalUnits() throws Exception
    {
        String filter = FILTER_TOP_LEVEL;
        String units = ServiceLocator.getOrganizationalUnitHandler().retrieveOrganizationalUnits(filter);
        logger.debug("units=" + units);
        assertNotNull(units);
        logger.info(LINE);
        showUnits(units,LINE);
    }
}
