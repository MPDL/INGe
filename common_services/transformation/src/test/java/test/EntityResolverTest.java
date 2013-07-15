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
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */ 

package test;

import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.xml.resolver.CatalogManager;
import org.apache.xml.resolver.tools.CatalogResolver;
import org.junit.Test;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;

/*
 * @author Stefan Krause, Editura GmbH & Co. KG  (initial creation)
 * @author $Author: skrause $ (last modification)
 * @version $Revision: 261 $ $LastChangedDate: 2013-04-30 20:57:29 +0200 (Di, 30 Apr 2013) $
 */

public class EntityResolverTest {

	@Test
	public void test() throws SAXException, IOException
		{
			int savedVerbosity = CatalogManager.getStaticManager().getVerbosity();
			CatalogManager.getStaticManager().setVerbosity(10);
			EntityResolver entityresolver = new CatalogResolver();
			
			assertFalse("Can't resolve -//BMC//DTD FULL LENGTH ARTICLE//EN", entityresolver.resolveEntity("-//BMC//DTD FULL LENGTH ARTICLE//EN", "") == null);
			assertFalse("Can't resolve http://www.biomedcentral.com/xml/article.dtd", entityresolver.resolveEntity("", "http://www.biomedcentral.com/xml/article.dtd") == null);
			assertFalse("Can't resolve http://www.biomedcentral.com/xml/MathML2/mathml2.dtd", entityresolver.resolveEntity("", "http://www.biomedcentral.com/xml/MathML2/mathml2.dtd") == null);
			
			assertFalse("Can't resolve -//W3C//ENTITIES MathML 2.0 Qualified Names 1.0//EN", entityresolver.resolveEntity("-//W3C//ENTITIES MathML 2.0 Qualified Names 1.0//EN", "") == null);
		    assertFalse("Can't resolve -//W3C//ENTITIES Added Math Symbols: Arrow Relations for MathML 2.0//EN", entityresolver.resolveEntity("-//W3C//ENTITIES Added Math Symbols: Arrow Relations for MathML 2.0//EN", "") == null);
		    assertFalse("Can't resolve -//W3C//ENTITIES Added Math Symbols: Binary Operators for MathML 2.0//EN", entityresolver.resolveEntity("-//W3C//ENTITIES Added Math Symbols: Binary Operators for MathML 2.0//EN", "") == null);
		    assertFalse("Can't resolve -//W3C//ENTITIES Added Math Symbols: Delimiters for MathML 2.0//EN", entityresolver.resolveEntity("-//W3C//ENTITIES Added Math Symbols: Delimiters for MathML 2.0//EN", "") == null);
		    assertFalse("Can't resolve -//W3C//ENTITIES Added Math Symbols: Negated Relations for MathML 2.0//EN", entityresolver.resolveEntity("-//W3C//ENTITIES Added Math Symbols: Negated Relations for MathML 2.0//EN", "") == null);
		    assertFalse("Can't resolve -//W3C//ENTITIES Added Math Symbols: Ordinary for MathML 2.0//EN", entityresolver.resolveEntity("-//W3C//ENTITIES Added Math Symbols: Ordinary for MathML 2.0//EN", "") == null);
		    assertFalse("Can't resolve -//W3C//ENTITIES Added Math Symbols: Relations for MathML 2.0//EN", entityresolver.resolveEntity("-//W3C//ENTITIES Added Math Symbols: Relations for MathML 2.0//EN", "") == null);
		    assertFalse("Can't resolve -//W3C//ENTITIES Greek Symbols for MathML 2.0//EN", entityresolver.resolveEntity("-//W3C//ENTITIES Greek Symbols for MathML 2.0//EN", "") == null);
		    assertFalse("Can't resolve -//W3C//ENTITIES Math Alphabets: Fraktur for MathML 2.0//EN", entityresolver.resolveEntity("-//W3C//ENTITIES Math Alphabets: Fraktur for MathML 2.0//EN", "") == null);
		    assertFalse("Can't resolve -//W3C//ENTITIES Math Alphabets: Open Face for MathML 2.0//EN", entityresolver.resolveEntity("-//W3C//ENTITIES Math Alphabets: Open Face for MathML 2.0//EN", "") == null);
		    assertFalse("Can't resolve -//W3C//ENTITIES Math Alphabets: Script for MathML 2.0//EN", entityresolver.resolveEntity("-//W3C//ENTITIES Math Alphabets: Script for MathML 2.0//EN", "") == null);
		    assertFalse("Can't resolve -//W3C//ENTITIES General Technical for MathML 2.0//EN", entityresolver.resolveEntity("-//W3C//ENTITIES General Technical for MathML 2.0//EN", "") == null);
		    assertFalse("Can't resolve -//W3C//ENTITIES Box and Line Drawing for MathML 2.0//EN", entityresolver.resolveEntity("-//W3C//ENTITIES Box and Line Drawing for MathML 2.0//EN", "") == null);
		    assertFalse("Can't resolve -//W3C//ENTITIES Russian Cyrillic for MathML 2.0//EN", entityresolver.resolveEntity("-//W3C//ENTITIES Russian Cyrillic for MathML 2.0//EN", "") == null);
		    assertFalse("Can't resolve -//W3C//ENTITIES Non-Russian Cyrillic for MathML 2.0//EN", entityresolver.resolveEntity("-//W3C//ENTITIES Non-Russian Cyrillic for MathML 2.0//EN", "") == null);
		    assertFalse("Can't resolve -//W3C//ENTITIES Diacritical Marks for MathML 2.0//EN", entityresolver.resolveEntity("-//W3C//ENTITIES Diacritical Marks for MathML 2.0//EN", "") == null);
		    assertFalse("Can't resolve -//W3C//ENTITIES Added Latin 1 for MathML 2.0//EN", entityresolver.resolveEntity("-//W3C//ENTITIES Added Latin 1 for MathML 2.0//EN", "") == null);
		    assertFalse("Can't resolve -//W3C//ENTITIES Added Latin 2 for MathML 2.0//EN", entityresolver.resolveEntity("-//W3C//ENTITIES Added Latin 2 for MathML 2.0//EN", "") == null);
		    assertFalse("Can't resolve -//W3C//ENTITIES Numeric and Special Graphic for MathML 2.0//EN", entityresolver.resolveEntity("-//W3C//ENTITIES Numeric and Special Graphic for MathML 2.0//EN", "") == null);
		    assertFalse("Can't resolve -//W3C//ENTITIES Publishing for MathML 2.0//EN", entityresolver.resolveEntity("-//W3C//ENTITIES Publishing for MathML 2.0//EN", "") == null);
		    assertFalse("Can't resolve -//W3C//ENTITIES Extra for MathML 2.0//EN", entityresolver.resolveEntity("-//W3C//ENTITIES Extra for MathML 2.0//EN", "") == null);
		    assertFalse("Can't resolve -//W3C//ENTITIES Aiases for MathML 2.0//EN", entityresolver.resolveEntity("-//W3C//ENTITIES Aiases for MathML 2.0//EN", "") == null);
		    
			assertFalse("Can't resolve http://www.biomedcentral.com/xml/CALS/calstbl.dtd", entityresolver.resolveEntity("", "http://www.biomedcentral.com/xml/CALS/calstbl.dtd") == null);
			
			CatalogManager.getStaticManager().setVerbosity(savedVerbosity);
		}
}
