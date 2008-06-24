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
package test.framework.aa;

import static org.junit.Assert.assertNotNull;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import test.framework.TestBase;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.services.framework.PropertyReader;;

/**
 * Shows all stored users.
 *
 * @author Peter (initial creation)
 * @author $Author: pbroszei $ (last modification)
 * @version $Revision: 284 $ $LastChangedDate: 2007-10-09 10:22:45 +0200 (Di, 09 Okt 2007) $
 * @revised by BrP: 04.09.2007
 */
public class CleanupUserData extends TestBase
{
    private Logger logger = Logger.getLogger(getClass());

    private void deleteUser(String user) throws Exception
    {
        final String xPath = "//user-account[1]";
        final String attributes[] = {"objid"};
        final String nodes[] = {"login-name"};//,"name","email","active"};
        Document doc = getDocument(user, false);        
        logger.info(LINE);
        for (int i=0; i<attributes.length; ++i)
        {
            logger.info(attributes[i] + "=" + getAttributeValue(doc, xPath, attributes[i]));
        }
        for (int i=0; i<nodes.length; ++i)
        {
            Node node = selectSingleNode(doc, xPath + "/properties/" + nodes[i]);
            logger.info(nodes[i] + "=" + (node == null ? "" : node.getTextContent()));
        }
    }
    
//    private void showGrants(String grants) throws Exception
//    {
//        Document grantsDoc = getDocument(grants, false);
//        NodeList nodelist = XPathAPI.selectNodeList(grantsDoc, "//current-grants/grant");
//        for (int i=0; i<nodelist.getLength(); ++i)
//        {
//            String xpath = "//grant[" + i + "]/properties/";
//            String roleId = getAttributeValue(nodelist.item(i), "properties/role", "objid");
//            logger.info("Role[" + i + "]: " + roleId);
//            try
//            {
//                String objectId = getAttributeValue(grantsDoc, "properties/object", "objid");
//                logger.info("Object[" + i + "]: " + objectId);
//            }
//            catch(Exception e)
//            {
//                logger.debug(e.getLocalizedMessage());
//            }
//        }
//    }

    /* (non-Javadoc)
     * @see test.framework.TestBase#setUp()
     */
    public void setUp() throws Exception
    {
        userHandle = loginSystemAdministrator();
    }

    /**
     * Shows all users from the given user list.
     */
    @Test
    public void showUsers() throws Exception
    {
        String loginnames[] = { PropertyReader.getProperty(PROPERTY_USERNAME_ADMIN)
                              , PropertyReader.getProperty(PROPERTY_USERNAME_LIBRARIAN)
                              , PropertyReader.getProperty(PROPERTY_USERNAME_SCIENTIST)
                              , PropertyReader.getProperty(PROPERTY_USERNAME_AUTHOR)
                              };
        for (int i=0; i<loginnames.length; ++i)
        {
            String user = ServiceLocator.getUserAccountHandler(userHandle).retrieve(loginnames[i]);
            logger.debug("user=" + user);
            assertNotNull(user);
            deleteUser(user);
//            String id = getAttributeValue(getDocument(user, false), "//user-account[1]", "objid");             
//            assertNotNull(id);
//            String grants = ServiceLocator.getUserAccountHandler(userHandle).retrieveCurrentGrants(id);
//            logger.debug("grants=" + grants);
//            assertNotNull(grants);
//            showGrants(grants);
        }
    }
}
