package de.mpg.mpdl.inge.service.util;

import de.mpg.mpdl.inge.util.PropertyReader;
import de.mpg.mpdl.inge.util.ResourceUtil;
import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.ResourceBundle;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.xml.sax.helpers.DefaultHandler;

@Component
public class GenrePropertiesProvider {
  private static final Logger logger = LogManager.getLogger(GenrePropertiesProvider.class);

  public GenrePropertiesProvider() {}

  @PostConstruct
  public void runOnceAtStartup() {
    try {
      logger.info("CRON: Starting to create Genre Properties.");
      InputStream file = ResourceUtil.getResourceAsStream(PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_GENRES_CONFIGURATION),
          GenrePropertiesProvider.class.getClassLoader());

      SAXParserFactory factory = SAXParserFactory.newInstance();
      SAXParser parser = factory.newSAXParser();

      String jbossHomeDir = System.getProperty(PropertyReader.JBOSS_HOME_DIR);
      DefaultHandler handler = new GenreHandler(jbossHomeDir + "/modules/pubman/main");

      parser.parse(file, handler);

      // Clear cache of resource bundles in order to load the newly created ones
      ResourceBundle.clearCache();

      logger.info("CRON: Finished creating Genre Properties.");
    } catch (Exception e) {
      logger.error("Error creating Genre Properties", e);
    }
  }
}
