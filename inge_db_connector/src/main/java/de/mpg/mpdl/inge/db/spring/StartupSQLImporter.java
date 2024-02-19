package de.mpg.mpdl.inge.db.spring;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.tool.schema.internal.script.MultiLineSqlScriptExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;

@Component
public class StartupSQLImporter {


  private static boolean databaseInitialized = false;

  private static final Logger logger = LogManager.getLogger(StartupSQLImporter.class);

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

        //String[] sqlCommandList = new MultipleLinesSqlCommandExtractor().extractCommands(sqlScriptScanner);
        List<String> sqlCommandList = new MultiLineSqlScriptExtractor().extractCommands(sqlScriptScanner, new PostgreSQLDialect());

        int updated = 0;
        for (String sqlStatement : sqlCommandList) {
          //Escape double colons
          sqlStatement = sqlStatement.replaceAll("::", "\\\\:\\\\:");
          logger.info("Executing: [" + sqlStatement + "]");
          updated += this.entityManager.createNativeQuery(sqlStatement).executeUpdate();
        }

        //entityManager.getTransaction().commit();
        logger.info("Database successfully initialized with " + updated + " updates");
      } catch (Exception e) {
        logger.error("Error while importing SQL ", e);
      }
    }


  }

}
