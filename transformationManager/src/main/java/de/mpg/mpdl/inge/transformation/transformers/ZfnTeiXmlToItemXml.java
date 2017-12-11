package de.mpg.mpdl.inge.transformation.transformers;

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;

import de.mpg.mpdl.inge.transformation.ChainableTransformer;
import de.mpg.mpdl.inge.transformation.SingleTransformer;
import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;
import de.mpg.mpdl.inge.transformation.TransformerModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.util.PropertyReader;

@TransformerModule(sourceFormat = FORMAT.ZFN_TEI_XML, targetFormat = FORMAT.ESCIDOC_ITEM_V3_XML)
@TransformerModule(sourceFormat = FORMAT.ZFN_TEI_XML, targetFormat = FORMAT.ESCIDOC_ITEMLIST_V3_XML)
public class ZfnTeiXmlToItemXml extends XslTransformer implements ChainableTransformer {


  @Override
  public Source getXsltSource() throws TransformationException {

    return getXmlSourceFromProperty("inge.transformation.zfn.stylesheet.filename",
        "transformations/standardFormats/xslt/zfn_tei2escidoc-publication-item.xsl");


  }

  @Override
  public Map<String, Object> getParameters() throws TransformationException {
    Map<String, Object> map = new HashMap<String, Object>();
    if (FORMAT.ESCIDOC_ITEM_V3_XML.equals(getTargetFormat())) {
      map.put("is-item-list", Boolean.FALSE);
    } else if (FORMAT.ESCIDOC_ITEMLIST_V3_XML.equals(getTargetFormat())) {
      map.put("is-item-list", Boolean.TRUE);
    }


    map.put("zfnId", getConfiguration().get("id"));
    map.put("content-model", PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication"));
    map.put("external_organisation_id", PropertyReader.getProperty("inge.pubman.external.organisation.id"));
    return map;
  }

  @Override
  public Map<String, String> getDefaultConfiguration() throws TransformationException {
    return SingleTransformer.getDefaultConfigurationFromProperty("inge.transformation.zfn.configuration.filename",
        "transformations/standardFormats/conf/zfn.properties");
  }


}
