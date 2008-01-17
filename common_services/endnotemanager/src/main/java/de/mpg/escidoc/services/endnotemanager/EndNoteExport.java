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

package de.mpg.escidoc.services.endnotemanager;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;

// import test.TestHelper;

/**
 * EndNote Export Manager. 
 * Converts PubMan item-list to EndNote format.   
 *
 * @author Vlad Makarenko (initial creation)
 * @author $Author: vdm $ (last modification)
 * @version $Revision: 67 $ $LastChangedDate: 2007-12-11 12:39:50 +0100 (Tue, 11 Dec 2007) $
 *
 */ 

public class EndNoteExport implements EndNoteExportHandler {


	private final static Logger logger = Logger.getLogger(EndNoteExport.class);
	
	// path to the EndNote export XSLT 
	private String xslt = 	"resource/" +  
							"schema/" + 
							"eSciDoc_to_EndNote.xsl";
	

	public EndNoteExport()
	{
//		 Use Saxon for XPath2.0 support
        System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
	}
	

	/* (
	 * Takes PubMan item-list and conversts it to EndNote format. Uses XSLT.   
	 * @see de.mpg.escidoc.services.endnotemanager.EndNoteExportHandler#getOutputString(java.lang.String)
	 */
	public byte[] getOutput(String itemList) 
		throws EndNoteExportXSLTNotFoundException, EndNoteManagerException 
	{
		// xml source
		
		if (itemList == null) {
			throw new EndNoteManagerException("Item list is null");
		}
		javax.xml.transform.Source xmlSource =
			new javax.xml.transform.stream.StreamSource(new StringReader(itemList));
		
		// result
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		StringWriter sw = new StringWriter();
		javax.xml.transform.Result result =
//			new javax.xml.transform.stream.StreamResult(sw);
		new javax.xml.transform.stream.StreamResult(baos);

		// create an instance of TransformerFactory
		javax.xml.transform.TransformerFactory transFact =
			javax.xml.transform.TransformerFactory.newInstance(  );
		
		try 
		{
			// xslt source
			javax.xml.transform.Source xsltSource =
				new javax.xml.transform.stream.StreamSource(getResource(xslt));
				
			 javax.xml.transform.Transformer trans = 
				 transFact.newTransformer(xsltSource);
				
			logger.debug("Transformer:" + trans);
			 
//			trans.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
//			logger.info("ENCODING:" + trans.getOutputProperty(OutputKeys.ENCODING)) ;
			
			trans.transform(xmlSource, result);
			
//			return sw.toString().getBytes();
			return baos.toByteArray();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new EndNoteExportXSLTNotFoundException(e);
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			throw new EndNoteManagerException(e);
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			throw new EndNoteManagerException(e);
		}
	
	}

	private InputStream getResource(final String fileName) throws IOException
	{
		InputStream fileIn = getClass()
								.getClassLoader()
								.getResourceAsStream(fileName);

		if (fileIn == null)
		{
			fileIn = new FileInputStream(fileName);
		}
		return fileIn;
	}	
	
    
//	   public static void main(String args[]) throws IOException
//	   {
//
//		   String itemList = TestHelper.readFile("test/item_publication.xml", "UTF-8");
//		   EndNoteExport export = new EndNoteExport();
//		   
//		try {
//			byte[] result_ba = export.getOutput(itemList);
//			
//			byte[] testOutput_ba = TestHelper.readBinFile("test/EndNoteTestOutput.txt");
//			
//			logger.info( "testOutput len: \n" + testOutput_ba.length + "; result len:" + result_ba.length);
//			
//			logger.info("Arrays, export is OK: " + Arrays.equals(result_ba, testOutput_ba));
//			
//			for (int i = 0; i < result_ba.length; i++) {
//				if (result_ba[i] != testOutput_ba[i])
//				{
//					logger.info("Diff at " + i + ": " + (char)result_ba[i] + "(" + result_ba[i] + ")" + " != " + (char)testOutput_ba[i] + "(" + testOutput_ba[i] + ")");
//				}
//			}
//			
////			String result = new String(result_ba, "UTF-8");
////			String testOutput = new String(testOutput_ba, "UTF-8");
////			logger.info("export is OK: " + result.equals(testOutput));
//			
//			//logger.info( "testOutput: \n" + testOutput + "; length:" + testOutput.length());
//			//logger.info( "Result: \n" + result  + "; length:" + result.length());
////			for (int i = 0; i < result.toCharArray().length; i++) {
////				if (result.toCharArray()[i] != testOutput.toCharArray()[i])
////				{
////					logger.info("Diff at " + i + ": " + result.toCharArray()[i] + "(" + (int)result.toCharArray()[i] + ")" + " != " + testOutput.toCharArray()[i] + "(" + (int)testOutput.toCharArray()[i] + ")");
////				}
////			}
//			
//			
//		} catch (EndNoteExportXSLTNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (EndNoteManagerException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		   
//	   }

	
}	