package de.mpg.mpdl.inge.transformation.transformers;

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;

import de.mpg.mpdl.inge.transformation.ChainableTransformer;
import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;
import de.mpg.mpdl.inge.transformation.TransformerModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.util.PropertyReader;

@TransformerModule(sourceFormat = FORMAT.ESCIDOC_ITEM_V3_XML, targetFormat = FORMAT.ESCIDOC_ITEM_V2_XML)
@TransformerModule(sourceFormat = FORMAT.ESCIDOC_ITEMLIST_V3_XML, targetFormat = FORMAT.ESCIDOC_ITEMLIST_V2_XML)
public class ItemXmlV3ToItemXmlV2 extends XslTransformer implements ChainableTransformer {

  @Override
  public Source getXsltSource() throws TransformationException {
    return getXmlSourceFromProperty(PropertyReader.INGE_TRANSFORMATION_ESCIDOC_V3_TO_ESCIDOC_V2_STYLESHEET_FILENAME);
  }

  @Override
  public Map<String, Object> getParameters() {
    Map<String, Object> map = new HashMap<>();

    if (FORMAT.ESCIDOC_ITEM_V2_XML.equals(getTargetFormat())) {
      map.put("is-item-list", Boolean.FALSE);
    } else if (FORMAT.ESCIDOC_ITEMLIST_V2_XML.equals(getTargetFormat())) {
      map.put("is-item-list", Boolean.TRUE);
    }

    return map;

  }

  @Override
  public Map<String, String> getDefaultConfiguration() {
    return null;
  }

}
