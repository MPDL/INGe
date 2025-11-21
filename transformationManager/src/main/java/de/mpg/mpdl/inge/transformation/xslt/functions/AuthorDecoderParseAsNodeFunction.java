package de.mpg.mpdl.inge.transformation.xslt.functions;

import de.mpg.mpdl.inge.transformation.util.creators.AuthorDecoder;
import net.sf.saxon.s9api.ExtensionFunction;
import net.sf.saxon.s9api.ItemType;
import net.sf.saxon.s9api.OccurrenceIndicator;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.SequenceType;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmValue;
import net.sf.saxon.s9api.Processor;

import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Node;

/**
 * s9api extension function: AuthorDecoder:parseAsNode(item()*) as document-node()? Namespace URI:
 * java:de.mpg.mpdl.inge.transformation.util.creators.AuthorDecoder
 */
public final class AuthorDecoderParseAsNodeFunction implements ExtensionFunction {
  private static final QName NAME = new QName("AuthorDecoder", "https://pubman.mpdl.mpg.de/author-decoder-functions", "parseAsNode");
  private final Processor processor;

  public AuthorDecoderParseAsNodeFunction(Processor processor) {
    this.processor = processor;
  }

  @Override
  public QName getName() {
    return NAME;
  }

  @Override
  public SequenceType[] getArgumentTypes() {
    return new SequenceType[] {SequenceType.makeSequenceType(ItemType.ANY_ITEM, OccurrenceIndicator.ZERO_OR_MORE)};
  }

  @Override
  public SequenceType getResultType() {
    return SequenceType.makeSequenceType(ItemType.DOCUMENT_NODE, OccurrenceIndicator.ZERO_OR_ONE);
  }

  @Override
  public XdmValue call(XdmValue[] arguments) throws SaxonApiException {
    StringBuilder sb = new StringBuilder();
    if (arguments != null && arguments.length > 0 && arguments[0] != null) {
      for (XdmItem item : arguments[0]) {
        sb.append(item.getStringValue());
      }
    }
    Node dom = AuthorDecoder.parseAsNode(sb.toString());

    if (dom == null) {
      return net.sf.saxon.s9api.XdmEmptySequence.getInstance();
    }
    XdmNode doc = processor.newDocumentBuilder().build(new DOMSource(dom));
    return doc;
  }
}
