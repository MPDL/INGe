package de.mpg.mpdl.inge.util;

import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

public class EscidocNamespaceContextImpl implements NamespaceContext {

  @Override
  public String getNamespaceURI(String prefix) {
    switch (prefix) {
      case "item":
        return "http://www.escidoc.de/schemas/item/0.10";
      case "prop":
        return "http://escidoc.de/core/01/properties/";
      case "srel":
        return "http://escidoc.de/core/01/structural-relations/";
      case "dcterms":
        return "http://purl.org/dc/terms/";
      default:
        return XMLConstants.NULL_NS_URI;
    }

  }

  @Override
  public String getPrefix(String namespaceURI) {
    return null; // we are not using this.
  }

  @Override
  public Iterator getPrefixes(String namespaceURI) {
    return null; // we are not using this.
  }

}
