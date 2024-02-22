package de.mpg.mpdl.inge.transformation.transformers;

import java.util.Map;

import javax.xml.transform.Source;

import de.mpg.mpdl.inge.transformation.TransformerFactory;
import de.mpg.mpdl.inge.transformation.TransformerModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.util.PropertyReader;

@TransformerModule(sourceFormat = TransformerFactory.FORMAT.JUS_SNIPPET_XML, targetFormat = TransformerFactory.FORMAT.JUS_INDESIGN_XML)
@TransformerModule(sourceFormat = TransformerFactory.FORMAT.JUS_SNIPPET_XML, targetFormat = TransformerFactory.FORMAT.JUS_HTML_XML)
public class JusSnippetXmlToJusIndesignXml extends XslTransformer {

  @Override
  public Source getXsltSource() throws TransformationException {
    if (TransformerFactory.FORMAT.JUS_INDESIGN_XML.equals(getTargetFormat())) {
      return getXmlSourceFromProperty(PropertyReader.INGE_TRANSFORMATION_JUS_INDESIGN_STYLESHEET_FILENAME);
    } else if (TransformerFactory.FORMAT.JUS_HTML_XML.equals(getTargetFormat())) {
      return getXmlSourceFromProperty(PropertyReader.INGE_TRANSFORMATION_JUS_SNIPPET2JUS_STYLESHEET_FILENAME);
    } else {
      return null;
    }
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
