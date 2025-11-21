package de.mpg.mpdl.inge.transformation.xslt.functions;

import de.mpg.mpdl.inge.transformation.Util;
import net.sf.saxon.s9api.ExtensionFunction;
import net.sf.saxon.s9api.ItemType;
import net.sf.saxon.s9api.OccurrenceIndicator;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.SequenceType;
import net.sf.saxon.s9api.XdmAtomicValue;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmValue;

/**
 * s9api extension function: Util:stripHtml(item()*) as xs:string Namespace URI:
 * java:de.mpg.mpdl.inge.transformation.Util
 */
public final class UtilStripHtmlFunction implements ExtensionFunction {
  private static final QName NAME = new QName("Util", "https://pubman.mpdl.mpg.de/util-functions", "stripHtml");

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
    return SequenceType.makeSequenceType(ItemType.STRING, OccurrenceIndicator.ONE);
  }

  @Override
  public XdmValue call(XdmValue[] arguments) throws SaxonApiException {
    StringBuilder sb = new StringBuilder();
    if (arguments != null && arguments.length > 0 && arguments[0] != null) {
      for (XdmItem item : arguments[0]) {
        sb.append(item.getStringValue());
      }
    }
    String out = Util.stripHtml(sb.toString());
    return new XdmAtomicValue(out == null ? "" : out);
  }
}
