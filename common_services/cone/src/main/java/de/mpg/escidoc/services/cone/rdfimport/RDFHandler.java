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

package de.mpg.escidoc.services.cone.rdfimport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import de.mpg.escidoc.services.common.util.ShortContentHandler;
import de.mpg.escidoc.services.cone.Querier;
import de.mpg.escidoc.services.cone.QuerierFactory;
import de.mpg.escidoc.services.cone.util.LocalizedString;
import de.mpg.escidoc.services.cone.util.LocalizedTripleObject;
import de.mpg.escidoc.services.cone.util.TreeFragment;


/**
 * SAX handler to read out RDF/XML data.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class RDFHandler extends ShortContentHandler
{

    private List<LocalizedTripleObject> result = new ArrayList<LocalizedTripleObject>();
    private Stack<LocalizedTripleObject> stack = new Stack<LocalizedTripleObject>();
    private Querier querier = QuerierFactory.newQuerier();
    
    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException
    {
        super.startElement(uri, localName, name, attributes);
        if ("RDF/Description".equals(getLocalStack().toString()))
        {
            // New element
            String subject = attributes.getValue("rdf:about");
            this.stack.push(new TreeFragment(subject));
        }
        else if (!"RDF".equals(getLocalStack().toString()))
        {
            String predicate;
            String namespace;
            String tagName;
            if (name.contains(":"))
            {
                String nsPrefix = name.split(":")[0];
                namespace = namespaces.get(nsPrefix);
                tagName = name.split(":")[1];
                if (!namespace.endsWith("/"))
                {
                    predicate = namespace + "/" + tagName;
                }
                else
                {
                    predicate = namespace + tagName;
                }
            }
            else if (namespaces.get("") != null)
            {
                namespace = namespaces.get("");
                if (!namespace.endsWith("/"))
                {
                    predicate = namespace + "/" + name;
                }
                else
                {
                    predicate = namespace + name;
                }
                tagName = name;
                
            }
            else
            {
                namespace = null;
                tagName = name;
                predicate = name;
            }

            if ("http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(namespace) && "Description".equals(tagName))
            {
                LocalizedString wrongData = (LocalizedString) this.stack.pop();
                TreeFragment container = (TreeFragment) this.stack.peek();
                String newSubject = attributes.getValue("rdf:about");
                if (newSubject == null)
                {
                    try
                    {
                        newSubject = querier.createUniqueIdentifier(null);
                    }
                    catch (Exception e) {
                        throw new SAXException(e);
                    }
                }
                
                String pred = null;
                for (Iterator<String> iterator = container.keySet().iterator(); iterator.hasNext();)
                {
                    String key = (String) iterator.next();
                    if (container.get(key).contains(wrongData))
                    {
                        pred = key;
                        break;
                    }
                }
                
                TreeFragment betterData = new TreeFragment(newSubject, attributes.getValue("xml:lang"));
                container.get(pred).remove(wrongData);
                container.get(pred).add(betterData);
                this.stack.push(betterData);
            }
            else if (this.stack.peek() instanceof TreeFragment)
            {
                LocalizedString firstValue = new LocalizedString();
                firstValue.setLanguage(attributes.getValue("xml:lang"));
                
                if (((TreeFragment) this.stack.peek()).get(predicate) != null)
                {
                    ((TreeFragment) this.stack.peek()).get(predicate).add(firstValue);
                }
                else
                {
                    List<LocalizedTripleObject> newList = new ArrayList<LocalizedTripleObject>();
                    newList.add(firstValue);
                    ((TreeFragment) this.stack.peek()).put(predicate, newList);
                }
                this.stack.push(firstValue);
            }
            else
            {
                throw new SAXException("Wrong RDF structure at " + getStack());
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException
    {
        super.endElement(uri, localName, name);
        if ("RDF".equals(getLocalStack().toString()))
        {
            result.add(this.stack.pop());
            return;
        }
        
        String namespace;
        String tagName;
        if (name.contains(":"))
        {
            String nsPrefix = name.split(":")[0];
            namespace = namespaces.get(nsPrefix);
            tagName = name.split(":")[1];
        }
        else if (namespaces.get("") != null)
        {
            namespace = namespaces.get("");
            tagName = name;
        }
        else
        {
            namespace = null;
            tagName = name;
        }

        
        if (!this.stack.isEmpty() && !("Description".equals(tagName) && "http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(namespace)))
        {
            this.stack.pop();
        }
    }

    @Override
    public void content(String uri, String localName, String name, String content)
    {
        if (this.stack.peek() instanceof LocalizedString)
        {
            ((LocalizedString) this.stack.peek()).setValue(content);
        }
        else
        {
            throw new RuntimeException("Wrong RDF structure at " + getStack());
        }
    }
    
    public List<LocalizedTripleObject> getResult()
    {
        return this.result;
    }
    
}
