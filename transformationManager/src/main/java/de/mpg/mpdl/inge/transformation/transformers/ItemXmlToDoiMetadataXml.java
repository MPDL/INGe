package de.mpg.mpdl.inge.transformation.transformers;

import java.util.Map;

import javax.xml.transform.Source;

import de.mpg.mpdl.inge.transformation.TransformerFactory;
import de.mpg.mpdl.inge.transformation.TransformerModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.util.PropertyReader;

@TransformerModule(sourceFormat = TransformerFactory.FORMAT.ESCIDOC_ITEM_V3_XML, targetFormat = TransformerFactory.FORMAT.DOI_METADATA_XML)
public class ItemXmlToDoiMetadataXml extends XslTransformer {

  @Override
  public Source getXsltSource() throws TransformationException {
    return getXmlSourceFromProperty(PropertyReader.INGE_TRANSFORMATION_DOI_STYLESHEET_FILENAME);
  }

  @Override
  public Map<String, Object> getParameters() {
    return null;
  }

  @Override
  public Map<String, String> getDefaultConfiguration() {
    return null;
  }

}
