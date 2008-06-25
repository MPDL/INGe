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
package test.framework.sb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileInputStream;

import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.w3c.dom.Document;

import test.framework.om.TestItemBase;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Testcases for simple search queries.
 *
 * @author Peter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * @revised by BrP: 04.09.2007
 */
public class TestSimpleSearch extends TestItemBase
{
    private Logger logger = Logger.getLogger(getClass());

    /**
     * Searches by escidoc.publication.creator.person.organization.identifier.
     */
    @Test
    public void searchByOrganizationId() throws Exception
    {
        // createReleasedItemWithFile();
        String query = "escidoc.publication.creator.person.organization.identifier=PIDAff";
        executeSearch(query, 1);
    }

    /**
     * Searches by escidoc.content-type.href.
     */
    @Test
    public void searchByContentType() throws Exception
    {
        String query = "escidoc.content-type.href=\"/ctm/content-type/escidoc:persistent4\"";
        executeSearch(query, -1);
    }

    /**
     * Searches by escidoc.fulltext.
     */
    @Test
    public void searchFulltext() throws Exception
    {
        String query = "escidoc.fulltext=Saint-Exupery or escidoc.metadata=Metzger";
        executeSearch(query, 1);
    }

    /**
     * Searches by escidoc.objid.
     */
    @Test
    public void searchByObjid() throws Exception
    {
        String q1 = "escidoc.objid=escidoc:275";
        executeSearch(q1, 1);
    }

    /**
     * Searches by escidoc.metadata for a simple constant.
     */
    @Test
    public void searchSimpleName1() throws Exception
    {
        String q1 = "escidoc.metadata=Metzger";
        executeSearch(q1, 1);
    }

    /**
     * Searches by escidoc.metadata for a simple constant.
     */
    @Test
    public void searchSimpleName2() throws Exception
    {
        String q2 = "escidoc.metadata=Apolonski";
        executeSearch(q2, 1);
    }

    /**
     * Searches by escidoc.metadata for a simple constant. 
     */
    @Test
    public void searchSimpleName3() throws Exception
    {
        String q3 = "escidoc.metadata=Schmid";
        executeSearch(q3, 1);
    }

    /**
     * Searches by escidoc.metadata by with operator or.
     */
    @Test
    public void searchWithBooleanOperatorOr() throws Exception
    {
        String query = "escidoc.metadata=Metzger or escidoc.metadata=Apolonsko";
        executeSearch(query, 1);
    }

    /**
     * Searches escidoc.metadata by with two operators and parenthesis.
     */
    @Test
    public void searchWithTwoBooleanOperatorsAndParenthesis() throws Exception
    {
        String query = "escidoc.metadata=Schmid or (escidoc.metadata=Metzger and escidoc.metadata=Apolonsko)";
        executeSearch(query, 1);
    }

    /**
     * Searches by escidoc.metadata with two operators and parenthesis.
     */
    @Test
    public void searchWithTwoBooleanOperatorsAndParenthesis2() throws Exception
    {
        String query = "(escidoc.metadata=Metzger and escidoc.metadata=Apolonsko) or escidoc.metadata=Schmid";
        executeSearch(query, 1);
    }

    /**
     * Searches by escidoc.metadata with a not operator and parenthesis.
     */
    @Test
    public void searchWithNotOperatorAndParenthesis() throws Exception
    {
        String query = "(escidoc.metadata=Metzger and escidoc.metadata=Apolonski) not escidoc.metadata=Schmid";
        executeSearch(query, 0);
    }

    /**
     * Searches by escidoc.metadata with a not operator.
     */
    @Test
    public void searchWithNotOperator() throws Exception
    {
        String query = "escidoc.metadata=Metzger not escidoc.metadata=Apolonsko";
        executeSearch(query, 1);
    }

    /**
     * Searches by escidoc.metadata for quoted terms. 
     */
    @Test
    public void searchQuotedTerms() throws Exception
    {
        String q6_1 = "escidoc.metadata=\"High-Field Physics\"";
        String q6_2 = "escidoc.metadata=\"High Physics\"";
        executeSearch(q6_1, 1);
        executeSearch(q6_2, 0);
    }

    /**
     * Searches by escidoc.metadata with a wildcard in the middle.
     */
    @Test
    public void searchWithWildcardQuestionmark() throws Exception
    {
        String query = "escidoc.metadata=Apolon?ki";
        executeSearch(query, 1);
    }

    /**
     * Searches by escidoc.metadata with a wildcard at the end.
     */
    @Test
    public void searchWithWildcardAsteriskAtEnd() throws Exception
    {
        String query = "escidoc.metadata=Schm*";
        executeSearch(query, 1);
    }

    /**
     * Searches by escidoc.metadata with a wildcard in the middle.
     */
    @Test
    public void searchWithWildcardAsteriskInMiddle() throws Exception
    {
        String query = "escidoc.metadata=S*mid";
        executeSearch(query, 1);
    }

    /**
     * Searches by escidoc.metadata with a wildcard at the beginning.
     */
    @Test
    public void searchWithWildcardAsteriskAtStart() throws Exception
    {
       //  // Laut CQL ist * am Anfang eines Terms erlaubt
//         // Laut Spez. PubMan Searching nicht.

//         String query = "escidoc.metadata=*mid";

//         SearchRetrieveRequestType searchRetrieveRequest = new SearchRetrieveRequestType();
//         searchRetrieveRequest.setVersion("1.1");
//         searchRetrieveRequest.setQuery(query);
//         searchRetrieveRequest.setRecordPacking("xml");
//         SearchRetrieveResponseType searchResult = ServiceLocator.getSearchHandler(null).searchRetrieveOperation(
//                 searchRetrieveRequest);

//         assertNotNull(searchResult);
//         assertEquals("Wrong number of result records for query: " + query, 0, searchResult.getNumberOfRecords()
//                 .intValue());

//         // error messages expected
//         assertNotNull(searchResult.getDiagnostics());
//         for (DiagnosticType diagnostic : searchResult.getDiagnostics())
//         {
//             logger.debug(diagnostic.getUri());
//             logger.debug(diagnostic.getMessage());
//             logger.debug(diagnostic.getDetails());
//         }
    }

    /**
     * Searches by escidoc.metadata with wildcards in a phrase.
     */
    @Test
    public void searchWithWildcardQuestionMarkInPhrase() throws Exception
    {
        String query = "escidoc.metadata=\"Schm?d, Karl\"";
        executeSearch(query, 0);
    }

    /**
     * Searches by escidoc.metadata with a phrase.
     */
    @Test
    public void searchWithWildcardAsteriskInPhrase() throws Exception
    {
        String query = "escidoc.metadata=\"Schm*, Karl\"";
        executeSearch(query, 0);
    }

    /**
     * Searches by escidoc.metadata with wildcards.
     */
    @Test
    public void searchWildcardTerms() throws Exception
    {
        String query = "escidoc.metadata=S*m?d";
        executeSearch(query, 1);
    }

    /**
     * Searches by escidoc.metadata all relation.
     */
    @Test
    public void searchWithAllRelation() throws Exception
    {
        String query = "escidoc.metadata all \"Schmid Metzger Apolonski\"";
        executeSearch(query, 1);
    }

    private void executeSearch(String query, int expectedRecords) throws Exception
    {
      //   SearchRetrieveRequestType searchRetrieveRequest = new SearchRetrieveRequestType();
//         searchRetrieveRequest.setVersion("1.1");
//         searchRetrieveRequest.setQuery(query);
//         searchRetrieveRequest.setRecordPacking("xml");
//         // searchRetrieveRequest.setRecordSchema("http://www.escidoc.de/schemas/search-result/0.1/");
//         SearchRetrieveResponseType searchResult = ServiceLocator.getSearchHandler(null).searchRetrieveOperation(searchRetrieveRequest);

//         assertNotNull(searchResult);

//         // nach Fehlermeldungen schauen
//         if (searchResult.getDiagnostics() != null)
//         {
//             // something went wrong
//             for (DiagnosticType diagnostic : searchResult.getDiagnostics())
//             {
//                 logger.debug(diagnostic.getUri());
//                 logger.debug(diagnostic.getMessage());
//                 logger.debug(diagnostic.getDetails());
//             }
//             fail("Search request failed. Diagnostics returned. See log for details.");
//         }

//         logger.debug("SearchResult()=" + searchResult.getNumberOfRecords());

//         if (expectedRecords >= 0)
//         {
//             assertEquals("Wrong number of result records for query: " + query, expectedRecords, searchResult
//                     .getNumberOfRecords().intValue());
//         }

//         if (expectedRecords > 0)
//         {
//             RecordType[] records = searchResult.getRecords();
//             assertNotNull(records);

//             for (int i = 0; i < records.length; ++i)
//             {
//                 StringOrXmlFragment data = records[i].getRecordData();
//                 MessageElement[] messages = data.get_any();
//                 for (int j = 0; j < messages.length; ++j)
//                 {
//                     logger.debug("record[" + i + "," + j + "]:");
//                     logger.debug(messages[j].getAsString());
//                 }
//             }
//         }
    }

    private void getItem() throws Exception
    {
        String item = ServiceLocator.getItemHandler(userHandle).retrieve("escidoc:1");
        logger.debug(item);
    }
 
    private String createReleasedItem() throws Exception
    {
        String userHandle = loginScientist();
        String item = ServiceLocator.getItemHandler(userHandle).create(readFile("src/test/resources/test/testsimplesearch/item1.xml"));
        String id = getId(item);
        String md = getModificationDate(item);
        ServiceLocator.getItemHandler(userHandle).submit(id, createModificationDate(md));
        item = ServiceLocator.getItemHandler(userHandle).retrieve(id);
        md = getModificationDate(item);
        ServiceLocator.getItemHandler(userHandle).release(id, createModificationDate(md));
        return id;
    }

    private String createReleasedItemWithFile() throws Exception
    {
        String userHandle = loginScientist();
        // Prepare the HttpMethod.
        PutMethod method = new PutMethod(ServiceLocator.getFrameworkUrl() + "/st/staging-file");
        method.setRequestEntity(new InputStreamRequestEntity(new FileInputStream("src/test/resourcestest/testsimplesearch/Der_kleine_Prinz_Auszug.pdf")));
        method.setRequestHeader("Content-Type", "application/pdf");
        method.setRequestHeader("Cookie", "escidocCookie=Skip-Authorization");

        // Execute the method with HttpClient.
        HttpClient client = new HttpClient();
        client.executeMethod(method);
        logger.debug("Status=" + method.getStatusCode()); // >= HttpServletResponse.SC_MULTIPLE_CHOICE 300 ???
        assertEquals(HttpServletResponse.SC_OK, method.getStatusCode());
        String response = method.getResponseBodyAsString();
        logger.debug("Response=" + response);

        // Create a document from the response.
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document document = docBuilder.parse(method.getResponseBodyAsStream());
        document.getDocumentElement().normalize();

        // Extract the file information.
        String href = getValue(document, "/staging-file/@href");
        assertNotNull(href);

        // Create an item with the href in the component.
        String item = readFile("src/test/resources/test/testsimplesearch/item1.xml");
        item = item.replaceFirst("XXX_CONTENT_REF_XXX", ServiceLocator.getFrameworkUrl() + href);
        logger.debug("Item=" + item);
        item = ServiceLocator.getItemHandler(userHandle).create(item);
        assertNotNull(item);
        logger.debug("Item=" + item);

        String id = getId(item);
        String md = getModificationDate(item);
        ServiceLocator.getItemHandler(userHandle).submit(id, createModificationDate(md));
        item = ServiceLocator.getItemHandler(userHandle).retrieve(id);
        md = getModificationDate(item);
        ServiceLocator.getItemHandler(userHandle).release(id, createModificationDate(md));
        return id;
    }
}
