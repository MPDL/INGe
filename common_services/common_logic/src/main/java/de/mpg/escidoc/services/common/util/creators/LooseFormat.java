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
 * Loose parser that accepts many citation errors for comma or semicolon seperated authors
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class LooseFormat extends AuthorFormat {
    
    @Override
    public String getPattern() {
        return "^\\s*" + LOOSE_SYLLABLE + "(\\s+" + LOOSE_SYLLABLE + ")*(\\s*(,|;| and | und | et )\\s*" + LOOSE_SYLLABLE + "(\\s+" + LOOSE_SYLLABLE + ")*)*\\s*$";
    }

    @Override
    public List<Author> getAuthors(String authorsString) throws Exception
    {

        String[] authors = authorsString.split("(,|;| and | und | et )");
        
        return getAuthorListLooseFormat(authors);
    }

    

    @Override
    public int getSignificance() {
        return 16;
    }

    @Override
    public String getDescription() {
        return "VorName prefix nachname[, nachname, prefix nachname]";
    }

    @Override
    public String getName() {
        return "Loose format, comma or semicolon seperated, accepts and ignores line breaks and tabs, case insensitive, takes last word as surname, the words before as prefix or given name";
    }

    @Override
    public String getWarning() {
        return null;
    }

}
