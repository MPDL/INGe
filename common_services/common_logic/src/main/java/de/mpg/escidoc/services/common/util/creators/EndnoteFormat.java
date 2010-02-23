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
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

package de.mpg.escidoc.services.common.util.creators;

import java.util.List;

import net.sf.saxon.functions.Contains;

/**
 * Parser for comma seperated author strings (surname first, semicolon, given name(s)), mixed given names and initials
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 2014 $ $LastChangedDate: 2009-05-25 15:49:47 +0200 (Mo, 25 Mai 2009) $
 *
 */
public class EndnoteFormat extends AuthorFormat {
    
    @Override
    public String getPattern() {
        return "^\\s*" + NAME + ", ?" + GIVEN_NAME_FORMAT_MIXED + "( *\\n *" + NAME + ", ?" + GIVEN_NAME_FORMAT_MIXED + ")*\\s*$";
    }

    @Override
    public List<Author> getAuthors(String authorsString) {

        if (!authorsString.contains("\n"))
        {
            return null;
        }
        
        String[] authors = authorsString.split(" *\\n *");

        return getAuthorListLeadingSurname(authors, ",");
    }


    @Override
    public int getSignificance() {
        return 5;
    }

    @Override
    public String getDescription() {
        return "Nachname, Vorname(n) I.[\n Nach-name, I. Vor-Name\n Nachname, I.\n Nachname, Vorname(n)]";
    }

    @Override
    public String getName() {
        return "Endnote-Format, Nachname voran, Initialen und komplette Vornamen gemischt, zeilenumbruch-getrennt";
    }

    @Override
    public String getWarning() {
        return null;
    }

    @Override
    protected String normalize(String authors)
    {
        return authors.trim();
    }
    
}
