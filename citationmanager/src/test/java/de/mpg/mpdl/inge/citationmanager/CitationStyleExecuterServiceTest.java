package de.mpg.mpdl.inge.citationmanager;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import de.mpg.mpdl.inge.citationmanager.utils.XmlHelper;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.util.EntityTransformer;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;

public class CitationStyleExecuterServiceTest {

  private static final Logger logger = Logger.getLogger(CitationStyleExecuterServiceTest.class);

  private static HashMap<String, String> itemLists;

  /**
   * Get test item list from XML
   * 
   * @throws Exception
   */
  @BeforeClass
  public static void getItemLists() throws Exception {

    itemLists = new HashMap<String, String>();

    for (String cs : CitationStyleExecuterService.getStyles()) {
      if (!XmlHelper.CSL.equals(cs)) {
        String itemList = TestHelper.getCitationStyleTestXmlAsString(TestHelper.getTestProperties(cs).getProperty("plain.test.xml"));
        assertNotNull("Item list xml is not found", itemList);
        itemLists.put(cs, itemList);
      }
    }
  }

  @Test
  public final void testOutputs() throws Exception {
    for (String cs : CitationStyleExecuterService.getStyles()) {
      if (!XmlHelper.CSL.equals(cs)) {
        logger.info("citationStyle <" + cs + ">");

        testOutput(cs, null, null, itemLists.get(cs));
      }
    }
  }

  /**
   * Test service for all citation styles and all output formats
   * 
   * @throws IOException
   * 
   * @throws Exception Any exception.
   */

  /*
   * outPrefix == null: omit output file generation outPrefix == "": generate output file, file name
   * by default outPrefix.length>0 == "": generate output file, use outPrefix as file name prefix
   */
  private void testOutput(String cs, String ouf, String outPrefix, String il) throws Exception {
    long start;
    List<String> result = null;
    logger.info("Test Citation Style: " + cs);

    start = System.currentTimeMillis();

    List<PubItemVO> oldItemList = XmlTransformingService.transformToPubItemList(il);
    List<ItemVersionVO> newItemList = EntityTransformer.transformToNew(oldItemList);
    result = CitationStyleExecuterService.getOutput(newItemList, new ExportFormatVO(ouf, cs));

    logger.info("Output to " + ouf + ", time: " + (System.currentTimeMillis() - start));
    assertTrue(ouf + " output should not be empty", result.size() > 0);

    for (String res : result) {
      assertNotNull("One of the citations is null", res);
      assertTrue("One of the citations has no content", !res.isEmpty());
    }

    logger.info(ouf + " length: " + result.size());
    if (result != null) {
      logger.info("***********************************************************************************************");
      logger.info(result);
      logger.info("***********************************************************************************************");
    }
  }
}
