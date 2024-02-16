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
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */

package de.mpg.mpdl.inge.transformation.util.creators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Loose parser that accepts many citation errors for comma or semicolon seperated authors
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 3183 $ $LastChangedDate: 2010-05-27 16:10:51 +0200 (Do, 27 Mai 2010) $
 *
 */
public class LooseFormatWithInfoInBraces extends AuthorFormat {

  public final String BRACES_WITH_ANY_CONTENT = "\\s+\\(\\s*(\\s*\\S+)+\\s*\\)\\s*";

  @Override
  public String getPattern() {
    // return "^\\s*" + LOOSE_SYLLABLE + "(\\s+" + LOOSE_SYLLABLE + ")*(" + BRACES_WITH_ANY_CONTENT
    // + ")*(\\s*(,|;| and | AND | und | et )\\s*" + LOOSE_SYLLABLE + "(\\s+" + LOOSE_SYLLABLE +
    // ")*(" + BRACES_WITH_ANY_CONTENT + ")*)*\\s*(,|;)*\\s*$";
    return ".";
  }

  @Override
  public List<Author> getAuthors(String authorsString) throws Exception {

    // Check 4 versions (new line as separator or blank, surname or given name first) and choose the
    // best result
    String newLineAsBlank = authorsString.replaceAll("\\s+", " ").trim();
    String newLineAsSeparator = authorsString.replaceAll("\\n", " and ").trim();
    newLineAsSeparator = newLineAsSeparator.replaceAll("\\s+", " ");

    List<Author> newLineAsBlankSurnameFirst = getAuthorListLooseFormatSurnameFirst(newLineAsBlank.split("(;| and | AND | und | et )"));
    List<Author> newLineAsSeparatorSurnameFirst =
        getAuthorListLooseFormatSurnameFirst(newLineAsSeparator.split("(;| and | AND | und | et )"));
    List<Author> newLineAsBlankGivenNameFirst = getAuthorListLooseFormat(prepareAuthorsLooseFormat(newLineAsBlank));
    List<Author> newLineAsSeparatorGivenNameFirst = getAuthorListLooseFormat(prepareAuthorsLooseFormat(newLineAsSeparator));



    if (testAuthors(newLineAsSeparatorGivenNameFirst)) {
      return newLineAsSeparatorGivenNameFirst;
    } else if (testAuthors(newLineAsBlankGivenNameFirst)) {
      return newLineAsBlankGivenNameFirst;
    } else if (testAuthors(newLineAsSeparatorSurnameFirst)) {
      return newLineAsSeparatorSurnameFirst;
    } else if (testAuthors(newLineAsBlankSurnameFirst)) {
      return newLineAsBlankSurnameFirst;
    }

    return newLineAsSeparatorGivenNameFirst;
  }

  private String[] prepareAuthorsLooseFormat(String authorsString) {
    List<String> parts = new ArrayList<String>();
    String currentString = "";

    // remove last comma or semicolon

    String openedBracketsRegEx = "(\\(|\\{|\\[)";
    String closedBracketsRegEx = "(\\)|\\}|\\])";
    String seperatorsRegEX = "(,|;)";
    int brackets = 0;

    // split string by commas and semicolons (if they are not inside a bracket)
    for (int i = 0; i < authorsString.length(); i++) {
      String currentChar = String.valueOf(authorsString.charAt(i));
      if (currentChar.matches(openedBracketsRegEx)) {
        brackets += 1;
        currentString += currentChar;

      } else if (currentChar.matches(closedBracketsRegEx)) {
        brackets -= 1;
        currentString += currentChar;
      } else if (currentChar.matches(seperatorsRegEX) && brackets == 0) {
        parts.add(new String(currentString));
        currentString = "";

      } else {
        currentString += currentChar;
      }

    }
    // add last Part if not empty
    if (!currentString.trim().isEmpty()) {
      parts.add(currentString);
    }



    // split strings by rest of seperators
    String seperatorsRegEX2 = "( and | und | et )";
    List<String> parts2 = new ArrayList<String>();
    for (int i = 0; i < parts.size(); i++) {
      String part = parts.get(i);
      String[] newSeps = part.split(seperatorsRegEX2);
      Collections.addAll(parts2, newSeps);
    }


    String[] authors = parts2.toArray(new String[0]);

    return authors;
  }

  private boolean testAuthors(List<Author> authorList) {

    for (Author a : authorList) {
      if (a.getGivenName() == null || a.getGivenName().isEmpty() || a.getSurname() == null || a.getSurname().isEmpty()
          || a.getSurname().trim().split(" ").length > 4) {
        return false;
      }
    }
    return true;
  }

  @Override
  public int getSignificance() {
    return 15;
  }

  @Override
  public String getDescription() {
    return "VorName prefix nachname (info, info2)[, vorname nachname, prefix nachname (info)]";
  }

  @Override
  public String getName() {
    return "Loose format with additional optional information in brackets for each autor, comma or semicolon seperated, accepts and ignores line breaks and tabs, case insensitive, takes last word as surname, the words before as prefix or given name";
  }

  @Override
  public String getWarning() {
    return null;
  }

}
