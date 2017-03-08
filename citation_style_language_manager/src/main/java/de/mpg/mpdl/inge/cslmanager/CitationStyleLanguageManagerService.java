package de.mpg.mpdl.inge.cslmanager;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.undercouch.citeproc.CSL;
import de.undercouch.citeproc.ItemDataProvider;
import de.undercouch.citeproc.output.Bibliography;
import net.sf.saxon.TransformerFactoryImpl;

/**
 * CitationStyleLanguageManagerDefaultImpl is used to generate a citation for an escidoc item or a
 * list of escidoc items
 * 
 * @author walter
 */
public class CitationStyleLanguageManagerService {
  private static final Logger logger = Logger.getLogger(CitationStyleLanguageManagerService.class);

  private static final String TRANSFORMATION_ITEM_LIST_2_SNIPPET = "itemList2snippet.xsl";
  private static final String CITATION_PROCESSOR_OUTPUT_FORMAT = "html";

  private static String citationStyle = null;

  public static byte[] getOutput(ExportFormatVO exportFormat, String itemList)
      throws CitationStyleLanguageException {
    List<String> citationList = new ArrayList<String>();
    StringWriter snippet = new StringWriter();
    byte[] result = null;
    try {
      ItemDataProvider itemDataProvider = new MetadataProvider(itemList);
      if (citationStyle == null) {
        citationStyle = CitationStyleLanguageUtils.loadStyleFromConeJsonUrl(exportFormat.getId());
      }
      String defaultLocale = CitationStyleLanguageUtils.parseDefaultLocaleFromStyle(citationStyle);
      CSL citeproc = null;
      if (defaultLocale != null) {
        citeproc = new CSL(itemDataProvider, citationStyle, defaultLocale);
      } else {
        citeproc = new CSL(itemDataProvider, citationStyle);
      }
      citeproc.registerCitationItems(itemDataProvider.getIds());
      citeproc.setOutputFormat(CITATION_PROCESSOR_OUTPUT_FORMAT);
      Bibliography bibl = citeproc.makeBibliography();

      List<String> biblIds = Arrays.asList(bibl.getEntryIds());

      // remove surrounding <div>-tags
      for (String id : itemDataProvider.getIds()) {
        String citation = "";

        int citationPosition = biblIds.indexOf(id);
        if (citationPosition != -1) {
          citation = bibl.getEntries()[citationPosition];


          if (citation.contains("<div class=\"csl-right-inline\">")) {
            citation =
                citation.substring(citation.indexOf("<div class=\"csl-right-inline\">") + 30);
            citation = citation.substring(0, citation.lastIndexOf("</div>"));
          } else if (citation.contains("<div class=\"csl-entry\">")) {
            citation = citation.substring(citation.indexOf("<div class=\"csl-entry\">") + 23);
            citation = citation.substring(0, citation.lastIndexOf("</div>"));
          } else {
            citation = citation.trim();
          }
          if (logger.isDebugEnabled()) {
            logger.debug("Citation: " + citation);
          }
        }
        citationList.add(citation);
      }
      // create snippet format
      TransformerFactory factory = new TransformerFactoryImpl();
      Transformer transformer =
          factory.newTransformer(new StreamSource(CitationStyleLanguageManagerService.class
              .getClassLoader().getResourceAsStream(TRANSFORMATION_ITEM_LIST_2_SNIPPET)));
      transformer.setParameter("citations", citationList);
      transformer
          .transform(new StreamSource(new StringReader(itemList)), new StreamResult(snippet));
      if (logger.isDebugEnabled()) {
        logger.debug("eSciDoc-Snippet including Ciation: " + snippet);
      }
      result = snippet.toString().getBytes("UTF-8");
    } catch (IOException e) {
      logger.error("Error creating CSL processor", e);
      throw new CitationStyleLanguageException("Error creating CSL processor", e);
    } catch (TransformerConfigurationException e) {
      logger.error("Error preparing transformation itemList to snippet", e);
      throw new CitationStyleLanguageException(
          "Error preparing transformation itemList to snippet", e);
    } catch (TransformerException e) {
      logger.error("Error transforming itemList to snippet", e);
      throw new CitationStyleLanguageException("Error transforming itemList to snippet", e);
    } catch (Exception e) {
      logger.error("Error getting output", e);
      throw new CitationStyleLanguageException("Error getting output", e);
    }

    return result;
  }
}
