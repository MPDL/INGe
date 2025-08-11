package de.mpg.mpdl.inge.cone;

import de.mpg.mpdl.inge.cone.elasticsearch.ElasticSearchIndexer;
import de.mpg.mpdl.inge.util.PropertyReader;

public class SearchIndexerFactory {

  private static SearchEngineIndexer INSTANCE;

  public static SearchEngineIndexer createSearchEngineIndexer() throws ConeException {
    if (!isSearchIndexEnabled()) {
      throw new ConeException("Search Engine Indexer is not enabled. Enable in pubman.properties");
    }
    if (INSTANCE == null) {
      INSTANCE = new ElasticSearchIndexer();
      INSTANCE.initializeIndices(ModelList.getInstance());
    }
    return INSTANCE;
  }

  public static boolean isSearchIndexEnabled() {
    try {
      return Boolean.parseBoolean(PropertyReader.getProperty(PropertyReader.INGE_CONE_SEARCHINDEX_ENABLED));
    } catch (Exception e) {
      return false;
    }
  }

}
