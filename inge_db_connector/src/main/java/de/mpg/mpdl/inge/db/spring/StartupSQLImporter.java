package de.mpg.mpdl.inge.db.spring;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;
import org.hibernate.tool.hbm2ddl.MultipleLinesSqlCommandExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class StartupSQLImporter {


  private static boolean databaseInitialized = false;

  private static final Logger logger = Logger.getLogger(StartupSQLImporter.class);

  @Autowired
  private EntityManager entityManager;

  /**
   * Initializes the database by executing the SQL script from db_init.sql once on every startup
   * 
   */
  @EventListener(ContextRefreshedEvent.class)
  @Transactional
  public void importSQL() {
    if (!databaseInitialized) {
      logger.info("Initializing Database...");
      databaseInitialized = true;
      InputStream sqlInitStream = JPAConfiguration.class.getClassLoader().getResourceAsStream("db_init.sql");
      try (Reader sqlScriptScanner = new InputStreamReader(sqlInitStream, StandardCharsets.UTF_8)) {

        String[] sqlCommandList = new MultipleLinesSqlCommandExtractor().extractCommands(sqlScriptScanner);

        int updated = 0;
        for (String sqlStatement : sqlCommandList) {
          //Escape double colons
          sqlStatement = sqlStatement.replaceAll("::", "\\\\:\\\\:");
          logger.info("Executing: [" + sqlStatement + "]");
          updated += entityManager.createNativeQuery(sqlStatement).executeUpdate();
        }

        //entityManager.getTransaction().commit();
        logger.info("Database successfully initialized with " + updated + " updates");
      } catch (Exception e) {
        logger.error("Error while importing SQL ", e);
      }
    }


  }

}
