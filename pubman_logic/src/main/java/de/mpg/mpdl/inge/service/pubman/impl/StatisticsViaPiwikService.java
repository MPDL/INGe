package de.mpg.mpdl.inge.service.pubman.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.TreeMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StatisticsViaPiwikService {

  static ObjectMapper om = new ObjectMapper();

  static String pageUrl =
      //"https://analytics.mpdl.mpg.de/?module=API&method=Actions.getPageUrl&pageUrl=http://pubman.mpdl.mpg.de/pubman/faces/viewItemOverviewPage.jsp?itemId=escidoc:2593630&idSite=1&period=month&date=last12&token_auth=38cc64c1f177e45ec36f2809a910e89a&format=json&showColumns=nb_visits";
      "https://analytics.mpdl.mpg.de?module=API&method=Actions.getPageUrl&idSite=1&period=range&date=2000-01-01%2Ctoday&token_auth=f65d066e35cc1d8974c1427d364ad830&format=json&showColumns=nb_visits&pageUrl=http%3A%2F%2Fpubman.mpdl.mpg.de%2Fpubman%2Ffaces%2FviewItemOverviewPage.jsp%3FitemId%3Descidoc%3A2451460";

  // "https://analytics.mpdl.mpg.de/?module=API&method=Actions.getPageUrl&pageUrl=http://dlc.mpdl.mpg.de/dlc/view/escidoc:7119:4/recto-verso&idSite=15&period=month&date=last12&token_auth=38cc64c1f177e45ec36f2809a910e89a&format=json&showColumns=nb_visits";
  static String dlc_item =
      "https://analytics.mpdl.mpg.de/index.php?module=CoreHome&action=index&date=yesterday&period=day&idSite=15#?idSite=15&period=day&date=yesterday&category=Dashboard_Dashboard&subcategory=1&popover=RowAction$3ARowEvolution$3AActions.getPageUrls$3A$257B$257D$3Adlc$20$3E$20view$20$3E$20escidoc$253A7101$253A7";

  public static void main(String[] args) {
    try {
      System.out.println(pageUrl);
      getStats4Page("");
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public static void getStats4Page(String id) throws Exception {
    // HttpResponse response = Request.Get(prepareItemURL("escidoc:67053:6", "full", "month", "last12")).execute().returnResponse();
    HttpResponse response = Request.Get(pageUrl).execute().returnResponse();

    System.out.println(response.getStatusLine().getStatusCode());
    System.out.println(response.getEntity().getContentType());
    // String json = Request.Get(prepareItemURL("escidoc:67053:6", "full", "month", "last12")).execute().returnContent().asString();
    String json = Request.Get(pageUrl).execute().returnContent().asString();

    System.out.println(json);
    Map<String, Integer> pageStatistics = new TreeMap<>();

    JsonNode theNode = om.readValue(response.getEntity().getContent(), JsonNode.class);
    theNode.fields().forEachRemaining(set -> {
      String key = set.getKey();
      int fullpage_visits = 0;
      int overview_visits = 0;

      if (theNode.get(key).get(0) != null) {
        overview_visits = theNode.get(key).get(0).get("nb_visits").asInt();
      }
      if (set.getValue().get(0) != null) {
        fullpage_visits = set.getValue().get(0).get("nb_visits").asInt();
      }

      pageStatistics.put(key, overview_visits + fullpage_visits);
    });
    pageStatistics.forEach((k, v) -> System.out.println(k + "   " + v));
    /*
    Map<String, Integer> theNode = MatomoStatisticsService.get124item("escidoc:1234838");
    theNode.forEach((k, v) -> System.out.println(k + "   " + v));
    Map<String, Integer> file =
      MatomoStatisticsService.get124file("escidoc:2148961:7", "escidoc:2149096", "MPDL_OA-Transition_White_Paper.pdf");
    file.forEach((k, v) -> System.out.println(k + "   " + v));
    
    System.out.println("TOTAL: " + MatomoStatisticsService.getTotal4Item("escidoc:1234838"));
    */
  }

  private static URI prepareItemURL(String id, String what, String period, String date) {
    URIBuilder builder;
    try {
      builder = new URIBuilder(new URI("https://analytics.mpdl.mpg.de/"));
      builder.addParameter("module", "API").addParameter("method", "Actions.getPageUrl").addParameter("idSite", "1")
          .addParameter("period", period).addParameter("date", date).addParameter("token_auth", "38cc64c1f177e45ec36f2809a910e89a")
          .addParameter("format", "json").addParameter("showColumns", "nb_visits");
      if (what.equalsIgnoreCase("full")) {
        builder.addParameter("pageUrl", "http://pubman.mpdl.mpg.de/pubman/faces/viewItemFullPage.jsp" + "?itemId=" + id);
      } else {
        builder.addParameter("pageUrl", "http://pubman.mpdl.mpg.de/pubman/faces/viewItemOverviewPage.jsp" + "?itemId=" + id);
      }
      URI analytics_URI = builder.build();
      System.out.println(analytics_URI);
      return analytics_URI;

    } catch (URISyntaxException e) {
      //logger.error(e.getMessage());
      e.printStackTrace();
    }
    return null;
  }

}
