package de.mpg.mpdl.inge.transformation.transformers;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import de.mpg.mpdl.inge.transformation.ChainableTransformer;
import de.mpg.mpdl.inge.transformation.SingleTransformer;
import de.mpg.mpdl.inge.transformation.TransformerModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.transformation.results.TransformerResult;
import de.mpg.mpdl.inge.transformation.results.TransformerStreamResult;
import de.mpg.mpdl.inge.transformation.sources.TransformerSource;
import de.mpg.mpdl.inge.transformation.transformers.helpers.ris.RISImport;

@TransformerModule(sourceFormat = de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT.RIS_STRING,
    targetFormat = de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT.RIS_XML)
public class RisToRisXml extends SingleTransformer implements ChainableTransformer {

  @Override
  public void transform(TransformerSource source, TransformerResult result) throws TransformationException {
    try {
      RISImport risImport = new RISImport();

      String resultXmlString = risImport.transformRIS2XML(getStringFromSource(source));

      this.xmlSourceToXmlResult(new StreamSource(new StringReader(resultXmlString)), (Result) result);
    } catch (Exception e) {
      throw new TransformationException("Error while transforming RIS to RIS XML", e);
    }
  }

  @Override
  public TransformerResult createNewInBetweenResult() {
    return new TransformerStreamResult(new ByteArrayOutputStream());
  }

  @Override
  public void xmlSourceToXmlResult(Source s, Result r) throws TransformerException {
    TransformerFactory xslTransformerFactory = de.mpg.mpdl.inge.transformation.SaxonFactoryProvider.createWithExtensions();
    Transformer t = xslTransformerFactory.newTransformer();
    t.setOutputProperty(OutputKeys.INDENT, "yes");
    t.setOutputProperty(OutputKeys.METHOD, "xml");
    t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    t.transform(s, r);
  }

}
