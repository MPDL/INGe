package de.mpg.mpdl.inge.inge_validation.util;

import java.io.IOException;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import de.mpg.mpdl.inge.util.ProxyHelper;

//TODO System.out.println rauswerfen
public class ConeSetsCache {
  // Innere private Klasse, die erst beim Zugriff durch die umgebende Klasse initialisiert wird
  private static final class InstanceHolder {
    // Die Initialisierung von Klassenvariablen geschieht nur einmal und wird vom ClassLoader implizit synchronisiert
    static final ConeSetsCache INSTANCE = new ConeSetsCache();
  }

  private final static Logger LOG = Logger.getLogger(ConeSetsCache.class);

  // TODO
  private final static String ISO639_3_IDENTIFIER_URL = "http://qa-pubman.mpdl.mpg.de/cone/iso639-3/query?format=rdf&q=*&mode=full&n=0";
  private final static String ISO639_3_TITLE_URL = "http://qa-pubman.mpdl.mpg.de/cone/iso639-3/query?format=rdf&q=*&mode=full&n=0";
  private final static String DDC_TITLE_URL = "http://qa-pubman.mpdl.mpg.de/cone/ddc/query?format=rdf&q=*&mode=full&n=0";
  private final static String MIME_TYPES_TITLE_URL = "http://qa-pubman.mpdl.mpg.de/cone/escidocmimetypes/query?format=rdf&q=*&mode=full&n=0";
  private final static String MPIPKS_TITLE_URL = "http://qa-pubman.mpdl.mpg.de/cone/mpipks/query?format=rdf&q=*&mode=full&n=0";
  private final static String MPIRG_TITLE_URL = "http://qa-pubman.mpdl.mpg.de/cone/mpirg/query?format=rdf&q=*&mode=full&n=0";
  private final static String MPIS_GROUPS_TITLE_URL = "http://qa-pubman.mpdl.mpg.de/cone/mpis-groups/query?format=rdf&q=*&mode=full&n=0";
  private final static String MPIS_PROJECTS_TITLE_URL = "http://qa-pubman.mpdl.mpg.de/cone/mpis-projects/query?format=rdf&q=*&mode=full&n=0";

  private final static String IDENTIFIER = "dc:identifier";
  private final static String TITLE = "dc:title";

  private ConeSet iso639_3_Identifier = ConeSet.ISO639_3_IDENTIFIER;
  private ConeSet iso639_3_Title = ConeSet.ISO639_3_TITLE;
  private ConeSet ddcTitle = ConeSet.DDC_TITLE;
  private ConeSet mimeTypesTitle = ConeSet.MIME_TYPES_TITLE;
  private ConeSet mpipksTitle = ConeSet.MPIPKS_TITLE;
  private ConeSet mpirgTitle = ConeSet.MPIRG_TITLE;
  private ConeSet mpisGroupsTitle = ConeSet.MPIS_GROUPS_TITLE;
  private ConeSet mpisProjectTitle = ConeSet.MPIS_PROJECTS_TITLE;

  private int testCount = 0;

  private ConeSetsCache() {
      refreshCache();
  }

  public static ConeSetsCache getInstance() {
    return ConeSetsCache.InstanceHolder.INSTANCE;
  }

  public void refreshCache() {
    refresh(this.iso639_3_Identifier, new ConeHandler(IDENTIFIER), ISO639_3_IDENTIFIER_URL);
    refresh(this.iso639_3_Title, new ConeHandler(TITLE), ISO639_3_TITLE_URL);
    refresh(this.ddcTitle, new ConeHandler(TITLE), DDC_TITLE_URL);
    refresh(this.mimeTypesTitle, new ConeHandler(TITLE), MIME_TYPES_TITLE_URL);
    refresh(this.mpipksTitle, new ConeHandler(TITLE), MPIPKS_TITLE_URL);
    refresh(this.mpirgTitle, new ConeHandler(TITLE), MPIRG_TITLE_URL);
    refresh(this.mpisGroupsTitle, new ConeHandler(TITLE), MPIS_GROUPS_TITLE_URL);
    refresh(this.mpisProjectTitle, new ConeHandler(TITLE), MPIS_PROJECTS_TITLE_URL);
  }

  private void refresh(ConeSet coneSet, ConeHandler handler, String queryUrl) {
    System.out.println("\n*** Start refillSet: " + queryUrl);
    try {
      Set<String> result = fill(handler, queryUrl);
      System.out.println("    " + "Size: " + result.size() + " " + queryUrl);
      if (!result.isEmpty()) {
        synchronized (coneSet.set()) {
          coneSet.set().clear();
          coneSet.set().addAll(result);
        }
      }
    } catch (IOException | ParserConfigurationException | SAXException | ConeException e) {
      System.out.println(e);
      LOG.warn("Could not refill Cone Set with Url: " + queryUrl);
      if (coneSet.set().isEmpty()) {
        LOG.error("Cone Set is empty: Url: " + queryUrl);
      }
    }
    System.out.println("*** Ende refillSet: " + queryUrl);
  }

  
  private Set<String> fill(ConeHandler handler, String queryUrl)
      throws HttpException, IOException, ParserConfigurationException, SAXException, ConeException {
    HttpClient client = new HttpClient();
    GetMethod method = new GetMethod(queryUrl);

    ProxyHelper.executeMethod(client, method);

    if (method.getStatusCode() == 200 && this.testCount < 20) {
      this.testCount++;
      SAXParserFactory factory = SAXParserFactory.newInstance();
      SAXParser saxParser = factory.newSAXParser();
      saxParser.parse(method.getResponseBodyAsStream(), handler);
      return handler.getResult();
    } else {
      System.out.println("**** ERROR ***** ");
      LOG.error("Could not load CONE attributes:" + method.getStatusCode());
      throw new ConeException("Could not load CONE attributes: " + method.getStatusCode());
    }
  }

  public Set<String> getDdcTitleSet() {
    if (this.ddcTitle.set().isEmpty()) {
      System.out.println("empty");
      LOG.error("CONE ddcTitleSet is empty.");
    }

    return this.ddcTitle.set();
  }

  public Set<String> getIso639_3_IdentifierSet() {
    if (this.iso639_3_Identifier.set().isEmpty()) {
      System.out.println("empty");
      LOG.error("CONE iso639_3_IdentifierSet is empty.");
    }

    return this.iso639_3_Identifier.set();
  }

  public Set<String> getIso639_3_TitleSet() {
    if (this.iso639_3_Title.set().isEmpty()) {
      System.out.println("empty");
      LOG.error("CONE iso639_3_TitleSet is empty.");
    }

    return this.iso639_3_Title.set();
  }

  public Set<String> getMimeTypesTitleSet() {
    if (this.mimeTypesTitle.set().isEmpty()) {
      System.out.println("empty");
      LOG.error("CONE mimeTypesTitleSet is empty.");
    }

    return this.mimeTypesTitle.set();
  }

  public Set<String> getMpipksTitleSet() {
    if (this.mpipksTitle.set().isEmpty()) {
      System.out.println("empty");
      LOG.error("CONE mpipksTitleSet is empty.");
    }

    return this.mpipksTitle.set();
  }

  public Set<String> getMpirgTitleSet() {
    if (this.mpirgTitle.set().isEmpty()) {
      System.out.println("empty");
      LOG.error("CONE mpirgTitleSet is empty.");
    }

    return this.mpirgTitle.set();
  }

  public Set<String> getMpisGroupsTitleSet() {
    if (this.mpisGroupsTitle.set().isEmpty()) {
      System.out.println("empty");
      LOG.error("CONE mpisGroupsTitleSet is empty.");
    }

    return this.mpisGroupsTitle.set();
  }

  public Set<String> getMpisProjectTitleSet() {
    if (this.mpisProjectTitle.set().isEmpty()) {
      System.out.println("empty");
      LOG.error("CONE mpisProjectTitleSet is empty.");
    }

    return this.mpisProjectTitle.set();
  }

}
