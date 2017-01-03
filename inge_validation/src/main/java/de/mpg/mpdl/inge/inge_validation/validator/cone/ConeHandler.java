package de.mpg.mpdl.inge.inge_validation.validator.cone;

import java.util.HashSet;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

// TODO System.out.println rauswerfen
public class ConeHandler extends DefaultHandler {

  private Set<String> result = new HashSet<String>();

  private String searchElement;
  private StringBuffer tmp;
  private boolean isSearchElement = false;

  public ConeHandler(String searchElement) {
    this.searchElement = searchElement;
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes)
      throws SAXException {
    if (qName.equalsIgnoreCase(this.searchElement)) {
      this.isSearchElement = true;
      this.tmp = new StringBuffer();
    }
  }

  @Override
  public void characters(char ch[], int start, int length) throws SAXException {
    if (isSearchElement) {
      this.tmp.append(new String(ch, start, length));
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if (qName.equalsIgnoreCase(this.searchElement)) {
      this.result.add(this.tmp.toString());
      this.isSearchElement = false;
    }
  }

  public Set<String> getResult() {
    System.out.println(this.result.size());
    return this.result;
  }

}
