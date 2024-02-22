package de.mpg.mpdl.inge.transformation.transformers;

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;

import de.mpg.mpdl.inge.transformation.TransformerFactory;
import de.mpg.mpdl.inge.transformation.TransformerModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.util.LocalUriResolver;
import de.mpg.mpdl.inge.util.PropertyReader;

@TransformerModule(sourceFormat = TransformerFactory.FORMAT.ESCIDOC_ITEMLIST_V3_XML,
    targetFormat = TransformerFactory.FORMAT.ENDNOTE_STRING)
@TransformerModule(sourceFormat = TransformerFactory.FORMAT.ESCIDOC_ITEM_V3_XML, targetFormat = TransformerFactory.FORMAT.ENDNOTE_STRING)
public class ItemXmlToEndNote extends XslTransformer {

  @Override
  public Source getXsltSource() throws TransformationException {
    return getXmlSourceFromProperty(PropertyReader.INGE_TRANSFORMATION_ESCIDOC2ENDNOTE_STYLESHEET_FILENAME);
  }

  @Override
  public Map<String, Object> getParameters() {
    Map<String, Object> map = new HashMap<>();

    map.put("handleShort", PropertyReader.getProperty(PropertyReader.INGE_PID_HANDLE_SHORT));
    map.put("handleUrl", PropertyReader.getProperty(PropertyReader.INGE_PID_HANDLE_URL));

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
