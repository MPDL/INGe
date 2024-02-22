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

@TransformerModule(sourceFormat = TransformerFactory.FORMAT.ESCIDOC_ITEMLIST_V3_XML, targetFormat = TransformerFactory.FORMAT.MARC_XML)
public class ItemXmlToMarcXml extends XslTransformer {

  @Override
  public Source getXsltSource() throws TransformationException {
    return getXmlSourceFromProperty(PropertyReader.INGE_TRANSFORMATION_ESCIDOC2MARCXML_STYLESHEET_FILENAME);
  }

  @Override
  public Map<String, Object> getParameters() {
    Map<String, Object> map = new HashMap<>();

    map.put("pubmanUrl", PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_URL)
        + PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_CONTEXT_PATH));

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
