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
 * s9api extension function: Util:queryConeExact(xs:string?, xs:string?, xs:string?) as node()* We
 * preserve the original java: namespace URI to avoid editing many XSLTs now. Namespace URI:
 * java:de.mpg.mpdl.inge.transformation.Util
 */
public final class UtilQueryConeExactFunction implements ExtensionFunction {
  private static final QName NAME = new QName("Util", "https://pubman.mpdl.mpg.de/util-functions", "queryConeExact");
  private final Processor processor;

  public UtilQueryConeExactFunction(Processor processor) {
    this.processor = processor;
  }

  @Override
  public QName getName() {
    return NAME;
  }

  @Override
  public SequenceType[] getArgumentTypes() {
    return new SequenceType[] {SequenceType.makeSequenceType(ItemType.STRING, OccurrenceIndicator.ZERO_OR_ONE),
        SequenceType.makeSequenceType(ItemType.STRING, OccurrenceIndicator.ZERO_OR_ONE),
        SequenceType.makeSequenceType(ItemType.STRING, OccurrenceIndicator.ZERO_OR_ONE)};
  }

  @Override
  public SequenceType getResultType() {
    return SequenceType.makeSequenceType(ItemType.DOCUMENT_NODE, OccurrenceIndicator.ZERO_OR_ONE);
  }

  @Override
  public XdmValue call(XdmValue[] arguments) throws SaxonApiException {
    String model = (arguments.length > 0 && arguments[0].size() > 0) ? arguments[0].itemAt(0).getStringValue() : "";
    String name = (arguments.length > 1 && arguments[1].size() > 0) ? arguments[1].itemAt(0).getStringValue() : "";
    String ou = (arguments.length > 2 && arguments[2].size() > 0) ? arguments[2].itemAt(0).getStringValue() : "";

    Node dom = Util.queryConeExact(model, name, ou);
    if (dom == null) {
      return net.sf.saxon.s9api.XdmEmptySequence.getInstance();
    }
    XdmNode doc = processor.newDocumentBuilder().build(new DOMSource(dom));
    return doc;
  }
}
