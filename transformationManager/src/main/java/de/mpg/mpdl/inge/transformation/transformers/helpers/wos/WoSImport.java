package de.mpg.mpdl.inge.transformation.transformers.helpers.wos;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.mpg.mpdl.inge.transformation.transformers.helpers.Pair;

/**
 * provides the import of a RIS file
 *
 * @author kurt (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class WoSImport {
  private static final Logger logger = LogManager.getLogger(WoSImport.class);

  private static final String URL = "/home/kurt/Dokumente/wok-isi-test.txt";

  public WoSImport() {}

  /**
   * reads the import file and transforms the items to XML
   *
   * @return xml
   */
  public String transformWoS2XML(String file) {
    String result = "";

    String[] itemList = getItemListFromString(file, "(\nER\n+EF\n)|(\nER\n)");
    List<List<Pair>> items = new ArrayList<>();
    if (null != itemList && 1 < itemList.length) { // transform items to XML

      for (String item : itemList) {
        List<Pair> itemPairs = getItemPairs(getItemFromString(item + "\n"));

        items.add(itemPairs);
      }
      result = transformItemListToXML(items);

    } else if (null != itemList && 1 == itemList.length) {
      List<Pair> item = getItemPairs(getItemFromString(itemList[0] + "\n"));
      result = transformItemToXML(item);
    }
    return result;
  }

  public String readFile() {
    String file = "";
    FileReader fileReader = null;
    BufferedReader input = null;

    try {
      fileReader = new FileReader(URL);
      input = new BufferedReader(fileReader);

      String str;
      while (null != (str = input.readLine())) {
        file = file + "\n" + str;
      }
    } catch (IOException e) {
      logger.error("An error occurred while reading WOS file.", e);
      throw new RuntimeException(e);
    } finally {
      if (null != input) {
        try {
          input.close();
        } catch (IOException e) {
          logger.error("An error occurred while reading WOS file.", e);
          throw new RuntimeException(e);
        } finally {
          if (null != fileReader) {
            try {
              fileReader.close();
            } catch (IOException e) {
              logger.error("An error occurred while reading WOS file.", e);
              throw new RuntimeException(e);
            }
          }
        }
      }
    }

    return file;
  }

  public List<String> getItemFromString(String itemStr) {
    Pattern pattern = Pattern.compile("((([A-Z]{1}[0-9]{1})|([A-Z]{2}))\\s(.*(\\r\\n|\\r|\\n))((\\s\\s\\s.*(\\r\\n|\\r|\\n))?)*)");
    // Pattern pattern =
    // Pattern.compile("(([A-Z]{1}[0-9]{1})|([A-Z]{2})) ((.*(\\r\\n|\\r|\\n))+?)");

    Matcher matcher = pattern.matcher(itemStr);
    List<String> lineStrArr = new ArrayList<>();
    while (matcher.find()) {
      lineStrArr.add(matcher.group());
    }

    return lineStrArr;
  }

  /**
   * identifies RIS items from input string and stores it in an String Array
   *
   * @param string
   * @return
   */
  public String[] getItemListFromString(String string, String pattern) {
    String[] strItemList = string.split(pattern);
    // strItemList[0] = "\n"+strItemList[0].split("(FN ISI Export Format)\\n(VR 1.0)\\n")[0]; // cut
    // file header

    return strItemList;
  }

  public List<Pair> getItemPairs(List<String> lines) {
    List<Pair> pairList = new ArrayList<>();

    if (null != lines) {
      for (String line : lines) {
        Pair pair = createWoSPairByString(line);
        pairList.add(pair);
      }
    }

    return pairList;
  }

  public Pair createWoSPairByString(String line) {
    String key = line.substring(0, 2);
    String value = line.substring(3);
    Pair pair = null;
    pair = new Pair(key.trim(), escape(value.trim()));

    return pair;
  }

  /**
   * creates a single item in xml
   *
   * @param item pair list
   * @return xml string of the whole item list
   */
  public String transformItemToXML(List<Pair> item) {
    if (null != item && !item.isEmpty()) {
      return createXMLElement("item", transformItemSubelementsToXML(item));
    }

    return "";
  }

  public String transformItemListToXML(List<List<Pair>> itemList) {
    String xml = "<item-list>";

    if (null != itemList && !itemList.isEmpty()) {
      for (List<Pair> item : itemList) {
        xml = xml + "\n" + transformItemToXML(item);
      }
    }

    xml = xml + "</item-list>";

    return xml;
  }

  /**
   * creates an xml string of the item pair list
   *
   * @param item pairs as list
   * @return xml String
   */
  public String transformItemSubelementsToXML(List<Pair> item) {
    String xml = "";
    if (null != item && !item.isEmpty()) {
      for (Pair pair : item) {
        xml = xml + createXMLElement(pair.getKey(), pair.getValue());
      }
    }

    return xml;
  }

  /**
   * creates a single element in xml
   *
   * @param tag - tag name of the element
   * @param value - value of the element
   * @return xml element as string
   */
  public String createXMLElement(String tag, String value) {
    if (null != tag && !tag.isEmpty()) {
      return "<" + tag + ">" + value + "</" + tag + ">";
    }

    return "";
  }

  /**
   * escapes special characters
   *
   * @param input string
   * @return string with escaped characters
   */
  public String escape(String input) {
    if (null != input) {
      input = input.replace("&", "&amp;");
      input = input.replace("<", "&lt;");
      input = input.replace(">", "&gt;");
      input = input.replace("\"", "&quot;");
    }

    return input;
  }
}
