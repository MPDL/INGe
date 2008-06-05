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

import org.junit.Test;


/**
 * JUnit test class for several search querys with brackets.
 * @author Hugo Niedermaier
 * Revised by NiH: 20.09.2007
 */
public class QueryParserTest
{

//    /**
//     * test search query with three expressions. 
//     * e. g. "bla bla bla"
//     * @throws ParseException
//     */
//    @Test
//    public void testParse() throws ParseException
//    {
//        QueryParser parser = new QueryParser("bla bla bla");
//        String cql1 = parser.parse();
//        assertEquals("bla and bla and bla", cql1);
//    }
//
//    /**
//     * test search query with three expressions and an reuse of the parser instance.
//     * e. g. "bla bla bla" and "hui hui hui"
//     * @throws ParseException
//     */
//    @Test
//    public void testParseString() throws ParseException
//    {
//        QueryParser parser = new QueryParser("bla bla bla");
//        String cql1 = parser.parse();
//        assertEquals("bla and bla and bla", cql1);
//        // use parser instance again
//        String cql2 = parser.parse("hui hui hui");
//        assertEquals("hui and hui and hui", cql2);
//    }
//    
//    /**
//     * test search query with two expressions and two CQL indices.
//     * e. g. "bla bla \"bla blubb\""
//     * @throws ParseException
//     */
//    @Test
//    public void testCQLIndexes() throws ParseException
//    {
//        QueryParser parser = new QueryParser("bla bla \"bla blubb\"");
//        parser.addCQLIndex("i1");
//        parser.addCQLIndex("i2");
//        String cql1 = parser.parse();
//        assertEquals("(i1=bla or i2=bla) and (i1=bla or i2=bla) and (i1=\"bla blubb\" or i2=\"bla blubb\")", cql1);
//    }

}
