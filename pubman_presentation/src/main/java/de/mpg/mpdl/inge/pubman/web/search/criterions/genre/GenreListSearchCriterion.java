/*
 * 
 * CDDL HEADER START
 * 
 * The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with
 * the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or
 * http://www.escidoc.org/license. See the License for the specific language governing permissions
 * and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License
 * file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with
 * the fields enclosed by brackets "[]" replaced with your own identifying information: Portions
 * Copyright [yyyy] [name of copyright owner]
 * 
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */
package de.mpg.mpdl.inge.pubman.web.search.criterions.genre;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.DegreeType;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.Genre;
import de.mpg.mpdl.inge.pubman.web.search.SearchParseException;
import de.mpg.mpdl.inge.pubman.web.search.criterions.SearchCriterionBase;
import de.mpg.mpdl.inge.pubman.web.search.criterions.enums.GenreSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.operators.LogicalOperator;
import de.mpg.mpdl.inge.pubman.web.search.criterions.operators.Parenthesis;
import de.mpg.mpdl.inge.pubman.web.search.criterions.standard.DegreeSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.InternationalizationHelper;

@SuppressWarnings("serial")
public class GenreListSearchCriterion extends SearchCriterionBase {
  private Map<Genre, Boolean> genreMap = new LinkedHashMap<Genre, Boolean>();
  private Map<DegreeType, Boolean> degreeMap = new LinkedHashMap<DegreeType, Boolean>();

  public GenreListSearchCriterion() {
    this.initGenreMap();
    this.initDegreeMap();
  }

  /**
   * Initializes a sorted map containing all Genres as key and their selection state as value. The
   * map is sorted by the label of the genre in the given language
   * 
   * @return
   */
  public Map<Genre, Boolean> initGenreMap() {

    // first create a map with genre as key and the label as value
    final Map<Genre, String> genreLabelMap = new LinkedHashMap<Genre, String>();
    final InternationalizationHelper i18nHelper = (InternationalizationHelper) FacesTools.findBean("InternationalizationHelper");
    for (final Genre g : Genre.values()) {
      genreLabelMap.put(g, i18nHelper.getLabel("ENUM_GENRE_" + g.name()));
    }


    // Then create a list with the map entries and sort the list by the label
    final List<Map.Entry<Genre, String>> sortedGenreList = new LinkedList<Map.Entry<Genre, String>>(genreLabelMap.entrySet());
    Collections.sort(sortedGenreList, new Comparator<Map.Entry<Genre, String>>() {
      @Override
      public int compare(Map.Entry<Genre, String> o1, Map.Entry<Genre, String> o2) {
        return (o1.getValue()).compareTo(o2.getValue());
      }
    });


    // now fill the genre map with the ordered genres
    // genreMap = new LinkedHashMap<Genre, Boolean>();

    final Map<Genre, Boolean> oldValMap = new LinkedHashMap<Genre, Boolean>(this.genreMap);
    this.genreMap.clear();
    Entry<Genre, String> thesisEntry = null;
    for (final Entry<Genre, String> entry : sortedGenreList) {
      if (!entry.getKey().equals(Genre.THESIS)) {
        if (oldValMap.get(entry.getKey()) == null) {
          this.genreMap.put(entry.getKey(), false);
        } else {
          this.genreMap.put(entry.getKey(), oldValMap.get(entry.getKey()));
        }
      } else {
        thesisEntry = entry;
      }

    }
    if (thesisEntry != null) {
      if (oldValMap.get(thesisEntry.getKey()) == null) {
        this.genreMap.put(thesisEntry.getKey(), false);
      } else {
        this.genreMap.put(thesisEntry.getKey(), oldValMap.get(thesisEntry.getKey()));
      }
    }

    return this.genreMap;
  }

  private Map<DegreeType, Boolean> initDegreeMap() {

    // degreeMap = new LinkedHashMap<DegreeType, Boolean>();
    for (final DegreeType dt : DegreeType.values()) {
      this.degreeMap.put(dt, false);
    }
    return this.degreeMap;
  }



  // SP: Wenn gleichzeitig auf die Methode zugegriffen wird, dann kann eine ConcurrentModificationException auftreten 
  public synchronized Genre[] getGenreList() {
    final Genre[] genreArray = new Genre[0];
    return this.genreMap.keySet().toArray(genreArray);
  }

  public DegreeType[] getDegreeList() {
    final DegreeType[] degreeArray = new DegreeType[0];
    return this.degreeMap.keySet().toArray(degreeArray);
  }


  //  @Override
  //  public String toCqlString(Index indexName) throws SearchParseException {
  //    return SearchCriterionBase.scListToCql(indexName, this.getGenreSearchCriterions(), false);
  //  }

  @Override
  public String getQueryStringContent() {
    final StringBuffer sb = new StringBuffer();


    boolean allGenres = true;
    boolean allDegrees = true;

    int i = 0;
    for (final Entry<Genre, Boolean> entry : this.genreMap.entrySet()) {
      if (entry.getValue()) {
        if (i > 0) {
          sb.append("|");
        }

        sb.append(entry.getKey().name());
        i++;
      } else {
        allGenres = false;
      }
    }



    if (this.genreMap.get(Genre.THESIS)) {
      sb.append("||");
      int j = 0;
      for (final Entry<DegreeType, Boolean> entry : this.degreeMap.entrySet()) {
        if (entry.getValue()) {
          if (j > 0) {
            sb.append("|");
          }

          sb.append(entry.getKey().name());
          j++;
        } else {
          allDegrees = false;
        }
      }


    }

    if (!allGenres || !allDegrees) {
      return sb.toString();
    } else {
      return null;
    }



  }

  @Override
  public void parseQueryStringContent(String content) {
    // Split by '||', which have no backslash before
    final String[] genreDegreeParts = content.split("(?<!\\\\)\\|\\|");



    for (final Entry<Genre, Boolean> e : this.genreMap.entrySet()) {
      e.setValue(false);
    }

    for (final Entry<DegreeType, Boolean> e : this.degreeMap.entrySet()) {
      e.setValue(false);
    }

    // Split by '|', which have no backslash before and no other '|' after
    final String[] genreParts = genreDegreeParts[0].split("(?<!\\\\)\\|(?!\\|)");
    for (final String genre : genreParts) {
      final Genre g = Genre.valueOf(genre);
      if (g == null) {
        throw new RuntimeException("Invalid genre: " + genre);
      }
      this.genreMap.put(g, true);
    }

    if (genreDegreeParts.length > 1 && !genreDegreeParts[1].trim().isEmpty()) {
      final String[] degreeParts = genreDegreeParts[1].split("(?<!\\\\)\\|(?!\\|)");
      for (final String degree : degreeParts) {
        final DegreeType d = DegreeType.valueOf(degree);
        if (d == null) {
          throw new RuntimeException("Invalid degree type: " + degree);
        }
        this.degreeMap.put(d, true);
      }
    }



  }

  /**
   * List is empty if either all genres or degrees are selected or all are deselected
   */
  @Override
  public boolean isEmpty(QueryType queryType) {

    boolean genreSelected = false;
    boolean genreDeselected = false;
    boolean degreeSelected = false;
    boolean degreeDeselected = false;
    for (final Entry<Genre, Boolean> entry : this.genreMap.entrySet()) {
      if (entry.getValue()) {
        genreSelected = true;
      } else {
        genreDeselected = true;
      }
    }

    if (this.genreMap.get(Genre.THESIS)) {
      for (final Entry<DegreeType, Boolean> entry : this.degreeMap.entrySet()) {
        if (entry.getValue()) {
          degreeSelected = true;
        } else {
          degreeDeselected = true;
        }
      }
    }

    final boolean allGenreSelected = genreSelected && !genreDeselected;
    final boolean noGenreSelected = !genreSelected && genreDeselected;
    final boolean allDegreesSelected = degreeSelected && !degreeDeselected;
    final boolean noDegreeSelected = !degreeSelected && degreeDeselected;

    /*
     * System.out.println("All Genres: " + allGenreSelected); System.out.println("No Genres: " +
     * noGenreSelected); System.out.println("All Degrees: " + allDegreesSelected);
     * System.out.println("No Degrees: " + noDegreeSelected);
     */

    return (allGenreSelected && allDegreesSelected) || (allGenreSelected && noDegreeSelected) || (noGenreSelected);
    // return (genreSelected && !genreDeselected && )

    // return !(genreOrDegreeSelected && genreOrDegreeDeselected);
  }



  public List<SearchCriterionBase> getGenreSearchCriterions() {

    boolean genreSelected = false;
    boolean genreDeselected = false;

    boolean degreeSelected = false;
    boolean degreeDeselected = false;

    // boolean allGenres = true;


    // boolean allDegrees = true;

    final List<SearchCriterionBase> returnList = new ArrayList<SearchCriterionBase>();
    final List<SearchCriterionBase> degreeCriterionsList = new ArrayList<SearchCriterionBase>();
    returnList.add(new Parenthesis(SearchCriterion.OPENING_PARENTHESIS));
    int i = 0;
    for (final Entry<Genre, Boolean> entry : this.genreMap.entrySet()) {
      if (entry.getValue() && i > 0) {
        returnList.add(new LogicalOperator(SearchCriterion.OR_OPERATOR));
      }

      if (entry.getValue()) {



        if (Genre.THESIS.equals(entry.getKey())) {

          degreeCriterionsList.add(new LogicalOperator(SearchCriterion.AND_OPERATOR));
          degreeCriterionsList.add(new Parenthesis(SearchCriterion.OPENING_PARENTHESIS));

          int j = 0;
          for (final Entry<DegreeType, Boolean> degreeEntry : this.degreeMap.entrySet()) {
            if (degreeEntry.getValue() && j > 0) {
              degreeCriterionsList.add(new LogicalOperator(SearchCriterion.OR_OPERATOR));
            }

            if (degreeEntry.getValue()) {
              degreeSelected = true;
              final DegreeSearchCriterion dsc = new DegreeSearchCriterion();
              dsc.setSearchString(degreeEntry.getKey().name());
              degreeCriterionsList.add(dsc);
              j++;
            } else {
              degreeDeselected = true;
            }
          }
          degreeCriterionsList.add(new Parenthesis(SearchCriterion.CLOSING_PARENTHESIS));



        }



        if (Genre.THESIS.equals(entry.getKey()) && (degreeSelected && degreeDeselected)) {
          returnList.add(new Parenthesis(SearchCriterion.OPENING_PARENTHESIS));


        }

        genreSelected = true;
        final GenreSearchCriterion gc = new GenreSearchCriterion();
        gc.setSelectedEnum(entry.getKey());
        returnList.add(gc);
        i++;


        if (Genre.THESIS.equals(entry.getKey()) && (degreeSelected && degreeDeselected)) {
          returnList.addAll(degreeCriterionsList);
          returnList.add(new Parenthesis(SearchCriterion.CLOSING_PARENTHESIS));


        }

      } else {
        genreDeselected = true;
        // allGenres = false;
      }

    }

    returnList.add(new Parenthesis(SearchCriterion.CLOSING_PARENTHESIS));


    if ((genreSelected && genreDeselected) || (degreeSelected && degreeDeselected)) {
      return returnList;
    } else {
      return null;
    }
  }

  // SP: Wenn gleichzeitig auf die Methode zugegriffen wird, dann kann eine ConcurrentModificationException auftreten 
  public synchronized Map<Genre, Boolean> getGenreMap() {
    this.initGenreMap();
    return this.genreMap;
  }


  public void setGenreMap(Map<Genre, Boolean> genreMap) {
    this.genreMap = genreMap;
  }


  public Map<DegreeType, Boolean> getDegreeMap() {
    return this.degreeMap;
  }

  public void setDegreeMap(Map<DegreeType, Boolean> degreeMap) {
    this.degreeMap = degreeMap;
  }

  @Override
  public Query toElasticSearchQuery() throws SearchParseException, IngeTechnicalException {
    return SearchCriterionBase.scListToElasticSearchQuery(this.getGenreSearchCriterions());
  }

  @Override
  public String getElasticSearchNestedPath() {
    return null;
  }

  /*
   * @Override public SearchCriterion getSearchCriterion() { return
   * SearchCriterion.GENRE_DEGREE_LIST; }
   */


}
