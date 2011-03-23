/**
 * 
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
