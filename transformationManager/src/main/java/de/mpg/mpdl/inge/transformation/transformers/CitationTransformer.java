package de.mpg.mpdl.inge.transformation.transformers;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.docx4j.Docx4J;
import org.docx4j.convert.in.xhtml.XHTMLImporter;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.PPrBase.Spacing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.mpg.mpdl.inge.citationmanager.CitationStyleExecuterService;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.util.EntityTransformer;
import de.mpg.mpdl.inge.model.util.MapperFactory;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
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
import net.sf.saxon.TransformerFactoryImpl;

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

  @Override
  public void transform(TransformerSource source, TransformerResult result) throws TransformationException {
    try {
      SearchRetrieveResponseVO<ItemVersionVO> s = (SearchRetrieveResponseVO<ItemVersionVO>) ((TransformerVoSource) source).getSource();

      List<ItemVersionVO> itemList = s.getRecords().stream().map(i -> i.getData()).collect(Collectors.toList());
      ExportFormatVO exportFormat = new ExportFormatVO(getTargetFormat().getName(), getConfiguration().get(CONFIGURATION_CITATION),
          getConfiguration().get(CONFIGURATION_CSL_ID));

      List<String> citationList = CitationStyleExecuterService.getOutput(itemList, exportFormat);


      byte[] content = integrateCitationIntoOutput(exportFormat, s, itemList, citationList);


      writeByteArrayToStreamResult(content, (TransformerStreamResult) result);
    } catch (Exception e) {
      logger.error(e);
      throw new TransformationException("Error while citation transformation", e);
    }

  }

  @Override
  public TransformerResult createNewInBetweenResult() {
    return null;
  }


  private byte[] integrateCitationIntoOutput(ExportFormatVO exportFormat, SearchRetrieveResponseVO<ItemVersionVO> searchResult,
      List<ItemVersionVO> itemList, List<String> citationList) throws Exception {

    if (itemList.size() != citationList.size()) {
      throw new IngeTechnicalException(
          "Error: Citationmanager returned " + citationList.size() + "citations for " + itemList.size() + " items");
    }

    List<PubItemVO> transformedList = EntityTransformer.transformToOld(itemList);
    ItemVOListWrapper listWrapper = new ItemVOListWrapper();
    listWrapper.setItemVOList(transformedList);

    if (searchResult != null) {
      listWrapper.setNumberOfRecords(String.valueOf(searchResult.getNumberOfRecords()));
    }

    String escidocItemList = XmlTransformingService.transformToItemList(listWrapper);

    StringWriter escidocSnippetWriter = new StringWriter();
    javax.xml.transform.TransformerFactory factory = new TransformerFactoryImpl();
    javax.xml.transform.Transformer transformer =
        factory.newTransformer(new StreamSource(CitationTransformer.class.getClassLoader().getResourceAsStream(
            PropertyReader.getProperty(PropertyReader.INGE_TRANSFORMATION_ESCIDOC_ITEMLIST_TO_SNIPPET_STYLESHEET_FILENAME))));
    transformer.setParameter("citations", citationList);
    transformer.transform(new StreamSource(new StringReader(escidocItemList)), new StreamResult(escidocSnippetWriter));

    String escidocSnippet = escidocSnippetWriter.toString();

    if (TransformerFactory.FORMAT.JSON_CITATION.equals(getTargetFormat())) {
      if (searchResult != null) {
        JsonNode node = MapperFactory.getObjectMapper().valueToTree(searchResult);
        if (searchResult.getRecords() != null && !searchResult.getRecords().isEmpty()) {
          int i = 0;
          for (JsonNode itemNode : node.get("records").findValues("data")) {
            ((ObjectNode) itemNode).put("bibliographicCitation", citationList.get(i));
            i++;
          }
        }

        return MapperFactory.getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(node).getBytes(StandardCharsets.UTF_8);
      }
    } else if (TransformerFactory.FORMAT.ESCIDOC_SNIPPET.equals(getTargetFormat())) {
      return escidocSnippet.getBytes(StandardCharsets.UTF_8);
    } else if (TransformerFactory.FORMAT.HTML_PLAIN.equals(getTargetFormat())
        || TransformerFactory.FORMAT.HTML_LINKED.equals(getTargetFormat())) {
      return generateHtmlOutput(escidocSnippet, getTargetFormat(), "html", true).getBytes(StandardCharsets.UTF_8);
    } else if (TransformerFactory.FORMAT.DOCX.equals(getTargetFormat()) || TransformerFactory.FORMAT.PDF.equals(getTargetFormat())) {
      String htmlResult = generateHtmlOutput(escidocSnippet, TransformerFactory.FORMAT.HTML_PLAIN, "xhtml", false);
      logger.info(htmlResult);
      WordprocessingMLPackage wordOutputDoc = WordprocessingMLPackage.createPackage();

      //      // TODO: Viel schöner machen!
      //      if (TransformerFactory.FORMAT.PDF.equals(getTargetFormat())) {
      //        for (Entry<String, PhysicalFont> entry : PhysicalFonts.getPhysicalFonts().entrySet()) {
      //          System.out.println(entry);
      //        }
      //
      //        PhysicalFont font = PhysicalFonts.getPhysicalFonts().get("liberation serif");
      //        if (font != null) {
      //          Mapper fontMapper = new IdentityPlusMapper();
      //          wordOutputDoc.setFontMapper(fontMapper, true);
      //
      //          for (Entry<String, PhysicalFont> entry : fontMapper.getFontMappings().entrySet()) {
      //            System.out.println(entry);
      //          }
      //
      //          fontMapper.getFontMappings().put("calibri", font);
      //          fontMapper.getFontMappings().put("times new roman", font);
      //
      //          for (Entry<String, PhysicalFont> entry : fontMapper.getFontMappings().entrySet()) {
      //            System.out.println(entry);
      //          }
      //
      //          wordOutputDoc.setFontMapper(fontMapper, true);
      //        }
      //      }

      XHTMLImporter xhtmlImporter = new XHTMLImporterImpl(wordOutputDoc);
      MainDocumentPart mdp = wordOutputDoc.getMainDocumentPart();

      List<Object> xhtmlObjects = xhtmlImporter.convert(htmlResult, null);

      // Remove line-height information for every paragraph
      for (Object xhtmlObject : xhtmlObjects) {
        P paragraph = (P) xhtmlObject;
        paragraph.getPPr().setSpacing(null);
      }

      mdp.getContent().addAll(xhtmlObjects);

      // Set global space after each paragrap
      PPr ppr = new PPr();
      Spacing spacing = new Spacing();
      spacing.setAfter(BigInteger.valueOf(400));
      ppr.setSpacing(spacing);
      mdp.getStyleDefinitionsPart().getDefaultParagraphStyle().setPPr(ppr);;

      ByteArrayOutputStream bos = new ByteArrayOutputStream();

      if (TransformerFactory.FORMAT.DOCX.equals(getTargetFormat())) {
        wordOutputDoc.save(bos);
      } else if (TransformerFactory.FORMAT.PDF.equals(getTargetFormat())) {
        //FOSettings foSettings = Docx4J.createFOSettings();
        //foSettings.setWmlPackage(wordOutputDoc);
        Docx4J.toPDF(wordOutputDoc, bos);
      }

      bos.flush();
      return bos.toByteArray();
    } else {
      throw new IngeTechnicalException(
          "Format " + getTargetFormat() + " is not supported for citations. Please use one of the following formats: "
              + TransformerFactory.FORMAT.JSON_CITATION.getName() + ", " + TransformerFactory.FORMAT.ESCIDOC_SNIPPET.getName() + ", "
              + TransformerFactory.FORMAT.HTML_PLAIN.getName() + ", " + TransformerFactory.FORMAT.HTML_LINKED.getName() + ", "
              + TransformerFactory.FORMAT.PDF.getName() + ", " + TransformerFactory.FORMAT.DOCX.getName() + ", ");
    }

    return null;
  }

  private static String generateHtmlOutput(String escidocSnippet, TransformerFactory.FORMAT fileFormat, String outputMethod, boolean indent)
      throws Exception {
    StringWriter sw = new StringWriter();
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

    htmlTransformer.transform(new StreamSource(new StringReader(escidocSnippet)), new StreamResult(sw));

    return sw.toString();
  }

  public void xmlSourceToXmlResult(Source s, Result r) throws TransformationException, TransformerException {
    TransformerFactoryImpl xslTransformerFactory = new net.sf.saxon.TransformerFactoryImpl();
    Transformer t = xslTransformerFactory.newTransformer();
    t.setOutputProperty(OutputKeys.INDENT, "yes");
    t.setOutputProperty(OutputKeys.METHOD, "xml");
    t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    t.transform(s, r);
  }
}
