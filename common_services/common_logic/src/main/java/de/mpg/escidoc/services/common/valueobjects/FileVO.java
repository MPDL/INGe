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

import java.util.ArrayList;
import java.util.List;

import de.mpg.escidoc.services.common.referenceobjects.AccountUserRO;
import de.mpg.escidoc.services.common.referenceobjects.FileRO;
import de.mpg.escidoc.services.common.valueobjects.metadata.MdsFileVO;

/**
 * A file that is contained in an item.
 * 
 * @revised by MuJ: 28.08.2007
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @updated 21-Nov-2007 12:05:47
 */
public class FileVO extends ValueObject implements Cloneable
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
     * The possible visibility of a file.
     * @updated 21-Nov-2007 12:05:47
     */
    public enum Visibility
    {
        PUBLIC, PRIVATE, AUDIENCE
    }

    /**
     * The possible storage of a file.
     */
    public enum Storage
    {
        INTERNAL_MANAGED, EXTERNAL_URL, EXTERNAL_MANAGED
    }
    /**
     * The possible storage of a file.
     */
    public enum ChecksumAlgorithm
    {
        MD5, SHA1
    }


    private FileRO reference;
    
    /**
     * The name of the file including the extension.
     */
    private String name;
    
    /**
     * The visibility of the file for users of the system.
     */
    private FileVO.Visibility visibility;
    
    /**
     * A short description of the file.
     */
    private String description;
    
    private AccountUserRO createdByRO;
    
    /**
     * This date gives the moment in time the file was created.
     */
    private java.util.Date creationDate;
    
    /**
     * This date is updated whenever the file is stored.
     */
    private java.util.Date lastModificationDate;
    
    /**
     * The persistent identifier of the file if the item is released.
     */
    private String pid;
    
    /**
     * A reference to the content of the file.
     */
    private String content;
    
    /**
     * A reference to the storage attribute of the file.
     */
    private FileVO.Storage storage;
    
    /**
     * The content type of the file.
     */
    private String contentCategory;
    
    private String checksum;
    
    private ChecksumAlgorithm checksumAlgorithm;
    
    /**
     * The size of the file in Bytes.
     * Has to be zero if no content is given.
     */
//    private long size;

    /**
     * The MIME-type of this format.
     * Valid values see http://www.iana.org/assignments/media-types/
     */
    private String mimeType;

    private List<MetadataSetVO> metadataSets = new ArrayList<MetadataSetVO>();
    
    /**
     * Public contructor.
     * 
     * @author Thomas Diebaecker
     */
    public FileVO()
    {
    }

    /**
     * Copy constructor.
     * 
     * @author Thomas Diebaecker
     * @param other The instance to copy.
     */
    public FileVO(FileVO other)
    {
        content = other.content;
        contentCategory = other.contentCategory;                
        creationDate = other.creationDate;
        description = other.description;
        lastModificationDate = other.lastModificationDate;
        mimeType = other.mimeType;
        name = other.name;
        pid = other.pid;
        reference = other.reference;
//        size = other.size;
        visibility = other.visibility;
        storage = other.storage;
        metadataSets = other.metadataSets;
        checksum = other.checksum;
        checksumAlgorithm = other.checksumAlgorithm;
    }
    
    /**
     * {@inheritDoc}
     * @author Thomas Diebaecker
     */
    @Override
    public Object clone()
    {
        return new FileVO(this);
    }

    /**
     * Helper method for JiBX transformations. This method helps JiBX to determine if this is a 'create' or an 'update'
     * transformation. (visibility restricted to package)
     * 
     * @return boolean true if this file already exists in the framework (creation date is already set)
     */
    boolean alreadyExistsInFramework()
    {
        return (this.creationDate != null);
    }

    public MdsFileVO getDefaultMetadata()
    {
        if (metadataSets.size() > 0 && metadataSets.get(0) instanceof MdsFileVO)
        {
            return (MdsFileVO) metadataSets.get(0);
        }
        else
        {
            return null;
        }
    }
    
    public void setDefaultMetadata(MdsFileVO mdsFileVO)
    {
        if (metadataSets.size() == 0)
        {
            metadataSets.add(mdsFileVO);
        }
        else
        {
            metadataSets.set(0, mdsFileVO);
        }
    }
    
    /**
     * Delivers the files' reference.
     * 
     * @see de.mpg.escidoc.services.common.referenceobjects.ReferenceObject
     */
    public FileRO getReference()
    {
        return reference;
    }

    /**
     * Sets the files' reference.
     * @see de.mpg.escidoc.services.common.referenceobjects.ReferenceObject
     * 
     * @param newVal
     */
    public void setReference(FileRO newVal)
    {
        reference = newVal;
    }

    /**
     * Delivers the name of the file including the extension.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name of the file including the extension.
     * 
     * @param newVal
     */
    public void setName(String newVal)
    {
        name = newVal;
    }

    /**
     * Delivers the persistent identifier of the file.
     */
    public String getPid()
    {
        return pid;
    }

    /**
     * Sets the persistent identifier of the file.
     * 
     * @param newVal
     */
    public void setPid(String newVal)
    {
        pid = newVal;
    }

    /**
     * Delivers the description of the file, i. e. a short description of the file.
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Sets the description of the file, i. e. a short description of the file.
     * 
     * @param newVal
     */
    public void setDescription(String newVal)
    {
        description = newVal;
    }

    public AccountUserRO getCreatedByRO()
    {
        return createdByRO;
    }

    public void setCreatedByRO(AccountUserRO createdByRO)
    {
        this.createdByRO = createdByRO;
    }

    /**
     * Delivers a reference to the content of the file, i. e. to the data of the file.
     */
    public String getContent()
    {
        return content;
    }

    /**
     * Sets a reference to the content of the file, i. e. to the data of the file.
     * 
     * @param newVal
     */
    public void setContent(String newVal)
    {
        this.content = newVal;
    }

    /**
     * Delivers the content type of the file.
     */
    public String getContentCategory()
    {
        return contentCategory;
    }

    /**
     * Sets the content type of the file.
     * 
     * @param newVal
     */
    public void setContentCategory(String newVal)
    {
        contentCategory = newVal;
    }

    /**
     * Delivers the visibility of the file.
     */
    public FileVO.Visibility getVisibility()
    {
        return visibility;
    }

    /**
     * Sets the visibility of the file.
     * 
     * @param newVal
     */
    public void setVisibility(FileVO.Visibility newVal)
    {
        visibility = newVal;
    }

    /**
     * Delivers the MIME-type of the file. For valid values see
     * http://www.iana.org/assignments/media-types/
     */
    public String getMimeType()
    {
        return mimeType;
    }

    /**
     * Sets the MIME-type of the file. For valid values see
     * http://www.iana.org/assignments/media-types/
     * 
     * @param newVal
     */
    public void setMimeType(String newVal)
    {
        mimeType = newVal;
    }

    /**
     * Delivers the creation date of the file.
     */
    public java.util.Date getCreationDate()
    {
        return creationDate;
    }

    /**
     * Sets the creation date of the file.
     * 
     * @param newVal
     */
    public void setCreationDate(java.util.Date newVal)
    {
        this.creationDate = newVal;
    }

    /**
     * Delivers the date of the last modification of the file.
     */
    public java.util.Date getLastModificationDate()
    {
        return lastModificationDate;
    }

    /**
     * Sets the date of the last modification of the file.
     * 
     * @param newVal
     */
    public void setLastModificationDate(java.util.Date newVal)
    {
        this.lastModificationDate = newVal;
    }

    /**
     * Delivers the value of the contentCategory Enum as a String. If the Enum is not set, an empty String is returned.
     */
    public String getContentCategoryString()
    {
        if (contentCategory == null || contentCategory.toString() == null)
        {
            return "";
        }
        return contentCategory.toString();
    }

    /**
     * Sets the value of the contentCategory Enum by a String.
     * 
     * @param newValString
     */
    public void setContentCategoryString(String newValString)
    {
        contentCategory = newValString;
    }

    /**
     * Delivers the value of the visibility Enum as a String. If the enum is not set, an empty String is returned.
     */
    public String getVisibilityString()
    {
        if (visibility == null || visibility.toString() == null)
        {
            return "";
        }
        return visibility.toString();
    }

    /**
     * Sets the value of the visibility Enum by a String.
     * 
     * @param newValString
     */
    public void setVisibilityString(String newValString)
    {
        if (newValString == null || newValString.length() == 0)
        {
            visibility = null;
        }
        else
        {
            FileVO.Visibility newVal = FileVO.Visibility.valueOf(newValString);
            visibility = newVal;
        }
    }

    public FileVO.Storage getStorage()
    {
        return storage;
    }

    public void setStorage(FileVO.Storage storage)
    {
        this.storage = storage;
    }
    
    public String getStorageString()
    {
        if (storage == null || storage.toString() == null)
        {
            return "";
        }
        return storage.toString();
    }
    
    public void setStorageString(String newValString)
    {
        if (newValString == null || newValString.length() == 0)
        {
            storage = null;
        }
        else
        {
            FileVO.Storage newVal = FileVO.Storage.valueOf(newValString);
            storage = newVal;
        }
    }

    public List<MetadataSetVO> getMetadataSets()
    {
        return metadataSets;
    }

    public String getChecksum()
    {
        return checksum;
    }

    public void setChecksum(String checksum)
    {
        this.checksum = checksum;
    }

    public ChecksumAlgorithm getChecksumAlgorithm()
    {
        return checksumAlgorithm;
    }

    public void setChecksumAlgorithm(ChecksumAlgorithm checksumAlgorithm)
    {
        this.checksumAlgorithm = checksumAlgorithm;
    }
    
    
}