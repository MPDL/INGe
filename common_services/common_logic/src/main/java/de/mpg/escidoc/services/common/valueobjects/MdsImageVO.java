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

import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.ImageAcquisitionDeviceVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.ImageInfoVO;

/**
 * Metadata of an image.
 * 
 * @revised by MuJ: 28.08.2007
 * @version $Revision: 611 $ $LastChangedDate: 2007-11-07 12:04:29 +0100 (Wed, 07 Nov 2007) $ by $Author: jmueller $
 * @updated 05-Sep-2007 10:58:49
 */
public class MdsImageVO extends MetadataSetVO
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
 

    /**
     * The possible image types.
     * 
     * @updated 05-Sep-2007 10:58:49
     */
    public enum ImageType
    {
        FIGURE, ILLUSTRATION, MAP, PAGE, PAINTING, PHOTOGRAPH, DRAWING, PICTURE, ENGRAVINGS
    }

    /**
     * The color depth of the digital image in bits. Only 1,4,16,24 and 32 are allowed.
     */
    private int colorDepth;
    private java.util.List<CreatorVO> creators = new java.util.ArrayList<CreatorVO>();
    /**
     * Information about the technology used in the process of producing the digital image.
     */
    private ImageAcquisitionDeviceVO imageAcquisitionDevice;
    /**
     * Information for all image types which are scans from original material.
     */
    private ImageInfoVO imageInfo;
    /**
     * The type of the image content.
     */
    private ImageType imageType;
    /**
     * Described in common words, e.g. the place where the photographed object(s) were or are located.
     */
    private String location;
    /**
     * The Number of the original page.
     */
    private int pageNumber;
    private String shortDescription;
    private String creationDate;

    /**
     * Delivers the color depth of the digital image in bits. Only 1,4,16,24 and 32 are allowed.
     */
    public int getColorDepth()
    {
        return colorDepth;
    }

    /**
     * Delivers the list of creators of the image.
     */
    public java.util.List<CreatorVO> getCreators()
    {
        return creators;
    }

    /**
     * Delivers the image acquisition device, i. e. information about the technology used in the process of producing
     * the digital image.
     */
    public ImageAcquisitionDeviceVO getImageAcquisitionDevice()
    {
        return imageAcquisitionDevice;
    }

    /**
     * Delivers information for all image types which are scans from original material.
     */
    public ImageInfoVO getImageInfo()
    {
        return imageInfo;
    }

    /**
     * Delivers the image type, i. e. the type of the image content.
     */
    public ImageType getImageType()
    {
        return imageType;
    }

    /**
     * Delivers the location of the image, e.g. the place where the photographed object(s) were or are located,
     * described in common words.
     */
    public String getLocation()
    {
        return location;
    }

    /**
     * Delivers the page number of the image, i. e. the Number of the original page. Constraint: PageNumber is only
     * required for ImageType 'page'.
     */
    public int getPageNumber()
    {
        return pageNumber;
    }

    /**
     * Delivers the short description of the image, i. e. a short description about the image.
     */
    public String getShortDescription()
    {
        return shortDescription;
    }

    /**
     * Sets the color depth of the digital image in bits. Only 1,4,16,24 and 32 are allowed.
     * 
     * @param newVal newVal
     */
    public void setColorDepth(int newVal)
    {
        colorDepth = newVal;
    }

    /**
     * Sets the image acquisition device, i. e. information about the technology used in the process of producing the
     * digital image.
     * 
     * @param newVal newVal
     */
    public void setImageAcquisitionDevice(ImageAcquisitionDeviceVO newVal)
    {
        imageAcquisitionDevice = newVal;
    }

    /**
     * Sets information for all image types which are scans from original material.
     * 
     * @param newVal newVal
     */
    public void setImageInfo(ImageInfoVO newVal)
    {
        imageInfo = newVal;
    }

    /**
     * Sets the image type, i. e. the type of the image content.
     * 
     * @param newVal newVal
     */
    public void setImageType(ImageType newVal)
    {
        imageType = newVal;
    }

    /**
     * Sets the location of the image, e.g. the place where the photographed object(s) were or are located, described in
     * common words.
     * 
     * @param newVal newVal
     */
    public void setLocation(String newVal)
    {
        location = newVal;
    }

    /**
     * Sets the page number of the image, i. e. the Number of the original page. Constraint: PageNumber is only required
     * for ImageType 'page'.
     * 
     * @param newVal newVal
     */
    public void setPageNumber(int newVal)
    {
        pageNumber = newVal;
    }

    /**
     * Sets the short description of the image, i. e. a short description about the image.
     * 
     * @param newVal newVal
     */
    public void setShortDescription(String newVal)
    {
        shortDescription = newVal;
    }

    /**
     * Delivers the creation date of the image, i. e. the date when the image was taken.
     */
    public String getCreationDate()
    {
        return creationDate;
    }

    /**
     * Sets the creation date of the image, i. e. the date when the image was taken.
     * 
     * @param newVal
     */
    public void setCreationDate(String newVal)
    {
        creationDate = newVal;
    }
}