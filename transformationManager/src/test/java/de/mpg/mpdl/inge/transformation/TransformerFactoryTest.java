package de.mpg.mpdl.inge.transformation;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

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
            .replace("\n", "").replace("\r", "");
    String result = wr.toString().replace("\n", "").replace("\r", "");

    assertTrue(StringUtils.difference(expectedResult, result), expectedResult.equals(result));



  }

}
