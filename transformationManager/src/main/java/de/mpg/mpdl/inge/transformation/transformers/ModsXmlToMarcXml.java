package de.mpg.mpdl.inge.transformation.transformers;

import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;

import de.mpg.mpdl.inge.transformation.ChainableTransformer;
import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;
import de.mpg.mpdl.inge.transformation.TransformerModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.util.LocalUriResolver;
import de.mpg.mpdl.inge.util.PropertyReader;

@TransformerModule(sourceFormat = FORMAT.MODS_XML, targetFormat = FORMAT.MARC_XML)
public class ModsXmlToMarcXml extends XslTransformer implements ChainableTransformer {

  @Override
  public Source getXsltSource() throws TransformationException {
    return getXmlSourceFromProperty(PropertyReader.INGE_TRANSFORMATION_MODS2MARC_STYLESHEET_FILENAME);
  }

  @Override
  public URIResolver getURIResolver() {
    return new LocalUriResolver("transformations/standardFormats/xslt");
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
