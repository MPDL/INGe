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

package test.pubman.searching;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.StringReader;

import org.junit.Test;

import de.mpg.escidoc.services.pubman.searching.ParseException;
import de.mpg.escidoc.services.pubman.searching.QueryParser;

/**
 * JUnit test class for search querys with several logic operators.
 * @author Hugo Niedermaier
 * Revised by NiH: 20.09.2007
 */
public class OperationsTest
{
    /**
     * test search query with AND. 
     * e. g. "hello AND world"
     * @throws ParseException
     */
    @Test
    public void andOk() throws ParseException
    {
        StringReader reader = new StringReader("hello AND world");
        QueryParser parser = new QueryParser(reader);
        parser.addCQLIndex("escidoc.metadata");
        String query = parser.parse();
        assertEquals("escidoc.metadata=hello and escidoc.metadata=world",query);
    }

    /**
     * test search query with AND and missing right term. 
     * e. g. "hello AND"
     */
    @Test
    public void andRightTermMissing()
    {
        StringReader reader = new StringReader("hello AND");
        QueryParser parser = new QueryParser(reader);
        parser.addCQLIndex("escidoc.metadata");
        try
        {
            parser.parse();
            fail("Exception expected.");
        }
        catch(ParseException e)
        {
        }
    }

    /**
     * test search query with AND and missing left term. 
     * e. g. "AND hello"
     */
    @Test
    public void andLeftTermMissing()
    {
        StringReader reader = new StringReader("AND hello");
        QueryParser parser = new QueryParser(reader);
        parser.addCQLIndex("escidoc.metadata");
        try
        {
            parser.parse();
            fail("Exception expected.");
        }
        catch(ParseException e)
        {
        }
    }

    /**
     * test search query with OR. 
     * e. g. "hello OR world"
     * @throws ParseException
     */
    @Test
    public void orOk() throws ParseException
    {
        StringReader reader = new StringReader("hello OR world");
        QueryParser parser = new QueryParser(reader);
        parser.addCQLIndex("escidoc.metadata");
        String query = parser.parse();
        assertEquals("escidoc.metadata=hello or escidoc.metadata=world",query);
    }

    /**
     * test search query with OR and right term missing. 
     * e. g. "hello OR"
     */
    @Test
    public void orRightTermMissing()
    {
        StringReader reader = new StringReader("hello OR");
        QueryParser parser = new QueryParser(reader);
        parser.addCQLIndex("escidoc.metadata");
        try
        {
            parser.parse();
            fail("Exception expected.");
        }
        catch(ParseException e)
        {
        }
    }

    /**
     * test search query with OR and missing left term. 
     * e. g. "OR hello"
     */
    @Test
    public void orLeftTermMissing()
    {
        StringReader reader = new StringReader("OR hello");
        QueryParser parser = new QueryParser(reader);
        parser.addCQLIndex("escidoc.metadata");
        try
        {
            parser.parse();
            fail("Exception expected.");
        }
        catch(ParseException e)
        {
        }
    }

    /**
     * test search query with NOT. 
     * e. g. "hello NOT world"
     * @throws ParseException
     */
    @Test
    public void notOk() throws ParseException
    {
        StringReader reader = new StringReader("hello NOT world");
        QueryParser parser = new QueryParser(reader);
        parser.addCQLIndex("escidoc.metadata");
        String query = parser.parse();
        assertEquals("escidoc.metadata=hello not escidoc.metadata=world",query);
    }

    /**
     * test search query with NOT and right term missing. 
     * e. g. "hello NOT"
     */
    @Test
    public void notRightTermMissing()
    {
        StringReader reader = new StringReader("hello NOT");
        QueryParser parser = new QueryParser(reader);
        parser.addCQLIndex("escidoc.metadata");
        try
        {
            parser.parse();
            fail("Exception expected.");
        }
        catch(ParseException e)
        {
        }
    }

    /**
     * test search query with NOT and left term missing. 
     * e. g. "NOT hello"
     */
    @Test
    public void notLeftTermMissing()
    {
        StringReader reader = new StringReader("NOT hello");
        QueryParser parser = new QueryParser(reader);
        parser.addCQLIndex("escidoc.metadata");
        try
        {
            parser.parse();
            fail("Exception expected.");
        }
        catch(ParseException e)
        {
        }
    }
}
