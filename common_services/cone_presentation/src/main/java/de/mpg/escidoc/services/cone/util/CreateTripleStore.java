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
package de.mpg.escidoc.services.cone.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.mulgara.itql.ItqlInterpreterBean;
import org.mulgara.query.Answer;

import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.framework.PropertyReader;

/**
 * Helper class to fill the Mulgara triple store with RDF data.
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class CreateTripleStore
{
    private static final String REGEX_BRACKETS = "<[^>]+>";
    private static final String EXECUTING = "Executing: ";
    private Logger logger = Logger.getLogger(CreateTripleStore.class);
    private String[] models = { "jnar", "lang" };
    private String mulgaraServer;
    private String mulgaraPort;

    /**
     * Main-Method.
     * 
     * @param args No arguments needed
     * @throws Exception Any exception
     */
    public static void main(String[] args) throws Exception
    {
        new CreateTripleStore();
    }

    private CreateTripleStore() throws Exception
    {
        mulgaraServer = PropertyReader.getProperty("escidoc.cone.mulgara.server.name");
        mulgaraPort = PropertyReader.getProperty("escidoc.cone.mulgara.server.port");
        File tqlFile = ResourceUtil.getResourceAsFile("setup.tql");
        BufferedReader reader = new BufferedReader(new FileReader(tqlFile));
        String line;
        while ((line = reader.readLine()) != null)
        {
            if (!line.trim().startsWith("#"))
            {
                ItqlInterpreterBean interpreter = new ItqlInterpreterBean();
                logger.debug(EXECUTING + line);
                interpreter.executeUpdate(line);
            }
        }
        for (String model : models)
        {
            List<String> pattern = new ArrayList<String>();
            File patternFile = ResourceUtil.getResourceAsFile(model + ".pattern");
            BufferedReader bufferedReader = new BufferedReader(new FileReader(patternFile));
            while ((line = bufferedReader.readLine()) != null)
            {
                pattern.add(line);
            }
            ItqlInterpreterBean interpreter = new ItqlInterpreterBean();
            String query = "select $s $p $o from <rmi://" + mulgaraServer + ":" + mulgaraPort + "/cone#" + model
                    + "> where $s $p $o order by $s $p $o limit 500;";
            logger.debug(EXECUTING + query);
            Answer answer = interpreter.executeQuery(query);
            boolean stillMore = true;
            int page = 0;
            String currentSubject = null;
            Map<String, String> poMap = new HashMap<String, String>();
            Set<String> languages = new HashSet<String>();
            while (stillMore)
            {
                stillMore = false;
                while (answer.next())
                {
                    stillMore = true;
                    String subject = answer.getObject(0).toString();
                    String predicate = answer.getObject(1).toString();
                    String objectString = answer.getObject(2).toString();
                    Pattern rePattern = Pattern.compile("^\"(.*)\"(@([a-z]+))?$");
                    Matcher matcher = rePattern.matcher(objectString);
                    String object = null;
                    String lang = null;
                    if (matcher.find())
                    {
                        object = matcher.group(1);
                        lang = matcher.group(3);
                    }
                    if (currentSubject != null && !currentSubject.equals(subject))
                    {
                        flushSubject(model, currentSubject, poMap, languages, pattern);
                        poMap = new HashMap<String, String>();
                        languages = new HashSet<String>();
                    }
                    currentSubject = subject;
                    poMap.put(predicate + (lang != null ? "@" + lang : ""), object);
                    if (lang != null)
                    {
                        languages.add(lang);
                    }
                }
                if (stillMore)
                {
                    page++;
                    query = "select $s $p $o from <rmi://" + mulgaraServer + ":" + mulgaraPort + "/cone#" + model
                            + "> where $s $p $o order by $s $p $o limit 500 offset " + page * 500 + ";";
                    logger.debug(EXECUTING + query);
                    interpreter = new ItqlInterpreterBean();
                    answer = interpreter.executeQuery(query);
                }
            }
        }
    }

    private void flushSubject(String model, String currentSubject, Map<String, String> poMap, Set<String> languages,
            List<String> pattern) throws Exception
    {
        if (languages.size() == 0)
        {
            String result = "";
            for (String line : pattern)
            {
                String string = line;
                for (String predicate : poMap.keySet())
                {
                    string = string.replace("<" + predicate + ">", poMap.get(predicate));
                }
                string = string.replaceAll(REGEX_BRACKETS, "");
                String newString = null;
                while (!string.equals(newString))
                {
                    newString = string;
                    string = replaceTokens(string);
                }
                if (string.startsWith(":"))
                {
                    string = "";
                }
                else if (string.contains(":"))
                {
                    string = string.substring(string.indexOf(":") + 1);
                }
                result += string;
            }
            String query = "insert <" + currentSubject + "> " + "<http://purl.org/dc/elements/1.1/title> " + "'"
                    + escapeForItqlObject(result) + "' " + "into <rmi://" + mulgaraServer + ":" + mulgaraPort
                    + "/cone#" + model + "_result>;";
            logger.debug("Query: " + query);
            ItqlInterpreterBean interpreter = new ItqlInterpreterBean();
            interpreter.executeUpdate(query);
        }
        else
        {
            for (String lang : languages)
            {
                String result = "";
                for (String line : pattern)
                {
                    String string = line;
                    for (String predicate : poMap.keySet())
                    {
                        if (predicate.endsWith("@" + lang))
                        {
                            string = string.replace("<" + predicate.substring(0, predicate.indexOf("@")) + ">", poMap
                                    .get(predicate));
                        }
                        else if (!predicate.contains("@"))
                        {
                            string = string.replace("<" + predicate + ">", poMap.get(predicate));
                        }
                    }
                    string = string.replaceAll(REGEX_BRACKETS, "");
                    String newString = null;
                    while (!string.equals(newString))
                    {
                        newString = string;
                        string = replaceTokens(string);
                    }
                    if (string.startsWith(":"))
                    {
                        string = "";
                    }
                    else if (string.contains(":"))
                    {
                        string = string.substring(string.indexOf(":") + 1);
                    }
                    result += string;
                }
                logger.debug("Result: " + result);
                String query = "insert <" + currentSubject + "> " + "<http://purl.org/dc/elements/1.1/title> " + "'"
                        + escapeForItqlObject(result) + "'@" + lang + " " + "into <rmi://" + mulgaraServer + ":"
                        + mulgaraPort + "/cone#" + model + "_result>;";
                logger.debug("Query: " + query);
                ItqlInterpreterBean interpreter = new ItqlInterpreterBean();
                interpreter.executeUpdate(query);
            }
        }
    }

    /**
     * @param string
     * @return
     */
    private String replaceTokens(String string)
    {
        string = string.replaceAll("AND\\{[^,\\}]+(,[^,\\}]+)*\\}", "a");
        string = string.replaceAll("AND\\{,[^\\}]*\\}|AND\\{[^\\}]*,\\}|AND\\{[^\\}]*,,[^\\}]*\\}", "");
        string = string.replaceAll("OR\\{[^\\}]*[^,\\}]+[^\\}]*\\}", "o");
        string = string.replaceAll("OR\\{,*\\}", "");
        string = string.replace("NOT\\{\\}", "n");
        string = string.replace("NOT\\{[^}]+\\}", "");
        return string;
    }

    private String escapeForItqlObject(String result)
    {
        return result.replace("'", "\\'");
    }
}
