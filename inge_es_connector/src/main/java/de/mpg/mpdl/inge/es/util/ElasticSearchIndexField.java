package de.mpg.mpdl.inge.es.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch._types.mapping.PropertyBase;


public class ElasticSearchIndexField {


  public enum Type
  {
    TEXT, KEYWORD, BOOLEAN, DATE, NUMERIC, UNKNOWN

  }

  private String indexName;

  private List<String> nestedPaths;

  private Type type;


  public String getIndexName() {
    return this.indexName;
  }



  public void setIndexName(String indexName) {
    this.indexName = indexName;
  }



  public List<String> getNestedPaths() {
    return this.nestedPaths;
  }



  public void setNestedPaths(List<String> nestedPaths) {
    this.nestedPaths = nestedPaths;
  }



  public Type getType() {
    return this.type;
  }



  public void setType(Type type) {
    this.type = type;
  }


  public String toString() {
    return this.indexName + " (" + this.type + ")" + " -- " + this.nestedPaths;
  }

  /**
   * Factory class that produces a Map of ElasticSearchIndexField objects with field name as keys.
   * Requires an Elasticsearch MappingMetadata object
   *
   * @author haarlae1
   *
   */
  public static class Factory {

    private Factory() {}

    public static Map<String, ElasticSearchIndexField> createIndexMapFromElasticsearch(Map<String, Property> resultMap) {

      Map<String, ElasticSearchIndexField> indexMap = new TreeMap<>();
      fillMap(resultMap, indexMap, "", new ArrayList<>());

      return indexMap;
    }



    private static void fillMap(Map<String, Property> mappingMap, Map<String, ElasticSearchIndexField> indexMap, String currentPath,
        List<String> currentNestedPaths) {

      for (Map.Entry<String, Property> entry : mappingMap.entrySet()) {

        StringBuilder newCurrentPath = new StringBuilder(currentPath);
        List<String> newCurrentNestedPaths = new ArrayList<>(currentNestedPaths);

        if (!newCurrentPath.isEmpty()) {
          newCurrentPath.append(".");
        }
        newCurrentPath.append(entry.getKey());

        if (entry.getValue().isNested()) {
          newCurrentNestedPaths.add(newCurrentPath.toString());
        }


        if (!entry.getValue().isObject() && !entry.getValue().isNested()) {
          ElasticSearchIndexField indexField =
              createIndexFieldObject(newCurrentPath.toString(), newCurrentNestedPaths, entry.getValue()._kind().jsonValue());
          indexMap.put(newCurrentPath.toString(), indexField);
          //System.out.println(newCurrentPath.toString() + " -- " + newCurrentNestedPaths + " -- " + entry.getValue()._kind().jsonValue());
        }

        if (entry.getValue().isObject()) {
          fillMap(entry.getValue().object().properties(), indexMap, newCurrentPath.toString(), newCurrentNestedPaths);
        } else if (entry.getValue().isNested()) {
          fillMap(entry.getValue().nested().properties(), indexMap, newCurrentPath.toString(), newCurrentNestedPaths);
        } else {
          fillMap(((PropertyBase) entry.getValue()._get()).fields(), indexMap, newCurrentPath.toString(), newCurrentNestedPaths);
        }


      }

      /*
      for (Entry<String, Property> entry : mappingMap.entrySet()) {
      
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
      */



    }

    private static ElasticSearchIndexField createIndexFieldObject(String path, List<String> nestedPath, String type) {
      ElasticSearchIndexField indexField = new ElasticSearchIndexField();

      indexField.setIndexName(path);
      if (null != nestedPath && !nestedPath.isEmpty()) {
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
