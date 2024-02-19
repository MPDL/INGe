package de.mpg.mpdl.inge.transformation.transformers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;

import de.mpg.mpdl.inge.transformation.ChainableTransformer;
import de.mpg.mpdl.inge.transformation.SingleTransformer;
import de.mpg.mpdl.inge.transformation.TransformerFactory;
import de.mpg.mpdl.inge.transformation.TransformerModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.util.LocalUriResolver;
import de.mpg.mpdl.inge.util.PropertyReader;

@TransformerModule(sourceFormat = TransformerFactory.FORMAT.EDOC_XML, targetFormat = TransformerFactory.FORMAT.ESCIDOC_ITEM_V3_XML)
@TransformerModule(sourceFormat = TransformerFactory.FORMAT.EDOC_XML, targetFormat = TransformerFactory.FORMAT.ESCIDOC_ITEMLIST_V3_XML)
public class EdocXmlToItemXml extends XslTransformer implements ChainableTransformer {

  @Override
  public Source getXsltSource() throws TransformationException {
    return getXmlSourceFromProperty(PropertyReader.INGE_TRANSFORMATION_EDOC_STYLESHEET_FILENAME);
  }

  @Override
  public Map<String, Object> getParameters() {
    Map<String, Object> map = new HashMap<>();

    if (TransformerFactory.FORMAT.ESCIDOC_ITEM_V3_XML.equals(getTargetFormat())) {
      map.put("is-item-list", Boolean.FALSE);
    } else if (TransformerFactory.FORMAT.ESCIDOC_ITEMLIST_V3_XML.equals(getTargetFormat())) {
      map.put("is-item-list", Boolean.TRUE);
    }

    map.put("source-name", "edoc");
    map.put("root-ou", PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_ROOT_ORGANISATION_ID));
    map.put("external-ou", PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_EXTERNAL_ORGANISATION_ID));
    map.put("frameworkUrl", PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_URL));

    return map;
  }

  @Override
  public Map<String, String> getDefaultConfiguration() throws TransformationException {
    return SingleTransformer.getDefaultConfigurationFromProperty(PropertyReader.INGE_TRANSFORMATION_EDOC_CONFIGURATION_FILENAME);
  }

  @Override
  public URIResolver getURIResolver() {
    return new LocalUriResolver("transformations/otherFormats/xslt");
  }

  @Override
  public List<String> getAllConfigurationValuesFor(String key) throws TransformationException {
    return getAllConfigurationValuesFromProperty(PropertyReader.INGE_TRANSFORMATION_EDOC_CONFIGURATION_FILENAME).get(key);
  }

}
