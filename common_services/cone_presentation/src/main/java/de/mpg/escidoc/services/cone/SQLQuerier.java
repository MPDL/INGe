package de.mpg.escidoc.services.cone;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.cone.util.Pair;
import de.mpg.escidoc.services.cone.util.Triple;
import de.mpg.escidoc.services.framework.PropertyReader;

public class SQLQuerier implements Querier
{

    private static final Logger logger = Logger.getLogger(SQLQuerier.class);
    private DataSource dataSource = null;
    
    public SQLQuerier() throws Exception
    {
        InitialContext context = new InitialContext();
        dataSource = (DataSource) context.lookup("Cone");
    }
    
    
    public List<Pair> query(String model, String query) throws Exception
    {
        return query(model, query, null);
    }

    public List<Pair> query(String model, String searchString, String language) throws Exception
    {
        
        if (language == null)
        {
            language = PropertyReader.getProperty("escidoc.cone.language.default");
        }
        
        String[] searchStringsWithWildcards = formatSearchString(searchString);
        
        String query = "select id, value, lang" +
        		" from results where id in (select subject from vw_search where model = '" + model + "' and";
        for (int i=0; i < searchStringsWithWildcards.length; i++)
        {
            if (i > 0)
            {
                query += " and";
            }
            query += " object ilike '" + searchStringsWithWildcards[i] + "'";
        }
        query += ") limit " + PropertyReader.getProperty("escidoc.cone.maximum.results") + ";";

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

    private String[] formatSearchString(String searchString)
    {
        String[] result = searchString.trim().split(" ");
        for (int i = 0; i < result.length; i++)
        {
            result[i] = "%" + result[i].replaceAll("\\*|%", "") + "%";
        }
        
        return result;
    }

    public Map<String, List<String>> details(String model, String id) throws Exception
    {
        return details(model, id, null);
    }


    public Map<String, List<String>> details(String model, String id, String language) throws Exception
    {
        id = escape(id);
        
        String query = "select distinct object, predicate from triples where " +
                "subject = '" + id + "'";
        
        
        if (language == null)
        {
            language = PropertyReader.getProperty("escidoc.cone.language.default");
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

    // TODO: Implement escaping for RDF ids
    private String escape(String id)
    {
        return id;
    }

}
