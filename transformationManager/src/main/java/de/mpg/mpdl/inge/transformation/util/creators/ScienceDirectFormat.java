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
import java.util.List;

/**
 * Very specialized parser to parse author strings like <code>Vorname Nachname[a]
 * , Corresponding Author Contact Information
 * , E-mail The Corresponding Author[, Vor-Name Nach-Name[b]]</code>.
 *
 * @author franke (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 3183 $ $LastChangedDate: 2010-05-27 16:10:51 +0200 (Do, 27 Mai 2010) $
 */
public class ScienceDirectFormat extends AuthorFormat {

  @Override
  public String getPattern() {
    return "^\\s*" + GIVEN_NAME_FORMAT + " " + NAME + "[a-z], "
        + "Corresponding Author Contact Information, E-mail The Corresponding Author" + "( *(,| and | AND | und | et ) *"
        + GIVEN_NAME_FORMAT + " " + NAME + "[a-z])*\\s*$";
  }

  @Override
  public List<Author> getAuthors(String authorsString) {

    if (!authorsString.contains("[") || !authorsString.contains("Corresponding Author Contact Information")) {
      return null;
    }

    String[] authors = authorsString.split(" *(,| and | AND | und | et ) *");
    List<String> newList = new ArrayList<>();
    for (int i = 0; i < authors.length; i++) {
      if (1 != i && 2 != i) {
        newList.add(authors[i]);
      }
    }
    List<Author> result = getAuthorListNormalFormat(newList.toArray(new String[] {}));
    for (Author author : result) {
      author.setSurname(author.getSurname().substring(0, author.getSurname().length() - 1));
    }
    return result;
  }

  @Override
  public int getSignificance() {
    return 1;
  }

  @Override
  public String getDescription() {
    return "Vorname Nachname[a], Corresponding Author Contact Information, " + "E-mail The Corresponding Author[, Vor-Name Nach-Name[b]]";
  }

  @Override
  public String getName() {
    return "ScienceDirectFormat";
  }

  @Override
  public String getWarning() {
    return null;
  }

}
