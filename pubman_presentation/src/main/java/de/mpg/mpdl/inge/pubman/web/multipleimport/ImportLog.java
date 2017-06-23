/*
 * 
 * CDDL HEADER START
 * 
 * The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with
 * the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or
 * http://www.escidoc.org/license. See the License for the specific language governing permissions
 * and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License
 * file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with
 * the fields enclosed by brackets "[]" replaced with your own identifying information: Portions
 * Copyright [yyyy] [name of copyright owner]
 * 
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */

package de.mpg.mpdl.inge.pubman.web.multipleimport;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.MissingResourceException;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PublicationAdminDescriptorVO.Workflow;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.pubman.web.util.beans.InternationalizationHelper;
import de.mpg.mpdl.inge.pubman.web.util.beans.LoginHelper;
import de.mpg.mpdl.inge.transformation.TransformerFactory;

/**
 * Class that describes an import.
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class ImportLog {
  /**
   * enum to describe the general state of the log.
   */
  public enum Status {
    PENDING, SUSPENDED, FINISHED, ROLLBACK
  }

  /**
   * enum to describe if something went wrong with this element.
   * 
   * - FINE: everything is alright - WARNING: import worked, but something could have been done
   * better - PROBLEM: some item was not imported because validation failed - ERROR: some items were
   * not imported because there were system errors during the import - FATAL: the import was
   * interrupted completely due to system errors
   */
  public enum ErrorLevel {
    FINE, WARNING, PROBLEM, ERROR, FATAL
  }

  /**
   * enum defining possible sorting columns.
   */
  public enum SortColumn {
    STARTDATE, ENDDATE, NAME, FORMAT, STATUS, ERRORLEVEL;

    /**
     * @return A representation of the element that is used for storing in a database
     */
    public String toSQL() {
      return super.toString().toLowerCase();
    }
  }

  /**
   * enum defining sorting directions.
   * 
   */
  public enum SortDirection {
    ASCENDING, DESCENDING;

    /**
     * @return A representation of the element that is used for storing in a database
     */
    public String toSQL() {
      final String value = super.toString();
      return value.replace("ENDING", "").toLowerCase();
    }
  }

  public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

  private static final Logger logger = Logger.getLogger(ImportLog.class);

  private Date endDate;
  private Date startDate;
  private ErrorLevel errorLevel;
  private ImportLogItem currentImportLogItem = null;
  private List<ImportLogItem> importLogItems = new ArrayList<ImportLogItem>();
  private Status status;
  private String context;
  private TransformerFactory.FORMAT format;
  private String message;
  private String user;
  private String userHandle;
  private Workflow workflow;
  private int percentage;
  private int id;

  protected ImportLog() {}

  public ImportLog(String user, TransformerFactory.FORMAT format, Connection connection) {
    this.startDate = new Date();
    this.status = Status.PENDING;
    this.errorLevel = ErrorLevel.FINE;
    this.user = user;
    this.format = format;

    this.saveImportLog(connection);
  }

  public void close(Connection connection) {
    this.endDate = new Date();
    this.status = Status.FINISHED;
    this.percentage = 100;

    this.updateImportLog(connection);
  }

  public void reopen(Connection connection) {
    this.endDate = null;
    this.status = Status.PENDING;

    this.updateImportLog(connection);
  }

  /**
   * Creates a new item using the given message.
   * 
   * Defaults: - Item id will be set to null - Start date will be set to the current date - Error
   * level will be set to FINE.
   * 
   * @param msg A message key for a localized message
   */
  public void startItem(String msg, Connection connection) {
    this.startItem(msg, null, connection);
  }

  /**
   * Creates a new item using the given message and item id, then putting the focus of the import on
   * it.
   * 
   * Defaults: - Start date will be set to the current date - Error level will be set to FINE.
   * 
   * @param msg A message key for a localized message
   * @param itemId The eSciDoc id of the imported item
   */
  public void startItem(String msg, String itemId, Connection connection) {
    this.startItem(msg, new Date(), itemId, connection);
  }

  /**
   * Creates a new item using the given message, item id and start date, then putting the focus of
   * the import on it.
   * 
   * Defaults: - Error level will be set to FINE.
   * 
   * @param msg A message key for a localized message
   * @param sDate The start date of this item
   * @param itemId The eSciDoc id of the imported item
   */
  public void startItem(String msg, Date sDate, String itemId, Connection connection) {
    this.startItem(ErrorLevel.FINE, msg, sDate, itemId, connection);
  }

  /**
   * Creates a new item using the given error level and message, then putting the focus of the
   * import on it.
   * 
   * Defaults: - Item id will be set to null - Start date will be set to the current date
   * 
   * @param errLevel The initial error level of this item
   * @param msg A message key for a localized message
   */
  public void startItem(ErrorLevel errLevel, String msg, Connection connection) {
    this.startItem(errLevel, msg, new Date(), null, connection);
  }

  /**
   * Creates a new item using the given error level, message, item id and start date, then putting
   * the focus of the import on it.
   * 
   * @param errLevel The initial error level of this item
   * @param msg A message key for a localized message
   * @param sDate The start date of this item
   * @param itemId The eSciDoc id of the imported item
   */
  public void startItem(ErrorLevel errLevel, String msg, Date sDate, String itemId,
      Connection connection) {
    if (this.currentImportLogItem != null) {
      throw new RuntimeException(
          "Trying to start logging an item while another is not yet finished");
    }

    final ImportLogItem newItem = new ImportLogItem(this, connection);

    newItem.setErrorLevel(errLevel, connection);
    newItem.setMessage(msg);
    newItem.setStartDate(sDate);

    this.saveImportLogItem(newItem, connection);

    this.importLogItems.add(newItem);

    this.currentImportLogItem = newItem;
  }

  /**
   * Sets the status of the focused item to FINISHED and the end date to the current date, then
   * removes the focus of the import.
   */
  public void finishItem(Connection connection) {
    if (this.currentImportLogItem != null) {
      this.currentImportLogItem.setEndDate(new Date());
      this.currentImportLogItem.setStatus(Status.FINISHED);

      this.updateImportLogItem(this.currentImportLogItem, connection);

      this.currentImportLogItem = null;
    }
  }

  /**
   * Sets the status of the focused item to SUSPENDED. This should be done when it is planned to
   * visit this item again later. I.e. in a first step, all items are transformed and validated,
   * then suspended. In a second step, all items are imported into the repository.
   */
  public void suspendItem(Connection connection) {
    if (this.currentImportLogItem != null) {
      this.currentImportLogItem.setStatus(Status.SUSPENDED);

      this.updateImportLogItem(this.currentImportLogItem, connection);

      this.currentImportLogItem = null;
    }
  }

  /**
   * Adds a detail to the focused item using the given error level and message key. Start- and
   * end-date are set to the current date. Status is set to FINISHED.
   * 
   * Defaults: - The detail id will be set to null
   * 
   * @param errLevel The error level of this item
   * @param msg A message key for a localized message
   */
  public void addDetail(ErrorLevel errLevel, String msg, Connection connection) {
    this.addDetail(errLevel, msg, null, connection);
  }

  /**
   * Adds a detail to the focused item using the given error level, message key and detail id.
   * Start- and end-date are set to the current date. Status is set to FINISHED.
   * 
   * @param errLevel The error level of this item
   * @param msg A message key for a localized message
   * @param detailId The (eSciDoc) id related to this detail (e.g. the id of an identified
   *        duplicate)
   */
  public void addDetail(ErrorLevel errLevel, String msg, String detailId, Connection connection) {
    if (this.currentImportLogItem == null) {
      throw new RuntimeException("Trying to add a detail but no log item is started.");
    }

    final ImportLogItemDetail importLogItemDetail =
        new ImportLogItemDetail(this.currentImportLogItem, connection);
    importLogItemDetail.setErrorLevel(errLevel, connection);
    importLogItemDetail.setMessage(msg);
    importLogItemDetail.setStartDate(new Date());
    importLogItemDetail.setStatus(Status.FINISHED);

    if (this.currentImportLogItem == null) {
      this.startItem("", connection);
    }

    this.currentImportLogItem.getItems().add(importLogItemDetail);

    this.saveImportLogItemDetail(importLogItemDetail, connection);
  }

  /**
   * Adds a detail to the focused item using the given error level and a previously caught
   * exception. Start- and end-date are set to the current date. Status is set to FINISHED. The
   * exception is transformed into a stack trace.
   * 
   * @param errLevel The error level of this item
   * @param exception The exception that should be added to the item
   */
  public void addDetail(ErrorLevel errLevel, Exception exception, Connection connection) {
    final String msg = this.getExceptionMessage(exception);
    this.addDetail(errLevel, msg, null, connection);
  }

  /**
   * @param itemVO Assigns a value object to the focused item.
   */
  public void setItemVO(PubItemVO itemVO) {
    this.currentImportLogItem.setItemVO(itemVO);
  }

  /**
   * Transforms an exception into a Java stack trace.
   * 
   * @param exception The exception
   * @return The stack trace
   */
  private String getExceptionMessage(Throwable exception) {
    final StringWriter stringWriter = new StringWriter();
    stringWriter.write(exception.getClass().getSimpleName());

    if (exception.getMessage() != null) {
      stringWriter.write(": ");
      stringWriter.write(exception.getMessage());
    }
    stringWriter.write("\n");

    final StackTraceElement[] stackTraceElements = exception.getStackTrace();
    stringWriter.write("\tat ");
    stringWriter.write(stackTraceElements[0].getClassName());
    stringWriter.write(".");
    stringWriter.write(stackTraceElements[0].getMethodName());
    stringWriter.write("(");
    stringWriter.write(stackTraceElements[0].getFileName());
    stringWriter.write(":");
    stringWriter.write(stackTraceElements[0].getLineNumber() + "");
    stringWriter.write(")\n");

    if (exception.getCause() != null) {
      stringWriter.write(this.getExceptionMessage(exception.getCause()));
    }

    return stringWriter.toString();
  }

  public void setItemId(String id, Connection connection) {
    this.currentImportLogItem.setItemId(id);
    this.updateImportLogItem(this.currentImportLogItem, connection);
  }

  public boolean isDone() {
    return (this.status == Status.FINISHED);
  }

  public Date getStartDate() {
    return this.startDate;
  }

  public String getStartDateFormatted() {
    if (this.startDate != null) {
      return ImportLog.DATE_FORMAT.format(this.startDate);
    }

    return "";
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public Date getEndDate() {
    return this.endDate;
  }

  public String getEndDateFormatted() {
    if (this.endDate != null) {
      return ImportLog.DATE_FORMAT.format(this.endDate);
    }

    return "";
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public Status getStatus() {
    return this.status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public TransformerFactory.FORMAT getFormat() {
    return this.format;
  }

  public void setFormat(TransformerFactory.FORMAT format) {
    this.format = format;
  }

  public String getUser() {
    return this.user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getContext() {
    return this.context;
  }

  public void setContext(String context) {
    this.context = context;
  }

  public ErrorLevel getErrorLevel() {
    return this.errorLevel;
  }

  public void setErrorLevel(ErrorLevel errorLevel) {
    setErrorLevel(errorLevel, null);
  }

  public void setErrorLevel(ErrorLevel errorLevel, Connection connection) {
    if (this.errorLevel == null
        || errorLevel == ErrorLevel.FATAL
        || (errorLevel == ErrorLevel.ERROR && this.errorLevel != ErrorLevel.FATAL)
        || (errorLevel == ErrorLevel.PROBLEM && this.errorLevel != ErrorLevel.FATAL && this.errorLevel != ErrorLevel.ERROR)
        || (errorLevel == ErrorLevel.WARNING && this.errorLevel != ErrorLevel.FATAL
            && this.errorLevel != ErrorLevel.ERROR && this.errorLevel != ErrorLevel.PROBLEM)) {
      this.errorLevel = errorLevel;
    }

    if (connection != null) {
      this.updateImportLog(connection);
    }
  }

  public boolean getFinished() {
    return (this.status == Status.FINISHED);
  }

  public boolean getImportedItems() {
    for (final ImportLogItem item : this.importLogItems) {
      if (item.getItemId() != null) {
        return true;
      }
    }

    return false;
  }

  public List<ImportLogItem> getItems() {
    return this.importLogItems;
  }

  public void setItems(List<ImportLogItem> items) {
    this.importLogItems = items;
  }

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public ImportLogItem getCurrentItem() {
    return this.currentImportLogItem;
  }

  public String getLocalizedMessage() {
    try {
      return ((InternationalizationHelper) FacesTools.findBean("InternationalizationHelper"))
          .getMessage(this.getMessage());
    } catch (final MissingResourceException mre) {
      // No message entry for this message, it's probably raw data.
      return this.getMessage();
    }
  }

  public String getMessage() {
    return this.message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getUserHandle() {
    return this.userHandle;
  }

  public void setUserHandle(String userHandle) {
    this.userHandle = userHandle;
  }

  public int getPercentage() {
    return this.percentage;
  }

  public void setPercentage(int percentage, Connection connection) {
    this.percentage = percentage;

    this.updateImportLog(connection);
  }

  private synchronized void saveImportLog(Connection connection) {
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ps =
          connection.prepareStatement("insert into import_log "
              + "(status, errorlevel, startdate, userid, name, context, format, percentage) "
              + "values (?, ?, ?, ?, ?, ?, ?, 0)");

      ps.setString(1, this.status.toString());
      ps.setString(2, this.errorLevel.toString());
      ps.setTimestamp(3, new Timestamp(this.startDate.getTime()));
      ps.setString(4, this.user);
      ps.setString(5, this.message);
      ps.setString(6, this.context);
      ps.setString(7, this.format.name());

      ps.executeUpdate();
      DbTools.closePreparedStatement(ps);

      ps = connection.prepareStatement("select max(id) as maxid from import_log");

      rs = ps.executeQuery();

      if (rs.next()) {
        this.id = rs.getInt("maxid");
      } else {
        throw new RuntimeException("Error saving log");
      }
    } catch (final Exception e) {
      throw new RuntimeException("Error saving log", e);
    } finally {
      DbTools.closeResultSet(rs);
      DbTools.closePreparedStatement(ps);
    }
  }

  private synchronized void updateImportLog(Connection connection) {
    PreparedStatement ps = null;

    try {
      ps =
          connection.prepareStatement("update import_log set status = ?, errorlevel = ?, "
              + "startdate = ?, enddate = ?, userid = ?, name = ?, "
              + "context = ?, format = ?, percentage = ? where id = ?");

      ps.setString(1, this.status.toString());
      ps.setString(2, this.errorLevel.toString());
      ps.setTimestamp(3, new Timestamp(this.startDate.getTime()));

      if (this.endDate != null) {
        ps.setTimestamp(4, new Timestamp(this.endDate.getTime()));
      } else {
        ps.setTimestamp(4, null);
      }

      ps.setString(5, this.user);
      ps.setString(6, this.message);
      ps.setString(7, this.context);
      ps.setString(8, this.format.name());
      ps.setInt(9, this.percentage);
      ps.setInt(10, this.id);

      ps.executeUpdate();
    } catch (final Exception e) {
      throw new RuntimeException("Error saving log", e);
    } finally {
      DbTools.closePreparedStatement(ps);
    }
  }

  private synchronized void saveImportLogItem(ImportLogItem importLogItem, Connection connection) {
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ps =
          connection.prepareStatement("insert into import_log_item "
              + "(status, errorlevel, startdate, parent, message, item_id) "
              + "values (?, ?, ?, ?, ?, ?)");

      ps.setString(1, importLogItem.getStatus().toString());
      ps.setString(2, importLogItem.getErrorLevel().toString());
      ps.setTimestamp(3, new Timestamp(importLogItem.getStartDate().getTime()));
      ps.setInt(4, this.id);
      ps.setString(5, importLogItem.getMessage());
      ps.setString(6, importLogItem.getItemId());

      ps.executeUpdate();
      DbTools.closePreparedStatement(ps);

      ps = connection.prepareStatement("select max(id) as maxid from import_log_item");

      rs = ps.executeQuery();

      if (rs.next()) {
        importLogItem.setId(rs.getInt("maxid"));
      } else {
        throw new RuntimeException("Error saving log item");
      }
    } catch (final Exception e) {
      throw new RuntimeException("Error saving log", e);
    } finally {
      DbTools.closeResultSet(rs);
      DbTools.closePreparedStatement(ps);
    }
  }

  private synchronized void updateImportLogItem(ImportLogItem importLogItem, Connection connection) {
    PreparedStatement ps = null;

    try {
      ps =
          connection.prepareStatement("update import_log_item set status = ?, "
              + "errorlevel = ?, startdate = ?, enddate = ?, parent = ?, "
              + "message = ?, item_id = ? where id = ?");

      ps.setString(1, importLogItem.getStatus().toString());
      ps.setString(2, importLogItem.getErrorLevel().toString());
      ps.setTimestamp(3, new Timestamp(importLogItem.getStartDate().getTime()));

      if (importLogItem.getEndDate() != null) {
        ps.setTimestamp(4, new Timestamp(importLogItem.getEndDate().getTime()));
      } else {
        ps.setDate(4, null);
      }

      ps.setInt(5, this.id);
      ps.setString(6, importLogItem.getMessage());
      ps.setString(7, importLogItem.getItemId());
      ps.setInt(8, importLogItem.getId());

      ps.executeUpdate();
    } catch (final Exception e) {
      throw new RuntimeException("Error saving log", e);
    } finally {
      DbTools.closePreparedStatement(ps);
    }
  }

  private synchronized void saveImportLogItemDetail(ImportLogItemDetail importLogItemDetail,
      Connection connection) {
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ps =
          connection.prepareStatement("insert into import_log_item_detail "
              + "(status, errorlevel, startdate, parent, message) values (?, ?, ?, ?, ?)");

      ps.setString(1, importLogItemDetail.getStatus().toString());
      ps.setString(2, importLogItemDetail.getErrorLevel().toString());
      ps.setTimestamp(3, new Timestamp(importLogItemDetail.getStartDate().getTime()));
      ps.setInt(4, importLogItemDetail.getParent().getId());
      ps.setString(5, importLogItemDetail.getMessage());

      ps.executeUpdate();
    } catch (final Exception e) {
      throw new RuntimeException("Error saving log", e);
    } finally {
      DbTools.closeResultSet(rs);
      DbTools.closePreparedStatement(ps);
    }
  }

  /**
   * Retrieves a users imports from the database.
   * 
   * Defaults: - items are loaded - item details are loaded
   * 
   * @param action Usually "import"
   * @param user The user's value object
   * @param sortBy The column the logs should be sorted by
   * @param dir The direction the imports should be sorted by
   * 
   * @return A list of imports
   */
  public static List<ImportLog> getImportLogs(AccountUserVO user, SortColumn sortBy,
      SortDirection dir, Connection connection) {

    return ImportLog.getImportLogs(user, sortBy, dir, true, connection);
  }

  /**
   * Retrieves a users imports from the database.
   * 
   * @param action Usually "import"
   * @param user The user's value object
   * @param sortBy The column the logs should be sorted by
   * @param dir The direction the imports should be sorted by
   * @param loadItems Indicates whether the import items should be loaded
   * @param loadDetails Indicates whether the items details should be loaded
   * 
   * @return A list of imports
   */
  public static List<ImportLog> getImportLogs(AccountUserVO user, SortColumn sortBy,
      SortDirection dir, boolean loadDetails, Connection connection) {
    final List<ImportLog> result = new ArrayList<ImportLog>();
    PreparedStatement ps = null;
    ResultSet rs = null;

    final String query =
        "select id from import_log where userid = ? " + "order by " + sortBy.toSQL() + " "
            + dir.toSQL();

    try {
      ps = connection.prepareStatement(query);
      ps.setString(1, user.getReference().getObjectId());

      rs = ps.executeQuery();

      while (rs.next()) {
        final int id = rs.getInt("id");
        final ImportLog log = ImportLog.getImportLog(id, loadDetails, connection);
        log.userHandle = user.getHandle();
        result.add(log);
      }
    } catch (final Exception e) {
      throw new RuntimeException("Error getting log", e);
    } finally {
      DbTools.closeResultSet(rs);
      DbTools.closePreparedStatement(ps);
    }

    return result;
  }

  /**
   * Get a single import by its stored id.
   * 
   * @param id The id
   * @param loadItems Indicates whether the import items should be loaded
   * @param loadDetails Indicates whether the items details should be loaded
   * 
   * @return The import
   */
  public static ImportLog getImportLog(int id, boolean loadDetails, Connection connection) {
    PreparedStatement ps = null;
    ResultSet rs = null;
    String query = null;
    ImportLog importLog = null;

    try {
      query = "select * from import_log where id = ?";
      ps = connection.prepareStatement(query);
      ps.setInt(1, id);
      rs = ps.executeQuery();

      if (rs.next()) {
        importLog = ImportLog.fillImportLog(rs, connection);
      } else {
        ImportLog.logger.warn("Import log query returned no result for id " + id);
      }

      DbTools.closePreparedStatement(ps);

      query = "select * from import_log_item where parent = ? order by id";
      ps = connection.prepareStatement(query);
      ps.setInt(1, id);
      rs = ps.executeQuery();

      final List<ImportLogItem> importLogItems = new ArrayList<ImportLogItem>();

      while (rs.next()) {
        final ImportLogItem importLogItem = ImportLog.fillImportLogItem(rs, importLog, connection);
        importLogItems.add(importLogItem);
      }

      importLog.setItems(importLogItems);

      DbTools.closePreparedStatement(ps);

      if (loadDetails) {
        query =
            "select import_log_item_detail.* from import_log_item, import_log_item_detail "
                + "where import_log_item.id = import_log_item_detail.parent "
                + "and import_log_item.parent = ? order by import_log_item_detail.id";
        ps = connection.prepareStatement(query);
        ps.setInt(1, id);
        rs = ps.executeQuery();

        final Iterator<ImportLogItem> iterator = importLogItems.iterator();

        if (importLogItems.size() > 0) {

          ImportLogItem currentImportLogItem = iterator.next();
          List<ImportLogItemDetail> importLogItemDetails = new ArrayList<ImportLogItemDetail>();
          currentImportLogItem.setItems(importLogItemDetails);

          while (rs.next()) {
            final int itemId = rs.getInt("parent");
            while (currentImportLogItem.getId() != itemId && iterator.hasNext()) {
              currentImportLogItem = iterator.next();
              importLogItemDetails = new ArrayList<ImportLogItemDetail>();
              currentImportLogItem.setItems(importLogItemDetails);
            }

            final ImportLogItemDetail importLogItemDetail =
                ImportLog.fillImportLogItemDetail(rs, currentImportLogItem, connection);
            importLogItemDetails.add(importLogItemDetail);
          }
        }
      }
    } catch (final Exception e) {
      throw new RuntimeException("Error getting detail", e);
    } finally {
      DbTools.closeResultSet(rs);
      DbTools.closePreparedStatement(ps);
    }

    return importLog;
  }

  /**
   * @param resultSet
   * @param importLogItem
   * @return
   * @throws SQLException
   */
  private static ImportLogItemDetail fillImportLogItemDetail(ResultSet resultSet,
      ImportLogItem importLogItem, Connection connection) throws SQLException {
    final ImportLogItemDetail importLogItemDetail =
        new ImportLogItemDetail(importLogItem, connection);

    importLogItemDetail.setErrorLevel(ErrorLevel.valueOf(resultSet.getString("errorlevel")
        .toUpperCase()));
    importLogItemDetail.setStartDate(resultSet.getTimestamp("startdate"));
    importLogItemDetail.setStatus(Status.valueOf(resultSet.getString("status")));
    importLogItemDetail.setMessage(resultSet.getString("message"));

    return importLogItemDetail;
  }

  /**
   * @param resultSet
   * @param importLog
   * @return
   * @throws SQLException
   */
  private static ImportLogItem fillImportLogItem(ResultSet resultSet, ImportLog importLog,
      Connection connection) throws SQLException {
    final ImportLogItem importLogItem = new ImportLogItem(importLog, connection);

    importLogItem.setEndDate(resultSet.getTimestamp("enddate"));
    importLogItem
        .setErrorLevel(ErrorLevel.valueOf(resultSet.getString("errorlevel").toUpperCase()));
    importLogItem.setStartDate(resultSet.getTimestamp("startdate"));
    importLogItem.setStatus(Status.valueOf(resultSet.getString("status")));
    importLogItem.setId(resultSet.getInt("id"));
    importLogItem.setItemId(resultSet.getString("item_id"));
    importLogItem.setMessage(resultSet.getString("message"));

    return importLogItem;
  }

  /**
   * @param resultSet SQL result set
   * @return The filled import
   * @throws SQLException
   */
  private static ImportLog fillImportLog(ResultSet resultSet, Connection connection)
      throws SQLException {
    final ImportLog importLog = new ImportLog();

    importLog.setEndDate(resultSet.getTimestamp("enddate"));
    importLog.setErrorLevel(ErrorLevel.valueOf(resultSet.getString("errorlevel").toUpperCase()));
    importLog.setFormat(TransformerFactory.FORMAT.valueOf(resultSet.getString("format")));
    importLog.setStartDate(resultSet.getTimestamp("startdate"));
    importLog.setStatus(Status.valueOf(resultSet.getString("status")));
    importLog.setId(resultSet.getInt("id"));
    importLog.setContext(resultSet.getString("context"));
    importLog.setUser(resultSet.getString("userid"));
    importLog.setMessage(resultSet.getString("name"));
    importLog.percentage = resultSet.getInt("percentage");

    return importLog;
  }

  /**
   * Get the details of a certain import item.
   * 
   * @param id The item id
   * @param userid The users id
   * 
   * @return A list of details
   */
  public static List<ImportLogItemDetail> loadImportLogItemDetails(int id, String userid,
      Connection connection) {
    final List<ImportLogItemDetail> importLogItemDetails = new ArrayList<ImportLogItemDetail>();

    final String query =
        "select import_log_item_detail.* "
            + "from import_log_item, import_log_item_detail, import_log "
            + "where import_log_item.id = import_log_item_detail.parent "
            + "and import_log_item.parent = import_log.id and import_log_item.id = ? "
            + "and import_log.userid = ? order by import_log_item_detail.id";

    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ps = connection.prepareStatement(query);
      ps.setInt(1, id);
      ps.setString(2, userid);

      rs = ps.executeQuery();

      while (rs.next()) {
        final ImportLogItemDetail importLogItemDetail =
            ImportLog.fillImportLogItemDetail(rs, (ImportLogItem) null, connection);
        importLogItemDetails.add(importLogItemDetail);
      }

      return importLogItemDetails;
    } catch (final Exception e) {
      throw new RuntimeException(e);
    } finally {
      DbTools.closeResultSet(rs);
      DbTools.closePreparedStatement(ps);
    }
  }

  @Override
  public String toString() {
    final StringWriter writer = new StringWriter();

    writer.write(this.getErrorLevel().toString());
    writer.write(": ");
    writer.write(" (");
    writer.write(ImportLog.DATE_FORMAT.format(this.getStartDate()));
    writer.write(" - ");
    if (this.getEndDate() != null) {
      writer.write(ImportLog.DATE_FORMAT.format(this.getEndDate()));
    }
    writer.write(") - ");
    writer.write(this.getStatus().toString());
    writer.write("\n");

    for (final ImportLogItem item : this.getItems()) {
      writer.write(item.toString().replaceAll("(.*)\n", "\t$1\n"));
    }

    return writer.toString();
  }

  /**
   * Puts the import's focus on this item.
   * 
   * @param item The item to be activated
   */
  public void activateItem(ImportLogItem item) {
    if (this.currentImportLogItem == null) {
      this.currentImportLogItem = item;
    } else {
      throw new RuntimeException(
          "Trying to start logging an item while another is not yet finished");
    }
  }

  // /**
  // * @return An XML representation of this import. Used to store it in the repository.
  // */
  // public void toXML(Writer writer) throws Exception {
  // writer.write("<import-task ");
  // writer.write("status=\"");
  // writer.write(this.status.toString());
  // writer.write("\" error-level=\"");
  // writer.write(this.errorLevel.toString());
  // writer.write("\" created-by=\"");
  // writer.write(this.user);
  // writer.write("\">\n");
  //
  // writer.write("\t<name>");
  // writer.write(this.escape(this.message));
  // writer.write("</name>\n");
  //
  // writer.write("\t<context>");
  // writer.write(this.context);
  // writer.write("</context>\n");
  //
  // writer.write("\t<start-date>");
  // writer.write(this.getStartDateFormatted());
  // writer.write("</start-date>\n");
  //
  // if (this.endDate != null) {
  // writer.write("\t<end-date>");
  // writer.write(this.getEndDateFormatted());
  // writer.write("</end-date>\n");
  // }
  //
  // writer.write("\t<format>");
  // writer.write(this.format.name());
  // writer.write("</format>\n");
  //
  // writer.write("\t<items>\n");
  // for (final ImportLogItem item : this.items) {
  // item.toXML(writer);// .replaceAll("(.*\\n)", "\t\t$1"));
  // }
  // writer.write("\t</items>\n");
  // writer.write("</import-task>\n");
  // }

  // /**
  // * An XML-safe representation of the given string.
  // *
  // * @param string The given string
  // * @return the escaped string
  // */
  // protected String escape(String string) {
  // if (string == null) {
  // return null;
  // }
  //
  // return string.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;");
  // }

  /**
   * JSF action to remove an import from the database.
   * 
   * @return Always null.
   */
  public void remove() {
    Connection connection = null;
    PreparedStatement ps = null;

    try {
      connection = DbTools.getNewConnection();

      String query =
          "delete from import_log_item_detail where parent in "
              + "(select id from import_log_item where parent = ?)";

      ps = connection.prepareStatement(query);
      ps.setInt(1, this.id);
      ps.executeUpdate();

      DbTools.closePreparedStatement(ps);

      query = "delete from import_log_item where parent  = ?";

      ps = connection.prepareStatement(query);
      ps.setInt(1, this.id);
      ps.executeUpdate();

      DbTools.closePreparedStatement(ps);

      query = "delete from import_log where id  = ?";

      ps = connection.prepareStatement(query);
      ps.setInt(1, this.id);
      ps.executeUpdate();
    } catch (final Exception e) {
      throw new RuntimeException(e);
    } finally {
      DbTools.closePreparedStatement(ps);
      DbTools.closeConnection(connection);
    }

    try {
      FacesTools.getExternalContext().redirect("ImportWorkspace.jsp");
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * JSF action to delete all items of an import from the repository.
   * 
   * @return Always null.
   */
  public void deleteAll() {
    final String authenticationToken =
        ((LoginHelper) FacesTools.findBean("LoginHelper")).getAuthenticationToken();

    final Connection connection = DbTools.getNewConnection();
    final DeleteProcess deleteProcess;
    try {
      deleteProcess = new DeleteProcess(this, authenticationToken, connection);
      deleteProcess.start();
    } catch (final Exception e) {
      DbTools.closeConnection(connection);
      throw e;
    }

    try {
      FacesTools.getExternalContext().redirect("ImportWorkspace.jsp");
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * JSF action to submit/release all items of an import from the repository.
   * 
   * @return Always null.
   */
  public void submitAll() {
    final String authenticationToken =
        ((LoginHelper) FacesTools.findBean("LoginHelper")).getAuthenticationToken();

    final Connection connection = DbTools.getNewConnection();
    final SubmitProcess submitProcess;
    try {
      submitProcess = new SubmitProcess(this, false, authenticationToken, connection);
      submitProcess.start();
    } catch (final Exception e) {
      DbTools.closeConnection(connection);
      throw e;
    }

    try {
      FacesTools.getExternalContext().redirect("ImportWorkspace.jsp");
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * JSF action to submit/release all items of an import from the repository.
   * 
   * @return Always null.
   */
  public void submitAndReleaseAll() {
    final String authenticationToken =
        ((LoginHelper) FacesTools.findBean("LoginHelper")).getAuthenticationToken();

    final Connection connection = DbTools.getNewConnection();
    final SubmitProcess submitProcess;
    try {
      submitProcess = new SubmitProcess(this, true, authenticationToken, connection);
      submitProcess.start();
    } catch (final Exception e) {
      DbTools.closeConnection(connection);
      throw e;
    }

    try {
      FacesTools.getExternalContext().redirect("ImportWorkspace.jsp");
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * @return A link to a JSP page showing only this import (no items)
   */
  public String getLogLink() {
    return "ImportLog.jsp?id=" + this.getId();
  }

  /**
   * @return A link to the MyItems page filtering for this import
   */
  public String getMyItemsLink() {
    try {
      return "DepositorWSPage.jsp?import="
          + URLEncoder.encode(this.getMessage() + " " + this.getStartDateFormatted(), "ISO-8859-1");
    } catch (final UnsupportedEncodingException usee) {
      throw new RuntimeException(usee);
    }
  }

  /**
   * @return A link to a JSP page showing the items of this import (no details)
   */
  public String getItemsLink() {
    return "ImportLogItems.jsp?id=" + this.getId();
  }

  /**
   * Dummy setter to avoid JSF warnings.
   * 
   * @param link The link
   */
  public void setItemsLink(String link) {}

  /**
   * Dummy setter to avoid JSF warnings.
   * 
   * @param link The link
   */
  public void setLogLink(String link) {}

  private Workflow getWorkflow() {
    if (this.workflow == null) {
      try {
        final ContextVO contextVO =
            ApplicationBean.INSTANCE.getContextService().get(this.context, null);

        this.workflow = contextVO.getAdminDescriptor().getWorkflow();
      } catch (final Exception e) {
        throw new RuntimeException(e);
      }
    }

    return this.workflow;
  }

  public boolean getSimpleWorkflow() {
    return (this.getWorkflow() == Workflow.SIMPLE);
  }
}
