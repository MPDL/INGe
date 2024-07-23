package de.mpg.mpdl.inge.cone;

import java.io.File;
import java.io.FileReader;
import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;

public class GfzUtil {
  public static final NamespaceContext GFZ_NamespaceCtx = new NamespaceContext() {
    public String getNamespaceURI(String prefix) {
      String uri;
      if (prefix.equals("organizational-unit-list"))
        uri = "http://www.escidoc.de/schemas/organizationalunitlist/0.8";
      else if (prefix.equals("organizational-unit"))
        uri = "http://www.escidoc.de/schemas/organizationalunit/0.8";
      else if (prefix.equals("prop"))
        uri = "http://escidoc.de/core/01/properties/";
      else if (prefix.equals("xlink"))
        uri = "http://www.w3.org/1999/xlink";
      else if (prefix.equals("escidocMetadataRecords"))
        uri = "http://www.escidoc.de/schemas/metadatarecords/0.5";
      else if (prefix.equals("dc"))
        uri = "http://purl.org/dc/elements/1.1/";
      else if (prefix.equals("o"))
        uri = "http://purl.org/escidoc/metadata/profiles/0.1/organizationalunit";
      else if (prefix.equals("dcterms"))
        uri = "http://purl.org/dc/terms/";
      else if (prefix.equals("rdf"))
        uri = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
      else if (prefix.equals("dc"))
        uri = "http://purl.org/dc/elements/1.1/";
      else if (prefix.equals("gfz"))
        uri = "http://www.gfz-potsdam.de/metadata/namespaces/cone-namespace/";
      else if (prefix.equals("zs"))
        uri = "http://www.loc.gov/zing/srw/";
      else if (prefix.equals("srel"))
        uri = "http://escidoc.de/core/01/structural-relations/";
      else if (prefix.equals("sru-zr"))
        uri = "http://www.loc.gov/zing/srw/";
      else
        uri = null;
      return uri;
    }

    public Iterator getPrefixes(String val) {
      return null;
    }

    public String getPrefix(String uri) {
      return null;
    }
  };

  public static String getFileAsString(String fileName) {
    String content = "";
    try {
      File file = new File(fileName);
      FileReader fr = new FileReader(file);

      char[] temp = new char[(int) file.length()];

      fr.read(temp);

      content = new String(temp);

      fr.close();
    } catch (Exception e) {
      System.out.println("Error reading File " + fileName + "..");
    }

    return content;
  }

  public static String getEscidocId(String itemXml, String hrefpath) {
    int start = itemXml.indexOf(hrefpath) + hrefpath.length();
    int end = itemXml.indexOf("\"", start);
    String id = itemXml.substring(start, end);
    return id;
  }

  public static String getLastModificationDate(String result) {
    int start = result.indexOf("last-modification-date=\"") + 24;
    int end = result.indexOf("\"", start);
    String lastModified = result.substring(start, end);
    return lastModified;
  }
}
