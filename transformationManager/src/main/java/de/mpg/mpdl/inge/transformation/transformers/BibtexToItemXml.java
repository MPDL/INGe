package de.mpg.mpdl.inge.transformation.transformers;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamSource;

import de.mpg.mpdl.inge.transformation.ChainableTransformer;
import de.mpg.mpdl.inge.transformation.SingleTransformer;
import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;
import de.mpg.mpdl.inge.transformation.TransformerModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.transformation.results.TransformerResult;
import de.mpg.mpdl.inge.transformation.results.TransformerStreamResult;
import de.mpg.mpdl.inge.transformation.sources.TransformerSource;
import de.mpg.mpdl.inge.transformation.transformers.helpers.bibtex.Bibtex;

@TransformerModule(sourceFormat = FORMAT.BIBTEX_STRING, targetFormat = FORMAT.ESCIDOC_ITEMLIST_V3_XML)
@TransformerModule(sourceFormat = FORMAT.BIBTEX_STRING, targetFormat = FORMAT.ESCIDOC_ITEM_V3_XML)
public class BibtexToItemXml extends SingleTransformer implements ChainableTransformer {

  @Override
  public void transform(TransformerSource source, TransformerResult result) throws TransformationException {

    try {

      Bibtex bib = new Bibtex();

      String res = bib.getBibtex(getStringFromSource(source));

      XslTransformer.xmlSourceToXmlResult(new StreamSource(new StringReader(res)), (Result) result);

    } catch (Exception e) {
      throw new TransformationException("Error while transforming Bibtex to item XML", e);
    }

  }

  @Override
  public TransformerResult createNewInBetweenResult() {
    return new TransformerStreamResult(new ByteArrayOutputStream());
  }

  @Override
  public List<String> getAllConfigurationValuesFor(String key) throws TransformationException {
    return getAllConfigurationValuesFromProperty("inge.transformation.bibtex.configuration.filename",
        "transformations/commonPublicationFormats/conf/bibtex.properties").get(key);
  }

  @Override
  public Map<String, String> getConfiguration() {

    if (super.getConfiguration() == null || super.getConfiguration().isEmpty()) {
      Map<String, String> c = new HashMap<String, String>();
      try {
        c = getDefaultConfigurationFromProperty("inge.transformation.bibtex.configuration.filename",
            "transformations/commonPublicationFormats/conf/bibtex.properties");

        setConfiguration(c);
        return c;
      } catch (TransformationException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return super.getConfiguration();
  }

}
