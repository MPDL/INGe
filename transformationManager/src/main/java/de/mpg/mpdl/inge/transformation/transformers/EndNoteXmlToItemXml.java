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
      return getXmlSourceFromProperty(PropertyReader.INGE_TRANSFORMATION_ENDNOTE_ICE_STYLESHEET_FILENAME);
    } else {
      return getXmlSourceFromProperty(PropertyReader.INGE_TRANSFORMATION_ENDNOTE_STYLESHEET_FILENAME);
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
    map.put("root-ou", PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_ROOT_ORGANIZATION_ID));
    map.put("external-ou", PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_EXTERNAL_ORGANIZATION_ID));
    map.put("frameworkUrl", PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_URL));

    return map;
  }

  @Override
  public URIResolver getURIResolver() {
    return new LocalUriResolver("transformations/commonPublicationFormats/xslt");
  }

  @Override
  public Map<String, String> getDefaultConfiguration() throws TransformationException {
    return SingleTransformer.getDefaultConfigurationFromProperty(PropertyReader.INGE_TRANSFORMATION_ENDNOTE_CONFIGURATION_FILENAME);
  }

  @Override
  public List<String> getAllConfigurationValuesFor(String key) throws TransformationException {
    return SingleTransformer.getAllConfigurationValuesFromProperty(PropertyReader.INGE_TRANSFORMATION_ENDNOTE_CONFIGURATION_FILENAME)
        .get(key);
  }

}
