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

import java.util.ArrayList;
import java.util.List;

/**
 * Special parser to write the input string into the surname
 * of a single author if no other parser matched the input.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class ResidualFormat extends AuthorFormat
{

    @Override
    public String getPattern()
    {
        return ".";
    }

    @Override
    public List<Author> getAuthors(String authorsString)
    {

        List<Author> result = new ArrayList<Author>();
        Author author = new Author();
        author.setSurname(authorsString);
        author.setFormat(this);
        result.add(author);
        return result;
    }

    @Override
    public int getSignificance()
    {
        return Integer.MAX_VALUE;
    }

    @Override
    public String getDescription()
    {
        return "Everything is written into the surname of a single author";
    }

    @Override
    public String getName()
    {
        return "Residual format, taken if no other format matched";
    }

    @Override
    public String getWarning()
    {
        return "The system was not able to identify the authors' names.";
    }

}
