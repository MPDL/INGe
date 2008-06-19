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
package test.framework;

import java.io.File;
import java.io.PrintWriter;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Testcases to create script files for purging objects from the data store.
 *
 * @author Peter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * @revised by BrP: 04.09.2007
 */
public class DeleteObjectsTest extends TestBase
{
    private Logger logger = Logger.getLogger(getClass());

    /* (non-Javadoc)
     * @see test.framework.TestBase#setUp()
     */
    @Before
    public void setUp() throws Exception
    {
        userHandle = loginSystemAdministrator();
    }

    /**
     * Create a script to purge the items from the data store.
     */
    @Test
    public void deleteItems() throws Exception
    {
        String filter = "<param></param>";
        final String xPath = "//item-list/item";
        String items = ServiceLocator.getItemHandler(userHandle).retrieveItems(filter);
        logger.debug("items=" + items);
        Document doc = getDocument(items, false);
        NodeList list = selectNodeList(doc, xPath);
        logger.info(list.getLength() + " Items:");
        File file = new File("purge-items.bat");
        PrintWriter writer = new PrintWriter(file);
        for (int n=1; n<=list.getLength(); ++n)
        {
            String id = getAttributeValue(list.item(n-1), xPath + "[" + n + "]", "objid");
            writer.println("call fedora-purge.bat localhost:8082 fedoraAdmin fedoraAdmin " + id + " https \"item " + id + " purged\"");
            writer.flush();
        }
        writer.close();
    }
}
