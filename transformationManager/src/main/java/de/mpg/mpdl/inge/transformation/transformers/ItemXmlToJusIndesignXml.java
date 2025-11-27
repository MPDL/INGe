package de.mpg.mpdl.inge.transformation.transformers;

import de.mpg.mpdl.inge.transformation.Transformer;
import de.mpg.mpdl.inge.transformation.TransformerFactory;
import de.mpg.mpdl.inge.transformation.TransformerModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.transformation.results.TransformerResult;
import de.mpg.mpdl.inge.transformation.results.TransformerStreamResult;
import de.mpg.mpdl.inge.transformation.sources.TransformerSource;
import de.mpg.mpdl.inge.util.ConeUtils;
import de.mpg.mpdl.inge.util.PropertyReader;

import javax.xml.transform.Source;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

@TransformerModule(sourceFormat = TransformerFactory.FORMAT.SEARCH_RESULT_VO, targetFormat = TransformerFactory.FORMAT.JUS_INDESIGN_XML)
@TransformerModule(sourceFormat = TransformerFactory.FORMAT.SEARCH_RESULT_VO, targetFormat = TransformerFactory.FORMAT.JUS_HTML_XML)
public class ItemXmlToJusIndesignXml extends XslTransformer {


  @Override
  public void transform(TransformerSource source, TransformerResult result) throws TransformationException {

    //Transform to escidoc snippet with JUS citation first
    Transformer jusCitationtransformer =
        TransformerFactory.newTransformer(TransformerFactory.FORMAT.SEARCH_RESULT_VO, TransformerFactory.FORMAT.ESCIDOC_SNIPPET);
    jusCitationtransformer.getConfiguration().put(CitationTransformer.CONFIGURATION_CITATION,
        TransformerFactory.CitationTypes.JUS_Report.getCitationName());
    TransformerStreamResult escidocSnippetJusCitationResult = new TransformerStreamResult(new ByteArrayOutputStream());
    jusCitationtransformer.transform(source, escidocSnippetJusCitationResult);

    //then transform to report
    this.getConfiguration().put("conePersonsIdIdentifier", ConeUtils.getConePersonsIdIdentifier());
    super.transform(escidocSnippetJusCitationResult.createSourceFromInBetweenResult(), result);

  }

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

    Map<String, String> map = new HashMap<>();
    return map;
  }

}
