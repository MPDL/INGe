package de.mpg.mpdl.inge.citationmanager.xslt.functions;

import de.mpg.mpdl.inge.citationmanager.utils.XsltHelper;
import net.sf.saxon.s9api.ExtensionFunction;
import net.sf.saxon.s9api.ItemType;
import net.sf.saxon.s9api.OccurrenceIndicator;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.SequenceType;
import net.sf.saxon.s9api.XdmAtomicValue;
import net.sf.saxon.s9api.XdmValue;

/**
 * s9api extension function: jfunc:isCJK(string) as xs:boolean Namespace URI:
 * https://pubman.mpdl.mpg.de/xslt-helper-functions
 */
public final class IsCjkFunction implements ExtensionFunction {

  private static final QName NAME = new QName("jfunc", "https://pubman.mpdl.mpg.de/xslt-helper-functions", "isCJK");

  @Override
  public QName getName() {
    return NAME;
  }

  @Override
  public SequenceType[] getArgumentTypes() {
    return new SequenceType[] {SequenceType.makeSequenceType(ItemType.STRING, OccurrenceIndicator.ONE)};
  }

  @Override
  public SequenceType getResultType() {
    return SequenceType.makeSequenceType(ItemType.BOOLEAN, OccurrenceIndicator.ONE);
  }

  @Override
  public XdmValue call(XdmValue[] arguments) throws SaxonApiException {
    String s = arguments[0].size() == 0 ? "" : arguments[0].itemAt(0).getStringValue();
    boolean result = XsltHelper.isCJK(s);
    return new XdmAtomicValue(result);
  }
}
