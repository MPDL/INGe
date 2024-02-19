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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.cone.ModelList.Model;
import de.mpg.mpdl.inge.cone.ModelList.Predicate;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * SQL implementation for the {@link Querier} interface. Currently works with Postgres, but should
 * also work with other relational databases like HSQL, MySQL. For Oracle and SQL Server, maybe some
 * modifications will be needed.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class SQLQuerier implements Querier {
  private static final Logger logger = Logger.getLogger(SQLQuerier.class);
  private final Connection connection;
  protected boolean loggedIn;

  /**
   * Default constructor initializing the {@link DataSource}.
   *
   * @throws Exception Any exception.
   */
  public SQLQuerier() throws Exception {
    Class.forName(PropertyReader.getProperty(PropertyReader.INGE_CONE_DATABASE_DRIVER_CLASS));
    connection = DriverManager.getConnection(
        "jdbc:postgresql://" + PropertyReader.getProperty(PropertyReader.INGE_CONE_DATABASE_SERVER_NAME) + ":"
            + PropertyReader.getProperty(PropertyReader.INGE_CONE_DATABASE_SERVER_PORT) + "/"
            + PropertyReader.getProperty(PropertyReader.INGE_CONE_DATABASE_NAME),
        PropertyReader.getProperty(PropertyReader.INGE_CONE_DATABASE_USER_NAME),
        PropertyReader.getProperty(PropertyReader.INGE_CONE_DATABASE_USER_PASSWORD));
  }

  public List<? extends Describable> query(String modelName, String searchString, ModeType modeType) throws ConeException {
    return query(modelName, searchString, PropertyReader.getProperty(PropertyReader.INGE_CONE_LANGUAGE_DEFAULT), modeType);
  }

  public List<? extends Describable> query(String modelName, String searchString, String language, ModeType modeType) throws ConeException {
    return query(modelName, searchString, language, modeType,
        Integer.parseInt(PropertyReader.getProperty(PropertyReader.INGE_CONE_RESULTS_DEFAULT)));
  }

  public List<? extends Describable> query(String modelName, String searchString, String language, ModeType modeType, int limit)
      throws ConeException {
    if (modeType == ModeType.FAST) {
      return queryFast(modelName, searchString, language, limit);
    } else if (modeType == ModeType.FULL) {
      return queryFull(modelName, searchString, language, limit);
    } else {
      throw new ConeException("Mode " + modeType + " not supported.");
    }
  }

  public List<? extends Describable> query(String modelName, Pair<String>[] searchPairs, String language, ModeType modeType, int limit)
      throws ConeException {
    if (modeType == ModeType.FAST) {
      return queryFast(modelName, searchPairs, language, limit);
    } else if (modeType == ModeType.FULL) {
      return queryFull(modelName, searchPairs, language, limit);
    } else {
      throw new ConeException("Mode " + modeType + " not supported.");
    }
  }

  private List<? extends Describable> queryFast(String modelName, String searchString, String language, int limit) throws ConeException {
    String query = null;
    try {
      if (connection.isClosed()) {
        throw new ConeException("Connection was already closed.");
      }

      language = language.replace("'", "''");

      String[] searchStrings = formatSearchString(searchString);
      String subQuery = "matches.model = '" + modelName + "'";
      String order1 = "";
      String order2 = "";
      for (String string : searchStrings) {
        subQuery += " and";
        if (string.startsWith("\"") && string.endsWith("\"")) {
          subQuery += " ('|' || matches.value || '|') ilike '%|" + string.substring(1, string.length() - 1) + "|%'";
        } else {
          subQuery += " matches.value ilike '%" + string + "%'";
          order1 += "('|' || matches.value || '|') ilike '%|" + string + "|%' desc, ";
          order2 += "('|' || matches.value || '|') ilike '%|" + string + "%' desc, ('|' || matches.value || '|') ilike '% " + string
              + "%' desc, ";

        }
      }
      query = "select r1.id, r1.value, r1.lang, r1.type, r1.sort from results r1 inner join matches on r1.id = matches.id "
          + "where (r1.lang = matches.lang or (r1.lang is null and matches.lang is null)) and " + subQuery;

      if (!"*".equals(language)) {
        query += " and (r1.lang = '" + language + "' or (r1.lang is null and '" + language
            + "' not in (select lang from results r2 where r2.id = r1.id and lang is not null)))";
      }
      query += " order by " + order1 + order2 + "r1.sort, r1.value, r1.id";
      if (limit > 0) {
        query += " limit " + limit;
      }

      query += ";";
      // logger.info("CoNE query: " + query);

      Statement statement = connection.createStatement();
      ResultSet result = statement.executeQuery(query);
      List<Pair<LocalizedString>> resultSet = new ArrayList<>();
      while (result.next()) {
        String id = result.getString("id");
        String value = result.getString("value");
        String lang = result.getString("lang");
        String type = result.getString("type");
        String sortKey = result.getString("sort");
        Pair<LocalizedString> pair = new Pair<>(id, new ResultEntry(value, lang, type, sortKey));
        resultSet.add(pair);
      }

      result.close();
      statement.close();

      return resultSet;

    } catch (SQLException | ConeException e) {
      logger.error("CoNE query: " + query);
      throw new ConeException(e);
    }
  }

  private List<? extends Describable> queryFast(String modelName, Pair<String>[] searchPairs, String language, int limit)
      throws ConeException {
    String query = null;
    try {
      if (connection.isClosed()) {
        throw new ConeException("Connection was already closed.");
      }

      language = language.replace("'", "''");

      String[] subQueries = getSubqueries(modelName, searchPairs);

      String fromExtension = subQueries[0];
      String joinClause = subQueries[1];
      String subQuery = subQueries[2];
      String order1 = subQueries[3];
      String order2 = subQueries[4];

      int found = Integer.parseInt(subQueries[5]);
      if (found != searchPairs.length) {
        throw new ConeException("Invalid search parameters.");
      }

      query = "select distinct r1.*" + fromExtension + " from results r1 inner join triples triples0_0 on r1.id = triples0_0.subject "
          + joinClause + "where " + subQuery;

      if (!"*".equals(language)) {
        query += " and (r1.lang = '" + language + "' or (r1.lang is null and '" + language
            + "' not in (select lang from results r2 where r2.id = r1.id and lang is not null)))";
      }
      query += " order by " + order1 + order2 + "r1.sort, r1.value, r1.id";
      if (limit > 0) {
        query += " limit " + limit;
      }

      query += ";";
      // logger.info("CoNE query: " + query);

      Statement statement = connection.createStatement();
      ResultSet result = statement.executeQuery(query);
      List<Pair<LocalizedString>> resultSet = new ArrayList<>();
      while (result.next()) {
        String id = result.getString("id");
        String value = result.getString("value");
        String lang = result.getString("lang");
        String type = result.getString("type");
        String sortKey = result.getString("sort");
        Pair<LocalizedString> pair = new Pair<>(id, new ResultEntry(value, lang, type, sortKey));
        resultSet.add(pair);
      }

      result.close();
      statement.close();

      return resultSet;
    } catch (SQLException | ConeException e) {
      logger.error("CoNE query: " + query);
      StringBuilder sb = new StringBuilder();
      sb.append("Parameters: ");
      for (Pair<String> pair : searchPairs) {
        sb.append(pair.getKey());
        sb.append(":");
        sb.append(pair.getValue());
        sb.append(" / ");
      }
      logger.error(sb.toString());

      throw new ConeException(e);
    }
  }

  private List<? extends Describable> queryFull(String modelName, String searchString, String language, int limit) throws ConeException {
    String query = null;
    try {
      if (connection.isClosed()) {
        throw new ConeException("Connection was already closed.");
      }

      language = language.replace("'", "''");

      String[] searchStrings = formatSearchString(searchString);
      String subQuery = "model = '" + modelName + "'";
      String order1 = "";
      String order2 = "";
      for (String string : searchStrings) {
        subQuery += " and";
        if (string.startsWith("\"") && string.endsWith("\"")) {
          subQuery += " ('|' || matches.value || '|') ilike '%|" + string.substring(1, string.length() - 1) + "|%'";
        } else {
          subQuery += " matches.value ilike '%" + string + "%'";
          order1 += "('|' || matches.value || '|') ilike '%|" + string + "|%' desc, ";
          order2 += "('|' || matches.value || '|') ilike '%|" + string + "%' desc, ('|' || matches.value || '|') ilike '% " + string
              + "%' desc, ";
        }
      }
      query = "select r1.id, r1.value, r1.lang from results r1 inner join matches on r1.id = matches.id "
          + "where (r1.lang = matches.lang or (r1.lang is null and matches.lang is null)) and " + subQuery;
      if (!"*".equals(language)) {
        query += " and (r1.lang = '" + language + "' or (r1.lang is null and '" + language
            + "' not in (select lang from results r2 where r2.id = r1.id and lang is not null)))";
      }
      query += " order by " + order1 + order2 + "r1.value, r1.id";

      if (limit > 0) {
        query += " limit " + limit;
      }

      query += ";";
      // logger.info("CoNE query: " + query);

      Statement statement = connection.createStatement();
      ResultSet result = statement.executeQuery(query);
      List<TreeFragment> resultSet = new ArrayList<>();
      while (result.next()) {
        String id = result.getString("id");
        TreeFragment treeFragment = details(modelName, id, language);
        resultSet.add(treeFragment);
      }

      result.close();
      statement.close();

      return resultSet;

    } catch (SQLException | ConeException e) {
      logger.error("CoNE query: " + query);
      throw new ConeException(e);
    }
  }

  private List<? extends Describable> queryFull(String modelName, Pair<String>[] searchPairs, String language, int limit)
      throws ConeException {
    String query = null;
    try {
      if (connection.isClosed()) {
        throw new ConeException("Connection was already closed.");
      }

      language = language.replace("'", "''");

      String[] subQueries = getSubqueries(modelName, searchPairs);

      String fromExtension = subQueries[0];
      String joinClause = subQueries[1];
      String subQuery = subQueries[2];
      String order1 = subQueries[3];
      String order2 = subQueries[4];

      int found = Integer.parseInt(subQueries[5]);
      if (found != searchPairs.length) {
        throw new ConeException("Invalid search parameters.");
      }

      query = "select distinct r1.*" + fromExtension + " from results r1 inner join triples triples0_0 on r1.id = triples0_0.subject "
          + joinClause + "where " + subQuery;

      if (!"*".equals(language)) {
        query += " and (r1.lang = '" + language + "' or (r1.lang is null and '" + language
            + "' not in (select lang from results r2 where r2.id = r1.id and lang is not null)))";
      }
      query += " order by " + order1 + order2 + "r1.value, r1.id";
      if (limit > 0) {
        query += " limit " + limit;
      }

      query += ";";
      // logger.info("CoNE query: " + query);

      Statement statement = connection.createStatement();
      ResultSet result = statement.executeQuery(query);
      List<TreeFragment> resultSet = new ArrayList<>();
      while (result.next()) {
        String id = result.getString("id");
        TreeFragment treeFragment = details(modelName, id, language);
        resultSet.add(treeFragment);
      }

      result.close();
      statement.close();

      return resultSet;

    } catch (SQLException | ConeException e) {
      logger.error("CoNE query: " + query);
      StringBuilder sb = new StringBuilder();
      sb.append("Parameters: ");
      for (Pair<String> pair : searchPairs) {
        sb.append(pair.getKey());
        sb.append(":");
        sb.append(pair.getValue());
        sb.append(" / ");
      }
      logger.error(sb.toString());

      throw new ConeException(e);
    }
  }

  private String[] getSubqueries(String modelName, Pair<String>[] searchPairs) throws ConeException {
    return getSubqueries(modelName, searchPairs, 0);
  }

  private String[] getSubqueries(String modelName, Pair<String>[] searchPairs, int level) throws ConeException {
    return getSubqueries(modelName, searchPairs, null, level, 0);
  }

  private String[] getSubqueries(String modelName, Pair<String>[] searchPairs, Predicate parentPredicate, int level, int counter)
      throws ConeException {
    String fromExtension = "";
    String subQuery = "";
    String joinClause = "";
    String order1 = "";
    String order2 = "";
    int found = 0;
    boolean first = true;

    for (Pair<String> pair : searchPairs) {
      String table = "triples" + counter + "_" + level;

      if (first) {
        first = false;
      } else {
        counter++;
        table = "triples" + counter + "_" + level;
        joinClause += " inner join triples " + table + " on triples" + (counter - 1) + "_" + level + ".subject = " + table + ".subject ";
      }

      if (counter > 0 || level > 0) {
        subQuery += " and ";
      }

      if (modelName == null) {
        subQuery += table + ".model is null ";
      } else {
        subQuery += table + ".model = '" + modelName + "' ";
      }

      String key = pair.getKey();
      if (key.matches("^[a-zA-Z0-9]+:.+")) {
        String prefix = key.substring(0, key.indexOf(":"));
        for (String namespace : ModelList.getInstance().getDefaultNamepaces().keySet()) {
          if (ModelList.getInstance().getDefaultNamepaces().get(namespace).equals(prefix)) {
            key = key.replaceFirst("^[a-zA-Z0-9]+:", namespace);
            break;
          }
        }
      }

      List<Predicate> predicateList = null;
      if (modelName != null) {
        predicateList = ModelList.getInstance().getModelByAlias(modelName).getPredicates();
      } else {
        predicateList = parentPredicate.getPredicates();
      }

      for (Predicate predicate : predicateList) {
        if (key.equals(predicate.getId())) {
          subQuery += " and " + table + ".predicate = '" + key + "' ";
          String[] results = formatSearchString(pair.getValue());
          for (String result : results) {
            if (result.startsWith("\"") && result.endsWith("\"")) {
              subQuery += " and " + table + ".object ilike '" + result.substring(1, result.length() - 1) + "'";
            } else {
              subQuery += " and " + table + ".object ilike '%" + result + "%'";
            }
          }
          if (!pair.getValue().startsWith("\"") || !pair.getValue().endsWith("\"")) {
            order1 += "r1.value ilike '" + pair.getValue() + "' desc, ";
            fromExtension += ", r1.value ilike '" + pair.getValue() + "'";

            order2 += "r1.value ilike '" + pair.getValue() + "%' desc, " + "r1.value ilike '% " + pair.getValue() + "%' desc, "
                + "r1.value ilike '%" + pair.getValue() + "%' desc, ";
            fromExtension += ", r1.value ilike '" + pair.getValue() + "%'" + ", r1.value ilike '% " + pair.getValue() + "%'"
                + ", r1.value ilike '%" + pair.getValue() + "%'";
          }
          found += 1;
          break;
        } else if (key.startsWith(predicate.getId())) {
          String[] subResult;
          if (predicate.isResource()) {
            String subModelName = predicate.getResourceModel();
            Pair<String> subPair = new Pair<>(key.replaceFirst(predicate.getId() + "/", ""), pair.getValue());
            joinClause += " inner join triples triples" + counter + "_" + (level + 1) + " on " + table + ".object = triples" + counter + "_"
                + (level + 1) + ".subject ";
            subQuery += " and " + table + ".predicate = '" + predicate.getId() + "' ";
            subResult = getSubqueries(subModelName, new Pair[] {subPair}, null, level + 1, counter);
          } else {
            Pair<String> subPair = new Pair<>(key.replaceFirst(predicate.getId() + "/", ""), pair.getValue());
            joinClause += " inner join triples triples" + counter + "_" + (level + 1) + " on " + table + ".object = triples" + counter + "_"
                + (level + 1) + ".subject ";
            subQuery += " and " + table + ".predicate = '" + predicate.getId() + "' ";
            subResult = getSubqueries(null, new Pair[] {subPair}, predicate, level + 1, counter);
          }

          fromExtension += subResult[0];
          joinClause += subResult[1];
          subQuery += subResult[2];
          order1 += subResult[3];
          order2 += subResult[4];
          found += Integer.parseInt(subResult[5]);
        }
      }
    }

    return new String[] {fromExtension, joinClause, subQuery, order1, order2, String.valueOf(found)};
  }

  private String[] formatSearchString(String searchString) {
    searchString = searchString.replace("'", "''").replace('*', '%').trim();

    ArrayList<String> list = new ArrayList<>();

    Pattern pattern = Pattern.compile("(\"[^\"]*\")");
    Matcher matcher = pattern.matcher(searchString);
    int start = 0;
    while (start < searchString.length() && matcher.find(start)) {
      if (start < matcher.start() && !searchString.substring(start, matcher.start()).trim().isEmpty()) {
        list.addAll(Arrays.asList(searchString.substring(start, matcher.start()).split(" ")));
      }
      list.add(matcher.group(1));
      start = matcher.end();
    }
    if (start < searchString.length()) {
      list.addAll(Arrays.asList(searchString.substring(start).split(" ")));
    }

    return list.toArray(new String[] {});
  }

  public TreeFragment details(String modelName, String id) throws ConeException {
    return details(modelName, id, null);
  }

  public TreeFragment details(String modelName, String id, String language) throws ConeException {
    try {
      if (connection.isClosed()) {
        throw new ConeException("Connection was already closed.");
      }

      if (modelName != null) {
        Stack<String> idStack = new Stack<>();
        idStack.push(id);

        TreeFragment result = details(modelName, id, language, idStack, connection);

        return result;
      } else {
        throw new ConeException("Model name not provided");
      }
    } catch (SQLException e) {
      throw new ConeException(e);
    }
  }

  public TreeFragment details(String modelName, String id, String language, Stack<String> idStack, Connection connection)
      throws ConeException {
    Model model = ModelList.getInstance().getModelByAlias(modelName);

    return details(modelName, model.getPredicates(), id, language, idStack, connection);
  }

  public TreeFragment details(String modelName, List<Predicate> predicates, String id, String language, Stack<String> idStack,
      Connection connection) throws ConeException {
    String query = null;
    try {
      if (connection.isClosed()) {
        throw new ConeException("Connection was already closed.");
      }

      id = escape(id);
      query = "select distinct object, predicate, lang from triples where ";

      if (modelName != null) {
        query += " model = '" + modelName + "' and";
      }

      query += " subject = '" + id + "'";

      if (!"*".equals(language)) {
        query += " and (lang is null or lang = '" + language + "')";
      }
      Statement statement = connection.createStatement();
      ResultSet result = statement.executeQuery(query);
      TreeFragment resultMap = new TreeFragment(id);
      while (result.next()) {
        String predicateValue = result.getString("predicate");
        String object = result.getString("object");

        // Redirect?
        if ("http://www.w3.org/2002/07/owl#sameAs".equals(predicateValue)) {
          return details(modelName, predicates, object, language, idStack, connection);
        }

        String lang = result.getString("lang");

        LocalizedTripleObject localizedTripleObject = null;

        boolean found = false;
        for (Predicate predicate : predicates) {
          if (predicate.getId().equals(predicateValue)) {
            if (!predicate.isRestricted() || loggedIn) {
              if (predicate.isResource() && !(idStack.contains(object)) && predicate.isIncludeResource()) {
                idStack.push(object);
                localizedTripleObject = details(predicate.getResourceModel(), object, language, idStack, connection);
                idStack.pop();
                localizedTripleObject.setLanguage(lang);
              } else if (!predicate.isResource() && predicate.getPredicates() != null && !predicate.getPredicates().isEmpty()) {
                localizedTripleObject = details(null, predicate.getPredicates(), object, language, idStack, connection);
                localizedTripleObject.setLanguage(lang);
              } else {
                localizedTripleObject = new LocalizedString(object, lang);
              }
              found = true;

              if (resultMap.containsKey(predicateValue)) {
                resultMap.get(predicateValue).add(localizedTripleObject);
              } else {
                ArrayList<LocalizedTripleObject> newEntry = new ArrayList<>();
                newEntry.add(localizedTripleObject);
                resultMap.put(predicateValue, newEntry);
              }

              break;
            } else {
              found = true;
              break;
            }
          }
        }
        if (!found) {
          logger.error("Predicate '" + predicateValue + "' (subject = '" + id + "') not found in model '" + modelName + "'");
        }
      }

      result.close();
      statement.close();

      return resultMap;
    } catch (SQLException | ConeException e) {
      logger.error("CoNE query: " + query);
      throw new ConeException(e);
    }
  }

  /**
   * Returns a SQL safe representation of the given String.
   *
   * @param str The string that should be escaped
   * @return The escaped string
   */
  private String escape(String str) {
    return str.replace("'", "''");
  }

  public void create(String modelName, String id, TreeFragment values) throws ConeException {
    try {
      if (connection.isClosed()) {
        throw new ConeException("Connection was already closed.");
      }

      String query = "select count(subject) as cnt from triples where subject = ?";
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setString(1, id);

      ResultSet result = statement.executeQuery();
      if (result.next()) {
        int count = result.getInt("cnt");
        if (count > 0) {
          if (modelName != null) {
            throw new ConeException("Trying to create a resource that is already existing: " + modelName + " " + id);
          } else {
            // Won't update an existing resource linked from this resource
            result.close();
            statement.close();
            return;
          }
        }
      } else {
        throw new ConeException("Select count statement should always return a result, but did not.");
      }

      query = "insert into triples (subject, predicate, object, lang, model) values (?, ?,  ?, ?, ?)";

      result.close();
      statement.close();
      statement = connection.prepareStatement(query);

      for (String predicate : values.keySet()) {
        statement.setString(1, id);
        statement.setString(2, predicate);
        statement.setString(5, modelName);

        for (LocalizedTripleObject object : values.get(predicate)) {
          if (object instanceof LocalizedString && !"".equals(((LocalizedString) object).getValue())) {
            statement.setString(3, ((LocalizedString) object).getValue());
          } else if (object instanceof TreeFragment) {
            statement.setString(3, ((TreeFragment) object).getSubject());
            create(null, ((TreeFragment) object).getSubject(), (TreeFragment) object);
          } else {
            continue;
          }

          if (object.getLanguage() == null || "".equals(object.getLanguage())) {
            statement.setString(4, null);
          } else {
            statement.setString(4, object.getLanguage());
          }
          statement.executeUpdate();
        }
      }

      if (modelName != null) {
        query = "insert into results (id, value, lang, type, sort) values (?, ?, ?, ?, ?)";
        statement.close();
        statement = connection.prepareStatement(query);

        statement.setString(1, id);

        List<Pair<ResultEntry>> results = ModelHelper.buildObjectFromPatternNew(modelName, id, values, loggedIn);

        for (Pair<ResultEntry> pair : results) {
          if (pair.getValue() != null) {
            statement.setString(2, pair.getValue().getValue());

            if (pair.getValue().getLanguage() != null && "".equals(pair.getValue().getLanguage())) {
              statement.setString(3, null);
            } else {
              statement.setString(3, pair.getValue().getLanguage());
            }

            statement.setString(4, pair.getValue().getType());
            statement.setString(5, pair.getValue().getSortResult());

            statement.executeUpdate();
          }
        }

        query = "insert into matches (id, value, lang, model) values (?, ?, ?, ?)";
        statement.close();
        statement = connection.prepareStatement(query);

        statement.setString(1, id);
        statement.setString(4, modelName);

        List<Pair<LocalizedString>> matchResults = ModelHelper.buildMatchStringFromModel(modelName, id, values, loggedIn);

        for (Pair<LocalizedString> pair : matchResults) {
          if (pair.getValue() != null) {
            statement.setString(2, pair.getValue().getValue());
            if (pair.getKey() != null && "".equals(pair.getKey())) {
              statement.setString(3, null);
            } else {
              statement.setString(3, pair.getKey());
            }
            statement.executeUpdate();
          }
        }
      }
      statement.close();
    } catch (SQLException e) {
      throw new ConeException(e);
    }
  }

  public void delete(String modelName, String id) throws ConeException {
    Model model = ModelList.getInstance().getModelByAlias(modelName);
    List<Predicate> predicates = model.getPredicates();
    delete(predicates, id);
  }

  public void delete(List<Predicate> predicates, String id) throws ConeException {
    try {
      if (connection.isClosed()) {
        throw new ConeException("Connection was already closed.");
      }

      String query = "select distinct object from triples where subject = ? and predicate = ?";
      PreparedStatement statement = connection.prepareStatement(query);

      for (Predicate predicate : predicates) {
        if (!predicate.isResource() && predicate.getPredicates() != null && !predicate.getPredicates().isEmpty()) {

          statement.setString(1, id);
          statement.setString(2, predicate.getId());

          ResultSet result = statement.executeQuery();

          while (result.next()) {
            String subId = result.getString("object");
            delete(predicate.getPredicates(), subId);
          }

          result.close();
        }
      }

      statement.close();
      query = "delete from triples where subject = ?";
      statement = connection.prepareStatement(query);
      statement.setString(1, id);
      statement.executeUpdate();

      statement.close();
      query = "delete from results where id = ?";
      statement = connection.prepareStatement(query);
      statement.setString(1, id);
      statement.executeUpdate();

      statement.close();
      query = "delete from matches where id = ?";
      statement = connection.prepareStatement(query);
      statement.setString(1, id);
      statement.executeUpdate();

      statement.close();

    } catch (SQLException e) {
      throw new ConeException(e);
    }

  }

  public synchronized String createUniqueIdentifier(String modelName) throws ConeException {
    try {
      if (connection.isClosed()) {
        throw new ConeException("Connection was already closed.");
      }

      String query = "select value from properties where name = 'max_id'";
      Statement statement = connection.createStatement();
      ResultSet result = statement.executeQuery(query);

      if (result.next()) {
        String maxIdAsString = result.getString("value");
        int maxId = Integer.parseInt(maxIdAsString) + 1;

        query = "update properties set value = '" + maxId + "' where name = 'max_id'";
        statement.executeUpdate(query);

        String uid;
        if (modelName == null) {
          uid = "genid:" + maxId;
        } else {
          uid = modelName + "/resource/" + modelName + maxId;
        }

        result.close();
        query = "select * from triples where subject = '" + uid + "' limit 1";
        result = statement.executeQuery(query);

        if (result.next()) {
          result.close();
          statement.close();
          return createUniqueIdentifier(modelName);
        } else {
          result.close();
          statement.close();
          return uid;
        }
      } else {
        result.close();
        statement.close();

        throw new ConeException("'max_id not found in properties table'");
      }
    } catch (SQLException e) {
      throw new ConeException(e);
    }
  }

  public List<String> getAllIds(String modelName) throws ConeException {
    return getAllIds(modelName, 0);
  }

  public List<String> getAllIds(String modelName, int hits) throws ConeException {
    try {
      if (connection.isClosed()) {
        throw new ConeException("Connection was already closed.");
      }

      String query = "select distinct subject from triples where model = ?";
      if (hits > 0) {
        query += " limit ?";
      }
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setString(1, modelName);
      if (hits > 0) {
        statement.setInt(2, hits);
      }
      ResultSet result = statement.executeQuery();
      List<String> results = new ArrayList<>();
      while (result.next()) {
        results.add(result.getString("subject"));
      }

      result.close();
      statement.close();

      return results;
    } catch (SQLException e) {
      throw new ConeException(e);
    }
  }

  @Override
  protected void finalize() throws Throwable {
    super.finalize();
    connection.close();
  }

  public void release() throws ConeException {
    try {
      connection.close();
    } catch (SQLException e) {
      throw new ConeException(e);
    }
  }

  public void setLoggedIn(boolean loggedIn) {
    this.loggedIn = loggedIn;
  }

  public boolean getLoggedIn() {
    return this.loggedIn;
  }

  public void cleanup() throws ConeException {
    try {
      if (connection.isClosed()) {
        throw new ConeException("Connection was already closed.");
      }

      String query =
          "delete from matches m1 where m1.id in ( select id from matches left join triples on id = subject where subject is null)";
      PreparedStatement statement = connection.prepareStatement(query);
      statement.executeUpdate();
      statement.close();

      query = "delete from results r1 where r1.id in ( select id from results left join triples on id = subject where subject is null)";
      statement = connection.prepareStatement(query);
      statement.executeUpdate();

      statement.close();

    } catch (SQLException e) {
      throw new ConeException(e);
    }
  }
}
