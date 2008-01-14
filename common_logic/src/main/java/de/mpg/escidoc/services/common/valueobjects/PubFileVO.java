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

import de.mpg.escidoc.services.common.referenceobjects.PubFileRO;

/**
 * A file that is contained in an item.
 * 
 * @revised by MuJ: 28.08.2007
 * @version $Revision: 632 $ $LastChangedDate: 2007-11-21 12:45:14 +0100 (Wed, 21 Nov 2007) $ by $Author: pbroszei $
 * @updated 21-Nov-2007 12:05:47
 */
public class PubFileVO extends ValueObject implements Cloneable
{
    /**
     * Fixed serialVersionUID to prevent java.io.InvalidClassExceptions like
     * 'de.mpg.escidoc.services.common.valueobjects.PubItemVO; local class incompatible: stream classdesc
     * serialVersionUID = 8587635524303981401, local class serialVersionUID = -2285753348501257286' that occur after
     * JiBX enhancement of VOs. Without the fixed serialVersionUID, the VOs have to be compiled twice for testing (once
     * for the Application Server, once for the local test).
     * 
     * @author Johannes Mueller
     */
    private static final long serialVersionUID = 1L;

    	/**
	 * The possible content types of a file.
	 * @updated 21-Nov-2007 12:05:47
	 */
    public enum ContentType
    {
        ABSTRACT, PRE_PRINT, POST_PRINT, PUBLISHER_VERSION, CORRESPONDENCE, SUPPLEMENTARY_MATERIAL, COPYRIGHT_TRANSFER_AGREEMENT
    }

    	/**
	 * The possible visibility of a file.
	 * @updated 21-Nov-2007 12:05:47
	 */
    public enum Visibility
    {
        PUBLIC, PRIVATE
    }

    private PubFileRO reference;
    /**
     * The name of the file including the extension.
     */
    private String name;
    /**
     * The visibility of the file for users of the system.
     */
    private PubFileVO.Visibility visibility;
    /**
     * A short description of the file.
     */
    private String description;
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
     * The content type of the file.
     */
    private PubFileVO.ContentType contentType;
    /**
	 * The size of the file in Bytes.
	 * Has to be zero if no content is given.
	 */
    private long size;
    /**
     * The location from which the data of the file has to be fetched.
     */
    private String locator;
    /**
	 * The MIME-type of this format.
	 * Valid values see http://www.iana.org/assignments/media-types/
	 */
    private String mimeType;

    /**
     * Public contructor.
     * 
     * @author Thomas Diebaecker
     */
    public PubFileVO()
    {
    }

    /**
     * Copy constructor.
     * 
     * @author Thomas Diebaecker
     * @param other The instance to copy.
     */
    public PubFileVO(PubFileVO other)
    {
        this.setContent(other.getContent());
        this.setContentType(other.getContentType());                
        this.setContentTypeString(other.getContentTypeString());
        this.setCreationDate(other.getCreationDate());
        this.setDescription(other.getDescription());
        this.setLastModificationDate(other.getLastModificationDate());
        this.setLocator(other.getLocator());
        this.setMimeType(other.getMimeType());
        this.setName(other.getName());
        this.setPid(other.getPid());
        this.setReference(other.getReference());
        this.setSize(other.getSize());
        this.setVisibility(other.getVisibility());
        this.setVisibilityString(other.getVisibilityString());
    }
    
    /**
     * {@inheritDoc}
     * @author Thomas Diebaecker
     */
    @Override
    public Object clone()
    {
        return new PubFileVO(this);
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

    /**
     * Delivers the files' reference.
     * 
     * @see de.mpg.escidoc.services.common.referenceobjects.ReferenceObject
     */
    public PubFileRO getReference()
    {
        return reference;
    }

    /**
	 * Sets the files' reference.
	 * @see de.mpg.escidoc.services.common.referenceobjects.ReferenceObject
	 * 
	 * @param newVal
	 */
    public void setReference(PubFileRO newVal)
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

    /**
     * Delivers the size of the file in Bytes.
     */
    public long getSize()
    {
        return size;
    }

    /**
	 * Sets the size of the file in Bytes.
	 * 
	 * @param newVal
	 */
    public void setSize(long newVal)
    {
        size = newVal;
    }

    /**
     * Delivers the locator of the file, i. e. the location from which the data of the file has to be fetched.
     */
    public String getLocator()
    {
        return locator;
    }

    /**
	 * Sets the locator of the file, i. e. the location from which the data of the
	 * file has to be fetched.
	 * 
	 * @param newVal
	 */
    public void setLocator(String newVal)
    {
        locator = newVal;
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
        content = newVal;
    }

    /**
     * Delivers the content type of the file.
     */
    public PubFileVO.ContentType getContentType()
    {
        return contentType;
    }

    /**
	 * Sets the content type of the file.
	 * 
	 * @param newVal
	 */
    public void setContentType(PubFileVO.ContentType newVal)
    {
        contentType = newVal;
    }

    /**
     * Delivers the visibility of the file.
     */
    public PubFileVO.Visibility getVisibility()
    {
        return visibility;
    }

    /**
	 * Sets the visibility of the file.
	 * 
	 * @param newVal
	 */
    public void setVisibility(PubFileVO.Visibility newVal)
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
     * Delivers the value of the contentType Enum as a String. If the Enum is not set, an empty String is returned.
     */
    public String getContentTypeString()
    {
        if (contentType == null || contentType.toString() == null)
        {
            return "";
        }
        return contentType.toString();
    }

    /**
	 * Sets the value of the contentType Enum by a String.
	 * 
	 * @param newValString
	 */
    public void setContentTypeString(String newValString)
    {
        if (newValString == null || newValString.length() == 0)
        {
            contentType = null;
        }
        else
        {
            PubFileVO.ContentType newVal = PubFileVO.ContentType.valueOf(newValString);
            contentType = newVal;
        }
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
            PubFileVO.Visibility newVal = PubFileVO.Visibility.valueOf(newValString);
            visibility = newVal;
        }
    }
}