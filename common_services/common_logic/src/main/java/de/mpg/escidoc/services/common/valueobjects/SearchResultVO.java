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

import de.mpg.escidoc.services.common.valueobjects.interfaces.SearchResultElement;
import de.mpg.escidoc.services.common.valueobjects.interfaces.Searchable;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;

/**
 * Representation of an PubItem search result record.
 * This class is used only for JiBX transformations of search results.
 * In {@link XmlTransformingBean} it is transformed to another implementation of {@link SearchResultElement}.
 * 
 * @revised by MuJ: 28.08.2007
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @updated 05-Sep-2007 10:30:52
 */
public class SearchResultVO implements SearchResultElement
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
     * List of hits. Every hit in files contains the file reference and the text fragments of the search hit.
     */
    private java.util.List<SearchHitVO> searchHitList = new java.util.ArrayList<SearchHitVO>();
    private Searchable resultVO;

    /**
     * {@inheritDoc}
     */
    public java.util.List<SearchHitVO> getSearchHitList()
    {
        return searchHitList;
    }

    public Searchable getResultVO()
    {
        return resultVO;
    }

    public void setResultVO(Searchable resultVO)
    {
        this.resultVO = resultVO;
    }
    
}