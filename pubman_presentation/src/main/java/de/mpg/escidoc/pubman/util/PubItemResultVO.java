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
* Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman.util;

import java.util.List;

import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.SearchHitVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;

/**
 * @author endres
 *
 */
public class PubItemResultVO extends PubItemVO {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * List of hits. Every hit in files contains the file reference and the text fragments of the search hit.
     */
    private java.util.List<SearchHitVO> searchHitList;
    //= new java.util.ArrayList<SearchHitVO>();

    private float score;

    /**
     * Delivers the list of search hits.
     */
    public List<SearchHitVO> getSearchHitList()
    {
        return searchHitList;
    }

    public PubItemResultVO(ItemVO itemVO, List<SearchHitVO> searchHits, float score )
    {
        super(itemVO);
        if (searchHits.size()>0)
        {
            this.searchHitList = new java.util.ArrayList<SearchHitVO>();
            this.searchHitList = searchHits;
            this.score = score;
        }
    }

    public void setScore(float score)
    {
        this.score = score;
    }

    public float getScore()
    {
        return score;
    }
}
