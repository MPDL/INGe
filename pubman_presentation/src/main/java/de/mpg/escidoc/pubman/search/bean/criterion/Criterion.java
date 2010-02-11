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

package de.mpg.escidoc.pubman.search.bean.criterion;

import java.util.ArrayList;

import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion.LogicalOperator;

/**
 * superclass for the advanced search criterion vo's.
 * @created 10-Mai-2007 15:45:31
 * @author NiH
 * @version 1.0
 * Revised by NiH: 13.09.2007
 */
public abstract class Criterion
{
	
	/**
	 * Returns a search criterion which can be used in a search query towards the search service.
	 * @return a metadata serach criterion
	 * @throws TechnicalException if MetadataSearchCriterion cannot be instantiated
	 */
	public abstract ArrayList<MetadataSearchCriterion> createSearchCriterion() throws TechnicalException;

	//logic operator between the search criteria
    private LogicalOperator logicalOperator = null;
    //the string to search for
	private String searchString = null;

	public Criterion()
    {
	}

	public LogicalOperator getLogicalOperator()
    {
		return logicalOperator;
	}

	public String getSearchString()
    {
		return searchString;
	}

	public void setLogicalOperator(LogicalOperator newVal)
    {
		logicalOperator = newVal;
	}

	public void setSearchString(String newVal)
    {
		searchString = newVal;
	}
	
	protected boolean isSearchStringEmpty() {
		if ( searchString == null || searchString.trim().equals("") ) {
			return true;
		}
		else {
			return false;
		}
	}
}