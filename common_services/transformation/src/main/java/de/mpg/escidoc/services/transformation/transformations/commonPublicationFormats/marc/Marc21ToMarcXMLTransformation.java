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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;
import org.marc4j.MarcXmlWriter;
import org.marc4j.marc.Record;

import de.mpg.escidoc.services.transformation.Configurable;
import de.mpg.escidoc.services.transformation.Transformation;
import de.mpg.escidoc.services.transformation.Util;
import de.mpg.escidoc.services.transformation.Transformation.TransformationModule;
import de.mpg.escidoc.services.transformation.exceptions.TransformationNotSupportedException;
import de.mpg.escidoc.services.transformation.valueObjects.Format;

/*
 * transforms MARC21 (binary) to MarcXML
 * initial author: Stefan Krause, Editura GmbH & Co. KG
 * 
 * @author $Author: skrause $ (last modification)
 * @version $Revision: 261 $ $LastChangedDate: 2013-04-30 20:57:29 +0200 (Di, 30 Apr 2013) $
 *
 */

@TransformationModule
public class Marc21ToMarcXMLTransformation implements Configurable, Transformation
	{

		private Logger logger = Logger.getLogger(getClass());
		
		public static final Format MARC21_FORMAT = new Format("marc21", "application/marc", ""); //if no encoding is given, the transformation tries to detect the current encoding. See http://www.tigris.org/files/documents/220/33576/tutorial.html#N101E4 (Stf, 2013-03-06)
	    public static final Format MARCXML_FORMAT = new Format("marcxml", "application/marcxml+xml", "*");
	    
		private Map<String, String> configuration = null;
		private Map<String, List<String>> properties = null;

		private void init()
	    {
	        configuration = new LinkedHashMap<String, String>();
	        properties = new HashMap<String, List<String>>();
	    }
	
		@Override
		public Format[] getSourceFormats() throws RuntimeException
			{
				return new Format[]{MARC21_FORMAT};
			}
	
		@Override
		public Format[] getSourceFormats(Format trg) throws RuntimeException
			{
				if (trg != null && trg.matches(MARCXML_FORMAT))
					{
						return new Format[]{MARC21_FORMAT};
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
				if (MARC21_FORMAT.equals(src))
					{
						return new Format[]{MARCXML_FORMAT};
					}
				else
					{
						return new Format[]{};
					}
			}
	
		@Override
		@Deprecated
	    public String getTargetFormatsAsXml(String srcFormatName, String srcType, String srcEncoding) throws RuntimeException
			{
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
				return this.transform(src, srcFormat, trgFormat, service, null);
			}
	
		@Override
		public byte[] transform(byte[] src, Format srcFormat, Format trgFormat,
				String service, Map<String, String> configuration)
				throws TransformationNotSupportedException, RuntimeException
			{
				if(Util.isFormatEqual(srcFormat, MARC21_FORMAT) && Util.isFormatEqual(trgFormat, MARCXML_FORMAT))
					{
						String srcEncoding = new String();
						
						// see http://www.tigris.org/files/documents/220/33576/tutorial.html#N101E4
						// marc4j tries to detect the input encoding
						if (srcFormat.getEncoding() == null || srcFormat.getEncoding().trim().equals("") || srcFormat.getEncoding().trim().equals("*") )
							{srcEncoding = null;}
						else
							{srcEncoding = srcFormat.getEncoding();}
			
						String trgEncoding = new String();
						
						if (trgFormat.getEncoding() == null || trgFormat.getEncoding().trim().equals("") || trgFormat.getEncoding().trim().equals("*") )
							{trgEncoding = "UTF-8";}
						else
							{trgEncoding = trgFormat.getEncoding();}
					
						MarcReader reader;
						
						if (srcEncoding == null)
							{reader = new MarcStreamReader(new ByteArrayInputStream(src));}
						else
							{reader = new MarcStreamReader(new ByteArrayInputStream(src), srcEncoding);}
						
						ByteArrayOutputStream result = new ByteArrayOutputStream();
						
					    MarcXmlWriter writer = new MarcXmlWriter(result, trgEncoding, true);
					    
					    while (reader.hasNext())
					    	{
					        	Record record = reader.next();
					        	writer.write(record);
				            }
					    
					    writer.close();
					    
					    //TODO: because MarcXmlWriter does not produce the correct namespace, I've created a nasty workaround. Need to be cleaned up sometimes, Stf, 2013-03-14
					    
					    String temp = new String();
					    byte[] output = null;
					    try
					    	{
								temp = result.toString(trgEncoding).replaceAll("xmlns=\"http://www.loc.gov/MARC21/slim\"", "").replaceAll("<collection", "<collection xmlns=\"http://www.loc.gov/MARC21/slim\"");
							    output = temp.getBytes(trgEncoding);
							}
					    catch (UnsupportedEncodingException e)
						    {
								throw new RuntimeException("Can't encode the result to the requested encoding: " + trgEncoding, e);
							}
					    
					    return output;
					
					}
				else                	
					{               	
						throw new TransformationNotSupportedException("Marc21ToMarcXML can't transform " + srcFormat.getName() + " to " + trgFormat.getName());
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

	}
