/**
 *
 */
package de.mpg.mpdl.inge.model.valueobjects.interfaces;

import de.mpg.mpdl.inge.model.valueobjects.SearchHitVO;

/**
 * This interface is used to provide a common datatype for a search result.
 *
 * @author endres
 *
 */
public interface SearchResultElement {


  /**
   * Delivers the list of search hits.
   *
   * @return A list of {@link SearchHitVO} containing the textual occurrences of the hits.
   */
  java.util.List<SearchHitVO> getSearchHitList();

  float getScore();

}
