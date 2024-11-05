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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
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
public class ImportLog extends BaseImportLog {
  private static ImportLog fillImportLog(ResultSet resultSet) throws SQLException {
    ImportLog importLog = new ImportLog();

    importLog.endDate = resultSet.getTimestamp("enddate");
    importLog.errorLevel = BaseImportLog.ErrorLevel.valueOf(resultSet.getString("errorlevel").toUpperCase());
    importLog.format = TransformerFactory.FORMAT.valueOf(resultSet.getString("format"));
    importLog.startDate = resultSet.getTimestamp("startdate");
    importLog.status = BaseImportLog.Status.valueOf(resultSet.getString("status"));
    importLog.id = resultSet.getInt("id");
    importLog.context = resultSet.getString("context");
    importLog.user = resultSet.getString("userid");
    importLog.message = resultSet.getString("name");
    importLog.percentage = resultSet.getInt("percentage");

    return importLog;
  }

  private static ImportLogItem fillImportLogItem(ResultSet resultSet, ImportLog importLog) throws SQLException {
    ImportLogItem importLogItem = new ImportLogItem(importLog);

    importLogItem.setEndDate(resultSet.getTimestamp("enddate"));
    importLogItem.setErrorLevel(BaseImportLog.ErrorLevel.valueOf(resultSet.getString("errorlevel").toUpperCase()));
    importLogItem.setStartDate(resultSet.getTimestamp("startdate"));
    importLogItem.setStatus(BaseImportLog.Status.valueOf(resultSet.getString("status")));
    importLogItem.setId(resultSet.getInt("id"));
    importLogItem.setItemId(resultSet.getString("item_id"));
    importLogItem.setMessage(resultSet.getString("message"));

    return importLogItem;
  }

  private static ImportLogItemDetail fillImportLogItemDetail(ResultSet resultSet, ImportLogItem importLogItem) throws SQLException {
    ImportLogItemDetail importLogItemDetail = new ImportLogItemDetail(importLogItem);

    importLogItemDetail.setErrorLevel(BaseImportLog.ErrorLevel.valueOf(resultSet.getString("errorlevel").toUpperCase()));
    importLogItemDetail.setStartDate(resultSet.getTimestamp("startdate"));
    importLogItemDetail.setStatus(BaseImportLog.Status.valueOf(resultSet.getString("status")));
    importLogItemDetail.setMessage(resultSet.getString("message"));

    return importLogItemDetail;
  }

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
        importLog = ImportLog.fillImportLog(rs);
      }

      // DbTools.closePreparedStatement(ps);

      query = "select * from import_log_item where parent = ? order by id";
      ps = connection.prepareStatement(query);
      ps.setInt(1, id);
      rs = ps.executeQuery();

      List<ImportLogItem> importLogItems = new ArrayList<>();

      while (rs.next()) {
        ImportLogItem importLogItem = ImportLog.fillImportLogItem(rs, importLog);
        importLogItems.add(importLogItem);
      }

      importLog.importLogItems = importLogItems;

      // DbTools.closePreparedStatement(ps);

      if (loadDetails) {
        query = "select import_log_item_detail.* from import_log_item, import_log_item_detail "
            + "where import_log_item.id = import_log_item_detail.parent "
            + "and import_log_item.parent = ? order by import_log_item_detail.id";
        ps = connection.prepareStatement(query);
        ps.setInt(1, id);
        rs = ps.executeQuery();

        Iterator<ImportLogItem> iterator = importLogItems.iterator();

        if (!importLogItems.isEmpty()) {

          ImportLogItem currentImportLogItem = iterator.next();
          List<ImportLogItemDetail> importLogItemDetails = new ArrayList<>();
          currentImportLogItem.setItems(importLogItemDetails);

          while (rs.next()) {
            int itemId = rs.getInt("parent");
            while (currentImportLogItem.getId() != itemId && iterator.hasNext()) {
              currentImportLogItem = iterator.next();
              importLogItemDetails = new ArrayList<>();
              currentImportLogItem.setItems(importLogItemDetails);
            }

            ImportLogItemDetail importLogItemDetail = ImportLog.fillImportLogItemDetail(rs, currentImportLogItem);
            importLogItemDetails.add(importLogItemDetail);
          }
        }
      }
    } catch (Exception e) {
      throw new RuntimeException("Error getting detail", e);
    }

    return importLog;
  }

  public static List<ImportLogItemDetail> getImportLogItemDetails(int id, String userid, Connection connection) {
    List<ImportLogItemDetail> importLogItemDetails = new ArrayList<>();

    final String query = "select import_log_item_detail.* " + "from import_log_item, import_log_item_detail, import_log "
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
        ImportLogItemDetail importLogItemDetail = ImportLog.fillImportLogItemDetail(rs, null);
        importLogItemDetails.add(importLogItemDetail);
      }

      return importLogItemDetails;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static List<ImportLog> getImportLogs(AccountUserDbVO user, ImportWorkspace.SortColumn sortBy, ImportWorkspace.SortDirection dir,
      boolean loadDetails, Connection connection) {
    List<ImportLog> result = new ArrayList<>();
    PreparedStatement ps = null;
    ResultSet rs = null;

    String query = "select id from import_log where userid = ? order by " + sortBy.toSQL() + " " + dir.toSQL();

    try {
      ps = connection.prepareStatement(query);
      ps.setString(1, user.getObjectId());

      rs = ps.executeQuery();

      while (rs.next()) {
        int id = rs.getInt("id");
        ImportLog log = ImportLog.getImportLog(id, loadDetails, connection);
        result.add(log);
      }
    } catch (Exception e) {
      throw new RuntimeException("Error getting log", e);
    }

    return result;
  }

  public static List<ImportLog> getImportLogs(AccountUserDbVO user, ImportWorkspace.SortColumn sortBy, ImportWorkspace.SortDirection dir,
      Connection connection) {

    return ImportLog.getImportLogs(user, sortBy, dir, true, connection);
  }

  private String context;
  private ImportLogItem currentImportLogItem = null;
  private TransformerFactory.FORMAT format;
  private List<ImportLogItem> importLogItems = new ArrayList<>();
  private int percentage;
  private String user;
  private ContextDbVO.Workflow workflow;

  protected ImportLog() {}

  public ImportLog(String user, TransformerFactory.FORMAT format, Connection connection) {
    this.startDate = new Date();
    this.status = BaseImportLog.Status.PENDING;
    this.errorLevel = BaseImportLog.ErrorLevel.FINE;
    this.user = user;
    this.format = format;

    this.saveImportLog(connection);
  }

  /**
   * Puts the import's focus on this item.
   *
   * @param item The item to be activated
   */
  public void activateItem(ImportLogItem item) {
    if (null == this.currentImportLogItem) {
      this.currentImportLogItem = item;
    } else {
      throw new RuntimeException("Trying to start logging an item while another is not yet finished");
    }
  }

  /**
   * Adds a detail to the focused item using the given error level and a previously caught
   * exception. Start- and end-date are set to the current date. Status is set to FINISHED. The
   * exception is transformed into a stack trace.
   *
   * @param errLevel The error level of this item
   * @param exception The exception that should be added to the item
   */
  public void addDetail(BaseImportLog.ErrorLevel errLevel, Exception exception, Connection connection) {
    String msg = this.getExceptionMessage(exception);
    this.addDetail(errLevel, msg, null, connection);
  }

  /**
   * Adds a detail to the focused item using the given error level and message key. Start- and
   * end-date are set to the current date. Status is set to FINISHED.
   * <p>
   * Defaults: - The detail id will be set to null
   *
   * @param errLevel The error level of this item
   * @param msg A message key for a localized message
   */
  public void addDetail(BaseImportLog.ErrorLevel errLevel, String msg, Connection connection) {
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
  public void addDetail(BaseImportLog.ErrorLevel errLevel, String msg, String detailId, Connection connection) {
    if (null == this.currentImportLogItem) {
      throw new RuntimeException("Trying to add a detail but no log item is started.");
    }

    ImportLogItemDetail importLogItemDetail = new ImportLogItemDetail(this.currentImportLogItem);
    importLogItemDetail.setErrorLevel(errLevel, connection);
    importLogItemDetail.setMessage(msg);
    importLogItemDetail.setStartDate(new Date());
    importLogItemDetail.setStatus(BaseImportLog.Status.FINISHED);

    //    if (null == this.currentImportLogItem) {
    //      this.startItem("", connection);
    //    }

    this.currentImportLogItem.getItems().add(importLogItemDetail);

    this.saveImportLogItemDetail(importLogItemDetail, connection);
  }

  public void close(Connection connection) {
    this.endDate = new Date();
    this.status = BaseImportLog.Status.FINISHED;
    this.percentage = BaseImportLog.PERCENTAGE_COMPLETED;

    this.updateImportLog(connection);
  }

  /**
   * JSF action to delete all items of an import from the repository.
   *
   * @return Always null.
   */
  public void deleteAll() {
    String authenticationToken = ((LoginHelper) FacesTools.findBean("LoginHelper")).getAuthenticationToken();

    Connection connection = DbTools.getNewConnection();
    DeleteProcess deleteProcess;
    try {
      deleteProcess = new DeleteProcess(this, authenticationToken, connection);
      deleteProcess.start();
    } catch (Exception e) {
      DbTools.closeConnection(connection);
      throw e;
    }

    try {
      FacesTools.getExternalContext().redirect("ImportWorkspace.jsp");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Sets the status of the focused item to FINISHED and the end date to the current date, then
   * removes the focus of the import.
   */
  public void finishItem(Connection connection) {
    if (null != this.currentImportLogItem) {
      this.currentImportLogItem.setEndDate(new Date());
      this.currentImportLogItem.setStatus(BaseImportLog.Status.FINISHED);

      this.updateImportLogItem(this.currentImportLogItem, connection);

      this.currentImportLogItem = null;
    }
  }

  public String getContext() {
    return this.context;
  }

  public ImportLogItem getCurrentItem() {
    return this.currentImportLogItem;
  }

  /**
   * Transforms an exception into a Java stack trace.
   *
   * @param exception The exception
   * @return The stack trace
   */
  private String getExceptionMessage(Throwable exception) {
    StringWriter stringWriter = new StringWriter();
    stringWriter.write(exception.getClass().getSimpleName());

    if (null != exception.getMessage()) {
      stringWriter.write(": ");
      stringWriter.write(exception.getMessage());
    }
    stringWriter.write("\n");

    StackTraceElement[] stackTraceElements = exception.getStackTrace();
    stringWriter.write("\tat ");
    stringWriter.write(stackTraceElements[0].getClassName());
    stringWriter.write(".");
    stringWriter.write(stackTraceElements[0].getMethodName());
    stringWriter.write("(");
    stringWriter.write(stackTraceElements[0].getFileName());
    stringWriter.write(":");
    stringWriter.write(stackTraceElements[0].getLineNumber() + "");
    stringWriter.write(")\n");

    if (null != exception.getCause()) {
      stringWriter.write(this.getExceptionMessage(exception.getCause()));
    }

    return stringWriter.toString();
  }

  public boolean getFinished() {
    return (Status.FINISHED == this.status);
  }

  public TransformerFactory.FORMAT getFormat() {
    return this.format;
  }

  public boolean getImportedItems() {
    for (ImportLogItem item : this.importLogItems) {
      if (null != item.getItemId()) {
        return true;
      }
    }

    return false;
  }

  public List<ImportLogItem> getItems() {
    return this.importLogItems;
  }

  /**
   * @return A link to a JSP page showing the items of this import (no details)
   */
  public String getItemsLink() {
    return "ImportLogItems.jsp?id=" + this.getId();
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
    return "DepositorWSPage.jsp?import="
        + URLEncoder.encode(this.getMessage() + " " + this.getStartDateFormatted(), StandardCharsets.ISO_8859_1);
  }

  public int getPercentage() {
    return this.percentage;
  }

  public boolean getSimpleWorkflow() {
    return (ContextDbVO.Workflow.SIMPLE == this.getWorkflow());
  }

  public boolean getStandardWorkflow() {
    return (ContextDbVO.Workflow.STANDARD == this.getWorkflow());
  }

  public String getUser() {
    return this.user;
  }

  private ContextDbVO.Workflow getWorkflow() {
    if (null == this.workflow) {
      try {
        ContextDbVO contextVO = ApplicationBean.INSTANCE.getContextService().get(this.context, null);

        this.workflow = contextVO.getWorkflow();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    return this.workflow;
  }

  public boolean isDone() {
    return (Status.FINISHED == this.status);
  }

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

      String query = "delete from import_log_item_detail where parent in " + "(select id from import_log_item where parent = ?)";

      ps = connection.prepareStatement(query);
      ps.setInt(1, this.id);
      ps.executeUpdate();

      // DbTools.closePreparedStatement(ps);

      query = "delete from import_log_item where parent  = ?";

      ps = connection.prepareStatement(query);
      ps.setInt(1, this.id);
      ps.executeUpdate();

      // DbTools.closePreparedStatement(ps);

      query = "delete from import_log where id  = ?";

      ps = connection.prepareStatement(query);
      ps.setInt(1, this.id);
      ps.executeUpdate();
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      // DbTools.closePreparedStatement(ps);
      DbTools.closeConnection(connection);
    }

    try {
      FacesTools.getExternalContext().redirect("ImportWorkspace.jsp");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void reopen(Connection connection) {
    this.endDate = null;
    this.status = BaseImportLog.Status.PENDING;

    this.updateImportLog(connection);
  }

  private synchronized void saveImportLog(Connection connection) {
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ps = connection.prepareStatement(
          "insert into import_log "
              + "(status, errorlevel, startdate, userid, name, context, format, percentage) values (?, ?, ?, ?, ?, ?, ?, 0) returning id",
          Statement.RETURN_GENERATED_KEYS);

      ps.setString(1, this.status.toString());
      ps.setString(2, this.errorLevel.toString());
      ps.setTimestamp(3, new Timestamp(this.startDate.getTime()));
      ps.setString(4, this.user);
      ps.setString(5, this.message);
      ps.setString(6, this.context);
      ps.setString(7, this.format.name());

      ps.executeUpdate();
      // DbTools.closePreparedStatement(ps);

      rs = ps.getGeneratedKeys();
      if (rs.next()) {
        this.id = rs.getInt(1);
      } else {
        throw new RuntimeException("Error saving import_log");
      }
    } catch (Exception e) {
      throw new RuntimeException("Error saving import_log", e);
    }
  }

  private synchronized void saveImportLogItem(ImportLogItem importLogItem, Connection connection) {
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ps = connection.prepareStatement(
          "insert into import_log_item (status, errorlevel, startdate, parent, message, item_id) values (?, ?, ?, ?, ?, ?) returning id",
          Statement.RETURN_GENERATED_KEYS);

      ps.setString(1, importLogItem.getStatus().toString());
      ps.setString(2, importLogItem.getErrorLevel().toString());
      ps.setTimestamp(3, new Timestamp(importLogItem.getStartDate().getTime()));
      ps.setInt(4, this.id);
      ps.setString(5, importLogItem.getMessage());
      ps.setString(6, importLogItem.getItemId());

      ps.executeUpdate();
      // DbTools.closePreparedStatement(ps);

      rs = ps.getGeneratedKeys();
      if (rs.next()) {
        importLogItem.setId(rs.getInt(1));
      } else {
        throw new RuntimeException("Error saving import_log_item");
      }
    } catch (Exception e) {
      throw new RuntimeException("Error saving import_log_item", e);
    }
  }

  private synchronized void saveImportLogItemDetail(ImportLogItemDetail importLogItemDetail, Connection connection) {
    PreparedStatement ps = null;
    //    final ResultSet rs = null;

    try {
      ps = connection
          .prepareStatement("insert into import_log_item_detail (status, errorlevel, startdate, parent, message) values (?, ?, ?, ?, ?)");

      ps.setString(1, importLogItemDetail.getStatus().toString());
      ps.setString(2, importLogItemDetail.getErrorLevel().toString());
      ps.setTimestamp(3, new Timestamp(importLogItemDetail.getStartDate().getTime()));
      ps.setInt(4, importLogItemDetail.getParent().getId());
      ps.setString(5, importLogItemDetail.getMessage());

      ps.executeUpdate();
    } catch (Exception e) {
      throw new RuntimeException("Error saving log_item_detail", e);
    }
  }

  public void setContext(String context) {
    this.context = context;
  }

  public void setErrorLevel(BaseImportLog.ErrorLevel errorLevel, Connection connection) {
    super.setErrorLevel(errorLevel);

    if (null != connection) {
      this.updateImportLog(connection);
    }
  }

  public void setFormat(TransformerFactory.FORMAT format) {
    this.format = format;
  }

  public void setItemId(String id, Connection connection) {
    this.currentImportLogItem.setItemId(id);
    this.updateImportLogItem(this.currentImportLogItem, connection);
  }

  public void setItems(List<ImportLogItem> items) {
    this.importLogItems = items;
  }

  /**
   * Dummy setter to avoid JSF warnings.
   *
   * @param link The link
   */
  public void setItemsLink(String link) {}

  /**
   * @param itemVO Assigns a value object to the focused item.
   */
  public void setItemVO(ItemVersionVO itemVO) {
    this.currentImportLogItem.setItemVO(itemVO);
  }

  /**
   * Dummy setter to avoid JSF warnings.
   *
   * @param link The link
   */
  public void setLogLink(String link) {}

  public void setPercentage(int percentage, Connection connection) {
    this.percentage = percentage;

    if (null != connection) {
      this.updateImportLog(connection);
    }
  }

  public void setUser(String user) {
    this.user = user;
  }

  /**
   * Creates a new item using the given error level and message, then putting the focus of the
   * import on it.
   * <p>
   * Defaults: - Item id will be set to null - Start date will be set to the current date
   *
   * @param errLevel The initial error level of this item
   * @param msg A message key for a localized message
   */
  public void startItem(BaseImportLog.ErrorLevel errLevel, String msg, Connection connection) {
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
  private void startItem(BaseImportLog.ErrorLevel errLevel, String msg, Date sDate, String itemId, Connection connection) {
    if (null != this.currentImportLogItem) {
      throw new RuntimeException("Trying to start logging an item while another is not yet finished");
    }

    ImportLogItem newItem = new ImportLogItem(this);

    newItem.setErrorLevel(errLevel, connection);
    newItem.setMessage(msg);
    newItem.setStartDate(sDate);

    this.saveImportLogItem(newItem, connection);

    this.importLogItems.add(newItem);

    this.currentImportLogItem = newItem;
  }

  /**
   * Creates a new item using the given message.
   * <p>
   * Defaults: - Item id will be set to null - Start date will be set to the current date - Error
   * level will be set to FINE.
   *
   * @param msg A message key for a localized message
   */
  public void startItem(String msg, Connection connection) {
    this.startItem(msg, null, connection);
  }

  /**
   * Creates a new item using the given message, item id and start date, then putting the focus of
   * the import on it.
   * <p>
   * Defaults: - Error level will be set to FINE.
   *
   * @param msg A message key for a localized message
   * @param sDate The start date of this item
   * @param itemId The eSciDoc id of the imported item
   */
  private void startItem(String msg, Date sDate, String itemId, Connection connection) {
    this.startItem(BaseImportLog.ErrorLevel.FINE, msg, sDate, itemId, connection);
  }

  /**
   * Creates a new item using the given message and item id, then putting the focus of the import on
   * it.
   * <p>
   * Defaults: - Start date will be set to the current date - Error level will be set to FINE.
   *
   * @param msg A message key for a localized message
   * @param itemId The eSciDoc id of the imported item
   */
  private void startItem(String msg, String itemId, Connection connection) {
    this.startItem(msg, new Date(), itemId, connection);
  }

  /**
   * JSF action to submit all items of an import from the repository.
   *
   * @return Always null.
   */
  public void submitAll() {
    String authenticationToken = ((LoginHelper) FacesTools.findBean("LoginHelper")).getAuthenticationToken();

    Connection connection = DbTools.getNewConnection();
    SubmitProcess submitProcess;
    try {
      submitProcess = new SubmitProcess(this, SubmitProcess.Modus.SUBMIT, authenticationToken, connection);
      submitProcess.start();
    } catch (Exception e) {
      DbTools.closeConnection(connection);
      throw e;
    }

    try {
      FacesTools.getExternalContext().redirect("ImportWorkspace.jsp");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * JSF action to submit/release all items of an import from the repository.
   *
   * @return Always null.
   */
  public void submitAndReleaseAll() {
    String authenticationToken = ((LoginHelper) FacesTools.findBean("LoginHelper")).getAuthenticationToken();

    Connection connection = DbTools.getNewConnection();
    SubmitProcess submitProcess;
    try {
      submitProcess = new SubmitProcess(this, SubmitProcess.Modus.SUBMIT_AND_RELEASE, authenticationToken, connection);
      submitProcess.start();
    } catch (Exception e) {
      DbTools.closeConnection(connection);
      throw e;
    }

    try {
      FacesTools.getExternalContext().redirect("ImportWorkspace.jsp");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * JSF action to release all items of an import from the repository.
   *
   * @return Always null.
   */
  public void releaseAll() {
    String authenticationToken = ((LoginHelper) FacesTools.findBean("LoginHelper")).getAuthenticationToken();

    Connection connection = DbTools.getNewConnection();
    SubmitProcess submitProcess;
    try {
      submitProcess = new SubmitProcess(this, SubmitProcess.Modus.RELEASE, authenticationToken, connection);
      submitProcess.start();
    } catch (Exception e) {
      DbTools.closeConnection(connection);
      throw e;
    }

    try {
      FacesTools.getExternalContext().redirect("ImportWorkspace.jsp");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Sets the status of the focused item to SUSPENDED. This should be done when it is planned to
   * visit this item again later. I.e. in a first step, all items are transformed and validated,
   * then suspended. In a second step, all items are imported into the repository.
   */
  public void suspendItem(Connection connection) {
    if (null != this.currentImportLogItem) {
      this.currentImportLogItem.setStatus(BaseImportLog.Status.SUSPENDED);

      this.updateImportLogItem(this.currentImportLogItem, connection);

      this.currentImportLogItem = null;
    }
  }

  @Override
  public String toString() {
    StringWriter writer = new StringWriter();

    writer.write(this.getErrorLevel().toString());
    writer.write(": ");
    writer.write(" (");
    writer.write(BaseImportLog.DATE_FORMAT.format(this.getStartDate()));
    writer.write(" - ");
    if (null != this.getEndDate()) {
      writer.write(BaseImportLog.DATE_FORMAT.format(this.getEndDate()));
    }
    writer.write(") - ");
    writer.write(this.getStatus().toString());
    writer.write("\n");

    for (ImportLogItem item : this.importLogItems) {
      writer.write(item.toString().replaceAll("(.*)\n", "\t$1\n"));
    }

    return writer.toString();
  }

  private synchronized void updateImportLog(Connection connection) {
    PreparedStatement ps = null;

    try {
      ps = connection.prepareStatement("update import_log set status = ?, errorlevel = ?, "
          + "startdate = ?, enddate = ?, userid = ?, name = ?, " + "context = ?, format = ?, percentage = ? where id = ?");

      ps.setString(1, this.status.toString());
      ps.setString(2, this.errorLevel.toString());
      ps.setTimestamp(3, new Timestamp(this.startDate.getTime()));

      if (null != this.endDate) {
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
    } catch (Exception e) {
      throw new RuntimeException("Error updating import_log", e);
    }
  }

  private synchronized void updateImportLogItem(ImportLogItem importLogItem, Connection connection) {
    PreparedStatement ps = null;

    try {
      ps = connection.prepareStatement("update import_log_item set status = ?, "
          + "errorlevel = ?, startdate = ?, enddate = ?, parent = ?, " + "message = ?, item_id = ? where id = ?");

      ps.setString(1, importLogItem.getStatus().toString());
      ps.setString(2, importLogItem.getErrorLevel().toString());
      ps.setTimestamp(3, new Timestamp(importLogItem.getStartDate().getTime()));

      if (null != importLogItem.getEndDate()) {
        ps.setTimestamp(4, new Timestamp(importLogItem.getEndDate().getTime()));
      } else {
        ps.setTimestamp(4, null);
      }

      ps.setInt(5, this.id);
      ps.setString(6, importLogItem.getMessage());
      ps.setString(7, importLogItem.getItemId());
      ps.setInt(8, importLogItem.getId());

      ps.executeUpdate();
    } catch (Exception e) {
      throw new RuntimeException("Error updating import_log_item", e);
    }
  }
}
