package de.mpg.mpdl.inge.transformation.xslt.functions;

import de.mpg.mpdl.inge.transformation.transformers.helpers.bibtex.BibtexExport;
import net.sf.saxon.s9api.ExtensionFunction;
import net.sf.saxon.s9api.ItemType;
import net.sf.saxon.s9api.OccurrenceIndicator;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.SequenceType;
import net.sf.saxon.s9api.XdmAtomicValue;
import net.sf.saxon.s9api.XdmValue;

/**
 * s9api extension function: jfunc:texString(xs:string?) as xs:string Preserves legacy java:
 * namespace used in XSLTs for BibtexExport#texString Namespace URI:
 * java:de.mpg.mpdl.inge.transformation.transformers.helpers.bibtex.BibtexExport
 */
public final class BibtexTexStringFunction implements ExtensionFunction {
  private static final QName NAME = new QName("jfunc", "https://pubman.mpdl.mpg.de/bibtex-functions", "texString");

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
    return SequenceType.makeSequenceType(ItemType.STRING, OccurrenceIndicator.ONE);
  }

  @Override
  public XdmValue call(XdmValue[] arguments) throws SaxonApiException {
    String input = (arguments.length > 0 && arguments[0].size() > 0) ? arguments[0].itemAt(0).getStringValue() : null;
    String out = BibtexExport.texString(input);
    return new XdmAtomicValue(out == null ? "" : out);
  }
}
