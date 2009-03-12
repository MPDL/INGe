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
* fÃŒr wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur FÃ¶rderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman.util;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Generic SAX handler with convenience methods. Useful for XML with only short string content. Classes that extend
 * this class should always call super() at the beginning of an overridden method.
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class ShortContentHandler extends DefaultHandler
{
    private StringBuffer currentContent;
    protected XMLStack stack = new XMLStack();
    protected XMLStack localStack = new XMLStack();
    protected Map<String, String> namespaces = new HashMap<String, String>();

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException
    {
        stack.push(name);
        if (name.contains(":"))
        {
            localStack.push(name.substring(name.indexOf(":") + 1));
        }
        else
        {
            localStack.push(name);
        }
        
        for (int i = 0; i < attributes.getLength(); i++)
        {
            if (attributes.getQName(i).startsWith("xmlns:"))
            {
                String prefix = attributes.getQName(i).substring(6);
                String nsUri = attributes.getValue(i);
                namespaces.put(prefix, nsUri);
            }
        }
        
        currentContent = new StringBuffer();
    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException
    {
        content(uri, localName, name, currentContent.toString());
        stack.pop();
        localStack.pop();
    }

    @Override
    public final void characters(char[] ch, int start, int length) throws SAXException
    {
        currentContent.append(ch, start, length);
    }

    /**
     * Called when string content was found.
     * 
     * @param uri The Namespace URI, or the empty string if the element has no Namespace URI or if Namespace
     *            processing is not being performed.
     * @param localName The local name (without prefix), or the empty string if Namespace processing is not being
     *            performed.
     * @param name The qualified name (with prefix), or the empty string if qualified names are not available.
     * @param content The string content of the current tag.
     */
    public void content(String uri, String localName, String name, String content)
    {
        // Do nothing by default
    }

    public String encodeAttribute(String str)
    {
        return str.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&apos;");
    }

    public String encodeContent(String str)
    {
        return str.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
    
    public XMLStack getStack()
    {
        return stack;
    }

    public XMLStack getLocalStack()
    {
        return localStack;
    }
    
    public Map<String, String> getNamespaces()
    {
        return namespaces;
    }

    /**
     * A {@link Stack} extension to facilitate XML navigation.
     * 
     * @author franke (initial creation)
     * @author $Author$ (last modification)
     * @version $Revision$ $LastChangedDate$
     */
    public class XMLStack extends Stack<String>
    {
        /**
         * Returns a String representation of the Stack in an XPath like way (e.g. "root/subtag/subsub"):
         */
        @Override
        public synchronized String toString()
        {
            StringWriter writer = new StringWriter();
            for (Iterator<String> iterator = this.iterator(); iterator.hasNext();)
            {
                String element = (String) iterator.next();
                writer.append(element);
                if (iterator.hasNext())
                {
                    writer.append("/");
                }
            }
            return writer.toString();
        }
    }
}
