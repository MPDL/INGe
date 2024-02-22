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

package de.mpg.mpdl.inge.pubman.web.browseBy;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.node.ObjectNode;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.CalendarInterval;
import co.elastic.clients.elasticsearch._types.aggregations.DateHistogramAggregate;
import co.elastic.clients.elasticsearch._types.aggregations.DateHistogramBucket;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.search.ResponseBody;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionRO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.LinkVO;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl;
import de.mpg.mpdl.inge.service.util.SearchUtils;
import de.mpg.mpdl.inge.util.PropertyReader;
import jakarta.faces.bean.ManagedBean;
import jakarta.faces.bean.SessionScoped;

/**
 *
 * Session Bean for Browse By
 *
 * @author kleinfe1 (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
@ManagedBean(name = "BrowseBySessionBean")
@SessionScoped
@SuppressWarnings("serial")
public class BrowseBySessionBean extends FacesBean {
  private static final Logger logger = LogManager.getLogger(BrowseBySessionBean.class);

  public static final char[] CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÜ".toCharArray();

  private List<LinkVO> searchResults;
  private String currentCharacter = "A";
  private String dateMode = "published";
  private String dateType = "published";
  //  private String pubContentModel = "";
  private String query = "q";
  private String selectedValue = "persons";
  private String[] characters = null;
  private boolean showChars = true;
  // private int yearStart;

  private Map<String, Long> yearMap = new TreeMap<>();

  public BrowseBySessionBean() {
    //    try {
    //      this.pubContentModel = PropertyReader.getProperty(PropertyReader.ESCIDOC_FRAMEWORK_ACCESS_CONTENT-MODEL_ID_PUBLICATION);
    //    } catch (final Exception e) {
    //      BrowseBySessionBean.logger.warn("Could not read property content model.", e);
    //    }
  }

  public void clear() {
    this.currentCharacter = "A";
    this.selectedValue = "persons";
    this.showChars = true;
    this.query = "q";
    this.dateType = "published";
  }

  public List<String> getControlledVocabs() {
    List<String> vocabs = new ArrayList<>();
    try {
      String vocabsStr = PropertyReader.getProperty(PropertyReader.INGE_CONE_SUBJECTVOCAB);
      if (null != vocabsStr && !vocabsStr.trim().isEmpty()) {
        String[] vocabsArr = vocabsStr.split(";");
        for (String s : vocabsArr) {
          vocabs.add(s.trim());
        }
      }
    } catch (Exception e) {
      logger.error("Could not read Property: '" + PropertyReader.INGE_CONE_SUBJECTVOCAB + "'", e);
    }
    return vocabs;
  }

  public List<LinkVO> getSearchResults() {
    return this.searchResults;
  }

  public void setSearchResults(List<LinkVO> searchResults) {
    this.searchResults = searchResults;
  }

  public String getCurrentCharacter() {
    return this.currentCharacter;
  }

  public void setCurrentCharacter(String currentCharacter) {
    this.currentCharacter = currentCharacter;
  }

  public String getSelectedValue() {
    return this.selectedValue;
  }

  public void setSelectedValue(String selectedValue) {
    this.selectedValue = selectedValue;
  }

  public int getMaxDisplay() {
    int maxDisplay = 100;
    return maxDisplay;
  }

  /**
   * This method checks weather the searchResult from CoNE has to be devided into character,
   * according to the value of 'maxDisplay'
   *
   * @return
   */
  public boolean isShowChars() {
    return this.showChars;
  }

  public void setShowChars() {
    if ("year".equals(this.selectedValue)) {
      this.showChars = false;
    } else {
      this.showChars = "persons".equals(this.selectedValue);

      if (!this.showChars) {
        List<LinkVO> all = this.getConeAll();
        this.showChars = (all.size() > this.getMaxDisplay());
      }

      if (this.showChars) {
        SortedSet<Character> characters = new TreeSet<>();

        for (int i = 0; i < BrowseBySessionBean.CHARACTERS.length; i++) {
          characters.add(BrowseBySessionBean.CHARACTERS[i]);
        }

        this.characters = new String[characters.size()];
        int counter = 0;

        for (Character character : characters) {
          this.characters[counter] = character.toString();
          counter++;
        }
      }
    }
  }

  /**
   * Call the cone service to retrieve all browse by values.
   *
   * @return
   */
  public List<LinkVO> getConeAll() {
    List<LinkVO> links = new ArrayList<>();

    try {
      URL coneUrl =
          new URL(PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL) + this.selectedValue + "/all?format=options&lang=en");
      URLConnection conn = coneUrl.openConnection();
      HttpURLConnection httpConn = (HttpURLConnection) conn;
      int responseCode = httpConn.getResponseCode();

      switch (responseCode) {
        case 200:
          logger.debug("Cone Service responded with 200.");
          break;
        default:
          throw new RuntimeException(
              "An error occurred while calling Cone Service: " + responseCode + ": " + httpConn.getResponseMessage());
      }

      InputStreamReader isReader = new InputStreamReader(coneUrl.openStream(), StandardCharsets.UTF_8);
      BufferedReader bReader = new BufferedReader(isReader);
      String line = "";
      while (null != (line = bReader.readLine())) {
        String[] parts = line.split("\\|");
        if (2 == parts.length) {
          LinkVO link = new LinkVO(parts[1], parts[1]);
          links.add(link);
        }
      }

      isReader.close();
      httpConn.disconnect();
    } catch (Exception e) {
      logger.warn("An error occurred while calling the Cone service.", e);
      return null;
    }

    return links;
  }

  private void fillDateMap(String... indexes)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    Query queryBuilder =
        SearchUtils.baseElasticSearchQueryBuilder(ApplicationBean.INSTANCE.getPubItemService().getElasticSearchIndexFields(),
            PubItemServiceDbImpl.INDEX_PUBLIC_STATE, ItemVersionRO.State.RELEASED.name());

    //SearchRetrieveRequestVO srr = new SearchRetrieveRequestVO(queryBuilder, 0, 0); // Limit 0, da nur Aggregationen interessieren

    SearchRequest.Builder sr = new SearchRequest.Builder();
    sr.size(0);

    for (String index : indexes) {
      sr.aggregations(index,
          Aggregation.of(a -> a.dateHistogram(dh -> dh.field(index).calendarInterval(CalendarInterval.Year).minDocCount(1))));

      /*
      AggregationBuilder aggBuilder =
          AggregationBuilders.dateHistogram(index).field(index).dateHistogramInterval(DateHistogramInterval.YEAR).minDocCount(1);
      srr.getAggregationBuilders().add(aggBuilder);
      
       */
    }

    ResponseBody<ObjectNode> resp = ApplicationBean.INSTANCE.getPubItemService().searchDetailed(sr.build(), null);

    this.yearMap.clear();
    for (String index : indexes) {
      DateHistogramAggregate ag = resp.aggregations().get(index).dateHistogram();

      for (DateHistogramBucket entry : ag.buckets().array()) {

        String year = entry.keyAsString().substring(0, 4);
        if (this.yearMap.containsKey(year)) {
          this.yearMap.put(year, this.yearMap.get(year) + entry.docCount());
        } else {
          this.yearMap.put(year, entry.docCount());
        }
      }

    }
  }


  /**
   * Searches the rep for the oldest year.
   *
   * @throws IngeApplicationException
   * @throws AuthorizationException
   * @throws AuthenticationException
   * @throws IngeTechnicalException
   */
  public void setYearPublished() {
    try {
      fillDateMap(PubItemServiceDbImpl.INDEX_METADATA_DATE_PUBLISHED_IN_PRINT, PubItemServiceDbImpl.INDEX_METADATA_DATE_PUBLISHED_ONLINE);
    } catch (Exception e) {
      logger.error("An error occurred while calling setYearPublished.", e);
    }
  }

  public void setYearStartAny() {
    try {
      fillDateMap(PubItemServiceDbImpl.INDEX_METADATA_DATE_PUBLISHED_IN_PRINT, PubItemServiceDbImpl.INDEX_METADATA_DATE_PUBLISHED_ONLINE,
          PubItemServiceDbImpl.INDEX_METADATA_DATE_ACCEPTED, PubItemServiceDbImpl.INDEX_METADATA_DATE_SUBMITTED,
          PubItemServiceDbImpl.INDEX_METADATA_DATE_MODIFIED, PubItemServiceDbImpl.INDEX_METADATA_DATE_CREATED);
    } catch (Exception e) {
      logger.error("An error occurred while calling setYearStartAny.", e);
    }
  }

  public String getQuery() {
    return this.query;
  }

  public void setQuery(String query) {
    this.query = query;
  }

  public String getDateType() {
    return this.dateType;
  }

  public void setDateType(String dateType) {
    this.dateType = dateType;
  }

  //  public String getPubContentModel() {
  //    return this.pubContentModel;
  //  }

  public String[] getCharacters() {
    return this.characters;
  }

  public void setCharacters(String[] characters) {
    this.characters = characters;
  }

  public String getDateMode() {
    return this.dateMode;
  }

  public void setDateMode(String dateMode) {
    this.dateMode = dateMode;
  }

  public Map<String, Long> getYearMap() {
    return this.yearMap;
  }

  public String[] getYearMapSortedKeyArray() {
    String[] keys = this.yearMap.keySet().toArray(new String[0]);
    ArrayUtils.reverse(keys);

    return keys;
  }

  public void setYearMap(Map<String, Long> yearMap) {
    this.yearMap = yearMap;
  }

}
