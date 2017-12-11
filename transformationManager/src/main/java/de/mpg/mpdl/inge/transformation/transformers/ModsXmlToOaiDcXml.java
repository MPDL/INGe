package de.mpg.mpdl.inge.transformation.transformers;

import java.util.Map;

import javax.xml.transform.Source;

import de.mpg.mpdl.inge.transformation.ChainableTransformer;
import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;
import de.mpg.mpdl.inge.transformation.TransformerModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;

@TransformerModule(sourceFormat = FORMAT.MODS_XML, targetFormat = FORMAT.OAI_DC)
public class ModsXmlToOaiDcXml extends XslTransformer implements ChainableTransformer {


  @Override
  public Source getXsltSource() throws TransformationException {
    return getXmlSourceFromProperty("inge.transformation.mods2oaidc.stylesheet.filename",
        "transformations/standardFormats/xslt/mods2oai_dc.xsl");
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
