package de.mpg.mpdl.inge.transformation.transformers;

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;

import de.mpg.mpdl.inge.transformation.ChainableTransformer;
import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;
import de.mpg.mpdl.inge.transformation.TransformerModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.util.LocalUriResolver;
import de.mpg.mpdl.inge.util.PropertyReader;

@TransformerModule(sourceFormat = FORMAT.ESCIDOC_ITEMLIST_V3_XML, targetFormat = FORMAT.EDOC_XML)
@TransformerModule(sourceFormat = FORMAT.ESCIDOC_ITEM_V3_XML, targetFormat = FORMAT.EDOC_XML)
public class ItemXmlToEdocXml extends XslTransformer implements ChainableTransformer {


  @Override
  public Source getXsltSource() throws TransformationException {
    return getXmlSourceFromProperty("inge.transformation.escidoc2edoc_export.stylesheet.filename",
        "transformations/otherFormats/xslt/escidoc2edoc_export.xsl");
  }

  @Override
  public Map<String, Object> getParameters() throws TransformationException {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("pubman_instance", PropertyReader.getProperty("inge.pubman.instance.url"));
    return map;
  }


  @Override
  public URIResolver getURIResolver() {
    return new LocalUriResolver("transformations/otherFormats/xslt");
  }

  @Override
  public Map<String, String> getDefaultConfiguration() throws TransformationException {
    return null;
  }


}
