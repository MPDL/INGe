package de.mpg.mpdl.inge.transformation.transformers;

import de.mpg.mpdl.inge.transformation.TransformerFactory;
import de.mpg.mpdl.inge.transformation.TransformerModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.util.PropertyReader;
import java.util.Map;
import javax.xml.transform.Source;

@TransformerModule(sourceFormat = TransformerFactory.FORMAT.ARXIV_OAIPMH_XML,
    targetFormat = TransformerFactory.FORMAT.ESCIDOC_COMPONENT_XML)
public class ArxivOaiPmhXmlToComponentXml extends XslTransformer {

  @Override
  public Source getXsltSource() throws TransformationException {
    return getXmlSourceFromProperty(PropertyReader.INGE_TRANSFORMATION_ARXIV2ESCIDOC_PUBLICATION_COMPONENT_STYLESHEET_FILENAME);
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
