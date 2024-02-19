package de.mpg.mpdl.inge.cslmanager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.util.PropertyReader;
import de.undercouch.citeproc.CSL;
import de.undercouch.citeproc.ItemDataProvider;
import de.undercouch.citeproc.output.Bibliography;
import de.undercouch.citeproc.script.ScriptRunnerFactory;
import de.undercouch.citeproc.script.ScriptRunnerFactory.RunnerType;

/**
 * CitationStyleLanguageManagerDefaultImpl is used to generate a citation for an escidoc item or a
 * list of escidoc items
 *
 * @author walter
 */
public class CitationStyleLanguageManagerService {
  private static final Logger logger = LogManager.getLogger(CitationStyleLanguageManagerService.class);
  private static final String CITATION_PROCESSOR_OUTPUT_FORMAT = "html";

  public static List<String> getOutput(String citationStyle, String itemList) throws CitationStyleLanguageException {
    List<String> citationList = new ArrayList<>();

    CSL citeproc = null;
    try {
      ItemDataProvider itemDataProvider = new MetadataProvider(itemList);

      String defaultLocale = CitationStyleLanguageUtils.parseDefaultLocaleFromStyle(citationStyle);
      if ("v8".equals(PropertyReader.getProperty(PropertyReader.INGE_CSL_JAVASCRIPT_ENGINE))) {
        ScriptRunnerFactory.setRunnerType(RunnerType.V8);
      } else if ("graaljs".equals(PropertyReader.getProperty(PropertyReader.INGE_CSL_JAVASCRIPT_ENGINE))) {
        ScriptRunnerFactory.setRunnerType(RunnerType.GRAALJS);
      } else {
        ScriptRunnerFactory.setRunnerType(RunnerType.AUTO);
      }

      if (defaultLocale != null) {
        citeproc = new CSL(itemDataProvider, citationStyle, defaultLocale);
      } else {
        citeproc = new CSL(itemDataProvider, citationStyle);
      }

      logger.info("JavaScript Engine: " + CSL.getJavaScriptEngineName() + " -Version: " + CSL.getJavaScriptEngineVersion() + " -JSVerison: "
          + CSL.getCiteprocJsVersion());

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
            citation = citation.substring(citation.indexOf("<div class=\"csl-right-inline\">") + 30);
            citation = citation.substring(0, citation.lastIndexOf("</div>"));
            citation = citation.substring(0, citation.lastIndexOf("</div>"));
          } else if (citation.contains("<div class=\"csl-entry\">")) {
            citation = citation.substring(citation.indexOf("<div class=\"csl-entry\">") + 23);
            citation = citation.substring(0, citation.lastIndexOf("</div>"));
          } else {
            citation = citation.trim();
          }
        }
        citationList.add(citation);
      }
      return citationList;

    } catch (IOException e) {
      logger.error("Error creating CSL processor", e);
      throw new CitationStyleLanguageException("Error creating CSL processor", e);
    } catch (Exception e) {
      logger.error("Error getting output", e);
      throw new CitationStyleLanguageException("Error getting output", e);
    } finally {
      if (citeproc != null) {
        citeproc.close();
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
