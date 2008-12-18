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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import gov.loc.www.zing.srw.ExplainRequestType;
import gov.loc.www.zing.srw.ExplainResponseType;
import gov.loc.www.zing.srw.RecordType;
import gov.loc.www.zing.srw.SearchRetrieveRequestType;
import gov.loc.www.zing.srw.SearchRetrieveResponseType;
import gov.loc.www.zing.srw.diagnostic.DiagnosticType;

import org.apache.axis.message.MessageElement;
import org.apache.log4j.Logger;
import org.junit.Test;

import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Testcases for the basic service Search and browse.
 * 
 * @author Peter Broszeit (initial creation)
 * @author $Author:pbroszei $ (last modification)
 * @version $Revision:93 $ $LastChangedDate:2007-02-14 11:33:54 +0100 (Mi, 14 Feb 2007) $
 * @revised by BrP: 04.09.2007
 */
public class TestSearchAndBrowse
{
    private static final String VERSION = "1.1";
    
    private Logger logger = Logger.getLogger(getClass());

    private void checkDiagnostics(SearchRetrieveResponseType searchResult)
    {
        if (searchResult.getDiagnostics() != null)
        {
            // something went wrong
            for (DiagnosticType diagnostic : searchResult.getDiagnostics().getDiagnostic())
            {
                 logger.debug("URI    :" + diagnostic.getUri());
                 logger.debug("Message:" + diagnostic.getMessage());
                 logger.debug("Details:" + diagnostic.getDetails());
             }
            fail("Search request failed. Diagnostics returned. See log for details.");
        }
    }

    private void displayRecords(SearchRetrieveResponseType searchResult) throws Exception
    {
        /* RecordType[] records = searchResult.getRecords().getRecord();
         if (records != null)
         {
             for (int i = 0; i < records.length; ++i)
             {
                 StringOrXmlFragment data = records[i].getRecordData();
                 MessageElement[] messages = data.get_any();
                 for (int j = 0; j < messages.length; ++j)
                 {
                     logger.debug("record[" + i + "," + j + "]=" + messages[j].getAsString());
                 }
             }
         }*/
    }

    /**
     * Test method for {@link gov.loc.www.zing.srw.service.ExplainPort#explainOperation(gov.loc.www.zing.srw.ExplainRequestType)}.
     */
    @Test
    public void explainPlan() throws Exception
    {
        long zeit = -System.currentTimeMillis();
        zeit += System.currentTimeMillis();
        ExplainRequestType explainRequest = new ExplainRequestType();
        explainRequest.setVersion(VERSION);
        ExplainResponseType explainResponse = ServiceLocator.getExplainHandler("escidoc_all").explainOperation(explainRequest);
        logger.info("explainPlan()->" + zeit + "ms");
        assertNotNull(explainResponse);
        RecordType record = explainResponse.getRecord();
        MessageElement[] messages = record.getRecordData().get_any();
        for (int i=0; i<messages.length; ++i)
        {
            logger.debug("Message[" + i + "]=" + messages[i].getAsString());
        }
    }

    /**
     * Test method for {@link gov.loc.www.zing.srw.service.SRWPort#searchRetrieveOperation(gov.loc.www.zing.srw.SearchRetrieveRequestType)}.
     */
    @Test
    public void searchForAbstracts() throws Exception
    {
        String query = "escidoc.genre=abstract";
        long zeit = -System.currentTimeMillis();
        zeit += System.currentTimeMillis();
        SearchRetrieveRequestType searchRetrieveRequest = new SearchRetrieveRequestType();
        searchRetrieveRequest.setVersion(VERSION);
        searchRetrieveRequest.setRecordPacking("xml");
        searchRetrieveRequest.setQuery(query);
        SearchRetrieveResponseType searchResult = ServiceLocator.getSearchHandler("escidoc_en").searchRetrieveOperation(searchRetrieveRequest);
        logger.info("searchForAbstracts(" + query + ")->" + zeit + "ms");
        logger.debug("SearchResult()=" + searchResult.getNumberOfRecords());
        assertNotNull(searchResult);
        checkDiagnostics(searchResult);
        displayRecords(searchResult);
    }

    /**
     * Test method for {@link gov.loc.www.zing.srw.service.SRWPort#searchRetrieveOperation(gov.loc.www.zing.srw.SearchRetrieveRequestType)}.
     */
    @Test
    public void searchForDates() throws Exception
    {
        String query = "escidoc.any-dates >= 1964-11-16 AND escidoc.any-dates <= 2007-10-08";
        long zeit = -System.currentTimeMillis();
        zeit += System.currentTimeMillis();
        SearchRetrieveRequestType searchRetrieveRequest = new SearchRetrieveRequestType();
        searchRetrieveRequest.setVersion(VERSION);
        searchRetrieveRequest.setRecordPacking("xml");
        searchRetrieveRequest.setQuery(query);
        SearchRetrieveResponseType searchResult = ServiceLocator.getSearchHandler("escidoc_all").searchRetrieveOperation(searchRetrieveRequest);
        logger.info("searchForDates(" + query + ")->" + zeit + "ms");
        logger.debug("SearchResult()=" + searchResult.getNumberOfRecords());
        assertNotNull(searchResult);
        checkDiagnostics(searchResult);
        displayRecords(searchResult);
    }
}
