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

/**
 * Parser for comma or semicolon separated author strings that accepts mixed initials and full given names.
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class WesternFormat10 extends AuthorFormat {
    
    @Override
    public String getPattern() {
        return "^\\s*" + GIVEN_NAME_FORMAT_MIXED + " " + NAME + "( *(,|;| and | und | et ) *" + GIVEN_NAME_FORMAT_MIXED + " " + NAME + ")*\\s*$";
    }

    @Override
    public List<Author> getAuthors(String authorsString) throws Exception
    {

        String[] authors = authorsString.split(" *(,|;| and | und | et ) *");
        
        return getAuthorListNormalFormat(authors);
    }

    @Override
    public int getSignificance() {
        return 9;
    }

    @Override
    public String getDescription() {
        return "Vorname Nachname[, Vor-Name Nach-Name, Vorname I. Nachname, I. Vorname Nachname]";
    }

    @Override
    public String getName() {
        return "Westliches Normalformat Initialen und komplette Vornamen gemischt, komma oder semikolon-getrennt";
    }

    @Override
    public String getWarning() {
        return null;
    }

}
