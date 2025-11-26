package de.mpg.mpdl.inge.service.util;

import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.util.PropertyReader;
import de.mpg.mpdl.inge.util.ResourceUtil;
import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.xml.sax.helpers.DefaultHandler;

@Component
public class GenrePropertiesProvider {
  private static final Logger logger = LogManager.getLogger(GenrePropertiesProvider.class);

  public GenrePropertiesProvider() {}

  public static JSONObject getGenreProperties(MdsPublicationVO.Genre genre) {
    ResourceBundle genreBundle = ResourceBundle.getBundle("genreBundles/Genre_" + genre.toString());

    Map<String, String> map = new LinkedHashMap<>();
    for (Enumeration<?> keys = genreBundle.getKeys(); keys.hasMoreElements();) {
      String key = keys.nextElement().toString();
      map.put(key, genreBundle.getString(key));
    }

    TreeMap<String, JSONObject> sortedBaseKeys = new TreeMap<>();
    for (Map.Entry<String, String> entry : map.entrySet()) {
      String mapKey = entry.getKey();

      if (mapKey.endsWith("_display")) {
        String baseKey = mapKey.substring(0, mapKey.lastIndexOf("_display"));
        JSONObject attributeDetails = new JSONObject();
        attributeDetails.put("display", Boolean.parseBoolean(map.get(baseKey + "_display")));
        attributeDetails.put("optional", Boolean.parseBoolean(map.get(baseKey + "_optional")));
        attributeDetails.put("repeatable", Boolean.parseBoolean(map.get(baseKey + "_repeatable")));
        sortedBaseKeys.put(baseKey, attributeDetails);
      }
    }

    JSONObject properties = new JSONObject();
    sortedBaseKeys.forEach((baseKey, attributeDetails) -> {
      properties.put(baseKey, attributeDetails);
    });

    JSONObject json = new JSONObject();
    json.put("genre", genre.toString());
    json.put("properties", properties);

    return json;
  }
}
