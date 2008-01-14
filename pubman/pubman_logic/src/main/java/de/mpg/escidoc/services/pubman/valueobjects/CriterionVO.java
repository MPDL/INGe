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

package de.mpg.escidoc.services.pubman.valueobjects;

import java.io.Serializable;
/**
 * superclass for the advanced search criterion vo's.
 * @created 10-Mai-2007 15:45:31
 * @author NiH
 * @version 1.0
 * Revised by NiH: 13.09.2007
 */
public class CriterionVO implements Serializable
{
	/** serial for the serializable interface*/
	private static final long serialVersionUID = 1L;
	
    /**
	 * enum for the logic operation between the search criteria.
	 */
	public enum LogicOperator
    {
		AND,
		OR,
		NOT
	}

	//logic operator between the search criteria
    private LogicOperator logicOperator;
    //the string to search for
	private String searchString;

	/**
	 * constructor.
	 */
	public CriterionVO()
    {
	}

	public LogicOperator getLogicOperator()
    {
		return logicOperator;
	}

	public String getSearchString()
    {
		return searchString;
	}

	public void setLogicOperator(LogicOperator newVal)
    {
		logicOperator = newVal;
	}

	public void setSearchString(String newVal)
    {
		searchString = newVal;
	}
}