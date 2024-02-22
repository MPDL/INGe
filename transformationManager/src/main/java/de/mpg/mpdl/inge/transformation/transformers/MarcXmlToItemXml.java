package de.mpg.mpdl.inge.transformation.transformers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;

import de.mpg.mpdl.inge.transformation.SingleTransformer;
import de.mpg.mpdl.inge.transformation.TransformerFactory;
import de.mpg.mpdl.inge.transformation.TransformerModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.util.LocalUriResolver;
import de.mpg.mpdl.inge.util.PropertyReader;

@TransformerModule(sourceFormat = TransformerFactory.FORMAT.MARC_XML, targetFormat = TransformerFactory.FORMAT.ESCIDOC_ITEM_V3_XML)
@TransformerModule(sourceFormat = TransformerFactory.FORMAT.MARC_XML, targetFormat = TransformerFactory.FORMAT.ESCIDOC_ITEMLIST_V3_XML)
public class MarcXmlToItemXml extends XslTransformer {

  @Override
  public Source getXsltSource() throws TransformationException {
    return getXmlSourceFromProperty(PropertyReader.INGE_TRANSFORMATION_MARCXML2ESCIDOC_STYLESHEET_FILENAME);
  }

  @Override
  public Map<String, Object> getParameters() {
    Map<String, Object> map = new HashMap<>();

    if (TransformerFactory.FORMAT.ESCIDOC_ITEM_V3_XML.equals(getTargetFormat())) {
      map.put("{http://www.editura.de/ns/2012/misc}target-format", "eSciDoc-publication-item");
    } else if (TransformerFactory.FORMAT.ESCIDOC_ITEMLIST_V3_XML.equals(getTargetFormat())) {
      map.put("{http://www.editura.de/ns/2012/misc}target-format", "eSciDoc-publication-item-list");
    }

    return map;
  }

  @Override
  public URIResolver getURIResolver() {
    return new LocalUriResolver("transformations/commonPublicationFormats/xslt");
  }

  @Override
  public Map<String, String> getDefaultConfiguration() throws TransformationException {
    return SingleTransformer.getDefaultConfigurationFromProperty(PropertyReader.INGE_TRANSFORMATION_MARCXML2ESCIDOC_CONFIGURATION_FILENAME);
  }

  @Override
  public List<String> getAllConfigurationValuesFor(String key) throws TransformationException {
    return getAllConfigurationValuesFromProperty(PropertyReader.INGE_TRANSFORMATION_MARCXML2ESCIDOC_CONFIGURATION_FILENAME).get(key);
  }

}
