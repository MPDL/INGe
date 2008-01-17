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

package test.common.xmltransforming.component;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test.common.xmltransforming.XmlTransformingTestBase;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.PubItemVersionVO;

/**
 * Test of {@link PubManTransforming} methods for transforming and integration with common_logic and the framework.
 * 
 * @author Johannes M&uuml;ller (initial creation)
 * @author $Author: jmueller $ (last change)
 * @version $Revision: 553 $ $LastChangedDate: 2007-10-23 19:09:52 +0200 (Di, 23 Okt 2007) $
 * @revised by MuJ: 20.09.2007
 */
public class TransformPubItemVersionListTest extends XmlTransformingTestBase
{
    /**
     * Logger for this class.
     */
    private Logger logger = Logger.getLogger(getClass());

    private static final String TEST_FILE_ROOT = "src/test/resources/xmltransforming/component/transformPubItemVersionListTest/";
    private static final String VERSION_LIST_SAMPLE_FILE = TEST_FILE_ROOT + "version-list-sample.xml";
    
    private static XmlTransforming xmlTransforming;
    private String userHandle;

    /**
     * Get an {@link XmlTransforming} instance once.
     * 
     * @throws Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        xmlTransforming = (XmlTransforming) getService(XmlTransforming.SERVICE_NAME);        
    }

    /**
     * Logs in as depositor and retrieves his grants (before every single test method).
     * 
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception
    {
        // get user handle for user "test_dep_scientist"
        userHandle = loginScientist();

    }

    /**
     * Logs out (after every single test method).
     * 
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception
    {
        logout(userHandle);
    }

    /**
     * @throws Exception
     */
    @Test
    public void testTransformPubItemVersionList() throws Exception
    {
        // create version list
        String itemVersionHistoryXml = readFile(VERSION_LIST_SAMPLE_FILE);
        assertNotNull(itemVersionHistoryXml);

        logger.info(itemVersionHistoryXml);

        // transform the version history XML to a list of PubItemVersionVOs
        long zeit = -System.currentTimeMillis();
        List<PubItemVersionVO> versionList = xmlTransforming.transformToPubItemVersionVOList(itemVersionHistoryXml);
        zeit += System.currentTimeMillis();
        logger.info("transformPubItemVersionList() -> " + zeit + "ms");

        assertNotNull(versionList);
    }
}
