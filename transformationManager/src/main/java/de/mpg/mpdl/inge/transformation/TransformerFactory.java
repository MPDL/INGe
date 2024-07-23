package de.mpg.mpdl.inge.transformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.reflections.Reflections;

import de.mpg.mpdl.inge.model.valueobjects.FileFormatVO;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.transformation.transformers.IdentityTransformer;

public class TransformerFactory {
  private static final Logger logger = Logger.getLogger(TransformerFactory.class);

  public static final String ARXIV = "arXiv";
  public static final String BIBTEX = "BibTeX";
  public static final String BMC_FULLTEXT_HTML = "BMC_Fulltext_Html";
  public static final String BMC_FULLTEXT_XML = "BMC_Fulltext_Xml";
  public static final String BMC_OAIPMH_XML = "BMC_Oaipmh_Xml";
  public static final String BMC_XML = "BMC_Xml";
  public static final String COINS = "Coins";
  public static final String CROSSREF = "Crossref";
  public static final String DC_XML = "Dc_Xml";
  public static final String DOCX = "docx";
  public static final String DOI_XML = "Doi_Xml";
  public static final String EDOC_XML = "eDoc_Xml";
  public static final String ENDNOTE = "EndNote";
  public static final String ENDNOTE_XML = "EndNote_Xml";
  public static final String ESCIDOC_COMPONENT_XML = "eSciDoc_Component_Xml";
  public static final String ESCIDOC_ITEMLIST_V1_XML = "eSciDoc_Itemlist_V1_Xml";
  public static final String ESCIDOC_ITEMLIST_V2_XML = "eSciDoc_Itemlist_V2_Xml";
  public static final String ESCIDOC_ITEMLIST_XML = "eSciDoc_Itemlist_Xml";
  public static final String ESCIDOC_ITEM_V1_XML = "eSciDoc_Item_V1_Xml";
  public static final String ESCIDOC_ITEM_V2_XML = "eSciDoc_Item_V2_Xml";
  public static final String ESCIDOC_ITEM_VO = "eSciDoc_Item_Vo";
  public static final String ESCIDOC_ITEM_XML = "eSciDoc_Item_Xml";
  public static final String ESCIDOC_SNIPPET = "escidoc_snippet";
  public static final String HTML_METATAGS_DC_XML = "Html_Metatags_Dc_Xml";
  public static final String HTML_METATAGS_HIGHWIRE_PRESS_CIT_XML = "Html_Metatags_Highwirepress_Cit_Xml";
  public static final String HTML_PLAIN = "html_plain";
  public static final String HTML_LINKED = "html_linked";
  public static final String JSON = "json";
  public static final String JSON_CITATION = "json_citation";
  public static final String JUS_HTML_XML = "Jus_Html_Xml";
  public static final String JUS_INDESIGN_XML = "Jus_Indesign_Xml";
  public static final String JUS_SNIPPET_XML = "Jus_Snippet_Xml";
  public static final String MAB = "MAB";
  public static final String MAB_XML = "MAB_Xml";
  public static final String MARC_21 = "Marc21";
  public static final String MARC_XML = "Marc_Xml";
  public static final String MODS_XML = "Mods_Xml";
  public static final String OAI_DC = "Oai_Dc";
  public static final String PDF = "pdf";
  public static final String PEER_TEI_XML = "Peer_TeiI_Xml";
  public static final String PMC_OAIPMH_XML = "Pmc_Oaipmh_Xml";
  public static final String RIS = "RIS";
  public static final String RIS_XML = "RIS_Xml";
  public static final String SEARCH_RESULT_VO = "search_result_vo";
  public static final String SPIRES_XML = "Spires_Xml";
  public static final String WOS = "WOS";
  public static final String WOS_XML = "WOS_Xml";
  public static final String ZFN_TEI_XML = "ZfN_Tei_Xml";
  public static final String ZIM_XML = "Zim_Xml";

  public enum FORMAT
  {
    ARXIV_OAIPMH_XML(TransformerFactory.ARXIV, FileFormatVO.FILE_FORMAT.XML), //
    CROSSREF_XML(TransformerFactory.CROSSREF, FileFormatVO.FILE_FORMAT.XML), //
    BIBTEX_STRING(TransformerFactory.BIBTEX, FileFormatVO.FILE_FORMAT.TXT), //
    BMC_FULLTEXT_HTML(TransformerFactory.BMC_FULLTEXT_HTML, FileFormatVO.FILE_FORMAT.HTML_PLAIN), //
    BMC_FULLTEXT_XML(TransformerFactory.BMC_FULLTEXT_XML, FileFormatVO.FILE_FORMAT.XML), //
    BMC_OAIPMH_XML(TransformerFactory.BMC_OAIPMH_XML, FileFormatVO.FILE_FORMAT.XML), //
    BMC_XML(TransformerFactory.BMC_XML, FileFormatVO.FILE_FORMAT.XML), //
    COINS_STRING(TransformerFactory.COINS, FileFormatVO.FILE_FORMAT.TXT), //
    DC_XML(TransformerFactory.DC_XML, FileFormatVO.FILE_FORMAT.XML), //
    DOCX(TransformerFactory.DOCX, FileFormatVO.FILE_FORMAT.DOCX),
    ESCIDOC_SNIPPET(TransformerFactory.ESCIDOC_SNIPPET, FileFormatVO.FILE_FORMAT.ESCIDOC_SNIPPET),
    DOI_METADATA_XML(TransformerFactory.DOI_XML, FileFormatVO.FILE_FORMAT.XML), //
    EDOC_XML(TransformerFactory.EDOC_XML, FileFormatVO.FILE_FORMAT.XML), //
    ENDNOTE_STRING(TransformerFactory.ENDNOTE, FileFormatVO.FILE_FORMAT.TXT), //
    ENDNOTE_XML(TransformerFactory.ENDNOTE_XML, FileFormatVO.FILE_FORMAT.XML), //
    ESCIDOC_COMPONENT_XML(TransformerFactory.ESCIDOC_COMPONENT_XML, FileFormatVO.FILE_FORMAT.XML), //
    ESCIDOC_ITEMLIST_V1_XML(TransformerFactory.ESCIDOC_ITEMLIST_V1_XML, FileFormatVO.FILE_FORMAT.XML), //
    ESCIDOC_ITEMLIST_V2_XML(TransformerFactory.ESCIDOC_ITEMLIST_V2_XML, FileFormatVO.FILE_FORMAT.XML), //
    ESCIDOC_ITEMLIST_V3_XML(TransformerFactory.ESCIDOC_ITEMLIST_XML, FileFormatVO.FILE_FORMAT.XML), //
    ESCIDOC_ITEM_V1_XML(TransformerFactory.ESCIDOC_ITEM_V1_XML, FileFormatVO.FILE_FORMAT.XML), //
    ESCIDOC_ITEM_V2_XML(TransformerFactory.ESCIDOC_ITEM_V2_XML, FileFormatVO.FILE_FORMAT.XML), //
    ESCIDOC_ITEM_V3_XML(TransformerFactory.ESCIDOC_ITEM_XML, FileFormatVO.FILE_FORMAT.XML), //
    ESCIDOC_ITEM_VO(TransformerFactory.ESCIDOC_ITEM_VO, FileFormatVO.FILE_FORMAT.XML), //
    HTML_METATAGS_DC_XML(TransformerFactory.HTML_METATAGS_DC_XML, FileFormatVO.FILE_FORMAT.TXT), //
    HTML_METATAGS_HIGHWIRE_PRESS_CIT_XML(TransformerFactory.HTML_METATAGS_HIGHWIRE_PRESS_CIT_XML, FileFormatVO.FILE_FORMAT.TXT), //
    HTML_PLAIN(TransformerFactory.HTML_PLAIN, FileFormatVO.FILE_FORMAT.HTML_PLAIN), //
    HTML_LINKED(TransformerFactory.HTML_LINKED, FileFormatVO.FILE_FORMAT.HTML_LINKED), //
    JSON(TransformerFactory.JSON, FileFormatVO.FILE_FORMAT.JSON), //
    JSON_CITATION(TransformerFactory.JSON_CITATION, FileFormatVO.FILE_FORMAT.JSON), //
    JUS_HTML_XML(TransformerFactory.JUS_HTML_XML, FileFormatVO.FILE_FORMAT.XML), //
    JUS_INDESIGN_XML(TransformerFactory.JUS_INDESIGN_XML, FileFormatVO.FILE_FORMAT.XML), //
    JUS_SNIPPET_XML(TransformerFactory.JUS_SNIPPET_XML, FileFormatVO.FILE_FORMAT.XML), //
    MAB_STRING(TransformerFactory.MAB, FileFormatVO.FILE_FORMAT.TXT), //
    MAB_XML(TransformerFactory.MAB_XML, FileFormatVO.FILE_FORMAT.XML), //
    MARC_21_STRING(TransformerFactory.MARC_21, FileFormatVO.FILE_FORMAT.TXT), //
    MARC_XML(TransformerFactory.MARC_XML, FileFormatVO.FILE_FORMAT.XML), //
    MODS_XML(TransformerFactory.MODS_XML, FileFormatVO.FILE_FORMAT.XML), //
    OAI_DC(TransformerFactory.OAI_DC, FileFormatVO.FILE_FORMAT.XML), //
    PEER_TEI_XML(TransformerFactory.PEER_TEI_XML, FileFormatVO.FILE_FORMAT.XML), //
    PDF(TransformerFactory.PDF, FileFormatVO.FILE_FORMAT.PDF), //
    PMC_OAIPMH_XML(TransformerFactory.PMC_OAIPMH_XML, FileFormatVO.FILE_FORMAT.XML), //
    RIS_STRING(TransformerFactory.RIS, FileFormatVO.FILE_FORMAT.TXT), //
    RIS_XML(TransformerFactory.RIS_XML, FileFormatVO.FILE_FORMAT.XML), //
    SEARCH_RESULT_VO(TransformerFactory.SEARCH_RESULT_VO, FileFormatVO.FILE_FORMAT.XML), //
    SPIRES_XML(TransformerFactory.SPIRES_XML, FileFormatVO.FILE_FORMAT.XML), //
    WOS_STRING(TransformerFactory.WOS, FileFormatVO.FILE_FORMAT.TXT), //
    WOS_XML(TransformerFactory.WOS_XML, FileFormatVO.FILE_FORMAT.XML), //
    ZFN_TEI_XML(TransformerFactory.ZFN_TEI_XML, FileFormatVO.FILE_FORMAT.XML), //
    ZIM_XML(TransformerFactory.ZIM_XML, FileFormatVO.FILE_FORMAT.XML);

  private final String name;
  private final FileFormatVO.FILE_FORMAT fileFormat;

  FORMAT(String name, FileFormatVO.FILE_FORMAT fileFormat) {
      this.name = name;
      this.fileFormat = fileFormat;
    }

  public String getName() {
    return this.name;
  }

  public FileFormatVO.FILE_FORMAT getFileFormat() {
    return this.fileFormat;
  }

  }

  public final static List<FORMAT> VALID_CITATION_OUTPUT =
      Arrays.asList(FORMAT.JSON_CITATION, FORMAT.ESCIDOC_SNIPPET, FORMAT.HTML_PLAIN, FORMAT.HTML_LINKED, FORMAT.DOCX, FORMAT.PDF);

  public enum CitationTypes{

  APA("APA"),
    CSL("CSL"),
    APA_CJK("APA(CJK)"),
    APA6("APA6"),
    AJP("AJP"),
    JUS("JUS"),
    JUS_Report("JUS_Report"),
    GFZPUBLISTS("GFZPUBLISTS");

  private String citationName;

  CitationTypes(String name) {
      this.setCitationName(name);
    }

  public String getCitationName() {
    return citationName;
  }

  public void setCitationName(String citationName) {
    this.citationName = citationName;
  }

  }

  public static FORMAT getFormat(String formatName) {
    for (FORMAT format : FORMAT.values()) {
      if (format.getName().equalsIgnoreCase(formatName)) {
        return format;
      }
    }

    throw new IllegalArgumentException("Format " + formatName + " unknown");
  }

  public static FORMAT getInternalFormat() {
    return FORMAT.ESCIDOC_ITEM_V3_XML;
  }



  protected static List<TransformerEdge> getShortestPath(FORMAT sourceFormat, FORMAT targetFormat) throws TransformationException {

    List<TransformerEdge> transformerEdges = new ArrayList<TransformerEdge>();

    Reflections refl = new Reflections("de.mpg.mpdl.inge.transformation.transformers");

    Set<Class<?>> transformerModuleClasses = refl.getTypesAnnotatedWith(TransformerModule.class);
    Set<Class<?>> transformerModulesClasses = refl.getTypesAnnotatedWith(TransformerModules.class);

    for (Class<?> t : transformerModuleClasses) {
      Class<Transformer> transformerClass = (Class<Transformer>) t;
      TransformerModule tm = transformerClass.getAnnotation(TransformerModule.class);

      transformerEdges.add(new TransformerEdge(transformerClass, tm.sourceFormat(), tm.targetFormat()));
    }

    for (Class<?> t : transformerModulesClasses) {
      Class<Transformer> transformerClass = (Class<Transformer>) t;
      TransformerModules tms = transformerClass.getAnnotation(TransformerModules.class);
      for (TransformerModule tm : tms.value()) {
        transformerEdges.add(new TransformerEdge(transformerClass, tm.sourceFormat(), tm.targetFormat()));
      }
    }

    DijkstraAlgorithm da = new DijkstraAlgorithm(Arrays.asList(FORMAT.values()), transformerEdges);
    da.execute(sourceFormat);

    List<TransformerEdge> edges = da.getPath(targetFormat);

    return edges;
  }

  public static Transformer newTransformer(FORMAT sourceFormat, FORMAT targetFormat) throws TransformationException {

    logger.info("Trying to find a transformer from " + sourceFormat + " to " + targetFormat);

    if (sourceFormat.equals(targetFormat)) {
      Transformer t = new IdentityTransformer();
      logger.info("Found suitable transformer: " + t);
      return t;
    }

    List<TransformerEdge> edges = TransformerCache.getTransformerEdges(sourceFormat, targetFormat);

    if (edges == null || edges.size() == 0) {
      logger.info("No suitable transformer found");
      throw new TransformationException("No transformation chain found for " + sourceFormat + " --> " + targetFormat);
    }

    if (edges.size() == 1) {
      try {
        TransformerEdge edge = edges.get(0);
        Transformer t = edge.getTransformerClass().newInstance();
        t.setSourceFormat(edge.getSourceFormat());
        t.setTargetFormat(edge.getTargetFormat());
        logger.info("Found suitable transformer: " + t);

        return t;
      } catch (Exception e) {
        throw new TransformationException("Could not initialize transformer.", e);
      }
    }

    try {
      ChainTransformer chainTransformer = new ChainTransformer();
      List<ChainableTransformer> tList = new ArrayList<ChainableTransformer>();
      chainTransformer.setTransformerChain(tList);
      chainTransformer.setSourceFormat(sourceFormat);
      chainTransformer.setTargetFormat(targetFormat);

      for (TransformerEdge edge : edges) {
        Transformer ct = edge.getTransformerClass().newInstance();
        ct.setSourceFormat(edge.getSourceFormat());
        ct.setTargetFormat(edge.getTargetFormat());
        tList.add((ChainableTransformer) ct);
      }
      logger.info("Found suitable transformer: " + chainTransformer);
      return chainTransformer;
    } catch (Exception e) {
      throw new TransformationException("Could not initialize transformer.", e);
    }
  }



  public static TransformerFactory.FORMAT[] getAllTargetFormatsFor(TransformerFactory.FORMAT sourceFormat) {
    return TransformerCache.getAllTargetFormatsFor(sourceFormat);
  }

  protected static FORMAT[] findAllTargetFormats(FORMAT sourceFormat) {
    Set<FORMAT> targetFormats = new HashSet<FORMAT>();
    Reflections refl = new Reflections("de.mpg.mpdl.inge.transformation.transformers");

    Set<Class<?>> transformerModuleClasses = refl.getTypesAnnotatedWith(TransformerModule.class);
    Set<Class<?>> transformerModulesClasses = refl.getTypesAnnotatedWith(TransformerModules.class);

    for (Class<?> t : transformerModuleClasses) {
      Class<Transformer> transformerClass = (Class<Transformer>) t;
      TransformerModule tm = transformerClass.getAnnotation(TransformerModule.class);

      if (tm.sourceFormat() == sourceFormat) {
        if (logger.isDebugEnabled())
          logger.debug("Adding <" + tm.targetFormat());
        targetFormats.add(tm.targetFormat());
      }
    }

    for (Class<?> t : transformerModulesClasses) {
      Class<Transformer> transformerClass = (Class<Transformer>) t;

      TransformerModules tms = transformerClass.getAnnotation(TransformerModules.class);
      for (TransformerModule tm : tms.value()) {
        if (tm.sourceFormat() == sourceFormat) {
          if (logger.isDebugEnabled())
            logger.debug("Adding <" + tm.targetFormat());
          targetFormats.add(tm.targetFormat());
        }
      }
    }

    return (FORMAT[]) targetFormats.toArray(new FORMAT[targetFormats.size()]);
  }


  public static TransformerFactory.FORMAT[] getAllSourceFormatsFor(TransformerFactory.FORMAT targetFormat) {
    return TransformerCache.getAllSourceFormatsFor(targetFormat);

  }

  protected static FORMAT[] findAllSourceFormats(FORMAT targetFormat) {
    Set<FORMAT> sourceFormats = new HashSet<FORMAT>();
    Reflections refl = new Reflections("de.mpg.mpdl.inge.transformation.transformers");

    Set<Class<?>> transformerModuleClasses = refl.getTypesAnnotatedWith(TransformerModule.class);
    Set<Class<?>> transformerModulesClasses = refl.getTypesAnnotatedWith(TransformerModules.class);

    for (Class<?> t : transformerModuleClasses) {
      Class<Transformer> transformerClass = (Class<Transformer>) t;
      TransformerModule tm = transformerClass.getAnnotation(TransformerModule.class);

      if (tm.targetFormat() == targetFormat) {
        if (logger.isDebugEnabled())
          logger.debug("Adding <" + tm.sourceFormat());
        sourceFormats.add(tm.sourceFormat());
      }
    }

    for (Class<?> t : transformerModulesClasses) {
      Class<Transformer> transformerClass = (Class<Transformer>) t;

      TransformerModules tms = transformerClass.getAnnotation(TransformerModules.class);
      for (TransformerModule tm : tms.value()) {
        if (tm.targetFormat() == targetFormat) {
          if (logger.isDebugEnabled())
            logger.debug("Adding <" + tm.sourceFormat());
          sourceFormats.add(tm.sourceFormat());
        }
      }
    }

    return (FORMAT[]) sourceFormats.toArray(new FORMAT[sourceFormats.size()]);
  }

  public static boolean isTransformationExisting(FORMAT sourceFormat, FORMAT targetFormat) {
    return TransformerCache.isTransformationExisting(sourceFormat, targetFormat);

  }
}
