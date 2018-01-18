package de.mpg.mpdl.inge.service.pubman.impl;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;

public class StatisticsViaPiwikService {

  static String pageUrl =
      "https://analytics.mpdl.mpg.de/?module=API&method=Actions.getPageUrl&pageUrl=http://dlc.mpdl.mpg.de/dlc/view/escidoc:7119:4/recto-verso&idSite=15&period=month&date=last12&token_auth=38cc64c1f177e45ec36f2809a910e89a&format=xml";

  public static void main(String[] args) {
    try {
      System.out.println(System.getProperty("jdk.security.defaultKeySize"));
      System.setProperty("jdk.tls.ephemeralDHKeySize	", "2048");
      System.out.println(System.getProperty("jdk.tls.ephemeralDHKeySize	"));

      System.out.println(pageUrl);
      getStats4Page("");
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public static void getStats4Page(String id) throws IOException {
    HttpResponse response = Request.Get(pageUrl).execute().returnResponse();
    System.out.println(response.getStatusLine().getStatusCode());
    System.out.println(response.getEntity().getContentType());
  }

}
