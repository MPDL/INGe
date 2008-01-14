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
package test.framework.om;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import de.mpg.escidoc.services.framework.ServiceLocator;
import test.framework.TestBase;

/**
 * Testcases to show all stored contexts.
 *
 * @author Peter (initial creation)
 * @author $Author: pbroszei $ (last modification)
 * @version $Revision: 314 $ $LastChangedDate: 2007-11-07 13:12:14 +0100 (Wed, 07 Nov 2007) $
 * @revised by BrP: 04.09.2007
 */
public class TestShowContextData extends TestBase
{
    private static final String FILTER_ALL = "<param></param>";

    private Logger logger = Logger.getLogger(getClass());

    /* (non-Javadoc)
     * @see test.framework.TestBase#setUp()
     */
    public void setUp() throws Exception
    {
        userHandle = loginSystemAdministrator();
    }

    /**
     * Shows all contexts.
     */
    @Test
    public void showContexts() throws Exception
    {
        String filter = FILTER_ALL;
        String contexts = ServiceLocator.getContextHandler(userHandle).retrieveContexts(filter);
        logger.debug("Contexts=" + contexts);
        final String xPath = "//context-list/context";
        final String attributes[] = {"objid","last-modification-date"};
        final String nodes[] = {"name","description","creation-date","public-status","type"};
        Document doc = getDocument(contexts, false);
        NodeList list = selectNodeList(doc, xPath);
        for (int n=1; n<=list.getLength(); ++n)
        {
            logger.info(LINE);
            for (int i=0; i<attributes.length; ++i)
            {
                logger.info(attributes[i] + "=" + getAttributeValue(doc, xPath, attributes[i]));
            }
            for (int i=0; i<nodes.length; ++i)
            {
                Node node = selectSingleNode(doc, xPath + "[" + n + "]" + "/properties/" + nodes[i]);
                logger.info(nodes[i] + "=" + node.getTextContent());
            }
        }
    }
}
