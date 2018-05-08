package de.mpg.mpdl.inge.service.pubman.impl;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class StatisticsViaPiwikService {

  static ObjectMapper om = new ObjectMapper();

  static String pageUrl =
      "https://analytics.mpdl.mpg.de/?module=API&method=Actions.getPageUrl&pageUrl=http://dlc.mpdl.mpg.de/dlc/view/escidoc:7119:4/recto-verso&idSite=15&period=month&date=last12&token_auth=38cc64c1f177e45ec36f2809a910e89a&format=json&showColumns=nb_visits";

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
    /*
    HttpResponse response = Request.Get(pageUrl).execute().returnResponse();
    System.out.println(response.getStatusLine().getStatusCode());
    System.out.println(response.getEntity().getContentType());
    String json = Request.Get(pageUrl).execute().returnContent().asString();
    System.out.println(json);
    */
    //JsonNode theNode = om.readValue(response.getEntity().getContent(), JsonNode.class);
    Map<String, Integer> theNode = MatomoStatisticsService.get124item("escidoc:1234838");
    theNode.forEach((k, v) -> System.out.println(k + "   " + v));
    Map<String, Integer> file =
        MatomoStatisticsService.get124file("escidoc:2148961:7", "escidoc:2149096", "MPDL_OA-Transition_White_Paper.pdf");
    file.forEach((k, v) -> System.out.println(k + "   " + v));

    System.out.println("TOTAL: " + MatomoStatisticsService.getTotal4Item("escidoc:1234838"));
  }

}
