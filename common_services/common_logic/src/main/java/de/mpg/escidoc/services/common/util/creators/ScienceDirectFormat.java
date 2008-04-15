/*
*
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

public class ScienceDirectFormat extends AuthorFormat {
	
	@Override
	public String getPattern() {
		return "^\\s*" + GIVEN_NAME_FORMAT + " " + NAME + "[a-z], Corresponding Author Contact Information, E-mail The Corresponding Author( *(,| and | und | et ) *" + GIVEN_NAME_FORMAT + " " + NAME + "[a-z])*\\s*$";
	}

	@Override
	public List<Author> getAuthors(String authorsString) throws Exception
	{

		String[] authors = authorsString.split(" *(,| and | und | et ) *");
		List<String> newList = new ArrayList<String>();
		for (int i = 0; i < authors.length; i++) {
			if (i != 1 && i != 2)
			{
				newList.add(authors[i]);
			}
		}
		List<Author> result = getAuthorListNormalFormat(newList.toArray(new String[]{}));
		for (Author author : result) {
			author.setSurname(author.getSurname().substring(0, author.getSurname().length() - 1 ));
		}
		return result;
	}

	@Override
	public int getSignificance() {
		return 1;
	}

	@Override
	public String getDescription() {
		return "Vorname Nachname[a], Corresponding Author Contact Information, E-mail The Corresponding Author[, Vor-Name Nach-Name[b]]";
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
