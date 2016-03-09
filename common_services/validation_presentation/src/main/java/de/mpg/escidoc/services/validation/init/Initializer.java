/*
 * CDDL HEADER START
 * 
 * The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with
 * the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or
 * http://www.escidoc.org/license. See the License for the specific language governing permissions
 * and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License
 * file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with
 * the fields enclosed by brackets "[]" replaced with your own identifying information: Portions
 * Copyright [yyyy] [name of copyright owner]
 * 
 * CDDL HEADER END
 */
/*
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */
package de.mpg.escidoc.services.validation.init;

import java.io.File;
import java.io.StringReader;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.jboss.vfs.VFS;
import org.jboss.vfs.VirtualFile;
import org.xml.sax.InputSource;

import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.util.PropertyReader;
import de.mpg.escidoc.services.util.ResourceUtil;
import de.mpg.escidoc.services.validation.ItemValidating;
import de.mpg.escidoc.services.validation.ItemValidatingBean;

/**
 * This class initializes the validation cache database. It should be deactivated when there is a
 * central validation schema repository.
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class Initializer extends Thread {

  /**
   * Logger for this class.
   */
  private static final Logger LOGGER = Logger.getLogger(Initializer.class);

  public static final String SCHEMA_DIRECTORY = "validation_schema";
  public static final String SQL_DIRECTORY = "validation_sql";


  private ItemValidating itemValidating;



  /**
   * Default constructor.
   */
  public Initializer(ItemValidating itemValidating) {
    this.itemValidating = itemValidating;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    initializeDatabase(itemValidating);
  }

  /**
   * This method executes the initialization.
   */
  public static void initializeDatabase(ItemValidating itemValidating) {
    LOGGER.info("Initializing validation database...");
    Connection conn = null;
    try {
      conn = getConnection();
      // executeSqlScript("delete_tables.sql", conn);
      try {
        executeSqlScript("create_structure.sql", conn);
        insertValidationData(conn);
      } catch (SQLException se) {
        LOGGER.debug("Error creating validation database", se);
        LOGGER.info("Validation database table structure already exists.");
        LOGGER.info("Skipping validation schema creation.");
      }



      // ItemValidating itemValidating = (ItemValidating)
      // ctx.lookup("java:global/pubman_ear/validation/ItemValidatingBean");


      itemValidating.refreshValidationSchemaCache();
      /*
       * String contextsXml = FrameworkUtil.getAllContexts(); LOGGER.debug("Contexts: " +
       * contextsXml); String[] contextSnippets = contextsXml.split("<context:context"); Pattern
       * objIdPattern = Pattern.compile("objid=\"([^\"]+)\""); Pattern namePattern =
       * Pattern.compile("<context:name>([^<]+)</context:name>"); for (String contextSnippet :
       * contextSnippets) { String objId = null; String name = null; Matcher matcher =
       * objIdPattern.matcher(contextSnippet); if (matcher.find()) { objId = matcher.group(1); }
       * matcher = namePattern.matcher(contextSnippet); if (matcher.find()) { name =
       * matcher.group(1); } LOGGER.debug("Context found: " + objId + ":" + name); PreparedStatement
       * statement = conn
       * .prepareStatement("SELECT * FROM escidoc_validation_schema WHERE context_name = ?");
       * PreparedStatement statement2 = conn .prepareStatement(
       * "UPDATE escidoc_validation_schema SET id_context_ref = ? WHERE id_context_ref = ?");
       * PreparedStatement statement3 = conn .prepareStatement(
       * "UPDATE escidoc_validation_schema_snippets SET id_context_ref = ? WHERE id_context_ref = ?"
       * ); statement.setString(1, name); ResultSet rs = statement.executeQuery(); if (rs.next()) {
       * String oldId = rs.getString("id_context_ref"); LOGGER.debug("Context found in database: " +
       * oldId + ":" + name); statement2.setString(1, objId); statement2.setString(2, oldId);
       * statement2.executeUpdate(); statement3.setString(1, objId); statement3.setString(2, oldId);
       * statement3.executeUpdate(); } else { LOGGER.debug("Context not found in database"); } }
       */
    } catch (Exception e) {
      LOGGER.error("Error initializing database", e);
    } finally {
      if (conn != null) {
        try {
          conn.close();
        } catch (Exception e) {
          LOGGER.error("Error closing database connection", e);
        }
      }
    }
    LOGGER.info("... validation database initialized!");
  }

  private static void executeSqlScript(final String scriptName, final Connection conn)
      throws Exception {
    String fullScriptName = SQL_DIRECTORY + "/" + scriptName;

    LOGGER.debug("fullScriptName:" + fullScriptName);

    String sql =
        ResourceUtil.getResourceAsString(fullScriptName, ItemValidating.class.getClassLoader());
    sql = replaceProperties(sql);
    LOGGER.debug("Executing script: " + sql);
    String[] commands = splitSqlScript(sql);
    for (String command : commands) {
      conn.createStatement().execute(command);
    }
  }

  /**
   * Initialize Connection to database.
   * 
   * @throws TechnicalException Any exception.
   */
  private static Connection getConnection() throws TechnicalException {
    try {
      Context ctx = new InitialContext();
      DataSource dataSource = (DataSource) ctx.lookup("java:jboss/datasources/Validation");
      return dataSource.getConnection();
    } catch (Exception e) {
      throw new TechnicalException(e);
    }
  }

  private static String[] splitSqlScript(final String sql) {
    return sql.replaceAll("\n--.*", "").split(";");
  }

  private static String replaceProperties(String sql) {
    Pattern pattern = Pattern.compile("\\#\\{[^\\}]+\\}");
    Matcher matcher = pattern.matcher(sql);
    int pos = 0;
    while (matcher.find(pos)) {
      String propertyName = matcher.group().substring(2, matcher.group().length() - 1);
      String propertyValue = null;
      try {
        propertyValue = PropertyReader.getProperty(propertyName);
      } catch (Exception e) {
        LOGGER.error("Error reading property", e);
      }
      if (propertyValue == null) {
        propertyValue = "$$$" + propertyName + "$$$";
      }
      sql = sql.replace(matcher.group(), propertyValue);
      matcher = pattern.matcher(sql);
    }
    return sql;
  }

  private static void insertValidationData(final Connection conn) throws Exception {
    URL dirUrl = Initializer.class.getClassLoader().getResource(SCHEMA_DIRECTORY);
    VirtualFile dirVirtFile = VFS.getChild(dirUrl.toURI());


    /*
     * String[] path = (URLDecoder.decode(dirUrl.getPath(), "UTF-8")).split("/|\\\\"); String
     * modifiedPath = ""; for (String pathElement : path) { if ("file:".equals(pathElement)) {
     * continue; } if (pathElement.endsWith("war!")) { int lastPoint = pathElement.lastIndexOf(".");
     * pathElement = pathElement.substring(0, lastPoint) + "-exp" + pathElement.substring(lastPoint,
     * pathElement.length() - 1); } modifiedPath += "/" + pathElement; } modifiedPath =
     * modifiedPath.substring(1); LOGGER.debug("Initial schema path: " + modifiedPath); File dir =
     * new File(modifiedPath);
     */
    insertSchemaDirectory(dirVirtFile, conn);
  }

  private static void insertSchemaDirectory(final VirtualFile dir, final Connection conn)
      throws Exception {
    // File[] schemas = dir.listFiles();

    List<VirtualFile> schemas = dir.getChildren();



    LOGGER.debug("Schemas found :" + schemas.size());
    for (VirtualFile schema : schemas) {
      if (schema.isDirectory()) {
        insertSchemaDirectory(schema, conn);
      } else {

        // String schemaContent = ResourceUtil.getResourceAsString(schema.getAbsolutePath(),
        // Initializer.class.getClassLoader());
        String schemaContent = ResourceUtil.getStreamAsString(schema.openStream());


        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        IdentityHandler idHandler = new IdentityHandler();
        parser.parse(new InputSource(new StringReader(schemaContent)), idHandler);
        if (idHandler.isIdentified()) {
          LOGGER.info("Adding validation schema to database: cm=" + idHandler.getContentModel()
              + "; cx=" + idHandler.getContext() + "(" + idHandler.getContextName() + ")" + "; mv"
              + idHandler.getMetadataVersion() + "; v=" + idHandler.getVersion());
          PreparedStatement statement =
              conn.prepareStatement("INSERT INTO escidoc_validation_schema ("
                  + "id_content_type_ref, " + "id_context_ref, " + "context_name, "
                  + "id_metadata_version_ref, " + "current_version, schema_content) "
                  + "VALUES (?, ?, ?, ?, ?, ?)");
          statement.setString(1, idHandler.getContentModel());
          statement.setString(2, idHandler.getContext());
          statement.setString(3, idHandler.getContextName());
          statement.setString(4, idHandler.getMetadataVersion());
          statement.setString(5, idHandler.getVersion());
          statement.setString(6, schemaContent);
          statement.executeUpdate();
        }
      }
    }
  }
}
