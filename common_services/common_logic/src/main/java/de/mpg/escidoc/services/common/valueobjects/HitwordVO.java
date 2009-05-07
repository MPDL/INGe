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
 * Representation of a hit word, especially the start and end index of the word in the text fragment data.
 * 
 * @revised by MuJ: 28.08.2007
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @updated 05-Sep-2007 10:30:48
 */
public class HitwordVO extends ValueObject
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
     * The end index of the word in the text fragment data.
     */
    private int endIndex;
    /**
     * The start index of the word in the text fragment data.
     */
    private int startIndex;

    /**
     * Delivers the end index of the word in the text fragment data.
     */
    public int getEndIndex()
    {
        return endIndex;
    }

    /**
     * Delivers the start index of the word in the text fragment data.
     */
    public int getStartIndex()
    {
        return startIndex;
    }

    /**
     * Sets the end index of the word in the text fragment data.
     * 
     * @param endIndex The end index to set.
     */
    public void setEndIndex(int endIndex)
    {
        this.endIndex = endIndex;
    }

    /**
     * Sets the start index of the word in the text fragment data.
     * 
     * @param startIndex The start index to set.
     */
    public void setStartIndex(int startIndex)
    {
        this.startIndex = startIndex;
    }
}