package de.mpg.mpdl.inge.transformation.transformers;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;

import de.mpg.mpdl.inge.transformation.TransformerFactory;
import de.mpg.mpdl.inge.transformation.TransformerModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.util.LocalUriResolver;
import de.mpg.mpdl.inge.util.PropertyReader;
import de.mpg.mpdl.inge.util.UriBuilder;

@TransformerModule(sourceFormat = TransformerFactory.FORMAT.ESCIDOC_ITEMLIST_V3_XML, targetFormat = TransformerFactory.FORMAT.EDOC_XML)
@TransformerModule(sourceFormat = TransformerFactory.FORMAT.ESCIDOC_ITEM_V3_XML, targetFormat = TransformerFactory.FORMAT.EDOC_XML)
public class ItemXmlToEdocXml extends XslTransformer {

  @Override
  public Source getXsltSource() throws TransformationException {
    return getXmlSourceFromProperty(PropertyReader.INGE_TRANSFORMATION_ESCIDOC2EDOC_EXPORT_STYLESHEET_FILENAME);
  }

  @Override
  public Map<String, Object> getParameters() {
    Map<String, Object> map = new HashMap<>();
    map.put("pubman_instance", PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_URL));
    try {
      map.put("itemLink", UriBuilder.getItemLink().toString());
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }

    return map;
  }

  @Override
  public URIResolver getURIResolver() {
    return new LocalUriResolver("transformations/otherFormats/xslt");
  }

  @Override
  public Map<String, String> getDefaultConfiguration() {
    return null;
  }

}
