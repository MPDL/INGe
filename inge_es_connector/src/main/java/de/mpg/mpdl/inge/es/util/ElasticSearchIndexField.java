package de.mpg.mpdl.inge.es.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.elasticsearch.cluster.metadata.MappingMetaData;


public class ElasticSearchIndexField {


  public enum Type
  {
    TEXT, KEYWORD, BOOLEAN, DATE, NUMERIC, UNKNOWN;

  }

  private String indexName;

  private List<String> nestedPaths;

  private Type type;


  public String getIndexName() {
    return indexName;
  }



  public void setIndexName(String indexName) {
    this.indexName = indexName;
  }



  public List<String> getNestedPaths() {
    return nestedPaths;
  }



  public void setNestedPaths(List<String> nestedPaths) {
    this.nestedPaths = nestedPaths;
  }



  public Type getType() {
    return type;
  }



  public void setType(Type type) {
    this.type = type;
  }


  public String toString() {
    return getIndexName() + " (" + getType() + ")" + " -- " + getNestedPaths();
  }

  /**
   * Factory class that produces a Map of ElasticSearchIndexField objects with field name as keys.
   * Requires an Elasticsearch MappingMetadata object
   * 
   * @author haarlae1
   * 
   */
  public static class Factory {

    public static Map<String, ElasticSearchIndexField> createIndexMapFromElasticsearch(MappingMetaData mdd) {

      Map<String, ElasticSearchIndexField> indexMap = new TreeMap<>();

      fillMap("", mdd.getSourceAsMap(), indexMap, "", new ArrayList<>());

      return indexMap;
    }



    private static void fillMap(String parentKey, Map<String, Object> mappingMap, Map<String, ElasticSearchIndexField> indexMap,
        String currentPath, List<String> currentNestedPaths) {

      for (Entry<String, Object> entry : mappingMap.entrySet()) {

        StringBuilder newCurrentPath = new StringBuilder(currentPath);
        List<String> newCurrentNestedPaths = new ArrayList<>(currentNestedPaths);

        if (parentKey != null && (parentKey.equals("properties") || parentKey.equals("fields"))) {
          if (newCurrentPath.length() > 0) {
            newCurrentPath.append(".");
          }
          newCurrentPath.append(entry.getKey());
        }

        else if (mappingMap.containsKey("type")) {
          String type = (String) mappingMap.get("type");
          if (type.equals("nested")) {
            newCurrentNestedPaths.add(newCurrentPath.toString());
          }

          else {
            ElasticSearchIndexField indexField = createIndexFieldObject(newCurrentPath.toString(), newCurrentNestedPaths, type);
            indexMap.put(newCurrentPath.toString(), indexField);

          }
        }
        if (entry.getValue() instanceof Map) {
          fillMap(entry.getKey(), (Map<String, Object>) entry.getValue(), indexMap, newCurrentPath.toString(), newCurrentNestedPaths);
        }



      }



    }

    private static ElasticSearchIndexField createIndexFieldObject(String path, List<String> nestedPath, String type) {
      ElasticSearchIndexField indexField = new ElasticSearchIndexField();

      indexField.setIndexName(path.toString());
      if (nestedPath != null && !nestedPath.isEmpty()) {
        indexField.setNestedPaths(new ArrayList<>(nestedPath));
      }
      switch (type) {
        case "text": {
          indexField.setType(Type.TEXT);
          break;
        }
        case "keyword": {
          indexField.setType(Type.KEYWORD);
          break;
        }
        case "boolean": {
          indexField.setType(Type.BOOLEAN);
          break;
        }
        case "date": {
          indexField.setType(Type.DATE);
          break;
        }

        case "long":
        case "integer":
        case "short":
        case "byte":
        case "double":
        case "float":
        case "half_float":
        case "scale_float": {
          indexField.setType(Type.NUMERIC);
          break;
        }
        default: {
          indexField.setType(Type.UNKNOWN);
        }

      }
      return indexField;
    }

  }


}
