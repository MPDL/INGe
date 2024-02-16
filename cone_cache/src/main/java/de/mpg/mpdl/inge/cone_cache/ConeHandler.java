package de.mpg.mpdl.inge.cone_cache;

import java.util.HashSet;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ConeHandler extends DefaultHandler {

  private final Set<String> result = new HashSet<>();

  private final String searchElement;
  private StringBuffer tmp;
  private boolean isSearchElement = false;

  public ConeHandler(String searchElement) {
    this.searchElement = searchElement;
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) {
    if (qName.equalsIgnoreCase(this.searchElement)) {
      this.isSearchElement = true;
      this.tmp = new StringBuffer();
    }
  }

  @Override
  public void characters(char[] ch, int start, int length) {
    if (this.isSearchElement) {
      this.tmp.append(new String(ch, start, length));
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) {
    if (qName.equalsIgnoreCase(this.searchElement)) {
      this.result.add(this.tmp.toString());
      this.isSearchElement = false;
    }
  }

  public Set<String> getResult() {
    return this.result;
  }

}
