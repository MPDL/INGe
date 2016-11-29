package de.mpg.mpdl.inge.inge_validation.util;

import java.util.HashSet;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ConeHandler extends DefaultHandler {

  private String searchElement = "dc:title";

  private Set<String> result = new HashSet<String>();
  private boolean isSearchElement = false;

  public ConeHandler(String searchElement) {
    this.searchElement = searchElement;
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes)
      throws SAXException {
    if (qName.equalsIgnoreCase(this.searchElement)) {
      this.isSearchElement = true;
    }
  }

  @Override
  public void characters(char ch[], int start, int length) throws SAXException {
    if (this.isSearchElement) {
      this.result.add(new String(ch, start, length));
      this.isSearchElement = false;
    }
  }

  public Set<String> getResult() {
    return this.result;
  }

}
