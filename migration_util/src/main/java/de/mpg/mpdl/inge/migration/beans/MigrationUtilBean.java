package de.mpg.mpdl.inge.migration.beans;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import de.mpg.mpdl.inge.util.AdminHelper;

@Component
public class MigrationUtilBean {

  @Value("${escidoc.url}")
  private String escidocUrl;


  public String changeId(String prefix, String href) {
    return href.substring(href.lastIndexOf("/") + 1, href.length()).replaceAll("escidoc:", prefix + "_").replaceAll(":", "_");
  }

  public HttpClient setup() throws URISyntaxException {
    HttpClient httpClientWithEscidocCookie;

    String userHandle = AdminHelper.getAdminUserHandle();
    BasicCookieStore cookieStore = new BasicCookieStore();
    BasicClientCookie cookie = new BasicClientCookie("escidocCookie", userHandle);
    URI uri = new URIBuilder(escidocUrl).build();
    cookie.setDomain(uri.getHost());
    cookie.setPath("/");
    cookieStore.addCookie(cookie);
    httpClientWithEscidocCookie = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).build();
    return httpClientWithEscidocCookie;
  }

  public void wfTesting() {
    try {
      HttpClient client = setup();
      URI uri = new URIBuilder(escidocUrl).build();
      System.out.println(uri.toURL());
    } catch (URISyntaxException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (MalformedURLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
