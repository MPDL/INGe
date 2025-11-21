package de.mpg.mpdl.inge.transformation;

import javax.xml.transform.TransformerFactory;

import de.mpg.mpdl.inge.citationmanager.xslt.functions.ConvertSnippetToHtmlFunction;
import de.mpg.mpdl.inge.citationmanager.xslt.functions.EscapeMarkupTagsFunction;
import de.mpg.mpdl.inge.citationmanager.xslt.functions.GetCitationStyleForJournalFunction;
import de.mpg.mpdl.inge.citationmanager.xslt.functions.IsCjkFunction;
import net.sf.saxon.Configuration;
import net.sf.saxon.TransformerFactoryImpl;
import net.sf.saxon.s9api.Processor;

/**
 * Provides a Saxon-backed TransformerFactory with integrated extension functions registered under
 * the namespace https://pubman.mpdl.mpg.de/xslt-helper-functions.
 *
 * This mirrors the setup used in citationmanager so that XSLTs in this module can resolve jfunc:*
 * calls.
 */
public final class SaxonFactoryProvider {
  private SaxonFactoryProvider() {}

  public static TransformerFactory createWithExtensions() {
    Processor proc = new Processor(false); // Saxon HE

    // Register citationmanager functions
    proc.registerExtensionFunction(new IsCjkFunction());
    proc.registerExtensionFunction(new EscapeMarkupTagsFunction());
    proc.registerExtensionFunction(new GetCitationStyleForJournalFunction());
    proc.registerExtensionFunction(new ConvertSnippetToHtmlFunction());

    // Register transformationManager legacy java: wrappers to avoid editing XSLTs
    proc.registerExtensionFunction(new de.mpg.mpdl.inge.transformation.xslt.functions.UtilQueryConeFunction(proc));
    proc.registerExtensionFunction(new de.mpg.mpdl.inge.transformation.xslt.functions.UtilQueryConeExactFunction(proc));
    proc.registerExtensionFunction(new de.mpg.mpdl.inge.transformation.xslt.functions.UtilGetMimetypeFunction());
    proc.registerExtensionFunction(new de.mpg.mpdl.inge.transformation.xslt.functions.UtilGetSizeFunction(proc));
    proc.registerExtensionFunction(new de.mpg.mpdl.inge.transformation.xslt.functions.UtilStripHtmlFunction());
    proc.registerExtensionFunction(new de.mpg.mpdl.inge.transformation.xslt.functions.AuthorDecoderParseAsNodeFunction(proc));
    proc.registerExtensionFunction(new de.mpg.mpdl.inge.transformation.xslt.functions.BibtexTexStringFunction());

    Configuration config = proc.getUnderlyingConfiguration();
    return new TransformerFactoryImpl(config);
  }
}
