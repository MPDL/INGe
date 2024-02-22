package de.mpg.mpdl.inge.transformation.transformers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;

import de.mpg.mpdl.inge.transformation.TransformerFactory;
import de.mpg.mpdl.inge.transformation.TransformerModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.util.PropertyReader;

@TransformerModule(sourceFormat = TransformerFactory.FORMAT.WOS_XML, targetFormat = TransformerFactory.FORMAT.ESCIDOC_ITEM_V3_XML)
@TransformerModule(sourceFormat = TransformerFactory.FORMAT.WOS_XML, targetFormat = TransformerFactory.FORMAT.ESCIDOC_ITEMLIST_V3_XML)
public class WosXmlToItemXml extends XslTransformer {

  @Override
  public Source getXsltSource() throws TransformationException {
    return getXmlSourceFromProperty(PropertyReader.INGE_TRANSFORMATION_WOS_STYLESHEET_FILENAME);
  }

  @Override
  public Map<String, Object> getParameters() {
    Map<String, Object> map = new HashMap<>();

    if (TransformerFactory.FORMAT.ESCIDOC_ITEM_V3_XML.equals(getTargetFormat())) {
      map.put("is-item-list", Boolean.FALSE);
    } else if (TransformerFactory.FORMAT.ESCIDOC_ITEMLIST_V3_XML.equals(getTargetFormat())) {
      map.put("is-item-list", Boolean.TRUE);
    }
    map.put("external-organization", PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_EXTERNAL_ORGANISATION_ID));

    return map;
  }

  @Override
  public Map<String, String> getDefaultConfiguration() throws TransformationException {
    return getDefaultConfigurationFromProperty(PropertyReader.INGE_TRANSFORMATION_WOS_CONFIGURATION_FILENAME);
  }

  @Override
  public List<String> getAllConfigurationValuesFor(String key) throws TransformationException {
    return getAllConfigurationValuesFromProperty(PropertyReader.INGE_TRANSFORMATION_WOS_CONFIGURATION_FILENAME).get(key);
  }

}
