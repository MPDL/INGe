package de.mpg.mpdl.inge.migration.beans;

import java.awt.List;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.valueobjects.FileVO;
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
	  Path path;
	  ArrayList<String> audienceIds = new ArrayList<String>();
	  FileVO file = new FileVO();
	  FileDbVO inge_file = new FileDbVO();
	  file.setVisibility(FileVO.Visibility.AUDIENCE);
	try {
		path = Paths.get(getClass().getClassLoader()
		  	      .getResource("Kontext_MPI-ID.txt").toURI());
		Stream<String> lines = Files.lines(path);
	    lines.filter(line -> line.startsWith("escidoc:1861388"))
	    .map(line -> line.split(", ")[1])
	    .forEach(id -> audienceIds.add(id));
	    System.out.println("Collected ids: "+audienceIds);
	    if (file.getVisibility().equals(FileVO.Visibility.AUDIENCE)) {
	    	inge_file.setAllowedAudienceIds(audienceIds);
	    }
	    System.out.println("new file has audienceIds " + inge_file.getAllowedAudienceIds());
	} catch (URISyntaxException | IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	    
  }
}
