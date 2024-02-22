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

/**
 * Special parser to parse author strings like
 * <code>Brian Richardson 1 *, Michael S. Watt 1, Euan G. Mason 2, and Darren J. Kriticos 1</code>.
 *
 * @author franke (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 3183 $ $LastChangedDate: 2010-05-27 16:10:51 +0200 (Do, 27 Mai 2010) $
 */
public class OxfordJournalFormat extends AuthorFormat {

  @Override
  public String getPattern() {
    return "^\\s*" + GIVEN_NAME_FORMAT + " " + NAME + " [0-9]+( \\*)?(, (and)? *" + GIVEN_NAME_FORMAT + " " + NAME
        + " [0-9]+( \\*)?)*\\s*$";
  }

  @Override
  public List<Author> getAuthors(String authorString) {

    if (authorString.contains(";") || !authorString.matches("\\d")) {
      return null;
    }

    String[] authors = authorString.split(" *, (and)? *");

    for (int i = 0; i < authors.length; i++) {
      authors[i] = authors[i].replaceAll(" [0-9]( \\*)?$", "");
    }
    List<Author> result = getAuthorListNormalFormat(authors);
    return result;
  }

  @Override
  public int getSignificance() {
    return 1;
  }

  @Override
  public String getDescription() {
    return "Brian Richardson 1 *, Michael S. Watt 1, Euan G. Mason 2, and Darren J. Kriticos 1";
  }

  @Override
  public String getName() {
    return "OxfordJournalFormat";
  }

  @Override
  public String getWarning() {
    return null;
  }

}
