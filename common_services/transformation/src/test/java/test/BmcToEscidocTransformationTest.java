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
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import org.custommonkey.xmlunit.XMLTestCase;
import org.junit.Test;
import org.xml.sax.SAXException;

import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.transformation.TransformationBean;
import de.mpg.escidoc.services.transformation.Util;
import de.mpg.escidoc.services.transformation.valueObjects.Format;

import de.mpg.escidoc.services.transformation.exceptions.TransformationNotSupportedException;
import de.mpg.escidoc.services.transformation.transformations.commonPublicationFormats.bmc.BmcToEscidocTransformation;

/*
 * @author Stefan Krause, Editura GmbH & Co. KG  (initial creation)
 * @author $Author: skrause $ (last modification)
 * @version $Revision: 261 $ $LastChangedDate: 2013-04-30 20:57:29 +0200 (Di, 30 Apr 2013) $
 */

public class BmcToEscidocTransformationTest extends XMLTestCase {

	private static final Format BMC_FORMAT = new Format("bmc_editura", "application/xml", "UTF-8");
	private static final Format ESCIDOC_ITEM_FORMAT_RECEIVE = new Format("eSciDoc-publication-item", "application/xml", "*");
	private static final Format ESCIDOC_ITEM_LIST_FORMAT_RECEIVE = new Format("eSciDoc-publication-item-list", "application/xml", "*");
	private static final Format ESCIDOC_ITEM_FORMAT_REQUEST = new Format("eSciDoc-publication-item", "application/xml", "UTF-8");
	private static final Format ESCIDOC_ITEM_LIST_FORMAT_REQUEST = new Format("eSciDoc-publication-item-list", "application/xml", "ISO-8859-1");

	private BmcToEscidocTransformation b2etransf = new BmcToEscidocTransformation();
	
	byte[] src = null;
	
	String expected = null;
	
	{
		try
			{
			 	src = ResourceUtil.getResourceAsBytes("testFiles/bmc/1752-1947-5-391.xml");
				expected = ResourceUtil.getResourceAsString("testFiles/escidoc/1752-1947-5-391.xml");
			}
		catch (Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException("Can't open file", e);
			}
		
		b2etransf.setTestmode(true);
		
	}
	
	@Test
	public void testGetSourceFormats() {
		assertEquals("expected length of m2etransf.getSourceFormats(): 1, delivered: " + b2etransf.getSourceFormats().length,
						1,
						b2etransf.getSourceFormats().length
						);
		assertEquals(BMC_FORMAT, b2etransf.getSourceFormats()[0]);
		}

	@Test
	public void testGetSourceFormatsFormat() {
		final Format[] result_item = b2etransf.getSourceFormats(ESCIDOC_ITEM_FORMAT_REQUEST);
		final Format[] result_list = b2etransf.getSourceFormats(ESCIDOC_ITEM_LIST_FORMAT_REQUEST);
		
		assertEquals("expected length of result_item: 1, delivered: " + result_item.length, 1, result_item.length);
		assertEquals("expected length of result_list: 1, delivered: " + result_list.length, 1, result_list.length);
		
		assertEquals(BMC_FORMAT, b2etransf.getSourceFormats(ESCIDOC_ITEM_FORMAT_RECEIVE)[0]);
		assertEquals(BMC_FORMAT, b2etransf.getSourceFormats(ESCIDOC_ITEM_LIST_FORMAT_REQUEST)[0]);
		}

	@Test
	public void testGetTargetFormats()
		{
			//with XMLTestCase 1.0 fails assertEquals(new Format[]{ESCIDOC_ITEM_FORMAT_RECEIVE, ESCIDOC_ITEM_LIST_FORMAT_RECEIVE}, b2etransf.getTargetFormats(BMC_FORMAT)); Stf, 2013-03-24
			Format[] gettedFormats = b2etransf.getTargetFormats(BMC_FORMAT);
			assertEquals(ESCIDOC_ITEM_FORMAT_RECEIVE, gettedFormats[0]);
			assertEquals(ESCIDOC_ITEM_LIST_FORMAT_RECEIVE, gettedFormats[1]);
	}

	@Test
	public void testTransformByteArrayFormatFormatString() throws TransformationNotSupportedException, RuntimeException, SAXException, IOException, ParserConfigurationException, URISyntaxException
		{
	        // skip the test in case of release build
	        if (PropertyReader.getProperty("escidoc.common.release.build").equals("true"))
	            return;
	    
			assertXMLEqual(
							expected,
							new String(
										b2etransf.transform(src,
												BMC_FORMAT,
												ESCIDOC_ITEM_FORMAT_REQUEST,
												"")
									, "UTF-8") + "\n" // somewhere in the pipeline was a \n added
						);
		}

	@Test
	public void testTransformByteArrayFormatFormatStringStringMap() throws TransformationNotSupportedException, RuntimeException, SAXException, IOException, ParserConfigurationException, URISyntaxException
		{
	        // skip the test in case of release build
	        if (PropertyReader.getProperty("escidoc.common.release.build").equals("true"))
	            return;
	    
			Map <String,String> config = new HashMap<String, String>();
			config.put("{http://escidoc.de/core/01/structural-relations/}origin", "Pubman File-Import");
			
			assertXMLEqual(
							expected,
							new String(b2etransf.transform(
															src,
															BMC_FORMAT,
															ESCIDOC_ITEM_FORMAT_REQUEST,
															"",
															config
														),
														"UTF-8"
									) + "\n" // somewhere in the pipeline was a \n added
					);
			
		}
	
	@Test
	public void testTransformByteArrayStringStringStringStringStringStringString() throws Exception
		{
	        // skip the test in case of release build
	        if (PropertyReader.getProperty("escidoc.common.release.build").equals("true"))
	            return;
	        
			assertXMLEqual(
							expected,
							new String(
										b2etransf.transform(
													src, 
													"bmc_editura", "application/xml", "UTF-8",
													"eSciDoc-publication-item", "application/xml", "*",
													"")
									, "UTF-8") + "\n" // somewhere in the pipeline was an \n added
						);
		}
	
	@Test
	public void testGetConfiguration() throws Exception
		{
			Map <String,String> config = new HashMap<String, String>();
			config.put("CoNE", "true");
			config.put("Files_to_Import", "both");
			
			assertEquals(config, b2etransf.getConfiguration(BMC_FORMAT, ESCIDOC_ITEM_FORMAT_RECEIVE));
		}
	
	@Test
	public void testGetConfigurationValues() throws Exception
		{
			List<String> coneValues = Arrays.asList("true", "false");
			
			assertEquals(coneValues, b2etransf.getConfigurationValues(BMC_FORMAT, ESCIDOC_ITEM_FORMAT_RECEIVE, "CoNE"));
		}
	
	@Test
	public void testTransformationBean()
		{
			TransformationBean transformationbean = new TransformationBean(true);
			
			assertTrue("missing ESCIDOC_ITEM_FORMAT_RECEIVE in transformationbean.getTargetFormats(BMC_FORMAT)",
					Util.containsFormat(transformationbean.getTargetFormats(BMC_FORMAT), ESCIDOC_ITEM_FORMAT_RECEIVE));
			
			assertTrue("missing ESCIDOC_ITEM_LIST_FORMAT_RECEIVE in transformationbean.getTargetFormats(BMC_FORMAT)",
					Util.containsFormat(transformationbean.getTargetFormats(BMC_FORMAT), ESCIDOC_ITEM_LIST_FORMAT_RECEIVE));
		
			assertTrue("missing BMC_FORMAT in transformationbean.getSourceFormats(ESCIDOC_ITEM_FORMAT_REQUEST)",
					Util.containsFormat(transformationbean.getSourceFormats(ESCIDOC_ITEM_FORMAT_REQUEST), BMC_FORMAT));
			
			assertTrue("missing BMC_FORMAT in transformationbean.getSourceFormats(ESCIDOC_ITEM_LIST_FORMAT_REQUEST)",
					Util.containsFormat(transformationbean.getSourceFormats(ESCIDOC_ITEM_LIST_FORMAT_REQUEST), BMC_FORMAT));
		
			assertTrue("checkTransformation BMC_FORMAT->ESCIDOC_ITEM_FORMAT_REQUEST failed", transformationbean.checkTransformation(BMC_FORMAT, ESCIDOC_ITEM_FORMAT_REQUEST));
			assertTrue("checkTransformation BMC_FORMAT->ESCIDOC_ITEM_LIST_FORMAT_REQUEST failed", transformationbean.checkTransformation(BMC_FORMAT, ESCIDOC_ITEM_LIST_FORMAT_REQUEST));
			assertTrue("checkTransformation BMC_FORMAT->ESCIDOC_ITEM_FORMAT_RECEIVE failed", transformationbean.checkTransformation(BMC_FORMAT, ESCIDOC_ITEM_FORMAT_RECEIVE));
			assertTrue("checkTransformation BMC_FORMAT->ESCIDOC_ITEM_LIST_FORMAT_RECEIVE failed", transformationbean.checkTransformation(BMC_FORMAT, ESCIDOC_ITEM_LIST_FORMAT_RECEIVE));
			
			transformationbean = null;
		}
}
