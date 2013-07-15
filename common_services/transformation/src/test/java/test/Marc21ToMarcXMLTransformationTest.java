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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.transformation.TransformationBean;
import de.mpg.escidoc.services.transformation.Util;
import de.mpg.escidoc.services.transformation.exceptions.TransformationNotSupportedException;
import de.mpg.escidoc.services.transformation.transformations.commonPublicationFormats.marc.Marc21ToMarcXMLTransformation;
import de.mpg.escidoc.services.transformation.valueObjects.Format;

/*
 * @author Stefan Krause, Editura GmbH & Co. KG  (initial creation)
 * @author $Author: skrause $ (last modification)
 * @version $Revision: 261 $ $LastChangedDate: 2013-04-30 20:57:29 +0200 (Di, 30 Apr 2013) $
 */

public class Marc21ToMarcXMLTransformationTest {

	public static final Format MARC21_FORMAT = new Format("marc21", "application/marc", "");
	public static final Format MARCXML_FORMAT_RECEIVED = new Format("marcxml", "application/marcxml+xml", "*");
	public static final Format MARCXML_FORMAT_REQUEST = new Format("marcxml", "application/marcxml+xml", "UTF-8");
	   
    private Marc21ToMarcXMLTransformation marc21ToMarcXML_transf = new Marc21ToMarcXMLTransformation();
	
	byte[] src = null;
	
	String expected = null;
	
	{
		try
			{
			 	src = ResourceUtil.getResourceAsBytes("testFiles/marc/simplemarcrecord.mrc");
				expected = ResourceUtil.getResourceAsString("testFiles/marcxml/simplemarcrecord.xml");
			}
		catch (Exception e)
			{
				throw new RuntimeException("Can't open file", e);
			}
	}
	
	@Test
	public void testGetSourceFormats()
		{
			assertEquals(MARC21_FORMAT, marc21ToMarcXML_transf.getSourceFormats()[0]);
		}

	@Test
	public void testGetSourceFormatsFormat()
		{
			assertEquals(MARC21_FORMAT, marc21ToMarcXML_transf.getSourceFormats(MARCXML_FORMAT_REQUEST)[0]);
		}

	@Test
	public void testGetTargetFormatsFormat()
		{
			assertEquals(MARCXML_FORMAT_RECEIVED, marc21ToMarcXML_transf.getTargetFormats(MARC21_FORMAT)[0]);
		}

	@Test
	public void testTransformByteArrayStringStringStringStringStringStringString() throws TransformationNotSupportedException, RuntimeException
		{
			String actual = new String(marc21ToMarcXML_transf.transform(src, "marc21", "application/marc", "", "marcxml", "application/marcxml+xml", "UTF-8", null)) + "\n";
			assertEquals(expected, actual);
		}

	@Test
	public void testTransformByteArrayFormatFormatString() throws TransformationNotSupportedException, RuntimeException
		{
			Marc21ToMarcXMLTransformation transf = new Marc21ToMarcXMLTransformation();
			String actual = new String(transf.transform(src, MARC21_FORMAT, MARCXML_FORMAT_REQUEST, null)) + "\n";
			assertEquals(expected, actual);
			transf = null;
		}

	@Test
	public void testTransformByteArrayFormatFormatStringMapOfStringString() throws TransformationNotSupportedException, RuntimeException
		{
			String actual = new String(marc21ToMarcXML_transf.transform(src, MARC21_FORMAT, MARCXML_FORMAT_REQUEST, null, null)) + "\n"; //somewhere in the chain a \n was added
			assertEquals(expected, actual);
		}

	@Test
	public void testGetConfiguration() throws Exception {
		Map<String, String> actual = marc21ToMarcXML_transf.getConfiguration(MARC21_FORMAT, MARCXML_FORMAT_REQUEST);
		Map<String, String> expected = Collections.<String,String>emptyMap();
		assertEquals(expected, actual);
	}

	@Test
	public void testGetConfigurationValues() throws Exception {
		List<String> actual = marc21ToMarcXML_transf.getConfigurationValues(MARC21_FORMAT, MARCXML_FORMAT_REQUEST, "nevermind");
		List<String> expected = null;
		assertEquals(expected, actual);
	}

	@Test(expected=TransformationNotSupportedException.class)
	public void testWrongSourceFormat() throws TransformationNotSupportedException, RuntimeException
		{
			String actual = new String(marc21ToMarcXML_transf.transform(src, MARCXML_FORMAT_REQUEST, MARCXML_FORMAT_REQUEST, null, null)) + "\n"; //somewhere in the chain a \n was added
			assertEquals("", actual);
		}

	@Test(expected=TransformationNotSupportedException.class)
	public void testWrongTargetFormat() throws TransformationNotSupportedException, RuntimeException
		{
			String actual = new String(marc21ToMarcXML_transf.transform(src, MARC21_FORMAT, MARC21_FORMAT, null, null)) + "\n"; //somewhere in the chain a \n was added
			assertEquals("", actual);
		}

	
	@Test
	public void testTransformationBean()
		{
			TransformationBean transformationbean = new TransformationBean(true);

			assertTrue("missing MARCXML_FORMAT in transformationbean.getTargetFormats(MARC21_FORMAT)", 
					Util.containsFormat(transformationbean.getTargetFormats(MARC21_FORMAT), MARCXML_FORMAT_RECEIVED));
			
			assertTrue("missing MARC21_FORMAT in transformationbean.getSourceFormats(MARCXML_FORMAT_REQUEST)",
					Util.containsFormat(transformationbean.getSourceFormats(MARCXML_FORMAT_REQUEST), MARC21_FORMAT));

			assertTrue("checkTransformation MARC21_FORMAT->MARCXML_FORMAT_REQUEST failed", transformationbean.checkTransformation(MARC21_FORMAT, MARCXML_FORMAT_REQUEST));
			assertTrue("checkTransformation MARC21_FORMAT->MARCXML_FORMAT_RECEIVED failed", transformationbean.checkTransformation(MARC21_FORMAT, MARCXML_FORMAT_RECEIVED));
			
			transformationbean = null;
		}

}
