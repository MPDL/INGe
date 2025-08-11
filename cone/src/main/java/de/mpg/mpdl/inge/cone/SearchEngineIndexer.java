package de.mpg.mpdl.inge.cone;

import de.mpg.mpdl.inge.cone.elasticsearch.ElasticSearchIndexer;

public interface SearchEngineIndexer {


  void index(String model, String id, TreeFragment values) throws ConeException;

  void deleteFromIndex(String model, String id) throws ConeException;

  void initializeIndices(ModelList modelList) throws ConeException;

  void resetIndex(ModelList.Model model) throws ConeException;

  String simpleSearch(String model, String query, int from, int size) throws ConeException;


}
