/*
*
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
package de.mpg.escidoc.services.test.search;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.Ignore;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import de.mpg.escidoc.services.search.query.ExportSearchQuery;
import de.mpg.escidoc.services.search.query.ExportSearchResult;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion;
import de.mpg.escidoc.services.search.query.MetadataSearchQuery;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion.CriterionType;


/**
 * Provides some basic test for the search and export component. 
 * The detailed tests are already covered by tests of the structured export manager and the TestMetadataSearch class.
 * TODO Description
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class TestSearchAndExport extends TestSearchBase
{
  
    private Logger logger = Logger.getLogger(TestSearchAndExport.class);
    
 
    
    /**
     * Searches the test item and exports it as snippet with APA citation.
     * @throws Exception
     */
    @Test
    @Ignore
    public void testSearchAndExportSnippetAPA() throws Exception
    {
        
        //search the item
        MetadataSearchQuery query = getStandardQuery();
        query.addCriterion(new MetadataSearchCriterion(CriterionType.ANY, itemTitle));
        ExportSearchQuery exportQuery = new ExportSearchQuery(query.getCqlQuery(), "APA", "snippet");
       
        ExportSearchResult result = itemContainerSearch.searchAndExportItems(exportQuery);
        String resultString = new String((byte[]) result.getExportedResults());
        
        //List<? extends ItemVO> itemList = xmlTransforming.transformToItemList(resultString);
        logger.debug(resultString);
        
        Document doc = getDocument(resultString, true);
        
        XPathFactory factory = XPathFactory.newInstance();
        XPath xPath = factory.newXPath();
       
        
        String xPathExp = "/*[local-name() = 'item-list' and namespace-uri() = 'http://www.escidoc.de/schemas/itemlist/0.7']/*[local-name() = 'item' and namespace-uri() = 'http://www.escidoc.de/schemas/item/0.7']";
        NodeList itemNodes = (NodeList) xPath.evaluate(xPathExp, doc, XPathConstants.NODESET);
        assertEquals("Wrong number of search results", 1, itemNodes.getLength());
        
        Node itemNode = itemNodes.item(0);
        String xPathExp2 = "*[local-name() = 'properties' and namespace-uri() = 'http://www.escidoc.de/schemas/item/0.7']/*[local-name() = 'content-model-specific' and namespace-uri() = 'http://escidoc.de/core/01/properties/']/*[local-name() = 'bibliographicCitation' and namespace-uri() = 'http://purl.org/dc/terms/']";
        Node citationNode = (Node) xPath.evaluate(xPathExp2, itemNode, XPathConstants.NODE);
        
        String citation = citationNode.getTextContent();
        assertNotNull("No citation found", citation);
        
        assertTrue("Title not found in citation", citation.contains(itemTitle));

   }
    
    /**
     * Searches the test item and exports it as html file with APA citation.
     * @throws Exception
     */
    @Test
    @Ignore
    public void testSearchAndExportHtmlApa() throws Exception
    {
        
        //search the item
        MetadataSearchQuery query = getStandardQuery();
        query.addCriterion(new MetadataSearchCriterion(CriterionType.ANY, itemTitle));
        ExportSearchQuery exportQuery = new ExportSearchQuery(query.getCqlQuery(), "APA", "html");
       
        ExportSearchResult result = itemContainerSearch.searchAndExportItems(exportQuery);
        
        byte[] htmlFile = result.getExportedResults();
        
        assertTrue("File size is null", htmlFile.length>0);
        
        String htmlContent = new String(htmlFile);
        
        assertTrue("Title not found in citation", htmlContent.contains(itemTitle));

    }
}
