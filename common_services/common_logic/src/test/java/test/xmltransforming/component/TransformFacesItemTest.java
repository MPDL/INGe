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

package test.xmltransforming.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.junit.Test;

import test.XmlComparator;
import test.xmltransforming.XmlTransformingTestBase;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.MdsJHoveVO;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;

/**
 * Test of {@link XmlTransforming} methods for transforming PubItemVOs to XML and back.
 * 
 * @author Johannes M&uuml;ller (initial creation)
 * @author $Author: mfranke $ (last change)
 * @version $Revision: 645 $ $LastChangedDate: 2007-08-20 19:55:57 +0200 (Mo, 20 Aug 2007)
 * @revised by MuJ: 21.08.2007
 */
public class TransformFacesItemTest extends XmlTransformingTestBase
{
    private Logger logger = Logger.getLogger(getClass());
    private static XmlTransforming xmlTransforming = new XmlTransformingBean();
    private static String TEST_FILE_ROOT = "xmltransforming/component/transformFacesItemTest/";
    private static String SAVED_ITEM_FILE = TEST_FILE_ROOT + "item.xml";

    /**
     * Test method for {@link de.mpg.escidoc.services.common.XmlTransforming#transformToItem(java.lang.String)}.
     * 
     * @throws Exception
     */
    @Test
    public void testTransformToItemWithComponents() throws Exception
    {
        logger.info("### testTransformToItemWithComponents ###");

        // read item[XML] from file
        String releasedFacesItemXML = readFile(SAVED_ITEM_FILE);
        logger.info("Item[XML] read from file.");
        logger.info("Content: " + releasedFacesItemXML);
        // transform the item directly into a PubItemVO
        long zeit = -System.currentTimeMillis();
        ItemVO facesItemVO = xmlTransforming.transformToItem(releasedFacesItemXML);
        zeit += System.currentTimeMillis();
        logger.info("transformToPubItem()->" + zeit + "ms");
        logger.info("Transformed item to PubItemVO.");

        logger.debug("Last comment: " + facesItemVO.getVersion().getLastMessage());
        
        // check results
        assertNotNull(facesItemVO);
        assertNotNull("PID is null!", facesItemVO.getVersion().getPid());

        assertEquals(2, facesItemVO.getFiles().get(0).getMetadataSets().size());
        
        assertEquals("JHove metadata set is not of the right type", MdsJHoveVO.class, facesItemVO.getFiles().get(0).getMetadataSets().get(1).getClass());

        MdsJHoveVO mdsJHoveVO = (MdsJHoveVO) facesItemVO.getFiles().get(0).getMetadataSets().get(1);
        
        Element element = mdsJHoveVO.getRepInfo();
        
        assertNotNull("JHove metadata is empty", element.asXML());
        
    }
    
    /**
     * Test method for checking the identity of a Faces Item after being transformed to an item(VO) and back.
     * 
     * @throws Exception
     */
    @Test
    public void testRountripTransformFacesItem() throws Exception
    {
        logger.info("### testRountripTransformFacesItem ###");

        // read PubItemXml from test resources
        String releasedFacesItemXML = readFile(SAVED_ITEM_FILE);

        // transform the item into XML
        ItemVO itemVO = xmlTransforming.transformToItem(releasedFacesItemXML);
        logger.debug("Transformed item(VO):\n" + itemVO);

        assertNotNull("ObjId lost.", itemVO.getVersion().getObjectId());
        
        String roundtrippedFacesItem = xmlTransforming.transformToItem(itemVO);

        // compare metadata before and after roundtripping
        XmlComparator oc = null;
        try
        {
            oc = new XmlComparator(releasedFacesItemXML, roundtrippedFacesItem);
            assertTrue(oc.getErrors().toString() + "\n\nXML1:\n" + releasedFacesItemXML + "\n\nXML2:\n" + roundtrippedFacesItem, oc.equal());
        }
        catch (AssertionError e)
        {
            logger.error(oc);
            throw (e);
        }
    }

}
