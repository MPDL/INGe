package de.mpg.mpdl.inge.transformation.transformers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;

import de.mpg.mpdl.inge.transformation.ChainableTransformer;
import de.mpg.mpdl.inge.transformation.SingleTransformer;
import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;
import de.mpg.mpdl.inge.transformation.TransformerModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.util.LocalUriResolver;
import de.mpg.mpdl.inge.util.PropertyReader;

@TransformerModule(sourceFormat = FORMAT.ENDNOTE_XML, targetFormat = FORMAT.ESCIDOC_ITEM_V3_XML)
@TransformerModule(sourceFormat = FORMAT.ENDNOTE_XML, targetFormat = FORMAT.ESCIDOC_ITEMLIST_V3_XML)
public class EndNoteXmlToItemXml extends XslTransformer implements ChainableTransformer {


  @Override
  public Source getXsltSource() throws TransformationException {
    String flavor = ((getConfiguration() == null || getConfiguration().isEmpty()) ? null : getConfiguration().get("Flavor"));

    if (flavor != null && ("ICE".equals(flavor) || "BGC".equals(flavor))) {

      return getXmlSourceFromProperty("inge.transformation.endnote.ice.stylesheet.filename",
          "transformations/commonPublicationFormats/xslt/endnoteicexml2escidoc.xsl");

    } else {

      return getXmlSourceFromProperty("inge.transformation.endnote.stylesheet.filename",
          "transformations/commonPublicationFormats/xslt/endnotexml2escidoc.xsl");
    }


  }

  @Override
  public Map<String, Object> getParameters() throws TransformationException {
    Map<String, Object> map = new HashMap<String, Object>();


    if (FORMAT.ESCIDOC_ITEM_V3_XML.equals(getTargetFormat())) {
      map.put("is-item-list", Boolean.FALSE);
    } else if (FORMAT.ESCIDOC_ITEMLIST_V3_XML.equals(getTargetFormat())) {
      map.put("is-item-list", Boolean.TRUE);
    }

    map.put("source-name", "endnote");
    map.put("root-ou", PropertyReader.getProperty("inge.pubman.root.organisation.id"));
    map.put("external-ou", PropertyReader.getProperty("inge.pubman.external.organisation.id"));
    map.put("frameworkUrl", PropertyReader.getFrameworkUrl());

    return map;

  }


  @Override
  public URIResolver getURIResolver() {
    return new LocalUriResolver("transformations/commonPublicationFormats/xslt");
  }

  @Override
  public Map<String, String> getDefaultConfiguration() throws TransformationException {
    return SingleTransformer.getDefaultConfigurationFromProperty("inge.transformation.endnote.configuration.filename",
        "transformations/commonPublicationFormats/conf/endnote.properties");
  }

  @Override
  public List<String> getAllConfigurationValuesFor(String key) throws TransformationException {
    return SingleTransformer.getAllConfigurationValuesFromProperty("inge.transformation.endnote.configuration.filename",
        "transformations/commonPublicationFormats/conf/endnote.properties").get(key);
  }


}
