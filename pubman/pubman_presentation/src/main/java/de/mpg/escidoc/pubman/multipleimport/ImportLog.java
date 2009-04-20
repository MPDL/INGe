/*
*
* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.de/license.
* See the License for the specific language governing permissions
* and limitations under the License.
*
* When distributing Covered Code, include this CDDL HEADER in each
* file and include the License file at license/ESCIDOC.LICENSE.
* If applicable, add the following below this CDDL HEADER, with the
* fields enclosed by brackets "[]" replaced with your own identifying
* information: Portions Copyright [yyyy] [name of copyright owner]
*
* CDDL HEADER END
*/

/*
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman.multipleimport;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;

import de.escidoc.www.services.om.ContextHandler;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.ContextVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PublicationAdminDescriptorVO.Workflow;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Class that describes an import process.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class ImportLog
{
    /**
     * enum to describe the general state of the log.
     *
     * @author franke (initial creation)
     * @author $Author$ (last modification)
     * @version $Revision$ $LastChangedDate$
     *
     */
    public enum Status
    {
        PENDING, SUSPENDED, FINISHED, ROLLBACK
    }
    
    /**
     * enum to describe if something went wrong with this element.
     * 
     * - FINE:      everything is alright
     * - WARNING:   import worked, but something could have been done better
     * - PROBLEM:   some item was not imported because validation failed
     * - ERROR:     some items were not imported because there were system errors during the import
     * - FATAL:     the import was interrupted completely due to system errors
     *
     * @author franke (initial creation)
     * @author $Author$ (last modification)
     * @version $Revision$ $LastChangedDate$
     *
     */
    public enum ErrorLevel
    {
        FINE, WARNING, PROBLEM, ERROR, FATAL
    }
    
    public enum SortColumn
    {
        STARTDATE, ENDDATE, NAME, STATUS, ERRORLEVEL;
        
        public String toString()
        {
            return super.toString().toLowerCase();
        }
    }
    
    public enum SortDirection
    {
        ASCENDING, DESCENDING;
        
        public String toString()
        {
            String value = super.toString();
            return value.substring(0, value.indexOf("E")).toLowerCase();
        }
    }
    
    private static final Logger logger = Logger.getLogger(ImportLog.class);
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm");
    
    private Date startDate;
    private Date endDate;
    
    private Status status;
    private ErrorLevel errorLevel;
    
    private int percentage;
    
    private int storedId;
    private Connection connection;
    
    private String format;
    private String action;
    private String user;
    private String userHandle;
    private String message;
    private String context;
    
    private Workflow workflow;
    
    private List<ImportLogItem> items = new ArrayList<ImportLogItem>();

    private ImportLogItem currentItem = null;
    
    /**
     * Implicit constructor for inheriting classes.
     */
    protected ImportLog()
    {
        
    }
    
    /**
     * Constructor.
     * 
     * @param action A string indicating what action is logged by this.
     * - "import"
     * - "delete"
     * - "submit"
     * - "release"
     * TODO: Put this into an enum
     * 
     * @param user The eSciDoc user id of the user that invoces this action.
     */
    public ImportLog(String action, String user, String format)
    {
        this.startDate = new Date();
        this.status = Status.PENDING;
        this.errorLevel = ErrorLevel.FINE;
        this.user = user;
        this.format = format;
        this.action = action;
        
        this.connection = getConnection();
        
        saveLog();
    }

    /**
     * @throws RuntimeException
     */
    private static Connection getConnection()
    {
        try
        {
            Class.forName(PropertyReader.getProperty("escidoc.import.database.driver.class"));
            String connectionUrl = PropertyReader.getProperty("escidoc.import.database.connection.url");
            return DriverManager.getConnection(connectionUrl
                    .replaceAll("\\$1", PropertyReader.getProperty("escidoc.import.database.server.name"))
                    .replaceAll("\\$2", PropertyReader.getProperty("escidoc.import.database.server.port"))
                    .replaceAll("\\$3", PropertyReader.getProperty("escidoc.import.database.name")),
                    PropertyReader.getProperty("escidoc.import.database.user.name"),
                    PropertyReader.getProperty("escidoc.import.database.user.password"));
            
//            Context ctx = new InitialContext();
//            DataSource dataSource = (DataSource) ctx.lookup("ImportLog");
//            return dataSource.getConnection();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error creating database connection", e);
        }
    }

    /**
     * @throws RuntimeException
     */
    public void closeConnection()
    {
        try
        {
            if (this.connection != null && !this.connection.isClosed())
            {
                //this.connection.close();
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error creating database connection", e);
        }
    }
    
    /**
     * Called when, for any reason, this action is over.
     */
    public void close()
    {
        try
        {
            if (!this.connection.isClosed())
            {
                
                this.endDate = new Date();
                this.status = Status.FINISHED;
                this.percentage = 100;
                
                updateLog();
                
                //this.connection.close();
                
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error closing connection", e);
        }
    }
    
    /**
     * Called when this process shall continue.
     */
    public void reopen()
    {

        this.endDate = null;
        this.status = Status.PENDING;
        
        updateLog();

    }
    
    /**
     * 
     * @param message
     */
    public void startItem(String message)
    {
        startItem(message, null);
    }
    
    public void startItem(String message, String itemId)
    {
        startItem(message, new Date(), itemId);
    }
    
    public void startItem(String message, Date startDate, String itemId)
    {
        startItem(ErrorLevel.FINE, message, startDate, itemId);
    }
    
    public void startItem(ErrorLevel errorLevel, String message)
    {
        startItem(errorLevel, message, new Date(), null);
    }
    
    public void startItem(ErrorLevel errorLevel, String message, Date startDate, String itemId)
    {
        if (this.currentItem != null)
        {
            throw new RuntimeException("Trying to start logging an item while another is not yet finished");
        }
        
        ImportLogItem newItem = new ImportLogItem(this);
        
        newItem.setErrorLevel(errorLevel);
        newItem.setMessage(message);
        newItem.setStartDate(startDate);
        
        saveItem(newItem);
        
        items.add(newItem);
        
        this.currentItem = newItem;
    }
    
    public void finishItem()
    {
        if (this.currentItem != null)
        {
            this.currentItem.setEndDate(new Date());
            this.currentItem.setStatus(Status.FINISHED);
            
            updateItem(this.currentItem);
            
            this.currentItem = null;
        }
    }
    
    public void suspendItem()
    {
        if (this.currentItem != null)
        {
            this.currentItem.setStatus(Status.SUSPENDED);
            
            updateItem(this.currentItem);
            
            this.currentItem = null;
        }
    }
    
    public void addDetail(ErrorLevel errorLevel, String message)
    {
        if (this.currentItem == null)
        {
            throw new RuntimeException("Trying to add a detail but no log item is started.");
        }
        
        ImportLogItem newDetail = new ImportLogItem(currentItem);
        
        newDetail.setErrorLevel(errorLevel);
        newDetail.setMessage(message);
        newDetail.setStartDate(new Date());
        newDetail.setStatus(Status.FINISHED);
        
        this.currentItem.getItems().add(newDetail);
        
        saveDetail(newDetail);
        
    }
    
    public void addDetail(ErrorLevel errorLevel, Exception exception)
    {
        String message = getExceptionMessage(exception);
        addDetail(errorLevel, message);
    }
    
    public void setItemVO(PubItemVO itemVO)
    {
        this.currentItem.setItemVO(itemVO);
    }
    
    private String getExceptionMessage(Throwable exception)
    {
        StringWriter stringWriter = new StringWriter();
        stringWriter.write(exception.getClass().getSimpleName());
        if (exception.getMessage() != null)
        {
            stringWriter.write(": ");
            stringWriter.write(exception.getMessage());
        }
        stringWriter.write("\n");
        
        StackTraceElement[] stackTraceElements = exception.getStackTrace();
        for (StackTraceElement stackTraceElement : stackTraceElements)
        {
            //at TimeTest.main(TimeTest.java:47)
            stringWriter.write("\tat ");
            stringWriter.write(stackTraceElement.getClassName());
            stringWriter.write(".");
            stringWriter.write(stackTraceElement.getMethodName());
            stringWriter.write("(");
            stringWriter.write(stackTraceElement.getFileName());
            stringWriter.write(":");
            stringWriter.write(stackTraceElement.getLineNumber() + "");
            stringWriter.write(")\n");
        }
        if (exception.getCause() != null)
        {
            stringWriter.write(getExceptionMessage(exception.getCause())); 
        }
        return stringWriter.toString();
    }

    public void setItemId(String id)
    {
        this.currentItem.setItemId(id);
        updateItem(this.currentItem);
    }

    public boolean isDone()
    {
        return (this.status == Status.FINISHED);
    }
    
    /**
     * @return the startDate
     */
    public Date getStartDate()
    {
        return startDate;
    }
    
    /**
     * @return the startDate
     */
    public String getStartDateFormatted()
    {
        if (this.startDate != null)
        {
            return DATE_FORMAT.format(startDate);
        }
        else
        {
            return "";
        }
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(Date startDate)
    {
        this.startDate = startDate;
    }

    /**
     * @return the endDate
     */
    public Date getEndDate()
    {
        return endDate;
    }

    /**
     * @return the endDate
     */
    public String getEndDateFormatted()
    {
        if (this.endDate != null)
        {
            return DATE_FORMAT.format(endDate);
        }
        else
        {
            return "";
        }
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(Date endDate)
    {
        this.endDate = endDate;
    }

    /**
     * @return the status
     */
    public Status getStatus()
    {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(Status status)
    {
        this.status = status;
    }

    /**
     * @return the format
     */
    public String getFormat()
    {
        return format;
    }

    /**
     * @param format the format to set
     */
    public void setFormat(String format)
    {
        this.format = format;
    }

    /**
     * @return the user
     */
    public String getUser()
    {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user)
    {
        this.user = user;
    }

    /**
     * @return the context
     */
    public String getContext()
    {
        return context;
    }

    /**
     * @param context the context to set
     */
    public void setContext(String context)
    {
        this.context = context;
    }

    /**
     * @return the errorLevel
     */
    public ErrorLevel getErrorLevel()
    {
        return errorLevel;
    }

    /**
     * @param errorLevel the errorLevel to set
     */
    public void setErrorLevel(ErrorLevel errorLevel)
    {
        if (this.errorLevel == null
                || errorLevel == ErrorLevel.FATAL
                || (errorLevel == ErrorLevel.ERROR
                        && this.errorLevel != ErrorLevel.FATAL)
                || (errorLevel == ErrorLevel.PROBLEM
                        && this.errorLevel != ErrorLevel.FATAL
                        && this.errorLevel != ErrorLevel.ERROR)
                || (errorLevel == ErrorLevel.WARNING
                        && this.errorLevel != ErrorLevel.FATAL
                        && this.errorLevel != ErrorLevel.ERROR
                        && this.errorLevel != ErrorLevel.PROBLEM))
        {
            this.errorLevel = errorLevel;
        }
        if (this.connection != null)
        {
            updateLog();
        }
    }

    public boolean getFinished()
    {
        return (this.status == Status.FINISHED);
    }
    
    /**
     * @return the items
     */
    public List<ImportLogItem> getItems()
    {
        return items;
    }

    /**
     * @param items the items to set
     */
    public void setItems(List<ImportLogItem> items)
    {
        this.items = items;
    }
    
    /**
     * @return the storedId
     */
    public int getStoredId()
    {
        return storedId;
    }

    /**
     * @param storedId the storedId to set
     */
    public void setStoredId(int storedId)
    {
        this.storedId = storedId;
    }

    /**
     * @return the action
     */
    public String getAction()
    {
        return action;
    }

    /**
     * @param action the action to set
     */
    public void setAction(String action)
    {
        this.action = action;
    }

    /**
     * @return the currentItem
     */
    public ImportLogItem getCurrentItem()
    {
        return currentItem;
    }

    /**
     * @return the message
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message)
    {
        this.message = message;
    }

    /**
     * @return the userHandle
     */
    public String getUserHandle()
    {
        return userHandle;
    }

    /**
     * @param userHandle the userHandle to set
     */
    public void setUserHandle(String userHandle)
    {
        this.userHandle = userHandle;
    }

    /**
     * @return the percentage
     */
    public int getPercentage()
    {
        return percentage;
    }

    /**
     * @param percentage the percentage to set
     */
    public void setPercentage(int percentage)
    {
        this.percentage = percentage;
        updateLog();
    }

    private synchronized void saveLog()
    {
        try
        {
            PreparedStatement statement = this.connection.prepareStatement("insert into escidoc_import_log "
                    + "(status, errorlevel, startdate, action, userid, name, context, format, percentage) values (?, ?, ?, ?, ?, ?, ?, ?, 0)");
            
            statement.setString(1, this.status.toString());
            statement.setString(2, this.errorLevel.toString());
            statement.setTimestamp(3, new Timestamp(this.startDate.getTime()));
            statement.setString(4, this.action);
            statement.setString(5, this.user);
            statement.setString(6, this.message);
            statement.setString(7, this.context);
            statement.setString(8, this.format);
            
            statement.executeUpdate();
            //statement.close();
            
            statement = this.connection.prepareStatement("select max(id) as maxid from escidoc_import_log");
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next())
            {
                this.storedId = resultSet.getInt("maxid");
                //resultSet.close();
                //statement.close();
            }
            else
            {
                //resultSet.close();
                //statement.close();
                throw new RuntimeException("Error saving log");
            }
            
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error saving log", e);
        }
    }
    
    private synchronized void updateLog()
    {
        try
        {
            PreparedStatement statement = this.connection.prepareStatement("update escidoc_import_log set "
                    + "status = ?, "
                    + "errorlevel = ?, "
                    + "startdate = ?, "
                    + "enddate = ?, "
                    + "action = ?, "
                    + "userid = ?, "
                    + "name = ?, "
                    + "context = ?, "
                    + "format = ?, "
                    + "percentage = ? "
                    + "where id = ?");
            
            statement.setString(1, this.status.toString());
            statement.setString(2, this.errorLevel.toString());
            statement.setTimestamp(3, new Timestamp(this.startDate.getTime()));
            if (this.endDate != null)
            {
                statement.setTimestamp(4, new Timestamp(this.endDate.getTime()));
            }
            else
            {
                statement.setTimestamp(4, null);
            }
            statement.setString(5, this.action);
            statement.setString(6, this.user);
            statement.setString(7, this.message);
            statement.setString(8, this.context);
            statement.setString(9, this.format);
            statement.setInt(10, this.percentage);
            
            statement.setInt(11, this.storedId);
            
            statement.executeUpdate();
            //statement.close();
                        
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error saving log", e);
        }
    }

    private synchronized void saveItem(ImportLogItem item)
    {
        try
        {
            PreparedStatement statement = this.connection.prepareStatement("insert into escidoc_import_log_item "
                    + "(status, errorlevel, startdate, parent, message, item_id, action) "
                    + "values (?, ?, ?, ?, ?, ?, ?)");
            
            statement.setString(1, item.getStatus().toString());
            statement.setString(2, item.getErrorLevel().toString());
            statement.setTimestamp(3, new Timestamp(item.getStartDate().getTime()));
            statement.setInt(4, this.storedId);
            statement.setString(5, item.getMessage());
            statement.setString(6, item.getItemId());
            statement.setString(7, item.getAction());
            
            statement.executeUpdate();
            //statement.close();
            
            statement = this.connection.prepareStatement("select max(id) as maxid from escidoc_import_log_item");
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next())
            {
                item.setStoredId(resultSet.getInt("maxid"));
                //resultSet.close();
                //statement.close();
            }
            else
            {
                //resultSet.close();
                //statement.close();
                throw new RuntimeException("Error saving log item");
            }
            
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error saving log", e);
        }
    }
    
    private synchronized void updateItem(ImportLogItem item)
    {
        try
        {
            PreparedStatement statement = this.connection.prepareStatement("update escidoc_import_log_item set "
                    + "status = ?, "
                    + "errorlevel = ?, "
                    + "startdate = ?, "
                    + "enddate = ?, "
                    + "parent = ?, "
                    + "message = ?, "
                    + "item_id = ?, "
                    + "action = ? "
                    + "where id = ?");
            
            statement.setString(1, item.getStatus().toString());
            statement.setString(2, item.getErrorLevel().toString());
            statement.setTimestamp(3, new Timestamp(item.getStartDate().getTime()));
            if (item.getEndDate() != null)
            {
                statement.setTimestamp(4, new Timestamp(item.getEndDate().getTime()));
            }
            else
            {
                statement.setDate(4, null);
            }
            statement.setInt(5, this.storedId);
            statement.setString(6, item.getMessage());
            statement.setString(7, item.getItemId());
            statement.setString(8, item.getAction());
            statement.setInt(9, item.getStoredId());
            
            statement.executeUpdate();
            //statement.close();
                        
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error saving log", e);
        }
    }

    private synchronized void saveDetail(ImportLogItem detail)
    {
        try
        {
            PreparedStatement statement = this.connection.prepareStatement("insert into escidoc_import_log_detail "
                    + "(status, errorlevel, startdate, parent, message, item_id, action) "
                    + "values (?, ?, ?, ?, ?, ?, ?)");
            
            statement.setString(1, detail.getStatus().toString());
            statement.setString(2, detail.getErrorLevel().toString());
            statement.setTimestamp(3, new Timestamp(detail.getStartDate().getTime()));
            statement.setInt(4, detail.getParent().getStoredId());
            statement.setString(5, detail.getMessage());
            statement.setString(6, detail.getItemId());
            statement.setString(7, detail.getAction());
            
            statement.executeUpdate();
            //statement.close();
            
            statement = this.connection.prepareStatement("select max(id) as maxid from escidoc_import_log_detail");
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next())
            {
                detail.setStoredId(resultSet.getInt("maxid"));
                //resultSet.close();
                //statement.close();
            }
            else
            {
                //resultSet.close();
                //statement.close();
                throw new RuntimeException("Error saving log item");
            }
            
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error saving log", e);
        }
    }

    public static List<ImportLog> getImportLogs(String action, AccountUserVO user, SortColumn sortBy, SortDirection dir)
    {
        return getImportLogs(action, user, sortBy, dir, true);
    }
    
    public static List<ImportLog> getImportLogs(String action, AccountUserVO user, SortColumn sortBy, SortDirection dir, boolean loadDetails)
    {
        return getImportLogs(action, user, sortBy, dir, true, loadDetails);
    }
    
    public static List<ImportLog> getImportLogs(String action, AccountUserVO user, SortColumn sortBy, SortDirection dir, boolean loadItems, boolean loadDetails)
    {
        List<ImportLog> result = new ArrayList<ImportLog>();
        Connection connection = getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = "select id from escidoc_import_log where action = ? and userid = ? order by " + sortBy + " " + dir;
        try
        {
            statement = connection.prepareStatement(query);
            statement.setString(1, action);
            statement.setString(2, user.getReference().getObjectId());
            
            resultSet = statement.executeQuery();
            
            while (resultSet.next())
            {
                int id = resultSet.getInt("id");
                ImportLog log = getImportLog(id, loadDetails);
                log.userHandle = user.getHandle();
                result.add(log);
            }
        }
        catch (Exception e)
        {
            try
            {
                //resultSet.close();
                //statement.close();
                //connection.close();
            }
            catch (Exception f)
            {}
            throw new RuntimeException("Error getting log", e);
        }
        try
        {
            //resultSet.close();
            //statement.close();
            //connection.close();
        }
        catch (Exception f)
        {
            throw new RuntimeException("Error closing connection", f);
        }
        
        return result;
    }

    public static ImportLog getImportLog(int id)
    {
        return getImportLog(id, true);
    }

    public static ImportLog getImportLog(int id, boolean loadDetails)
    {
        return getImportLog(id, true, loadDetails);
    }
    
    public static ImportLog getImportLog(int id, boolean loadItems, boolean loadDetails)
    {
        Connection connection = getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = null;
        
        ImportLog result = null;
        
        
        try
        {
            query = "select * from escidoc_import_log where id = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            
            resultSet = statement.executeQuery();
            
            if (resultSet.next())
            {
                result = new ImportLog();
                result.setAction(resultSet.getString("action"));
                result.setEndDate(resultSet.getTimestamp("enddate"));
                result.setErrorLevel(ErrorLevel.valueOf(resultSet.getString("errorlevel").toUpperCase()));
                result.setFormat(resultSet.getString("format"));
                result.setStartDate(resultSet.getTimestamp("startdate"));
                result.setStatus(Status.valueOf(resultSet.getString("status")));
                result.setStoredId(id);
                result.setUser(resultSet.getString("userid"));
                result.setMessage(resultSet.getString("name"));
                result.percentage = resultSet.getInt("percentage");
            }
            else
            {
                logger.warn("Import log query returned no result for id " + id);
            }
            
            query = "select * from escidoc_import_log_item where parent = ? order by id";
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            
            resultSet = statement.executeQuery();
            
            List<ImportLogItem> items = new ArrayList<ImportLogItem>();
            
            while (resultSet.next())
            {
                ImportLogItem item = new ImportLogItem(result);
                
                item.setAction(resultSet.getString("action"));
                item.setEndDate(resultSet.getTimestamp("enddate"));
                item.setErrorLevel(ErrorLevel.valueOf(resultSet.getString("errorlevel").toUpperCase()));
                item.setStartDate(resultSet.getTimestamp("startdate"));
                item.setStatus(Status.valueOf(resultSet.getString("status")));
                item.setStoredId(resultSet.getInt("id"));
                item.setItemId(resultSet.getString("item_id"));
                item.setMessage(resultSet.getString("message"));
                
                items.add(item);
            }
            
            result.setItems(items);
            
            if (loadDetails)
            {
                query = "select escidoc_import_log_detail.* "
                		+ "from escidoc_import_log_item, escidoc_import_log_detail "
                		+ "where escidoc_import_log_item.id = escidoc_import_log_detail.parent "
                		+ "and escidoc_import_log_item.parent = ? "
                		+ "order by escidoc_import_log_detail.id";
                statement = connection.prepareStatement(query);
                statement.setInt(1, id);
                
                resultSet = statement.executeQuery();
    
                Iterator<ImportLogItem> iterator = items.iterator();
                
                if (items.size() > 0)
                {
                
                    ImportLogItem currentItem = iterator.next();
                    List<ImportLogItem> details = new ArrayList<ImportLogItem>();
                    currentItem.setItems(details);
                    
                    while (resultSet.next())
                    {
                        int itemId = resultSet.getInt("parent");
                        while (currentItem.getStoredId() != itemId && iterator.hasNext())
                        {
                            currentItem = iterator.next();
                            details = new ArrayList<ImportLogItem>();
                            currentItem.setItems(details);
                        }
                        
                        ImportLogItem detail = new ImportLogItem(currentItem);
                        
                        detail.setAction(resultSet.getString("action"));
                        detail.setEndDate(resultSet.getTimestamp("enddate"));
                        detail.setErrorLevel(ErrorLevel.valueOf(resultSet.getString("errorlevel").toUpperCase()));
                        detail.setStartDate(resultSet.getTimestamp("startdate"));
                        detail.setStatus(Status.valueOf(resultSet.getString("status")));
                        detail.setStoredId(resultSet.getInt("id"));
                        detail.setItemId(resultSet.getString("item_id"));
                        detail.setMessage(resultSet.getString("message"));
                        
                        details.add(detail);
                    }
                }
            }
        }
        catch (Exception e)
        {
            try
            {
                //resultSet.close();
                //statement.close();
                //connection.close();
            }
            catch (Exception f)
            {}
            throw new RuntimeException("Error getting detail", e);
        }
        try
        {
            //resultSet.close();
            //statement.close();
            //connection.close();
        }
        catch (Exception f)
        {
            throw new RuntimeException("Error closing connection", f);
        }
        
        return result;
    }
    
    public static List<ImportLogItem> loadDetails(int id, String userid)
    {
        List<ImportLogItem> details = new ArrayList<ImportLogItem>();
        Connection connection = getConnection();
        
        String query = "select escidoc_import_log_detail.* "
            + "from escidoc_import_log_item, escidoc_import_log_detail, escidoc_import_log "
            + "where escidoc_import_log_item.id = escidoc_import_log_detail.parent "
            + "and escidoc_import_log_item.parent = escidoc_import_log.id "
            + "and escidoc_import_log_item.id = ? "
            + "and escidoc_import_log.userid = ? "
            + "order by escidoc_import_log_detail.id";
        try
        {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            statement.setString(2, userid);
            
            ResultSet resultSet = statement.executeQuery();
        
            while (resultSet.next())
            {
                
                ImportLogItem detail = new ImportLogItem(null);
                
                detail.setAction(resultSet.getString("action"));
                detail.setEndDate(resultSet.getTimestamp("enddate"));
                detail.setErrorLevel(ErrorLevel.valueOf(resultSet.getString("errorlevel").toUpperCase()));
                detail.setStartDate(resultSet.getTimestamp("startdate"));
                detail.setStatus(Status.valueOf(resultSet.getString("status")));
                detail.setStoredId(resultSet.getInt("id"));
                detail.setItemId(resultSet.getString("item_id"));
                detail.setMessage(resultSet.getString("message"));
                
                details.add(detail);
            }
            return details;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public String toString()
    {
        StringWriter writer = new StringWriter();

        writer.write(getErrorLevel().toString());
        writer.write(": ");
        if (getRelevantString() != null)
        {
            writer.write(getRelevantString());
        }
        else
        {
            writer.write("- - -");
        }
        writer.write(" (");
        writer.write(DATE_FORMAT.format(getStartDate()));
        writer.write(" - ");
        if (getEndDate() != null)
        {
            writer.write(DATE_FORMAT.format(getEndDate()));
        }
        writer.write(") - ");
        writer.write(getStatus().toString());
        writer.write("\n");

        for (ImportLogItem item : getItems())
        {
            writer.write(item.toString().replaceAll("(.*)\n", "\t$1\n"));
        }
        
        return writer.toString();
    }

    protected String getRelevantString()
    {
        return getAction();
    }

    public void activateItem(ImportLogItem item)
    {
        if (this.currentItem == null)
        {
            this.currentItem = item;
        }
        else
        {
            throw new RuntimeException("Trying to start logging an item while another is not yet finished");
        }
    }
    
    public String toXML()
    {
        StringWriter writer = new StringWriter();

        writer.write("<import-task ");
        writer.write("status=\"");
        writer.write(this.status.toString());
        writer.write("\" error-level=\"");
        writer.write(this.errorLevel.toString());
        writer.write("\" created-by=\"");
        writer.write(this.user);
        writer.write("\">\n");
        
        writer.write("\t<name>");
        writer.write(this.message);
        writer.write("</name>\n");
        
        writer.write("\t<context>");
        writer.write(this.context);
        writer.write("</context>\n");
        
        writer.write("\t<start-date>");
        writer.write(getStartDateFormatted());
        writer.write("</start-date>\n");
        
        if (this.endDate != null)
        {
            writer.write("\t<end-date>");
            writer.write(getEndDateFormatted());
            writer.write("</end-date>\n");
        }
        
        writer.write("\t<format>");
        writer.write(this.format);
        writer.write("</format>\n");
        
        writer.write("\t<items>\n");
        for (ImportLogItem item : this.items)
        {
            writer.write(item.toXML().replaceAll("(.*\\n)", "\t\t$1"));
        }
        writer.write("\t</items>\n");
        writer.write("</import-task>\n");
        
        return writer.toString();
    }

    public String getLocalizedMessage()
    {
        FacesContext context = FacesContext.getCurrentInstance();
        InternationalizationHelper i18nHelper = (InternationalizationHelper) context.getExternalContext().getSessionMap().get(InternationalizationHelper.BEAN_NAME);
        try
        {
            return ResourceBundle.getBundle(i18nHelper.getSelectedMessagesBundle()).getString(getMessage());
        }
        catch (MissingResourceException mre)
        {
            // No message entry for this message, it's probably raw data.
            return getMessage();
        }
        
    }
    
    /**
     * JSF action to remove an import from the database.
     * 
     * @return Always null.
     */
    public String remove()
    {
        try
        {
            Connection connection = getConnection();
            
            String query = "delete from escidoc_import_log_detail where parent in (select id from escidoc_import_log_item where parent = ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, this.storedId);
            statement.executeUpdate();
            statement.close();
            
            query = "delete from escidoc_import_log_item where parent  = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, this.storedId);
            statement.executeUpdate();
            statement.close();
            
            query = "delete from escidoc_import_log where id  = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, this.storedId);
            statement.executeUpdate();
            statement.close();
            
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        
        FacesContext fc = FacesContext.getCurrentInstance();
        try
        {
            fc.getExternalContext().redirect("ImportWorkspace.jsp");
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        return null;
    }
    
    /**
     * JSF action to delete all items of an import from the repository.
     * 
     * @return Always null.
     */
    public String deleteAll()
    {
        this.connection = getConnection();
        DeleteProcess deleteProcess = new DeleteProcess(this);
        deleteProcess.start();
        
        FacesContext fc = FacesContext.getCurrentInstance();
        try
        {
            fc.getExternalContext().redirect("ImportWorkspace.jsp");
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        
        return null;
    }
    
    /**
     * JSF action to submit/release all items of an import from the repository.
     * 
     * @return Always null.
     */
    public String submitAll()
    {
        this.connection = getConnection();
        SubmitProcess submitProcess = new SubmitProcess(this, false);
        submitProcess.start();
        
        FacesContext fc = FacesContext.getCurrentInstance();
        try
        {
            fc.getExternalContext().redirect("ImportWorkspace.jsp");
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        
        return null;
    }
    
    /**
     * JSF action to submit/release all items of an import from the repository.
     * 
     * @return Always null.
     */
    public String submitAndReleaseAll()
    {
        this.connection = getConnection();
        SubmitProcess submitProcess = new SubmitProcess(this, true);
        submitProcess.start();
        
        FacesContext fc = FacesContext.getCurrentInstance();
        try
        {
            fc.getExternalContext().redirect("ImportWorkspace.jsp");
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        
        return null;
    }
    
    /**
     * @return the itemLink
     */
    public String getLogLink()
    {
        return "ImportData.jsp?id=" + getStoredId();
    }
    
    /**
     * @return the itemLink
     */
    public String getItemsLink()
    {
        return "ImportItems.jsp?id=" + getStoredId();
    }
    
    private Workflow getWorkflow()
    {
        if (this.workflow == null)
        {
            try
            {
                ContextVO contextVO;
                ContextHandler contextHandler = ServiceLocator.getContextHandler();
                InitialContext context = new InitialContext();
                XmlTransforming xmlTransforming = (XmlTransforming) context.lookup(XmlTransforming.SERVICE_NAME);
                
                String contextXml = contextHandler.retrieve(this.context);
                contextVO = xmlTransforming.transformToContext(contextXml);
        
                this.workflow = contextVO.getAdminDescriptor().getWorkflow();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return this.workflow;
    }
    
    public boolean getSimpleWorkflow()
    {
        return (getWorkflow() == Workflow.SIMPLE);
    }
}
