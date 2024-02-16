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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Very specialized parser to parse author strings like <code>Nachname Vorname{CoNE-Identifier}
 * {CoNE-Affiliation_01}{CoNE-Affiliation_02}...</code>.
 *
 * @author walter (initial creation)
 * @author $Author: walter $ (last modification)
 * @version $Revision: $ $LastChangedDate: $
 */
public class MpiKybFormat extends AuthorFormat {
  protected static final String IDENTIFIER = "identifier";
  protected static final String AFFILIATION = "affiliation";
  private static final String AFFILIATION_COUNT = "affiliationsCount";

  @Override
  public String getPattern() {
    return NAME + " +" + INITIALS + "\\{.+?\\}" + "\\{??.*?\\}??";
  }

  @Override
  public List<Author> getAuthors(String authorsString) {
    Pattern pattern = Pattern.compile(getPattern());
    Matcher matcher = pattern.matcher(authorsString);
    if (!matcher.find()) {
      return null;
    }
    Pattern kommaPattern = Pattern.compile(",([^\\}]+?[\\{\\n\\r$])");
    matcher = kommaPattern.matcher(authorsString + "\n");
    if (matcher.find()) {
      authorsString = matcher.replaceAll(";$1");
    }
    Pattern andPattern = Pattern.compile(" +?and +?([^\\}]+?[\\{\\n\\r$])");
    matcher = andPattern.matcher(authorsString);
    if (matcher.find()) {
      authorsString = matcher.replaceAll(";$1");
    }
    if (authorsString.lastIndexOf("\n") == authorsString.length() - 1) {
      authorsString = authorsString.substring(0, authorsString.lastIndexOf("\n"));
    }
    String[] authors = authorsString.split(";");
    List<String> newList = new ArrayList<>();
    Collections.addAll(newList, authors);
    List<Author> result = getAuthorList(newList.toArray(new String[] {}), " ");
    return result;
  }

  private List<Author> getAuthorList(String[] authors, String separator) {
    List<Author> result = new ArrayList<>();
    for (String authorString : authors) {
      String[] parts = null;
      String identifier = null;
      List<String> affiliations = new ArrayList<>();
      int affiliationCount = 0;
      if (authorString.contains("{")) {
        identifier = authorString.substring(authorString.indexOf("{") + 1, authorString.indexOf("}"));
        String affiliationsString = null;
        if (authorString.indexOf("{", authorString.indexOf("}")) != -1) {
          affiliationsString = authorString.substring(authorString.indexOf("{", authorString.indexOf("}")));
          while (affiliationsString.contains("{")) {
            affiliations.add(affiliationsString.substring(affiliationsString.indexOf("{") + 1, affiliationsString.indexOf("}")));
            affiliationsString = affiliationsString.substring(affiliationsString.indexOf("}") + 1);
            affiliationCount++;
          }
        }
        authorString = authorString.substring(0, authorString.indexOf("{"));
      }
      authorString = authorString.trim();
      parts = authorString.split(separator);
      String initials = parts[parts.length - 1];
      String surname = "";
      for (int i = parts.length - 2; i >= 0; i--) {
        surname = parts[i] + " " + surname;
      }
      Author author = new Author();
      if (surname != null && !(surname.equalsIgnoreCase(""))) {
        author.setInitial(initials.trim());
      } else {
        surname = initials;
      }
      author.setSurname(surname.trim());
      if (identifier != null) {
        author.addTag(IDENTIFIER, identifier);
      }
      if (affiliations != null) {
        author.addTag(AFFILIATION_COUNT, String.valueOf(affiliationCount));
        for (int j = 0; j < affiliationCount; j++) {
          author.addTag(AFFILIATION + affiliationCount, affiliations.get(j));
        }
      }
      author.setFormat(this);
      result.add(author);
    }
    return result;
  }

  @Override
  public int getSignificance() {
    return 1;
  }

  @Override
  public String getDescription() {
    return "Nachname Initialen{CoNE-identifier}{CoNE-Affilation_01}{CoNE-Affilation_02}...";
  }

  @Override
  public String getName() {
    return "MPI for Biological Cybernetics";
  }

  @Override
  public String getWarning() {
    return null;
  }
}
