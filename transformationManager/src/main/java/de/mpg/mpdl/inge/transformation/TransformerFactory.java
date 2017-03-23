package de.mpg.mpdl.inge.transformation;

import java.io.FileInputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.reflections.Reflections;

import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.transformation.results.TransformerStreamResult;
import de.mpg.mpdl.inge.transformation.sources.TransformerStreamSource;

public class TransformerFactory {

  private static Logger logger = Logger.getLogger(TransformerFactory.class);


  public enum FORMAT {

    BMC_XML, ENDNOTE_STRING, ENDNOTE_XML, BIBTEX_STRING, MARC_21_STRING, MARC_XML, ESCIDOC_ITEM_V3_XML, ESCIDOC_ITEM_V2_XML, ESCIDOC_ITEM_V1_XML, ESCIDOC_ITEMLIST_V1_XML, ESCIDOC_ITEMLIST_V2_XML, ESCIDOC_ITEMLIST_V3_XML, ESCIDOC_COMPONENT_XML, ESCIDOC_ITEM_VO, COINS_STRING, DOI_METADATA_XML, ZIM_XML, EDOC_XML, MAB_STRING, MAB_XML, RIS_STRING, RIS_XML, WOS_STRING, WOS_XML, JUS_SNIPPET_XML, JUS_INDESIGN_XML, JUS_HTML_XML, DC_XML, HTML_METATAGS_DC_XML, HTML_METATAGS_HIGHWIRE_PRESS_CIT_XML, OAI_DC, MODS_XML, PEER_TEI_XML, ZFN_TEI_XML, ARXIV_OAIPMH_XML, BMC_OAIPMH_XML, PMC_OAIPMH_XML, SPIRES_XML;

  }

  static Transformer newInstance(FORMAT sourceFormat, FORMAT targetFormat)
      throws TransformationException {

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
    } else if (edges.size() == 1) {

      try {
        TransformerEdge edge = edges.get(0);
        Transformer t = edge.getTransformerClass().newInstance();
        t.setSourceFormat(edge.getSourceFormat());
        t.setTargetFormat(edge.getTargetFormat());
        return t;
      } catch (Exception e) {
        throw new TransformationException("Could not initialize transformer.", e);
      }
    } else {
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
  }

  static FORMAT[] getAllTargetFormatsFor(FORMAT sourceFormat) {

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

  static FORMAT[] getAllSourceFormatsFor(FORMAT targetFormat) {

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


  public static void main(String[] arg) {

    try {



      Transformer t =
          TransformerFactory.newInstance(FORMAT.ESCIDOC_ITEM_V3_XML, FORMAT.ESCIDOC_ITEM_V1_XML);
      StringWriter wr = new StringWriter();

      t.transform(new TransformerStreamSource(new FileInputStream(
          "C:\\Users\\haarlae1\\Downloads\\export_escidoc_xml_v13.xml")),
          new TransformerStreamResult(wr));
      System.out.println(wr.toString());

    } catch (Exception e) {
      logger.error("error", e);
      e.printStackTrace();
    }
  }

}
