package de.mpg.mpdl.inge.transformation.transformers;

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;

import de.mpg.mpdl.inge.transformation.ChainableTransformer;
import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;
import de.mpg.mpdl.inge.transformation.TransformerModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;

@TransformerModule(sourceFormat = FORMAT.ESCIDOC_ITEM_V2_XML,
    targetFormat = FORMAT.ESCIDOC_ITEM_V1_XML)
@TransformerModule(sourceFormat = FORMAT.ESCIDOC_ITEMLIST_V2_XML,
    targetFormat = FORMAT.ESCIDOC_ITEMLIST_V1_XML)
public class ItemXmlV2ToItemXmlV1 extends XslTransformer implements ChainableTransformer {


  @Override
  public Source getXsltSource() throws TransformationException {
    return getXmlSourceFromProperty(
        "escidoc.transformation.escidoc_v2_to_escidoc_v1.stylesheet.filename",
        "transformations/otherFormats/xslt/escidoc-publication-v2_2_escidoc-publication-v1.xsl");
  }

  @Override
  public Map<String, Object> getParameters() throws TransformationException {
    Map<String, Object> map = new HashMap<String, Object>();

    if (FORMAT.ESCIDOC_ITEM_V1_XML.equals(getTargetFormat())) {
      map.put("is-item-list", Boolean.FALSE);
    } else if (FORMAT.ESCIDOC_ITEMLIST_V1_XML.equals(getTargetFormat())) {
      map.put("is-item-list", Boolean.TRUE);
    }

    return map;

  }


  @Override
  public Map<String, String> getDefaultConfiguration() throws TransformationException {
    return null;
  }

}
