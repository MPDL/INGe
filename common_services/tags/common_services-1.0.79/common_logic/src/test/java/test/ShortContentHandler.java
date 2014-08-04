package test;

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
    private Map<String, HashMap<String, String>> namespacesMap = new HashMap<String, HashMap<String, String>>();

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException
    {
        HashMap<String, String> currentNamespaces;
        if ("".equals(stack.toString()))
        {
            currentNamespaces = new HashMap<String, String>();
        }
        else
        {
            currentNamespaces = (HashMap) getNamespaces().clone();
        }
        stack.push(name);
        for (int i = 0; i < attributes.getLength(); i++)
        {
            String attributeName = attributes.getQName(i);
            if (attributeName.startsWith("xmlns:"))
            {
                String namespace = attributes.getValue(i);
                currentNamespaces.put(attributeName.substring(6), namespace);
            }
        }
        namespacesMap.put(stack.toString(), currentNamespaces);
        currentContent = new StringBuffer();
    }

    protected HashMap<String, String> getNamespaces()
    {
        return namespacesMap.get(stack.toString());
    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException
    {
        content(uri, localName, name, currentContent.toString());
        currentContent = new StringBuffer();
        namespacesMap.remove(stack.toString());
        stack.pop();
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

    public XMLStack getStack()
    {
        return stack;
    }

    /**
     * A {@link Stack} extension to facilitate XML navigation.
     * 
     * @author franke (initial creation)
     * @author $Author$ (last modification)
     * @version $Revision$ $LastChangedDate$
     */
    protected class XMLStack extends Stack<String>
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