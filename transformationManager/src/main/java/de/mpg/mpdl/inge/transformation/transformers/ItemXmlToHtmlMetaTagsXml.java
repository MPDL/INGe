package de.mpg.mpdl.inge.transformation.transformers;

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;

import de.mpg.mpdl.inge.transformation.ChainableTransformer;
import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;
import de.mpg.mpdl.inge.transformation.TransformerModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.util.LocalUriResolver;
import de.mpg.mpdl.inge.util.PropertyReader;

@TransformerModule(sourceFormat = FORMAT.ESCIDOC_ITEM_V3_XML, targetFormat = FORMAT.HTML_METATAGS_DC_XML)
@TransformerModule(sourceFormat = FORMAT.ESCIDOC_ITEM_V3_XML, targetFormat = FORMAT.HTML_METATAGS_HIGHWIRE_PRESS_CIT_XML)
public class ItemXmlToHtmlMetaTagsXml extends XslTransformer implements ChainableTransformer {

  @Override
  public Source getXsltSource() throws TransformationException {
    if (FORMAT.HTML_METATAGS_DC_XML.equals(getTargetFormat())) {
      return getXmlSourceFromProperty(PropertyReader.INGE_TRANSFORMATION_HTML_METATAGS_DC_STYLESHEET_FILENAME);
    } else if (FORMAT.HTML_METATAGS_HIGHWIRE_PRESS_CIT_XML.equals(getTargetFormat())) {
      return getXmlSourceFromProperty(PropertyReader.INGE_TRANSFORMATION_HTML_METATAGS_HIGHWIRE_STYLESHEET_FILENAME);
    } else {
      return null;
    }
  }

  @Override
  public Map<String, Object> getParameters() {
    Map<String, Object> map = new HashMap<>();

    map.put("pubmanInstanceUrl", PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_URL));
    map.put("pubmanContextPath", PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_CONTEXT_PATH));
    map.put("pubmanComponentPattern", PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_COMPONENT_PATTERN));

    return map;
  }

  @Override
  public Map<String, String> getDefaultConfiguration() {
    return null;
  }

  @Override
  public URIResolver getURIResolver() {
    return new LocalUriResolver("transformations/standardFormats/xslt");
  }

}
