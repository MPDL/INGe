package de.mpg.mpdl.inge.transformation.transformers;

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;

import de.mpg.mpdl.inge.transformation.ChainableTransformer;
import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;
import de.mpg.mpdl.inge.transformation.TransformerModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.util.PropertyReader;

@TransformerModule(sourceFormat = FORMAT.SPIRES_XML, targetFormat = FORMAT.ESCIDOC_ITEM_V3_XML)
public class SpiresXmlToItemXml extends XslTransformer implements ChainableTransformer {


  @Override
  public Source getXsltSource() throws TransformationException {

    return getXmlSourceFromProperty("inge.transformation.spires2escidoc_publication_item.stylesheet.filename",
        "transformations/thirdParty/xslt/spires2escidoc-publication-item.xsl");


  }

  @Override
  public Map<String, Object> getParameters() throws TransformationException {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("content-model", PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication"));
    map.put("external_organization_id", PropertyReader.getProperty("inge.pubman.external.organisation.id"));
    return map;
  }

  @Override
  public Map<String, String> getDefaultConfiguration() throws TransformationException {
    return null;
  }


}
