package de.mpg.mpdl.inge.citationmanager;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.xml.transform.TransformerFactory;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.mpg.mpdl.inge.citationmanager.utils.XmlHelper;
import de.mpg.mpdl.inge.citationmanager.xslt.CitationStyleManagerImpl;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.util.EntityTransformer;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.util.ResourceUtil;

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
  public final void testOutputs() throws CitationStyleManagerException {
    for (String cs : CitationStyleExecuterService.getStyles()) {
      for (String format : CitationStyleExecuterService.getOutputFormats(cs)) {
        if (!XmlHelper.CSL.equals(cs)) {
          logger.info("citationStyle <" + cs + "> format <" + format);
          try {
            testOutput(cs, format);
          } catch (Exception e) {
            e.printStackTrace();
            logger.info("Error in citationStyle <" + cs + "> format <" + format + "\n", e);
            Assert.fail(e.getMessage());
            continue;
          }
        }
      }
    }
  }

  @Test
  public final void testArxiv() throws Exception {

    testOutput(XmlHelper.APA, XmlHelper.SNIPPET, "", ResourceUtil.getResourceAsString("src/test/resources/testFiles/arXiv0904-2.3933.xml",
        CitationStyleManagerImpl.class.getClassLoader()));

  }

  public final void testOutput(String cs, String ouf, String outPrefix) throws Exception {
    testOutput(cs, ouf, outPrefix, itemLists.get(cs));
  }

  public final void testOutput(String cs, String ouf) throws Exception {
    testOutput(cs, ouf, null);
  }

  public final void testOutput(String cs) throws Exception {
    for (String format : CitationStyleExecuterService.getOutputFormats(cs)) {
      testOutput(cs, format);
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
  public final void testOutput(String cs, String ouf, String outPrefix, String il) throws IOException, TechnicalException {

    long start;
    List<String> result = null;
    logger.info("Test Citation Style: " + cs);

    start = System.currentTimeMillis();
    try {
      String escidocXml = ResourceUtil.getResourceAsString(il, CitationStyleManagerImpl.class.getClassLoader());
      List<PubItemVO> oldItemList = XmlTransformingService.transformToPubItemList(escidocXml);
      List<ItemVersionVO> newItemList = EntityTransformer.transformToNew(oldItemList);
      result = CitationStyleExecuterService.getOutput(newItemList, new ExportFormatVO(ouf, cs));
    } catch (CitationStyleManagerException e) {
      Assert.fail(e.getMessage());
    }

    logger.info("Output to " + ouf + ", time: " + (System.currentTimeMillis() - start));
    assertTrue(ouf + " output should not be empty", result.size() > 0);

    logger.info(ouf + " length: " + result.size());
    if (result != null) {
      logger.info("***********************************************************************************************");
      logger.info(result);
      logger.info("***********************************************************************************************");
    }

    /*
    if (outPrefix != null)
      try {
        TestHelper.writeToFile(
            "target/" + (!outPrefix.equals("") ? outPrefix + "_" : "") + cs + "_" + ouf + "." + XmlHelper.getExtensionByName(ouf), result);
      } catch (CitationStyleManagerException e) {
        Assert.fail(e.getMessage());
      }
      */

  }



}
