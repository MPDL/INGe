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

    String expectedResult =
        ResourceUtil.getResourceAsString("results/bibtex_item.txt", getClass().getClassLoader())
            .replaceAll("[^A-Za-z0-9]", "");
    String result = wr.toString().replaceAll("[^A-Za-z0-9]", "");

    assertTrue(StringUtils.difference(expectedResult, result), expectedResult.equals(result));
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

    String expectedResult =
        ResourceUtil.getResourceAsString("results/doi_item.xml", getClass().getClassLoader())
            .replaceAll("[^A-Za-z0-9]", "");
    logger.info("expectedResult\n" + expectedResult);

    String result = wr.toString().replaceAll("[^A-Za-z0-9]", "");
    logger.info("result\n" + result);

    assertTrue(StringUtils.difference(expectedResult, result), expectedResult.equals(result));

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



}
