/*
 * roject* CDDL HEADER START
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
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */

package de.mpg.mpdl.inge.structuredexportmanager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.util.ResourceUtil;

public class StructuredExportServiceTest {
  private static final Logger logger = Logger.getLogger(StructuredExportServiceTest.class);

  private static HashMap<String, String> itemLists;

  @SuppressWarnings("serial")
  public static final Map<String, String> ITEM_LISTS_FILE_MAMES = new HashMap<String, String>() {
    String pref = "target/test-classes/";
    {
      put("MARCXML", pref + "publicationItems/metadataV2/item_book.xml");
      put("BIBTEX", pref + "publicationItems/metadataV2/item_book.xml");
      put("ENDNOTE", pref + "publicationItems/metadataV2/item_book.xml");
      put("EDOC_EXPORT", pref + "publicationItems/metadataV2/full_item.xml");
      put("EDOC_IMPORT", pref + "publicationItems/metadataV2/full_item.xml");
      put("CSV", pref + "facesItems/item-list.xml");
    }
  };

  /**
   * Get test item list from XML
   * 
   * @throws Exception
   */

  public static final void getItemLists() throws Exception {
    itemLists = new HashMap<String, String>();

    for (String key : ITEM_LISTS_FILE_MAMES.keySet()) {
      String itemList =
          ResourceUtil.getResourceAsString(ITEM_LISTS_FILE_MAMES.get(key),
              StructuredExportServiceTest.class.getClassLoader());
      assertNotNull("Item list xml is not found", itemList);
      itemLists.put(key, itemList);
    }
  }

  /**
   * Test explainExport XML file
   * 
   * @throws Exception Any exception.
   */
  @Test
  public final void testExplainExport() throws Exception {
    String result = StructuredExportService.explainFormats();
    assertNotNull("explain formats file is null", result);
    logger.info("explain formats: " + result);
  }

  /**
   * Test service with a item list XML.
   * 
   * @throws Exception Any exception.
   */
  @Test
  public final void testStructuredExports() throws Exception {
    long start;
    for (String f : ITEM_LISTS_FILE_MAMES.keySet()) {
      logger.info("Export format: " + f);
      logger.info("Number of items to proceed: " + TestHelper.ITEMS_LIMIT);
      String itemList =
          ResourceUtil.getResourceAsString(ITEM_LISTS_FILE_MAMES.get(f),
              StructuredExportServiceTest.class.getClassLoader());

      start = System.currentTimeMillis();
      byte[] result = StructuredExportService.getOutput(itemList, f);
      logger.info("Processing time: " + (System.currentTimeMillis() - start));
      logger.info("---------------------------------------------------");
      assertFalse(f + " output is empty", result == null || result.length == 0);
      logger.info(f + " export result:\n" + new String(result));
      TestHelper.writeBinFile(result, "target/" + f + "_result.txt");
    }

  }

  @Test
  public void doExportTest() throws Exception {
    String itemList =
        ResourceUtil.getResourceAsString("publicationItems/metadataV2/item_book.xml",
            StructuredExportServiceTest.class.getClassLoader());
    PubItemVO itemVO = XmlTransformingService.transformToPubItem(itemList);
    List<PubItemVO> pubitemList = Arrays.asList(itemVO);
    itemList = XmlTransformingService.transformToItemList(pubitemList);
    byte[] result = StructuredExportService.getOutput(itemList, "BIBTEX");
    assertNotNull(result);
    logger.info("BIBTEX (Book)");
    logger.info(new String(result));

    itemList =
        ResourceUtil.getResourceAsString("publicationItems/metadataV2/item_book.xml",
            StructuredExportServiceTest.class.getClassLoader());
    itemVO = XmlTransformingService.transformToPubItem(itemList);
    pubitemList = Arrays.asList(itemVO);
    itemList = XmlTransformingService.transformToItemList(pubitemList);
    result = StructuredExportService.getOutput(itemList, "ENDNOTE");
    assertNotNull(result);
    logger.info("ENDNOTE (Book)");
    logger.info(new String(result));

    itemList =
        ResourceUtil.getResourceAsString("publicationItems/metadataV2/item_thesis.xml",
            StructuredExportServiceTest.class.getClassLoader());
    itemVO = XmlTransformingService.transformToPubItem(itemList);
    pubitemList = Arrays.asList(itemVO);
    itemList = XmlTransformingService.transformToItemList(pubitemList);
    result = StructuredExportService.getOutput(itemList, "BIBTEX");
    assertNotNull(result);
    logger.info("BIBTEX (Thesis)");
    logger.info(new String(result));

    itemList =
        ResourceUtil.getResourceAsString("publicationItems/metadataV2/item_thesis.xml",
            StructuredExportServiceTest.class.getClassLoader());
    itemVO = XmlTransformingService.transformToPubItem(itemList);
    pubitemList = Arrays.asList(itemVO);
    itemList = XmlTransformingService.transformToItemList(pubitemList);
    result = StructuredExportService.getOutput(itemList, "ENDNOTE");
    assertNotNull(result);
    logger.info("ENDNOTE (Thesis)");
    logger.info(new String(result));
  }


  /**
   * Test service with a non-valid item list XML.
   * 
   * @throws Exception
   * @throws Exception Any exception.
   */
  @Test(expected = StructuredExportManagerException.class)
  @Ignore
  public final void testBadItemsListEndNoteExport() throws Exception {
    StructuredExportService.getOutput(itemLists.get("BAD_ITEM_LIST"), "ENDNOTE");
  }
}
