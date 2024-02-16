package de.mpg.mpdl.inge.transformation.transformers;

import java.util.Map;

import javax.xml.transform.Source;

import de.mpg.mpdl.inge.transformation.ChainableTransformer;
import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;
import de.mpg.mpdl.inge.transformation.TransformerModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.util.PropertyReader;

@TransformerModule(sourceFormat = FORMAT.JUS_SNIPPET_XML, targetFormat = FORMAT.JUS_INDESIGN_XML)
@TransformerModule(sourceFormat = FORMAT.JUS_SNIPPET_XML, targetFormat = FORMAT.JUS_HTML_XML)
public class JusSnippetXmlToJusIndesignXml extends XslTransformer implements ChainableTransformer {

  @Override
  public Source getXsltSource() throws TransformationException {
    if (FORMAT.JUS_INDESIGN_XML.equals(getTargetFormat())) {
      return getXmlSourceFromProperty(PropertyReader.INGE_TRANSFORMATION_JUS_INDESIGN_STYLESHEET_FILENAME);
    } else if (FORMAT.JUS_HTML_XML.equals(getTargetFormat())) {
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
