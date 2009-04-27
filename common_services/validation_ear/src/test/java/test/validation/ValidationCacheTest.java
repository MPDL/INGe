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

package test.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Date;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.mpg.escidoc.services.validation.ValidationSchemaCache;

/**
 * Test class for the validation package.
 *
 * @author franke (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 106 $ $LastChangedDate: 2007-11-07 13:14:06 +0100 (Wed, 07 Nov 2007) $
 *
 */
public class ValidationCacheTest
{

    private String validationPoint = "submit_item";
    private String context = "publication";
    private String contentType = "escidoc:persistent4";

    private ValidationSchemaCache cache;

    private Connection connection;

    /**
     * Sets the validation schema cache instance.
     * @throws Exception Any exception.
     */
    @Before
    public final void getCache() throws Exception
    {
        cache = ValidationSchemaCache.getInstance();
    }

    /**
     * Sets the JDBC connection.
     * @throws Exception Any exception.
     */
    @Before
    public final void getConnection() throws Exception
    {
        Context ctx = new InitialContext();
        DataSource dataSource = (DataSource) ctx.lookup("Validation");
        connection = dataSource.getConnection();
    }

    /**
     * Clears the validation database. Since the schemas cannot be retrieved from the framework
     * the schema table is not not cleared.
     * @throws Exception Any exception.
     */
    @Ignore("Won't work until validation repository moves to framework.")
    @Test
    public final void testClearCache() throws Exception
    {

        //cache.clearCache();

        String sql = "SELECT * FROM escidoc_validation_schema";
        ResultSet rs = connection.createStatement().executeQuery(sql);

        assertNotNull("ResultSet is null", rs);
        assertTrue("Schema table is not empty.", !rs.next());

        rs.close();

        sql = "SELECT * FROM escidoc_validation_schema_snippets";
        rs = connection.createStatement().executeQuery(sql);

        assertNotNull("ResultSet is null", rs);
        assertTrue("Precompiled schema table is not empty.", !rs.next());
    }

    /**
     * Tests cache creation. Since the schemas cannot be retrieved from the framework
     * this test is a little fake, because it only tests on the still existing schemas,
     * but not if these are newly retrieved.
     * @throws Exception Any exception.
     */
    @Ignore
    @Test
    public final void testCreateCache() throws Exception
    {

        cache.createCache();

        String sql = "SELECT * FROM escidoc_validation_schema";
        ResultSet rs = connection.createStatement().executeQuery(sql);

        assertNotNull("ResultSet is null", rs);
        assertTrue("Schema table is empty.", rs.next());

        rs.close();

        sql = "SELECT * FROM escidoc_validation_schema_snippets";
        rs = connection.createStatement().executeQuery(sql);

        assertNotNull("ResultSet is null", rs);
        assertTrue("Precompiled schema table is empty.", rs.next());
    }

    /**
     * Tests cache refreshing.
     * @throws Exception Any exception.
     */
    @Test
    public final void testRefreshCache() throws Exception
    {

        Date lastRefreshDate = cache.getLastRefreshDate();

        cache.refreshCache();

        assertFalse("lastRefreshDate was not updated", cache.getLastRefreshDate().equals(lastRefreshDate));

    }

    /**
     * Tests retrieval of PrecompiledTransformer as done in ItemValidatingBean.
     * @throws Exception Any exception.
     */
    @Test
    public final void testGetPrecompiledTransformer() throws Exception
    {

        Transformer transformer = cache.getPrecompiledTransformer(context, contentType, validationPoint);

        assertNotNull(transformer);

        String origXml = "<test/>";

        StringWriter result = new StringWriter();
        transformer.transform(new StreamSource(new StringReader(origXml)), new StreamResult(result));

        assertTrue("Transformer is not loaded", !result.toString().contains(origXml));

    }

}
