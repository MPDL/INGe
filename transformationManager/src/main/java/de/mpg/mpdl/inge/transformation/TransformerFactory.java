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
  private static Logger logger = Logger.getLogger(TransformerFactory.class);

  public enum FORMAT {
    ARXIV_OAIPMH_XML("ArXiv", FileFormatVO.XML_MIMETYPE, "UTF-8"), //
    BIBTEX_STRING("BibTex", FileFormatVO.TEXT_MIMETYPE, "UTF-8"), //
    BMC_XML("Bmc", FileFormatVO.XML_MIMETYPE, "UTF-8"), //
    BMC_OAIPMH_XML("Bmc_Oaipmh", FileFormatVO.XML_MIMETYPE, "UTF-8"), //
    BMC_FULLTEXT_XML("Bmc_Fulltext", FileFormatVO.XML_MIMETYPE, "UTF-8"), //
    BMC_FULLTEXT_HTML("Bmc_Fulltext", FileFormatVO.HTML_PLAIN_MIMETYPE, "UTF-8"), //
    COINS_STRING("Coins", FileFormatVO.TEXT_MIMETYPE, "UTF-8"), //
    DOI_METADATA_XML("Doi", FileFormatVO.XML_MIMETYPE, "UTF-8"), //
    EDOC_XML("Edoc", FileFormatVO.XML_MIMETYPE, "UTF-8"), //
    ENDNOTE_STRING("EndNote", FileFormatVO.TEXT_MIMETYPE, "UTF-8"), //
    ENDNOTE_XML("EndNote", FileFormatVO.XML_MIMETYPE, "UTF-8"), //
    ESCIDOC_ITEM_V3_XML("eSciDoc-publication-item", FileFormatVO.XML_MIMETYPE, "UTF-8"), //
    ESCIDOC_ITEM_V2_XML("eSciDoc-publication-item-V2", FileFormatVO.XML_MIMETYPE, "UTF-8"), //
    ESCIDOC_ITEM_V1_XML("eSciDoc-publication-item-V1", FileFormatVO.XML_MIMETYPE, "UTF-8"), //
    ESCIDOC_ITEMLIST_V1_XML("eSciDoc-publication-itemlist-V1", FileFormatVO.XML_MIMETYPE, "UTF-8"), //
    ESCIDOC_ITEMLIST_V2_XML("eSciDoc-publication-itemlist-V2", FileFormatVO.XML_MIMETYPE, "UTF-8"), //
    ESCIDOC_ITEMLIST_V3_XML("eSciDoc-publication-itemlist", FileFormatVO.XML_MIMETYPE, "UTF-8"), //
    ESCIDOC_COMPONENT_XML("eSciDoc-publication-component", FileFormatVO.XML_MIMETYPE, "UTF-8"), //
    ESCIDOC_ITEM_VO("eSciDoc-publication-itemVO", FileFormatVO.XML_MIMETYPE, "UTF-8"), //
    HTML_METATAGS_DC_XML("Html_Metatags_dc", FileFormatVO.XML_MIMETYPE, "UTF-8"), //
    HTML_METATAGS_HIGHWIRE_PRESS_CIT_XML("Html_Metatags_Highwirepress_Cit",
        FileFormatVO.XML_MIMETYPE, "UTF-8"), //
    JUS_SNIPPET_XML("Jus_Snippet", FileFormatVO.XML_MIMETYPE, "UTF-8"), //
    JUS_INDESIGN_XML("Jus_Indesign", FileFormatVO.XML_MIMETYPE, "UTF-8"), //
    JUS_HTML_XML("Jus_Html", FileFormatVO.XML_MIMETYPE, "UTF-8"), //
    DC_XML("Dc", FileFormatVO.XML_MIMETYPE, "UTF-8"), //
    MAB_STRING("Mab", FileFormatVO.TEXT_MIMETYPE, "UTF-8"), //
    MAB_XML("Mab", FileFormatVO.XML_MIMETYPE, "UTF-8"), //
    MARC_21_STRING("Marc21", FileFormatVO.TEXT_MIMETYPE, "UTF-8"), //
    MARC_XML("Marc", FileFormatVO.XML_MIMETYPE, "UTF-8"), //
    MODS_XML("Mods", FileFormatVO.XML_MIMETYPE, "UTF-8"), //
    OAI_DC("Oai_Dc", FileFormatVO.XML_MIMETYPE, "UTF-8"), //
    PDF("Pdf", FileFormatVO.PDF_MIMETYPE, "UTF-8"), //
    PS("Ps", FileFormatVO.PS_MIMETYPE, "UTF-8"), //
    PEER_TEI_XML("Peer", FileFormatVO.XML_MIMETYPE, "UTF-8"), //
    RIS_STRING("Ris", FileFormatVO.TEXT_MIMETYPE, "UTF-8"), //
    RIS_XML("Ris", FileFormatVO.XML_MIMETYPE, "UTF-8"), //
    PMC_OAIPMH_XML("Pmc_Oaipmh", FileFormatVO.XML_MIMETYPE, "UTF-8"), //
    SPIRES_XML("Spires", FileFormatVO.XML_MIMETYPE, "UTF-8"), //
    WOS_STRING("Wos", FileFormatVO.TEXT_MIMETYPE, "UTF-8"), //
    WOS_XML("Wos", FileFormatVO.XML_MIMETYPE, "UTF-8"), //
    ZFN_TEI_XML("Zfn", FileFormatVO.XML_MIMETYPE, "UTF-8"), //
    ZIM_XML("Zim", FileFormatVO.XML_MIMETYPE, "UTF-8");

    private final String name;
    private final String type;
    private final String encoding;

    FORMAT(String name, String type, String encoding) {
      this.name = name;
      this.type = type;
      this.encoding = encoding;
    }

    public String getName() {
      return this.name;
    }

    public String getType() {
      return this.type;
    }

    public String getEncoding() {
      return this.encoding;
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

  public static Transformer newInstance(FORMAT sourceFormat, FORMAT targetFormat)
      throws TransformationException {
    if (sourceFormat.equals(targetFormat)) {
      return new IdentityTransformer();
    }

    List<TransformerEdge> transformerEdges = new ArrayList<TransformerEdge>();

    Reflections refl = new Reflections("de.mpg.mpdl.inge.transformation.transformers");

    Set<Class<?>> transformerModuleClasses = refl.getTypesAnnotatedWith(TransformerModule.class);
    Set<Class<?>> transformerModulesClasses = refl.getTypesAnnotatedWith(TransformerModules.class);

    for (Class<?> t : transformerModuleClasses) {
      Class<Transformer> transformerClass = (Class<Transformer>) t;
      TransformerModule tm = transformerClass.getAnnotation(TransformerModule.class);

      transformerEdges.add(new TransformerEdge(transformerClass, tm.sourceFormat(), tm
          .targetFormat()));
    }

    for (Class<?> t : transformerModulesClasses) {
      Class<Transformer> transformerClass = (Class<Transformer>) t;
      TransformerModules tms = transformerClass.getAnnotation(TransformerModules.class);
      for (TransformerModule tm : tms.value()) {
        transformerEdges.add(new TransformerEdge(transformerClass, tm.sourceFormat(), tm
            .targetFormat()));
      }
    }

    DijkstraAlgorithm da = new DijkstraAlgorithm(Arrays.asList(FORMAT.values()), transformerEdges);
    da.execute(sourceFormat);

    List<TransformerEdge> edges = da.getPath(targetFormat);

    if (edges == null || edges.size() == 0) {
      throw new TransformationException("No transformation chain found for " + sourceFormat
          + " --> " + targetFormat);
    }

    if (edges.size() == 1) {
      try {
        TransformerEdge edge = edges.get(0);
        Transformer t = edge.getTransformerClass().newInstance();
        t.setSourceFormat(edge.getSourceFormat());
        t.setTargetFormat(edge.getTargetFormat());

        return t;
      } catch (Exception e) {
        throw new TransformationException("Could not initialize transformer.", e);
      }
    }

    try {
      ChainTransformer chainTransformer = new ChainTransformer();
      List<ChainableTransformer> tList = new ArrayList<ChainableTransformer>();
      chainTransformer.setTransformerChain(tList);

      for (TransformerEdge edge : edges) {
        Transformer ct = edge.getTransformerClass().newInstance();
        ct.setSourceFormat(edge.getSourceFormat());
        ct.setTargetFormat(edge.getTargetFormat());
        tList.add((ChainableTransformer) ct);
      }

      return chainTransformer;
    } catch (Exception e) {
      throw new TransformationException("Could not initialize transformer.", e);
    }
  }

  public static FORMAT[] getAllTargetFormatsFor(FORMAT sourceFormat) {
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

  public static FORMAT[] getAllSourceFormatsFor(FORMAT targetFormat) {
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
}
