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

import java.io.StringReader;

import org.junit.Test;


/**
 * JUnit test class for search querys with phrases.
 * @author Hugo Niedermaier
 * Revised by NiH: 20.09.2007
 */
public class PhrasesTest
{
//    /**
//     * test search query with an correct phrase. 
//     * e. g. "\"hello world\""
//     * @throws ParseException
//     */
//    @Test
//    public void phraseOk() throws ParseException
//    {
//        StringReader reader = new StringReader("\"hello world\"");
//        QueryParser parser = new QueryParser(reader);
//        parser.addCQLIndex("escidoc.metadata");
//        String query = parser.parse();
//        assertEquals("escidoc.metadata=\"hello world\"",query);
//    }
//
//    /**
//     * test search query with an phrase without an trailing quote. 
//     * e. g. "hello \"hello world"
//     */
//    @Test
//    public void phraseWithoutTrailingQuote()
//    {
//        StringReader reader = new StringReader("hello \"hello world");
//        QueryParser parser = new QueryParser(reader);
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
}
