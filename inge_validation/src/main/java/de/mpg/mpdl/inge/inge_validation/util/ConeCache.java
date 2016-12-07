package de.mpg.mpdl.inge.inge_validation.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import de.mpg.mpdl.inge.inge_validation.ValidationException;
import de.mpg.mpdl.inge.util.PropertyReader;
import de.mpg.mpdl.inge.util.ProxyHelper;

// TODO System.out.println rauswerfen
public class ConeCache {
  // Innere private Klasse, die erst beim Zugriff durch die umgebende Klasse initialisiert wird
  private static final class InstanceHolder {
    // Die Initialisierung von Klassenvariablen geschieht nur einmal und wird vom ClassLoader
    // implizit synchronisiert
    static final ConeCache INSTANCE = new ConeCache();
  }

  private static final Logger LOG = Logger.getLogger(ConeCache.class);

  private static final String ISO639_3_IDENTIFIER_QUERY =
      "iso639-3/query?format=rdf&q=*&mode=full&n=0";
  private static final String ISO639_3_TITLE_QUERY = "iso639-3/query?format=rdf&q=*&mode=full&n=0";
  private static final String DDC_TITLE_QUERY = "ddc/query?format=rdf&q=*&mode=full&n=0";
  private static final String MIME_TYPES_TITLE_QUERY =
      "escidocmimetypes/query?format=rdf&q=*&mode=full&n=0";
  private static final String MPIPKS_TITLE_QUERY = "mpipks/query?format=rdf&q=*&mode=full&n=0";
  private static final String MPIRG_TITLE_QUERY = "mpirg/query?format=rdf&q=*&mode=full&n=0";
  private static final String MPIS_GROUPS_TITLE_QUERY =
      "/mpis-groups/query?format=rdf&q=*&mode=full&n=0";
  private static final String MPIS_PROJECTS_TITLE_QUERY =
      "mpis-projects/query?format=rdf&q=*&mode=full&n=0";

  private static final String IDENTIFIER = "dc:identifier";
  private static final String TITLE = "dc:title";

  private final ConeSet iso639_3_Identifier = ConeSet.ISO639_3_IDENTIFIER;
  private final ConeSet iso639_3_Title = ConeSet.ISO639_3_TITLE;
  private final ConeSet ddcTitle = ConeSet.DDC_TITLE;
  private final ConeSet mimeTypesTitle = ConeSet.MIME_TYPES_TITLE;
  private final ConeSet mpipksTitle = ConeSet.MPIPKS_TITLE;
  private final ConeSet mpirgTitle = ConeSet.MPIRG_TITLE;
  private final ConeSet mpisGroupsTitle = ConeSet.MPIS_GROUPS_TITLE;
  private final ConeSet mpisProjectTitle = ConeSet.MPIS_PROJECTS_TITLE;

  private final String coneServiceUrl;

  // TODO entfernen
  private int testCount = 0;

  private ConeCache() {
    try {
      this.coneServiceUrl = PropertyReader.getProperty(Properties.ESCIDOC_CONE_SERVICE_URL);
      System.out.println("ConeServiceUrl: " + this.coneServiceUrl);
    } catch (IOException | URISyntaxException e) {
      LOG.error(e);
      throw new IllegalArgumentException();
    }

    try {
      refreshCache();
    } catch (ValidationException e) {
      LOG.error(e);
      throw new IllegalStateException();
    }
  }

  public static ConeCache getInstance() {
    return ConeCache.InstanceHolder.INSTANCE;
  }

  public void refreshCache() throws ValidationException {
    refresh(this.iso639_3_Identifier, new ConeHandler(IDENTIFIER), coneServiceUrl
        + ISO639_3_IDENTIFIER_QUERY);
    refresh(this.iso639_3_Title, new ConeHandler(TITLE), coneServiceUrl + ISO639_3_TITLE_QUERY);
    refresh(this.ddcTitle, new ConeHandler(TITLE), coneServiceUrl + DDC_TITLE_QUERY);
    refresh(this.mimeTypesTitle, new ConeHandler(TITLE), coneServiceUrl + MIME_TYPES_TITLE_QUERY);
    refresh(this.mpipksTitle, new ConeHandler(TITLE), coneServiceUrl + MPIPKS_TITLE_QUERY);
    refresh(this.mpirgTitle, new ConeHandler(TITLE), coneServiceUrl + MPIRG_TITLE_QUERY);
    refresh(this.mpisGroupsTitle, new ConeHandler(TITLE), coneServiceUrl + MPIS_GROUPS_TITLE_QUERY);
    refresh(this.mpisProjectTitle, new ConeHandler(TITLE), coneServiceUrl
        + MPIS_PROJECTS_TITLE_QUERY);
  }

  private void refresh(ConeSet coneSet, ConeHandler handler, String queryUrl)
      throws ValidationException {
    System.out.println("\n*** Start refresh: " + queryUrl);
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
      LOG.warn("Could not refresh Cone Set with Url: " + queryUrl);
      if (coneSet.set().isEmpty()) {
        LOG.error("Cone Set is empty: Url: " + queryUrl);
        throw new ValidationException(e);
      }
    }
    System.out.println("*** Ende refresh: " + queryUrl);
  }

  private Set<String> fill(ConeHandler handler, String queryUrl) throws HttpException, IOException,
      ParserConfigurationException, SAXException, ConeException {
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
