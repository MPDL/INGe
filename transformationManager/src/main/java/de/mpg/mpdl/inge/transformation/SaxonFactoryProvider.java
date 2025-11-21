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

  private static volatile TransformerFactory CACHED_TF;

  /**
   * Returns a Saxon-backed TransformerFactory with all extension functions registered.
   * Uses a lazily-initialized, thread-safe singleton to avoid repeated bootstrapping.
   */
  public static TransformerFactory createWithExtensions() {
    TransformerFactory tf = CACHED_TF;
    if (tf != null) {
      return tf;
    }
    synchronized (SaxonFactoryProvider.class) {
      if (CACHED_TF == null) {
        Processor proc = new Processor(false); // Saxon HE

        // Register citationmanager functions
        proc.registerExtensionFunction(new IsCjkFunction());
        proc.registerExtensionFunction(new EscapeMarkupTagsFunction());
        proc.registerExtensionFunction(new GetCitationStyleForJournalFunction());
        proc.registerExtensionFunction(new ConvertSnippetToHtmlFunction());

        // Register transformationManager functions
        proc.registerExtensionFunction(new de.mpg.mpdl.inge.transformation.xslt.functions.UtilQueryConeFunction(proc));
        proc.registerExtensionFunction(new de.mpg.mpdl.inge.transformation.xslt.functions.UtilQueryConeExactFunction(proc));
        proc.registerExtensionFunction(new de.mpg.mpdl.inge.transformation.xslt.functions.UtilGetMimetypeFunction());
        proc.registerExtensionFunction(new de.mpg.mpdl.inge.transformation.xslt.functions.UtilGetSizeFunction(proc));
        proc.registerExtensionFunction(new de.mpg.mpdl.inge.transformation.xslt.functions.UtilStripHtmlFunction());
        proc.registerExtensionFunction(new de.mpg.mpdl.inge.transformation.xslt.functions.AuthorDecoderParseAsNodeFunction(proc));
        proc.registerExtensionFunction(new de.mpg.mpdl.inge.transformation.xslt.functions.BibtexTexStringFunction());

        Configuration config = proc.getUnderlyingConfiguration();
        CACHED_TF = new TransformerFactoryImpl(config);
      }
      return CACHED_TF;
    }
  }

  /**
   * For tests only: clears the cached factory so a fresh Processor/Configuration can be created.
   */
  static void resetForTests() {
    synchronized (SaxonFactoryProvider.class) {
      CACHED_TF = null;
    }
  }
}
