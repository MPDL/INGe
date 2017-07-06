package de.mpg.mpdl.inge.es.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.mpg.mpdl.inge.es.connector.ElasticSearchTransportClientProvider;


public class ElasticSearchIndexField {


  enum Type {
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

    public static Map<String, ElasticSearchIndexField> createIndexMapFromElasticsearch(
        MappingMetaData mdd) {

      Map<String, ElasticSearchIndexField> indexMap = new TreeMap<>();

      try {
        fillMap("", mdd.getSourceAsMap(), indexMap, "", new ArrayList<>());
      } catch (IOException e) {
        e.printStackTrace();
      }

      return indexMap;
    }



    private static void fillMap(String parentKey, Map<String, Object> mappingMap,
        Map<String, ElasticSearchIndexField> indexMap, String currentPath,
        List<String> currentNestedPaths) {

      for (Entry<String, Object> entry : mappingMap.entrySet()) {

        StringBuilder newCurrentPath = new StringBuilder(currentPath);
        List<String> newCurrentNestedPaths = new ArrayList<>(currentNestedPaths);

        if (parentKey != null && parentKey.equals("properties") || parentKey.equals("fields")) {
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
            ElasticSearchIndexField indexField = new ElasticSearchIndexField();
            indexMap.put(newCurrentPath.toString(), indexField);
            indexField.setIndexName(newCurrentPath.toString());
            if (!newCurrentNestedPaths.isEmpty()) {
              indexField.setNestedPaths(new ArrayList<>(newCurrentNestedPaths));
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
          }


        }

        if (entry.getValue() instanceof Map) {
          fillMap(entry.getKey(), (Map<String, Object>) entry.getValue(), indexMap,
              newCurrentPath.toString(), newCurrentNestedPaths);
        }
      }


    }



  }


}
