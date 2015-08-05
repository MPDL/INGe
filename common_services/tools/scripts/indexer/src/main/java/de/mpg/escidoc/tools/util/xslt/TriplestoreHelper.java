package de.mpg.escidoc.tools.util.xslt;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;


public class TriplestoreHelper
{
	private static final Logger logger = Logger.getLogger(TriplestoreHelper.class);
    private Connection connection;
    private static Properties properties = new Properties();


    
    // the database table containing the context - organizational unit mapping
    private String context_ou_table;

    
    //private static final String CONTEXT_OU_RELATION = "http://escidoc.de/core/01/structural-relations/organizational-unit";
    private static final String CONTEXT_OU_RELATION = "%organizational-unit%";

	public TriplestoreHelper() 
	{
		try
		{
			InputStream s = TriplestoreHelper.class.getClassLoader().getResourceAsStream("indexer.properties");
			properties.load(s);
		} catch (Exception e1)
		{
			logger.warn("Could not read properties");
		}

		try
		{
			Class.forName(properties.getProperty("triplestore.datasource.driverClassName"));
					
			connection = DriverManager.getConnection(
					properties.getProperty("triplestore.datasource.url"),
					properties.getProperty("triplestore.datasource.username"),
					properties.getProperty("triplestore.datasource.password"));

			context_ou_table = getTableName(CONTEXT_OU_RELATION);
		} catch (Exception e)
		{
			logger.warn("Triple store connection failed.");
		}
	}
	
	private String getTableName(String contextOuRelation)
	{
		String sql = "SELECT pkey FROM tmap WHERE p LIKE ?";
        logger.debug("SQL: " + sql);

        try
        {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, CONTEXT_OU_RELATION);
            
            ResultSet rs = pstmt.executeQuery();

            if (rs.next())
            {
                String result = rs.getString("pkey");
                if (rs.next())
                {
 //                   connection.close();
                   logger.warn("More than one entry was found for component <" + CONTEXT_OU_RELATION + ">");
                }
                else
                {
 //                   connection.close();
                    return "t" + result;
                }
            }
            else
            {
                connection.close();
                logger.warn("No pkex was found in tmap for  <" + CONTEXT_OU_RELATION + ">");
            }
        }
        catch (SQLException sqle)
        {
            try
            {
                connection.close();
            }
            catch (Exception e)
            {
                logger.error("Error trying to close the connection", e);
            }
        }
        
        return null;
	}
	
	public static TriplestoreHelper getInstance()
	{
		return TriplestoreHelperHolder.instance;
	}
	
	public String getContextOuRelationName()
	{
		return this.context_ou_table;
	}
	
	public String getOrganizationFor(String context)
	{
		String sql = "SELECT o FROM " + context_ou_table + " WHERE s LIKE ?";
        logger.debug("SQL: " + sql);

        try
        {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, "%" + context + "%");
            
            ResultSet rs = pstmt.executeQuery();

            if (rs.next())
            {
                String result = rs.getString("o");
                if (rs.next())
                {
                   //connection.close();
                   logger.warn("More than one entry was found for context <" + context + ">");
                }
                else
                {
                    //connection.close();
                    return  result;
                }
            }
            else
            {
                connection.close();
                logger.warn("No organization was found in tmap for  <" + context + ">");
            }
        }
        catch (SQLException sqle)
        {
            try
            {
                connection.close();
            }
            catch (Exception e)
            {
                logger.error("Error trying to close the connection", e);
            }
        }
        
        return null;
	}

	private static class TriplestoreHelperHolder
	{
		private static final TriplestoreHelper instance = new TriplestoreHelper();
	}

}
