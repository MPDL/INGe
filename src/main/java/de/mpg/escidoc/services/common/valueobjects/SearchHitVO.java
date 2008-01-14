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
 * Representation of a search hit.
 * 
 * @revised by MuJ: 28.08.2007
 * @updated 05-Sep-2007 10:30:53
 */
public class SearchHitVO extends ValueObject
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
     * The possible search hit types.
     * 
     * @updated 05-Sep-2007 10:30:53
     */
    public enum SearchHitType
    {
        METADATA, FULLTEXT
    }

    /**
     * List of text fragments belonging to this search hit.
     */
    private java.util.List<TextFragmentVO> textFragmentList = new java.util.ArrayList<TextFragmentVO>();
    private SearchHitType type;
    /**
     * This PubFileRO points to the File containing the search hit. When the search hit is of type metadata, this
     * property should not be set.
     */
    private PubFileRO hitReference;

    /**
     * Delivers the list of text fragments belonging to this search hit.
     */
    public java.util.List<TextFragmentVO> getTextFragmentList()
    {
        return textFragmentList;
    }

    /**
     * Delivers the type of the search hit.
     */
    public SearchHitType getType()
    {
        return type;
    }

    /**
     * Sets the type of the search hit.
     * 
     * @param newVal
     */
    public void setType(SearchHitType newVal)
    {
        type = newVal;
    }

    /**
     * Delivers the reference to the file containing the search hit. When the search hit is of type metadata, this
     * property should not be set.
     */
    public PubFileRO getHitReference()
    {
        return hitReference;
    }

    /**
     * Sets the reference to the file containing the search hit. When the search hit is of type metadata, this property
     * should not be set.
     * 
     * @param newVal
     */
    public void setHitReference(PubFileRO newVal)
    {
        hitReference = newVal;
    }
}