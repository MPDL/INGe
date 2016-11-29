package de.mpg.mpdl.inge.inge_validation.util;

import java.io.IOException;
import java.nio.file.ProviderNotFoundException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.xml.sax.SAXException;

import de.mpg.mpdl.inge.util.ProxyHelper;

public class ConeSetsCache {
  private static ConeSetsCache INSTANCE = null;

  private final static String ISO639_3_IDENTIFIER_URL =
      "http://pubman.mpdl.mpg.de/cone/iso639-3/query?format=rdf&q=*&mode=full&n=0";
  private final static String ISO639_3_TITLE_URL =
      "http://pubman.mpdl.mpg.de/cone/iso639-3/query?format=rdf&q=*&mode=full&n=0";
  private final static String DDC_TITLE_URL =
      "http://pubman.mpdl.mpg.de/cone/ddc/query?format=rdf&q=*&mode=full&n=0";
  private final static String MIME_TYPES_TITLE_URL =
      "http://pubman.mpdl.mpg.de/cone/escidocmimetypes/query?format=rdf&q=*&mode=full&n=0";
  private final static String MPIPKS_TITLE_URL =
      "http://pubman.mpdl.mpg.de/cone/mpipks/query?format=rdf&q=*&mode=full&n=0";
  private final static String MPIRG_TITLE_URL =
      "http://pubman.mpdl.mpg.de/cone/mpirg/query?format=rdf&q=*&mode=full&n=0";
  private final static String MPIS_GROUPS_TITLE_URL =
      "http://pubman.mpdl.mpg.de/cone/mpis-groups/query?format=rdf&q=*&mode=full&n=0";
  private final static String MPIS_PROJECTS_TITLE_URL =
      "http://pubman.mpdl.mpg.de/cone/mpis-projects/query?format=rdf&q=*&mode=full&n=0";

  private final static String IDENTIFIER = "dc:identifier";
  private final static String TITLE = "dc:title";

  private Set<String> iso639_3_IdentifierSet = new HashSet<String>();
  private Set<String> iso639_3_TitleSet = new HashSet<String>();
  private Set<String> ddcTitleSet = new HashSet<String>();
  private Set<String> mimeTypesTitleSet = new HashSet<String>();
  private Set<String> mpipksTitleSet = new HashSet<String>();
  private Set<String> mpirgTitleSet = new HashSet<String>();
  private Set<String> mpisGroupsTitleSet = new HashSet<String>();
  private Set<String> mpisProjectTitleSet = new HashSet<String>();

  private Set<String> iso639_3_IdentifierSet_OLD = new HashSet<String>();
  private Set<String> iso639_3_TitleSet_OLD = new HashSet<String>();
  private Set<String> ddcTitleSet_OLD = new HashSet<String>();
  private Set<String> mimeTypesTitleSet_OLD = new HashSet<String>();
  private Set<String> mpipksTitleSet_OLD = new HashSet<String>();
  private Set<String> mpirgTitleSet_OLD = new HashSet<String>();
  private Set<String> mpisGroupsTitleSet_OLD = new HashSet<String>();
  private Set<String> mpisProjectTitleSet_OLD = new HashSet<String>();

  private ConeSetsCache() {}

  public static ConeSetsCache getInstance() {
    if (ConeSetsCache.INSTANCE == null) {
      ConeSetsCache.INSTANCE = new ConeSetsCache();
    }
    return ConeSetsCache.INSTANCE;
  }

  public void clearCache() {
    resetSet(this.iso639_3_IdentifierSet, this.iso639_3_IdentifierSet_OLD);
    resetSet(this.iso639_3_TitleSet, this.iso639_3_TitleSet_OLD);
    resetSet(this.ddcTitleSet, this.ddcTitleSet_OLD);
    resetSet(this.mimeTypesTitleSet, this.mimeTypesTitleSet_OLD);
    resetSet(this.mpipksTitleSet, this.mpipksTitleSet_OLD);
    resetSet(this.mpirgTitleSet, this.mpirgTitleSet_OLD);
    resetSet(this.mpisGroupsTitleSet, this.mpisGroupsTitleSet_OLD);
    resetSet(this.mpisProjectTitleSet, this.mpisProjectTitleSet_OLD);
  }

  // TODO: correct? Getter synchronized?
  private void resetSet(Set<String> mySet, Set<String> mySet_OLD) {
    synchronized (mySet) {
      if (!mySet.isEmpty()) {
        mySet_OLD = new HashSet<String>();
        mySet_OLD.addAll(mySet);
      }
      mySet = new HashSet<String>();
    }
  }

  private Set<String> fill(String queryUrl, ConeHandler handler) throws HttpException, IOException,
      ParserConfigurationException, SAXException {
    HttpClient client = new HttpClient();
    GetMethod method = new GetMethod(queryUrl);

    ProxyHelper.executeMethod(client, method);

    if (method.getStatusCode() == 200) {
      SAXParserFactory factory = SAXParserFactory.newInstance();
      SAXParser saxParser = factory.newSAXParser();
      saxParser.parse(method.getResponseBodyAsStream(), handler);
      return handler.getResult();
    } else {
      throw new ProviderNotFoundException("Could not load CONE attributes.");
    }
  }

  public Set<String> getDdcTitleSet() {
    if (this.ddcTitleSet.isEmpty()) {
      try {
        this.ddcTitleSet = fill(DDC_TITLE_URL, new ConeHandler(TITLE));
      } catch (Exception e) {
        // TODO
        if (!this.ddcTitleSet_OLD.isEmpty()) {
          this.ddcTitleSet.addAll(this.ddcTitleSet_OLD);
        }
      }
    }

    return this.ddcTitleSet;
  }

  public Set<String> getIso639_3_IdentifierSet() {
    if (this.iso639_3_IdentifierSet.isEmpty()) {
      try {
        this.iso639_3_IdentifierSet = fill(ISO639_3_IDENTIFIER_URL, new ConeHandler(IDENTIFIER));
      } catch (Exception e) {
        // TODO
        if (!this.iso639_3_IdentifierSet_OLD.isEmpty()) {
          this.iso639_3_IdentifierSet.addAll(this.iso639_3_IdentifierSet_OLD);
        }
      }
    }

    return this.iso639_3_IdentifierSet;
  }

  public Set<String> getIso639_3_TitleSet() {
    if (this.iso639_3_TitleSet.isEmpty()) {
      try {
        this.iso639_3_TitleSet = fill(ISO639_3_TITLE_URL, new ConeHandler(TITLE));
      } catch (Exception e) {
        // TODO
        if (!this.iso639_3_TitleSet_OLD.isEmpty()) {
          this.iso639_3_TitleSet.addAll(this.iso639_3_TitleSet_OLD);
        }
      }
    }

    return this.iso639_3_TitleSet;
  }

  public Set<String> getMimeTypesTitleSet() {
    if (this.mimeTypesTitleSet.isEmpty()) {
      try {
        this.mimeTypesTitleSet = fill(MIME_TYPES_TITLE_URL, new ConeHandler(TITLE));
      } catch (Exception e) {
        // TODO
        if (!this.mimeTypesTitleSet_OLD.isEmpty()) {
          this.mimeTypesTitleSet.addAll(this.mimeTypesTitleSet_OLD);
        }
      }
    }

    return this.mimeTypesTitleSet;
  }

  public Set<String> getMpipksTitleSet() {
    if (this.mpipksTitleSet.isEmpty()) {
      try {
        this.mpipksTitleSet = fill(MPIPKS_TITLE_URL, new ConeHandler(TITLE));
      } catch (Exception e) {
        // TODO
        if (!this.mpipksTitleSet_OLD.isEmpty()) {
          this.mpipksTitleSet.addAll(this.mpipksTitleSet_OLD);
        }
      }
    }

    return this.mpipksTitleSet;
  }

  public Set<String> getMpirgTitleSet() {
    if (this.mpirgTitleSet.isEmpty()) {
      try {
        this.mpirgTitleSet = fill(MPIRG_TITLE_URL, new ConeHandler(TITLE));
      } catch (Exception e) {
        // TODO
        if (!this.mpirgTitleSet_OLD.isEmpty()) {
          this.mpirgTitleSet.addAll(this.mpirgTitleSet_OLD);
        }
      }
    }

    return this.mpirgTitleSet;
  }

  public Set<String> getMpisGroupsTitleSet() {
    if (this.mpisGroupsTitleSet.isEmpty()) {
      try {
        this.mpisGroupsTitleSet = fill(MPIS_GROUPS_TITLE_URL, new ConeHandler(TITLE));
      } catch (Exception e) {
        // TODO
        if (!this.mpisGroupsTitleSet_OLD.isEmpty()) {
          this.mpisGroupsTitleSet.addAll(this.mpisGroupsTitleSet_OLD);
        }
      }
    }

    return this.mpisGroupsTitleSet;
  }

  public Set<String> getMpisProjectTitleSet() {
    if (this.mpisProjectTitleSet.isEmpty()) {
      try {
        this.mpisProjectTitleSet = fill(MPIS_PROJECTS_TITLE_URL, new ConeHandler(TITLE));
      } catch (Exception e) {
        // TODO
        if (!this.mpisProjectTitleSet_OLD.isEmpty()) {
          this.mpisProjectTitleSet.addAll(this.mpisProjectTitleSet_OLD);
        }
      }
    }

    return this.mpisProjectTitleSet;
  }

}
