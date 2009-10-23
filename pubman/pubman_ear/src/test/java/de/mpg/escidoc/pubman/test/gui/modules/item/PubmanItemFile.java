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
package de.mpg.escidoc.pubman.test.gui.modules.item;

import de.mpg.escidoc.pubman.test.gui.modules.item.PubmanItem.ComponentVisibility;


/**
 * @author endres
 *
 */
public class PubmanItemFile
{
    public enum UploadFileType {
        Pdf,
        PdfJapanese,
        Word,
        Ascii,
        Xml
    }
    
    public enum FileContentCategory
    {
        ANY_FULLTEXT, PRE_PRINT, POST_PRINT, PUBLISHER_VERSION, ABSTRACT, TABLE_OF_CONTENTS,
        SUPPLEMENTARY_MATERIAL, CORRESPONDENCE, COPYRIGHT_TRANSFER_AGREEMENT
    }
     
    public String localPath = null;
    public String uploadUrl = null;
    public UploadFileType uploadFileType;
    
    public FileContentCategory contentCategory = null;
    public ComponentVisibility componentVisibility = null;
    public String description = null;
    public String copyrightStatement = null;
    public String copyrightDate = null;
    public String license = null;
    
    public PubmanItemFile( String localPath, String uploadUrl, UploadFileType uploadFileType, FileContentCategory contentCategory, 
            ComponentVisibility componentVisibility, String description, String copyrightStatement, 
            String copyrightDate, String license) {
        
        this.localPath = localPath;
        this.uploadUrl = uploadUrl;
        this.uploadFileType = uploadFileType;
        this.contentCategory = contentCategory;
        this.componentVisibility = componentVisibility;
        this.description = description;
        this.copyrightStatement = copyrightStatement;
        this.copyrightDate = copyrightDate;
        this.license = license;
    }

    public String getLocalPath()
    {
        return localPath;
    }

    public String getUploadUrl()
    {
        return uploadUrl;
    }

    public UploadFileType getUploadFileType()
    {
        return uploadFileType;
    }

    public FileContentCategory getContentCategory()
    {
        return contentCategory;
    }

    public ComponentVisibility getComponentVisibility()
    {
        return componentVisibility;
    }

    public String getDescription()
    {
        return description;
    }

    public String getCopyrightStatement()
    {
        return copyrightStatement;
    }

    public String getCopyrightDate()
    {
        return copyrightDate;
    }

    public String getLicense()
    {
        return license;
    }
}
