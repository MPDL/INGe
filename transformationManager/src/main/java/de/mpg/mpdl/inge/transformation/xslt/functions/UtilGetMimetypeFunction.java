package de.mpg.mpdl.inge.transformation.xslt.functions;

import de.mpg.mpdl.inge.transformation.Util;
import net.sf.saxon.s9api.ExtensionFunction;
import net.sf.saxon.s9api.ItemType;
import net.sf.saxon.s9api.OccurrenceIndicator;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.SequenceType;
import net.sf.saxon.s9api.XdmAtomicValue;
import net.sf.saxon.s9api.XdmValue;

/**
 * s9api extension function: Util:getMimetype(xs:string?) as xs:string? Namespace URI:
 * java:de.mpg.mpdl.inge.transformation.Util
 */
public final class UtilGetMimetypeFunction implements ExtensionFunction {
  private static final QName NAME = new QName("Util", "https://pubman.mpdl.mpg.de/util-functions", "getMimetype");

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
    return SequenceType.makeSequenceType(ItemType.STRING, OccurrenceIndicator.ZERO_OR_ONE);
  }

  @Override
  public XdmValue call(XdmValue[] arguments) throws SaxonApiException {
    String filename = (arguments.length > 0 && arguments[0].size() > 0) ? arguments[0].itemAt(0).getStringValue() : null;
    String mt = Util.getMimetype(filename == null ? "" : filename);
    return mt == null ? net.sf.saxon.s9api.XdmEmptySequence.getInstance() : new XdmAtomicValue(mt);
  }
}
