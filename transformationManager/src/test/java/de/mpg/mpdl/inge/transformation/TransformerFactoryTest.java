package de.mpg.mpdl.inge.transformation;

import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.transformation.results.TransformerStreamResult;
import de.mpg.mpdl.inge.transformation.sources.TransformerStreamSource;
import de.mpg.mpdl.inge.util.ResourceUtil;
import de.mpg.mpdl.inge.util.XmlComparator;


public class TransformerFactoryTest {

  private static Logger logger = Logger.getLogger(TransformerFactoryTest.class);

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  //
  // source ESCIDOC_ITEM_V3_XML
  //

  @Test
  public void testItemXmlV3ToItemXmlV2() throws FileNotFoundException, TransformationException {

    StringWriter wr = new StringWriter();

    Transformer t =
        TransformerFactory.newInstance(FORMAT.ESCIDOC_ITEM_V3_XML, FORMAT.ESCIDOC_ITEM_V2_XML);

    t.transform(
        new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream(
            "escidoc_item_v13.xml")), new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());

    assertTrue(wr.toString().length() > 1000);

  }

  @Test
  public void testItemXmlV3ToBibtex() throws TransformationException, IOException {

    StringWriter wr = new StringWriter();

    Transformer t =
        TransformerFactory.newInstance(FORMAT.ESCIDOC_ITEM_V3_XML, FORMAT.BIBTEX_STRING);

    t.transform(
        new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream(
            "escidoc_item_v13.xml")), new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());

    assertTransformation(wr, "results/fromEscidocItemToBibtex.txt");
  }



  @Test
  public void testItemXmlV3ToDoiMetadataXml() throws TransformationException, IOException {

    StringWriter wr = new StringWriter();

    Transformer t =
        TransformerFactory.newInstance(FORMAT.ESCIDOC_ITEM_V3_XML, FORMAT.DOI_METADATA_XML);

    t.transform(
        new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream(
            "escidoc_item_doi_v13.xml")), new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());

    assertXmlTransformation(wr, "results/fromEscidocItemToDoiMetadata.xml");
  }

  @Test
  public void testItemXmlV3ToEdocXml() throws TransformationException, IOException {

    StringWriter wr = new StringWriter();

    Transformer t = TransformerFactory.newInstance(FORMAT.ESCIDOC_ITEM_V3_XML, FORMAT.EDOC_XML);

    t.transform(
        new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream(
            "escidoc_item_v13.xml")), new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());

    assertXmlTransformation(wr, "results/fromEscidocItemToEdoc.xml");
  }

  @Test
  public void testItemXmlV3ToEndnote() throws TransformationException, IOException {

    StringWriter wr = new StringWriter();

    Transformer t =
        TransformerFactory.newInstance(FORMAT.ESCIDOC_ITEM_V3_XML, FORMAT.ENDNOTE_STRING);

    t.transform(
        new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream(
            "escidoc_item_v13.xml")), new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());

    assertTransformation(wr, "results/fromEscidocItemToEndnote.txt");
  }

  @Test
  public void testItemXmlV3ToEndnoteXml() throws TransformationException, IOException {

    StringWriter wr = new StringWriter();

    Transformer t = TransformerFactory.newInstance(FORMAT.ESCIDOC_ITEM_V3_XML, FORMAT.ENDNOTE_XML);

    t.transform(
        new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream(
            "escidoc_item_v13.xml")), new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());

    assertTransformation(wr, "results/fromEscidocItemToEndnote.xml");
  }

  @Test
  public void testItemXmlV3ToHtmlMetaTags() throws TransformationException, IOException {

    StringWriter wr = new StringWriter();

    Transformer t =
        TransformerFactory.newInstance(FORMAT.ESCIDOC_ITEM_V3_XML, FORMAT.HTML_METATAGS_DC_XML);

    t.transform(
        new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream(
            "escidoc_item_v13.xml")), new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());

    assertTransformation(wr, "results/fromEscidocItemToHtmlMetatagsDC.xml");
  }

  @Test
  public void testItemXmlV3ToMarc21Xml() throws TransformationException, IOException {

    StringWriter wr = new StringWriter();

    Transformer t = TransformerFactory.newInstance(FORMAT.ESCIDOC_ITEMLIST_V3_XML, FORMAT.MARC_XML);

    t.transform(
        new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream(
            "escidoc_item_v13.xml")), new TransformerStreamResult(wr));
    logger.info("\n" + wr.toString());

    assertXmlTransformationWithIgnore(wr, "results/fromEscidocItemToMarc.xml",
        Arrays.asList(new String[] {
        		"controlfield, tag=005, http://www.loc.gov/MARC21/slim", 
        		"controlfield, tag=008, http://www.loc.gov/MARC21/slim"}));
  }

  @Test
  public void testItemXmlV3ToOaiDcXml() throws TransformationException, IOException {

    StringWriter wr = new StringWriter();

    Transformer t = TransformerFactory.newInstance(FORMAT.ESCIDOC_ITEMLIST_V3_XML, FORMAT.OAI_DC);

    t.transform(
        new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream(
            "escidoc_item_v13.xml")), new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());

    assertTransformation(wr, "results/fromEscidocItemToOaiDC.xml");
  }

  @Test
  public void testItemXmlV3ToZimXml() throws TransformationException, IOException {

    StringWriter wr = new StringWriter();

    Transformer t = TransformerFactory.newInstance(FORMAT.ESCIDOC_ITEMLIST_V3_XML, FORMAT.ZIM_XML);

    t.transform(
        new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream(
            "escidoc_item_v13.xml")), new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());

    assertXmlTransformation(wr, "results/fromEscidocItemToZim.xml");
  }
  
  //
  // target ESCIDOC_ITEM_V3_XML);
  //
  

  @Test
  public void testBibtexToItemXml() throws TransformationException, IOException {

    StringWriter wr = new StringWriter();

    Transformer t =
        TransformerFactory.newInstance(FORMAT.BIBTEX_STRING, FORMAT.ESCIDOC_ITEM_V3_XML);

    t.transform(
        new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream("bibtex.txt")),
        new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());
    
    assertXmlTransformationWithIgnore(wr, "results/fromBibtexToEscidocItem.xml",
            Arrays.asList(new String[] {
            		"date, , http://escidoc.de/core/01/properties/release/",
            		"date, , http://escidoc.de/core/01/properties/version/"}));
  }

  @Test
  public void testBmcXmlToItemXml() throws TransformationException, IOException {

    StringWriter wr = new StringWriter();

    Transformer t = TransformerFactory.newInstance(FORMAT.BMC_XML, FORMAT.ESCIDOC_ITEM_V3_XML);

    t.transform(
        new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream("bmc.xml")),
        new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());
    
    assertXmlTransformationWithIgnore(wr, "results/fromBmcToEscidocItem.xml",
            Arrays.asList(new String[] {
            		"description, , http://purl.org/dc/elements/1.1/"}));
  }
  

  
  //
  // other transformations
  //
  @Test
  public void testMarc21ToMarcXml() throws TransformationException, IOException {

    StringWriter wr = new StringWriter();

    Transformer t = TransformerFactory.newInstance(FORMAT.MARC_21_STRING, FORMAT.MARC_XML);

    t.transform(
        new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream("marc_record.mrc")),
        new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());
    
    assertXmlTransformationWithIgnore(wr, "results/fromMarc21ToMarc.xml",
            Arrays.asList(new String[] {
            		"description, , http://purl.org/dc/elements/1.1/"}));
    }
  
  @Test
  public void testRisToRisXml() throws TransformationException, IOException {

    StringWriter wr = new StringWriter();

    Transformer t = TransformerFactory.newInstance(FORMAT.RIS_STRING, FORMAT.RIS_XML);

    t.transform(
        new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream("ris.txt")),
        new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());
    
   assertXmlTransformation(wr, "results/fromRisToRis.xml");
   
  }


  //
  // deprecated transformations
  //
  @Test
  @Ignore
  public void testItemXmlV2ToItemXmlV1() throws FileNotFoundException, TransformationException {

    StringWriter wr = new StringWriter();

    Transformer t =
        TransformerFactory.newInstance(FORMAT.ESCIDOC_ITEM_V2_XML, FORMAT.ESCIDOC_ITEM_V1_XML);

    t.transform(
        new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream(
            "escidoc_item_v2.xml")), new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());

    assertTrue(wr.toString().length() > 10000);
  }

  @Test
  @Ignore
  public void testItemXmlV3ToItemXmlV1() throws FileNotFoundException, TransformationException {

    StringWriter wr = new StringWriter();

    Transformer t =
        TransformerFactory.newInstance(FORMAT.ESCIDOC_ITEM_V3_XML, FORMAT.ESCIDOC_ITEM_V1_XML);

    t.transform(
        new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream(
            "escidoc_item_v13.xml")), new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());

    assertTrue(wr.toString().length() > 1000);

  }

  // Helper method to compare expected result with real result
  // we strip the String omitting all not printible characters
  private void assertTransformation(StringWriter wr, String fileNameOfExpectedResult)
      throws IOException {

    String result = wr.toString().replaceAll("[^A-Za-z0-9]", "");
    String expectedResult =
        ResourceUtil.getResourceAsString(fileNameOfExpectedResult, getClass().getClassLoader())
            .replaceAll("[^A-Za-z0-9]", "");

    String difference = StringUtils.difference(expectedResult, result);

    assertTrue("Difference in assert <" + difference + ">", difference.equals(""));
  }

  private void assertXmlTransformation(StringWriter wr, String fileNameOfExpectedResult)
      throws IOException {
    String result = wr.toString();
    String expectedResult =
        ResourceUtil.getResourceAsString(fileNameOfExpectedResult, getClass().getClassLoader());

    XmlComparator xmlComparator = null;
    try {
      xmlComparator = new XmlComparator(result, expectedResult);
    } catch (Exception e) {
      e.printStackTrace();
    }

    assertTrue("Difference in assert <" + xmlComparator.listErrors() + ">", xmlComparator.equal());
  }

  private void assertXmlTransformationWithIgnore(StringWriter wr, String fileNameOfExpectedResult,
      List<String> ignoreElements) throws IOException {
    String result = wr.toString();
    String expectedResult =
        ResourceUtil.getResourceAsString(fileNameOfExpectedResult, getClass().getClassLoader());

    XmlComparator xmlComparator = null;
    try {
      xmlComparator = new XmlComparator(result, expectedResult, ignoreElements);
    } catch (Exception e) {
      e.printStackTrace();
    }

    assertTrue("Difference in assert <" + xmlComparator.listErrors() + ">", xmlComparator.equal());
  }

}
