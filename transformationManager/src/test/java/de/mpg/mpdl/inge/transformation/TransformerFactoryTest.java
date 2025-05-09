package de.mpg.mpdl.inge.transformation;

import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
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

// @FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class TransformerFactoryTest {

  private static final Logger logger = Logger.getLogger(TransformerFactoryTest.class);

  public final static FORMAT[] sourceForESCIDOC_ITEM_V3_XML = {FORMAT.ARXIV_OAIPMH_XML, FORMAT.BIBTEX_STRING, FORMAT.BMC_XML,
      FORMAT.BMC_OAIPMH_XML, FORMAT.EDOC_XML, FORMAT.ENDNOTE_XML, FORMAT.MAB_XML, FORMAT.MARC_XML, FORMAT.MODS_XML, FORMAT.PEER_TEI_XML,
      FORMAT.PMC_OAIPMH_XML, FORMAT.RIS_XML, FORMAT.SPIRES_XML, FORMAT.WOS_XML, FORMAT.ZFN_TEI_XML};

  public final static FORMAT[] targetForESCIDOC_ITEM_V3_XML =
      {FORMAT.DOI_METADATA_XML, FORMAT.ZIM_XML, FORMAT.EDOC_XML, FORMAT.OAI_DC, FORMAT.BIBTEX_STRING, FORMAT.ESCIDOC_ITEM_V2_XML,
          FORMAT.HTML_METATAGS_DC_XML, FORMAT.HTML_METATAGS_HIGHWIRE_PRESS_CIT_XML, FORMAT.ENDNOTE_STRING,};

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  //
  // source ESCIDOC_ITEM_V3_XML
  //

  @Test
  public void testItemXmlV3ToItemXmlV2() throws FileNotFoundException, TransformationException {

    StringWriter wr = new StringWriter();

    Transformer t = TransformerFactory.newTransformer(FORMAT.ESCIDOC_ITEM_V3_XML, FORMAT.ESCIDOC_ITEM_V2_XML);

    t.transform(new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream("sourceFiles/escidoc_item_v13.xml")),
        new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());

    assertTrue(wr.toString().length() > 1000);
  }

  @Test
  public void testItemXmlV3ToBibtex() throws TransformationException, IOException {

    StringWriter wr = new StringWriter();

    Transformer t = TransformerFactory.newTransformer(FORMAT.ESCIDOC_ITEM_V3_XML, FORMAT.BIBTEX_STRING);

    t.transform(new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream("sourceFiles/escidoc_item_v13.xml")),
        new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());

    assertTransformation(wr, "results/fromEscidocItemToBibtex.txt");
  }


  @Test
  public void testItemXmlV3ToDoiMetadataXml() throws TransformationException, IOException {

    StringWriter wr = new StringWriter();

    Transformer t = TransformerFactory.newTransformer(FORMAT.ESCIDOC_ITEM_V3_XML, FORMAT.DOI_METADATA_XML);

    t.transform(new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream("sourceFiles/escidoc_item_doi_v13.xml")),
        new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());

    assertXmlTransformation(wr, "results/fromEscidocItemToDoiMetadata.xml");
  }

  @Test
  public void testItemXmlV3ToEdocXml() throws TransformationException, IOException {

    StringWriter wr = new StringWriter();

    Transformer t = TransformerFactory.newTransformer(FORMAT.ESCIDOC_ITEM_V3_XML, FORMAT.EDOC_XML);

    t.transform(new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream("sourceFiles/escidoc_item_v13.xml")),
        new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());

    assertXmlTransformationWithIgnore(wr, "results/fromEscidocItemToEdocXml.xml",
        Arrays.asList(new String[] {"fturl,viewftext=PUBLIC filename=Acheson_et_al_Brain_Lang_2012.pdf size=366920, "}));
  }

  @Test
  public void testItemXmlV3ToEndnote() throws TransformationException, IOException {

    StringWriter wr = new StringWriter();

    Transformer t = TransformerFactory.newTransformer(FORMAT.ESCIDOC_ITEM_V3_XML, FORMAT.ENDNOTE_STRING);

    t.transform(new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream("sourceFiles/escidoc_item_v13.xml")),
        new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());

    assertTransformation(wr, "results/fromEscidocItemToEndnote.txt");
  }

  @Test
  public void testItemXmlV3ToEndnoteXml() throws TransformationException, IOException {

    StringWriter wr = new StringWriter();

    Transformer t = TransformerFactory.newTransformer(FORMAT.ESCIDOC_ITEM_V3_XML, FORMAT.ENDNOTE_XML);

    t.transform(new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream("sourceFiles/escidoc_item_v13.xml")),
        new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());

    assertTransformation(wr, "results/fromEscidocItemToEndnoteXml.xml");
  }

  @Test
  public void testItemXmlV3ToHtmlMetaTags() throws TransformationException, IOException {

    StringWriter wr = new StringWriter();

    Transformer t = TransformerFactory.newTransformer(FORMAT.ESCIDOC_ITEM_V3_XML, FORMAT.HTML_METATAGS_DC_XML);

    t.transform(
        new TransformerStreamSource(
            getClass().getClassLoader().getResourceAsStream("sourceFiles/escidoc_item_component_public_pdf_v13.xml")),
        new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());

    assertTransformation(wr, "results/fromEscidocItemToHtmlMetatagsDC.xml");
  }

  @Test
  public void testItemXmlV3ToHtmlMetaTagsHighwirePressCitXml() throws TransformationException, IOException {

    StringWriter wr = new StringWriter();

    Transformer t = TransformerFactory.newTransformer(FORMAT.ESCIDOC_ITEM_V3_XML, FORMAT.HTML_METATAGS_HIGHWIRE_PRESS_CIT_XML);

    t.transform(
        new TransformerStreamSource(
            getClass().getClassLoader().getResourceAsStream("sourceFiles/escidoc_item_component_public_pdf_v13.xml")),
        new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());

    assertTransformation(wr, "results/fromEscidocItemToHtmlMetaTagsHighwirePressCit.xml");
  }

  @Test
  public void testItemXmlV3ToMarcXml() throws TransformationException, IOException {

    StringWriter wr = new StringWriter();

    Transformer t = TransformerFactory.newTransformer(FORMAT.ESCIDOC_ITEMLIST_V3_XML, FORMAT.MARC_XML);

    t.transform(new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream("sourceFiles/escidoc_item_v13.xml")),
        new TransformerStreamResult(wr));
    logger.info("MARC_EXPORT");
    logger.info("\n" + wr.toString());

    assertXmlTransformationWithIgnore(wr, "results/fromEscidocItemToMarcXml.xml",
        Arrays.asList(new String[] {"controlfield, tag=005, http://www.loc.gov/MARC21/slim",
            "controlfield, tag=008, http://www.loc.gov/MARC21/slim", "subfield, code=u, http://www.loc.gov/MARC21/slim"}));
  }

  @Test
  public void testItemXmlV3ToOaiDcXml() throws TransformationException, IOException {

    StringWriter wr = new StringWriter();

    Transformer t = TransformerFactory.newTransformer(FORMAT.ESCIDOC_ITEMLIST_V3_XML, FORMAT.OAI_DC);

    t.transform(new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream("sourceFiles/escidoc_item_v13.xml")),
        new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());

    assertTransformation(wr, "results/fromEscidocItemToOaiDC.xml");
  }

  @Test
  public void testItemXmlV3ToZimXml() throws TransformationException, IOException {

    StringWriter wr = new StringWriter();

    Transformer t = TransformerFactory.newTransformer(FORMAT.ESCIDOC_ITEMLIST_V3_XML, FORMAT.ZIM_XML);

    t.transform(new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream("sourceFiles/escidoc_item_v13.xml")),
        new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());

    assertXmlTransformationWithIgnore(wr, "results/fromEscidocItemToZimXml.xml",
        Arrays.asList(new String[] {"fturl,viewftext=PUBLIC filename=Acheson_et_al_Brain_Lang_2012.pdf, ", "identifier,type=url, "}));
  }

  // target ItemXml

  @Test
  public void testArXivOaiXmlToItemXml() throws TransformationException, IOException {

    StringWriter wr = new StringWriter();

    Transformer t = TransformerFactory.newTransformer(FORMAT.ARXIV_OAIPMH_XML, FORMAT.ESCIDOC_ITEM_V3_XML);

    t.transform(new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream("sourceFiles/arXiv.xml")),
        new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());

    assertXmlTransformation(wr, "results/fromArXivXmlToEscidocItem.xml");
  }

  @Test
  public void testBibtexToItemXmlV3() throws TransformationException, IOException {

    StringWriter wr = new StringWriter();

    Transformer t = TransformerFactory.newTransformer(FORMAT.BIBTEX_STRING, FORMAT.ESCIDOC_ITEM_V3_XML);

    t.transform(new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream("sourceFiles/bibtex.txt")),
        new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());

    assertXmlTransformationWithIgnore(wr, "results/fromBibtexToEscidocItem.xml", Arrays.asList(
        new String[] {"date, , http://escidoc.de/core/01/properties/release/", "date, , http://escidoc.de/core/01/properties/version/"}));
  }

  @Test
  @Ignore
  public void testBmcXmlToItemXmlV3() throws TransformationException, IOException {

    StringWriter wr = new StringWriter();

    Transformer t = TransformerFactory.newTransformer(FORMAT.BMC_XML, FORMAT.ESCIDOC_ITEM_V3_XML);

    t.transform(new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream("sourceFiles/bmc.xml")),
        new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());

    assertXmlTransformationWithIgnore(wr, "results/fromBmcXmlToEscidocItem.xml", Arrays.asList(
        new String[] {"description, , http://purl.org/dc/elements/1.1/", "person, ,http://purl.org/escidoc/metadata/profiles/0.1/person"}));
  }

  @Test
  @Ignore
  public void testEdocXmlToItemXmlV3() throws TransformationException, IOException {

    StringWriter wr = new StringWriter();

    Transformer t = TransformerFactory.newTransformer(FORMAT.EDOC_XML, FORMAT.ESCIDOC_ITEM_V3_XML);

    t.transform(new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream("sourceFiles/edoc_item1.xml")),
        new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());

    assertXmlTransformation(wr, "results/fromEdocToEscidocItem.xml");
  }

  @Test
  public void testEndnoteXmlToItemXmlV3() throws TransformationException, IOException {

    StringWriter wr = new StringWriter();

    Transformer t = TransformerFactory.newTransformer(FORMAT.ENDNOTE_XML, FORMAT.ESCIDOC_ITEM_V3_XML);

    t.transform(new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream("sourceFiles/endnote_item.xml")),
        new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());

    assertXmlTransformationWithIgnore(wr, "results/fromEndnoteXmlToItemXml.xml",
        Arrays.asList(new String[] {"creator,role=http://www.loc.gov/loc.terms/relators/AUT,http://purl.org/escidoc/metadata/terms/0.1/"}));
  }

  @Test
  public void testMabXmlToItemXmlV3() throws TransformationException, IOException {

    StringWriter wr = new StringWriter();

    Transformer t = TransformerFactory.newTransformer(FORMAT.MAB_XML, FORMAT.ESCIDOC_ITEM_V3_XML);

    t.transform(new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream("sourceFiles/mabXml_item.xml")),
        new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());

    assertXmlTransformation(wr, "results/fromMabXmlToEscidocItem.xml");
  }

  @Ignore
  @Test(expected = TransformationException.class)
  public void testMabXmlWrongLinkToItemXmlV3() throws TransformationException {

    StringWriter wr = new StringWriter();

    Transformer t = TransformerFactory.newTransformer(FORMAT.MAB_XML, FORMAT.ESCIDOC_ITEM_V3_XML);

    t.transform(new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream("sourceFiles/mabXml_item_wronglink.xml")),
        new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());
  }

  @Test
  public void testMarcXmlToItemXmlV3() throws TransformationException, IOException {

    StringWriter wr = new StringWriter();

    Transformer t = TransformerFactory.newTransformer(FORMAT.MARC_XML, FORMAT.ESCIDOC_ITEM_V3_XML);

    t.transform(new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream("sourceFiles/marc_item.xml")),
        new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());

    assertXmlTransformation(wr, "results/fromMarcXmlToEscidocItem.xml");
  }

  @Test
  public void testPmcOaiXmlToItemXmlV3() throws TransformationException, IOException {

    StringWriter wr = new StringWriter();

    Transformer t = TransformerFactory.newTransformer(FORMAT.PMC_OAIPMH_XML, FORMAT.ESCIDOC_ITEM_V3_XML);

    t.transform(new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream("sourceFiles/pmcOai.xml")),
        new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());

    assertXmlTransformation(wr, "results/fromPmcOaiXmlToEscidocItem.xml");
  }

  @Ignore
  @Test
  public void testSpiresToItemXmlV3() throws TransformationException, IOException {

    StringWriter wr = new StringWriter();

    Transformer t = TransformerFactory.newTransformer(FORMAT.SPIRES_XML, FORMAT.ESCIDOC_ITEM_V3_XML);

    t.transform(new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream("sourceFiles/spires.html")),
        new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());

    assertXmlTransformation(wr, "results/fromSpiresXmlToEscidocItem.xml");
  }

  /*
   * @Test public void testModsXmlToItemXmlV3() throws TransformationException, IOException {
   * 
   * StringWriter wr = new StringWriter();
   * 
   * Transformer t = TransformerFactory.newTransformer(FORMAT.MODS_XML, FORMAT.ESCIDOC_ITEM_V3_XML);
   * 
   * t.transform( new
   * TransformerStreamSource(getClass().getClassLoader().getResourceAsStream("mods2.xml")), new
   * TransformerStreamResult(wr));
   * 
   * logger.info("\n" + wr.toString());
   * 
   * // assertXmlTransformation(wr, "results/fromMarcXmlToEscidocItem.xml"); }
   */


  //
  // other transformations
  //
  @Test
  public void testEndnoteToEndnoteXml() throws TransformationException, IOException {

    StringWriter wr = new StringWriter();

    Transformer t = TransformerFactory.newTransformer(FORMAT.ENDNOTE_STRING, FORMAT.ENDNOTE_XML);

    t.transform(new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream("sourceFiles/endnote_item.txt")),
        new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());

    assertXmlTransformation(wr, "results/fromEndnoteToEndnoteXml.xml");
  }

  @Test
  public void testMabToMabXml() throws TransformationException, IOException {

    StringWriter wr = new StringWriter();

    Transformer t = TransformerFactory.newTransformer(FORMAT.MAB_STRING, FORMAT.MAB_XML);

    t.transform(new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream("sourceFiles/mab_item_list.txt")),
        new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());

    assertXmlTransformation(wr, "results/fromMabToMabXml.xml");
  }

  @Test
  public void testMarc21ToMarcXml() throws TransformationException, IOException {

    StringWriter wr = new StringWriter();

    Transformer t = TransformerFactory.newTransformer(FORMAT.MARC_21_STRING, FORMAT.MARC_XML);

    t.transform(new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream("sourceFiles/marc_record.mrc")),
        new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());

    assertXmlTransformationWithIgnore(wr, "results/fromMarc21ToMarcXml.xml",
        Arrays.asList(new String[] {"description, , http://purl.org/dc/elements/1.1/"}));
  }

  @Test
  public void testModsXmlToMarcXml() throws TransformationException, IOException {

    StringWriter wr = new StringWriter();

    Transformer t = TransformerFactory.newTransformer(FORMAT.MODS_XML, FORMAT.MARC_XML);

    t.transform(new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream("sourceFiles/mods_item.xml")),
        new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());

    assertXmlTransformation(wr, "results/fromModsXmlToMarcXml.xml");
  }

  @Test
  public void testModsXmlToOaiDcXml() throws TransformationException, IOException {

    StringWriter wr = new StringWriter();

    Transformer t = TransformerFactory.newTransformer(FORMAT.MODS_XML, FORMAT.OAI_DC);

    t.transform(new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream("sourceFiles/mods_item.xml")),
        new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());

    assertXmlTransformation(wr, "results/fromModsXmlToOaiDc.xml");
  }

  @Test
  public void testRisToRisXml() throws TransformationException, IOException {

    StringWriter wr = new StringWriter();

    Transformer t = TransformerFactory.newTransformer(FORMAT.RIS_STRING, FORMAT.RIS_XML);

    t.transform(new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream("sourceFiles/ris.txt")),
        new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());

    assertXmlTransformation(wr, "results/fromRisToRisXml.xml");
  }

  @Test
  public void testRisXmlToItemXmlV3() throws TransformationException, IOException {

    StringWriter wr = new StringWriter();

    Transformer t = TransformerFactory.newTransformer(FORMAT.RIS_XML, FORMAT.ESCIDOC_ITEM_V3_XML);

    t.transform(new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream("sourceFiles/ris_item.xml")),
        new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());

    assertXmlTransformation(wr, "results/fromRisXmlToEscidocItem.xml");
  }

  @Test
  public void testWosToWosXml() throws TransformationException, IOException {

    StringWriter wr = new StringWriter();

    Transformer t = TransformerFactory.newTransformer(FORMAT.WOS_STRING, FORMAT.WOS_XML);

    t.transform(new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream("sourceFiles/wos_item.txt")),
        new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());

    assertXmlTransformation(wr, "results/fromWosToWosXml.xml");
  }

  @Test
  public void testWosXmlToItemXmlV3() throws TransformationException, IOException {

    StringWriter wr = new StringWriter();

    Transformer t = TransformerFactory.newTransformer(FORMAT.WOS_XML, FORMAT.ESCIDOC_ITEM_V3_XML);

    t.transform(new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream("sourceFiles/wos_item.xml")),
        new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());

    assertXmlTransformation(wr, "results/fromWosXmlToEscidocItemXml.xml");
  }

  /*
   * @Test public void testRisToItemListXml() throws TransformationException, IOException {
   * 
   * StringWriter wr = new StringWriter();
   * 
   * Transformer t = TransformerFactory.newTransformer(FORMAT.RIS_XML, FORMAT.ESCIDOC_ITEMLIST_V3_XML);
   * 
   * t.transform( new
   * TransformerStreamSource(getClass().getClassLoader().getResourceAsStream("ris.txt")), new
   * TransformerStreamResult(wr));
   * 
   * logger.info("\n" + wr.toString());
   * 
   * //assertXmlTransformation(wr, "results/fromRisXmlToEscidocItem.xml");
   * 
   * }
   */

  //
  // chain transformers
  //
  @Test
  public void testEndnoteToItemXmlV3() throws TransformationException, IOException {

    StringWriter wr = new StringWriter();

    Transformer t = TransformerFactory.newTransformer(FORMAT.ENDNOTE_STRING, FORMAT.ESCIDOC_ITEM_V3_XML);

    t.transform(new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream("sourceFiles/endnote_item.txt")),
        new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());

    assertXmlTransformation(wr, "results/fromEndnoteToItemXml.xml");
  }

  //  @Test
  //  public void testEndnoteToItemXmlList() throws TransformationException, IOException {
  //
  //    StringWriter wr = new StringWriter();
  //
  //    Transformer t = TransformerFactory.newTransformer(FORMAT.ENDNOTE_STRING, FORMAT.ESCIDOC_ITEMLIST_V3_XML);
  //
  //    t.transform(new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream("sourceFiles/endnote_item.txt")),
  //        new TransformerStreamResult(wr));
  //
  //    logger.info("\n" + wr.toString());
  //
  //    assertXmlTransformation(wr, "results/fromEndnoteToItemListXml.xml");
  //  }
  //
  @Test
  public void testMabToItemXmlV3() throws TransformationException, IOException {

    StringWriter wr = new StringWriter();

    Transformer t = TransformerFactory.newTransformer(FORMAT.MAB_STRING, FORMAT.ESCIDOC_ITEM_V3_XML);

    t.transform(new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream("sourceFiles/mab_item.txt")),
        new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());

    assertXmlTransformation(wr, "results/fromMabToEscidocItemXml.xml");
  }

  //  @Test
  //  public void testMarc21ToItemXmlV3() throws TransformationException, IOException {
  //
  //    StringWriter wr = new StringWriter();
  //
  //    Transformer t = TransformerFactory.newTransformer(FORMAT.MARC_21_STRING, FORMAT.ESCIDOC_ITEM_V3_XML);
  //
  //    t.transform(new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream("sourceFiles/marc21.txt")),
  //        new TransformerStreamResult(wr));
  //
  //    logger.info("\n" + wr.toString());
  //
  //    assertXmlTransformation(wr, "results/fromMarc21ToEscidocItemXml.xml");
  //  }

  @Test
  public void testWosToItemXmlV3() throws TransformationException, IOException {

    StringWriter wr = new StringWriter();

    Transformer t = TransformerFactory.newTransformer(FORMAT.WOS_STRING, FORMAT.ESCIDOC_ITEM_V3_XML);

    t.transform(new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream("sourceFiles/wos_item.txt")),
        new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());

    assertXmlTransformation(wr, "results/fromWosToEscidocItemXml.xml");
  }

  @Test
  public void testIdentity() throws TransformationException, IOException {
    Transformer t = TransformerFactory.newTransformer(FORMAT.ESCIDOC_ITEM_V3_XML, FORMAT.ESCIDOC_ITEM_V3_XML);

    assertTrue(t.getClass().getName().equals("de.mpg.mpdl.inge.transformation.transformers.IdentityTransformer"));

    StringWriter wr = new StringWriter();
    StringWriter wr1 = new StringWriter();

    t.transform(new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream("sourceFiles/escidoc_item_v13.xml")),
        new TransformerStreamResult(wr));

    logger.info("\n" + wr.toString());

    IOUtils.copy(getClass().getClassLoader().getResourceAsStream("sourceFiles/escidoc_item_v13.xml"), wr1, "UTF-8");

    assertTrue(wr.toString().length() == wr1.toString().length());
  }


  //
  // deprecated transformations
  //
  //  @Test
  //  @Ignore
  //  public void testItemXmlV2ToItemXmlV1() throws FileNotFoundException, TransformationException {
  //
  //    StringWriter wr = new StringWriter();
  //
  //    Transformer t = TransformerFactory.newTransformer(FORMAT.ESCIDOC_ITEM_V2_XML, FORMAT.ESCIDOC_ITEM_V1_XML);
  //
  //    t.transform(new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream("sourceFiles/escidoc_item_v2.xml")),
  //        new TransformerStreamResult(wr));
  //
  //    logger.info("\n" + wr.toString());
  //
  //    assertTrue(wr.toString().length() > 10000);
  //  }
  //
  //  @Test
  //  @Ignore
  //  public void testItemXmlV3ToItemXmlV1() throws FileNotFoundException, TransformationException {
  //
  //    StringWriter wr = new StringWriter();
  //
  //    Transformer t = TransformerFactory.newTransformer(FORMAT.ESCIDOC_ITEM_V3_XML, FORMAT.ESCIDOC_ITEM_V1_XML);
  //
  //    t.transform(new TransformerStreamSource(getClass().getClassLoader().getResourceAsStream("sourceFiles/escidoc_item_v13.xml")),
  //        new TransformerStreamResult(wr));
  //
  //    logger.info("\n" + wr.toString());
  //
  //    assertTrue(wr.toString().length() > 1000);
  //
  //  }

  @Test
  public void testBmcXmlToBmcOaiPmhXml() throws FileNotFoundException, TransformationException {

    thrown.expect(TransformationException.class);
    thrown.expectMessage("No transformation chain found for");

    TransformerFactory.newTransformer(FORMAT.BMC_XML, FORMAT.BMC_OAIPMH_XML);
  }

  // Helper method to compare expected result with real result
  // we strip the String omitting all not printible characters
  private void assertTransformation(StringWriter wr, String fileNameOfExpectedResult) throws IOException {

    String result = wr.toString().replaceAll("[^A-Za-z0-9]", "");
    String expectedResult =
        ResourceUtil.getResourceAsString(fileNameOfExpectedResult, getClass().getClassLoader()).replaceAll("[^A-Za-z0-9]", "");

    String difference = StringUtils.difference(expectedResult, result);

    assertTrue("Difference in assert <" + difference + ">", difference.equals(""));
  }

  private void assertXmlTransformation(StringWriter wr, String fileNameOfExpectedResult) throws IOException {
    String result = wr.toString();
    String expectedResult = ResourceUtil.getResourceAsString(fileNameOfExpectedResult, getClass().getClassLoader());

    XmlComparator xmlComparator = null;
    try {
      xmlComparator = new XmlComparator(result, expectedResult);
    } catch (Exception e) {
      e.printStackTrace();
    }

    assertTrue("Difference in assert <" + xmlComparator.listErrors() + ">", xmlComparator.equal());
  }

  private void assertXmlTransformationWithIgnore(StringWriter wr, String fileNameOfExpectedResult, List<String> ignoreElements)
      throws IOException {
    String result = wr.toString();
    String expectedResult = ResourceUtil.getResourceAsString(fileNameOfExpectedResult, getClass().getClassLoader());

    XmlComparator xmlComparator = null;
    try {
      xmlComparator = new XmlComparator(result, expectedResult, ignoreElements);
    } catch (Exception e) {
      e.printStackTrace();
    }

    assertTrue("Difference in assert <" + xmlComparator.listErrors() + ">", xmlComparator.equal());
  }

  // ///////////////////////////////////////////////////////////////////////////////////////////////////

  @Test
  public void testGetAllSourceFormatsFor() {
    assertTrue(Arrays.asList(TransformerFactory.getAllSourceFormatsFor(FORMAT.ESCIDOC_ITEM_V3_XML))
        .containsAll(Arrays.asList(sourceForESCIDOC_ITEM_V3_XML)));
  }

  @Test
  public void testGetAllTargetFormatsFor() {
    assertTrue(Arrays.asList(TransformerFactory.getAllTargetFormatsFor(FORMAT.ESCIDOC_ITEM_V3_XML))
        .containsAll(Arrays.asList(targetForESCIDOC_ITEM_V3_XML)));
  }

}
