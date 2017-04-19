package de.mpg.mpdl.inge.transformation.transformers;

import java.util.Map;

import javax.xml.transform.Source;

import de.mpg.mpdl.inge.transformation.ChainableTransformer;
import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;
import de.mpg.mpdl.inge.transformation.TransformerModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;

@TransformerModule(sourceFormat = FORMAT.BMC_FULLTEXT_XML, targetFormat = FORMAT.BMC_FULLTEXT_HTML)
public class BmcFulltextXmlToBmcFulltextHtml extends XslTransformer implements ChainableTransformer {

  @Override
  public Source getXsltSource() throws TransformationException {
    return getXmlSourceFromProperty(
        "escidoc.transformation.bmcfulltext2bmcfulltexthtml.stylesheet.filename",
        "transformations/thirdParty/xslt/bmc-fulltext-xml2bmc-fulltext-html.xsl");
  }

  @Override
  public Map<String, Object> getParameters() throws TransformationException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Map<String, String> getDefaultConfiguration() throws TransformationException {
    /**
     * return SingleTransformer.getDefaultConfigurationFromProperty(
     * "escidoc.transformation.bmcfulltext2bmcfulltexthtml.configuration.filename",
     * "transformations/commonPublicationFormats/conf/bmcfulltext2bmcfulltexthtml.properties");
     */
    return null;
  }

}
