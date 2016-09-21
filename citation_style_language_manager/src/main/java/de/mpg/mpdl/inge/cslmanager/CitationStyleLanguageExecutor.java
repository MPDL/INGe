package de.mpg.mpdl.inge.cslmanager;

import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.FileFormatVO;

/**
 * CitationStyleLanguageExecutor will simply process a sample item with a citationstyle
 * 
 * @author walter
 * 
 */
public class CitationStyleLanguageExecutor {



  public static void main(String[] args) throws Exception {
    CitationStyleLanguageManagerInterface cslManager =
        new CitationStyleLanguageManagerDefaultImpl();
    cslManager.getOutput(createExportFormat("Name", "html"), "escidoc:1234");
  }

  private static ExportFormatVO createExportFormat(String name, String fileFormatName) {
    ExportFormatVO exportFormat = new ExportFormatVO();
    exportFormat.setName("CSL");
    exportFormat.setFormatType(ExportFormatVO.FormatType.LAYOUT);
    FileFormatVO fileFormat = new FileFormatVO();
    fileFormat.setName(fileFormatName);
    fileFormat.setMimeType(FileFormatVO.getMimeTypeByName(fileFormatName));
    exportFormat.setSelectedFileFormat(fileFormat);
    // TODO set ID
    exportFormat.setId("xxx");;
    return exportFormat;
  }


}
