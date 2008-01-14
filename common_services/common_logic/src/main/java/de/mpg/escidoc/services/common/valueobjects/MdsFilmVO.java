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
import de.mpg.escidoc.services.common.valueobjects.metadata.FilmAcquisitionDeviceVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;

/**
 * This metadata set describes all kind of motion pictures like documentation, art, movies and simulations.
 * 
 * @revised by MuJ: 03.09.2007
 * @version $Revision: 611 $ $LastChangedDate: 2007-11-07 12:04:29 +0100 (Wed, 07 Nov 2007) $ by $Author: jmueller $
 * @updated 05-Sep-2007 10:54:29
 */
public class MdsFilmVO extends MetadataSetVO
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
     * The possible film types.
     * 
     * @updated 05-Sep-2007 10:54:29
     */
    public enum FilmType
    {
        VIDEO_ART, DOCUMENTARY, SIMULATION, ANIMATION, MIXED
    }

    /**
     * Alternative title of the film, e.g. translations of original title or sub-titles.
     */
    private TextVO alternativeTitle;
    private java.util.List<CreatorVO> creators = new java.util.ArrayList<CreatorVO>();
    /**
     * Information about the technology used in the process of producing the film.
     */
    private FilmAcquisitionDeviceVO filmAcquisitionDevice;
    private FilmType filmType;
    /**
     * The place where the motive on the film could be found. Described in common words.
     */
    private String location;
    /**
     * Description of the recording process.
     */
    private String recording;
    private String shortDescription;
    private String creationDate;

    /**
     * Delivers the alternative title of the film, e.g. translation of original title or sub-title.
     */
    public TextVO getAlternativeTitle()
    {
        return alternativeTitle;
    }

    /**
     * Delivers the list of creators of the film. Constraint: Only CreatorRoles 'Publisher', 'Artist' and 'Photographer'
     * are allowed.
     */
    public java.util.List<CreatorVO> getCreators()
    {
        return creators;
    }

    /**
     * Delivers the film acquisition device, i. e. information about the technology used in the process of producing the
     * film.
     */
    public FilmAcquisitionDeviceVO getFilmAcquisitionDevice()
    {
        return filmAcquisitionDevice;
    }

    /**
     * Delivers the type of the film.
     */
    public FilmType getFilmType()
    {
        return filmType;
    }

    /**
     * Delivers the location of the film, i. e. the place where the motive on the film could be found. Described in
     * common words.
     */
    public String getLocation()
    {
        return location;
    }

    /**
     * Delivers the description of the recording process.
     */
    public String getRecording()
    {
        return recording;
    }

    /**
     * Delivers the short description about the film.
     */
    public String getShortDescription()
    {
        return shortDescription;
    }

    /**
     * Sets the alternative title of the film, e.g. translation of original title or sub-title.
     * 
     * @param newVal newVal
     */
    public void setAlternativeTitle(TextVO newVal)
    {
        alternativeTitle = newVal;
    }

    /**
     * Sets the film acquisition device, i. e. information about the technology used in the process of producing the
     * film.
     * 
     * @param newVal newVal
     */
    public void setFilmAcquisitionDevice(FilmAcquisitionDeviceVO newVal)
    {
        filmAcquisitionDevice = newVal;
    }

    /**
     * Sets the type of the film.
     * 
     * @param newVal newVal
     */
    public void setFilmType(FilmType newVal)
    {
        filmType = newVal;
    }

    /**
     * Sets the location of the film, i. e. the place where the motive on the film could be found. Described in common
     * words.
     * 
     * @param newVal newVal
     */
    public void setLocation(String newVal)
    {
        location = newVal;
    }

    /**
     * Sets the description of the recording process.
     * 
     * @param newVal newVal
     */
    public void setRecording(String newVal)
    {
        recording = newVal;
    }

    /**
     * Sets the short description about the film.
     * 
     * @param newVal newVal
     */
    public void setShortDescription(String newVal)
    {
        shortDescription = newVal;
    }

    /**
     * Delivers the creation date of the film, i. e. the date when the film was taken.
     */
    public String getCreationDate()
    {
        return creationDate;
    }

    /**
     * Sets the creation date of the film, i. e. the date when the film was taken.
     * 
     * @param newVal
     */
    public void setCreationDate(String newVal)
    {
        creationDate = newVal;
    }
}