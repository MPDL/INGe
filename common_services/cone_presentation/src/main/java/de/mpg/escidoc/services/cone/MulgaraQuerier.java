package de.mpg.escidoc.services.cone;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.mulgara.itql.ItqlInterpreterBean;
import org.mulgara.query.Answer;

import de.mpg.escidoc.services.framework.PropertyReader;

public class MulgaraQuerier implements Querier
{

    private static final Logger logger = Logger.getLogger(MulgaraQuerier.class);
    
    public Map<String, String> query(String model, String searchString) throws Exception
    {
        
        searchString = formatSearchString(searchString);
        
        String mulgaraServer = PropertyReader.getProperty("escidoc.cone.mulgara.server.name");
        String mulgaraPort = PropertyReader.getProperty("escidoc.cone.mulgara.server.port");
        
        String query = "select $s $o from <rmi://" + mulgaraServer + ":" + mulgaraPort + "/cone#" + model + "> where " +
        		"$s <http://purl.org/dc/elements/1.1/title> $o and " +
        		"$s <http://purl.org/dc/elements/1.1/title> '" + searchString + "' " +
        		"in <rmi://" + mulgaraServer + ":" + mulgaraPort + "/cone#" + model + "_title>;";
        
        logger.debug("query: " + query);
        
        ItqlInterpreterBean interpreter = new ItqlInterpreterBean();
        
        Answer answer = interpreter.executeQuery(query);

        Map<String, String> resultMap = new LinkedHashMap<String, String>();

        while (answer.next())
        {
            String subject = answer.getObject(0).toString();
            //subject = subject.substring(1, subject.length() - 1);
            String object = answer.getObject(1).toString();
            object = object.substring(1, object.length() - 1);
            resultMap.put(subject, object);
        }
        
        logger.info("Result: " + resultMap);

        return resultMap;
    }

    private String formatSearchString(String searchString)
    {
        searchString = searchString.trim().replaceAll("\\*", "") + "*";
        
        return searchString;
    }

    public Map<String, String> details(String model, String id) throws Exception
    {
        id = formatIdString(id);
        
        String mulgaraServer = PropertyReader.getProperty("escidoc.cone.mulgara.server.name");
        String mulgaraPort = PropertyReader.getProperty("escidoc.cone.mulgara.server.port");
        
        String query = "select $p $o from <rmi://" + mulgaraServer + ":" + mulgaraPort + "/cone#" + model + "> where " +
                "<" + id + "> $p $o;";
        
        logger.debug("query: " + query);
        
        ItqlInterpreterBean interpreter = new ItqlInterpreterBean();
        
        Answer answer = interpreter.executeQuery(query);

        Map<String, String> resultMap = new LinkedHashMap<String, String>();

        while (answer.next())
        {
            String predicate = answer.getObject(0).toString();
            //subject = subject.substring(1, subject.length() - 1);
            String object = answer.getObject(1).toString();
            resultMap.put(predicate, object);
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
