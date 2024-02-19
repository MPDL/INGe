package de.mpg.mpdl.inge.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CSVUtils {

  private static final char DEFAULT_SEPARATOR = ';';
  private static final char DEFAULT_QUOTE = '"';

  private CSVUtils() {}

  public static void main(String[] args) throws Exception {

    String csvFile = "/Users/mkyong/csv/country2.csv";

    Scanner scanner = new Scanner(new File(csvFile));
    while (scanner.hasNext()) {
      List<String> line = parseLine(scanner.nextLine());
      //      System.out.println("Country [id= " + line.get(0) + ", code= " + line.get(1) + " , name=" + line.get(2) + "]");
    }
    scanner.close();

  }

  public static List<String> parseLine(String cvsLine) {
    return parseLine(cvsLine, DEFAULT_SEPARATOR, DEFAULT_QUOTE);
  }

  public static List<String> parseLine(String cvsLine, char separators) {
    return parseLine(cvsLine, separators, DEFAULT_QUOTE);
  }

  public static List<String> parseLine(String cvsLine, char separators, char customQuote) {

    List<String> result = new ArrayList<>();

    //if empty, return!
    if (null == cvsLine || cvsLine.isEmpty()) {
      return result;
    }

    if (' ' == customQuote) {
      customQuote = DEFAULT_QUOTE;
    }

    if (' ' == separators) {
      separators = DEFAULT_SEPARATOR;
    }

    StringBuilder curVal = new StringBuilder();
    boolean inQuotes = false;
    boolean startCollectChar = false;
    boolean doubleQuotesInColumn = false;

    char[] chars = cvsLine.toCharArray();

    for (char ch : chars) {

      if (inQuotes) {
        startCollectChar = true;
        if (ch == customQuote) {
          inQuotes = false;
          doubleQuotesInColumn = false;
        } else {

          //Fixed : allow "" in custom quote enclosed
          if ('\"' == ch) {
            if (!doubleQuotesInColumn) {
              curVal.append(ch);
              doubleQuotesInColumn = true;
            }
          } else {
            curVal.append(ch);
          }

        }
      } else {
        if (ch == customQuote) {

          inQuotes = true;

          //Fixed : allow "" in empty quote enclosed
          if ('"' != chars[0] && '\"' == customQuote) {
            curVal.append('"');
          }

          //double quotes in column will hit this!
          if (startCollectChar) {
            curVal.append('"');
          }

        } else if (ch == separators) {

          result.add(curVal.toString());

          curVal = new StringBuilder();
          startCollectChar = false;

        } else if ('\r' == ch) {
          //ignore LF characters
        } else if ('\n' == ch) {
          //the end, break!
          break;
        } else {
          curVal.append(ch);
        }
      }

    }

    result.add(curVal.toString());

    return result;
  }

}
