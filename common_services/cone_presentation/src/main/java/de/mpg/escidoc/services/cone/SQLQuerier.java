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

import de.mpg.escidoc.services.cone.util.Pair;
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
            if (lang == null || language == null || lang.equals(language))
            {
                resultSet.add(new Pair(id, value));
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
    public Map<String, List<String>> details(String model, String id) throws Exception
    {
        return details(model, id, null);
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, List<String>> details(String model, String id, String language) throws Exception
    {
        id = escape(id);
        String query = "select distinct object, predicate from triples where " + "subject = '" + id + "'";
        if (language == null)
        {
            language = PropertyReader.getProperty(ESCIDOC_CONE_LANGUAGE_DEFAULT);
        }
        if (language != null)
        {
            query += "and (lang is null or lang = '" + language + "')";
        }
        logger.debug("query: " + query);
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(query);
        Map<String, List<String>> resultMap = new HashMap<String, List<String>>();
        while (result.next())
        {
            String predicate = result.getString("predicate");
            String object = result.getString("object");
            if (resultMap.containsKey(predicate))
            {
                resultMap.get(predicate).add(object);
            }
            else
            {
                ArrayList<String> newEntry = new ArrayList<String>();
                newEntry.add(object);
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
}
