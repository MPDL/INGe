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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.axis.encoding.Base64;
import org.apache.tika.io.IOUtils;
import org.apache.xml.resolver.tools.CatalogResolver;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;

/*
 * does most of the work needed for processing xml sources
 * 
 * @author Stefan Krause, Editura GmbH & Co. KG  (initial creation)
 * @author $Author: skrause $ (last modification)
 * @version $Revision: 261 $ $LastChangedDate: 2013-04-30 20:57:29 +0200 (Di, 30 Apr 2013) $
 *
 */

public abstract class GenericXmlProcessor extends FormatProcessor
{
	private boolean isInitialized = false;
	private List<String> items = new ArrayList<String>();
    private int counter = -1;
    private int length = -1;
    private byte[] originalData = null;
    
    private void initialize()
	    {
	    	try {
					this.originalData = IOUtils.toByteArray(this.getSource());
				}
	    	catch (IOException e)
	    		{
					throw new RuntimeException("Can't convert source to byte[]", e);
				}
    	
    		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        factory.setNamespaceAware(true);
	        DocumentBuilder builder;
			try {
				builder = factory.newDocumentBuilder();
				EntityResolver eresolver = new CatalogResolver();
				builder.setEntityResolver(eresolver);
				
			}
			catch (ParserConfigurationException e) {
				throw new RuntimeException("Can't create DocumentBuilder", e);
			}

			try {
				Document document = builder.parse(new ByteArrayInputStream(this.originalData));
				
				Node root = document.getDocumentElement();
				
				if(root != null)
					this.addItems(root);
				
				factory = null;
				builder = null;
				document = null;
				root = null;
				this.counter = 0;
		    	this.isInitialized = true;
			}
			catch (Exception e) {
				throw new RuntimeException("Error during parsing input", e);
			}
	    }
    
    abstract void addItems(Node root);
    
    protected void addItem(Node node)
	    {
    		try {
	    			StringWriter writer = new StringWriter();
	    			String xml = new String();
	    			Transformer transformer = TransformerFactory.newInstance().newTransformer();
					transformer.transform(new DOMSource(node), new StreamResult(writer));
					xml = writer.toString();
					this.items.add(xml);
					this.length = this.items.size();
					writer = null;
					transformer = null;
				}
			catch (Exception e)
				{
					throw new RuntimeException("Can't transform node to String()", e);
				}
	    }
    
	@Override
	public boolean hasNext()
	    {
	        if (!this.isInitialized)
	        {
	            initialize();
	        }
	        return (this.counter < this.length);
	    }

	@Override
	public String next()
	    {
	        if (!this.isInitialized)
		        {
		            initialize();
		        }
	        if (this.items != null && this.counter < this.length)
		        {
		            return this.items.get(this.counter++);
		        }
	        else
		        {
		            throw new NoSuchElementException("No more entries left");
		        }
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
	        return this.length;
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
	        	if (!this.isInitialized)
		        {
		            initialize();
		        }
		        return Base64.encode(this.originalData);
		    }
		}
}