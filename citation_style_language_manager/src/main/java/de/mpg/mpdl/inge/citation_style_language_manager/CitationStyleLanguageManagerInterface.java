package de.mpg.mpdl.inge.citation_style_language_manager;

import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;

/**
 * Interface for transforming a list of items into a citation output for each of the items
 * 
 * @author walter
 */
public interface CitationStyleLanguageManagerInterface {
  /**
   * The name to obtain this service.
   */
  String SERVICE_NAME =
      "ejb/de/mpg/escidoc/services/citation_style_language_manager/CitationStyleLanguageManagerInterface";

  byte[] getOutput(ExportFormatVO exportFormat, String itemList) throws Exception;

  boolean isCitationStyle(String cs);
}
