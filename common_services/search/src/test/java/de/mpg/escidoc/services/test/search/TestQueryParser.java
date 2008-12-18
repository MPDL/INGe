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
* Copyright 2006-2008 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 
package de.mpg.escidoc.services.test.search;
import static org.junit.Assert.*;

import java.io.Reader;
import java.io.StringReader;

import org.apache.log4j.Logger;
import org.junit.Test;

import de.mpg.escidoc.services.search.parser.ParseException;
import de.mpg.escidoc.services.search.parser.QueryParser;

/**
 * 
 * Unit testing class for QueryParser.
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class TestQueryParser
{
    private Logger logger = Logger.getLogger(TestQueryParser.class);
    
    @Test
    public void testQueryParserSimpleQuery() throws Exception
    {
        String query = ("test suche");;
        QueryParser qp = new QueryParser(query, "=");
        String cqlQuery = qp.parse();
        
        String expected = "\"test\" and \"suche\"";
        assertEquals(expected, cqlQuery);
    }
    
    @Test
    public void testQueryParserCQLIndex() throws Exception
    {
        String query = ("test suche");;
        QueryParser qp = new QueryParser(query, "=");
        qp.addCQLIndex("escidoc.any-title");
        String cqlQuery = qp.parse();
        
        String expected = "escidoc.any-title=\"test\" and escidoc.any-title=\"suche\"";
        assertEquals(expected, cqlQuery);
    }
    
    
    @Test
    public void testQueryParserEmptySearchString() throws Exception
    {
        Reader sr = new StringReader(" ");;
        QueryParser qp = new QueryParser(sr);
        try
        {
            String cqlQuery = qp.parse();
            fail("ParseException expected");
        }
        catch (ParseException e)
        {
            
        }
    }
    
    @Test
    public void testQueryParserWithQuotes() throws Exception
    {
        Reader sr = new StringReader("\"test suche\"");;
        QueryParser qp = new QueryParser(sr);
        String cqlQuery = qp.parse();
        String expected = "\"test suche\"";
        assertEquals(expected, cqlQuery);
    }
    
    @Test
    public void testQueryParserWithOpeningQuote() throws Exception
    {
        Reader sr = new StringReader("\"test suche");;
        QueryParser qp = new QueryParser(sr);
        try
        {
            String cqlQuery = qp.parse();
            fail("ParseException expected");
        }
        catch (ParseException e)
        {
            
        }
        
    }
    
    
    
    /*
     * TODO Single Quotes at the end or in the middle of a word are currently accepted by the parser, but shouldn't be.
     * Parser has to be changed
    @Test
    public void testQueryParserWithClosingQuote() throws Exception
    {
        Reader sr = new StringReader("test suche\"");;
        QueryParser qp = new QueryParser(sr);
        try
        {
            String cqlQuery = qp.parse();
            logger.info(cqlQuery);
            fail("ParseException expected");
        }
        catch (ParseException e)
        {
            
        }
        
    }
    
    @Test
    public void testQueryParserWithMiddleQuote() throws Exception
    {
        Reader sr = new StringReader("test su\"che");;
        QueryParser qp = new QueryParser(sr);
        try
        {
            String cqlQuery = qp.parse();
            logger.info(cqlQuery);
            fail("ParseException expected");
        }
        catch (ParseException e)
        {
            
        }
        
    }
    */
    
    @Test
    public void testQueryParserWithLogicalOperators() throws Exception
    {
        Reader sr = new StringReader("t?est* search? \\oper*ator");;
        QueryParser qp = new QueryParser(sr);
        String cqlQuery = qp.parse();
        String expected = "\"t?est*\" and \"search?\" and \"\\oper*ator\"";
        assertEquals(expected, cqlQuery);
    }
    
    @Test
    public void testQueryParserWithAsteriskAtBeginning() throws Exception
    {
        Reader sr = new StringReader("*test");
        QueryParser qp = new QueryParser(sr);
        try
        {
            String cqlQuery = qp.parse();
            fail("ParseException expected");
        }
        catch (ParseException e)
        {
            
        }
    }
    
    @Test
    public void testQueryParserWithSingleAsterisk() throws Exception
    {
        Reader sr = new StringReader("*");
        QueryParser qp = new QueryParser(sr);
        try
        {
            String cqlQuery = qp.parse();
            fail("ParseException expected");
        }
        catch (ParseException e)
        {
            
        }
    }
    
    @Test
    public void testQueryParserWithSingleQuestionmark() throws Exception
    {
        Reader sr = new StringReader("?");
        QueryParser qp = new QueryParser(sr);
        try
        {
            String cqlQuery = qp.parse();
            fail("ParseException expected");
        }
        catch (ParseException e)
        {
            
        }
    }
    
    @Test
    public void testQueryParserWithQuestionMarkAtBeginning() throws Exception
    {
        Reader sr = new StringReader("?test");
        QueryParser qp = new QueryParser(sr);
        try
        {
            String cqlQuery = qp.parse();
            fail("ParseException expected");
        }
        catch (ParseException e)
        {
            
        }
    }
    
    @Test
    public void testQueryParserWithEscapeCharacters() throws Exception
    {
        Reader sr = new StringReader("+-&|!() {}[]^~");
        QueryParser qp = new QueryParser(sr);
        String cqlQuery = qp.parse();
        String expected = "\"\\+\\-\\&\\|\\!\\(\\)\" and \"\\{\\}\\[\\]\\^\\~\"";
        assertEquals(expected, cqlQuery);
    }
    
    @Test
    public void testQueryParserWithSpecialCharacters() throws Exception
    {
        Reader sr = new StringReader("äöüß/@ §$%=`´'_");;
        QueryParser qp = new QueryParser(sr);
        String cqlQuery = qp.parse();
        String expected = "\"äöüß/@\" and \"§$\\%=`´'_\"";
        assertEquals(expected, cqlQuery);
    }
    
    @Test
    public void testQueryParserWithBooleanOperators() throws Exception
    {
        String query = ("test");
        QueryParser qp = new QueryParser(query, ">=");
        qp.addCQLIndex("escidoc.any-title");
        String cqlQuery = qp.parse();
        String expected = "escidoc.any-title>=\"test\"";
        assertEquals(expected, cqlQuery);
    }
    
    @Test
    public void testQueryParserWithIllegalLogicalOperators() throws Exception
    {
        Reader r = new StringReader("test and or not");
        QueryParser qp = new QueryParser(r);
        String cqlQuery = qp.parse();
        String expected = "\"test\" and \"and\" and \"or\" and \"not\"";
        assertEquals(expected, cqlQuery);
    }
    
    
}
