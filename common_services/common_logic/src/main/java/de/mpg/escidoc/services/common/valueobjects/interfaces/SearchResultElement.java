/**
 * 
 */
package de.mpg.escidoc.services.common.valueobjects.interfaces;

import de.mpg.escidoc.services.common.valueobjects.SearchHitVO;

/**
 * This interface is used to provide a common datatype for a search result.
 * @author endres
 *
 */
public interface SearchResultElement
{
    /**
     * Delivers the list of search hits.
     * 
     * @return A list of {@link SearchHitVO} containing the textual occurrences of the hits.
     */
    public java.util.List<SearchHitVO> getSearchHitList();

}
