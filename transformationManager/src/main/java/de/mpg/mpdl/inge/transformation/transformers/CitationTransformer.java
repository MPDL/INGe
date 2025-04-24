package de.mpg.mpdl.inge.transformation.transformers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.mpg.mpdl.inge.citationmanager.CitationStyleExecuterService;
import de.mpg.mpdl.inge.citationmanager.utils.XsltHelper;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.util.EntityTransformer;
import de.mpg.mpdl.inge.model.util.MapperFactory;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.wrappers.ItemVOListWrapper;
import de.mpg.mpdl.inge.transformation.ChainableTransformer;
import de.mpg.mpdl.inge.transformation.SingleTransformer;
import de.mpg.mpdl.inge.transformation.TransformerFactory;
import de.mpg.mpdl.inge.transformation.TransformerModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.transformation.results.TransformerResult;
import de.mpg.mpdl.inge.transformation.results.TransformerStreamResult;
import de.mpg.mpdl.inge.transformation.sources.TransformerSource;
import de.mpg.mpdl.inge.transformation.sources.TransformerVoSource;
import de.mpg.mpdl.inge.util.PropertyReader;
import de.mpg.mpdl.inge.util.ResourceUtil;
import net.sf.saxon.TransformerFactoryImpl;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FopFactoryBuilder;
import org.apache.fop.apps.MimeConstants;
import org.apache.fop.configuration.DefaultConfiguration;
import org.apache.fop.configuration.DefaultConfigurationBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.docx4j.convert.in.xhtml.XHTMLImporter;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.PPr;
import org.docx4j.wml.PPrBase;
import org.jsoup.nodes.Document;

import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.math.BigInteger;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@TransformerModule(sourceFormat = TransformerFactory.FORMAT.SEARCH_RESULT_VO, targetFormat = TransformerFactory.FORMAT.DOCX)
@TransformerModule(sourceFormat = TransformerFactory.FORMAT.SEARCH_RESULT_VO, targetFormat = TransformerFactory.FORMAT.PDF)
@TransformerModule(sourceFormat = TransformerFactory.FORMAT.SEARCH_RESULT_VO, targetFormat = TransformerFactory.FORMAT.JSON_CITATION)
@TransformerModule(sourceFormat = TransformerFactory.FORMAT.SEARCH_RESULT_VO, targetFormat = TransformerFactory.FORMAT.HTML_LINKED)
@TransformerModule(sourceFormat = TransformerFactory.FORMAT.SEARCH_RESULT_VO, targetFormat = TransformerFactory.FORMAT.HTML_PLAIN)
@TransformerModule(sourceFormat = TransformerFactory.FORMAT.SEARCH_RESULT_VO, targetFormat = TransformerFactory.FORMAT.ESCIDOC_SNIPPET)
public class CitationTransformer extends SingleTransformer implements ChainableTransformer {

  static final Logger logger = LogManager.getLogger(CitationTransformer.class);
  public static final String CONFIGURATION_CITATION = "citation";
  public static final String CONFIGURATION_CSL_ID = "csl_id";

  private static FopFactory fopFactory = instantiateFopFactory();

  @Override
  public void transform(TransformerSource source, TransformerResult result) throws TransformationException {
    try {
      SearchRetrieveResponseVO<ItemVersionVO> s = (SearchRetrieveResponseVO<ItemVersionVO>) ((TransformerVoSource) source).getSource();

      List<ItemVersionVO> itemList = s.getRecords().stream().map(SearchRetrieveRecordVO::getData).collect(Collectors.toList());
      ExportFormatVO exportFormat = new ExportFormatVO(getTargetFormat().getName(), getConfiguration().get(CONFIGURATION_CITATION),
          getConfiguration().get(CONFIGURATION_CSL_ID));

      List<String> citationList = CitationStyleExecuterService.getOutput(itemList, exportFormat);


      integrateCitationIntoOutput(exportFormat, s, itemList, citationList, result);
      //writeByteArrayToStreamResult(content, result);
    } catch (Exception e) {
      logger.error(e);
      throw new TransformationException("Error while citation transformation", e);
    }

  }

  @Override
  public TransformerResult createNewInBetweenResult() {
    return null;
  }


  private void integrateCitationIntoOutput(ExportFormatVO exportFormat, SearchRetrieveResponseVO<ItemVersionVO> searchResult,
      List<ItemVersionVO> itemList, List<String> citationList, TransformerResult transformerResult) throws Exception {

    if (itemList.size() != citationList.size()) {
      throw new IngeTechnicalException(
          "Error: Citationmanager returned " + citationList.size() + " citations for " + itemList.size() + " items");
    }

    TransformerStreamResult res = null;
    try {
      res = (TransformerStreamResult) transformerResult;
    } catch (Exception e1) {
      throw new TransformationException("Wrong result type, expected a TransformerStreamResult", e1);
    }


    Integer numberofRecords = searchResult != null ? searchResult.getNumberOfRecords() : null;

    if (TransformerFactory.FORMAT.JSON_CITATION.equals(getTargetFormat())) {
      if (null != searchResult) {
        JsonNode node = MapperFactory.getObjectMapper().valueToTree(searchResult);
        if (null != searchResult.getRecords() && !searchResult.getRecords().isEmpty()) {
          int i = 0;
          for (JsonNode itemNode : node.get("records").findValues("data")) {
            ((ObjectNode) itemNode).put("bibliographicCitation", citationList.get(i));
            i++;
          }
        }

        MapperFactory.getObjectMapper().writerWithDefaultPrettyPrinter().writeValue(res.getOutputStream(), node);
      }
    } else if (TransformerFactory.FORMAT.ESCIDOC_SNIPPET.equals(getTargetFormat())) {
      generateEsciDocSnippet(itemList, citationList, numberofRecords, res.getOutputStream());
      //return escidocSnippet.getBytes(StandardCharsets.UTF_8);
    } else if (TransformerFactory.FORMAT.HTML_PLAIN.equals(getTargetFormat())
        || TransformerFactory.FORMAT.HTML_LINKED.equals(getTargetFormat())) {

      try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

        generateEsciDocSnippet(itemList, citationList, numberofRecords, bos);
        String escidocSnippet = new String(bos.toByteArray(), StandardCharsets.UTF_8);
        generateHtmlOutput(escidocSnippet, getTargetFormat(), "html", true, res.getOutputStream());
      }
    } else if (TransformerFactory.FORMAT.DOCX.equals(getTargetFormat()) || TransformerFactory.FORMAT.PDF.equals(getTargetFormat())) {

      Document xhtmlDoc = new Document("");
      xhtmlDoc.outputSettings().syntax(org.jsoup.nodes.Document.OutputSettings.Syntax.xml);
      xhtmlDoc.outputSettings().charset("UTF-8");
      citationList.stream().forEach(cit -> xhtmlDoc.body().appendElement("p").html(XsltHelper.convertSnippetToHtml(cit)));


      if (TransformerFactory.FORMAT.DOCX.equals(getTargetFormat())) {
        WordprocessingMLPackage wordOutputDoc = WordprocessingMLPackage.createPackage();
        MainDocumentPart mdp = wordOutputDoc.getMainDocumentPart();

        // Set global space after each paragrap
        PPr ppr = new PPr();
        PPrBase.Spacing spacing = new PPrBase.Spacing();
        spacing.setAfter(BigInteger.valueOf(400));
        ppr.setSpacing(spacing);
        mdp.getStyleDefinitionsPart().getDefaultParagraphStyle().setPPr(ppr);

        XHTMLImporter xhtmlImporter = new XHTMLImporterImpl(wordOutputDoc);
        List<Object> xhtmlObjects = xhtmlImporter.convert(xhtmlDoc.html(), null);
        mdp.getContent().addAll(xhtmlObjects);

        wordOutputDoc.save(res.getOutputStream());

      } else if (TransformerFactory.FORMAT.PDF.equals(getTargetFormat())) {
        generatePdfApacheFO(xhtmlDoc, res.getOutputStream());
      }
    } else {
      throw new IngeTechnicalException(
          "Format " + getTargetFormat() + " is not supported for citations. Please use one of the following formats: "
              + TransformerFactory.FORMAT.JSON_CITATION.getName() + ", " + TransformerFactory.FORMAT.ESCIDOC_SNIPPET.getName() + ", "
              + TransformerFactory.FORMAT.HTML_PLAIN.getName() + ", " + TransformerFactory.FORMAT.HTML_LINKED.getName() + ", "
              + TransformerFactory.FORMAT.PDF.getName() + ", " + TransformerFactory.FORMAT.DOCX.getName() + ", ");
    }

    //return null;
  }

  private static void generateHtmlOutput(String escidocSnippet, TransformerFactory.FORMAT fileFormat, String outputMethod, boolean indent,
      OutputStream os) throws Exception {
    //StringWriter sw = new StringWriter();
    javax.xml.transform.TransformerFactory factory = new TransformerFactoryImpl();
    javax.xml.transform.Transformer htmlTransformer = factory.newTransformer(new StreamSource(CitationTransformer.class.getClassLoader()
        .getResourceAsStream(PropertyReader.getProperty(PropertyReader.INGE_TRANSFORMATION_ESCIDOC_SNIPPET_TO_HTML_STYLESHEET_FILENAME))));

    htmlTransformer.setOutputProperty(OutputKeys.INDENT, indent ? "yes" : "no");
    htmlTransformer.setOutputProperty(OutputKeys.METHOD, outputMethod);

    String instanceUrl = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_URL);
    String pubmanUrl = instanceUrl + PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_CONTEXT_PATH);
    htmlTransformer.setParameter("instanceUrl", instanceUrl);
    htmlTransformer.setParameter("pubmanUrl", pubmanUrl);

    if (TransformerFactory.FORMAT.HTML_LINKED.equals(fileFormat)) {
      htmlTransformer.setParameter("html_linked", Boolean.TRUE);
    }

    htmlTransformer.transform(new StreamSource(new StringReader(escidocSnippet)), new StreamResult(os));

    //return sw.toString();
  }


  private void generateEsciDocSnippet(List<ItemVersionVO> itemList, List<String> citationList, Integer numberOfRecords, OutputStream os)
      throws TechnicalException, TransformerException {
    List<PubItemVO> transformedList = EntityTransformer.transformToOld(itemList);
    ItemVOListWrapper listWrapper = new ItemVOListWrapper();
    listWrapper.setItemVOList(transformedList);

    if (null != numberOfRecords) {
      listWrapper.setNumberOfRecords(String.valueOf(numberOfRecords));
    }

    String escidocItemList = XmlTransformingService.transformToItemList(listWrapper);

    //StringWriter escidocSnippetWriter = new StringWriter();
    javax.xml.transform.TransformerFactory factory = new TransformerFactoryImpl();
    javax.xml.transform.Transformer transformer =
        factory.newTransformer(new StreamSource(CitationTransformer.class.getClassLoader().getResourceAsStream(
            PropertyReader.getProperty(PropertyReader.INGE_TRANSFORMATION_ESCIDOC_ITEMLIST_TO_SNIPPET_STYLESHEET_FILENAME))));
    transformer.setParameter("citations", citationList);
    transformer.transform(new StreamSource(new StringReader(escidocItemList)), new StreamResult(os));

    //return escidocSnippetWriter.toString();
  }

  public void xmlSourceToXmlResult(Source s, Result r) throws TransformerException {
    TransformerFactoryImpl xslTransformerFactory = new net.sf.saxon.TransformerFactoryImpl();
    Transformer t = xslTransformerFactory.newTransformer();
    t.setOutputProperty(OutputKeys.INDENT, "yes");
    t.setOutputProperty(OutputKeys.METHOD, "xml");
    t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    t.transform(s, r);
  }



  private static FopFactory instantiateFopFactory() {
    String fopconfig = """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <fop version="1.0">
                <strict-configuration>true</strict-configuration>
                <renderers>
                    <renderer mime="application/pdf">                  
                        <fonts>
                           <!-- Default Latin fonts in different styles -->
                           <font simulate-style="false" embed-url="{{embed_regular_font_url}}">
                                <font-triplet name="Noto Serif" style="normal" weight="normal"/>
                            </font>
                            <font simulate-style="false" embed-url="{{embed_italic_font_url}}">
                                <font-triplet name="Noto Serif" style="italic" weight="normal"/>
                            </font>
                            <font simulate-style="false" embed-url="{{embed_bold_font_url}}">
                                <font-triplet name="Noto Serif" style="normal" weight="bold"/>
                            </font>
                            <font simulate-style="false" embed-url="{{embed_bold_italic_font_url}}">
                                <font-triplet name="Noto Serif" style="italic" weight="bold"/>
                            </font>
                            <!-- Unicode Font containing many non-latin characters, see Go Noto Universal https://github.com/satbyy/go-noto-universal -->
                            <font simulate-style="true" embed-url="{{embed_unicode_regular_font_url}}">
                                <font-triplet name="Go Noto" style="normal" weight="normal"/>
                                <font-triplet name="Go Noto" style="italic" weight="normal"/>
                            </font>
                            <font simulate-style="true" embed-url="{{embed_unicode_bold_font_url}}">
                                <font-triplet name="Go Noto" style="normal" weight="bold"/>
                                <font-triplet name="Go Noto" style="italic" weight="bold"/>
                            </font>
                            <!-- Math font containing math symbols -->
                             <font simulate-style="true" embed-url="{{embed_math_font_url}}">
                                <font-triplet name="Noto Math" style="normal" weight="normal"/>
                                <font-triplet name="Noto Math" style="italic" weight="normal"/>
                                <font-triplet name="Noto Math" style="normal" weight="bold"/>
                                <font-triplet name="Noto Math" style="italic" weight="bold"/>
                            </font>
                            
                        </fonts>
                        
                    </renderer>
                </renderers>
            </fop>
            """;

    try {
      fopconfig = fopconfig.replace("{{embed_regular_font_url}}", CitationTransformer.class.getClassLoader().getResource("fonts/NotoSerif-Regular.ttf").toURI().toString());
      fopconfig = fopconfig.replace("{{embed_italic_font_url}}", CitationTransformer.class.getClassLoader().getResource("fonts/NotoSerif-Italic.ttf").toURI().toString());
      fopconfig = fopconfig.replace("{{embed_bold_font_url}}", CitationTransformer.class.getClassLoader().getResource("fonts/NotoSerif-Bold.ttf").toURI().toString());
      fopconfig = fopconfig.replace("{{embed_bold_italic_font_url}}", CitationTransformer.class.getClassLoader().getResource("fonts/NotoSerif-BoldItalic.ttf").toURI().toString());
      fopconfig = fopconfig.replace("{{embed_unicode_regular_font_url}}", CitationTransformer.class.getClassLoader().getResource("fonts/GoNotoKurrent-Regular.ttf").toURI().toString());
      fopconfig = fopconfig.replace("{{embed_unicode_bold_font_url}}", CitationTransformer.class.getClassLoader().getResource("fonts/GoNotoKurrent-Bold.ttf").toURI().toString());
      fopconfig = fopconfig.replace("{{embed_math_font_url}}", CitationTransformer.class.getClassLoader().getResource("fonts/NotoSansMath-Regular.ttf").toURI().toString());

      //FopFactoryBuilder fopFactoryBuilder = new FopFactoryBuilder(new URI("file:" + System.getProperty("java.io.tmpdir") + "/fop"));
      FopFactoryBuilder fopFactoryBuilder = new FopFactoryBuilder(new URI("."));
      DefaultConfigurationBuilder configurationBuilder = new DefaultConfigurationBuilder();
      DefaultConfiguration fopConfig = configurationBuilder.build(new ByteArrayInputStream(fopconfig.getBytes()));
      fopFactoryBuilder.setConfiguration(fopConfig);

      FopFactory fopFactory = fopFactoryBuilder.build();
      return fopFactory;

    } catch (Exception e) {
      logger.error("Error instantiating FOP Factory for PDF creation", e);
    }
    return null;
  }



  private static void generatePdfApacheFO(Document xhtmlDoc, OutputStream out) {

    try (out) {
      Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);
      //Set html namespace, otherwise the stylesheet does not work
      xhtmlDoc.firstElementChild().attr("xmlns", "http://www.w3.org/1999/xhtml");
      //Set fonts to the fonts set in the FOP configuration
      xhtmlDoc.body().attr("style", "font-family: Noto Serif, Go Noto, Noto Math; font-size: 11px;");

      Source xmlSource = new StreamSource(new StringReader(xhtmlDoc.html()));
      Source xsltSource = new StreamSource(ResourceUtil.getResourceAsStream("transformations/xhtml2fo.xsl", CitationTransformer.class.getClassLoader()));

      javax.xml.transform.TransformerFactory transformerFactory = javax.xml.transform.TransformerFactory.newInstance();
      Transformer t = transformerFactory.newTransformer(xsltSource);
      SAXResult result = new SAXResult(fop.getDefaultHandler());

      t.transform(xmlSource, result);
    } catch (Exception e) {
      logger.error("Error generating PDF", e);
    }

  }


  public static void main(String[] args) throws Exception {

    String citation =
        "Tester, M. <span class=\"DisplayDateStatus\" style=\"font-weight: bold;\">(2000).</span> <span style=\"font-style: italic;\">Book with <u>special</u> char É and <sup>high</sup> and <sub>low</sub> and &lt;person>&lt;/person> and ψ( → |+ l−) and <b>宅中图</b> <i>大朱元</i>璋与南京营造漢詩 ㅏ ㅑ ㅓ ㅕ ㅗ ㅛ ㅜ ㅠ ㅡ ㅣ모든인간 and much of some other things to keep a very long title</span>.";


    String citation2 = "Schlienz, H., Beckendorf, M., Katter, U. J., Risse, T., & Freund, H.-J. (1995). Electron "
        + "<i>Spin Resonance Investigations of the Molecular Motion of NO<sub>2</sub> on Al<sup>2O3(111)</sup> under "
        + "Ultrahigh Vacuum Conditions.</i>" + "Physical Review Letters, <i>74</i>(5), 761-764. doi:10.1103/" + "PhysRevLett.74.761.";
    String basePath = System.getProperty("user.dir");

    Path pdfFilePath = Paths.get(basePath, "output.pdf");
    Files.deleteIfExists(pdfFilePath);
    Path pdfFile = Files.createFile(pdfFilePath);

    Path wordFilePath = Paths.get(basePath, "output.docx");
    Files.deleteIfExists(wordFilePath);
    Path wordFile = Files.createFile(wordFilePath);

    Path fopFilePath = Paths.get(basePath, "output_fop.xml");
    Files.deleteIfExists(fopFilePath);
    Path fopFile = Files.createFile(fopFilePath);

    Path fopConfigFilePath = Paths.get(basePath, "output_fop_config.xml");
    Files.deleteIfExists(fopConfigFilePath);
    Path fopConfigFile = Files.createFile(fopConfigFilePath);

    //ImportXHTMLProperties.setProperty("docx4j-ImportXHTML.fonts.default.serif", "");
    //ImportXHTMLProperties.setProperty("docx4j-ImportXHTML.fonts.default.sans-serif", "");
    //ImportXHTMLProperties.setProperty("docx4j-ImportXHTML.fonts.default.monospace", "");

    Document xhtmlDoc = new Document("");
    xhtmlDoc.outputSettings().syntax(org.jsoup.nodes.Document.OutputSettings.Syntax.xml);
    xhtmlDoc.outputSettings().charset("UTF-8");

    //xhtmlDoc.attr("xmlns", "http://www.w3.org/1999/xhtml");
    for (int i = 0; i < 2; i++) {
      xhtmlDoc.body().appendElement("p").html(citation);
    }
    xhtmlDoc.body().appendElement("p").html(citation2);
    xhtmlDoc.firstElementChild().attr("xmlns", "http://www.w3.org/1999/xhtml");
    //xhtmlDoc.body().attr("style","font-family: Go Noto;").html(citation);
    System.out.println(xhtmlDoc.toString());
    //xhtmlDoc.body().attr("style", "font-family: Go Noto;");


    /*
    
    WordprocessingMLPackage wordOutputDoc = WordprocessingMLPackage.createPackage();
    
    MainDocumentPart mdp = wordOutputDoc.getMainDocumentPart();
    
    // Set global space after each paragrap
    PPr ppr = new PPr();
    PPrBase.Spacing spacing = new PPrBase.Spacing();
    spacing.setAfter(BigInteger.valueOf(400));
    ppr.setSpacing(spacing);
    mdp.getStyleDefinitionsPart().getDefaultParagraphStyle().setPPr(ppr);
    
    
    
    XHTMLImporterImpl xhtmlImporter = new XHTMLImporterImpl(wordOutputDoc);
    RFonts rfontMapping = new RFonts();
    rfontMapping.setAscii("Go Noto");
    rfontMapping.setHAnsi("Go Noto");
    rfontMapping.setEastAsia("Go Noto");
    rfontMapping.setCs("Go Noto");
    xhtmlImporter.addFontMapping("serif", rfontMapping);
    xhtmlImporter.addFontMapping("sans-serif", rfontMapping);
    xhtmlImporter.addFontMapping("monospace", rfontMapping);
    List<Object> xhtmlObjects = xhtmlImporter.convert(xhtmlDoc.html(), null);
    
    mdp.getContent().addAll(xhtmlObjects);
    
    
    
    //ByteArrayOutputStream bos = new ByteArrayOutputStream();
    
    //Docx4jProperties.setProperty("docx4j.fonts.fop.util.FopConfigUtil.simulate-style", true);
    PhysicalFonts.setRegex("useOnlyLocalFonts");
    
    PhysicalFonts.addPhysicalFont(new URI("file:/Users/haarlae1/Downloads/fonts/NotoSans-Regular.ttf"));
    PhysicalFonts.addPhysicalFont(new URI("file:/Users/haarlae1/Downloads/fonts/NotoSans-Bold.ttf"));
    PhysicalFonts.addPhysicalFont(new URI("file:/Users/haarlae1/Downloads/fonts/NotoSans-Italic.ttf"));
    PhysicalFonts.addPhysicalFont(new URI("file:/Users/haarlae1/Downloads/fonts/NotoSans-BoldItalic.ttf"));
    
    PhysicalFonts.addPhysicalFont(new URI("file:/Users/haarlae1/Downloads/fonts/NotoSansSC-Bold.otf"));
    PhysicalFonts.addPhysicalFont(new URI("file:/Users/haarlae1/Downloads/fonts/NotoSansSC-Regular.otf"));
    
    
    
    PhysicalFonts.addPhysicalFont(
        ResourceUtil.getResourceAsFile("fonts/GoNotoKurrent-Regular.ttf", CitationTransformer.class.getClassLoader()).toURI());
    PhysicalFonts.addPhysicalFont(
        ResourceUtil.getResourceAsFile("fonts/GoNotoKurrent-Bold.ttf", CitationTransformer.class.getClassLoader()).toURI());
    
    
    //PhysicalFonts.addPhysicalFont(new URI("file:/Users/haarlae1/Downloads/Noto_Sans,Roboto/Noto_Sans/NotoSans-Italic-VariableFont_wdth,wght.ttf"));
    //PhysicalFonts.addPhysicalFont(new URI("file:/Users/haarlae1/Downloads/NotoSansCJKsc-VF.ttf"));
    
    Mapper fontMapper = new IdentityPlusMapper();
    fontMapper.registerRegularForm("Go Noto", PhysicalFonts.get("Go Noto Kurrent-Regular Regular"));
    fontMapper.registerBoldForm("Go Noto", PhysicalFonts.get("Go Noto Kurrent-Bold Bold"));
    
    //fontMapper.put("Arial", PhysicalFonts.get("Arial Unicode MS"));
    //fontMapper.registerRegularForm("Noto Sans", PhysicalFonts.get("Noto Sans Regular"));
    //fontMapper.registerBoldForm("Noto Sans", PhysicalFonts.get("Noto Sans Bold"));
    //fontMapper.registerItalicForm("Noto Sans", PhysicalFonts.get("Noto Sans Italic"));
    //fontMapper.registerBoldItalicForm("Noto Sans", PhysicalFonts.get("Noto Sans Bold Italic"));
    
    //fontMapper.registerBoldForm("Noto Sans SC", PhysicalFonts.get("Noto Sans SC Bold"));
    //fontMapper.registerRegularForm("Noto Sans SC", PhysicalFonts.get("Noto Sans SC"));
    
    wordOutputDoc.setFontMapper(fontMapper);
    
    
    
    Source xmlSource = new StreamSource(new StringReader(xhtmlDoc.html()));
    
    Source xsltSource =
        new StreamSource(ResourceUtil.getResourceAsStream("transformations/xhtml2fo.xsl", CitationTransformer.class.getClassLoader()));
    
    javax.xml.transform.TransformerFactory transformerFactory = javax.xml.transform.TransformerFactory.newInstance();
    Transformer t = transformerFactory.newTransformer(xsltSource);
    
    t.setOutputProperty(OutputKeys.INDENT, "yes");
    t.setOutputProperty(OutputKeys.METHOD, "xml");
    t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    
    StringWriter swr = new StringWriter();
    StreamResult result = new StreamResult(swr);
    t.transform(xmlSource, result);
    
    System.out.println(xhtmlDoc.html());
    System.out.println("XSLT transformation completed successfully.");
    System.out.println(swr.toString());
    
    
    
    FOSettings foSettings = new FOSettings(wordOutputDoc);
    foSettings.setApacheFopMime(FOSettings.INTERNAL_FO_MIME);
    
    
    
    try {
      JAXBContext context = JAXBContext.newInstance(Fop.class);
      Marshaller m = context.createMarshaller();
    
      m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE); // To format XML
    
      StringWriter sw = new StringWriter();
      m.marshal(foSettings.getFopConfig(), new FileWriter(fopConfigFile.toFile()));
    
    } catch (JAXBException e) {
      e.printStackTrace();
    }
    
    Docx4J.toFO(foSettings, Files.newOutputStream(fopFile), Docx4J.FLAG_EXPORT_PREFER_XSL);
    
    
    Docx4J.toFO(foSettings, Files.newOutputStream(pdfFile), Docx4J.FLAG_EXPORT_PREFER_XSL);
    
    Docx4J.toPDF(wordOutputDoc, Files.newOutputStream(pdfFile));
    
    wordOutputDoc.save(wordFilePath.toFile());
    
    */
    generatePdfApacheFO(xhtmlDoc, Files.newOutputStream(pdfFile));


  }



}
