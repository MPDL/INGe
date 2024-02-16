package de.mpg.mpdl.inge.transformation.transformers;

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;

import de.mpg.mpdl.inge.transformation.ChainableTransformer;
import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;
import de.mpg.mpdl.inge.transformation.TransformerModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.util.PropertyReader;

@TransformerModule(sourceFormat = FORMAT.PEER_TEI_XML, targetFormat = FORMAT.ESCIDOC_ITEM_V3_XML)
@TransformerModule(sourceFormat = FORMAT.PEER_TEI_XML, targetFormat = FORMAT.ESCIDOC_ITEMLIST_V3_XML)
public class PeerTeiXmlToItemXml extends XslTransformer implements ChainableTransformer {

  @Override
  public Source getXsltSource() throws TransformationException {
    return getXmlSourceFromProperty(PropertyReader.INGE_TRANSFORMATION_PEER_STYLESHEET_FILENAME);
  }

  @Override
  public Map<String, Object> getParameters() {
    Map<String, Object> map = new HashMap<>();

    if (FORMAT.ESCIDOC_ITEM_V3_XML.equals(getTargetFormat())) {
      map.put("is-item-list", Boolean.FALSE);
    } else if (FORMAT.ESCIDOC_ITEMLIST_V3_XML.equals(getTargetFormat())) {
      map.put("is-item-list", Boolean.TRUE);
    }

    map.put("external_organisation_id", PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_EXTERNAL_ORGANISATION_ID));

    return map;
  }

  @Override
  public Map<String, String> getDefaultConfiguration() {
    return null;
  }

}
