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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * JUnit test class for several search querys with brackets.
 * @author Hugo Niedermaier
 * Revised by NiH: 20.09.2007
 */
public class BracketsTest
{
//    /**
//     * test search query with brackets. 
//     * e. g. "(hello)"
//     * @throws ParseException
//     */
//    @Test
//    public void bracketsOk() throws ParseException
//    {
//        QueryParser parser = new QueryParser("(hello)");
//        parser.addCQLIndex("escidoc.metadata");
//        String query = parser.parse();
//        assertEquals("(escidoc.metadata=hello)",query);
//    }
//
//    /**
//     * test search query with empty brackets.
//     * e. g. "()"
//     */
//    @Test
//    public void emptyBrackets()
//    {
//        QueryParser parser = new QueryParser("()");
//        parser.addCQLIndex("escidoc.metadata");
//        try
//        {
//            parser.parse();
//            fail("Exception expected.");
//        }
//        catch(ParseException e)
//        {
//        }
//    }
//
//    /**
//     * test search query with double empty brackets.
//     * e. g. "(())"
//     */
//    @Test
//    public void doubleEmptyBrackets()
//    {
//        QueryParser parser = new QueryParser("(())");
//        parser.addCQLIndex("escidoc.metadata");
//        try
//        {
//            parser.parse();
//            fail("Exception expected.");
//        }
//        catch(ParseException e)
//        {
//        }
//    }
//
//    /**
//     * test search query with a missing leading bracket.
//     * e. g. "hello)"
//     */
//    @Test
//    public void leadingBracketMissing()
//    {
//        QueryParser parser = new QueryParser("hello)");
//        parser.addCQLIndex("escidoc.metadata");
//        try
//        {
//            parser.parse();
//            fail("Exception expected.");
//        }
//        catch(ParseException e)
//        {
//        }
//    }
//
//    /**
//     * test search query with a missing trailing bracket.
//     * e. g. "(hello"
//     */
//    @Test
//    public void trailingBracketMissing()
//    {
//        QueryParser parser = new QueryParser("(hello");
//        parser.addCQLIndex("escidoc.metadata");
//        try
//        {
//            parser.parse();
//            fail("Exception expected.");
//        }
//        catch(ParseException e)
//        {
//        }
//    }
//
//    /**
//     * test search query with several brackets.
//     * e. g. "((hello)(world))"
//     * @throws Exception
//     */
//    @Test
//    public void severalBrackets() throws Exception
//    {
//        QueryParser parser = new QueryParser("((hello)(world))");
//        parser.addCQLIndex("escidoc.metadata");
//        String query = parser.parse();
//        assertEquals("((escidoc.metadata=hello) and (escidoc.metadata=world))",query);
//    }
}
