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

import java.io.StringReader;

import org.junit.Test;


/**
 * JUnit test class for several search querys with constant expressions.
 * @author Hugo Niedermaier
 * Revised by NiH: 20.09.2007
 */
public class ConstantsTest
{
//    /**
//     * test search query with "normal" letters.
//     * e. g. "hello"
//     * @throws ParseException
//     */
//    @Test
//    public void constantOk() throws ParseException
//    {
//        StringReader reader = new StringReader("hello");
//        QueryParser parser = new QueryParser(reader);
//        parser.addCQLIndex("escidoc.metadata");
//        String query = parser.parse();
//        assertEquals("escidoc.metadata=hello",query);
//    }
//
//    /**
//     * test search query with a expression with operator.
//     * e. g. "he(llo"
//     * @throws ParseException
//     */
//    @Test(expected = ParseException.class)
//    public void constantWithOperator() throws ParseException
//    {
//        StringReader reader = new StringReader("he(llo");
//        QueryParser parser = new QueryParser(reader);
//        parser.addCQLIndex("escidoc.metadata");
//        parser.parse();
//    }
//
//    /**
//     * test search query with a experssion with an asterik at start.
//     * e. g. "*hello"
//     * @throws ParseException
//     */
//    @Test(expected = ParseException.class)
//    public void constantWithAsteriskAtStart() throws ParseException
//    {
//        StringReader reader = new StringReader("*hello");
//        QueryParser parser = new QueryParser(reader);
//        parser.addCQLIndex("escidoc.metadata");
//        parser.parse();
//    }
//
//    /**
//     * test search query with a experssion with an asterik in the middle.
//     * e. g. "he*o"
//     * @throws ParseException
//     */
//    @Test
//    public void constantWithAsteriskAtMiddle() throws ParseException
//    {
//        StringReader reader = new StringReader("he*o");
//        QueryParser parser = new QueryParser(reader);
//        parser.addCQLIndex("escidoc.metadata");
//        parser.parse();
//    }
//
//    /**
//     * test search query with a experssion with an asterik at the end.
//     * e. g. "hello*"
//     * @throws ParseException
//     */
//    @Test
//    public void constantWithAsteriskAtEnd() throws ParseException
//    {
//        StringReader reader = new StringReader("hello*");
//        QueryParser parser = new QueryParser(reader);
//        parser.addCQLIndex("escidoc.metadata");
//        parser.parse();
//    }
//
//    /**
//     * test search query with a experssion with a questionmark at start.
//     * e. g. "?hello"
//     * @throws ParseException
//     */
//    @Test(expected = ParseException.class)
//    public void constantWithQuestionmarkAtStart() throws ParseException
//    {
//        StringReader reader = new StringReader("?hello");
//        QueryParser parser = new QueryParser(reader);
//        parser.addCQLIndex("escidoc.metadata");
//        parser.parse();
//    }
//
//    /**
//     * test search query with a experssion with a questionmark in the middle.
//     * e. g. "he?lo"
//     * @throws ParseException
//     */
//    @Test
//    public void constantWithQuestionmarkAtMiddle() throws ParseException
//    {
//        StringReader reader = new StringReader("he?lo");
//        QueryParser parser = new QueryParser(reader);
//        parser.addCQLIndex("escidoc.metadata");
//        parser.parse();
//    }
//
//    /**
//     * test search query with a experssion with a questionmark at the end.
//     * e. g. "hello?"
//     * @throws ParseException
//     */
//    @Test
//    public void constantWithQuestionmarkAtEnd() throws ParseException
//    {
//        StringReader reader = new StringReader("hello?");
//        QueryParser parser = new QueryParser(reader);
//        parser.addCQLIndex("escidoc.metadata");
//        parser.parse();
//    }
//
//    /**
//     * test search query with a experssion with a circumflex at start.
//     * e. g. "^hello"
//     * @throws ParseException
//     */
//    @Test(expected = ParseException.class)
//    public void constantWithCircumflecAccentAtStart() throws ParseException
//    {
//        StringReader reader = new StringReader("^hello");
//        QueryParser parser = new QueryParser(reader);
//        parser.addCQLIndex("escidoc.metadata");
//        parser.parse();
//    }
//
//    /**
//     * test search query with a experssion with a circumflex in the middle.
//     * e. g. "hel^lo"
//     * @throws ParseException
//     */
//    @Test
//    public void constantWithCircumflecAccentAtMiddle() throws ParseException
//    {
//        StringReader reader = new StringReader("hel^lo");
//        QueryParser parser = new QueryParser(reader);
//        parser.addCQLIndex("escidoc.metadata");
//        parser.parse();
//    }
//
//    /**
//     * test search query with a experssion with a circumflex at the end.
//     * e. g. "hello^"
//     * @throws ParseException
//     */
//    @Test
//    public void constantWithCircumflecAccentAtEnd() throws ParseException
//    {
//        StringReader reader = new StringReader("hello^");
//        QueryParser parser = new QueryParser(reader);
//        parser.addCQLIndex("escidoc.metadata");
//        parser.parse();
//    }
}
