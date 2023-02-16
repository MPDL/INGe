package de.mpg.mpdl.inge.citationmanager.utils;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.Test;

import de.mpg.mpdl.inge.citationmanager.CitationStyleExecuterService;
import de.mpg.mpdl.inge.citationmanager.CitationStyleManagerTest;

public class XmlHelperTest {

  private static final Logger logger = Logger.getLogger(CitationStyleManagerTest.class);

  private final Set<String> expectedStyles =
      new HashSet<String>(Arrays.asList("APA", "APA6", "APA(CJK)", "AJP", "JUS", "CSL", "JUS_Report", "Default"));


  /**
   * Test list of styles
   * 
   * @throws Exception Any exception.
   */
  @Test
  public final void testGetStyles() throws Exception {
    logger.info("List of citation styles: ");

    Set<String> stylenames = new HashSet<String>();
    for (String s : CitationStyleExecuterService.getStyles()) {
      logger.info("Citation Style: " + s);
      stylenames.add(s);
    }
    assertTrue(stylenames.size() == 8);
    assertTrue(stylenames.equals(expectedStyles));
  }

  /**
   * Test list of styles
   * 
   * @throws Exception Any exception.
   */
  @Test
  public final void testExplainStuff() throws Exception {
    String explain = CitationStyleExecuterService.explainStyles();
    assertTrue("Empty explain xml", Utils.checkVal(explain));
    logger.info("Explain file: \n" + explain);

    logger.info("List of citation styles with output formats: ");
    for (String s : CitationStyleExecuterService.getStyles()) {
      logger.info("Citation Style: " + s);
      for (String of : CitationStyleExecuterService.getOutputFormats(s)) {
        logger.info("--Output Format: " + of);
        logger.info("   --Mime Type: " + CitationStyleExecuterService.getMimeType(s, of));
      }
    }
  }

  @Test
  public final void testGetCitationStylesHash() throws Exception {

    Map<String, HashMap<String, String[]>> map = XmlHelper.getCitationStylesHash();
    assertTrue(map.keySet().equals(expectedStyles));

    for (String style : map.keySet()) {
      HashMap<String, String[]> outputFormats = map.get(style);

      switch (style) {
        // the outputFormat contains the same data, irrespective of the style!

        case "APA":
          for (String s : outputFormats.keySet()) {
            doAssert(outputFormats, s);
          }
          break;
        case "APA6":
          for (String s : outputFormats.keySet()) {
            doAssert(outputFormats, s);
          }
          break;
        case "APA(CJK)":
          for (String s : outputFormats.keySet()) {
            doAssert(outputFormats, s);
          }
          break;
        case "AJP":
          for (String s : outputFormats.keySet()) {
            doAssert(outputFormats, s);
          }
          break;
        case "JUS":
          for (String s : outputFormats.keySet()) {
            doAssert(outputFormats, s);
          }
          break;
        case "CSL":
          for (String s : outputFormats.keySet()) {
            doAssert(outputFormats, s);
          }
          break;
        case "JUS_Report":
          for (String s : outputFormats.keySet()) {
            doAssert(outputFormats, s);
          }
          break;
        case "Default":
          for (String s : outputFormats.keySet()) {
            doAssert(outputFormats, s);
          }
          break;


      }

    }
  }

  private void doAssert(HashMap<String, String[]> outputFormats, String s) {
    String[] mimeTypes = outputFormats.get(s);
    switch (s) {
      case "snippet":
        assertTrue(Arrays.asList(mimeTypes).equals(Arrays.asList(new String[] {"application/xml", "xml"})));
        break;
      case "txt":
        assertTrue(Arrays.asList(mimeTypes).equals(Arrays.asList(new String[] {"text/plain", "txt"})));
        break;
      case "pdf":
        assertTrue(Arrays.asList(mimeTypes).equals(Arrays.asList(new String[] {"application/pdf", "pdf"})));
        break;
      case "html_plain":
        assertTrue(Arrays.asList(mimeTypes).equals(Arrays.asList(new String[] {"text/html", "html"})));
        break;
      case "html_linked":
        assertTrue(Arrays.asList(mimeTypes).equals(Arrays.asList(new String[] {"text/html", "html"})));
        break;
      case "docx":
        assertTrue(Arrays.asList(mimeTypes)
            .equals(Arrays.asList(new String[] {"application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx"})));
        break;
      case "escidoc_snippet":
        assertTrue(Arrays.asList(mimeTypes).equals(Arrays.asList(new String[] {"application/xml", "xml"})));
        break;
    }
  }

  @Test
  public void testGetOutputFormatsHash() {
    XmlHelper.getOutputFormatsHash();
  }



}
