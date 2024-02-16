package de.mpg.mpdl.inge.transformation.transformers;

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;

import de.mpg.mpdl.inge.transformation.ChainableTransformer;
import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;
import de.mpg.mpdl.inge.transformation.TransformerModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.util.PropertyReader;

@TransformerModule(sourceFormat = FORMAT.BMC_OAIPMH_XML, targetFormat = FORMAT.ESCIDOC_ITEM_V3_XML)
public class BmcOaiPmhXmlToItemXml extends XslTransformer implements ChainableTransformer {

  @Override
  public Source getXsltSource() throws TransformationException {
    return getXmlSourceFromProperty(PropertyReader.INGE_TRANSFORMATION_BMC2ESCIDOC_PUBLICATION_ITEM_STYLESHEET_FILENAME);
  }

  @Override
  public Map<String, Object> getParameters() {
    Map<String, Object> map = new HashMap<>();
    map.put("external_organization_id", PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_EXTERNAL_ORGANISATION_ID));

    return map;
  }

  @Override
  public Map<String, String> getDefaultConfiguration() {
    return null;
  }

}
