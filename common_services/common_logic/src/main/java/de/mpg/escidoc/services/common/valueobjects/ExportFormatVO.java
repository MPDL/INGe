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
 * Valueobject representing the export format data needed for the export.
 * 
 * @revised by MuJ: 28.08.2007
 * @version $Revision: 654 $ $LastChangedDate: 2007-12-11 11:08:15 +0100 (Tue, 11 Dec 2007) $ by $Author: vdm $
 * @updated 05-Sep-2007 10:42:30
 */
public class ExportFormatVO extends ValueObject
{
    /**
     * Fixed serialVersionUID to prevent java.io.InvalidClassExceptions like
     * 'de.mpg.escidoc.services.common.valueobjects.ItemVO; local class incompatible: stream classdesc
     * serialVersionUID = 8587635524303981401, local class serialVersionUID = -2285753348501257286' that occur after
     * JiBX enhancement of VOs. Without the fixed serialVersionUID, the VOs have to be compiled twice for testing (once
     * for the Application Server, once for the local test).
     * 
     * @author Johannes Mueller
     */
	
    /**
     * The possible file formats for this export format (i.e. pdf,html,..).
     */
    private java.util.List<FileFormatVO> fileFormats;
    private FormatType formatType;    
    private String name;
    private java.util.List<String> creators;
    private String description;
    
   
    /**
     * The id used for the transforming.
     */
    private java.lang.String id;
    /**
     * The currently selected file format.
     */
    private FileFormatVO selectedFileFormat;

    /**
     * The possible export format types.
     * 
     * @version $Revision: 654 $ $LastChangedDate: 2007-12-11 11:08:15 +0100 (Tue, 11 Dec 2007) $ by $Author: vdm $
     * @updated 05-Sep-2007 10:42:30
     */
    public enum FormatType
    {
        LAYOUT, STRUCTURED
    }

    /**
     * Delivers the name of this export format.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name of this export format.
     * 
     * @param newVal
     */
    public void setName(String newVal)
    {
        name = newVal;
    }

    /**
     * Delivers the list of file formats.
     */
    public java.util.List<FileFormatVO> getFileFormats()
    {
        return fileFormats;
    }

    /**
     * Delivers the type of this export format.
     */
    public FormatType getFormatType()
    {
        return formatType;
    }

    /**
     * Sets the type of this export format.
     * 
     * @param newVal
     */
    public void setFormatType(FormatType newVal)
    {
        formatType = newVal;
    }
    

    /**
     * Gets the mime type accordingthe selected file format.
     */
    public String getSelectedContentType()
    {
//        return selectedContentType;
        return selectedFileFormat.getMimeType();
    }

    /**
     * Sets the name of the selected file format.
     * 
     * @param newVal
     */
    public void setSelectedFileFormat(FileFormatVO newVal)
    {
        selectedFileFormat = newVal;
    }

    /**
     * Delivers the name of the selected file format.
     */
    public FileFormatVO getSelectedFileFormat()
    {
        return selectedFileFormat;
    }

 
    
     /**
     * Delivers the id of this export format.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the id of this export format.
     * 
     * @param id
     */
    public void setId(String id)
    {
        this.id = id;
    }

    public java.util.List<String> getCreators() {
    	return creators;
    }
    
    public void setCreators(java.util.List<String> creators) {
    	this.creators = creators;
    }

    /*
     * set description (optional)
     */
	public String getDescription() {
		return description;
	}

    /*
     * get description (optional)
     */
	public void setDescription(String description) {
		this.description = description;
	}
    
    /**
     * Returns the String representation of this object.
     */
    public final String toString()
    {
        return "[" + name + "(" + id + "): " + selectedFileFormat + ", " + description + ", " + fileFormats + ", " + creators + "]";
    }


}