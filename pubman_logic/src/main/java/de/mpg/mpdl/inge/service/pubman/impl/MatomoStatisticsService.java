package de.mpg.mpdl.inge.service.pubman.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.TreeMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.mpg.mpdl.inge.util.PropertyReader;

public class MatomoStatisticsService {

  private static final Logger logger = Logger.getLogger(MatomoStatisticsService.class);
  private static final String ANALYTICS_BASE_URI = PropertyReader.getProperty("inge.matomo.analytics.base.uri");
  private static final String ANALYTICS_SITE_ID = PropertyReader.getProperty("inge.matomo.analytics.site.id");
  private static final String ANALYTICS_TOKEN = PropertyReader.getProperty("inge.matomo.analytics.auth.token");

  private static final String PURE_OVERVIEW = "http://pubman.mpdl.mpg.de/pubman/faces/viewItemOverviewPage.jsp";
  private static final String PURE_FULLPAGE = "http://pubman.mpdl.mpg.de/pubman/faces/viewItemFullPage.jsp";
  private static final String PURE_ITEM = "http://pubman.mpdl.mpg.de/pubman/item/";
  private static final String PURE_FILE = "/component/";

  static ObjectMapper om = new ObjectMapper();


  public static Map<String, Integer> get124item(String id) throws Exception {
    Map<String, Integer> pageStatistics = new TreeMap<>();
    HttpResponse fullpage_response = Request.Get(prepareItemURL(id, "full", "month", "last12")).execute().returnResponse();
    JsonNode fullpage = om.readValue(fullpage_response.getEntity().getContent(), JsonNode.class);
    HttpResponse overview_response = Request.Get(prepareItemURL(id, "over", "month", "last12")).execute().returnResponse();
    JsonNode overview = om.readValue(overview_response.getEntity().getContent(), JsonNode.class);
    fullpage.fields().forEachRemaining(set -> {
      String key = set.getKey();
      int fullpage_visits = 0;
      int overview_visits = 0;

      if (overview.get(key).get(0) != null) {
        overview_visits = overview.get(key).get(0).get("nb_visits").asInt();
      }
      if (set.getValue().get(0) != null) {
        fullpage_visits = set.getValue().get(0).get("nb_visits").asInt();
      }

      pageStatistics.put(key, overview_visits + fullpage_visits);
    });
    return pageStatistics;

  }

  public static Map<String, Integer> get124file(String id, String file_id, String name) throws Exception {
    Map<String, Integer> fileStatistics = new TreeMap<>();
    HttpResponse file_response = Request.Get(prepareFileURL(id, file_id, name, "month", "last12")).execute().returnResponse();
    JsonNode downloads = om.readValue(file_response.getEntity().getContent(), JsonNode.class);
    downloads.fields().forEachRemaining(set -> {
      String key = set.getKey();
      int visits = 0;
      if (set.getValue().get(0) != null) {
        visits = set.getValue().get(0).get("nb_visits").asInt();
      }

      fileStatistics.put(key, visits);
    });
    return fileStatistics;
  }

  public static int getTotal4Item(String id) throws Exception {
    HttpResponse full_response = Request.Get(prepareItemURL(id, "full", "range", "2000-01-01,today")).execute().returnResponse();
    HttpResponse over_response = Request.Get(prepareItemURL(id, "over", "range", "2000-01-01,today")).execute().returnResponse();
    JsonNode fullpage = om.readValue(full_response.getEntity().getContent(), JsonNode.class);
    JsonNode overview = om.readValue(over_response.getEntity().getContent(), JsonNode.class);
    int total = 0, full = 0, over = 0;
    if (fullpage.get(0) != null) {
      full = fullpage.get(0).get("nb_visits").asInt();
    }
    if (overview.get(0) != null) {
      over = overview.get(0).get("nb_visits").asInt();
    }
    total = full + over;

    return total;

  }

  public static int getTotal4File(String id, String file_id, String name) throws IOException {
    HttpResponse file_response = Request.Get(prepareFileURL(id, file_id, name, "range", "2000-01-01,today")).execute().returnResponse();
    JsonNode file_downloads = om.readValue(file_response.getEntity().getContent(), JsonNode.class);
    int total = 0;
    if (file_downloads.get(0) != null) {
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
          .addParameter("format", "json").addParameter("showColumns", "nb_visits");
      if (what.equalsIgnoreCase("full")) {
        builder.addParameter("pageUrl", PURE_FULLPAGE + "?itemId=" + id);
      } else {
        builder.addParameter("pageUrl", PURE_OVERVIEW + "?itemId=" + id);
      }
      URI analytics_URI = builder.build();
      System.out.println(analytics_URI);
      return analytics_URI;

    } catch (URISyntaxException e) {
      logger.error(e.getMessage());
      e.printStackTrace();
    }
    return null;
  }

  private static URI prepareFileURL(String id, String file_id, String name, String period, String date) {
    URIBuilder builder;
    try {
      builder = new URIBuilder(new URI(ANALYTICS_BASE_URI));
      builder.addParameter("module", "API").addParameter("method", "Actions.getDownload").addParameter("idSite", ANALYTICS_SITE_ID)
          .addParameter("period", period).addParameter("date", date).addParameter("token_auth", ANALYTICS_TOKEN)
          .addParameter("format", "json").addParameter("showColumns", "nb_visits");
      builder.addParameter("downloadUrl", PURE_ITEM + id + PURE_FILE + file_id + "/" + name);
      URI analytics_URI = builder.build();
      System.out.println(analytics_URI);
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
    objectId = objectId.replaceAll("_", ":");
    objectId = objectId.replace("item", "escidoc");
    requests = getTotal4Item(objectId);

    return String.valueOf(requests);
  }

  /**
   * {@inheritDoc}
   */
  public static String getNumberOfFileDownloads(String objectId, String file_id, String name) throws Exception {
    int requests = 0;
    objectId = objectId.replaceAll("_", ":");
    objectId = objectId.replace("item", "escidoc");
    file_id = file_id.replaceAll("_", ":");
    file_id = file_id.replace("file", "escidoc");
    requests = getTotal4File(objectId, file_id, name);

    return String.valueOf(requests);
  }

}
