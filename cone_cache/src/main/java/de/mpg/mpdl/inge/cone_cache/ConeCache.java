package de.mpg.mpdl.inge.cone_cache;

import de.mpg.mpdl.inge.util.PropertyReader;
import java.io.IOException;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

@Service
public class ConeCache {
  // Innere private Klasse, die erst beim Zugriff durch die umgebende Klasse initialisiert wird
  private static final class InstanceHolder {
    // Die Initialisierung von Klassenvariablen geschieht nur einmal und wird vom ClassLoader
    // implizit synchronisiert
    static final ConeCache INSTANCE = new ConeCache();
  }

  private static final Logger logger = LogManager.getLogger(ConeCache.class);

  private static final String DDC_TITLE_QUERY = "ddc/query?format=rdf&q=*&n=0";
  private static final String ISO639_3_IDENTIFIER_QUERY = "iso639-3/query?format=rdf&q=*&mode=full&n=0";
  private static final String ISO639_3_TITLE_QUERY = "iso639-3/query?format=rdf&q=*&n=0";
  private static final String JEL_TITLE_QUERY = "jel/query?format=rdf&q=*&n=0";
  private static final String MPICC_PROJECTS_TITLE_QUERY = "mpicc-projects/query?format=rdf&q=*&n=0";
  private static final String MPINP_TITLE_QUERY = "mpinp/query?format=rdf&q=*&n=0";
  private static final String MPIPKS_TITLE_QUERY = "mpipks/query?format=rdf&q=*&n=0";
  private static final String MPIRG_TITLE_QUERY = "mpirg/query?format=rdf&q=*&n=0";
  private static final String MPIS_GROUPS_TITLE_QUERY = "mpis-groups/query?format=rdf&q=*&n=0";
  private static final String MPIS_PROJECTS_TITLE_QUERY = "mpis-projects/query?format=rdf&q=*&n=0";
  private static final String MPIWG_PROJECTS_TITLE_QUERY = "mpiwg-projects/query?format=rdf&q=*&n=0";

  private static final String IDENTIFIER = "dc:identifier";
  private static final String TITLE = "dc:title";

  private static final ConeSet ddcTitle = ConeSet.DDC_TITLE;
  private static final ConeSet iso639_3_Identifier = ConeSet.ISO639_3_IDENTIFIER;
  private static final ConeSet iso639_3_Title = ConeSet.ISO639_3_TITLE;
  private static final ConeSet jelTitle = ConeSet.JEL_TITLE;
  private static final ConeSet mpiccProjectsTitle = ConeSet.MPICC_PROJECTS_TITLE;
  private static final ConeSet mpinpTitle = ConeSet.MPINP_TITLE;
  private static final ConeSet mpipksTitle = ConeSet.MPIPKS_TITLE;
  private static final ConeSet mpirgTitle = ConeSet.MPIRG_TITLE;
  private static final ConeSet mpisGroupsTitle = ConeSet.MPIS_GROUPS_TITLE;
  private static final ConeSet mpisProjectsTitle = ConeSet.MPIS_PROJECTS_TITLE;
  private static final ConeSet mpiwgProjectsTitle = ConeSet.MPIWG_PROJECTS_TITLE;

  private ConeCache() {}

  public static ConeCache getInstance() {
    return ConeCache.InstanceHolder.INSTANCE;
  }

  @Scheduled(fixedDelay = 3600000, initialDelay = 0)
  public static void refreshCache() {
    logger.info("*** CRON (fixedDelay 3600000 initialDelay 0): Start CONE-Cache Refresh-Cycle");

    String coneServiceUrl = PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL);

    ConeCache.refresh(ddcTitle, new ConeHandler(ConeCache.TITLE), coneServiceUrl + ConeCache.DDC_TITLE_QUERY);
    ConeCache.refresh(iso639_3_Identifier, new ConeHandler(ConeCache.IDENTIFIER), coneServiceUrl + ConeCache.ISO639_3_IDENTIFIER_QUERY);
    ConeCache.refresh(iso639_3_Title, new ConeHandler(ConeCache.TITLE), coneServiceUrl + ConeCache.ISO639_3_TITLE_QUERY);
    ConeCache.refresh(jelTitle, new ConeHandler(ConeCache.TITLE), coneServiceUrl + ConeCache.JEL_TITLE_QUERY);
    ConeCache.refresh(mpiccProjectsTitle, new ConeHandler(ConeCache.TITLE), coneServiceUrl + ConeCache.MPICC_PROJECTS_TITLE_QUERY);
    ConeCache.refresh(mpinpTitle, new ConeHandler(ConeCache.TITLE), coneServiceUrl + ConeCache.MPINP_TITLE_QUERY);
    ConeCache.refresh(mpipksTitle, new ConeHandler(ConeCache.TITLE), coneServiceUrl + ConeCache.MPIPKS_TITLE_QUERY);
    ConeCache.refresh(mpirgTitle, new ConeHandler(ConeCache.TITLE), coneServiceUrl + ConeCache.MPIRG_TITLE_QUERY);
    ConeCache.refresh(mpisGroupsTitle, new ConeHandler(ConeCache.TITLE), coneServiceUrl + ConeCache.MPIS_GROUPS_TITLE_QUERY);
    ConeCache.refresh(mpisProjectsTitle, new ConeHandler(ConeCache.TITLE), coneServiceUrl + ConeCache.MPIS_PROJECTS_TITLE_QUERY);
    ConeCache.refresh(mpiwgProjectsTitle, new ConeHandler(ConeCache.TITLE), coneServiceUrl + ConeCache.MPIWG_PROJECTS_TITLE_QUERY);

    logger.info("*** CRON: Ende CONE-Cache Refresh-Cycle");
  }

  private static void refresh(ConeSet coneSet, ConeHandler handler, String queryUrl) {
    logger.info("** Start refresh: " + queryUrl);
    try {
      Set<String> result = ConeCache.getData(handler, queryUrl);
      if (null == result) {
        logger.info("** Not used");
        return;
      }

      if (result.isEmpty()) {
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
      if ("true".equalsIgnoreCase(PropertyReader.getProperty(PropertyReader.INGE_CONE_CACHE_USE))) {
        logger.error("Could not refresh Cone Set with Url: " + queryUrl, e);
        if (coneSet.set().isEmpty()) {
          logger.error("Cone Set is empty: Url: " + queryUrl);
        }
      }
    }
    logger.info("** Ende refresh: " + queryUrl);
  }

  private static Set<String> getData(ConeHandler handler, String queryUrl)
      throws ParserConfigurationException, SAXException, ConeCacheException, IOException {
    HttpClient client = new HttpClient();
    GetMethod method = new GetMethod(queryUrl);

    client.executeMethod(method);

    if (200 == method.getStatusCode()) {
      SAXParserFactory factory = SAXParserFactory.newInstance();
      SAXParser saxParser = factory.newSAXParser();
      saxParser.parse(method.getResponseBodyAsStream(), handler);
      return handler.getResult();
    } else {
      if ("true".equalsIgnoreCase(PropertyReader.getProperty(PropertyReader.INGE_CONE_CACHE_USE))) {
        logger.error("Could not load CONE attributes:" + method.getStatusCode());
        throw new ConeCacheException("Could not load CONE attributes: " + method.getStatusCode());
      } else {
        return null;
      }
    }
  }

  public Set<String> getDdcTitleSet() {
    if ("true".equalsIgnoreCase(PropertyReader.getProperty(PropertyReader.INGE_CONE_CACHE_USE)) && ddcTitle.set().isEmpty()) {
      logger.error("CONE ddcTitleSet is empty.");
    }

    return ddcTitle.set();
  }

  public Set<String> getIso639_3_IdentifierSet() {
    if ("true".equalsIgnoreCase(PropertyReader.getProperty(PropertyReader.INGE_CONE_CACHE_USE)) && iso639_3_Identifier.set().isEmpty()) {
      logger.error("CONE iso639_3_IdentifierSet is empty.");
    }

    return iso639_3_Identifier.set();
  }

  public Set<String> getIso639_3_TitleSet() {
    if ("true".equalsIgnoreCase(PropertyReader.getProperty(PropertyReader.INGE_CONE_CACHE_USE)) && iso639_3_Title.set().isEmpty()) {
      logger.error("CONE iso639_3_TitleSet is empty.");
    }

    return iso639_3_Title.set();
  }

  public Set<String> getJelTitleSet() {
    if ("true".equalsIgnoreCase(PropertyReader.getProperty(PropertyReader.INGE_CONE_CACHE_USE)) && jelTitle.set().isEmpty()) {
      logger.error("CONE jelTitleSet is empty.");
    }

    return jelTitle.set();
  }

  public Set<String> getMpiccProjectsTitleSet() {
    if ("true".equalsIgnoreCase(PropertyReader.getProperty(PropertyReader.INGE_CONE_CACHE_USE)) && mpiccProjectsTitle.set().isEmpty()) {
      logger.error("CONE mpiccTitleSet is empty.");
    }

    return mpiccProjectsTitle.set();
  }

  public Set<String> getMpinpTitleSet() {
    if ("true".equalsIgnoreCase(PropertyReader.getProperty(PropertyReader.INGE_CONE_CACHE_USE)) && mpinpTitle.set().isEmpty()) {
      logger.error("CONE mpinpTitleSet is empty.");
    }

    return mpinpTitle.set();
  }

  public Set<String> getMpipksTitleSet() {
    if ("true".equalsIgnoreCase(PropertyReader.getProperty(PropertyReader.INGE_CONE_CACHE_USE)) && mpipksTitle.set().isEmpty()) {
      logger.error("CONE mpipksTitleSet is empty.");
    }

    return mpipksTitle.set();
  }

  public Set<String> getMpirgTitleSet() {
    if ("true".equalsIgnoreCase(PropertyReader.getProperty(PropertyReader.INGE_CONE_CACHE_USE)) && mpirgTitle.set().isEmpty()) {
      logger.error("CONE mpirgTitleSet is empty.");
    }

    return mpirgTitle.set();
  }

  public Set<String> getMpisGroupsTitleSet() {
    if ("true".equalsIgnoreCase(PropertyReader.getProperty(PropertyReader.INGE_CONE_CACHE_USE)) && mpisGroupsTitle.set().isEmpty()) {
      logger.error("CONE mpisGroupsTitleSet is empty.");
    }

    return mpisGroupsTitle.set();
  }

  public Set<String> getMpisProjectsTitleSet() {
    if ("true".equalsIgnoreCase(PropertyReader.getProperty(PropertyReader.INGE_CONE_CACHE_USE)) && mpisProjectsTitle.set().isEmpty()) {
      logger.error("CONE mpisProjectTitleSet is empty.");
    }

    return mpisProjectsTitle.set();
  }

  public Set<String> getMpiwgProjectsTitleSet() {
    if ("true".equalsIgnoreCase(PropertyReader.getProperty(PropertyReader.INGE_CONE_CACHE_USE)) && mpiwgProjectsTitle.set().isEmpty()) {
      logger.error("CONE mpiwgProjectTitleSet is empty.");
    }

    return mpiwgProjectsTitle.set();
  }
}
