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

import org.w3c.dom.Node;
import static org.w3c.dom.Node.ELEMENT_NODE;
import org.w3c.dom.NodeList;

/**
 * takes MarcXML data and returns chunks of single MarcXML records
 *
 * @author Stefan Krause, Editura GmbH & Co. KG  (initial creation)
 * @author $Author: skrause $ (last modification)
 * @version $Revision: 261 $ $LastChangedDate: 2013-04-30 20:57:29 +0200 (Di, 30 Apr 2013) $
 *
 */
public class MarcXmlProcessor extends GenericXmlProcessor
{
	private static final String MARC_NS = "http://www.loc.gov/MARC21/slim";

	protected void addItems(Node root)
	    {
	    	if (root.getLocalName() != null && root.getLocalName().equals("record") &&
	    			root.getNamespaceURI() != null && root.getNamespaceURI().equals(MARC_NS) )
	  			{
	  				addItem(root);
	  			}
  			else if (root.getLocalName() != null && root.getLocalName().equals("collection") &&
  						root.getNamespaceURI() != null && root.getNamespaceURI().equals(MARC_NS)) 
	  			{
  					NodeList nodes = root.getChildNodes();       

  					for(int i = 0 ; i < nodes.getLength() ; i++)
				      {
			    	  	Node currentNode = nodes.item(i);
			    	  	if (currentNode.getNodeType() == ELEMENT_NODE &&
			    	  			root.getLocalName() != null && currentNode.getLocalName().equals("record") &&
	    	  					root.getNamespaceURI() != null && currentNode.getNamespaceURI().equals(MARC_NS) )
				        	{
			    	  			addItem(currentNode);
				        	}
				      }
  					
  					nodes = null;
			    }         
			else	//nothing to deliver
	  			{
	  				throw new RuntimeException("document format not supported: root = {" + root.getNamespaceURI() + "}" + root.getLocalName());
	  			}
			
		}
}