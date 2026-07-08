package de.mpg.mpdl.inge.transformation.transformers;

import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.util.MapperFactory;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.AbstractVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.transformation.ChainableTransformer;
import de.mpg.mpdl.inge.transformation.SingleTransformer;
import de.mpg.mpdl.inge.transformation.Transformer;
import de.mpg.mpdl.inge.transformation.TransformerFactory;
import de.mpg.mpdl.inge.transformation.TransformerModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.transformation.results.TransformerResult;
import de.mpg.mpdl.inge.transformation.results.TransformerStreamResult;
import de.mpg.mpdl.inge.transformation.sources.TransformerSource;
import de.mpg.mpdl.inge.transformation.sources.TransformerVoSource;
import de.mpg.mpdl.inge.transformation.transformers.helpers.mab.MABImport;
import de.mpg.mpdl.inge.util.LocalUriResolver;
import de.mpg.mpdl.inge.util.PropertyReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Element;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@TransformerModule(sourceFormat = TransformerFactory.FORMAT.SEARCH_RESULT_VO, targetFormat = TransformerFactory.FORMAT.HTML_SCHOLAR)
public class ItemXmlToScholarHtml extends SingleTransformer implements ChainableTransformer {

  @Override
  public void transform(TransformerSource source, TransformerResult result) throws TransformationException {
    try {

      SearchRetrieveResponseVO<ItemVersionVO> searchResult =
          (SearchRetrieveResponseVO<ItemVersionVO>) ((TransformerVoSource) source).getSource();

      if (searchResult.getRecords().size() > 0) {

        Transformer dcTransformer =
            TransformerFactory.newTransformer(TransformerFactory.FORMAT.SEARCH_RESULT_VO, TransformerFactory.FORMAT.HTML_METATAGS_DC_XML);
        StringWriter dcWriter = new StringWriter();
        dcTransformer.transform(new TransformerVoSource(searchResult), new TransformerStreamResult(dcWriter));

        Transformer highwireTransformer = TransformerFactory.newTransformer(TransformerFactory.FORMAT.SEARCH_RESULT_VO,
            TransformerFactory.FORMAT.HTML_METATAGS_HIGHWIRE_PRESS_CIT_XML);
        StringWriter highwireWriter = new StringWriter();
        highwireTransformer.transform(new TransformerVoSource(searchResult), new TransformerStreamResult(highwireWriter));

        ItemVersionVO item = searchResult.getRecords().get(0).getData();
        Document doc = Jsoup.parse("<html></html>");
        doc.prependChild(new DocumentType("html", "", ""));
        Element headEl = doc.head();
        headEl.append(dcWriter.toString());
        headEl.append(highwireWriter.toString());
        Element bodyEl = doc.body();
        headEl.appendElement("title").appendText(item.getMetadata().getTitle());
        bodyEl.appendElement("h1").appendText(item.getMetadata().getTitle());
        if (item.getMetadata().getCreators() != null) {
          Element creatorPart = bodyEl.appendElement("span");
          for (CreatorVO creator : item.getMetadata().getCreators()) {
            if (creator.getPerson() != null) {
              creatorPart.appendText(creator.getPerson().getFamilyName() + ", " + creator.getPerson().getGivenName() + "; ");
            } else if (creator.getOrganization() != null) {
              creatorPart.appendText(creator.getOrganization().getName() + "; ");
            }
          }
        }
        if (item.getMetadata().getAbstracts() != null) {
          for (AbstractVO abs : item.getMetadata().getAbstracts()) {
            bodyEl.appendElement("p").appendText(abs.getValue());
          }

        }
        writeStringToStreamResult(doc.html(), result);
      }

    } catch (Exception e) {
      throw new TransformationException("Error while transforming Mab Text to Mab XML", e);
    }
  }

  @Override
  public TransformerResult createNewInBetweenResult() {
    return new TransformerStreamResult(new ByteArrayOutputStream());
  }

  @Override
  public void xmlSourceToXmlResult(Source s, Result r) throws TransformerException {

  }


}
