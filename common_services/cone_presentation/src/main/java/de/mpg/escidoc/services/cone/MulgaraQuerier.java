package de.mpg.escidoc.services.cone;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.mulgara.itql.ItqlInterpreterBean;
import org.mulgara.itql.ItqlInterpreterException;
import org.mulgara.query.Answer;
import org.mulgara.query.TuplesException;

public class MulgaraQuerier implements Querier
{

    private static final Logger logger = Logger.getLogger(MulgaraQuerier.class);
    
    public Map<String, String> query(String model, String searchString) throws Exception
    {
        
        searchString = formatSearchString(searchString);
        
        String query = "select $s $o from <rmi://localhost:9099/cone#jnar> where " +
        		"$s <http://purl.org/dc/elements/1.1/title> $o and " +
        		"$s <http://purl.org/dc/elements/1.1/title> '" + searchString + "' " +
        		"in <rmi://localhost:9099/cone#jnar_title>;";
        
        logger.debug("query: " + query);
        
        ItqlInterpreterBean interpreter = new ItqlInterpreterBean();
        
        Answer answer = interpreter.executeQuery(query);

        Map<String, String> resultMap = new LinkedHashMap<String, String>();

        while (answer.next())
        {
            String subject = answer.getObject(0).toString();
            subject = subject.substring(1, subject.length() - 1);
            String object = answer.getObject(1).toString();
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
    
}
