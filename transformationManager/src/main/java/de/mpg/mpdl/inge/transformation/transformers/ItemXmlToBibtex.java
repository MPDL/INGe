package de.mpg.mpdl.inge.transformation.transformers;

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;

import de.mpg.mpdl.inge.transformation.ChainableTransformer;
import de.mpg.mpdl.inge.transformation.TransformerFactory;
import de.mpg.mpdl.inge.transformation.TransformerModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.util.LocalUriResolver;
import de.mpg.mpdl.inge.util.PropertyReader;

@TransformerModule(sourceFormat = TransformerFactory.FORMAT.ESCIDOC_ITEMLIST_V3_XML, targetFormat = TransformerFactory.FORMAT.BIBTEX_STRING)
@TransformerModule(sourceFormat = TransformerFactory.FORMAT.ESCIDOC_ITEM_V3_XML, targetFormat = TransformerFactory.FORMAT.BIBTEX_STRING)
public class ItemXmlToBibtex extends XslTransformer implements ChainableTransformer {
  @Override
  public Source getXsltSource() throws TransformationException {
    return getXmlSourceFromProperty(PropertyReader.INGE_TRANSFORMATION_ESCIDOC2BIBTEX_STYLESHEET_FILENAME);
  }

  @Override
  public Map<String, Object> getParameters() {
    return null;
  }

  public Map<String, String> getOutputKeys() {
    Map<String, String> map = new HashMap<>();
    map.put(OutputKeys.INDENT, "yes");
    map.put(OutputKeys.METHOD, "text");
    map.put(OutputKeys.ENCODING, "UTF-8");

    return map;
  }

  @Override
  public URIResolver getURIResolver() {
    return new LocalUriResolver("transformations/commonPublicationFormats/xslt");
  }

  @Override
  public Map<String, String> getDefaultConfiguration() {
    return null;
  }

}
