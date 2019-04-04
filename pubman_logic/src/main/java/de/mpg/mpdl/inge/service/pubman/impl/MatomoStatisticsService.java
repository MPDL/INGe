package de.mpg.mpdl.inge.service.pubman.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.mpg.mpdl.inge.util.PropertyReader;

public class MatomoStatisticsService {

  private static final Logger logger = Logger.getLogger(MatomoStatisticsService.class);
  private static final String INSTANCE_URI = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_URL);
  private static final String INSTANCE_CONTEXT_PATH = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_CONTEXT_PATH);
  private static final String ANALYTICS_BASE_URI = PropertyReader.getProperty(PropertyReader.INGE_MATOMO_ANALYTICS_BASE_URI);
  private static final String ANALYTICS_SITE_ID = PropertyReader.getProperty(PropertyReader.INGE_MATOMO_ANALYTICS_SITE_ID);
  private static final String ANALYTICS_TOKEN = PropertyReader.getProperty(PropertyReader.INGE_MATOMO_ANALYTICS_AUTH_TOKEN);

  //  private static final String PURE_ITEM = INSTANCE_URI + INSTANCE_CONTEXT_PATH + "/item/";
  // used for Testing on another instance
  private static final String PURE_ITEM = "https://qa.pure.mpdl.mpg.de/pubman/item/";
  private static final String PURE_FILE = "/component/";
  private static final String DATE_RANGE = "2000-01-01,today";


  static ObjectMapper om = new ObjectMapper();


  public static Map<String, Integer> get124item(String id) throws Exception {
    Map<String, Integer> pageStatistics = new TreeMap<>();
    HttpResponse item_response = Request.Get(prepareItemURL(id, "full", "month", "last12")).execute().returnResponse();
    JsonNode item = om.readValue(item_response.getEntity().getContent(), JsonNode.class);
    Iterator<Entry<String, JsonNode>> iteratorRpage = item.fields();
    while (iteratorRpage.hasNext()) {

      Entry<String, JsonNode> set = iteratorRpage.next();
      String key = set.getKey();
      int item_visits = 0;

      if (set.getValue() != null && set.getValue().get(0) != null && set.getValue().get(0).get("nb_visits") != null) {
        item_visits = set.getValue().get(0).get("nb_visits").asInt();
      }

      pageStatistics.put(key, item_visits);
    } ;
    return pageStatistics;

  }

  public static Map<String, Integer> get124file(String id, String file_id, String name) throws Exception {
    Map<String, Integer> fileStatistics = new TreeMap<>();
    HttpResponse fileResponse = Request.Get(prepareFileURL(id, "pure", file_id, name, "month", "last12")).execute().returnResponse();
    JsonNode downloads = om.readValue(fileResponse.getEntity().getContent(), JsonNode.class);

    Iterator<Entry<String, JsonNode>> iteratorDownloads = downloads.fields();
    while (iteratorDownloads.hasNext()) {

      Entry<String, JsonNode> set = iteratorDownloads.next();
      String key = set.getKey();
      int download_count = 0;

      //      if (set.getValue() != null && set.getValue().get("nb_visits") != null) {
      if (set.getValue() != null && set.getValue().get(0) != null && set.getValue().get(0).get("nb_visits") != null) {
        download_count = set.getValue().get(0).get("nb_visits").asInt();
      }

      fileStatistics.put(key, download_count);
    } ;
    return fileStatistics;
  }

  public static int getTotal4Item(String id) throws Exception {
    HttpResponse itemTotalResponse = Request.Get(prepareItemURL(id, "full", "range", DATE_RANGE)).execute().returnResponse();

    JsonNode totalJson = om.readValue(itemTotalResponse.getEntity().getContent(), JsonNode.class);
    int totalCount = 0;
    if (totalJson != null && totalJson.get(0) != null && totalJson.get(0).get("nb_visits") != null) {
      totalCount = totalJson.get(0).get("nb_visits").asInt();
    }
    return totalCount;

  }

  public static int getTotal4File(String id, String file_id, String name) throws IOException {
    HttpResponse file_response = Request.Get(prepareFileURL(id, "pure", file_id, name, "range", DATE_RANGE)).execute().returnResponse();
    JsonNode file_downloads = om.readValue(file_response.getEntity().getContent(), JsonNode.class);
    int total = 0;
    if (file_downloads != null && file_downloads.get(0) != null && file_downloads.get(0).get("nb_visits") != null) {
      total = file_downloads.get(0).get("nb_visits").asInt();
    }
    return total;
  }

  private static URI prepareItemURL(String id, String what, String period, String date) {
    URIBuilder builder;
    try {
      builder = new URIBuilder(new URI(ANALYTICS_BASE_URI));
      builder.addParameter("module", "API").addParameter("method", "Actions.getPageUrl").addParameter("idSite", ANALYTICS_SITE_ID)
          .addParameter("period", period).addParameter("date", date).addParameter("token_auth", ANALYTICS_TOKEN)
          .addParameter("format", "json");
      builder.addParameter("pageUrl", PURE_ITEM + id);

      URI analytics_URI = builder.build();
      logger.info("Requesting item stats 4 " + analytics_URI.toString());
      return analytics_URI;

    } catch (URISyntaxException e) {
      logger.error(e.getMessage());
      e.printStackTrace();
    }
    return null;
  }

  private static URI prepareFileURL(String id, String what, String file_id, String name, String period, String date) {
    URIBuilder builder;
    String idForFileGet = null;
    if ((id.substring(id.indexOf("_"))).contains("_"))
    {
      idForFileGet = id.substring(0, id.lastIndexOf("_"));
    } else {
      idForFileGet = id;
    }

    try {
      builder = new URIBuilder(new URI(ANALYTICS_BASE_URI));
      builder.addParameter("module", "API").addParameter("method", "Actions.getDownload").addParameter("idSite", ANALYTICS_SITE_ID)
          .addParameter("period", period).addParameter("date", date).addParameter("token_auth", ANALYTICS_TOKEN)
          .addParameter("format", "json").addParameter("downloadUrl", PURE_ITEM + idForFileGet + PURE_FILE + file_id + "/" + name);

      URI analytics_URI = builder.build();
      logger.info("Requesting item stats 4 " + analytics_URI.toString());
      return analytics_URI;
    } catch (URISyntaxException e) {
      logger.error(e.getMessage());
      e.printStackTrace();
    }

    return null;
  }

  /**
   * {@inheritDoc}
   */
  public static String getNumberOfItemOrFileRequests(String objectId) throws Exception {
    int requests = 0;
    requests = getTotal4Item(objectId);

    return String.valueOf(requests);
  }

  /**
   * {@inheritDoc}
   */
  public static String getNumberOfFileDownloads(String objectId, String file_id, String name) throws Exception {
    int requests = 0;
    requests = getTotal4File(objectId, file_id, name);

    return String.valueOf(requests);
  }

}
