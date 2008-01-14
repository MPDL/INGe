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
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import org.apache.log4j.Logger;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.util.LocalURIResolver;
import de.mpg.escidoc.services.util.ResourceUtil;

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
    private Map<XsltCacheTriple, Transformer> xsltCache = new HashMap<XsltCacheTriple, Transformer>();

    /**
     * Enable caching of XSLT Transformer objects.
     */
    private final boolean xsltCacheEnabled = true;

    private Date lastRefreshDate = null;

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

        if (instance == null)
        {
            instance = new ValidationSchemaCache();
            factory = TransformerFactory.newInstance();
            factory.setURIResolver(new LocalURIResolver());
        }
        return instance;

    }

    /**
     * Retrieve the precompiled schematron validation schema according to the given context and content-type.
     *
     * @param context The escidoc context id.
     * @param contentType The escidoc content-type.
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

        String sql = "SELECT validation_schema_snippets.snippet_content "
                + "FROM validation_schema_snippets "
                + "WHERE validation_schema_snippets.id_content_type_ref = ? "
                + "AND validation_schema_snippets.id_context_ref = ? "
                + "AND validation_schema_snippets.id_validation_point = ?";

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
                        "No schema for context=" + context + ", content-type=" + contentType
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
     * Retrieve the precompiled schematron validation schema according to the given context and content-type.
     *
     * @param context The escidoc context id.
     * @param contentType The escidoc content-type.
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

        XsltCacheTriple triple = new XsltCacheTriple(context, contentType, validationPoint);

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
     * Retrieve the precompiled schematron validation schema according to the given context and content-type.
     *
     * @param context The escidoc context id.
     * @param contentType The escidoc content-type.
     * @return The validation schema as xml.
     * @throws TechnicalException Mostly SQL exeptions.
     * @throws ValidationSchemaNotFoundException Schema was not found in the database.
     */
    public String getValidationSchema(
            final String context,
            final String contentType)
        throws TechnicalException, ValidationSchemaNotFoundException
    {

        String sql = "SELECT schema_content FROM validation_schema"
                + " WHERE id_content_type_ref = ? and id_context_ref = ?";

        Connection connection = getConnection();

        try
        {

            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, contentType);
            pstmt.setString(2, context);
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
     * Update the cache from the main repository.
     * @throws TechnicalException Any unmanaged exception.
     */
    public void refreshCache() throws TechnicalException
    {
        refreshCache(lastRefreshDate);
    }

    /**
     * Update the cache from the main repository.
     * @param lastRefresh refresh all contents that are older than this date.
     * @throws TechnicalException Any unmanaged exception.
     */
    public void refreshCache(Date lastRefresh) throws TechnicalException
    {

        Date actualDate = new Date();

        if (lastRefresh == null)
        {
            Connection connection = getConnection();
            try
            {
                String sql = "SELECT MAX(date_last_refreshed) AS date_last_refreshed FROM validation_schema";
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

        // TODO FrM: Implementation
        precompileAll();
        xsltCache = new HashMap<XsltCacheTriple, Transformer>();

        this.lastRefreshDate = actualDate;
    }

    /**
     * Precompile a validation schema.
     *
     * @param context The escidoc context id.
     * @param contentType The escidoc content-type.
     * @throws TechnicalException Any exception
     * @throws ValidationSchemaNotFoundException Validation schema not found in database.
     */
    private void precompile(
            final String context,
            final String contentType,
            final String metadataVersion) throws TechnicalException, ValidationSchemaNotFoundException
    {

        String sql =
            "SELECT schema_content FROM validation_schema WHERE id_content_type_ref = ? and "
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
    private void transformSchema(final String schema, final String context, final String contentType, String metadataVersion)
        throws TechnicalException, SQLException
    {
        String sql;
        PreparedStatement pstmt;
        Connection connection = getConnection();

        // Delete old precompiled schemas
        sql = "DELETE FROM validation_schema_snippets WHERE id_context_ref = ? AND id_content_type_ref = ?";
        pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, context);
        pstmt.setString(2, contentType);
        pstmt.executeUpdate();

        // Get phases
        StringWriter phaseList = XsltTransforming.transform(schema, getPhaseTemplate(), null);
        String[] phases = phaseList.toString().split("\n");

        // Precompile phases and store back to database
        for (int i = 0; i < phases.length; i++)
        {

            Map<String, String> params = new HashMap<String, String>();
            params.put("phase", phases[i].trim());

            StringWriter precompiled = XsltTransforming.transform(schema, getSchematronTemplate(), params);

            sql = "INSERT INTO validation_schema_snippets (id_context_ref, id_content_type_ref, "
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
            XsltCacheTriple triple = new XsltCacheTriple(context, contentType, phases[i].trim());
            if (xsltCache.containsKey(triple))
            {
                xsltCache.remove(triple);
            }

        }
        connection.close();

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
                InputStream fileIn = ResourceUtil.getResourceAsStream("resource/stylesheet/validation_report.xsl");

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
                InputStream fileIn = ResourceUtil.getResourceAsStream("resource/stylesheet/validation_points.xsl");
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

        String sql = "SELECT id_context_ref, id_content_type_ref, id_metadata_version_ref FROM validation_schema";
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
        createCache();
    }

    /**
     * Clear validation database.
     * @throws TechnicalException Any unmanaged exception.
     */
    public void clearCache() throws TechnicalException
    {
        xsltCache = new HashMap<XsltCacheTriple, Transformer>();
        Connection connection = getConnection();
        try
        {
            String sql = "DELETE FROM validation_schema";
            connection.createStatement().executeUpdate(sql);
            sql = "DELETE FROM validation_schema_snippets";
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

        String sql = "SELECT COUNT(*) AS cnt FROM validation_schema";
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
        refreshCache(null);

    }

    /**
     * Initialize Connection to postgres database.
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
        // Use Saxon for XPath2.0 support
        System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
    }

    public Date getLastRefreshDate()
    {
        return lastRefreshDate;
    }

    public void setLastRefreshDate(final Date lastRefreshDate)
    {
        this.lastRefreshDate = lastRefreshDate;
    }

    /**
     *
     * Identifier class for XSLT transformer cache.
     *
     */
    class XsltCacheTriple
    {
        private String context;
        private String contentType;
        private String validationPoint;
        private int hash = 1;

        /**
         * Constructor.
         * @param context Context.
         * @param contentType Content-Type.
         * @param validationPoint Validation Point.
         */
        XsltCacheTriple(final String context, final String contentType, final String validationPoint)
        {
            this.context = context;
            this.contentType = contentType;
            this.validationPoint = validationPoint;
            this.hash = (context + contentType + validationPoint).length();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(final Object other)
        {
            if (this.context != null
                    && this.contentType != null
                    && this.validationPoint != null
                    && other instanceof XsltCacheTriple)
            {
                return (this.context.equals(((XsltCacheTriple) other).context)
                        && this.contentType.equals(((XsltCacheTriple) other).contentType)
                        && this.validationPoint.equals(((XsltCacheTriple) other).validationPoint));
            }
            else
            {
                return false;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString()
        {
            return "[" + context + "|" + contentType  + "|" + validationPoint + "]";
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode()
        {
            return hash;
        }
    }
}
