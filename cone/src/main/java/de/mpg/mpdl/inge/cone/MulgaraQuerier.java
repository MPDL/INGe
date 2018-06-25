/*
 * CDDL HEADER START The contents of this file are subject to the terms of the Common Development
 * and Distribution License, Version 1.0 only (the "License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the license at license/ESCIDOC.LICENSE or
 * http://www.escidoc.org/license. See the License for the specific language governing permissions
 * and limitations under the License. When distributing Covered Code, include this CDDL HEADER in
 * each file and include the License file at license/ESCIDOC.LICENSE. If applicable, add the
 * following below this CDDL HEADER, with the fields enclosed by brackets "[]" replaced with your
 * own identifying information: Portions Copyright [yyyy] [name of copyright owner] CDDL HEADER END
 */
/*
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */
package de.mpg.mpdl.inge.cone;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.mulgara.itql.ItqlInterpreterBean;
import org.mulgara.itql.ItqlInterpreterException;
import org.mulgara.query.Answer;
import org.mulgara.query.TuplesException;

import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * Mulgara triple store implementation for the {@link Querier} interface.
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class MulgaraQuerier implements Querier {
  private static final String REGEX_OBJECT_WITH_LANGUAGE = "^\"(.*)\"(@([a-z]+))?$";
  private static final String DATABASE_NAME = "/cone#";
  private static final Logger logger = Logger.getLogger(MulgaraQuerier.class);
  private String mulgaraServer;
  private String mulgaraPort;
  protected boolean loggedIn;

  /**
   * Default constructor getting needed properties.
   * 
   * @throws Exception Any exception.
   */
  public MulgaraQuerier() throws Exception {
    mulgaraServer = PropertyReader.getProperty(PropertyReader.INGE_CONE_MULGARA_SERVER_NAME);
    mulgaraPort = PropertyReader.getProperty(PropertyReader.INGE_CONE_MULGARA_SERVER_PORT);
  }

  /**
   * {@inheritDoc}
   */
  public List<Pair> query(String model, String query, ModeType modeType) throws ConeException {
    return query(model, query, null);
  }

  /**
   * {@inheritDoc}
   */
  public List<Pair> query(String model, String searchString, String language, ModeType modeType, int limit) throws ConeException {
    if (language == null) {
      language = PropertyReader.getProperty(PropertyReader.INGE_CONE_LANGUAGE_DEFAULT, "en");
    }
    String[] searchStringsWithWildcards = formatSearchString(searchString);
    String query = "select $s $o from <rmi://" + mulgaraServer + ":" + mulgaraPort + DATABASE_NAME + model + "_result> where " + "$s $p $o";
    for (String string : searchStringsWithWildcards) {
      query += " and $s $p '" + string + "' " + "in <rmi://" + mulgaraServer + ":" + mulgaraPort + DATABASE_NAME + model + "_fulltext>";
    }

    if (limit > 0) {
      query += " limit " + limit;
    }

    query += ";";

    ItqlInterpreterBean interpreter = new ItqlInterpreterBean();
    Answer answer = null;
    List<Pair> resultSet = new ArrayList<Pair>();

    try {
      answer = interpreter.executeQuery(query);
      while (answer.next()) {
        String subject = answer.getObject(0).toString();
        String objectString = answer.getObject(1).toString();
        Pattern pattern = Pattern.compile(REGEX_OBJECT_WITH_LANGUAGE);
        Matcher matcher = pattern.matcher(objectString);
        String object = null;
        String lang = null;
        if (matcher.find()) {
          object = matcher.group(1);
          lang = matcher.group(3);
        }
        if (lang == null || language == null || lang.equals(language)) {
          resultSet.add(new Pair(subject, object));
        }
      }
    } catch (TuplesException | ItqlInterpreterException e) {
      e.printStackTrace();
    }

    return resultSet;
  }

  /**
   * {@inheritDoc}
   */
  public List<Pair> query(String model, Pair[] searchFields, String language, ModeType modeType) throws ConeException {
    String limitString;

    limitString = PropertyReader.getProperty(PropertyReader.INGE_CONE_MAXIMUM_RESULTS);
    if (limitString == null) {
      limitString = "50";
    }
    return query(model, searchFields, language, modeType, Integer.parseInt(limitString));

  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.cone.Querier#query(java.lang.String, de.mpg.mpdl.inge.cone.util.Pair[],
   * java.lang.String, int)
   */
  public List<Pair> query(String model, Pair[] searchFields, String lang, ModeType modeType, int limit) throws ConeException {
    return null;
  }

  private String[] formatSearchString(String searchString) {
    String[] result = searchString.trim().split(" ");
    for (int i = 0; i < result.length; i++) {
      result[i] = result[i].replaceAll("\\*", "") + "*";
    }
    return result;
  }

  /**
   * {@inheritDoc}
   */
  public TreeFragment details(String model, String id) throws ConeException {
    return details(model, id, null);
  }

  /**
   * {@inheritDoc}
   */
  public TreeFragment details(String model, String id, String language) throws ConeException {

    try {
      TreeFragment resultMap = new TreeFragment(id);
      id = formatIdString(id);
      String query =
          "select $p $o from <rmi://" + mulgaraServer + ":" + mulgaraPort + DATABASE_NAME + model + "> where " + "<" + id + "> $p $o;";
      logger.debug("query: " + query);
      ItqlInterpreterBean interpreter = new ItqlInterpreterBean();
      Answer answer = interpreter.executeQuery(query);
      while (answer.next()) {
        String predicate = answer.getObject(0).toString();
        // subject = subject.substring(1, subject.length() - 1);
        String objectString = answer.getObject(2).toString();
        Pattern pattern = Pattern.compile(REGEX_OBJECT_WITH_LANGUAGE);
        Matcher matcher = pattern.matcher(objectString);
        String object = null;
        String lang = null;
        if (matcher.find()) {
          object = matcher.group(1);
          lang = matcher.group(3);
        }
        if (lang == null || language == null || lang.equals(language)) {
          if (resultMap.containsKey(predicate)) {
            resultMap.get(predicate).add(new LocalizedString(object, lang));
          } else {
            ArrayList<LocalizedTripleObject> newEntry = new ArrayList<LocalizedTripleObject>();
            newEntry.add(new LocalizedString(object, lang));
            resultMap.put(predicate, newEntry);
          }
        }
      }
      logger.info("Result: " + resultMap);
      return resultMap;
    } catch (ItqlInterpreterException | TuplesException e) {
      throw new ConeException(e);
    }
  }

  // TODO: Implement escaping for RDF ids
  private String formatIdString(String id) {
    return id;
  }

  /**
   * {@inheritDoc}
   */
  public List<Pair> query(String model, String query, String language, ModeType modeType) throws ConeException {

    String limitString = PropertyReader.getProperty(PropertyReader.INGE_CONE_MAXIMUM_RESULTS, "50");
    return query(model, query, null, modeType, Integer.parseInt(limitString));

  }

  /**
   * {@inheritDoc}
   */
  public void create(String model, String id, TreeFragment values) throws ConeException {
    // TODO MF: Implement
  }

  /**
   * {@inheritDoc}
   */
  public void delete(String model, String id) throws ConeException {
    // TODO MF: Implement
  }

  /**
   * {@inheritDoc}
   */
  public String createUniqueIdentifier(String model) throws ConeException {
    // TODO MF: Implement
    return null;
  }

  /**
   * {@inheritDoc}
   */
  public List<String> getAllIds(String modelName) throws ConeException {
    // TODO MF: Implement
    return null;
  }

  /**
   * {@inheritDoc}
   */
  public List<String> getAllIds(String modelName, int hits) throws ConeException {
    // TODO MF: Implement
    return null;
  }

  /**
   * {@inheritDoc}
   */
  public void release() throws ConeException {
    // TODO MF: Implement
  }

  public void setLoggedIn(boolean loggedIn) {
    this.loggedIn = loggedIn;
  }

  public boolean getLoggedIn() {
    return this.loggedIn;
  }

  public void cleanup() throws ConeException {
    // TODO Auto-generated method stub

  }

}
