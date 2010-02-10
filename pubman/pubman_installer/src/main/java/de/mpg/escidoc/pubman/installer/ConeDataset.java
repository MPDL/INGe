package de.mpg.escidoc.pubman.installer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.installer.util.ResourceUtil;
import de.mpg.escidoc.services.cone.ModelList;
import de.mpg.escidoc.services.cone.Querier;
import de.mpg.escidoc.services.cone.QuerierFactory;
import de.mpg.escidoc.services.cone.ModelList.Model;
import de.mpg.escidoc.services.cone.util.TreeFragment;

public class ConeDataset 
{

	/** logging instance */
    private Logger logger = Logger.getLogger(ConeDataset.class);
	/** driver class for the cone database */
    private static final String CONE_DB_DRIVER_CLASS = "org.postgresql.Driver";
    /** connection type for the cone database */
    private static final String CONE_DB_CONNECTION_TYPE = "jdbc:postgresql://";
    /** SQL script for checking cone database availability */
    public static final String CONE_CHECK_DATABASES = "coneData/check_databases.sql";
    /** SQL script for creating cone database */
    public static final String CONE_CREATE_DATABASE = "coneData/create_cone_db.sql";
    /** SQL script for creating cone database structure */
    public static final String CONE_CREATE_SCRIPT = "coneData/database_create.sql";
    /** SQL script for creating indexing cone database */
    public static final String CONE_INDEX_SCRIPT = "coneData/database_index.sql";
    /** SQL script for insertig ddc data into cone database */
    public static final String CONE_INSERT_DDC = "coneData/ddc.sql";
    /** SQL script for insertig ddc data into cone database */
    public static final String CONE_INSERT_ESCIDOC_MIMETYPES = "coneData/escidoc_mimetypes.sql";
    /** SQL script for insertig escidoc mimetypes into cone database */
    public static final String CONE_INSERT_JOURNALS = "coneData/journals.sql";
    /** SQL script for insertig languages into cone database */
    public static final String CONE_INSERT_LANGUAGES = "coneData/languages.sql";
    /** SQL script for insertig mimetypes into cone database */
    public static final String CONE_INSERT_MIMETYPES = "coneData/mimetypes.sql";
    /** SQL script for insertig cc_licenses into cone database */
    public static final String CONE_INSERT_CC_LICENSES = "coneData/cc_licenses.sql";
    /** the connection to the cone DB */
    private Connection connection;
    private String coneServer; 
    private String conePort; 
    private String coneDatabase; 
    private String coneUser; 
    private String conePassword;
    
    
    public ConeDataset() {
        
    }
    
    public ConeDataset(String coneServer, String conePort, String coneDatabase, String coneUser, String conePassword)
    {
    	this.coneServer = coneServer;
    	this.conePort = conePort;
    	this.coneDatabase = coneDatabase;
    	this.coneUser = coneUser;
    	this.conePassword = conePassword;
    }

    public String getResourceAsString(final String fileName) throws FileNotFoundException, Exception
    {
        StringBuffer buffer = new StringBuffer();
        InputStream is = null;
        BufferedReader br = null;
        String line;

        try
        {
            is = getClass().getClassLoader().getResourceAsStream(fileName);
            br = new BufferedReader(new InputStreamReader(is));
            while (null != (line = br.readLine()))
            {
                buffer.append(line);
                buffer.append("\n");
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            try
            {
                if (br != null)
                    br.close();
                if (is != null)
                    is.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return buffer.toString();
    }
    
    public void connectToDB(String dbName) throws Exception
    {
    	connection = DriverManager.getConnection(CONE_DB_CONNECTION_TYPE +
        		this.coneServer +
                ":" +
                this.conePort +
                "/" +
                dbName,
                this.coneUser,
                this.conePassword);
    }
    
    public void disconnectFromDB() throws Exception
    {
    	connection.close();
       
    }
    
    public void runConeScript(String sqlScript) throws Exception
    {
        
        try
        {
            InputStream fileIn = ResourceUtil.getResourceAsStream(sqlScript);
            BufferedReader br = new BufferedReader(new InputStreamReader(fileIn, "UTF-8"));
            StringBuffer query = new StringBuffer();
            String queryPart = "";
            while ((queryPart = br.readLine()) != null)
            {
            	//if(queryPart != null && queryPart.endsWith(";"))
	            //{
            		query.append(queryPart);
            	//}
            }
            
            
        	Statement statement = connection.createStatement();
            statement.executeUpdate(query.toString());
            statement.close();
                
        	
        }
        catch (SQLException e)
        {
            logger.debug("Error description", e);
            logger.info("cone database is set up already");
        }
    }
    
    /**
     * This method checks if the cone database is already existing
     * @param sqlScript the sql script to select all databases
     * @return boolean if cone exists or not
     * @throws Exception
     */
    public boolean isConeDBAvailable(String sqlScript) throws Exception
    {
    	boolean coneIsAvailable = false;
        
        String dbScript = ResourceUtil.getResourceAsString(sqlScript);
        
        String[] queries = dbScript.split(";\n");
        ResultSet resultSet = null;
        Statement statement = null;
        try
        {
            // Query all databases
        	for (String query : queries)
            {
                statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                resultSet = statement.executeQuery(query);
            }
        	int rowCount = 0;
        	resultSet.last();
        	rowCount = resultSet.getRow();
            if(resultSet != null && rowCount > 0)
            {
	            coneIsAvailable = true;
            }
            statement.close();
            
        }
        catch (SQLException e)
        {
            logger.debug("Error description", e);
            logger.info("unable to get databases");
        }
        return coneIsAvailable;
    }
    
    
    public void processConeData() throws Exception
    {
    	Querier querier = QuerierFactory.newQuerier();
		Set<Model> models = ModelList.getInstance().getList();

		for (Model model : models)
		{
		    logger.info("Processing CoNe model: " + model.getName());
		    List<String> ids = querier.getAllIds(model.getName());
		    for (String id : ids)
		    {
		        TreeFragment details = querier.details(model.getName(), id, "*");
		        querier.delete(model.getName(), id);
		        querier.create(model.getName(), id, details);
		       
		    }
		}
		querier.release();
    }
    
}
