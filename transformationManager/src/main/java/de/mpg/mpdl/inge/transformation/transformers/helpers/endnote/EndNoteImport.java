package de.mpg.mpdl.inge.transformation.transformers.helpers.endnote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.mpg.mpdl.inge.transformation.transformers.helpers.Pair;

/**
 * provides the import of a EndNote file
 *
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class EndNoteImport {
  /**
   * reads the import file and transforms the items to XML
   *
   * @return xml
   */
  public String transformEndNote2XML(String file) {
    String result = "";

    List<String> itemList = splitItems(file);
    if (itemList != null && itemList.size() > 1) { // transform items to XML
      List<List<Pair>> itemPairsList = new ArrayList<List<Pair>>();
      for (String s : itemList) {
        List<Pair> itemPairs = getItemPairs(splitItemElements(s));
        itemPairsList.add(itemPairs);
      }
      result = transformItemPairsListToXML(itemPairsList);
    } else if (itemList != null && itemList.size() == 1) {
      List<Pair> itemPairs = getItemPairs(splitItemElements(itemList.get(0)));
      result = transformItemToXML(itemPairs);
    }

    return result;
  }


  /**
   * Splits EndNote items and puts them into List<String>
   *
   * @param itemsStr item list string
   * @return
   */
  public List<String> splitItems(String itemsStr) {
    List<String> l = new ArrayList<String>();
    String buff;
    boolean firstItem = true;
    StringBuffer sb = null;
    int counter = 0;

    // replace first empty lines and BOM
    itemsStr = Pattern.compile("^.*?%", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(itemsStr).replaceFirst("%");

    BufferedReader reader = new BufferedReader(new StringReader(itemsStr));

    try {
      while ((buff = reader.readLine()) != null) {
        if (!checkVal(buff)) {
          counter++;
        } else {
          // first item handling
          if (firstItem) {
            firstItem = false;
            sb = new StringBuffer();
          }
          // new item
          else if (counter >= 1 && buff.startsWith("%0")) {
            l.add(sb.toString().trim());
            counter = 0;
            sb = new StringBuffer();
          }
          sb.append(buff).append("\n");
        }

      }
      // add last item
      if (sb != null) {
        l.add(sb.toString().trim());
      }

      reader.close();

    } catch (IOException e) {
      throw new RuntimeException("error by reading of items string", e);
    }

    return l;
  }

  /**
   * Splits EndNote fields of an item and puts them into List<String>
   *
   * @param itemStr - item string
   * @return
   */
  public List<String> splitItemElements(String itemStr) {

    List<String> l = new ArrayList<String>();
    String pattern = "(\\r\\n|\\n|\\r)%";
    Pattern p = Pattern.compile(pattern);
    for (String s : p.split("\n" + itemStr))
      if (checkVal(s)) {
        l.add("%" + s);
      }

    return l;

  }

  /**
   * get item pairs from item string and pack them into the <code>List</code>
   *
   * @param string - EndNote item as string
   * @return String list with item key-value pairs
   */
  public List<Pair> getItemPairs(List<String> lines) {

    List<Pair> pairList = new ArrayList<Pair>();
    if (lines != null) {
      for (String line : lines) {
        Pair p = createEndNotePairByString(line);
        if (p != null)
          pairList.add(p);
      }
    }
    return pairList;
  }

  /**
   * get a EndNote <code>Pair</code> from line string
   *
   * @param string - EndNote line as string
   * @return Pair - key-value pair created by string line
   */
  public Pair createEndNotePairByString(String line) {
    Pattern p = Pattern.compile("^(%\\S)\\s+(.*)$", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
    Matcher m = p.matcher(line);
    if (m.find()) {
      return new Pair(m.group(1), m.group(2));
    }
    return null;
  }

  /**
   * creates a single item in xml
   *
   * @param item pair list
   * @return xml string of the whole item list
   */
  public String transformItemToXML(List<Pair> item) {
    String xml = "";
    if (item != null && !item.isEmpty()) {
      xml = createXMLElement("item", transformItemSubelementsToXML(item));
    }
    return xml;
  }

  /**
   * creates the complete item list in xml
   *
   * @param item pair list
   * @return xml string of the whole item list
   */
  public String transformItemPairsListToXML(List<List<Pair>> itemList) {
    String xml = "";
    if (itemList != null && !itemList.isEmpty())
      for (List<Pair> lp : itemList)
        xml += transformItemToXML(lp);
    return createXMLElement("item-list", xml);
  }

  /**
   * creates an xml string of the item pair list
   *
   * @param item pairs as list
   * @return xml String
   */
  public String transformItemSubelementsToXML(List<Pair> item) {
    String xml = "";
    if (item != null && !item.isEmpty()) {
      for (Pair p : item)
        xml += createXMLElement(p.getXmlTag(), escape(p.getValue()));
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
    String element = "";
    if (checkVal(tag)) {
      element = "\n<" + tag + ">" + value + "</" + tag + ">";
    }
    return element;
  }

  /**
   * escapes special characters
   *
   * @param input string
   * @return string with escaped characters
   */
  public String escape(String input) {
    if (input != null) {
      input = input.replace("&", "&amp;");
      input = input.replace("<", "&lt;");
      input = input.replace("\"", "&quot;");
    }
    return input;
  }

  public static boolean checkVal(String val) {
    return (val != null && !val.trim().isEmpty());
  }

  public static boolean checkLen(String val) {
    return (val != null && !val.isEmpty());
  }

}
