package de.mpg.mpdl.inge.transformation.transformers;

import java.util.Map;

import javax.xml.transform.Source;

import de.mpg.mpdl.inge.transformation.ChainableTransformer;
import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;
import de.mpg.mpdl.inge.transformation.TransformerModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;

@TransformerModule(sourceFormat = FORMAT.MODS_XML, targetFormat = FORMAT.MARC_XML)
public class ModsXmlToMarcXml extends XslTransformer implements ChainableTransformer {


  @Override
  public Source getXsltSource() throws TransformationException {
    return getXmlSourceFromProperty("escidoc.transformation.mods2marc.stylesheet.filename",
        "transformations/standardFormats/xslt/mods2marc21.xsl");
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
