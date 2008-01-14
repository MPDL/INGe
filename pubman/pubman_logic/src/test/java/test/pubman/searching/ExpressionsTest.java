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
 * JUnit test class for several search querys with several eypressions.
 * @author Hugo Niedermaier
 * Revised by NiH: 20.09.2007
 */
public class ExpressionsTest
{
    /**
     * test search query with an empty eypression.
     * e. g. ""
     */
    @Test
    public void emptyQuery()
    {
        StringReader reader = new StringReader("");
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
     * test search query with brackets an logic operators.
     * e. g. "(hello AND world) OR other"
     * @throws ParseException
     */
    @Test
    public void booleanWithBrackets1() throws ParseException
    {
        StringReader reader = new StringReader("(hello AND world) OR other");
        QueryParser parser = new QueryParser(reader);
        parser.addCQLIndex("escidoc.metadata");
        String query = parser.parse();
        assertEquals("(escidoc.metadata=hello and escidoc.metadata=world) or escidoc.metadata=other",query);
    }
    
    /**
     * test search query with brackets an logic operators.
     * e. g. "hello AND (world OR other)"
     * @throws ParseException
     */
    @Test
    public void booleanWithBrackets2() throws ParseException
    {
        StringReader reader = new StringReader("hello AND (world OR other)");
        QueryParser parser = new QueryParser(reader);
        parser.addCQLIndex("escidoc.metadata");
        String query = parser.parse();
        assertEquals("escidoc.metadata=hello and (escidoc.metadata=world or escidoc.metadata=other)",query);
    }
    
    /**
     * test search query with illegal brackets.
     * e. g. "hello AND world(OR other)"
     */
    @Test
    public void booleanWithIllegalBrackets()
    {
        StringReader reader = new StringReader("hello AND world(OR other)");
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
     * test search query with several escape characters.
     * e. g. "Mr \"OR\" Mrs NOT Teacher but \"Exactly \\\"this stuff\""
     * @throws ParseException
     */
    @Test
    public void muchEscaping() throws ParseException
    {
        StringReader reader = new StringReader("Mr \"OR\" Mrs NOT Teacher but \"Exactly \\\"this stuff\"");
        QueryParser parser = new QueryParser(reader);
        parser.addCQLIndex("escidoc.metadata");
        String query = parser.parse();
        assertEquals("escidoc.metadata=Mr and escidoc.metadata=\"OR\" and escidoc.metadata=Mrs not escidoc.metadata=Teacher and escidoc.metadata=but and escidoc.metadata=\"Exactly \\\"this stuff\"",query);
    }

    /**
     * test search query with several illegal escape characters.
     * e. g. "NOT Teacher but \"Exactly \"this stuff\""
     */
    @Test
    public void muchWrongEscaping()
    {
        StringReader reader = new StringReader("NOT Teacher but \"Exactly \"this stuff\"");
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
     * test search query with several brackets.
     * e. g. "(((The) (quick) (brown)) ((fox) (jumps)) ((over) (the)) ((lazy) (dog)(.)))"
     * @throws ParseException
     */
    @Test
    public void manyBrackets() throws ParseException
    {
        StringReader reader = new StringReader("(((The) (quick) (brown)) ((fox) (jumps)) ((over) (the)) ((lazy) (dog)(.)))");
        QueryParser parser = new QueryParser(reader);
        parser.addCQLIndex("escidoc.metadata");
        String query = parser.parse();
        assertEquals("(((escidoc.metadata=The) and (escidoc.metadata=quick) and (escidoc.metadata=brown)) and " +
                     "((escidoc.metadata=fox) and (escidoc.metadata=jumps)) and ((escidoc.metadata=over) and " + 
                     "(escidoc.metadata=the)) and ((escidoc.metadata=lazy) and (escidoc.metadata=dog) and (escidoc.metadata=.)))",query);
    }

    /**
     * test search query with several brackets and logic operators.
     * e. g. "(((The)NOT(quick)AND(brown))((fox)DOES(jump))NOT((over)NOT(the))NOT((lazy)AND(dog).))"
     * @throws ParseException
     */
    @Test
    public void manyBracketsAndOperators() throws ParseException
    {
        StringReader reader = new StringReader("(((The)NOT(quick)AND(brown))((fox)DOES(jump))NOT((over)NOT(the))NOT((lazy)AND(dog).))");
        QueryParser parser = new QueryParser(reader);
        parser.addCQLIndex("escidoc.metadata");
        String query = parser.parse();
        assertEquals("(((escidoc.metadata=The) not (escidoc.metadata=quick) and (escidoc.metadata=brown)) and " +
                "((escidoc.metadata=fox) and escidoc.metadata=DOES and (escidoc.metadata=jump)) not ((escidoc.metadata=over) not " + 
                "(escidoc.metadata=the)) not ((escidoc.metadata=lazy) and (escidoc.metadata=dog) and escidoc.metadata=.))",query);
    }
    
    /**
     * test search query with brackets an illegal logic operators.
     * e. g. "(((The)NOT(quick)AND NOT(brown))((fox)DOES(jump))NOT((over)NOT(the))NOT((lazy)AND(dog).))"
     */
    @Test
    public void manyBracketsAndOperatorsNOTallowed()
    {
        StringReader reader = new StringReader("(((The)NOT(quick)AND NOT(brown))((fox)DOES(jump))NOT((over)NOT(the))NOT((lazy)AND(dog).))");
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
     * test search query with several brackets and logic operators with missing brackets.
     * e. g. "(((The)\"NOT(quick)AND(brown))\"((fox)DOES(jump))NOT((over)NOT(the))NOT((lazy)AND(dog).))"
     */
    @Test
    public void manyBracketsAndOperatorsWithMissingBracket()
    {
        StringReader reader = new StringReader("(((The)\"NOT(quick)AND(brown))\"((fox)DOES(jump))NOT((over)NOT(the))NOT((lazy)AND(dog).))");
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
