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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import test.framework.TestBase;

/**
 * Tests the namespace awareness of the Xerces XPath implementation.
 * 
 * @author Johannes Mueller (initial creation)
 * @author $Author: pbroszei $ (last modification)
 * @version $Revision: 314 $ $LastChangedDate: 2007-11-07 13:12:14 +0100 (Wed, 07 Nov 2007) $
 * @revised by BrP: 04.09.2007
 */  
public class XPathTest extends TestBase
{
    /**
     * Logger for this class.
     */
    private static final Logger logger = Logger.getLogger(XPathTest.class);

    /**
     *  Tests the correct behaviour of {@link test.framework.TestBase#getAttributeValue(Node, String, String)}.
     */
    @Test
    public void testGetAttributeValue() throws Exception
    {
        logger.debug("\n### testGetAttributeValue ###");
        String grantsXml = readFile("src/test/resources/test/framework/um/grants.xml");
        Document grantsDoc = getDocument(grantsXml, false);
        String xPath = "//grant[1]/object-ref";
        Node n = selectSingleNode(grantsDoc, xPath);
        StringBuffer sb = new StringBuffer();
        if (n.hasAttributes())
        {
            logger.info("-> This node has attributes.");
            int i = 0;
            Node an = n.getAttributes().item(i);

            while (an != null)
            {
                sb.append(an.toString() + "\n");
                i++;
                an = n.getAttributes().item(i);
            }
        }
        if (sb != null)
        {
            logger.info(sb.toString());
        }
        String s = getAttributeValue(grantsDoc, xPath, "xlink:href");
        assertEquals("/ir/context/escidoc:persistent3", s);
    }
    
    /**
     * Tests the correct behaviour of {@link test.framework.TestBase#getAttributeValue(Node, String, String)}.
     */
    @Test
    public void testAccountUserXMLVorher() throws Exception
    {
        logger.debug("\n### testAccountUserXMLVorher ###");
        String grantsXml = readFile("src/test/resources/testframework/um/xpathtest/user-account-vorher.xml");
        Document grantsDoc = getDocument(grantsXml, false);
        String id = getAttributeValue(grantsDoc, "//user-account", "objid");
        logger.info("Vorher: id="+id);
        assertNotNull(id);
    }
    
    /**
     * Tests the correct behaviour of {@link test.framework.TestBase#getAttributeValue(Node, String, String)}.
     */
    @Test
    public void testAccountUserXMLNachher() throws Exception
    {
        logger.debug("\n### testAccountUserXMLNachher ###");
        String grantsXml = readFile("src/test/resources/testframework/um/xpathtest/user-account-nachher.xml");
        Document grantsDoc = getDocument(grantsXml, false);
        String id = getAttributeValue(grantsDoc, "//user-account[1]", "objid");
        logger.info("Vorher: id="+id);
        assertNotNull(id);
    }

    /**
     * Tests the correct behaviour of the XPath methods in {@link test.framework.TestBase} working in  namespace aware mode.
     */
    @Test
    public void testNamespaceAwareness() throws Exception
    {
        logger.debug("\n### testNamespaceAwareness ###");
        String grantsXml = readFile("src/test/resources/testframework/um/grants_with_double_hrefs.xml");
        Document grantsDoc = getDocument(grantsXml, true);
        String xPath = "//*[local-name() = 'current-grants']/*[local-name() = 'grant'][1]/*[local-name() = 'object-ref']";
        NodeList nl = selectNodeList(grantsDoc, xPath);
        // this should be one distict node
        assertTrue(nl.getLength() == 1);
        Node node = nl.item(0);
        NamedNodeMap nnl = node.getAttributes();
        Node attrNode = nnl.getNamedItemNS("http://www.dummy.org/4711/dummy", "href");
        logger.info("attrNode: " + attrNode.getTextContent());
    }

    /**
     * Tests the behaviour of {@link org.apache.xpath.XPathAPI#selectSingleNode(Node, String)}.
     */
    @Test
    public void testGetNodeText() throws Exception
    {
        logger.debug("\n### testGetNodeText ###");
        String grantsXml = readFile("src/test/resources/testframework/um/user-account.xml");
        Document grantsDoc = getDocument(grantsXml, false);
        String xPath = "//user-account/name";
        Node n = selectSingleNode(grantsDoc, xPath);
        String s = n.getTextContent();
        logger.info(s);
        assertEquals("Test Depositor Scientist", s);
    }
}
