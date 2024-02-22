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

import java.util.List;

public class WesternFormat6 extends AuthorFormat {

  @Override
  public String getPattern() {
    return "^\\s*" + NAME + ", *" + GIVEN_NAME_FORMAT + "( *(;| and | AND | und | et |\\n) *" + NAME + ", *" + GIVEN_NAME_FORMAT
        + ")*\\s*$";
  }

  @Override
  public List<Author> getAuthors(String authorString) {

    if ((!authorString.contains(",")) || contains(authorString, "0123456789")
        || (authorString.contains(",") && authorString.contains(";") && authorString.indexOf(";") < authorString.indexOf(","))) {
      return null;
    }

    String[] authors = authorString.split(" *(;| and | AND | und | et |\\n) *");

    for (String author : authors) {
      if (author.split("\\s")[0].contains(".")) {
        return null;
      }
    }

    return getAuthorListLeadingSurname(authors, ",");
  }

  @Override
  public int getSignificance() {
    return 11;
  }

  @Override
  public String getDescription() {
    return "Nachname, Vorname[; Nach-Name, Vor-Name]";
  }

  @Override
  public String getName() {
    return "Westliches Format, Nachname voran, semikolon-getrennt";
  }

  @Override
  public String getWarning() {
    return null;
  }

}
