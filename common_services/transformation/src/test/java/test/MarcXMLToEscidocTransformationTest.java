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

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.custommonkey.xmlunit.XMLTestCase;
import org.junit.Test;
import org.xml.sax.SAXException;

import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.transformation.TransformationBean;
import de.mpg.escidoc.services.transformation.Util;
import de.mpg.escidoc.services.transformation.valueObjects.Format;

import de.mpg.escidoc.services.transformation.exceptions.TransformationNotSupportedException;
import de.mpg.escidoc.services.transformation.transformations.commonPublicationFormats.marc.MarcXMLToEscidocTransformation;

/*
 * @author Stefan Krause, Editura GmbH & Co. KG  (initial creation)
 * @author $Author: skrause $ (last modification)
 * @version $Revision: 261 $ $LastChangedDate: 2013-04-30 20:57:29 +0200 (Di, 30 Apr 2013) $
 */

public class MarcXMLToEscidocTransformationTest extends XMLTestCase {

	private static final Format MARCXML_FORMAT = new Format("marcxml", "application/marcxml+xml", "UTF-8");
	private static final Format MARC21VIAXML_FORMAT = new Format("marc21viaxml", "application/marc", "UTF-8");
    private static final Format ESCIDOC_ITEM_FORMAT_RECEIVE = new Format("eSciDoc-publication-item", "application/xml", "*");
	private static final Format ESCIDOC_ITEM_LIST_FORMAT_RECEIVE = new Format("eSciDoc-publication-item-list", "application/xml", "*");
	private static final Format ESCIDOC_ITEM_FORMAT_REQUEST = new Format("eSciDoc-publication-item", "application/xml", "ISO-8859-1");
	private static final Format ESCIDOC_ITEM_LIST_FORMAT_REQUEST = new Format("eSciDoc-publication-item-list", "application/xml", "UTF-8");

	private MarcXMLToEscidocTransformation m2etransf = new MarcXMLToEscidocTransformation();
	
	byte[] src = null;
	
	String expected = null;
	
	{
		try
			{
			 	src = ResourceUtil.getResourceAsBytes("testFiles/marcxml/simplemarcrecord.xml");
				expected = ResourceUtil.getResourceAsString("testFiles/escidoc/simplemarcrecord.xml");
			}
		catch (Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException("Can't open file", e);
			}
	}
	
	@Test
	public void testGetSourceFormats() {
		assertEquals("expected length of m2etransf.getSourceFormats(): 2, delivered: " + m2etransf.getSourceFormats().length,
						2,
						m2etransf.getSourceFormats().length
						);
		assertEquals(MARCXML_FORMAT, m2etransf.getSourceFormats()[0]);
		assertEquals(MARC21VIAXML_FORMAT, m2etransf.getSourceFormats()[1]);
		}

	@Test
	public void testGetSourceFormatsFormat() {
		final Format[] result_item = m2etransf.getSourceFormats(ESCIDOC_ITEM_FORMAT_REQUEST);
		final Format[] result_list = m2etransf.getSourceFormats(ESCIDOC_ITEM_LIST_FORMAT_REQUEST);
		
		assertEquals("expected length of result_item: 2, delivered: " + result_item.length, 2, result_item.length);
		assertEquals("expected length of result_list: 2, delivered: " + result_list.length, 2, result_list.length);
		
		assertEquals(MARCXML_FORMAT, m2etransf.getSourceFormats(ESCIDOC_ITEM_FORMAT_RECEIVE)[0]);
		assertEquals(MARC21VIAXML_FORMAT, m2etransf.getSourceFormats(ESCIDOC_ITEM_FORMAT_RECEIVE)[1]);
		assertEquals(MARCXML_FORMAT, m2etransf.getSourceFormats(ESCIDOC_ITEM_LIST_FORMAT_REQUEST)[0]);
		assertEquals(MARC21VIAXML_FORMAT, m2etransf.getSourceFormats(ESCIDOC_ITEM_LIST_FORMAT_REQUEST)[1]);
		}

	@Test
	public void testGetTargetFormats()
		{
			//with XMLTestCase 1.0 fails assertEquals(new Format[]{ESCIDOC_ITEM_FORMAT_RECEIVE, ESCIDOC_ITEM_LIST_FORMAT_RECEIVE}, m2etransf.getTargetFormats(MARCXML_FORMAT)); Stf, 2013-03-24
			Format[] gettedFormats = m2etransf.getTargetFormats(MARCXML_FORMAT);
			assertEquals(ESCIDOC_ITEM_FORMAT_RECEIVE, gettedFormats[0]);
			assertEquals(ESCIDOC_ITEM_LIST_FORMAT_RECEIVE, gettedFormats[1]);
		}

	@Test
	public void testTransformByteArrayFormatFormatString() throws TransformationNotSupportedException, RuntimeException, SAXException, IOException, ParserConfigurationException
		{
			assertXMLEqual(
							expected,
							new String(
										m2etransf.transform(src,
												MARCXML_FORMAT,
												ESCIDOC_ITEM_LIST_FORMAT_REQUEST,
												"")
									) + "\n" // somewhere in the pipeline was an\n added
						);
		}

	@Test
	public void testTransformByteArrayFormatFormatStringStringMap() throws TransformationNotSupportedException, RuntimeException, SAXException, IOException, ParserConfigurationException
		{
			Map <String,String> config = new HashMap<String, String>();
			config.put("{http://escidoc.de/core/01/structural-relations/}origin", "something");
			
			assertXMLEqual(
							expected,
							new String(
									m2etransf.transform(src,
											MARCXML_FORMAT,
											ESCIDOC_ITEM_LIST_FORMAT_REQUEST,
											"",
											config
										)
									).replace("something", "Pubman File-Import") + "\n" // somewhere in the pipeline was an\n added
					);
			
		}
	
	@Test
	public void testTransformByteArrayStringStringStringStringStringStringString() throws Exception
		{
			assertXMLEqual(
							expected,
							new String(
										m2etransf.transform(
													src, 
													"marc21viaxml", "application/marc", "*",
													"eSciDoc-publication-item-list", "application/xml", "*",
													"")
									) + "\n" // somewhere in the pipeline was an\n added
						);
		}
	
	@Test
	public void testGetConfiguration() throws Exception
		{
			Map <String,String> config = new HashMap<String, String>();
			config.put("CoNE", "true");
			
			assertEquals(config, m2etransf.getConfiguration(MARCXML_FORMAT, ESCIDOC_ITEM_FORMAT_RECEIVE));
		}
	
	@Test
	public void testGetConfigurationValues() throws Exception
		{
			List<String> coneValues = Arrays.asList("true", "false");
			
			assertEquals(coneValues, m2etransf.getConfigurationValues(MARCXML_FORMAT, ESCIDOC_ITEM_FORMAT_RECEIVE, "CoNE"));
		}
	
	@Test
	public void testTransformationBean()
	{
		TransformationBean transformationbean = new TransformationBean(true);
		
		// getTargetFormats(MARCXML_FORMAT)
		assertTrue("missing ESCIDOC_ITEM_FORMAT_RECEIVE in transformationbean.getTargetFormats(MARCXML_FORMAT)",
				Util.containsFormat(transformationbean.getTargetFormats(MARCXML_FORMAT), ESCIDOC_ITEM_FORMAT_RECEIVE));
		
		assertTrue("missing ESCIDOC_ITEM_LIST_FORMAT_RECEIVE in transformationbean.getTargetFormats(MARCXML_FORMAT)",
				Util.containsFormat(transformationbean.getTargetFormats(MARCXML_FORMAT), ESCIDOC_ITEM_LIST_FORMAT_RECEIVE));
		
		// getTargetFormats(MARC21VIAXML_FORMAT)
		assertTrue("missing ESCIDOC_ITEM_FORMAT_RECEIVE in transformationbean.getTargetFormats(MARC21VIAXML_FORMAT)",
				Util.containsFormat(transformationbean.getTargetFormats(MARC21VIAXML_FORMAT), ESCIDOC_ITEM_FORMAT_RECEIVE));
		
		assertTrue("missing ESCIDOC_ITEM_LIST_FORMAT_RECEIVE in transformationbean.getTargetFormats(MARC21VIAXML_FORMAT)",
				Util.containsFormat(transformationbean.getTargetFormats(MARC21VIAXML_FORMAT), ESCIDOC_ITEM_LIST_FORMAT_RECEIVE));
		
		// getSourceFormats(ESCIDOC_ITEM_FORMAT_REQUEST)
		assertTrue("missing MARCXML_FORMAT in transformationbean.getSourceFormats(ESCIDOC_ITEM_FORMAT_REQUEST",
				Util.containsFormat(transformationbean.getSourceFormats(ESCIDOC_ITEM_FORMAT_REQUEST), MARCXML_FORMAT));
		
		assertTrue("missing MARC21VIAXML_FORMAT in transformationbean.getSourceFormats(ESCIDOC_ITEM_FORMAT_REQUEST",
				Util.containsFormat(transformationbean.getSourceFormats(ESCIDOC_ITEM_FORMAT_REQUEST), MARC21VIAXML_FORMAT));
		
		// getSourceFormats(ESCIDOC_ITEM_LIST_FORMAT_REQUEST)
		assertTrue("missing MARCXML_FORMAT in transformationbean.getSourceFormats(ESCIDOC_ITEM_LIST_FORMAT_REQUEST",
				Util.containsFormat(transformationbean.getSourceFormats(ESCIDOC_ITEM_LIST_FORMAT_REQUEST), MARCXML_FORMAT));
		assertTrue("missing MARC21VIAXML_FORMAT in transformationbean.getSourceFormats(ESCIDOC_ITEM_LIST_FORMAT_REQUEST",
				Util.containsFormat(transformationbean.getSourceFormats(ESCIDOC_ITEM_LIST_FORMAT_REQUEST), MARC21VIAXML_FORMAT));
	
		assertTrue("checkTransformation MARCXML_FORMAT->ESCIDOC_ITEM_FORMAT_REQUEST failed", transformationbean.checkTransformation(MARCXML_FORMAT, ESCIDOC_ITEM_FORMAT_REQUEST));
		assertTrue("checkTransformation MARCXML_FORMAT->ESCIDOC_ITEM_LIST_FORMAT_REQUEST failed", transformationbean.checkTransformation(MARCXML_FORMAT, ESCIDOC_ITEM_LIST_FORMAT_REQUEST));
		assertTrue("checkTransformation MARC21VIAXML_FORMAT->ESCIDOC_ITEM_FORMAT_REQUEST failed", transformationbean.checkTransformation(MARC21VIAXML_FORMAT, ESCIDOC_ITEM_FORMAT_REQUEST));
		assertTrue("checkTransformation MARC21VIAXML_FORMAT->ESCIDOC_ITEM_LIST_FORMAT_REQUEST failed", transformationbean.checkTransformation(MARC21VIAXML_FORMAT, ESCIDOC_ITEM_LIST_FORMAT_REQUEST));
		
		assertTrue("checkTransformation MARCXML_FORMAT->ESCIDOC_ITEM_FORMAT_RECEIVE failed", transformationbean.checkTransformation(MARCXML_FORMAT, ESCIDOC_ITEM_FORMAT_RECEIVE));
		assertTrue("checkTransformation MARCXML_FORMAT->ESCIDOC_ITEM_LIST_FORMAT_RECEIVE failed", transformationbean.checkTransformation(MARCXML_FORMAT, ESCIDOC_ITEM_LIST_FORMAT_RECEIVE));
		assertTrue("checkTransformation MARC21VIAXML_FORMAT->ESCIDOC_ITEM_FORMAT_RECEIVE failed", transformationbean.checkTransformation(MARC21VIAXML_FORMAT, ESCIDOC_ITEM_FORMAT_RECEIVE));
		assertTrue("checkTransformation MARC21VIAXML_FORMAT->ESCIDOC_ITEM_LIST_FORMAT_RECEIVE failed", transformationbean.checkTransformation(MARC21VIAXML_FORMAT, ESCIDOC_ITEM_LIST_FORMAT_RECEIVE));
		
		transformationbean = null;
	}
}
