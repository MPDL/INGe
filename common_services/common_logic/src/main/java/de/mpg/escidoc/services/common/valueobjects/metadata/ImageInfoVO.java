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
 * Scanned images contain information about the physical parameters of the original media and the resolution of the
 * scan.
 * 
 * @revised by MuJ: 29.08.2007
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @updated 05-Sep-2007 12:48:57
 */
public class ImageInfoVO extends ValueObject
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
     * The possible size units of the image.
     * 
     * @updated 05-Sep-2007 12:48:57
     */
    public enum SizeUnit
    {
        FOOT, MILE, INCH, KM, CM, M, MM, UM, NM
    }

    /**
     * The desired presentation width of the image in pixels.
     */
    private int displayPixelX;
    /**
     * The desired presentation height of the image in pixels.
     */
    private int displayPixelY;
    /**
     * The desired presentation resolution in its widths and height in pixels per inch.
     */
    private int displayPpi;
    /**
     * The desired presentation resolution in its widths in pixels per inch.
     */
    private int displayPpiX;
    /**
     * The desired presentation resolution in its heights in pixels per inch.
     */
    private int displayPpiY;
    /**
     * The width of the scan in pixels.
     */
    private int originalPixelX;
    /**
     * The height of the scan in pixels.
     */
    private int originalPixelY;
    /**
     * The resolution of the scan in its widths and height in pixels per inch.
     */
    private int originalPpi;
    /**
     * The resolution of the scan in its widths in pixels per inch.
     */
    private int originalPpiX;
    /**
     * The resolution of the scan in its heights in pixels per inch.
     */
    private int originalPpiY;
    /**
     * The unit of the original size parameters.
     */
    private SizeUnit originalSizeUnit;
    /**
     * The width of the original scanned area. Without a specified unit, the value is given in meter.
     */
    private int originalSizeX;
    /**
     * The height of the original scanned area. Without a specified unit, the value is given in meter.
     */
    private int originalSizeY;

    /**
     * Delivers the presentation width of the image in pixels.
     */
    public int getDisplayPixelX()
    {
        return displayPixelX;
    }

    /**
     * Delivers the presentation height of the image in pixels.
     */
    public int getDisplayPixelY()
    {
        return displayPixelY;
    }

    /**
     * Delivers the desired presentation resolution in its width and height in pixels per inch. Constraint: DisplayPpi
     * is required, if neither the DisplaySizeX/Y nor the DisplayPpiX/Y is given.
     */
    public int getDisplayPpi()
    {
        return displayPpi;
    }

    /**
     * Delivers the desired presentation resolution in its width in pixels per inch. Constraint: DisplayPpiX is
     * required, if neither the DisplaySizeX/Y nor the DisplayPpi is given.
     */
    public int getDisplayPpiX()
    {
        return displayPpiX;
    }

    /**
     * Delivers the desired presentation resolution in its height in pixels per inch. Constraint: DisplayPpiY is
     * required, if neither the DisplaySizeX/Y nor the DisplayPpi is given.
     */
    public int getDisplayPpiY()
    {
        return displayPpiY;
    }

    /**
     * Delivers the width of the scan in pixels.
     */
    public int getOriginalPixelX()
    {
        return originalPixelX;
    }

    /**
     * Delivers the height of the scan in pixels.
     */
    public int getOriginalPixelY()
    {
        return originalPixelY;
    }

    /**
     * Delivers the resolution of the scan in its widths and height in pixels per inch. Constraint: OriginalPpi is
     * required, if neither the OriginalSizeX/Y nor the OriginalPpiX/Y is given.
     */
    public int getOriginalPpi()
    {
        return originalPpi;
    }

    /**
     * Delivers the resolution of the scan in its width in pixels per inch. Constraint: OriginalPpiX is required, if
     * neither the OriginalSizeX/Y nor the OriginalPpi is given.
     */
    public int getOriginalPpiX()
    {
        return originalPpiX;
    }

    /**
     * Delivers the resolution of the scan in its height in pixels per inch. Constraint: OriginalPpiY is required, if
     * neither the OriginalSizeX/Y nor the OriginalPpi is given.
     */
    public int getOriginalPpiY()
    {
        return originalPpiY;
    }

    /**
     * Delivers the possible units of the original size parameters.
     */
    public SizeUnit getOriginalSizeUnit()
    {
        return originalSizeUnit;
    }

    /**
     * Delivers the original size of the image in the direction of the X-axis, i. e. the width of the original scanned
     * area. Without a specified unit, the value is given in meter.
     */
    public int getOriginalSizeX()
    {
        return originalSizeX;
    }

    /**
     * Delivers the original size of the image in the direction of the Y-axis, i. e. the height of the original scanned
     * area. Without a specified unit, the value is given in meter.
     */
    public int getOriginalSizeY()
    {
        return originalSizeY;
    }

    /**
     * Sets the presentation width of the image in pixels.
     * 
     * @param newVal
     */
    public void setDisplayPixelX(int newVal)
    {
        displayPixelX = newVal;
    }

    /**
     * Sets the presentation height of the image in pixels.
     * 
     * @param newVal
     */
    public void setDisplayPixelY(int newVal)
    {
        displayPixelY = newVal;
    }

    /**
     * Sets the desired presentation resolution in its width and height in pixels per inch. Constraint: DisplayPpi is
     * required, if neither the DisplaySizeX/Y nor the DisplayPpiX/Y is given.
     * 
     * @param newVal
     */
    public void setDisplayPpi(int newVal)
    {
        displayPpi = newVal;
    }

    /**
     * Sets the desired presentation resolution in its width in pixels per inch. Constraint: DisplayPpiX is required, if
     * neither the DisplaySizeX/Y nor the DisplayPpi is given.
     * 
     * @param newVal
     */
    public void setDisplayPpiX(int newVal)
    {
        displayPpiX = newVal;
    }

    /**
     * Sets the desired presentation resolution in its height in pixels per inch. Constraint: DisplayPpiY is required,
     * if neither the DisplaySizeX/Y nor the DisplayPpi is given.
     * 
     * @param newVal
     */
    public void setDisplayPpiY(int newVal)
    {
        displayPpiY = newVal;
    }

    /**
     * Sets the width of the scan in pixels.
     * 
     * @param newVal
     */
    public void setOriginalPixelX(int newVal)
    {
        originalPixelX = newVal;
    }

    /**
     * Sets the height of the scan in pixels.
     * 
     * @param newVal
     */
    public void setOriginalPixelY(int newVal)
    {
        originalPixelY = newVal;
    }

    /**
     * Sets the resolution of the scan in its widths and height in pixels per inch. Constraint: OriginalPpi is required,
     * if neither the OriginalSizeX/Y nor the OriginalPpiX/Y is given.
     * 
     * @param newVal
     */
    public void setOriginalPpi(int newVal)
    {
        originalPpi = newVal;
    }

    /**
     * Sets the resolution of the scan in its width in pixels per inch. Constraint: OriginalPpiX is required, if neither
     * the OriginalSizeX/Y nor the OriginalPpi is given.
     * 
     * @param newVal
     */
    public void setOriginalPpiX(int newVal)
    {
        originalPpiX = newVal;
    }

    /**
     * Sets the resolution of the scan in its height in pixels per inch. Constraint: OriginalPpiY is required, if
     * neither the OriginalSizeX/Y nor the OriginalPpi is given.
     * 
     * @param newVal
     */
    public void setOriginalPpiY(int newVal)
    {
        originalPpiY = newVal;
    }

    /**
     * Sets the possible units of the original size parameters.
     * 
     * @param newVal
     */
    public void setOriginalSizeUnit(SizeUnit newVal)
    {
        originalSizeUnit = newVal;
    }

    /**
     * Sets the original size of the image in the direction of the X-axis, i. e. the width of the original scanned area.
     * Without a specified unit, the value is given in meter.
     * 
     * @param newVal
     */
    public void setOriginalSizeX(int newVal)
    {
        originalSizeX = newVal;
    }

    /**
     * Sets the original size of the image in the direction of the Y-axis, i. e. the height of the original scanned
     * area. Without a specified unit, the value is given in meter.
     * 
     * @param newVal
     */
    public void setOriginalSizeY(int newVal)
    {
        originalSizeY = newVal;
    }
}