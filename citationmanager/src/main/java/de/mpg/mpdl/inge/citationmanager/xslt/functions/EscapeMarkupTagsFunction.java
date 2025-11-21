package de.mpg.mpdl.inge.citationmanager.xslt.functions;

import de.mpg.mpdl.inge.citationmanager.utils.XsltHelper;
import net.sf.saxon.s9api.ExtensionFunction;
import net.sf.saxon.s9api.ItemType;
import net.sf.saxon.s9api.OccurrenceIndicator;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.SequenceType;
import net.sf.saxon.s9api.XdmAtomicValue;
import net.sf.saxon.s9api.XdmEmptySequence;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmValue;

import java.util.ArrayList;
import java.util.List;

/**
 * s9api extension function: jfunc:escapeMarkupTags(item()*) as xs:string* Namespace URI:
 * https://pubman.mpdl.mpg.de/xslt-helper-functions
 */
public final class EscapeMarkupTagsFunction implements ExtensionFunction {

  private static final QName NAME = new QName("jfunc", "https://pubman.mpdl.mpg.de/xslt-helper-functions", "escapeMarkupTags");

  @Override
  public QName getName() {
    return NAME;
  }

  @Override
  public SequenceType[] getArgumentTypes() {
    // Be tolerant: accept any sequence and convert each item to its string-value
    return new SequenceType[] {SequenceType.makeSequenceType(ItemType.ANY_ITEM, OccurrenceIndicator.ZERO_OR_MORE)};
  }

  @Override
  public SequenceType getResultType() {
    // Preserve prior behavior: may return zero or more strings
    return SequenceType.makeSequenceType(ItemType.STRING, OccurrenceIndicator.ZERO_OR_MORE);
  }

  @Override
  public XdmValue call(XdmValue[] arguments) throws SaxonApiException {
    XdmValue seq = (arguments != null && arguments.length > 0) ? arguments[0] : null;
    if (seq == null || seq.size() == 0) {
      // Do not return empty-sequence: many call sites expect xs:string
      return new XdmAtomicValue("");
    }

    // Convert each item to its string value (no concatenation) to mirror String[]-based helper
    List<String> inList = new ArrayList<>(seq.size());
    for (XdmItem item : seq) {
      inList.add(item.getStringValue());
    }

    String[] outArr = XsltHelper.escapeMarkupTags(inList.toArray(new String[0]));
    if (outArr == null || outArr.length == 0) {
      // Return single empty string to avoid XPTY0004 in contexts requiring xs:string
      return new XdmAtomicValue("");
    }

    List<XdmAtomicValue> out = new ArrayList<>(outArr.length);
    for (String s : outArr) {
      out.add(new XdmAtomicValue(s == null ? "" : s));
    }
    return XdmValue.makeSequence(out);
  }
}
