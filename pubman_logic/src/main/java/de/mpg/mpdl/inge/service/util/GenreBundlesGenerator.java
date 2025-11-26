package de.mpg.mpdl.inge.service.util;

import de.mpg.mpdl.inge.util.PropertyReader;
import de.mpg.mpdl.inge.util.ResourceUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.util.ResourceBundle;

public class GenreBundlesGenerator {

  private static final Logger logger = LogManager.getLogger(GenreBundlesGenerator.class);

  public final static void generateGenreBundles(String directory) {
    try {
      logger.info("*** CRON (onceAtStartup): Starting to create Genre Properties.");

      InputStream file = ResourceUtil.getResourceAsStream("Genres.xml", GenrePropertiesProvider.class.getClassLoader());

      SAXParserFactory factory = SAXParserFactory.newInstance();
      SAXParser parser = factory.newSAXParser();

      String jbossHomeDir = System.getProperty(PropertyReader.JBOSS_HOME_DIR);
      DefaultHandler handler = new GenreHandler(directory);

      parser.parse(file, handler);

      // Clear cache of resource bundles in order to load the newly created ones
      ResourceBundle.clearCache();

      logger.info("*** CRON: Finished creating Genre Properties.");
    } catch (Exception e) {
      logger.error("*** CRON: Error creating Genre Properties", e);
    }
  }

  public static void main(String[] args) {
    String targetDir = args[0];
    //String compileDir = args[1];
    generateGenreBundles(targetDir + "/genreBundles");
  }
}
