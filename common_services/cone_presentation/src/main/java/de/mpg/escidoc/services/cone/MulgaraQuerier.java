package de.mpg.escidoc.services.cone;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.mulgara.itql.ItqlInterpreterBean;
import org.mulgara.query.Answer;

import de.mpg.escidoc.services.cone.util.Pair;
import de.mpg.escidoc.services.cone.util.Triple;
import de.mpg.escidoc.services.framework.PropertyReader;

public class MulgaraQuerier implements Querier
{

    private static final Logger logger = Logger.getLogger(MulgaraQuerier.class);
    
    
    
    public Set<Pair> query(String model, String query) throws Exception
    {
        return query(model, query, null);
    }

    public Set<Pair> query(String model, String searchString, String language) throws Exception
    {
        
        if (language == null)
        {
            language = PropertyReader.getProperty("escidoc.cone.language.default");
        }
        
        String searchStringWithWildcards = formatSearchString(searchString);
        
        String mulgaraServer = PropertyReader.getProperty("escidoc.cone.mulgara.server.name");
        String mulgaraPort = PropertyReader.getProperty("escidoc.cone.mulgara.server.port");
        
        String query = "select $s $o from <rmi://" + mulgaraServer + ":" + mulgaraPort + "/cone#" + model + "_result> where " +
                "$s $p $o and (" +
                "$s $p '" + searchStringWithWildcards + "' " +
                "in <rmi://" + mulgaraServer + ":" + mulgaraPort + "/cone#" + model + "_fulltext>);";

        logger.debug("query: " + query);
        
        ItqlInterpreterBean interpreter = new ItqlInterpreterBean();
        
        long now = new Date().getTime();
        Answer answer = interpreter.executeQuery(query);
        logger.debug("Took " + (new Date().getTime() - now) + " ms.");
        
        Set<Pair> resultSet = new LinkedHashSet<Pair>();

        String query2 = "";
        boolean found = false;
        
        while (answer.next())
        {
            String subject = answer.getObject(0).toString();
            String objectString = answer.getObject(1).toString();
            
            Pattern pattern = Pattern.compile("^\"(.*)\"(@([a-z]+))?$");
            Matcher matcher = pattern.matcher(objectString);
            String object = null;
            String lang = null;
            if (matcher.find())
            {
                object = matcher.group(1);
                lang = matcher.group(3);
            }
            if (lang == null || language == null || lang.equals(language))
            {
                resultSet.add(new Pair(subject, object));
            }
        }

        logger.debug("Result: " + resultSet);

        return resultSet;
    }

    private String formatSearchString(String searchString)
    {
        searchString = searchString.trim().replaceAll("\\*", "") + "*";
        
        return searchString;
    }

    public Set<Triple> details(String model, String id) throws Exception
    {
        id = formatIdString(id);
        
        String mulgaraServer = PropertyReader.getProperty("escidoc.cone.mulgara.server.name");
        String mulgaraPort = PropertyReader.getProperty("escidoc.cone.mulgara.server.port");
        
        String query = "select $p $o from <rmi://" + mulgaraServer + ":" + mulgaraPort + "/cone#" + model + "> where " +
                "<" + id + "> $p $o;";
        
        logger.debug("query: " + query);
        
        ItqlInterpreterBean interpreter = new ItqlInterpreterBean();
        
        Answer answer = interpreter.executeQuery(query);

        Set<Triple> resultMap = new HashSet<Triple>();

        while (answer.next())
        {
            String predicate = answer.getObject(0).toString();
            //subject = subject.substring(1, subject.length() - 1);
            String objectString = answer.getObject(2).toString();
            
            Pattern pattern = Pattern.compile("^\"(.*)\"(@([a-z]+))?$");
            Matcher matcher = pattern.matcher(objectString);
            String object = null;
            String lang = null;
            if (matcher.find())
            {
                object = matcher.group(1);
                lang = matcher.group(3);
            }
            Triple triple = new Triple(id, predicate, object);
            resultMap.add(triple);
        }
        
        logger.info("Result: " + resultMap);

        return resultMap;
    }

    // TODO: Implement escaping for RDF ids
    private String formatIdString(String id)
    {
        return id;
    }

}
