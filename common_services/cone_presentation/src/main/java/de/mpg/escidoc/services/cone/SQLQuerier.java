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
 * Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft für wissenschaftlich-technische Information mbH
 * and Max-Planck- Gesellschaft zur Förderung der Wissenschaft e.V. All rights reserved. Use is subject to license
 * terms.
 */
package de.mpg.escidoc.services.cone;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.cone.util.LocalizedString;
import de.mpg.escidoc.services.cone.util.Pair;
import de.mpg.escidoc.services.cone.util.PatternHelper;
import de.mpg.escidoc.services.framework.PropertyReader;

/**
 * SQL implementation for the {@link Querier} interface. Currently works with Postgres, but should also work with other
 * relational databases like HSQL, MySQL. For Oracle and SQL Server, maybe some modifications will be needed.
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class SQLQuerier implements Querier
{
    private static final String ESCIDOC_CONE_LANGUAGE_DEFAULT = "escidoc.cone.language.default";
    private static final Logger logger = Logger.getLogger(SQLQuerier.class);
    private DataSource dataSource = null;

    /**
     * Default constructor initializing the {@link DataSource}.
     * @throws Exception Any exception.
     */
    public SQLQuerier() throws Exception
    {
        InitialContext context = new InitialContext();
        dataSource = (DataSource) context.lookup("Cone");
    }

    /**
     * {@inheritDoc}
     */
    public List<Pair> query(String model, String query) throws Exception
    {
        return query(model, query, null);
    }

    /**
     * {@inheritDoc}
     */
    public List<Pair> query(String model, String query, String language) throws Exception
    {
        String limitString = PropertyReader.getProperty("escidoc.cone.maximum.results");
        return query(model, query, language, Integer.parseInt(limitString));
    }

    /**
     * {@inheritDoc}
     */
    public List<Pair> query(String model, String searchString, String language, int limit) throws Exception
    {
        if (language == null)
        {
            language = PropertyReader.getProperty(ESCIDOC_CONE_LANGUAGE_DEFAULT);
        }

        String[] searchStringsWithWildcards = formatSearchString(searchString);
        String query = "select id, value, lang"
                + " from results where id in (select subject from vw_search where model = '" + model + "' and";
        for (int i = 0; i < searchStringsWithWildcards.length; i++)
        {
            if (i > 0)
            {
                query += " and";
            }
            query += " object ilike '" + searchStringsWithWildcards[i] + "'";
        }
        query += ")";
        query += " order by value, id";
        if (limit > 0)
        {
            query += " limit " + limit;
        }
        
        query += ";";
        
        logger.debug("query: " + query);
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        long now = new Date().getTime();
        ResultSet result = statement.executeQuery(query);
        logger.debug("Took " + (new Date().getTime() - now) + " ms.");
        List<Pair> resultSet = new ArrayList<Pair>();
        while (result.next())
        {
            String id = result.getString("id");
            String value = result.getString("value");
            String lang = result.getString("lang");
            Pair pair = new Pair(id, value);
            if ((lang == null || language == null || lang.equals(language)) && !resultSet.contains(pair))
            {
                resultSet.add(pair);
            }
        }
        connection.close();
        logger.debug("Result: " + resultSet);
        return resultSet;
    }

    /**
     * {@inheritDoc}
     */
    private String[] formatSearchString(String searchString)
    {
        String[] result = searchString.trim().split(" ");
        for (int i = 0; i < result.length; i++)
        {
            result[i] = "%" + result[i].replaceAll("\\*|%", "") + "%";
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, List<LocalizedString>> details(String model, String id) throws Exception
    {
        return details(model, id, null);
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, List<LocalizedString>> details(String model, String id, String language) throws Exception
    {
        id = escape(id);
        String query = "select distinct object, predicate, lang from triples where model = '"
            + model + "' and " + "subject = '" + id + "'";
        if (language == null)
        {
            language = PropertyReader.getProperty(ESCIDOC_CONE_LANGUAGE_DEFAULT);
        }
        if (language != null && !"*".equals(language))
        {
            query += "and (lang is null or lang = '" + language + "')";
        }
        logger.debug("query: " + query);
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(query);
        Map<String, List<LocalizedString>> resultMap = new HashMap<String, List<LocalizedString>>();
        while (result.next())
        {
            String predicate = result.getString("predicate");
            String object = result.getString("object");
            String lang = result.getString("lang");
            
            if (resultMap.containsKey(predicate))
            {
                resultMap.get(predicate).add(new LocalizedString(object, lang));
            }
            else
            {
                ArrayList<LocalizedString> newEntry = new ArrayList<LocalizedString>();
                newEntry.add(new LocalizedString(object, lang));
                resultMap.put(predicate, newEntry);
            }
        }
        connection.close();
        logger.info("Result: " + resultMap);
        return resultMap;
    }

    /**
     * Returns a SQL safe representation of the given String.
     * 
     * @param str The string that should be escaped
     * @return The escaped string
     */
    private String escape(String str)
    {
        return str.replace("'", "''");
    }

    /**
     * {@inheritDoc}
     */
    public void create(String model, String id, Map<String, List<LocalizedString>> values) throws Exception
    {
        
        String query = "insert into triples (subject, predicate, object, lang, model) values (?, ?,  ?, ?, ?)";
        
        Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        
        for (String predicate : values.keySet())
        {
            statement.setString(1, id);
            statement.setString(2, predicate);
            statement.setString(5, model);
            
            for (LocalizedString object : values.get(predicate))
            {
                statement.setString(3, object.getValue());
                if (object.getLanguage() != null && "".equals(object.getLanguage()))
                {
                    statement.setString(4, null);
                }
                else
                {
                    statement.setString(4, object.getLanguage());
                }
                statement.executeUpdate();
            }
        }
        
        query = "insert into results (id, value, lang) values (?, ?, ?)";
        statement = connection.prepareStatement(query);
        
        statement.setString(1, id);
        
        List<Pair> results = PatternHelper.buildObjectFromPattern(model, id, values);
        
        for (Pair pair : results)
        {
            if (pair.getValue() != null && !"".equals(pair.getValue()))
            {
                statement.setString(2, pair.getValue());
                if (pair.getKey() != null && "".equals(pair.getKey()))
                {
                    statement.setString(3, null);
                }
                else
                {
                    statement.setString(3, pair.getKey());
                }
                statement.executeUpdate();
            }
        }

        connection.close();
    }

    /**
     * {@inheritDoc}
     */
    public void delete(String model, String id) throws Exception
    {
        
        String query = "delete from triples where subject = ? and model = ?";
        
        Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        
        statement.setString(1, id);
        statement.setString(2, model);
        
        statement.executeUpdate();
        
        query = "delete from results where id = ?";
                
        statement = connection.prepareStatement(query);
        statement.setString(1, id);

        statement.executeUpdate();
        
        connection.close();
    }

    public static void main(String[] args) throws Exception
    {
        SQLQuerier querier = new SQLQuerier();
        querier.delete("abc", "123");
    }
    
    /**
     * {@inheritDoc}
     */
    public synchronized String createUniqueIdentifier(String model) throws Exception
    {
        String query = "select value from properties where name = 'max_id'";
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        
        ResultSet resultSet = statement.executeQuery(query);
        
        if (resultSet.next())
        {
            String maxIdAsString = resultSet.getString("value");
            int maxId = Integer.parseInt(maxIdAsString) + 1;
            
            query = "update properties set value = '" + maxId + "' where name = 'max_id'";
            statement.executeUpdate(query);
            
            connection.close();
            
            return model + maxId;
        }
        else
        {
            connection.close();
            throw new Exception("'max_id not found in properties table'");
        }
    }
    
}
