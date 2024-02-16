/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with
 * the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or
 * http://www.escidoc.org/license. See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License
 * file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with
 * the fields enclosed by brackets "[]" replaced with your own identifying information: Portions
 * Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2010 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */

package de.mpg.mpdl.inge.transformation.util.creators;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.util.ResourceUtil;

/**
 * Abstract superclass for author string decoding formats. Provides basic functionality.
 *
 * @author franke (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 3442 $ $LastChangedDate: 2010-08-03 09:44:01 +0200 (Di, 03 Aug 2010) $
 */
public abstract class AuthorFormat implements Comparable<AuthorFormat> {
  private static final Logger logger = Logger.getLogger(AuthorFormat.class);

  protected static final String SYLLABLE = "([A-ZÄÖÜÁÂÀÅÆÇÉÊÈËÍÎÌÏÐÑÓÔÒÕØÚÛÙÝ][a-zäöüßáâàãåæéêèëíîìïñóôòõúûùýÿçøð]+)";
  // protected static final String LOOSE_SYLLABLE = "([\\w\\.'-]+)";
  protected static final String LOOSE_SYLLABLE = "([\\p{L}\\d\\.'\\-\\*\\(\\)\\[\\]\\{\\}@!\\$§%&/=\\+\\?¤]+)";
  protected static final String WORD = "((O'|D')?" + SYLLABLE + "(" + SYLLABLE + ")*)";
  protected static final String PREFIX = "(von|vom|von +und +zu|zu|de +la|dela|la|de|da|du|of|van|van +der|van +den|den|der|und|le|Le|La)";
  protected static final String NAME = "(" + PREFIX + "? *" + WORD + "( *- *" + WORD + ")*)";
  protected static final String INITIAL = "(([A-Z]|Ch|Sch|Th|Chr)\\.?)";
  protected static final String INITIALS = "(" + INITIAL + "( *-? *" + INITIAL + ")*)";
  protected static final String TITLE = "(Dr\\.|Doktor|Doctor|Prof\\.|Professor|Kardinal|Geheimrat|Bischof|)";
  protected static final String SUFFIX = "(,*)? (sen.?|Sen.?|jr.?|Jr.?)";
  protected static final String MIDDLEFIX = "(y|dela|de la)";
  protected static final String GIVEN_NAME_FORMAT = "(" + NAME + "( *(" + NAME + "|" + INITIALS + "))*)";
  protected static final String GIVEN_NAME_FORMAT_MIXED =
      "((" + NAME + "|" + INITIAL + ")( *( *" + NAME + "|" + INITIAL + "|(sen\\.?|Sen\\.?|jr\\.?|Jr\\.?)" + "))*)";

  protected static final String FORBIDDEN_CHARACTERS =
      "(\\d|\\*|\\(|\\)|\\[|\\]|\\{|\\}|!|\\$|§|%|&|/|=|\\+|\\?|¤|†|‡||email|written|et al)";
  protected static final String IGNORE_CHARACTERS = ".*(@).*";


  protected Set<String> givenNames = null;
  protected Set<String> surnames = null;

  /**
   * This method is called to execute the parser.
   *
   * @param authorString
   * @return A {@link List} of {@link Author} beans.
   * @throws Exception Any exception.
   */
  public abstract List<Author> getAuthors(String authorString) throws Exception;

  /**
   * Returns the regular expression to identify a string this Class can probably handle.
   *
   * @return A string containing a regular expression.
   */
  public abstract String getPattern();

  public abstract int getSignificance();

  /**
   * Returns the name of this parser.
   *
   * @return The name
   */
  public abstract String getName();

  /**
   * Returns a description what kind of format this parser analyzes.
   *
   * @return The description.
   */
  public abstract String getDescription();

  /**
   * Should be implemented in case the format the parser analyzes is very special or is covered by
   * other Parsers, too.
   *
   * @return A warning message why the result of this parser might be problematic.
   */
  public abstract String getWarning();

  /**
   * {@inheritDoc}
   */
  public int compareTo(AuthorFormat o) {
    return getSignificance() - o.getSignificance();
  }

  /**
   * Checks a controlled vocabulary, if the given string is a given name.
   *
   * @param name The given string.
   * @return <code>true</code> if the given string is contained in the list of given names.
   * @throws Exception Any {@link Exception}.
   */
  public boolean isGivenName(String name) throws Exception {
    boolean result = getGivenNames().contains(name);
    return result;
  }

  /**
   * Checks a controlled vocabulary, if the given string is a surname.
   *
   * @param name The given string.
   * @return <code>true</code> if the given string is contained in the list of surnames.
   * @throws Exception Any {@link Exception}.
   */
  public boolean isSurname(String name) throws Exception {
    boolean result = getSurnames().contains(name);
    return result;
  }

  /**
   * Returns the list of given names. If the list is not initialized yet, this is done.
   *
   * @return The list of given names.
   * @throws Exception Any {@link Exception}.
   */
  public Set<String> getGivenNames() throws Exception {
    if (givenNames == null) {
      givenNames = getNamesFromFile("metadata/names/givennames.txt");
    }
    return givenNames;
  }

  public void setGivenNames(Set<String> givenNames) {
    this.givenNames = givenNames;
  }

  /**
   * Returns the list of surnames. If the list is not initialized yet, this is done.
   *
   * @return The list of surnames.
   * @throws Exception Any {@link Exception}.
   */
  public Set<String> getSurnames() throws Exception {
    if (surnames == null) {
      surnames = getNamesFromFile("metadata/names/surnames.txt");
    }
    return surnames;
  }

  public void setSurnames(Set<String> surnames) {
    this.surnames = surnames;
  }

  /**
   * Reads words from a file into a {@link Set}.
   *
   * @param filename The name of the file relative or absolute. The file should contain lines each
   *        with one line.
   * @return A {@link Set} containing the words in a file.
   * @throws Exception Any {@link Exception}.
   */
  public static Set<String> getNamesFromFile(String filename) throws Exception {
    InputStream file = ResourceUtil.getResourceAsStream(filename, AuthorFormat.class.getClassLoader());
    BufferedReader br = new BufferedReader(new InputStreamReader(file));
    String name = "";
    Set<String> result = new HashSet<>();
    while ((name = br.readLine()) != null) {
      result.add(name);
    }
    return result;
  }

  /**
   * Parses authors in the following formats: "Peter Müller" or "Linda McCartney" or "John Gabriel
   * Smith-Wesson" or "Karl H. Meiser"
   * <p>
   * Returns false results with e.g. "Harald Grün Haselstein" or "Karl Kardinal Lehmann" or "Ban Ki
   * Moon"
   *
   * @param authors The authors as string array.
   * @return The authors as list of author objects.
   */
  public List<Author> getAuthorListNormalFormat(String[] authors) {
    return getAuthorListNormalFormat(authors, " ");
  }

  /**
   * Parses authors in the following formats: "Peter Müller" or "Linda McCartney" or "John Gabriel
   * Smith-Wesson" or "Karl H. Meiser"
   * <p>
   * Returns false results with e.g. "Harald Grün Haselstein" or "Karl Kardinal Lehmann" or "Ban Ki
   * Moon"
   *
   * @param authors The authors as string array.
   * @param separator The separator between first names and lastnames.
   *
   * @return The authors as list of author objects.
   */
  public List<Author> getAuthorListNormalFormat(String[] authors, String separator) {

    List<Author> result = new ArrayList<>();
    for (String authorString : authors) {

      String[] parts = authorString.split(separator);

      StringBuilder givenName = new StringBuilder();
      StringBuilder surname = new StringBuilder(parts[parts.length - 1]);
      for (int i = parts.length - 2; i >= 0; i--) {
        if (parts[i].matches(PREFIX)) {
          surname.insert(0, parts[i] + " ");
        } else {
          givenName.insert(0, parts[i] + " ");
        }

      }
      Author author = new Author();

      author.setGivenName(givenName.toString().trim());
      author.setSurname(surname.toString());
      author.setFormat(this);
      result.add(author);

      /*
       * int lastSpace = authorString.lastIndexOf(separator);
       *
       * Author author = new Author();
       *
       * author.setGivenName(authorString.substring(0, lastSpace));
       * author.setSurname(authorString.substring(lastSpace + 1)); author.setFormat(this);
       * result.add(author);
       */
    }

    return result;
  }

  /**
   * Parses authors in the following formats: "P. Müller" or "L. McCartney" or "J.-P. Smith-Wesson"
   * or "K. H. Meiser" or "R-X Wang"
   *
   * @param authors The authors as string array.
   * @return The authors as list of author objects.
   */
  public List<Author> getAuthorListWithInitials(String[] authors) {
    List<Author> result = new ArrayList<>();
    for (String authorString : authors) {

      logger.debug("Testing " + authorString);

      int limit = authorString.lastIndexOf(". ");

      logger.debug("Limit " + limit);

      if (limit == -1) {
        return null;
      }

      Author author = new Author();
      author.setInitial(authorString.substring(0, limit + 1));
      author.setSurname(authorString.substring(limit + 2));
      author.setFormat(this);
      result.add(author);
    }

    return result;
  }

  /**
   * Parses authors in the following formats: "Müller, Herbert", "Meier-Schmitz, K.L." etc.
   *
   * @param authors The authors as string array.
   * @return The authors as list of author objects.
   */
  public List<Author> getAuthorListLeadingSurname(String[] authors, String limit) {
    List<Author> result = new ArrayList<>();
    for (String authorString : authors) {
      int delimiter = authorString.indexOf(limit);
      Author author = new Author();

      logger.debug("delimiter: " + delimiter);

      if (delimiter >= 0) {
        String givenName = authorString.substring(delimiter + 1).trim();
        if (givenName.contains(limit)) {
          return null;
        }
        author.setGivenName(givenName);
        author.setSurname(authorString.substring(0, delimiter).trim());
      } else {
        return null;
      }
      author.setFormat(this);
      result.add(author);
    }

    return result;
  }

  public List<Author> getAuthorListCheckingGivenNames(String[] authors) throws Exception {
    List<Author> result = new ArrayList<>();


    int prefixPosition = -1;
    for (String authorString : authors) {
      Author author = new Author();
      // check for prefix
      String[] parts = authorString.split(" ");

      // check middle parts
      if (parts.length > 2) {
        for (int i = 1; i < parts.length - 1; i++) {
          if (parts[i].matches(" *" + PREFIX + " *")) {
            author.setPrefix(parts[i]);
            prefixPosition = i;
          }
        }
      }

      StringBuilder givenName = new StringBuilder();
      String surname = "";

      if (prefixPosition == -1) {
        int lastSpace = authorString.lastIndexOf(" ");
        if (lastSpace == -1) {
          return null;
        }
        givenName = new StringBuilder(authorString.substring(0, lastSpace));
        surname = authorString.substring(lastSpace + 1);
      } else {
        surname = parts[parts.length - 1];
        for (int i = 0; i < prefixPosition; i++) {
          givenName.append(parts[i]);
        }

      }


      String[] names = givenName.toString().split(" |-");
      for (String name : names) {
        if (!isGivenName(name)) {
          return null;
        }
      }


      author.setGivenName(givenName.toString());
      author.setSurname(surname);
      author.setFormat(this);
      result.add(author);
    }

    return result;
  }



  /**
   * Parses authors using controlled vocabularies.
   *
   * @param authorsString The complete author string.
   * @param authors A string array holding the strings of single authors.
   * @return A {@link List} of {@link Author} beans.
   * @throws Exception Any {@link Exception}
   */
  public List<Author> getAuthorListCheckingNames(String authorsString, String[] authors) throws Exception {
    List<Author> result = new ArrayList<>();
    for (String authorString : authors) {

      Author author = new Author();

      String[] names = authorString.split(" ");
      int part = 0;
      while (isGivenName(names[part])) {
        part++;
        if (part == names.length) {
          return null;
        }
      }
      for (int i = part; i < names.length; i++) {
        if (!isSurname(names[i])) {
          return null;
        }
      }

      logger.debug("part: " + authorsString.indexOf(names[part]));

      if (part == 0) {
        return null;
      }
      String givenName = authorString.substring(0, authorString.indexOf(names[part]) - 1);
      String surname = authorString.substring(authorString.indexOf(names[part]));

      author.setGivenName(givenName);
      author.setSurname(surname);
      author.setFormat(this);
      result.add(author);
    }

    return result;
  }

  public List<Author> getAuthorListLooseFormat(String[] authors) {
    List<Author> result = new ArrayList<>();
    for (String authorString : authors) {
      Author author = new Author();



      // parse information in brackets, if available
      int openBracketIndex = authorString.indexOf("(");
      int closingBracketIndex = authorString.indexOf(")");

      if (openBracketIndex != -1 && closingBracketIndex != -1) {
        // String additionalInfo = authorString.substring(openBracketIndex + 1,
        // closingBracketIndex);
        authorString = authorString.substring(0, openBracketIndex);
      }

      // remove forbidden characters
      authorString = authorString.replaceAll(FORBIDDEN_CHARACTERS, "");

      // split the rest of the string and parse it
      String[] parts = authorString.split("\\s");

      if (parts.length > 1) {

        String surname = parts[parts.length - 1];
        StringBuilder prefix = new StringBuilder();
        StringBuilder givenName = new StringBuilder();
        StringBuilder title = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
          String part = parts[i];
          if (part.matches(PREFIX) && !givenName.toString().trim().isEmpty()) {
            prefix.append(part).append(" ");
          } else if (part.matches(TITLE) && givenName.toString().trim().isEmpty()) {
            title.append(part).append(" ");
          } else if (part.matches(IGNORE_CHARACTERS)) {
            // ignore whole string
          }

          else {
            givenName.append(part).append(" ");
          }


        }

        author.setGivenName(givenName.toString().trim());
        author.setSurname(prefix.toString().trim() + " " + surname.trim());
        author.setSurname(author.getSurname().trim());
        author.setTitle(title.toString().trim());
        author.setFormat(this);
        result.add(author);

      } else if (parts.length == 1 && !parts[0].isEmpty()) {

        author.setSurname(parts[0].trim());
        author.setFormat(this);
        result.add(author);

      } else {
        // do nothing
      }



    }

    return result;

  }

  protected List<Author> getAuthorListLooseFormatSurnameFirst(String[] authors) {
    List<Author> result = new ArrayList<>();
    for (String authorString : authors) {
      Author author = new Author();
      String[] parts = null;
      if (authorString.contains(",")) {
        parts = authorString.split(",");
      } else if (authorString.contains(";")) {
        parts = authorString.split(";");
      } else {
        if (authorString.contains("{") && authorString.contains("}") && authorString.indexOf("{") < authorString.indexOf("}")) {
          authorString.substring(authorString.indexOf("{") + 1, authorString.indexOf("}"));
          if (authorString.indexOf("{", authorString.indexOf("}")) != -1 && authorString.indexOf("}", authorString.indexOf("}")) != -1
              && authorString.indexOf("{", authorString.indexOf("}")) < authorString.indexOf("}", authorString.indexOf("}"))) {
            authorString.substring(authorString.indexOf("{", authorString.indexOf("}")) + 1,
                authorString.indexOf("}", authorString.indexOf("{", authorString.indexOf("}"))));
          }
          authorString = authorString.substring(0, authorString.indexOf("{"));
        }
        parts = authorString.split(" ");
      }


      if (parts != null && parts.length > 1) {

        String[] surnameParts = parts[0].split("\\s");
        String[] givenNameParts = parts[1].split("\\s");

        // look for other parts that are seperated by a comma, e.g. "Jun." or "Sen."
        StringBuilder additionalParts = new StringBuilder();
        if (parts.length > 2) {
          for (int i = 2; i < parts.length; i++) {
            additionalParts.append(parts[i]).append(" ");
          }
        }
        additionalParts = new StringBuilder(additionalParts.toString().trim());

        StringBuilder surname = new StringBuilder();
        StringBuilder prefix = new StringBuilder();
        StringBuilder givenName = new StringBuilder();
        StringBuilder title = new StringBuilder();

        for (String surnamePart : surnameParts) {
          if (surnamePart.toLowerCase().matches(PREFIX)) {
            prefix.append(surnamePart).append(" ");
          } else if (surnamePart.matches(TITLE)) {
            title.append(surnamePart).append(" ");
          } else if (surnamePart.matches(FORBIDDEN_CHARACTERS)) {
            // ignore part
          } else {
            surname.append(surnamePart).append(" ");
          }

        }

        for (String givenNamePart : givenNameParts) {
          if (givenNamePart.matches(FORBIDDEN_CHARACTERS)) {
            // ignore part
          } else {
            givenName.append(givenNamePart).append(" ");
          }

        }

        if (additionalParts.toString().matches(FORBIDDEN_CHARACTERS)) {
          additionalParts = new StringBuilder();
        }

        author.setGivenName(givenName.toString().trim());
        author.setSurname(prefix.toString().trim() + " " + surname.toString().trim() + " " + additionalParts);
        author.setSurname(author.getSurname().trim());
        author.setTitle(title.toString().trim());

      } else if (parts != null) {
        author.setSurname(parts[0].trim());
      }



      author.setFormat(this);
      result.add(author);
    }

    return result;

  }

  /**
   * Checks if a string contains at least one of the given characters without using regexp.
   *
   * @param authorsString The string that is tested
   * @param listOfCharacters A string containing characters that are searched for.
   *
   * @return true if the tested string contains one of the test characters.
   */
  protected boolean contains(String authorsString, String listOfCharacters) {
    for (char chr : listOfCharacters.toCharArray()) {
      if (authorsString.indexOf(chr) >= 0) {
        return true;
      }
    }
    return false;
  }

  /**
   * Splits a string into pieces without using regexp.
   *
   * @param authorsString The string that is splitted.
   * @param delimiter The character where the string is splitted at.
   *
   * @return A string array containing the trimmed pieces of the string.
   */
  protected String[] split(String authorsString, char delimiter) {
    ArrayList<String> list = new ArrayList<>();
    int currentStart = 0;
    int currentEnd;
    while ((currentEnd = authorsString.indexOf(delimiter, currentStart)) >= 0) {
      list.add(authorsString.substring(currentStart, currentEnd).trim());
      currentStart = currentEnd + 1;
    }
    list.add(authorsString.substring(currentStart).trim());
    return list.toArray(new String[] {});
  }

  /**
   * Removes disturbing whitespaces from a given input string. If an author format does not require
   * this normalization it has to override this method.
   *
   * @param authors The string that should be normalized.
   *
   * @return The normalized string.
   */
  protected String normalize(String authors) {
    return authors.replaceAll("[ \t]+", " ").replaceAll("(\\n)+", "\n").trim();
  }

}
