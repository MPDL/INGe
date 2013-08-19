/*
* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.de/license.
* See the License for the specific language governing permissions
* and limitations under the License.
*
* When distributing Covered Code, include this CDDL HEADER in each
* file and include the License file at license/ESCIDOC.LICENSE.
* If applicable, add the following below this CDDL HEADER, with the
* fields enclosed by brackets "[]" replaced with your own identifying
* information: Portions Copyright [yyyy] [name of copyright owner]
*
* CDDL HEADER END
*/

/*
* Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

package de.mpg.escidoc.services.transformation.transformations.commonPublicationFormats.creators;

import java.util.List;

public class MpisBibtexFormat extends AuthorFormat {

    @Override
    public String getPattern() {
        System.out.println("NAME: " + NAME);
        System.out.println("INITIALS: " + INITIALS);
        return "^\\s*" + GIVEN_NAME_FORMAT_MIXED + "(\\{\\})?" + ", +" + GIVEN_NAME_FORMAT_MIXED + "( +and +" + GIVEN_NAME_FORMAT_MIXED + "(\\{\\})?" + ", +" + GIVEN_NAME_FORMAT_MIXED + ")*\\s*$";
    }

    @Override
    public List<Author> getAuthors(String authorsString) {
        System.out.println("MpisBibtexFormat-Pattern: " + getPattern());
        if (authorsString == null || !authorsString.contains("{}") || !authorsString.matches(getPattern())) {
            return null;
        }
        String[] authors = authorsString.split(" +and +");
        List<Author> result = getAuthorListLeadingSurname(authors, ",");
        for (Author author : result)
        {
            if (author.getSurname().endsWith("{}"))
            {
                author.setSurname(author.getSurname().replace("{}", ""));
                author.getTags().put("brackets", "true");
            }
        }
        return result;
    }

    @Override
    public int getSignificance() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "V. Nachname[; V.-N. Nach-Name]";
    }

    @Override
    public String getName() {
        return "BibTeX-Format mit geschweiften Klammern als CoNE-Indikator";
    }

    @Override
    public String getWarning() {
        return null;
    }

}
