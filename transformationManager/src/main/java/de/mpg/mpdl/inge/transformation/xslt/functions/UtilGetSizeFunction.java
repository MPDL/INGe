package de.mpg.mpdl.inge.transformation.xslt.functions;

import de.mpg.mpdl.inge.transformation.Util;
import net.sf.saxon.s9api.ExtensionFunction;
import net.sf.saxon.s9api.ItemType;
import net.sf.saxon.s9api.OccurrenceIndicator;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.SequenceType;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;
import net.sf.saxon.s9api.Processor;

import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Node;

/**
 * s9api extension function: Util:getSize(xs:string?) as document-node()? Namespace URI:
 * java:de.mpg.mpdl.inge.transformation.Util
 */
public final class UtilGetSizeFunction implements ExtensionFunction {
  private static final QName NAME = new QName("Util", "https://pubman.mpdl.mpg.de/util-functions", "getSize");
  private final Processor processor;

  public UtilGetSizeFunction(Processor processor) {
    this.processor = processor;
  }

  @Override
  public QName getName() {
    return NAME;
  }

  @Override
  public SequenceType[] getArgumentTypes() {
    return new SequenceType[] {SequenceType.makeSequenceType(ItemType.STRING, OccurrenceIndicator.ZERO_OR_ONE)};
  }

  @Override
  public SequenceType getResultType() {
    return SequenceType.makeSequenceType(ItemType.DOCUMENT_NODE, OccurrenceIndicator.ZERO_OR_ONE);
  }

  @Override
  public XdmValue call(XdmValue[] arguments) throws SaxonApiException {
    String url = (arguments.length > 0 && arguments[0].size() > 0) ? arguments[0].itemAt(0).getStringValue() : null;
    if (url == null || url.isEmpty()) {
      return net.sf.saxon.s9api.XdmEmptySequence.getInstance();
    }
    Node dom = Util.getSize(url);
    if (dom == null) {
      return net.sf.saxon.s9api.XdmEmptySequence.getInstance();
    }
    XdmNode doc = processor.newDocumentBuilder().build(new DOMSource(dom));
    return doc;
  }
}
