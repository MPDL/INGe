/*
 * CDDL HEADER START The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with the License. You can
 * obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License for the
 * specific language governing permissions and limitations under the License. When distributing Covered Code, include
 * this CDDL HEADER in each file and include the License file at license/ESCIDOC.LICENSE. If applicable, add the
 * following below this CDDL HEADER, with the fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner] CDDL HEADER END
 */
/*
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft für wissenschaftlich-technische Information mbH
 * and Max-Planck- Gesellschaft zur Förderung der Wissenschaft e.V. All rights reserved. Use is subject to license
 * terms.
 */
package de.mpg.escidoc.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import de.mpg.escidoc.handler.PIDProviderException;



/**
 * Helper class for reading attributes from riTriple database
 * 
 */
public class SQLQuerier 
{
    private static final Logger logger = Logger.getLogger(SQLQuerier.class);
    private Connection connection;
    
    // the database table containing the item - component mapping
    private String table;


    /**
     * Default constructor initializing the {@link DataSource}.
     * @throws Exception Any exception.
     */
    public SQLQuerier() throws Exception
    {      
        Class.forName(Util.getProperty("triplestore.datasource.driverClassName"));
        connection = DriverManager.getConnection(
                Util.getProperty("triplestore.datasource.url"),
                Util.getProperty("triplestore.datasource.username"),
                Util.getProperty("triplestore.datasource.password"));
        
        table = Util.getProperty("triplestore.datasource.table");
    }



    
    public String getItemIdForComponent(final String componentId) throws PIDProviderException       
    {
        String sql = "SELECT s FROM " + table + " WHERE o = ?";
        logger.debug("SQL: " + sql);

        try
        {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, "<info:fedora/" + componentId + ">");
            
            ResultSet rs = pstmt.executeQuery();

            if (rs.next())
            {
                String result = rs.getString("s");
                if (rs.next())
                {
                    connection.close();
                    throw new PIDProviderException("More than one item was found for component <" + componentId + ">");
                }
                else
                {
                    connection.close();
                    return getId(result);
                }
            }
            else
            {
                connection.close();
                throw new PIDProviderException("No item was found for component <" + componentId + ">");
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
            throw new PIDProviderException("Error getting itemId from database", sqle);
        }
    }


    private String getId(String result)
    {
        if (result == null || "".equals(result))
            return "";
        
        int idx = result.indexOf("/");
        
        if (idx > 0)
            return result.substring(idx + 1, result.length() - 1);
        
        return "";
    }




    /**
     * {@inheritDoc}
     */
    public void release() throws Exception
    {
        connection.close();
    }
}
