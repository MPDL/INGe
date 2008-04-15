/*
*
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


package de.mpg.escidoc.services.common.valueobjects;

/**
 * Valueobject representing the export file format data needed for the export.
 * 
 * @version $Revision: 653 $ $LastChangedDate: 2007-12-11 11:07:02 +0100 (Tue, 11 Dec 2007) $ by $Author: vdm $
 */

public class FileFormatVO extends ValueObject {

	/**
	 * Fixed serialVersionUID to prevent java.io.InvalidClassExceptions like
	 * 'de.mpg.escidoc.services.common.valueobjects.ItemVO; local class incompatible: stream classdesc
	 * serialVersionUID = 8587635524303981401, local class serialVersionUID = -2285753348501257286' that occur after
	 * JiBX enhancement of VOs. Without the fixed serialVersionUID, the VOs have to be compiled twice for testing (once
	 * for the Application Server, once for the local test).
	 * 
	 * @author Johannes Mueller
	 */
   

    public static final String TEXT_NAME = "txt";
    public static final String TEXT_MIMETYPE = "text/plain";
    
    public static final String PDF_NAME = "pdf";
    public static final String PDF_MIMETYPE = "application/pdf";
    
    public static final String RTF_NAME = "rtf";
    public static final String RTF_MIMETYPE = "application/rtf";
    
    public static final String HTML_NAME = "html";
    public static final String HTML_MIMETYPE = "text/html";
    
    public static final String ODT_NAME = "odt";
    public static final String ODT_MIMETYPE = "application/vnd.oasis.opendocument.text";
    
    public static final String SNIPPET_NAME = "snippet";
    public static final String SNIPPET_MIMETYPE = "application/xml";

    public static final String DEFAULT_NAME = PDF_NAME;
    public static final String DEFAULT_MIMETYPE = PDF_MIMETYPE;
    
    /**
     * The mime type of FileFormat
     */    
	private String mimeType;
	
    /**
     * The name of FileFormat
     */    
    private String name;

    
    /**
     * Delivers the name of the selected file according to name of format.
     */
    public static String getMimeTypeByName(String name)
    {
    	name = name == null || name.trim().equals("") ? "" : name.trim();  
    	// if name is not in scope of file format, set it to FileFormatVO.PDF_MIMETYPE
    	// by default
    	return
    		name.equals(TEXT_NAME)		? TEXT_MIMETYPE	: 
    		name.equals(PDF_NAME) 		? PDF_MIMETYPE	: 
    		name.equals(RTF_NAME) 		? RTF_MIMETYPE	: 
    		name.equals(HTML_NAME)		? HTML_MIMETYPE	: 
            name.equals(ODT_NAME)		? ODT_MIMETYPE	: 
            name.equals(SNIPPET_NAME)	? SNIPPET_MIMETYPE	: 
            						  	DEFAULT_MIMETYPE; 
    }
    

    // workaround to find out whether the output format is presented  
	//TODO: should be taken directly from xml description of the export 
	//rather then hardcoded in FileFormatVO class    
    public static boolean isOutputFormatSupported(String outputFormat)
    {
    	return !(
    			getMimeTypeByName(outputFormat).equals(DEFAULT_MIMETYPE) 
    			&& !outputFormat.equals(DEFAULT_NAME)
    			); 
    }

    
    
	/**
	 * get mimeType
	 */
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * set mimeType
	 */
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	/**
	 * get name of the file format
	 */
	public String getName() {
		return name;
	}

	/**
	 * set name of the file format
	 */
	public void setName(String name) {
		this.name = name;
	}
    
    
}
