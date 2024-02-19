/*
 *
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with
 * the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or
 * http://www.escidoc.org/license. See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License
 * file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with
 * the fields enclosed by brackets "[]" replaced with your own identifying information: Portions
 * Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2010 Fachinformationszentrum Karlsruhe Gesellschaft fÃ¼r
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur FÃ¶rderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */

package de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.mpg.mpdl.inge.model.valueobjects.FileVO;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO.IdType;
import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsFileVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.model.xmltransforming.util.ObjectComparator;
import de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.JiBXHelper;
import de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.XmlTransformingTestBase;
import de.mpg.mpdl.inge.util.DOMUtilities;
import de.mpg.mpdl.inge.util.PropertyReader;
import de.mpg.mpdl.inge.util.ResourceUtil;
import de.mpg.mpdl.inge.util.XmlComparator;

/**
 * Test of {@link XmlTransforming} methods for transforming PubItemVOs to XML and back.
 *
 * @author Johannes M&uuml;ller (initial creation)
 * @author $Author$ (last change)
 * @version $Revision$ $LastChangedDate: 2007-08-20 19:55:57 +0200 (Mo, 20 Aug 2007)
 * @revised by MuJ: 21.08.2007
 */
public class TransformPubItemTest extends XmlTransformingTestBase {
  private static final Logger logger = LogManager.getLogger(TransformPubItemTest.class);

  private static String TEST_FILE_ROOT = "xmltransforming/component/transformPubItemTest/";
  private static String JPG_FARBTEST_FILE = TEST_FILE_ROOT + "farbtest_wasserfarben.jpg";
  private static String RELEASED_ITEM_FILE = TEST_FILE_ROOT + "released_item_with_one_component.xml";
  private static final String REST_ITEM_FILE = TEST_FILE_ROOT + "rest_item.xml";
  private static String SAVED_ITEM_FILE = TEST_FILE_ROOT + "saved_item1.xml";
  private static String WITHDRAWN_ITEM_FILE = TEST_FILE_ROOT + "withdrawn_item1.xml";
  private static String ITEM_LIST1_FILE = TEST_FILE_ROOT + "item_list1.xml";

  /**
   * Test method for
   * {@link de.mpg.mpdl.inge.model.xmltransforming.XmlTransforming#transformToItem(java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testTransformToItemWithOneComponent() throws Exception {
    logger.info("### testTransformToItemWithOneComponent ###");

    // create a new item with one component

    // create new PubItemVO containing some metadata content
    PubItemVO pubItemVO = getPubItemWithoutFiles();

    IdentifierVO ident = new IdentifierVO(IdType.CONE, "123");
    pubItemVO.getMetadata().getCreators().get(0).getPerson().setIdentifier(ident);

    pubItemVO.getMetadata().setTitle("<blink>organisation</blink>");

    // add file to PubItemVO
    FileVO fileVO = getFile();

    // and add it to the PubItemVO's files list
    pubItemVO.getFiles().add(fileVO);

    // transform the PubItemVO into an item
    long zeit = -System.currentTimeMillis();
    String pubItemXML = XmlTransformingService.transformToItem(pubItemVO);
    zeit += System.currentTimeMillis();
    logger.info("transformToItem() (with component)->" + zeit + "ms");
    logger.info("PubItemVO with file transformed to item(XML).");
    logger.info("item(XML) =" + pubItemXML);
    // is item list[XML] valid according to item.xsd?
    assertXMLValid(pubItemXML);

    // transform the item(XML) back to a PubItemVO
    PubItemVO roundtrippedPubItemVO = XmlTransformingService.transformToPubItem(pubItemXML);

    assertEquals(IdType.CONE, roundtrippedPubItemVO.getMetadata().getCreators().get(0).getPerson().getIdentifier().getType());

    // compare with original PubItemVO
    ObjectComparator oc = new ObjectComparator(pubItemVO, roundtrippedPubItemVO);
    for (String diff : oc.getDiffs()) {
      logger.info(diff);
    }
    // assertTrue(oc.isEqual());
    // compare FileVO before and after roundtripping
    oc = new ObjectComparator(pubItemVO.getFiles().get(0), roundtrippedPubItemVO.getFiles().get(0));
    for (String diff : oc.getDiffs()) {
      logger.info(diff);
    }
    // assertTrue(oc.isEqual());
    logger.info("FileVO.description, vorher:" + pubItemVO.getFiles().get(0).getDescription());
    logger.info("FileVO.description, vorher (escaped):" + JiBXHelper.xmlEscape(pubItemVO.getFiles().get(0).getDescription()));
    logger.info("FileVO.description, nachher:" + roundtrippedPubItemVO.getFiles().get(0).getDescription());
    assertEquals(pubItemVO.getFiles().get(0).getDescription(), roundtrippedPubItemVO.getFiles().get(0).getDescription());
  }

  /**
   * @return
   */
  private FileVO getFile() throws Exception {
    FileVO fileVO = new FileVO();
    // first upload the file to the framework
    fileVO.setContent("<blink>organisation</blink>");
    // set some properties of the FileVO (mandatory fields first of all)
    fileVO.setContentCategory("post-print");
    fileVO.setName("Ein Kaufmannsund (&), ein GrÃ¶ÃŸer (>), ein Kleiner (<), AnfÃ¼hrungsstriche (\") und ein Apostroph (').");
    fileVO.setDescription(
        "This is my <blink>organisation</blink>.' + ' und meine cookies sind ' + document.cookie + '<script>alert(\'I am injected\');</script>");
    fileVO.setVisibility(FileVO.Visibility.PUBLIC);
    fileVO.setStorage(FileVO.Storage.INTERNAL_MANAGED);
    MdsFileVO md = new MdsFileVO();
    md.setContentCategory("post-print");
    md.setDescription(
        "This is my <blink>organisation</blink>.' + ' und meine cookies sind ' + document.cookie + '<script>alert(\'I am injected\');</script>");
    md.getIdentifiers().add(new IdentifierVO(IdentifierVO.IdType.URI, "http://www.escidoc.de/12345"));
    md.setSize((int) ResourceUtil.getResourceAsFile(JPG_FARBTEST_FILE, TransformPubItemTest.class.getClassLoader()).length());
    // md.setTitle(new TextVO(fileVO.getName()));
    fileVO.getMetadataSets().add(md);
    return fileVO;
  }

  /**
   * Test of {@link XmlTransforming#transformToItemList(List)}
   *
   * @throws Exception
   */
  @Test
  public void testTransformToItemList() throws Exception {
    logger.info("### testTransformToItemList ###");

    // create a List<PubItemVO> from the scratch.
    List<PubItemVO> pubItemList = new ArrayList<PubItemVO>();
    PubItemVO pubItem;
    for (int i = 0; i < 5; i++) {
      pubItem = getComplexPubItemWithoutFiles();
      pubItemList.add(pubItem);
    }

    // transform the PubItemVO into an item
    long zeit = -System.currentTimeMillis();
    String pubItemListXML = XmlTransformingService.transformToItemList(pubItemList);
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
    Document doc = DOMUtilities.createDocument(pubItemListXML, false);
    NodeList list = DOMUtilities.selectNodeList(doc, xPath);
    assertEquals("item list does not contain correct number of items", list.getLength(), 5);
  }

  /**
   * Test method for
   * {@link de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService#transformToItem(ItemVO)} ;
   * checks whether the metadata part meets the requirements.
   *
   * @throws Exception
   */
  @Test
  public void testTransformToItemAndCheckMetadata() throws Exception {
    logger.info("### testTransformToItemAndCheckMetadata ###");

    // create a minimal PubItemVO from scratch
    PubItemVO pubItem = getPubItemWithoutFiles();

    // transform the PubItemVO into an item
    long zeit = -System.currentTimeMillis();
    String itemXML = XmlTransformingService.transformToItem(pubItem);
    zeit += System.currentTimeMillis();
    logger.info("transformToItem()->" + zeit + "ms");
    logger.info("PubItemVO transformed to item[XML].");
    logger.debug("item[XML] =\n" + itemXML);

    // check the metadata part of the XML
    final String xPath = "//item/md-records/md-record/publication";
    Document doc = DOMUtilities.createDocument(itemXML, false);
    NodeList publicationMetadataList = DOMUtilities.selectNodeList(doc, xPath);
    assertEquals("item does not contain exactly one metadata record of type 'publication'", publicationMetadataList.getLength(), 1);
    String metadataXML = toString(publicationMetadataList.item(0), false);
    logger.debug("md-record of type 'publication':\n" + metadataXML);
    // Is the metadata part valid according to the metadata schema?
    assertXMLValid(metadataXML);
  }

  /**
   * Test method for
   * {@link de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService#transformToItem(java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testTransformReleasedItemToPubItemAndCheckPID() throws Exception {
    logger.info("### testTransformReleasedItemToPubItemAndCheckPID ###");

    // read item[XML] from file
    String releasedPubItemXML = readFile(RELEASED_ITEM_FILE);
    logger.info("Item[XML] read from file.");
    logger.info("Content: " + releasedPubItemXML);
    // transform the item directly into a PubItemVO
    long zeit = -System.currentTimeMillis();
    ItemVO pubItemVO = XmlTransformingService.transformToItem(releasedPubItemXML);
    assertNotNull(pubItemVO.getRelations());
    zeit += System.currentTimeMillis();
    logger.info("transformToPubItem()->" + zeit + "ms");
    logger.info("Transformed item to PubItemVO.");

    logger.debug("Last comment: " + pubItemVO.getVersion().getLastMessage());

    // check results
    assertNotNull(pubItemVO);
    assertNotNull("PID is null!", pubItemVO.getVersion().getPid());

    assertEquals(1, pubItemVO.getFiles().get(0).getMetadataSets().size());

    assertEquals("hdl:someHandle/test/escidoc_4747", pubItemVO.getVersion().getPid());
  }

  /**
   * Test method for
   * {@link de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService#transformToItem(java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testTransformReleasedItemToPubItemAndCheckLocalTags() throws Exception {
    logger.info("### testTransformReleasedItemToPubItemAndCheckLocalTags ###");

    // read item[XML] from file
    String releasedPubItemXML = readFile(RELEASED_ITEM_FILE);
    logger.info("Item[XML] read from file.");
    logger.info("Content: " + releasedPubItemXML);
    // transform the item directly into a PubItemVO

    ItemVO pubItemVO = XmlTransformingService.transformToItem(releasedPubItemXML);

    assertNotNull("Local tags are null", pubItemVO.getLocalTags());
    assertEquals("There should be two local tags", 2, pubItemVO.getLocalTags().size());
    assertEquals("best-of", pubItemVO.getLocalTags().get(0));
    assertEquals("very-best-of", pubItemVO.getLocalTags().get(1));

    assertEquals("4fe9cebf84b66a9ebb07729f24f9f8cc", pubItemVO.getFiles().get(0).getChecksum());
    assertEquals("CC-LICENSE", pubItemVO.getFiles().get(0).getDefaultMetadata().getLicense());
  }

  /**
   * Test method for
   * {@link de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService#transformToItem(java.lang.String)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testTransformRestItemToPubItem() throws Exception {
    logger.info("### testTransformRestItemToPubItem ###");

    // read item[XML] from file
    String restPubItemXML = readFile(REST_ITEM_FILE);
    logger.info("Item[XML] read from file.");
    logger.info("Content: " + restPubItemXML);
    // transform the item directly into a PubItemVO
    ItemVO pubItemVO = XmlTransformingService.transformToItem(restPubItemXML);

    assertEquals("ObjectId not transformed correctly", "escidoc:149937", pubItemVO.getVersion().getObjectId());
    assertEquals("ObjectId and Version not transformed correctly", "escidoc:149937_3", pubItemVO.getVersion().getObjectIdAndVersion());
    assertEquals("Content Model not transformed correctly", "escidoc:persistent4", pubItemVO.getContentModel());
    assertEquals("Context not transformed correctly", "/ir/context/escidoc:147965", pubItemVO.getContext().getObjectId());
    assertEquals("Version number not transformed correctly", 3, pubItemVO.getLatestVersion().getVersionNumber());
    assertEquals("Owner not transformed correctly", "/aa/user-account/escidoc:146934", pubItemVO.getOwner().getObjectId());
    assertEquals("Latest release not transformed correctly", "escidoc:149937", pubItemVO.getLatestRelease().getObjectId());
  }

  /**
   * Test method for local tags.
   *
   * @throws Exception
   */
  @Test
  public void testTransformItemWithLocalTags() throws Exception {
    logger.info("### testTransformItemWithLocalTags ###");

    PubItemVO pubItem = getPubItemNamedTheFirstOfAll();
    pubItem.getLocalTags().add("ÃœmlÃ¤ut-TÃ¡g");
    pubItem.getLocalTags().add("Ã›mlÃ ut-TÃ„g");
    String itemXml = XmlTransformingService.transformToItem(pubItem);

    logger.info(itemXml);

    assertTrue("Local tag not found", itemXml.contains("<local-tag>ÃœmlÃ¤ut-TÃ¡g</local-tag>"));
    assertTrue("Local tag not found", itemXml.contains("<local-tag>Ã›mlÃ ut-TÃ„g</local-tag>"));
  }

  /**
   * Test method for checking the identity of a PubItem after being transformed to an item(XML) and
   * back.
   *
   * @throws Exception
   */
  @Test
  public void testRountripTransformPubItem1() throws Exception {
    logger.info("### testRountripTransformPubItem1 ###");

    // read PubItemVO from base test class
    PubItemVO pubItem = getPubItemNamedTheFirstOfAll();

    // transform the item into XML
    String itemXml = XmlTransformingService.transformToItem(pubItem);
    logger.info("Transformed item(XML):\n" + itemXml);
    Node itemDoc = DOMUtilities.createDocument(new String(itemXml.getBytes(), "UTF-8"), false);
    // check validity of item
    assertXMLValid(toString(itemDoc, false));
    logger.info("Transformed item is valid.");
    // check validity of metadata
    Node metadataXml = DOMUtilities.selectSingleNode(itemDoc, "//item/md-records/md-record/publication");
    logger.debug("Metadata:\n" + toString(metadataXml, false));
    assertXMLValid(toString(metadataXml, false));
    logger.info("Transformed item metadata is valid.");

    PubItemVO roundtrippedPubItem = XmlTransformingService.transformToPubItem(itemXml);

    // compare metadata before and after roundtripping
    ObjectComparator oc = null;
    try {
      oc = new ObjectComparator(pubItem.getMetadata(), roundtrippedPubItem.getMetadata());
      assertEquals(0, oc.getDiffs().size());
    } catch (AssertionError e) {
      logger.error(oc);
      throw (e);
    }
  }

  /**
   * Test method for checking the identity of a PubItem after being transformed to an item(VO) and
   * back.
   *
   * @throws Exception
   */
  @Test
  public void testRountripTransformPubItem2() throws Exception {
    logger.info("### testRountripTransformPubItem2 ###");

    // read PubItemXml from test resources
    String releasedPubItemXML = readFile(RELEASED_ITEM_FILE);

    // transform the item into XML
    ItemVO itemVO = XmlTransformingService.transformToItem(releasedPubItemXML);
    logger.debug("Transformed item(VO):\n" + itemVO);

    assertNotNull("ObjId lost.", itemVO.getVersion().getObjectId());

    String roundtrippedPubItem = XmlTransformingService.transformToItem(itemVO);

    // compare metadata before and after roundtripping
    XmlComparator oc = null;
    try {
      oc = new XmlComparator(releasedPubItemXML, roundtrippedPubItem);
      assertTrue(oc.getErrors().toString() + "\n\nXML1:\n" + releasedPubItemXML + "\n\nXML2:\n" + roundtrippedPubItem, oc.equal());
    } catch (AssertionError e) {
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
  public void testTransformSavedItemToPubItemAndCheckCreator() throws Exception {
    logger.info("### testTransformStoredItemToPubItemAndCheckCreator ###");

    // read item[XML] from file
    String savedPubItemXML = readFile(SAVED_ITEM_FILE);
    logger.info("Item[XML] read from file.");
    logger.debug("Item[XML]: " + savedPubItemXML.length() + " chars, " + savedPubItemXML.getBytes("UTF-8").length + " bytes, Ã¼ = "
        + (savedPubItemXML.contains("Ã¼")));

    // transform the item directly into a PubItemVO
    long zeit = -System.currentTimeMillis();
    PubItemVO savedItem = XmlTransformingService.transformToPubItem(savedPubItemXML);
    zeit += System.currentTimeMillis();
    logger.info("transformToPubItem()->" + zeit + "ms");
    logger.info("Transformed item to PubItemVO.");

    // check results
    assertNotNull(savedItem);
    PubItemVO expectedPubItem = getPubItemNamedTheFirstOfAll();

    logger.debug("file.encoding=" + System.getProperty(PropertyReader.FILE_ENCODING));

    // compare first creator in metadata
    ObjectComparator oc = null;
    try {
      oc = new ObjectComparator(expectedPubItem.getMetadata().getCreators().get(0), savedItem.getMetadata().getCreators().get(0));
      assertEquals(0, oc.getDiffs().size());
    } catch (AssertionError e) {
      logger.error(oc);
      throw (e);
    }

    // compare metadata as a whole
    oc = null;
    try {
      String s1 = expectedPubItem.getMetadata().getFreeKeywords();
      logger.debug("s1: " + s1.length() + " chars, " + s1.getBytes("UTF-8").length + " bytes, \u00FC = " + (s1.contains("\u00FC")));
      String s2 = savedItem.getMetadata().getFreeKeywords();
      logger.debug("s2: " + s2.length() + " chars, " + s2.getBytes("UTF-8").length + " bytes, \u00FC = " + (s2.contains("\u00FC")));
      oc = new ObjectComparator(expectedPubItem.getMetadata(), savedItem.getMetadata());

      assertTrue("Metadata are not equal" + oc, oc.isEqual());
    } catch (AssertionError e) {
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
  public void testTransformWithdrawnItemToPubItem() throws Exception {
    logger.info("### testTransformWithdrawnItemToPubItem ###");

    // read item[XML] from file
    String savedPubItemXML = readFile(WITHDRAWN_ITEM_FILE);
    logger.info("Item[XML] read from file.");

    // transform the item directly into a PubItemVO
    long zeit = -System.currentTimeMillis();
    PubItemVO savedItem = XmlTransformingService.transformToPubItem(savedPubItemXML);
    zeit += System.currentTimeMillis();
    logger.info("transformToPubItem()->" + zeit + "ms");
    logger.info("Transformed item to PubItemVO.");

    // check results
    assertNotNull(savedItem);

    assertEquals("Withdraw withdraw", savedItem.getVersion().getLastMessage());
  }

  /**
   * Test method for checking the correct transformation of an item-list[XML] to a
   * List&lt;PubItemVO&gt;.
   *
   * @throws Exception Any exception
   */
  @Test
  public void testTransformToPubItemList() throws Exception {
    logger.info("### testTransformToPubItemList ###");

    // read item-list[XML] from file
    String itemListXML = readFile(ITEM_LIST1_FILE);
    logger.info("item-list[XML] read from file.");
    logger.debug("item-list[XML]:\n" + itemListXML);

    // transform to a list of PubItemVOs
    List<PubItemVO> itemList = XmlTransformingService.transformToPubItemList(itemListXML);
    assertNotNull(itemList);
  }

  @Test
  public void testTransformItemVO2XmlAndValidateXml() throws Exception {
    logger.info("### testTransformItemVO2XmlAndValidateXml ###");

    ItemVO itemVO = new PubItemVO();
    MdsPublicationVO mdsPublicationVO = new MdsPublicationVO();
    itemVO.getMetadataSets().add(mdsPublicationVO);

    String itemXml = XmlTransformingService.transformToItem(itemVO);

    logger.info("XML: " + itemXml);

    assertXMLValid(itemXml);
  }

  @Test
  public void testCleanupItem() throws Exception {
    // read PubItemVO from base test class
    PubItemVO pubItem = getPubItemNamedTheFirstOfAll();

    // transform the item into XML
    String itemXml = XmlTransformingService.transformToItem(pubItem);
    logger.info("Transformed item(XML):\n" + itemXml);

    pubItem.getMetadata().getCreators().get(0).getPerson().getOrganizations().get(0).setName("");
    pubItem.getMetadata().getCreators().get(0).getPerson().getOrganizations().get(0).setAddress("");
    pubItem.getMetadata().cleanup();

    String itemXmlCleanedUp = XmlTransformingService.transformToItem(pubItem);
    logger.info("-----------------------------------------------------");
    logger.info("Cleaned up item(XML):\n" + itemXmlCleanedUp);

  }
}
