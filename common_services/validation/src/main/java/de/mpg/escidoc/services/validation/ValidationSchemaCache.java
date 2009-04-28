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
* Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

package de.mpg.escidoc.services.validation;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;

import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.types.Validatable;
import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.common.valueobjects.AdminDescriptorVO;
import de.mpg.escidoc.services.common.valueobjects.ContextVO;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.services.util.LocalURIResolver;
import de.mpg.escidoc.services.validation.util.CacheTriple;
import de.mpg.escidoc.services.validation.util.CacheTuple;
import de.mpg.escidoc.services.validation.xmltransforming.ConeContentHandler;

/**
 * Class to deal with validation schemas.
 *
 * @author mfranke
 * @author $Author: mfranke $
 * @version $Revision: 146 $$LastChangedDate: 2007-12-18 14:42:42 +0100 (Tue, 18 Dec 2007) $
 */
public final class ValidationSchemaCache
{

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(ValidationSchemaCache.class);

    /**
     * Singleton.
     */
    private static ValidationSchemaCache instance = null;

    /**
     * The schematron skeleton template used for precompilation.
     */
    private Transformer schematronTemplate = null;

    /**
     * The phase template used for precompilation.
     */
    private Transformer phaseTemplate = null;

    /**
     * XSLT Transformer cache.
     */
    private Map<CacheTriple, Transformer> xsltCache = new HashMap<CacheTriple, Transformer>();

    /**
     * Enable caching of XSLT Transformer objects.
     */
    private final boolean xsltCacheEnabled = true;

    private Date lastRefreshDate = null;

    /**
     * Cached map of contexts and the according validation schemas.
     */
    private final Map<String, String> validationSchemaContextMap = new HashMap<String, String>();

    /**
     * Common XML transforming functionalities.
     */
    private XmlTransforming xmlTransforming;
    
    /**
     * XSLT transformer factory.
     */
    private static TransformerFactory factory = null;

    /**
     * Main method for testing.
     * @param args The arguments.
     * @throws Exception Anyexception.
     */
    public static void main(final String[] args) throws Exception
    {

        ValidationSchemaCache cache = ValidationSchemaCache.getInstance();
        cache.resetCache();
        cache.precompileAll();

    }

    /**
     * Get a singleton instance.
     * @return The singleton.
     * @throws TechnicalException Any exception.
     */
    public static ValidationSchemaCache getInstance() throws TechnicalException
    {

        //System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
        
        if (instance == null)
        {
            instance = new ValidationSchemaCache();
            factory = new net.sf.saxon.TransformerFactoryImpl();
            factory.setURIResolver(new LocalURIResolver());
        }
        return instance;

    }

    /**
     * Retrieve the precompiled schematron validation schema according to the given schemaName and content-type.
     *
     * @param schemaName The escidoc schemaName id.
     * @param contentModel The escidoc content-type.
     * @param validationPoint The escidoc validation point.
     * @return The validation schema as xml.
     * @throws TechnicalException Mostly SQL exeptions.
     * @throws ValidationSchemaNotFoundException Schema was not found in the database.
     */
    public String getPrecompiledSchema(
            final String context,
            final String contentType,
            final String validationPoint)
        throws TechnicalException, ValidationSchemaNotFoundException
    {

        String sql = "SELECT escidoc_validation_schema_snippets.snippet_content "
                + "FROM escidoc_validation_schema_snippets "
                + "WHERE escidoc_validation_schema_snippets.id_content_type_ref = ? "
                + "AND escidoc_validation_schema_snippets.id_context_ref = ? "
                + "AND escidoc_validation_schema_snippets.id_validation_point = ?";

        LOGGER.debug("SQL: " + sql);
        Connection connection = getConnection();

        try
        {

            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, contentType);
            pstmt.setString(2, context);
            pstmt.setString(3, validationPoint);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next())
            {
                String result = rs.getString("snippet_content");
                if (rs.next())
                {
                    connection.close();
                    throw new TechnicalException("More than one schema was found in the database");
                }
                else
                {
                    connection.close();
                    return result;
                }
            }
            else
            {
                connection.close();
                throw new ValidationSchemaNotFoundException(
                        "No schema for schemaName=" + context + ", content-type=" + contentType
                        + " and validationPoint=" + validationPoint);
            }
        }
        catch (SQLException sqle)
        {
            try
            {
                connection.close();
            }
            catch (Exception e)
            {
                LOGGER.error("Error trying to close the connection", e);
            }
            throw new TechnicalException("Error getting schema from database", sqle);
        }
    }

    /**
     * Retrieve the precompiled schematron validation schema according to the given schemaName and content-type.
     *
     * @param schemaName The escidoc schemaName id.
     * @param contentModel The escidoc content-type.
     * @param validationPoint The escidoc validation point.
     * @return The validation schema as xml.
     * @throws TechnicalException Mostly SQL exeptions.
     * @throws ValidationSchemaNotFoundException Schema was not found in the database.
     */
    public Transformer getPrecompiledTransformer(
            final String context,
            final String contentType,
            final String validationPoint) throws TechnicalException, ValidationSchemaNotFoundException
    {

        CacheTriple triple = new CacheTriple(context, contentType, validationPoint);

        if (xsltCacheEnabled && xsltCache.containsKey(triple))
        {
            LOGGER.debug("Getting transformer (" + triple + ") from cache");
            return xsltCache.get(triple);
        }
        else
        {
            String xsl = getPrecompiledSchema(context, contentType, validationPoint);

            LOGGER.debug("Getting transformer (" + triple + ") from database");
            
            try
            {
                Transformer t = factory.newTransformer(new StreamSource(new StringReader(xsl)));
                xsltCache.put(triple, t);
                return t;
            }
            catch (TransformerConfigurationException tce)
            {
                throw new TechnicalException(tce);
            }
        }
    }


    /**
     * Retrieve the precompiled schematron validation schema according to the given schemaName and content model.
     *
     * @param schemaName The escidoc schemaName id.
     * @param contentModel The escidoc content model.
     * @return The validation schema as xml.
     * @throws TechnicalException Mostly SQL exeptions.
     * @throws ValidationSchemaNotFoundException Schema was not found in the database.
     */
    public String getValidationSchema(
            final String schemaName,
            final String contentModel)
        throws TechnicalException, ValidationSchemaNotFoundException
    {

        String sql = "SELECT schema_content FROM escidoc_validation_schema"
                + " WHERE id_content_type_ref = ? and id_context_ref = ?";

        Connection connection = getConnection();

        try
        {

            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, contentModel);
            pstmt.setString(2, schemaName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next())
            {
                String result = rs.getString("schema_content");
                if (rs.next())
                {
                    connection.close();
                    throw new TechnicalException("More than one schema was found in the database");
                }
                else
                {
                    connection.close();
                    return result;
                }
            }
            else
            {
                connection.close();
                throw new ValidationSchemaNotFoundException();
            }
        }
        catch (SQLException sqle)
        {
            try
            {
                connection.close();
            }
            catch (Exception e)
            {
                LOGGER.error("Error trying to close the connection", e);
            }
            throw new TechnicalException("Error getting schema from database", sqle);
        }
    }
    
    /**
     * Retrieve the precompiled schematron validation schema according to the given schemaName and content model.
     *
     * @param schemaName The escidoc schemaName id.
     * @param contentModel The escidoc content model.
     * @return The validation schema as xml.
     * @throws TechnicalException Mostly SQL exeptions.
     * @throws ValidationSchemaNotFoundException Schema was not found in the database.
     */
    public void setValidationSchema(
            final String schemaName,
            final String contentModel,
            final String content)
        throws TechnicalException, ValidationSchemaNotFoundException
    {

        String sql = "SELECT schema_content FROM escidoc_validation_schema"
                + " WHERE id_content_type_ref = ? and id_context_ref = ?";

        Connection connection = getConnection();

        try
        {

            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, contentModel);
            pstmt.setString(2, schemaName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next())
            {
                if (rs.next())
                {
                    connection.close();
                    throw new TechnicalException("More than one schema was found in the database");
                }
                else
                {
                    // Update
                    sql = "UPDATE escidoc_validation_schema SET schema_content = ?"
                        + " WHERE id_content_type_ref = ? and id_context_ref = ?";

                    pstmt = connection.prepareStatement(sql);
                    pstmt.setString(1, content);
                    pstmt.setString(2, contentModel);
                    pstmt.setString(3, schemaName);
                    pstmt.executeUpdate();
                }
            }
            else
            {
                // Insert
                sql = "INSERT INTO escidoc_validation_schema " +
                		"(id_content_type_ref, id_context_ref, schema_content) VALUES (?, ?, ?)";

                pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, contentModel);
                pstmt.setString(2, schemaName);
                pstmt.setString(3, content);
                pstmt.executeUpdate();
            }
        }
        catch (SQLException sqle)
        {
            try
            {
                connection.close();
            }
            catch (Exception e)
            {
                LOGGER.error("Error trying to close the connection", e);
            }
            throw new TechnicalException("Error getting schema from database", sqle);
        }
        finally
        {
            try
            {
                connection.close();
            }
            catch (Exception e)
            {
                LOGGER.error("Error trying to close the connection", e);
            }
        }
    }
    /**
     * Update the cache from the main repository.
     * @throws TechnicalException Any unmanaged exception.
     */
    public void refreshCache() throws TechnicalException
    {
        try
        {
            refreshCache(lastRefreshDate);
        }
        catch (Exception e)
        {
            throw new TechnicalException("Error refreshing validation schema cache", e);
        }
    }

    /**
     * Update the cache from the main repository.
     * @param lastRefresh refresh all contents that are older than this date.
     * @throws Exception Any unmanaged exception.
     */
    public void refreshCache(Date lastRefresh) throws Exception
    {

        Date actualDate = new Date();

        if (lastRefresh == null)
        {
            Connection connection = getConnection();
            try
            {
                String sql = "SELECT MAX(date_last_refreshed) AS date_last_refreshed FROM escidoc_validation_schema";
                ResultSet rs = connection.createStatement().executeQuery(sql);
                if (rs.next())
                {
                    lastRefresh = rs.getDate("date_last_refreshed");
                }
                else
                {
                    lastRefresh = null;
                }
                connection.close();
            }
            catch (Exception e)
            {
                try
                {
                    connection.close();
                }
                catch (Exception ce)
                {
                    LOGGER.error("Error trying to close the connection", e);
                }
                throw new TechnicalException("Error getting last refresh date", e);
            }
        }

        // Retrieve changed validation schemas from external source
        retrieveNewSchemas(lastRefresh);
        
        
        precompileAll();
        xsltCache = new HashMap<CacheTriple, Transformer>();

        this.lastRefreshDate = actualDate;
    }

    private void retrieveNewSchemas(Date lastRefresh) throws Exception
    {
        String externalSourceName = PropertyReader.getProperty("escidoc.validation.source.classname");

            Class<?> cls = Class.forName(externalSourceName);
            ValidationSchemaSource source = (ValidationSchemaSource) cls.newInstance();
            Map<CacheTuple, String> newSchemas = source.retrieveNewSchemas(lastRefresh);
            
            if (newSchemas != null)
            {
                for (CacheTuple id : newSchemas.keySet())
                {
                    LOGGER.info("Creating/updating validation schema: cm=" + id.getContentModel() + "; name=" + id.getSchemaName());
                    setValidationSchema(id.getSchemaName(), id.getContentModel(), newSchemas.get(id));
                }
            }
    }

    /**
     * Precompile a validation schema.
     *
     * @param schemaName The escidoc schemaName id.
     * @param contentModel The escidoc content-type.
     * @throws TechnicalException Any exception
     * @throws ValidationSchemaNotFoundException Validation schema not found in database.
     */
    private void precompile(
            final String context,
            final String contentType,
            final String metadataVersion) throws TechnicalException, ValidationSchemaNotFoundException
    {

        String sql =
            "SELECT schema_content FROM escidoc_validation_schema WHERE id_content_type_ref = ? and "
            + "id_context_ref = ?";
        Connection connection = getConnection();
        try
        {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, contentType);
            pstmt.setString(2, context);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
            {
                String schema = rs.getString("schema_content");
                if (rs.next())
                {
                    connection.close();
                    throw new TechnicalException("More than one schema was found in the database");
                }
                else
                {
                    connection.close();
                    if (schema != null && !"".equals(schema))
                    {
                        transformSchema(schema, context, contentType, metadataVersion);
                    }
                }
            }
            else
            {
                connection.close();
                throw new ValidationSchemaNotFoundException();
            }
        }
        catch (SQLException sqle)
        {
            try
            {
                connection.close();
            }
            catch (Exception ce)
            {
                LOGGER.error("Error trying to close the connection", ce);
            }
            throw new TechnicalException("Error getting schema from database", sqle);
        }
    }

    /**
     * Internal method to store the preecompiled Template for each phase into the database.
     *
     * @param schema The schema xsl.
     * @param schemaId The schema uid.
     * @throws TechnicalException Any Exception.
     * @throws SQLException Might be thrown by the database.
     */
    private void transformSchema(String schema, final String context, final String contentType, String metadataVersion)
        throws TechnicalException, SQLException
    {
        String sql;
        PreparedStatement pstmt;
        Connection connection = getConnection();

        // Delete old precompiled schemas
        sql = "DELETE FROM escidoc_validation_schema_snippets WHERE id_context_ref = ? AND id_content_type_ref = ?";
        pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, context);
        pstmt.setString(2, contentType);
        pstmt.executeUpdate();

        schema = insertConeContent(schema);
        
        // Get phases
        StringWriter phaseList = XsltTransforming.transform(schema, getPhaseTemplate(), null);
        String[] phases = phaseList.toString().split("\n");

        // Precompile phases and store back to database
        for (int i = 0; i < phases.length; i++)
        {

            Map<String, String> params = new HashMap<String, String>();
            params.put("phase", phases[i].trim());

            StringWriter precompiled = XsltTransforming.transform(schema, getSchematronTemplate(), params);

            sql = "INSERT INTO escidoc_validation_schema_snippets (id_context_ref, id_content_type_ref, "
                    + "id_validation_point, id_metadata_version_ref, snippet_content) "
                    + "VALUES (?, ?, ?, ?, ?)";
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, context);
            pstmt.setString(2, contentType);
            pstmt.setString(3, phases[i].trim());
            pstmt.setString(4, metadataVersion);
            pstmt.setString(5, precompiled.toString());
            pstmt.executeUpdate();

            // Remove existing transformers from the cache
            CacheTriple triple = new CacheTriple(context, contentType, phases[i].trim());
            if (xsltCache.containsKey(triple))
            {
                xsltCache.remove(triple);
            }

        }
        connection.close();

    }

    private String insertConeContent(String schema)
    {
        try
        {
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            InputSource inputSource = new InputSource(new StringReader(schema));
            ConeContentHandler contentHandler = new ConeContentHandler();
            parser.parse(inputSource, contentHandler);
            return contentHandler.getResult();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the schematron skeleton template from the database.
     *
     * @return Schematron skeleton template.
     * @throws TechnicalException Any exception.
     */
    private Transformer getSchematronTemplate() throws TechnicalException
    {
        if (schematronTemplate == null)
        {
            try
            {
                InputStream fileIn = ResourceUtil.getResourceAsStream("stylesheet/validation_report.xsl");

                BufferedReader br = new BufferedReader(new InputStreamReader(fileIn));
                String line = null;
                String result = "";
                while ((line = br.readLine()) != null)
                {
                    result += line + "\n";
                }

                schematronTemplate = factory.newTransformer(new StreamSource(new StringReader(result)));

            }
            catch (Exception e)
            {
                throw new TechnicalException(e);
            }
        }
        return schematronTemplate;
    }

    /**
     * Get the xslt template for extracting the phases.
     *
     * @return phase template.
     * @throws TechnicalException Any exception.
     */
    private Transformer getPhaseTemplate() throws TechnicalException
    {
        if (phaseTemplate == null)
        {

            try
            {
                InputStream fileIn = ResourceUtil.getResourceAsStream("stylesheet/validation_points.xsl");
                phaseTemplate = factory.newTransformer(new StreamSource(fileIn));
                LOGGER.debug("phaseTemplate: " + phaseTemplate);
            }
            catch (Exception e)
            {
                throw new TechnicalException("Error loading validation points template", e);
            }
        }
        return phaseTemplate;
    }

    /**
     * Precompile every validation schema in the database.
     *
     * @throws TechnicalException Any exception
     */
    private void precompileAll() throws TechnicalException
    {

        String sql = "SELECT id_context_ref, id_content_type_ref, id_metadata_version_ref FROM escidoc_validation_schema";
        Connection connection = getConnection();
        try
        {

            PreparedStatement pstmt = connection.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next())
            {
                String context = rs.getString("id_context_ref");
                String contentType = rs.getString("id_content_type_ref");
                String metadataVersion = rs.getString("id_metadata_version_ref");
                try
                {
                    precompile(context, contentType, metadataVersion);
                }
                catch (ValidationSchemaNotFoundException vsnfe)
                {
                    try
                    {
                        connection.close();
                    }
                    catch (Exception ce)
                    {
                        LOGGER.error("Error trying to close the connection", ce);
                    }
                    throw new TechnicalException("Schema not found, but should have been found.", vsnfe);
                }
            }
            connection.close();

        }
        catch (SQLException sqle)
        {
            try
            {
                connection.close();
            }
            catch (Exception ce)
            {
                LOGGER.error("Error trying to close the connection", ce);
            }
            throw new TechnicalException("Error getting schematron template from database", sqle);
        }
    }

    /**
     * Resets the validation schema cache database.
     *
     * @throws TechnicalException Any exception.
     */
    public void resetCache() throws TechnicalException
    {

        // TODO FrM: Commented out until schema repository is implemented.
        //clearCache();
        validationSchemaContextMap.clear();
        createCache();
    }

    /**
     * Clear validation database.
     * @throws TechnicalException Any unmanaged exception.
     */
    public void clearCache() throws TechnicalException
    {
        xsltCache = new HashMap<CacheTriple, Transformer>();
        Connection connection = getConnection();
        try
        {
            String sql = "DELETE FROM escidoc_validation_schema";
            connection.createStatement().executeUpdate(sql);
            sql = "DELETE FROM escidoc_validation_schema_snippets";
            connection.createStatement().executeUpdate(sql);
            connection.close();
        }
        catch (Exception e)
        {
            try
            {
                connection.close();
            }
            catch (Exception ce)
            {
                LOGGER.error("Error trying to close the connection", e);
            }
            throw new TechnicalException("Error deleting schema cache contents", e);
        }
    }

    /**
     * Create validation cache.
     * @throws TechnicalException Any unmanaged exception.
     */
    public void createCache() throws TechnicalException
    {

        String sql = "SELECT COUNT(*) AS cnt FROM escidoc_validation_schema";
        Connection connection = getConnection();
        try
        {
            ResultSet rs = connection.createStatement().executeQuery(sql);
            if (rs.next())
            {
                connection.close();
            }
            else
            {
                connection.close();
                throw new TechnicalException("Error getting count for existing validation schemas");
            }
        }
        catch (Exception e)
        {
            try
            {
                connection.close();
            }
            catch (Exception ce)
            {
                LOGGER.error("Error trying to close the connection", e);
            }
            throw new TechnicalException("Error checking for existing validation schemas", e);
        }

        // Call update method without last-modification-date
        try
        {
            refreshCache(null);
        }
        catch (Exception e)
        {
            throw new TechnicalException("Error creating validation cache", e);
        }

    }

    /**
     * Initialize Connection to database.
     *
     * @throws TechnicalException Any exception.
     */
    private Connection getConnection() throws TechnicalException
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

    /**
     * Constructor is hidden. An instance can be retrieved by calling getInstance.
     * @throws TechnicalException Any Exception.
     */
    private ValidationSchemaCache() throws TechnicalException
    {
        try
        {
            Context ctx = new InitialContext();
            xmlTransforming = (XmlTransforming) ctx.lookup(XmlTransforming.SERVICE_NAME);
        }
        catch (Exception e)
        {
            throw new TechnicalException("Error getting xmlTransforming bean.", e);
        }
    }

    public Date getLastRefreshDate()
    {
        return lastRefreshDate;
    }

    public void setLastRefreshDate(final Date lastRefreshDate)
    {
        this.lastRefreshDate = lastRefreshDate;
    }
    
    public String getValidationSchemaId(String context) throws Exception
    {
        if (!validationSchemaContextMap.containsKey(context))
        {
            String contextXml = ServiceLocator.getContextHandler().retrieve(context);
            ContextVO contextVO = xmlTransforming.transformToContext(contextXml);
            AdminDescriptorVO adminDescriptorVO = contextVO.getAdminDescriptors().get(0);
            if (adminDescriptorVO instanceof Validatable)
            {
                String validateSchemaId = ((Validatable) adminDescriptorVO).getValidationSchema();
                return validateSchemaId; 
            }
        }

        return validationSchemaContextMap.get(context);
    }
}
