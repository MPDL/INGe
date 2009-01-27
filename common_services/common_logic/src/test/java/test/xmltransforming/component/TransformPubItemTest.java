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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import test.XmlComparator;
import test.xmltransforming.XmlTransformingTestBase;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.util.ObjectComparator;
import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO.Visibility;
import de.mpg.escidoc.services.common.valueobjects.FileVO.Storage;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.MdsFileVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.xmltransforming.JiBXHelper;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;

/**
 * Test of {@link XmlTransforming} methods for transforming PubItemVOs to XML and back.
 * 
 * @author Johannes M&uuml;ller (initial creation)
 * @author $Author: mfranke $ (last change)
 * @version $Revision: 645 $ $LastChangedDate: 2007-08-20 19:55:57 +0200 (Mo, 20 Aug 2007)
 * @revised by MuJ: 21.08.2007
 */
public class TransformPubItemTest extends XmlTransformingTestBase
{
    private Logger logger = Logger.getLogger(getClass());
    private static XmlTransforming xmlTransforming = new XmlTransformingBean();
    private static String TEST_FILE_ROOT = "xmltransforming/component/transformPubItemTest/";
    private static String JPG_FARBTEST_FILE = TEST_FILE_ROOT + "farbtest_wasserfarben.jpg";
    private static String RELEASED_ITEM_FILE = TEST_FILE_ROOT + "released_item_with_one_component.xml";
    private static String SAVED_ITEM_FILE = TEST_FILE_ROOT + "saved_item1.xml";
    private static String WITHDRAWN_ITEM_FILE = TEST_FILE_ROOT + "withdrawn_item1.xml";
    private static String ITEM_LIST1_FILE = TEST_FILE_ROOT + "item_list1.xml";

    /**
     * Test method for {@link de.mpg.escidoc.services.common.XmlTransforming#transformToItem(java.lang.String)}.
     * 
     * @throws Exception
     */
    @Test
    public void testTransformToItemWithOneComponent() throws Exception
    {
        logger.info("### testTransformToItemWithOneComponent ###");

        // create a new item with one component

        // create new PubItemVO containing some metadata content
        PubItemVO pubItemVO = getPubItemWithoutFiles();

        pubItemVO.getMetadata().setTitle(new TextVO("<blink>organisation</blink>"));

        // add file to PubItemVO
        FileVO fileVO = getFile();
        
        // and add it to the PubItemVO's files list
        pubItemVO.getFiles().add(fileVO);

        // transform the PubItemVO into an item
        long zeit = -System.currentTimeMillis();
        String pubItemXML = xmlTransforming.transformToItem(pubItemVO);
        zeit += System.currentTimeMillis();
        logger.info("transformToItem() (with component)->" + zeit + "ms");
        logger.info("PubItemVO with file transformed to item(XML).");
        logger.info("item(XML) =" + pubItemXML);
        // is item list[XML] valid according to item.xsd?
        assertXMLValid(pubItemXML);

        // transform the item(XML) back to a PubItemVO
        PubItemVO roundtrippedPubItemVO = xmlTransforming.transformToPubItem(pubItemXML);
        // compare with original PubItemVO
        ObjectComparator oc = new ObjectComparator(pubItemVO, roundtrippedPubItemVO);
        for (String diff : oc.getDiffs())
        {
            logger.info(diff);
        }
        //assertTrue(oc.isEqual());
        // compare FileVO before and after roundtripping
        oc = new ObjectComparator(pubItemVO.getFiles().get(0), roundtrippedPubItemVO.getFiles().get(0));
        for (String diff : oc.getDiffs())
        {
            logger.info(diff);
        }
        //assertTrue(oc.isEqual());
        logger.info("FileVO.description, vorher:" + pubItemVO.getFiles().get(0).getDescription());
        logger.info("FileVO.description, vorher (escaped):" + JiBXHelper.xmlEscape(pubItemVO.getFiles().get(0).getDescription()));
        logger.info("FileVO.description, nachher:" + roundtrippedPubItemVO.getFiles().get(0).getDescription());
        assertEquals(pubItemVO.getFiles().get(0).getDescription(), roundtrippedPubItemVO.getFiles().get(0).getDescription());
    }

    /**
     * @return
     */
    private FileVO getFile() throws Exception
    {
        FileVO fileVO = new FileVO();
        // first upload the file to the framework
        fileVO.setContent("<blink>organisation</blink>");
        // set some properties of the FileVO (mandatory fields first of all)
        fileVO.setContentCategory("post-print");
        fileVO.setName("Ein Kaufmannsund (&), ein Größer (>), ein Kleiner (<), Anführungsstriche (\") und ein Apostroph (').");
        fileVO.setDescription("This is my <blink>organisation</blink>.' + ' und meine cookies sind ' + document.cookie + '<script>alert(\'I am injected\');</script>");
        fileVO.setVisibility(Visibility.PUBLIC);
        fileVO.setStorage(Storage.INTERNAL_MANAGED);
        MdsFileVO md = new MdsFileVO();
        md.setContentCategory("post-print");
        md.setDescription("This is my <blink>organisation</blink>.' + ' und meine cookies sind ' + document.cookie + '<script>alert(\'I am injected\');</script>");
        md.getIdentifiers().add(new IdentifierVO(IdentifierVO.IdType.URI, "http://www.escidoc.de/12345"));
        md.setSize((int)ResourceUtil.getResourceAsFile(JPG_FARBTEST_FILE).length());
        //md.setTitle(new TextVO(fileVO.getName()));
        fileVO.getMetadataSets().add(md);
        return fileVO;
    }

    /**
     * Test of {@link XmlTransforming#transformToItemList(List)}
     * 
     * @throws Exception
     */
    @Test
    public void testTransformToItemList() throws Exception
    {
        logger.info("### testTransformToItemList ###");

        // create a List<PubItemVO> from the scratch.
        List<PubItemVO> pubItemList = new ArrayList<PubItemVO>();
        PubItemVO pubItem;
        for (int i = 0; i < 5; i++)
        {
            pubItem = getComplexPubItemWithoutFiles();
            pubItemList.add(pubItem);
        }

        // transform the PubItemVO into an item
        long zeit = -System.currentTimeMillis();
        String pubItemListXML = xmlTransforming.transformToItemList(pubItemList);
        zeit += System.currentTimeMillis();
        logger.info("transformToItemList()->" + zeit + "ms");
        logger.info("List<PubItemVO> transformed to item-list[XML].");
        logger.debug("item-list[XML] =\n" + pubItemListXML);

        // check the results
        assertNotNull(pubItemListXML);
        // is item list[XML] valid according to item-list.xsd?
        assertXMLValid(pubItemListXML);
        // does item list[XML] contain five item nodes?
        final String xPath = "//item-list/item";
        Document doc = getDocument(pubItemListXML, false);
        NodeList list = selectNodeList(doc, xPath);
        assertEquals("item list does not contain correct number of items", list.getLength(), 5);
    }

    /**
     * Test method for
     * {@link de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean#transformToItem(ItemVO)}; checks
     * whether the metadata part meets the requirements.
     * 
     * @throws Exception
     */
    @Test
    public void testTransformToItemAndCheckMetadata() throws Exception
    {
        logger.info("### testTransformToItemAndCheckMetadata ###");

        // create a minimal PubItemVO from scratch
        PubItemVO pubItem = getPubItemWithoutFiles();

        // transform the PubItemVO into an item
        long zeit = -System.currentTimeMillis();
        String itemXML = xmlTransforming.transformToItem(pubItem);
        zeit += System.currentTimeMillis();
        logger.info("transformToItem()->" + zeit + "ms");
        logger.info("PubItemVO transformed to item[XML].");
        logger.debug("item[XML] =\n" + itemXML);

        // check the metadata part of the XML
        final String xPath = "//item/md-records/md-record/publication";
        Document doc = getDocument(itemXML, false);
        NodeList publicationMetadataList = selectNodeList(doc, xPath);
        assertEquals("item does not contain exactly one metadata record of type 'publication'", publicationMetadataList.getLength(), 1);
        String metadataXML = toString(publicationMetadataList.item(0), false);
        logger.debug("md-record of type 'publication':\n" + metadataXML);
        // Is the metadata part valid according to the metadata schema?
        assertXMLValid(metadataXML);
    }

    /**
     * Test method for
     * {@link de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean#transformToItem(java.lang.String)}.
     * 
     * @throws Exception
     */
    @Test
    public void testTransformReleasedItemToPubItemAndCheckPID() throws Exception
    {
        logger.info("### testTransformReleasedItemToPubItemAndCheckPID ###");

        // read item[XML] from file
        String releasedPubItemXML = readFile(RELEASED_ITEM_FILE);
        logger.info("Item[XML] read from file.");
        logger.info("Content: " + releasedPubItemXML);
        // transform the item directly into a PubItemVO
        long zeit = -System.currentTimeMillis();
        ItemVO pubItemVO = xmlTransforming.transformToItem(releasedPubItemXML);
        assertNotNull(pubItemVO.getRelations());
        zeit += System.currentTimeMillis();
        logger.info("transformToPubItem()->" + zeit + "ms");
        logger.info("Transformed item to PubItemVO.");

        logger.debug("Last comment: " + pubItemVO.getVersion().getLastMessage());
        
        // check results
        assertNotNull(pubItemVO);
        assertNotNull("PID is null!", pubItemVO.getVersion().getPid());

        assertEquals(1, pubItemVO.getFiles().get(0).getMetadataSets().size());
        
        assertEquals("hdl:someHandle/test/escidoc:4747", pubItemVO.getVersion().getPid());
    }

    /**
     * Test method for
     * {@link de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean#transformToItem(java.lang.String)}.
     * 
     * @throws Exception
     */
    @Test
    public void testTransformReleasedItemToPubItemAndCheckLocalTags() throws Exception
    {
        logger.info("### testTransformReleasedItemToPubItemAndCheckLocalTags ###");

        // read item[XML] from file
        String releasedPubItemXML = readFile(RELEASED_ITEM_FILE);
        logger.info("Item[XML] read from file.");
        logger.info("Content: " + releasedPubItemXML);
        // transform the item directly into a PubItemVO
        long zeit = -System.currentTimeMillis();
        ItemVO pubItemVO = xmlTransforming.transformToItem(releasedPubItemXML);

        assertNotNull("Local tags are null", pubItemVO.getLocalTags());
        assertEquals("There should be two local tags", 2, pubItemVO.getLocalTags().size());
        assertEquals("best-of", pubItemVO.getLocalTags().get(0));
        assertEquals("very-best-of", pubItemVO.getLocalTags().get(1));
    }

    /**
     * Test method for local tags.
     * 
     * @throws Exception
     */
    @Test
    public void testTransformItemWithLocalTags() throws Exception
    {
        logger.info("### testTransformItemWithLocalTags ###");

        PubItemVO pubItem = getPubItemNamedTheFirstOfAll();
        pubItem.getLocalTags().add("Ümläut-Tág");
        pubItem.getLocalTags().add("Ûmlàut-TÄg");
        String itemXml = xmlTransforming.transformToItem(pubItem);
        
        logger.info(itemXml);
        
        assertTrue("Local tag not found", itemXml.contains("<local-tag>Ümläut-Tág</local-tag>"));
        assertTrue("Local tag not found", itemXml.contains("<local-tag>Ûmlàut-TÄg</local-tag>"));
    }

    /**
     * Test method for checking the identity of a PubItem after being transformed to an item(XML) and back.
     * 
     * @throws Exception
     */
    @Test
    public void testRountripTransformPubItem1() throws Exception
    {
        logger.info("### testRountripTransformPubItem1 ###");

        // read PubItemVO from base test class
        PubItemVO pubItem = getPubItemNamedTheFirstOfAll();

        // transform the item into XML
        String itemXml = xmlTransforming.transformToItem(pubItem);
        logger.debug("Transformed item(XML):\n" + itemXml);
        Node itemDoc = getDocument(itemXml, false);
        // check validity of item
        assertXMLValid(toString(itemDoc, false));
        logger.info("Transformed item is valid.");
        // check validity of metadata
        Node metadataXml = selectSingleNode(itemDoc, "//item/md-records/md-record/publication");
        logger.debug("Metadata:\n" + toString(metadataXml, false));
        assertXMLValid(toString(metadataXml, false));
        logger.info("Transformed item metadata is valid.");

        PubItemVO roundtrippedPubItem = xmlTransforming.transformToPubItem(itemXml);

        // compare metadata before and after roundtripping
        ObjectComparator oc = null;
        try
        {
            oc = new ObjectComparator(pubItem.getMetadata(), roundtrippedPubItem.getMetadata());
            assertEquals(0, oc.getDiffs().size());
        }
        catch (AssertionError e)
        {
            logger.error(oc);
            throw (e);
        }
    }
    /**
     * Test method for checking the identity of a PubItem after being transformed to an item(VO) and back.
     * 
     * @throws Exception
     */
    @Test
    public void testRountripTransformPubItem2() throws Exception
    {
        logger.info("### testRountripTransformPubItem2 ###");

        // read PubItemXml from test resources
        String releasedPubItemXML = readFile(RELEASED_ITEM_FILE);

        // transform the item into XML
        ItemVO itemVO = xmlTransforming.transformToItem(releasedPubItemXML);
        logger.debug("Transformed item(VO):\n" + itemVO);

        String roundtrippedPubItem = xmlTransforming.transformToItem(itemVO);

        // compare metadata before and after roundtripping
        XmlComparator oc = null;
        try
        {
            oc = new XmlComparator(releasedPubItemXML, roundtrippedPubItem);
            assertTrue(oc.getErrors().toString() + "\n\nXML1:\n" + releasedPubItemXML + "\n\nXML2:\n" + roundtrippedPubItem, oc.equal());
        }
        catch (AssertionError e)
        {
            logger.error(oc);
            throw (e);
        }
    }

    /**
     * Test method for checking the correct transformation of the creator in the metadata.
     * 
     * @throws Exception
     */
    @Test
    public void testTransformSavedItemToPubItemAndCheckCreator() throws Exception
    {
        logger.info("### testTransformStoredItemToPubItemAndCheckCreator ###");

        // read item[XML] from file
        String savedPubItemXML = readFile(SAVED_ITEM_FILE);
        logger.info("Item[XML] read from file.");
        logger.debug("Item[XML]: " + savedPubItemXML.length() + " chars, " + savedPubItemXML.getBytes("UTF-8").length + " bytes, ü = " + (savedPubItemXML.contains("ü")));
        
        // transform the item directly into a PubItemVO
        long zeit = -System.currentTimeMillis();
        PubItemVO savedItem = xmlTransforming.transformToPubItem(savedPubItemXML);
        zeit += System.currentTimeMillis();
        logger.info("transformToPubItem()->" + zeit + "ms");
        logger.info("Transformed item to PubItemVO.");

        // check results
        assertNotNull(savedItem);
        PubItemVO expectedPubItem = getPubItemNamedTheFirstOfAll();

        logger.debug("file.encoding=" + System.getProperty("file.encoding"));
        
        // compare first creator in metadata
        ObjectComparator oc = null;
        try
        {
            oc = new ObjectComparator(expectedPubItem.getMetadata().getCreators().get(0), savedItem.getMetadata().getCreators().get(0));
            assertEquals(0, oc.getDiffs().size());
        }
        catch (AssertionError e)
        {
            logger.error(oc);
            throw (e);
        }

        // compare metadata as a whole
        oc = null;
        try
        {
            String s1 = expectedPubItem.getMetadata().getSubject().getValue();
            logger.debug("s1: " + s1.length() + " chars, " + s1.getBytes("UTF-8").length + " bytes, ü = " + (s1.contains("ü")));
            String s2 = savedItem.getMetadata().getSubject().getValue();
            logger.debug("s2: " + s2.length() + " chars, " + s2.getBytes("UTF-8").length + " bytes, ü = " + (s2.contains("ü")));
            oc = new ObjectComparator(expectedPubItem.getMetadata(), savedItem.getMetadata());
            assertEquals(0, oc.getDiffs().size());
        }
        catch (AssertionError e)
        {
            logger.error(oc);
            throw (e);
        }
    }

    /**
     * Test method for checking the correct transformation of the creator in the metadata.
     * 
     * @throws Exception
     */
    @Test
    public void testTransformWithdrawnItemToPubItem() throws Exception
    {
        logger.info("### testTransformWithdrawnItemToPubItem ###");

        // read item[XML] from file
        String savedPubItemXML = readFile(WITHDRAWN_ITEM_FILE);
        logger.info("Item[XML] read from file.");

        // transform the item directly into a PubItemVO
        long zeit = -System.currentTimeMillis();
        PubItemVO savedItem = xmlTransforming.transformToPubItem(savedPubItemXML);
        zeit += System.currentTimeMillis();
        logger.info("transformToPubItem()->" + zeit + "ms");
        logger.info("Transformed item to PubItemVO.");

        // check results
        assertNotNull(savedItem);

        assertEquals("Withdraw withdraw", savedItem.getVersion().getLastMessage());
    }

    /**
     * Test method for checking the correct transformation of an item-list[XML] to a List&lt;PubItemVO&gt;.
     * 
     * @throws Exception Any exception
     */
    @Test
    public void testTransformToPubItemList() throws Exception
    {
        logger.info("### testTransformToPubItemList ###");

        // read item-list[XML] from file
        String itemListXML = readFile(ITEM_LIST1_FILE);
        logger.info("item-list[XML] read from file.");
        logger.debug("item-list[XML]:\n" + itemListXML);

        // transform to a list of PubItemVOs
        List<PubItemVO> itemList = xmlTransforming.transformToPubItemList(itemListXML);
        assertNotNull(itemList);
    }

    @Test
    public void testTransformItemVO2XmlAndValidateXml() throws Exception
    {
        logger.info("### testTransformItemVO2XmlAndValidateXml ###");
        
        ItemVO itemVO = new PubItemVO();
        MdsPublicationVO mdsPublicationVO = new MdsPublicationVO();
        itemVO.getMetadataSets().add(mdsPublicationVO);
        
        String itemXml = xmlTransforming.transformToItem(itemVO);
        
        logger.info("XML: " + itemXml);
        
        assertXMLValid(itemXml);
    }
    
}
