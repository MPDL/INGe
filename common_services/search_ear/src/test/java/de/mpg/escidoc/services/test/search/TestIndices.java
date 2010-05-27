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
 * Copyright 2006-2010 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */
package de.mpg.escidoc.services.test.search;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import static org.junit.Assert.*;

import de.mpg.escidoc.services.search.query.MetadataSearchCriterion;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion.CriterionType;



/**
 * @author endres
 *
 */
public class TestIndices
{
    private Logger logger = Logger.getLogger(TestIndices.class);
    
    public class IndexHandler extends DefaultHandler {

        boolean bsortkey = false;
        boolean btitle = false;
        public ArrayList<String> coreserviceIndices = new ArrayList<String>();
        public ArrayList<String> coreserviceSortingKeys = new ArrayList<String>();
        private String content = null;


        public void endElement(String uri, String localName,
                String qName) throws SAXException {

            if (qName.equalsIgnoreCase("name") ) {
                coreserviceIndices.add(content);           
            }
            if (qName.equalsIgnoreCase("sortKeyword")) {
                coreserviceSortingKeys.add(content);
            }
        }

        public void characters(char ch[], int start, int length)
        throws SAXException {
            content = new String(ch, start, length);
        }  
        public ArrayList<String> getIndices() {
            return coreserviceIndices;
        }
    }

    /**
     * This test is used to compare indices offered by coreservice and indices the search service uses.
     */
    @Test
    public void testCheckIndicesAgainstCoreserver() throws Exception {
        
        // 
        String urlPostfix = "/srw/search/escidoc_all?operation=explain";
        
        InputStream instream = TestIndices.class.getClassLoader().getResource("test.properties").openStream();
        Properties properties = new Properties();
        properties.load(instream);

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        
        IndexHandler handler = new IndexHandler();
        
        String completeUrl = properties.getProperty("escidoc.framework_access.framework.url") + urlPostfix;
        
        logger.info("Fetching indices from URL: " + completeUrl);
        saxParser.parse(completeUrl, handler);
        
        // Check if coreservice has a index we are using
        MetadataSearchCriterion criterion = new MetadataSearchCriterion(CriterionType.ANY);
        for( String index : criterion.getAllSupportedIndicesAsString() ) {
            logger.info("Checking index '" + index + "'");
            logger.info("Index found: " + handler.getIndices().contains(index));
        }
    }    
}
