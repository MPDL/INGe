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
 * s9api extension function: jfunc:getCitationStyleForJournal(string, string) as xs:string Namespace
 * URI: https://pubman.mpdl.mpg.de/xslt-helper-functions
 */
public final class GetCitationStyleForJournalFunction implements ExtensionFunction {

  private static final QName NAME = new QName("jfunc", "https://pubman.mpdl.mpg.de/xslt-helper-functions", "getCitationStyleForJournal");

  @Override
  public QName getName() {
    return NAME;
  }

  @Override
  public SequenceType[] getArgumentTypes() {
    return new SequenceType[] {SequenceType.makeSequenceType(ItemType.STRING, OccurrenceIndicator.ONE),
        SequenceType.makeSequenceType(ItemType.STRING, OccurrenceIndicator.ONE)};
  }

  @Override
  public SequenceType getResultType() {
    return SequenceType.makeSequenceType(ItemType.STRING, OccurrenceIndicator.ONE);
  }

  @Override
  public XdmValue call(XdmValue[] arguments) throws SaxonApiException {
    String idType = arguments[0].size() == 0 ? "" : arguments[0].itemAt(0).getStringValue();
    String idValue = arguments[1].size() == 0 ? "" : arguments[1].itemAt(0).getStringValue();
    try {
      String result = XsltHelper.getCitationStyleForJournal(idType, idValue);
      return new XdmAtomicValue(result == null ? "" : result);
    } catch (Exception e) {
      throw new SaxonApiException("getCitationStyleForJournal failed", e);
    }
  }
}
