package de.mpg.mpdl.inge.util;

import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

public class EscidocNamespaceContextImpl implements NamespaceContext {

  @Override
  public String getNamespaceURI(String prefix) {
    return switch (prefix) {
      case "item" -> "http://www.escidoc.de/schemas/item/0.10";
      case "prop" -> "http://escidoc.de/core/01/properties/";
      case "srel" -> "http://escidoc.de/core/01/structural-relations/";
      case "dcterms" -> "http://purl.org/dc/terms/";
      default -> XMLConstants.NULL_NS_URI;
    };

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
