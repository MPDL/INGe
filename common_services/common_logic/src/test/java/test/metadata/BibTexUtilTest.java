package test.metadata;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Test;

import de.mpg.escidoc.services.common.util.BibTexUtil;

public class BibTexUtilTest {

    private static Logger logger = Logger.getLogger(BibTexUtilTest.class);
    
    @Test
    public void testbibtexDecode()
    {
        Map<String, String> examples = new HashMap<String, String>();
        examples.put("{languages; theory}", "languages; theory");
        examples.put("{{{{Test} {1}}}}", "Test 1");
        examples.put("\\\"A{\\\"U}\\\"{O} \\\"a{\\\"o}\\\"{u} {\\AE}\\'e{\\`{\\i}}", "ÄÜÖ äöü Æéì");
        examples.put("e = mc^{2}", "e = mc²");
        examples.put("#A\\#B\\#C#", "A#B#C");

        for (String example : examples.keySet()) {
            String result = BibTexUtil.bibtexDecode(example);
            logger.debug("Assert: '" + example + "' --> '" + result + "' = '" + examples.get(example) + "'");
            assertEquals(examples.get(example), result);
        }
    }
    
    @Test
    public void testParseMonth()
    {
        Map<String, String> examples = new HashMap<String, String>();
        examples.put("0", "01");
        examples.put("04", "05");
        examples.put("Jan", "01");
        examples.put("Aug", "08");
        examples.put("September", "09");
        examples.put("December", "12");

        for (String example : examples.keySet()) {
            String result = BibTexUtil.parseMonth(example);
            logger.debug("Assert: '" + example + "' --> '" + result + "' = '" + examples.get(example) + "'");
            assertEquals(examples.get(example), result);
        }
    }
}
