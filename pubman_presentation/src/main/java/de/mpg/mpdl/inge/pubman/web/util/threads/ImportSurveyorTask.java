package de.mpg.mpdl.inge.pubman.web.util.threads;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import de.mpg.mpdl.inge.pubman.web.multipleimport.BaseImportLog;
import de.mpg.mpdl.inge.pubman.web.multipleimport.DbTools;
import de.mpg.mpdl.inge.pubman.web.multipleimport.ImportLog;
import de.mpg.mpdl.inge.pubman.web.multipleimport.ImportLogItem;

@Component
public class ImportSurveyorTask {
  private static final Logger logger = LogManager.getLogger(ImportSurveyorTask.class);

  @Autowired
  private DataSource dataSource;

  public ImportSurveyorTask() {}

  @Scheduled(cron = "${inge.cron.import.surveyor}")
  public void run() {
    logger.info("CRON: Import surveyor task checks logs...");

    Connection connection = null;
    ResultSet rs = null;
    PreparedStatement ps = null;

    // Searches for Import-Items which are in status "pending" OR "rollback" AND which have NOT
    // been changed in the last 60 Minutes
    final String query = "select id from import_log where (status = 'PENDING' or status = 'ROLLBACK') "
        + "and id not in (select parent from import_log_item " + "               where localtimestamp - interval '60 minutes' < startdate)";

    try {
      connection = this.dataSource.getConnection();
      ps = connection.prepareStatement(query);
      rs = ps.executeQuery();
      while (rs.next()) {
        int id = rs.getInt("id");
        logger.warn("Unfinished import detected (" + id + "). Finishing it with status FATAL.");
        ImportLog log = ImportLog.getImportLog(id, true, connection);

        for (ImportLogItem item : log.getItems()) {
          if (null == item.getEndDate()) {
            log.activateItem(item);
            log.addDetail(BaseImportLog.ErrorLevel.WARNING, "import_process_terminate_item", connection);
            log.finishItem(connection);
          }
        }

        log.startItem("import_process_aborted_unexpectedly", connection);
        log.addDetail(BaseImportLog.ErrorLevel.FATAL, "import_process_failed", connection);
        log.finishItem(connection);
        log.close(connection);
      }
    } catch (Exception e) {
      logger.error("Error checking database for unfinished imports", e);
    } finally {
      // DbTools.closeResultSet(rs);
      // DbTools.closePreparedStatement(ps);
      DbTools.closeConnection(connection);
    }

    logger.info("CRON: Import surveyor task finished.");
  }
}
