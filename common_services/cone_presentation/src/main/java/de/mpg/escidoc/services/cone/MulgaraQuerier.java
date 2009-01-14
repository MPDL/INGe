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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.mulgara.itql.ItqlInterpreterBean;
import org.mulgara.query.Answer;

import de.mpg.escidoc.services.cone.util.LocalizedString;
import de.mpg.escidoc.services.cone.util.Pair;
import de.mpg.escidoc.services.framework.PropertyReader;

/**
 * Mulgara triple store implementation for the {@link Querier} interface.
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class MulgaraQuerier implements Querier
{
    private static final String REGEX_OBJECT_WITH_LANGUAGE = "^\"(.*)\"(@([a-z]+))?$";
    private static final String DATABASE_NAME = "/cone#";
    private static final Logger logger = Logger.getLogger(MulgaraQuerier.class);
    private String mulgaraServer;
    private String mulgaraPort;

    /**
     * Default constructor getting needed properties.
     * 
     * @throws Exception Any exception.
     */
    public MulgaraQuerier() throws Exception
    {
        mulgaraServer = PropertyReader.getProperty("escidoc.cone.mulgara.server.name");
        mulgaraPort = PropertyReader.getProperty("escidoc.cone.mulgara.server.port");
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
    public List<Pair> query(String model, String searchString, String language, int limit) throws Exception
    {
        if (language == null)
        {
            language = PropertyReader.getProperty("escidoc.cone.language.default");
        }
        String[] searchStringsWithWildcards = formatSearchString(searchString);
        String query = "select $s $o from <rmi://" + mulgaraServer + ":" + mulgaraPort + DATABASE_NAME + model
                + "_result> where " + "$s $p $o";
        for (String string : searchStringsWithWildcards)
        {
            query += " and $s $p '" + string + "' " + "in <rmi://" + mulgaraServer + ":" + mulgaraPort + DATABASE_NAME
                    + model + "_fulltext>";
        }
        
        if (limit > 0)
        {
            query += " limit " + limit;
        }

        query += ";";
        
        ItqlInterpreterBean interpreter = new ItqlInterpreterBean();
        long now = new Date().getTime();
        Answer answer = interpreter.executeQuery(query);
        logger.debug("Took " + (new Date().getTime() - now) + " ms.");
        List<Pair> resultSet = new ArrayList<Pair>();

        while (answer.next())
        {
            String subject = answer.getObject(0).toString();
            String objectString = answer.getObject(1).toString();
            Pattern pattern = Pattern.compile(REGEX_OBJECT_WITH_LANGUAGE);
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

        return resultSet;
    }

    private String[] formatSearchString(String searchString)
    {
        String[] result = searchString.trim().split(" ");
        for (int i = 0; i < result.length; i++)
        {
            result[i] = result[i].replaceAll("\\*", "") + "*";
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
        id = formatIdString(id);
        String query = "select $p $o from <rmi://" + mulgaraServer 
                + ":" + mulgaraPort + DATABASE_NAME + model + "> where "
                + "<" + id + "> $p $o;";
        logger.debug("query: " + query);
        ItqlInterpreterBean interpreter = new ItqlInterpreterBean();
        Answer answer = interpreter.executeQuery(query);
        Map<String, List<LocalizedString>> resultMap = new HashMap<String, List<LocalizedString>>();
        while (answer.next())
        {
            String predicate = answer.getObject(0).toString();
            // subject = subject.substring(1, subject.length() - 1);
            String objectString = answer.getObject(2).toString();
            Pattern pattern = Pattern.compile(REGEX_OBJECT_WITH_LANGUAGE);
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
        }
        logger.info("Result: " + resultMap);
        return resultMap;
    }

    // TODO: Implement escaping for RDF ids
    private String formatIdString(String id)
    {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    public List<Pair> query(String model, String query, String language) throws Exception
    {
        String limitString = PropertyReader.getProperty("escidoc.cone.maximum.results");
        return query(model, query, null, Integer.parseInt(limitString));
    }

    public void create(String model, String id, Map<String, List<LocalizedString>> values) throws Exception
    {
        // TODO MF: Implement
    }

    public void delete(String model, String id) throws Exception
    {
        // TODO MF: Implement
    }

    public String createUniqueIdentifier(String model) throws Exception
    {
        // TODO MF: Implement
        return null;
    }

}
