package de.mpg.mpdl.inge.transformation.transformers;

import java.util.Map;

import javax.xml.transform.Source;

import de.mpg.mpdl.inge.transformation.ChainableTransformer;
import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;
import de.mpg.mpdl.inge.transformation.TransformerModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.util.PropertyReader;

@TransformerModule(sourceFormat = FORMAT.ESCIDOC_ITEM_V3_XML, targetFormat = FORMAT.OAI_DC)
@TransformerModule(sourceFormat = FORMAT.ESCIDOC_ITEMLIST_V3_XML, targetFormat = FORMAT.OAI_DC)
public class ItemXmlToOaiDcXml extends XslTransformer implements ChainableTransformer {

  @Override
  public Source getXsltSource() throws TransformationException {
    return getXmlSourceFromProperty(PropertyReader.INGE_TRANSFORMATION_OAI_DC_STYLESHEET_FILENAME);
  }

  @Override
  public Map<String, Object> getParameters() throws TransformationException {
    return null;
  }

  @Override
  public Map<String, String> getDefaultConfiguration() throws TransformationException {
    return null;
  }

}
