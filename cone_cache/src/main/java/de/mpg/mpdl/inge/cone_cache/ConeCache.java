package de.mpg.mpdl.inge.cone_cache;

import java.io.IOException;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import de.mpg.mpdl.inge.util.PropertyReader;

public class ConeCache {
  // Innere private Klasse, die erst beim Zugriff durch die umgebende Klasse initialisiert wird
  private static final class InstanceHolder {
    // Die Initialisierung von Klassenvariablen geschieht nur einmal und wird vom ClassLoader
    // implizit synchronisiert
    static final ConeCache INSTANCE = new ConeCache();
  }

  private static final Logger logger = Logger.getLogger(ConeCache.class);

  private static final String DDC_TITLE_QUERY = "ddc/query?format=rdf&q=*&n=0";
  private static final String ISO639_3_IDENTIFIER_QUERY = "iso639-3/query?format=rdf&q=*&mode=full&n=0";
  private static final String ISO639_3_TITLE_QUERY = "iso639-3/query?format=rdf&q=*&n=0";

  private static final String IDENTIFIER = "dc:identifier";
  private static final String TITLE = "dc:title";

  private static final ConeSet ddcTitle = ConeSet.DDC_TITLE;
  private static final ConeSet iso639_3_Identifier = ConeSet.ISO639_3_IDENTIFIER;
  private static final ConeSet iso639_3_Title = ConeSet.ISO639_3_TITLE;

  private ConeCache() {}

  public static ConeCache getInstance() {
    return ConeCache.InstanceHolder.INSTANCE;
  }

  public static void refreshCache() {
    logger.info("*** Start CONE-Cache Refresh-Cycle ***");

    String coneServiceUrl = PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL);

    ConeCache.refresh(ddcTitle, new ConeHandler(ConeCache.TITLE), coneServiceUrl + ConeCache.DDC_TITLE_QUERY);
    ConeCache.refresh(iso639_3_Identifier, new ConeHandler(ConeCache.IDENTIFIER), coneServiceUrl + ConeCache.ISO639_3_IDENTIFIER_QUERY);
    ConeCache.refresh(iso639_3_Title, new ConeHandler(ConeCache.TITLE), coneServiceUrl + ConeCache.ISO639_3_TITLE_QUERY);

    logger.info("*** Ende CONE-Cache Refresh-Cycle ***");
  }

  private static void refresh(ConeSet coneSet, ConeHandler handler, String queryUrl) {
    logger.info("*** Start refresh: " + queryUrl);
    try {
      final Set<String> result = ConeCache.getData(handler, queryUrl);
      if (result == null) {
        logger.info("*** Not used");
        return;
      }

      if (0 == result.size()) {
        logger.warn("    " + "Size: " + result.size() + " " + queryUrl);
      } else {
        logger.info("    " + "Size: " + result.size() + " " + queryUrl);
      }
      if (!result.isEmpty()) {
        synchronized (coneSet.set()) {
          coneSet.set().clear();
          coneSet.set().addAll(result);
        }
      }
    } catch (IOException | ParserConfigurationException | SAXException | ConeCacheException e) {
      if (PropertyReader.getProperty(PropertyReader.INGE_CONE_CACHE_USE).equalsIgnoreCase("true")) {
        logger.error("Could not refresh Cone Set with Url: " + queryUrl, e);
        if (coneSet.set().isEmpty()) {
          logger.error("Cone Set is empty: Url: " + queryUrl);
        }
      }
    }
    logger.info("*** Ende refresh: " + queryUrl);
  }

  private static Set<String> getData(ConeHandler handler, String queryUrl)
      throws ParserConfigurationException, SAXException, ConeCacheException, IOException {
    final HttpClient client = new HttpClient();
    final GetMethod method = new GetMethod(queryUrl);

    client.executeMethod(method);

    if (method.getStatusCode() == 200) {
      final SAXParserFactory factory = SAXParserFactory.newInstance();
      final SAXParser saxParser = factory.newSAXParser();
      saxParser.parse(method.getResponseBodyAsStream(), handler);
      return handler.getResult();
    } else {
      if (PropertyReader.getProperty(PropertyReader.INGE_CONE_CACHE_USE).equalsIgnoreCase("true")) {
        logger.error("Could not load CONE attributes:" + method.getStatusCode());
        throw new ConeCacheException("Could not load CONE attributes: " + method.getStatusCode());
      } else {
        return null;
      }
    }
  }

  public Set<String> getDdcTitleSet() {
    if (PropertyReader.getProperty(PropertyReader.INGE_CONE_CACHE_USE).equalsIgnoreCase("true") && ddcTitle.set().isEmpty()) {
      logger.error("CONE ddcTitleSet is empty.");
    }

    return ddcTitle.set();
  }

  public Set<String> getIso639_3_IdentifierSet() {
    if (PropertyReader.getProperty(PropertyReader.INGE_CONE_CACHE_USE).equalsIgnoreCase("true") && iso639_3_Identifier.set().isEmpty()) {
      logger.error("CONE iso639_3_IdentifierSet is empty.");
    }

    return iso639_3_Identifier.set();
  }

  public Set<String> getIso639_3_TitleSet() {
    if (PropertyReader.getProperty(PropertyReader.INGE_CONE_CACHE_USE).equalsIgnoreCase("true") && iso639_3_Title.set().isEmpty()) {
      logger.error("CONE iso639_3_TitleSet is empty.");
    }

    return iso639_3_Title.set();
  }

}
