package de.mpg.mpdl.inge.transformation;

import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;

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

    assertTransformation(wr, "results/bibtex_item.txt");
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

    assertXmlTransformation(wr, "results/doi_item.xml");
  }

  @Test
  public void testItemXmlV3ToEdocXml() throws TransformationException, IOException {

    StringWriter wr = new StringWriter();

    Transformer t = TransformerFactory.newInstance(FORMAT.ESCIDOC_ITEM_V3_XML, FORMAT.EDOC_XML);

    t.transform(
        new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream(
            "escidoc_item_v13.xml")), new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());

    assertTransformation(wr, "results/edoc_item.xml");
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

    assertTransformation(wr, "results/endnote_item.txt");
  }

  @Test
  public void testItemXmlV3ToEndnoteXml() throws TransformationException, IOException {

    StringWriter wr = new StringWriter();

    Transformer t = TransformerFactory.newInstance(FORMAT.ESCIDOC_ITEM_V3_XML, FORMAT.ENDNOTE_XML);

    t.transform(
        new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream(
            "escidoc_item_v13.xml")), new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());

    assertTransformation(wr, "results/endnote_item.xml");
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

    assertTransformation(wr, "results/html-meta-tags.txt");
  }

  @Test
  public void testItemXmlV3ToMarc21Xml() throws TransformationException, IOException {

    StringWriter wr = new StringWriter();

    Transformer t = TransformerFactory.newInstance(FORMAT.ESCIDOC_ITEMLIST_V3_XML, FORMAT.MARC_XML);

    t.transform(
        new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream(
            "escidoc_item_v13.xml")), new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());

    assertXmlTransformation(wr, "results/marc21.xml");
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

}
