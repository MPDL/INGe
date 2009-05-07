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

package de.mpg.escidoc.services.common.valueobjects.metadata;

import de.mpg.escidoc.services.common.valueobjects.ValueObject;

/**
 * A description of the technology used in the process of producing a film.
 * 
 * @revised by MuJ: 28.08.2007
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @updated 06-Sep-2007 11:29:07
 */
public class FilmAcquisitionDeviceVO extends ValueObject
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
    private static final long serialVersionUID = 1L;

    /**
     * The possible device types of the film acquisition device.
     * 
     * @updated 06-Sep-2007 11:29:07
     */
    public enum DeviceType
    {
        CAMERA, COMPUTER
    }

    /**
     * The manufactures name for the device.
     */
    private String name;
    /**
     * A short description of the production process.
     */
    private String productionComment;
    /**
     * The type of the device which produced the image.
     */
    private DeviceType type;

    /**
     * Delivers the name of the film acquisition device.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Delivers the production comment of the film acquisition device. The production comment is a short description of
     * the production process.
     */
    public String getProductionComment()
    {
        return productionComment;
    }

    /**
     * Delivers the type of the film acquisition device, i. e. the type of the device which produced the image.
     */
    public DeviceType getType()
    {
        return type;
    }

    /**
     * Sets the name of the film acquisition device.
     * 
     * @param newVal
     */
    public void setName(String newVal)
    {
        name = newVal;
    }

    /**
     * Sets the production comment of the film acquisition device. The production comment is a short description of the
     * production process.
     * 
     * @param newVal
     */
    public void setProductionComment(String newVal)
    {
        productionComment = newVal;
    }

    /**
     * Sets the type of the film acquisition device, i. e. the type of the device which produced the image.
     * 
     * @param newVal
     */
    public void setType(DeviceType newVal)
    {
        type = newVal;
    }
}