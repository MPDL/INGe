package de.mpg.escidoc.services.citation_style_language_manager;

import de.mpg.escidoc.services.common.valueobjects.ExportFormatVO;
import de.mpg.escidoc.services.common.valueobjects.FileFormatVO;

/**
 * CitationStyleLanguageExecutor will simply process a sample item with a
 * citationstyle
 * 
 * @author walter
 * 
 */
public class CitationStyleLanguageExecutor {

	
	
	public static void main(String[] args) {
		CitationStyleLanguageManagerInterface cslManager = new CitationStyleLanguageManagerDefaultImpl();
		cslManager.getOutput(createExportFormat("Name", "html") , "escidoc:1234");
	}
	
	private static ExportFormatVO createExportFormat(String name, String fileFormatName) 
	{
		ExportFormatVO exportFormat = new ExportFormatVO();
		exportFormat.setName(name);
		exportFormat.setFormatType(ExportFormatVO.FormatType.LAYOUT_CSL);
		FileFormatVO fileFormat = new FileFormatVO();
		fileFormat.setName(fileFormatName);
		fileFormat.setMimeType(FileFormatVO.getMimeTypeByName(fileFormatName));
		exportFormat.setSelectedFileFormat(fileFormat);
		exportFormat.setCslXml(CitationStyleLanguageUtils.loadStyleFromUrl("https://raw.githubusercontent.com/citation-style-language/styles/master/ieee-with-url.csl"));;
		return exportFormat;
	}
	

}
