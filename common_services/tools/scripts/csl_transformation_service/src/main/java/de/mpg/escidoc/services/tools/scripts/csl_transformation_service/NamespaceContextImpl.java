package de.mpg.escidoc.services.tools.scripts.csl_transformation_service;

import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

public class NamespaceContextImpl implements NamespaceContext
{
    @Override
    public String getNamespaceURI(String prefix)
    {
        switch (prefix) {
            case "csl": return "http://purl.org/net/xbiblio/csl";
            default: return XMLConstants.NULL_NS_URI;
        }
    }

    @Override
    public String getPrefix(String namespaceURI)
    {
        return null; // we are not using this.
    }

    @Override
    public Iterator getPrefixes(String namespaceURI)
    {
        return null; // we are not using this.
    }
}
