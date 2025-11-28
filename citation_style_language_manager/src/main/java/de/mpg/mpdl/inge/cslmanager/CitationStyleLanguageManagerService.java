package de.mpg.mpdl.inge.cslmanager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.undercouch.citeproc.CSL;
import de.undercouch.citeproc.ItemDataProvider;
import de.undercouch.citeproc.output.Bibliography;

/**
 * CitationStyleLanguageManagerDefaultImpl is used to generate a citation for an escidoc item or a
 * list of escidoc items
 *
 * @author walter
 */
public class CitationStyleLanguageManagerService {
  private static final Logger logger = LogManager.getLogger(CitationStyleLanguageManagerService.class);
  private static final String CITATION_PROCESSOR_OUTPUT_FORMAT = "html";

  private CitationStyleLanguageManagerService() {}

  public static List<String> getOutput(String citationStyle, String itemList) throws CitationStyleLanguageException {
    List<String> citationList = new ArrayList<>();

    CSL citeproc = null;
    try {
      ItemDataProvider itemDataProvider = new MetadataProvider(itemList);

      String defaultLocale = CitationStyleLanguageUtils.parseDefaultLocaleFromStyle(citationStyle);

      if (null != defaultLocale) {
        citeproc = new CSL(itemDataProvider, citationStyle, defaultLocale);
      } else {
        citeproc = new CSL(itemDataProvider, citationStyle);
      }


      citeproc.registerCitationItems(itemDataProvider.getIds());
      citeproc.setOutputFormat(new CiteProcPubManHTMLFormat());
      Bibliography bibl = citeproc.makeBibliography();

      return Arrays.asList(bibl.getEntries());

    } catch (IOException e) {
      logger.error("Error creating CSL processor", e);
      throw new CitationStyleLanguageException("Error creating CSL processor", e);
    } catch (Exception e) {
      logger.error("Error getting output", e);
      throw new CitationStyleLanguageException("Error getting output", e);
    } finally {
      if (null != citeproc) {
        //citeproc.close();
      }
    }
  }

  public static List<String> getOutput(ExportFormatVO exportFormat, String itemList) throws CitationStyleLanguageException {
    String citationStyle;
    try {
      citationStyle = CitationStyleLanguageUtils.loadStyleFromConeJsonUrl(exportFormat.getId());
    } catch (Exception e) {
      throw new CitationStyleLanguageException("Error while getting citation style from cone", e);
    }
    return getOutput(citationStyle, itemList);
  }

}
