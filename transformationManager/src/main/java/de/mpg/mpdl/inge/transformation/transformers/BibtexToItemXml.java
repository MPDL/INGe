package de.mpg.mpdl.inge.transformation.transformers;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.List;

import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamSource;

import de.mpg.mpdl.inge.transformation.ChainableTransformer;
import de.mpg.mpdl.inge.transformation.ImportUsableTransformer;
import de.mpg.mpdl.inge.transformation.SingleTransformer;
import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;
import de.mpg.mpdl.inge.transformation.TransformerModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.transformation.results.TransformerResult;
import de.mpg.mpdl.inge.transformation.results.TransformerStreamResult;
import de.mpg.mpdl.inge.transformation.sources.TransformerSource;
import de.mpg.mpdl.inge.transformation.transformers.helpers.bibtex.Bibtex;

@TransformerModule(sourceFormat = FORMAT.BIBTEX_STRING,
    targetFormat = FORMAT.ESCIDOC_ITEMLIST_V3_XML)
@TransformerModule(sourceFormat = FORMAT.BIBTEX_STRING, targetFormat = FORMAT.ESCIDOC_ITEM_V3_XML)
public class BibtexToItemXml extends SingleTransformer implements ChainableTransformer,
    ImportUsableTransformer {

  @Override
  public void transform(TransformerSource source, TransformerResult result)
      throws TransformationException {

    try {

      Bibtex bib = new Bibtex();
      if (getConfiguration() == null) {
        setConfiguration(getDefaultConfigurationFromProperty(
            "escidoc.transformation.bibtex.configuration.filename",
            "transformations/commonPublicationFormats/conf/bibtex.properties"));
      }
      bib.setConfiguration(getConfiguration());

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
  public List<String> getConfigurationValuesFor(String key) throws TransformationException {
    return getAllConfigurationValuesFromProperty(
        "escidoc.transformation.bibtex.configuration.filename",
        "transformations/commonPublicationFormats/conf/bibtex.properties").get(key);
  }

}
