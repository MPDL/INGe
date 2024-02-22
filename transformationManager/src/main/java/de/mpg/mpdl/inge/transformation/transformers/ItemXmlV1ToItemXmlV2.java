package de.mpg.mpdl.inge.transformation.transformers;

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;

import de.mpg.mpdl.inge.transformation.TransformerFactory;
import de.mpg.mpdl.inge.transformation.TransformerModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.util.PropertyReader;

@TransformerModule(sourceFormat = TransformerFactory.FORMAT.ESCIDOC_ITEM_V1_XML,
    targetFormat = TransformerFactory.FORMAT.ESCIDOC_ITEM_V2_XML)
@TransformerModule(sourceFormat = TransformerFactory.FORMAT.ESCIDOC_ITEMLIST_V1_XML,
    targetFormat = TransformerFactory.FORMAT.ESCIDOC_ITEMLIST_V2_XML)
public class ItemXmlV1ToItemXmlV2 extends XslTransformer {

  @Override
  public Source getXsltSource() throws TransformationException {
    return getXmlSourceFromProperty(PropertyReader.INGE_TRANSFORMATION_ESCIDOC_V1_TO_ESCIDOC_V2_STYLESHEET_FILENAME);
  }

  @Override
  public Map<String, Object> getParameters() {
    Map<String, Object> map = new HashMap<>();

    if (TransformerFactory.FORMAT.ESCIDOC_ITEM_V2_XML.equals(getTargetFormat())) {
      map.put("is-item-list", Boolean.FALSE);
    } else if (TransformerFactory.FORMAT.ESCIDOC_ITEMLIST_V2_XML.equals(getTargetFormat())) {
      map.put("is-item-list", Boolean.TRUE);
    }

    return map;
  }

  @Override
  public Map<String, String> getDefaultConfiguration() {
    return null;
  }

}
