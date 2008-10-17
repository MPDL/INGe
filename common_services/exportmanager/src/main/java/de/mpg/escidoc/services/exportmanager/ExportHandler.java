/*
* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.de/license.
* See the License for the specific language governing permissions
* and limitations under the License.
*
* When distributing Covered Code, include this CDDL HEADER in each
* file and include the License file at license/ESCIDOC.LICENSE.
* If applicable, add the following below this CDDL HEADER, with the
* fields enclosed by brackets "[]" replaced with your own identifying
* information: Portions Copyright [yyyy] [name of copyright owner]
*
* CDDL HEADER END
*/

/*
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

package de.mpg.escidoc.services.exportmanager;

import java.io.File;
import java.io.IOException;

/**
 * Interface for the wrapping of the export interfaces like StructuredExportHandler 
 * and CitationStyleHandler 
 * in the structured format.
 * @author Vlad Makarenko  (initial creation) 
 * @author $Author: vdm $ (last modification)
 * @ $Revision: 63 $
 *  $LastChangedDate: 2007-11-13 15:40:28 +0100 (Tue, 13 Nov 2007) $ 
 */


public interface ExportHandler {
 
    /**
     * The name to obtain this service.
     */
    String SERVICE_NAME = "ejb/de/mpg/escidoc/services/exportmanager/ExportHandler";

    /**
	 * This method provides XML formatted output of the supported export formats 
     * @throws IOException 
     * @throws UnsupportedEncodingException 
	 */
    String explainFormatsXML() throws ExportManagerException, IOException;


	/**
	 * Returns data presenting the items in export format.
	 * 
	 * @param exportFormat - export format of the items 
	 * @param outputFormat - file format (e.g. pdf, odt, rtf etc.) to be exported 
	 * @param archiveFormat - 	archive format for the export bundle. Can be zip, tar.gz, etc. 
	 * 		  					If <code>null </code>the archive will not be created.
	 * @param filteredItemList is used for archive generation. This is the XML String in the appropriate scheme which will be processed by export component. 
	 * 							 All files will be downloaded according to theirs URIs references  and put into the resulting archive. 
	 * @return export as byte[]
	 * @throws ExportManagerException
	 * @throws IOException 
	 */
	byte[] getOutput(
			String exportFormat,
			String outputFormat,
			String archiveFormat,
			String filteredItemList 
	) throws  
			ExportManagerException, IOException;

    /**
     * Generates archive, put the following issues in it:
     * 
     * 1) All files, referenced via URIs in the <code>itemListFiltered</code> XML 
     * 2) Description of the list, content of the <code>description</code>      
     * 3) License agreement
     * 
     * To be used in case of the huge sizes, which can be precalculated with {@link calculateItemListFileSizes(java.lang.String) calculateItemListFileSizes}  
     * 
     * P.S. Please delete generated File since processing of it will be finished!  
     * 
     * @param exportFormat - is the name of the export format to ba presented in the archive description file   
     * @param archiveFormat is archive format for the export bundle. Can be zip, tar.gz, etc.
     * @param description contains some info which can describe the content of the archive. E.g. CSV or XMl file with the list of all archive items, some addition info 
     * @param itemListFiltered is XML String which contents all file references (URIs) to be presented in the archive.
     * 	 	  The components which will not be exported should be removed from the itemList.    
     * @return archive as <code>java.io.File</code> reference
     * @throws ExportManagerException
     * @throws IOException 
     */
	File generateArchiveFile(
    		String exportFormat,  
    		String archiveFormat,
			byte[] description, 
			String itemListFiltered
    ) throws 
    	ExportManagerException, IOException;
	
    /**
     * Generates archive, put the following issues in it:
     * 
     * 1) All files, referenced via URIs in the <code>itemListFiltered</code> XML 
     * 2) Description of the list, content of the <code>description</code>      
     * 3) License agreement
     * 
     * @param exportFormat - is the name of the export format to be presented in the archive description file   
     * @param archiveFormat is archive format for the export bundle. Can be zip, tar.gz, etc.
     * @param description contains some info which can describe the content of the archive. E.g. CSV or XMl file with the list of all archive items, some addition info 
     * @param itemListFiltered is XML String which contents all file references (URIs) to be presented in the archive.
     * 	 	  The components which will not be exported should be removed from the itemList.    
     * @return archive as byte[]
     * @throws ExportManagerException
     * @throws IOException 
     */

	byte[] generateArchive(
    		String exportFormat, 
    		String archiveFormat,
    		byte[] description, 
    		String itemListFiltered
    ) throws 
    	ExportManagerException, IOException;
	
	/**
	 * Generates archive, put the following issues in it:
	 * 
	 * 1) All files, referenced via URIs in the <code>itemListFiltered</code> XML 
	 * 2) License agreement
	 * 
	 * @param archiveFormat is archive format for the export bundle. Can be zip, tar.gz, etc.
	 * @param itemListFiltered is XML String which contents all file references (URIs) to be presented in the archive.
	 * 	 	  The components which will not be exported should be removed from the itemList.    
	 * @return archive as byte[]
	 * @throws ExportManagerException
	 * @throws IOException 
	 */
	
	byte[] generateArchive(
			String archiveFormat,
			String itemListFiltered
	) throws 
	ExportManagerException, IOException;	
 
	/**
	 * Returns data presenting the items in export format.
	 *  
	 * Used in the cases of the huge sizes.   
	 * 
	 * @param exportFormat - export format of the items 
	 * @param outputFormat - file format (e.g. pdf, odt, rtf etc.) to be exported 
	 * @param archiveFormat - 	archive format for the export bundle. Can be zip, tar.gz, etc. 
	 * 							Archive will be created with the method {@link generateArchiveFile(java.lang.String, java.lang.String, byte[], java.lang.String) generateArchiveFile}.  
	 * 		  					If <code>null</code>, the archive will not be created.
	 * @param filteredItemList - XML String in the appropriate scheme (PibItemList for the moment) which will be processed by export component
	 * @return export as <code>java.io.File</code> 
	 * @throws ExportManagerException
	 * @throws IOException 
	 */
	File getOutputFile(
			String exportFormat,
			String outputFormat,
			String archiveFormat,
			String filteredItemList
	) throws  
			ExportManagerException, IOException;
	
	
	/**
	 * Calculates complete sum of the file sizes referenced from the itemList.
	 * The following elements are taken for the size calculation:
	 * //{http://www.escidoc.de/schemas/metadatarecords/0.4}md-records/{http://escidoc.mpg.de/metadataprofile/schema/0.1/file}file/{http://purl.org/dc/terms/}extent  
	 * @param itemList
	 * @return Sum of the file sizes 
	 * @throws ExportManagerException
	 */
	long calculateItemListFileSizes( String itemList ) 
		throws ExportManagerException;
	
//    String[] getListOfStructuredExportFormats(String contexts);
//    
//    String[] getListOfCitationStyles();
	
}
