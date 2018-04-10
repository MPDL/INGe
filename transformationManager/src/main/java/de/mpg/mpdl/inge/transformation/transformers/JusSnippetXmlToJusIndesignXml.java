package de.mpg.mpdl.inge.transformation.transformers;

import java.util.Map;

import javax.xml.transform.Source;

import de.mpg.mpdl.inge.transformation.ChainableTransformer;
import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;
import de.mpg.mpdl.inge.transformation.TransformerModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;

@TransformerModule(sourceFormat = FORMAT.JUS_SNIPPET_XML, targetFormat = FORMAT.JUS_INDESIGN_XML)
@TransformerModule(sourceFormat = FORMAT.JUS_SNIPPET_XML, targetFormat = FORMAT.JUS_HTML_XML)
public class JusSnippetXmlToJusIndesignXml extends XslTransformer implements ChainableTransformer {

  @Override
  public Source getXsltSource() throws TransformationException {
    if (FORMAT.JUS_INDESIGN_XML.equals(getTargetFormat())) {
      return getXmlSourceFromProperty("inge.transformation.jus_indesign.stylesheet.filename",
          "transformations/reports/xslt/jus_report_snippet2jus_out_indesign.xsl");
    } else if (FORMAT.JUS_HTML_XML.equals(getTargetFormat())) {
      return getXmlSourceFromProperty("inge.transformation.jus_html.stylesheet.filename",
          "transformations/reports/xslt/jus_report_snippet2jus_out_html.xsl");
    } else {
      return null;
    }
  }

  @Override
  public Map<String, Object> getParameters() throws TransformationException {
    return null;
    //    Map<String, Object> map = new HashMap<String, Object>();
    //
    //    map.put("indesign-namespace", PropertyReader.getProperty("inge.report.indesign.namespace"));
    //
    //    try {
    //      DocumentBuilderFactory fac = new DocumentBuilderFactoryImpl();
    //      fac.setNamespaceAware(true);
    //      DocumentBuilder docBuilder = fac.newDocumentBuilder();
    //      Document sortDoc =
    //          docBuilder.parse(ResourceUtil.getResourceAsStream(PropertyReader.getProperty("inge.transformation.report.sortorder.filename"),
    //              JusSnippetXmlToJusIndesignXml.class.getClassLoader()));
    //
    //      map.put("sortOrderXml", sortDoc.getDocumentElement());
    //    } catch (Exception e) {
    //      throw new TransformationException("Error while parsing sort order xml for JUS snippet transformation processing", e);
    //    }
    //
    //    return map;
  }


  @Override
  public Map<String, String> getDefaultConfiguration() throws TransformationException {
    return null;
  }

}
