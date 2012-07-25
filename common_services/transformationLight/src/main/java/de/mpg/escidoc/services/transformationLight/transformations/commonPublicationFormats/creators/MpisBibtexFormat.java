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
* Copyright 2006-2010 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

package de.mpg.escidoc.services.transformationLight.transformations.commonPublicationFormats.creators;

import java.util.List;

public class MpisBibtexFormat extends AuthorFormat {

    @Override
    public String getPattern() {
        return "^\\s*" + NAME + "(\\{\\})?" + ", +" + INITIALS + "( +and +" + NAME + "(\\{\\})?" + ", +" + INITIALS + ")*\\s*$";
    }

    @Override
    public List<Author> getAuthors(String authorsString) {
        System.out.println(getPattern());
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
        return 1;
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
