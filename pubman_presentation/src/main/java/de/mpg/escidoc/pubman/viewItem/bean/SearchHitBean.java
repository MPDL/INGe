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

package de.mpg.escidoc.pubman.viewItem.bean;

import org.apache.log4j.Logger;

/**
 * Simple bean to store the elements of the fulltext search hits.
 * 
 * @author: Tobias Schraut, created 25.03.2008
 * @version: $Revision$ $LastChangedDate$
 */
public class SearchHitBean 
{
	private static Logger logger = Logger.getLogger(SearchHitBean.class);
	private String beforeSearchHitString;
	private String searchHitString;
	private String afterSearchHitString;

    
	/**
	 * Public constructor
	 * @param beforeSearchHitString
	 * @param searchHitString
	 * @param afterSearchHitString
	 */
	public SearchHitBean(String beforeSearchHitString, String searchHitString, String afterSearchHitString)
	{
		this.beforeSearchHitString = beforeSearchHitString;
		this.searchHitString = searchHitString;
		this.afterSearchHitString = afterSearchHitString;
	}

	public String getBeforeSearchHitString() {
		return beforeSearchHitString;
	}

	public void setBeforeSearchHitString(String beforeSearchHitString) {
		this.beforeSearchHitString = beforeSearchHitString;
	}

	public String getSearchHitString() {
		return searchHitString;
	}

	public void setSearchHitString(String searchHitString) {
		this.searchHitString = searchHitString;
	}

	public String getAfterSearchHitString() {
		return afterSearchHitString;
	}

	public void setAfterSearchHitString(String afterSearchHitString) {
		this.afterSearchHitString = afterSearchHitString;
	}
    
    
    
}