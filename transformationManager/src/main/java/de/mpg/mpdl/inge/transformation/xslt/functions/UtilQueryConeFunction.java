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
 * s9api extension function: Util:queryCone(xs:string?, xs:string?) as node()* Namespace URI:
 * https://pubman.mpdl.mpg.de/util-functions
 */
public final class UtilQueryConeFunction implements ExtensionFunction {
  private static final QName NAME = new QName("Util", "https://pubman.mpdl.mpg.de/util-functions", "queryCone");
  private final Processor processor;

  public UtilQueryConeFunction(Processor processor) {
    this.processor = processor;
  }

  @Override
  public QName getName() {
    return NAME;
  }

  @Override
  public SequenceType[] getArgumentTypes() {
    return new SequenceType[] {SequenceType.makeSequenceType(ItemType.STRING, OccurrenceIndicator.ZERO_OR_ONE),
        SequenceType.makeSequenceType(ItemType.STRING, OccurrenceIndicator.ZERO_OR_ONE)};
  }

  @Override
  public SequenceType getResultType() {
    return SequenceType.makeSequenceType(ItemType.DOCUMENT_NODE, OccurrenceIndicator.ZERO_OR_ONE);
  }

  @Override
  public XdmValue call(XdmValue[] arguments) throws SaxonApiException {
    String model = (arguments.length > 0 && arguments[0].size() > 0) ? arguments[0].itemAt(0).getStringValue() : "";
    String query = (arguments.length > 1 && arguments[1].size() > 0) ? arguments[1].itemAt(0).getStringValue() : "";

    Node dom = Util.queryCone(model, query);
    if (dom == null) {
      return net.sf.saxon.s9api.XdmEmptySequence.getInstance();
    }
    // Build an XdmNode using the same Processor/Configuration as the running transformation
    XdmNode doc = processor.newDocumentBuilder().build(new DOMSource(dom));
    return doc;
  }
}
