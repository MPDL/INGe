/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License"). You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE
 * or http://www.escidoc.de/license.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at license/ESCIDOC.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */
/*
 * Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */
package de.mpg.escidoc.services.validation.init;

import java.io.File;
import java.io.StringReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.util.FrameworkUtil;
import de.mpg.escidoc.services.util.ResourceUtil;
import de.mpg.escidoc.services.validation.ItemValidating;

/**
 * This class initializes the validation cache database. It should be deactivated when there is a central validation
 * schema repository.
 *
 * @author franke (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 131 $ $LastChangedDate: 2007-11-21 18:53:43 +0100 (Wed, 21 Nov 2007) $
 */
public class Initializer
{
    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(Initializer.class);
    /**
     * Validation EJB.
     */
    private static ItemValidating itemValidating;

    /**
     * Hidden constructor.
     */
    protected Initializer()
    {
    }

    /**
     * This method executes the initialization.
     */
    public static void initializeDatabase()
    {
        LOGGER.info("Initializing validation database...");
        Connection conn = null;
        try
        {
            conn = getConnection();
            executeSqlScript("delete_tables.sql", conn);
            executeSqlScript("create_structure.sql", conn);
            insertValidationData(conn);
            Context ctx = new InitialContext();
            itemValidating = (ItemValidating) ctx.lookup(ItemValidating.SERVICE_NAME);
            itemValidating.refreshValidationSchemaCache();
            String contextsXml = FrameworkUtil.getAllContexts();
            LOGGER.debug("Contexts: " + contextsXml);
            String[] contextSnippets = contextsXml.split("<context:context");
            Pattern objIdPattern = Pattern.compile("objid=\"([^\"]+)\"");
            Pattern namePattern = Pattern.compile("<context:name>([^<]+)</context:name>");
            for (String contextSnippet : contextSnippets)
            {
                String objId = null;
                String name = null;
                Matcher matcher = objIdPattern.matcher(contextSnippet);
                if (matcher.find())
                {
                    objId = matcher.group(1);
                }
                matcher = namePattern.matcher(contextSnippet);
                if (matcher.find())
                {
                    name = matcher.group(1);
                }
                LOGGER.debug("Context found: " + objId + ":" + name);
                PreparedStatement statement = conn
                        .prepareStatement("SELECT * FROM validation_schema WHERE context_name = ?");
                PreparedStatement statement2 = conn
                        .prepareStatement(
                                "UPDATE validation_schema SET id_context_ref = ? WHERE id_context_ref = ?");
                PreparedStatement statement3 = conn
                        .prepareStatement(
                                "UPDATE validation_schema_snippets SET id_context_ref = ? WHERE id_context_ref = ?");
                statement.setString(1, name);
                ResultSet rs = statement.executeQuery();
                if (rs.next())
                {
                    String oldId = rs.getString("id_context_ref");
                    LOGGER.debug("Context found in database: " + oldId + ":" + name);
                    statement2.setString(1, objId);
                    statement2.setString(2, oldId);
                    statement2.executeUpdate();
                    statement3.setString(1, objId);
                    statement3.setString(2, oldId);
                    statement3.executeUpdate();
                }
                else
                {
                    LOGGER.debug("Context not found in database");
                }
            }
        }
        catch (Exception e)
        {
            LOGGER.error("Error initializing database", e);
        }
        finally
        {
            if (conn != null)
            {
                try
                {
                    conn.close();
                }
                catch (Exception e)
                {
                    LOGGER.error("Error closing database connection", e);
                }
            }
        }
        LOGGER.info("... validation database initialized!");
    }

    private static void executeSqlScript(final String scriptName, final Connection conn) throws Exception
    {
        String sql = ResourceUtil.getResourceAsString("resource/sql/" + scriptName);
        sql = replaceProperties(sql);
        LOGGER.debug("Executing script: " + sql);
        String[] commands = splitSqlScript(sql);
        for (String command : commands)
        {
            conn.createStatement().execute(command);
        }
    }

    /**
     * Initialize Connection to postgres database.
     *
     * @throws TechnicalException Any exception.
     */
    private static Connection getConnection() throws TechnicalException
    {
        try
        {
            Context ctx = new InitialContext();
            DataSource dataSource = (DataSource) ctx.lookup("Validation");
            return dataSource.getConnection();
        }
        catch (Exception e)
        {
            throw new TechnicalException(e);
        }
    }

    private static String[] splitSqlScript(final String sql)
    {
        return sql.replaceAll("\n--.*", "").split(";");
    }

    private static String replaceProperties(String sql)
    {
        Pattern pattern = Pattern.compile("\\#\\{[^\\}]+\\}");
        Matcher matcher = pattern.matcher(sql);
        int pos = 0;
        while (matcher.find(pos))
        {
            String propertyName = matcher.group().substring(2, matcher.group().length() - 1);
            String propertyValue = null;
            try
            {
                propertyValue = PropertyReader.getProperty(propertyName);
            }
            catch (Exception e)
            {
                LOGGER.error("Error reading property", e);
            }
            if (propertyValue == null)
            {
                propertyValue = "$$$" + propertyName + "$$$";
            }
            sql = sql.replace(matcher.group(), propertyValue);
            matcher = pattern.matcher(sql);
        }
        return sql;
    }

    private static void insertValidationData(final Connection conn) throws Exception
    {
        URL dirUrl = Initializer.class.getClassLoader().getResource("WEB-INF/resource/schema");
        String[] path = dirUrl.getPath().split("/|\\\\");
        String modifiedPath = "";
        for (String pathElement : path)
        {
            if ("file:".equals(pathElement))
            {
                continue;
            }
            if (pathElement.endsWith("ar!"))
            {
                int lastPoint = pathElement.lastIndexOf(".");
                pathElement = pathElement.substring(0, lastPoint) + "-exp"
                        + pathElement.substring(lastPoint, pathElement.length() - 1);
            }
            modifiedPath += "/" + pathElement;
        }
        modifiedPath = modifiedPath.substring(1);
        LOGGER.debug("Initial schema path: " + modifiedPath);
        File dir = new File(modifiedPath);
        insertSchemaDirectory(dir, conn);
    }

    private static void insertSchemaDirectory(final File dir, final Connection conn) throws Exception
    {
        File[] schemas = dir.listFiles();
        LOGGER.debug("Schemas found :" + schemas.length);
        for (File schema : schemas)
        {
            if (schema.isDirectory())
            {
                insertSchemaDirectory(schema, conn);
            }
            else
            {
                String schemaContent = ResourceUtil.getResourceAsString(schema.getAbsolutePath());
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser parser = factory.newSAXParser();
                IdentityHandler idHandler = new IdentityHandler();
                parser.parse(new InputSource(new StringReader(schemaContent)), idHandler);
                if (idHandler.isIdentified())
                {
                    LOGGER.info("Adding validation schema to database: cm=" + idHandler.getContentModel() + "; cx="
                            + idHandler.getContext() + "(" + idHandler.getContextName() + ")" + "; mv"
                            + idHandler.getMetadataVersion() + "; v=" + idHandler.getVersion());
                    PreparedStatement statement = conn.prepareStatement("INSERT INTO validation_schema ("
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
