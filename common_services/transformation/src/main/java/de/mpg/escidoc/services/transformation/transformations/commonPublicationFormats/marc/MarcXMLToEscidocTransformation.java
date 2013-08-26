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

package de.mpg.escidoc.services.transformation.transformations.commonPublicationFormats.marc;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.transformation.Configurable;
import de.mpg.escidoc.services.transformation.Transformation;
import de.mpg.escidoc.services.transformation.Util;
import de.mpg.escidoc.services.transformation.Transformation.TransformationModule;
import de.mpg.escidoc.services.transformation.exceptions.TransformationNotSupportedException;
import de.mpg.escidoc.services.transformation.transformations.LocalUriResolver;
import de.mpg.escidoc.services.transformation.valueObjects.Format;

/*
 * transforms MARCXML to ESciDocXML
 * initial author: Stefan Krause, Editura GmbH & Co. KG
 * 
 * @author $Author: skrause $ (last modification)
 * @version $Revision: 275 $ $LastChangedDate: 2013-05-24 20:08:05 +0200 (Fr, 24 Mai 2013) $
 *
 */

@TransformationModule
public class MarcXMLToEscidocTransformation implements Transformation, Configurable
{

	private Logger logger = Logger.getLogger(getClass());
	
	private static final Format MARCXML_FORMAT = new Format("marcxml", "application/marcxml+xml", "UTF-8");
	private static final Format MARC21VIAXML_FORMAT = new Format("marc21viaxml", "application/marc", "UTF-8"); //this input format was preprocessed elsewhere to marcxml, Stf, 2013-03-19

	private static final Format ESCIDOC_ITEM_LIST_FORMAT = new Format("eSciDoc-publication-item-list", "application/xml", "*");
	private static final Format ESCIDOC_ITEM_FORMAT = new Format("eSciDoc-publication-item", "application/xml", "*");
	
	private Map<String, String> configuration = null;
	private Map<String, List<String>> properties = null;
    
	private void init() throws RuntimeException
	    {
	        configuration = new LinkedHashMap<String, String>();
	        properties = new HashMap<String, List<String>>();
	  
	        Properties props = new Properties();
	        try {
				props.load(ResourceUtil.getResourceAsStream(PropertyReader.getProperty("escidoc.transformation.marcxml2escidoc.configuration.filename")));
				}
	        catch (Exception e)
		        {
					throw new RuntimeException("Error during loading properties", e);
				} 

	        for (Object key : props.keySet())
	        {
	            if (!"configuration".equals(key.toString()))
	            {
	                String[] values = props.getProperty(key.toString()).split(",");
	                properties.put(key.toString(), Arrays.asList(values));
	            }
	            else
	            {
	                String[] confValues = props.getProperty("configuration").split(",");
	                for (String field : confValues)
	                {
	                    String[] fieldArr = field.split("=", 2);
	                    configuration.put(fieldArr[0], fieldArr[1] == null ? "" : fieldArr[1]);
	               }
	            }
	        }
	    }
	
	@Override
	public byte[] transform(byte[] src, Format srcFormat, Format trgFormat,
			String service, Map<String, String> configuration)
			throws TransformationNotSupportedException, RuntimeException
			{
		
			if((Util.isFormatEqual(srcFormat, MARCXML_FORMAT) || Util.isFormatEqual(srcFormat, MARC21VIAXML_FORMAT)) && 
				(Util.isFormatEqual(trgFormat, ESCIDOC_ITEM_FORMAT)) || Util.isFormatEqual(trgFormat, ESCIDOC_ITEM_LIST_FORMAT) )
				
				{
					String srcEncoding = new String();
					
					if (srcFormat.getEncoding() == null || srcFormat.getEncoding().trim().equals("") || srcFormat.getEncoding().trim().equals("*") )
						{srcEncoding = "UTF-8";}
					else
						{srcEncoding = srcFormat.getEncoding();}
				
					String trgEncoding = new String();
					
					if (trgFormat.getEncoding() == null || trgFormat.getEncoding().trim().equals("") || trgFormat.getEncoding().trim().equals("*") )
						{trgEncoding = "UTF-8";}
					else
						{trgEncoding = trgFormat.getEncoding();}
				
					StringReader source;
					try {
							source = new StringReader(new String(src, srcEncoding));
						}
					catch (UnsupportedEncodingException e)
						{
							throw new RuntimeException("Wrong encoding. Can't convert source with encoding " + srcEncoding + " to a String", e);
						}
					
					try {
							String fileName;
							
							try {
									fileName = PropertyReader.getProperty("escidoc.transformation.marcxml2escidoc.stylesheet.filename");
								}
							catch (Exception e) {
									throw new RuntimeException("Error during getProperty('escidoc.transformation.marcxml2escidoc.stylesheet.filename')", e);
								}

							if (fileName == null)
								{throw new RuntimeException("getProperty('escidoc.transformation.marcxml2escidoc.stylesheet.filename') can't acquire URI of the stylesheet");}
							
						    InputStream stylesheet;
							try
								{
									stylesheet = ResourceUtil.getResourceAsStream(fileName);
								}
							catch (Exception e) {
									throw new RuntimeException("Can't open stylesheet " + fileName, e);
								}
						    
							TransformerFactory factory = new net.sf.saxon.TransformerFactoryImpl();
						    
						    factory.setURIResolver(new LocalUriResolver("transformations/commonPublicationFormats/xslt"));
						    
						    Transformer transformer;
						 
						    try { 
									transformer = factory.newTransformer(new StreamSource(stylesheet));
									transformer.setOutputProperty(OutputKeys.INDENT, "yes");
									transformer.setOutputProperty(OutputKeys.METHOD, "xml");
									transformer.setOutputProperty(OutputKeys.ENCODING, trgEncoding);
									
									String ns_prefix_xsd_soap_common_srel;
									if (PropertyReader.getProperty("xsd.soap.common.srel") != null)
										ns_prefix_xsd_soap_common_srel = "{" + PropertyReader.getProperty("xsd.soap.common.srel") + "}";
									else
										ns_prefix_xsd_soap_common_srel = "{http://escidoc.de/core/01/structural-relations/}";
									transformer.setParameter(ns_prefix_xsd_soap_common_srel + "context-URI", PropertyReader.getProperty("escidoc.framework_access.context.id.test")); //TODO: set the correct context
									transformer.setParameter(ns_prefix_xsd_soap_common_srel + "content-model-URI", PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication")); //TODO: set the correct content model
									
									transformer.setParameter("{http://www.editura.de/ns/2012/misc}target-format", trgFormat.getName());
									
									if (configuration != null)
					                {
					                    for (String key : configuration.keySet())
					                    {
					                    	logger.debug("[MarcXMLToEscidocTransformation] ADD PARAM " + key + " WITH VALUE " + configuration.get(key));
					                        transformer.setParameter(key, configuration.get(key));
					                    }
					                }
					            	
						    	}
							catch (Exception e)
								{
									throw new RuntimeException("Can't set up transformer: " + e.toString(), e);
								}
						    
							StringWriter result = new StringWriter();
				            
							transformer.transform(new StreamSource(source), new StreamResult(result));
						    
						    return result.toString().getBytes(trgEncoding);
			      	    
						}
					catch (Exception e)
						{
							throw new RuntimeException("Error during transformation: " + e.toString(), e);
						}
					
				}
			else                	
				{               	
					throw new TransformationNotSupportedException("MarcXMLToEscidoc can't transform " + srcFormat.getName() + " to " + trgFormat.getName());
				}
	}

	@Override
	public Map<String, String> getConfiguration(Format srcFormat, Format trgFormat) throws Exception
			{
				if (configuration == null)
		        {
		            init();
		        }
		
		        return configuration;
			}

	@Override
	public List<String> getConfigurationValues(Format srcFormat, Format trgFormat, String key) throws Exception
		{
	        if (properties == null)
	        {
	            init();
	        }

	   	        return properties.get(key);
	    }

	@Override
	public Format[] getSourceFormats() throws RuntimeException
		{
			return new Format[]{MARCXML_FORMAT, MARC21VIAXML_FORMAT};
		}

	@Override
	public Format[] getSourceFormats(Format trg) throws RuntimeException
		{
			if (trg != null && (trg.matches(ESCIDOC_ITEM_FORMAT) || trg.matches(ESCIDOC_ITEM_LIST_FORMAT)))
				{
					return new Format[]{MARCXML_FORMAT, MARC21VIAXML_FORMAT};
				}
				else
				{
					return new Format[]{};
				}
		}

	@Override
	@Deprecated
    public String getSourceFormatsAsXml() throws RuntimeException {
		return null;
	}

	@Override
	public Format[] getTargetFormats(Format src) throws RuntimeException
		{
			if (MARCXML_FORMAT.equals(src) || MARC21VIAXML_FORMAT.equals(src))
				{
					return new Format[]{ESCIDOC_ITEM_FORMAT, ESCIDOC_ITEM_LIST_FORMAT};
				}
			else
				{
					return new Format[]{};
				}
		}

	@Override
	@Deprecated
    public String getTargetFormatsAsXml(String srcFormatName, String srcType,
			String srcEncoding) throws RuntimeException {
		return null;
	}

	@Override
	public byte[] transform(byte[] src, String srcFormatName, String srcType,
			String srcEncoding, String trgFormatName, String trgType,
			String trgEncoding, String service)
			throws TransformationNotSupportedException, RuntimeException
		{
			return transform(src, new Format(srcFormatName, srcType, srcEncoding), new Format(trgFormatName, trgType, trgEncoding), service);
		}

	@Override
	public byte[] transform(byte[] src, Format srcFormat, Format trgFormat,
			String service) throws TransformationNotSupportedException,
			RuntimeException
		{
			if (configuration == null)
		        {
		            init();
		        }
			return transform(src, srcFormat, trgFormat, service, configuration);
		}

}
