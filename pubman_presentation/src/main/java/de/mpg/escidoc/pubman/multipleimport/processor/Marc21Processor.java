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

package de.mpg.escidoc.pubman.multipleimport.processor;

import java.io.ByteArrayOutputStream;
import org.apache.axis.encoding.Base64;
import org.apache.tika.io.IOUtils;
import org.marc4j.MarcStreamReader;
import org.marc4j.MarcXmlWriter;
import org.marc4j.marc.Record;

/*
 * takes Marc21 binary data and returns chunks of single MARRCXML records
 * This is kind of cheating, but FormatProcessor has to return String
 *
 * @author Stefan Krause, Editura GmbH & Co. KG  (initial creation)
 * @author $Author: skrause $ (last modification)
 * @version $Revision: 261 $ $LastChangedDate: 2013-04-30 20:57:29 +0200 (Di, 30 Apr 2013) $
 */

public class Marc21Processor extends FormatProcessor
	 {
		private boolean isInitialized = false;
		private MarcXmlProcessor marcxmlprocessor;
		    
		private void initialize()
			{
				MarcStreamReader reader;
			 	
			 	if (this.encoding == null || this.encoding.trim().equals("") || this.encoding.trim().equals("*") )
					reader = new MarcStreamReader(source);
				 		
				else
			 		reader = new MarcStreamReader(source, encoding);
			 	
			 	ByteArrayOutputStream result = new ByteArrayOutputStream();
				
			 	MarcXmlWriter writer = new MarcXmlWriter(result, "UTF-8", true);
			    
			 	while (reader.hasNext())
			    	{
			        	Record record = reader.next();
			        	writer.write(record);
			    	}
			    
			 	writer.close();
				
           		try
           			{
           				//nasty workaround to get rid of the namespace issues, has to be fixed, Stf, 2013-03-22
           				String xml = new String(result.toString("UTF-8").replaceAll("xmlns=\"http://www.loc.gov/MARC21/slim\"", "").replaceAll("<collection", "<collection xmlns=\"http://www.loc.gov/MARC21/slim\"") );
           				marcxmlprocessor = new MarcXmlProcessor();
           				marcxmlprocessor.setSource(IOUtils.toInputStream(xml, "UTF-8"));
           			}
				catch (Exception e)
					{
						throw new RuntimeException("Can't encode the result to UTF-8", e);
					}
				       
	           	result = null;
			    writer = null;
			    reader = null;
			    
			    this.isInitialized = true;
			    
			 }
		
		@Override
		public boolean hasNext()
		    {
		        if (!this.isInitialized)
		        {
		            initialize();
		        }
		        return marcxmlprocessor.hasNext();
		    }

		@Override
		public String next()
		    {
		        if (!this.isInitialized)
			        {
			            initialize();
			        }
		        return marcxmlprocessor.next();
		    }

		@Override
		@Deprecated
	    public void remove()
		    {
		        throw new RuntimeException("Not implemented");
		    }
		
		@Override
		public int getLength()
		    {
		        if (!this.isInitialized)
		        {
		            initialize();
		        }
		        return marcxmlprocessor.getLength();
		    }

		@Override
		public String getDataAsBase64()
			{
				if (this.getSource() == null)
			        {
			            return null;
			        }
		        else
		        {
		            try
			            {
				            return Base64.encode(IOUtils.toByteArray(this.getSource()));
			            }
		            catch (Exception e)
		            	{
		            		throw new RuntimeException("Can't read input stream", e);
		            	}
		        }
			}
	}