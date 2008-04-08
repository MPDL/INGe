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

package de.mpg.escidoc.services.pubman;

import java.util.ArrayList;
import java.util.List;

import de.mpg.escidoc.services.common.exceptions.AffiliationNotFoundException;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;
import de.mpg.escidoc.services.common.valueobjects.PubItemResultVO;
import de.mpg.escidoc.services.common.valueobjects.PubItemVO;
import de.mpg.escidoc.services.pubman.searching.ParseException;
import de.mpg.escidoc.services.pubman.valueobjects.CriterionVO;

/**
 * Interface of the PubMan Search Service.
 * 
 * @author $Author: tendres $
 * @version $Revision: 423 $ $LastChangedDate: 2007-11-07 17:24:50 +0100 (Wed, 07 Nov 2007) $
 * @created 22-Jan-2007 16:17:37
 * Revised by NiH: 13.09.2007
 */
public interface PubItemSearching
{
    /**
     * The service name.
     */
	public static final String SERVICE_NAME = "ejb/de/mpg/escidoc/services/pubman/PubItemSearching";
    
    /**
     * retieves the cql search query of the advanced search method.
     * @return (String): the cql query string.
     */
    public String getCqlQuery();
    
	/**
	 * Searches for publication items that matches the given query.
     * 
	 * @param searchString The search query.
	 * @param searchInFiles If true, search is also executed in files. 
	 * @return The list of PubItemResultVOs that matched the query.
	 * @throws ParseException 
	 * @throws TechnicalException 
	 */
	public List<PubItemResultVO> search(String searchString, boolean searchInFiles) throws ParseException, TechnicalException;

	
    /**
     * Searches for publication items that matches the given query.
     * 
     * @param list with the search criteria (ArrayList<CriterionVO>)
     * @return The list of PubItemResultVOs that matched the query.
     * @throws ParseException 
     * @throws TechnicalException 
     */
    public List<PubItemResultVO> advancedSearch(ArrayList<CriterionVO> list, String language) throws ParseException, TechnicalException;

    /**
     * Searches for publication items that matches the given query 
     * and returns output as one of the export formats.
     * 
	 * @param searchString The search query.
     * @param exportFormat - is the one of the eSciDoc export formats
     * @param outputFormat - is the one of the eSciDoc export file formats
     * @return the result of the output according as byte array   
     * @throws ParseException 
     * @throws TechnicalException 
     */
    public byte[] searchAndOutput(String searchString, String exportFormat, String outputFormat) throws ParseException, TechnicalException;
    
	/**
	 * Searches for all items that belong to the given affiliation. An item belongs to an affilation if it
     * has the affiliationPID set in the item metadata as a creator-organization-id or a creator-person-organisation-id.
     * 
	 * @param affilationRef The reference of the affiliation
	 * @return The list of PubItemVOs that belong to the given affiliation.
	 * @throws TechnicalException 
	 * @throws AffiliationNotFoundException 
	 */
	public List<PubItemVO> searchPubItemsByAffiliation(AffiliationVO affilation) throws TechnicalException, AffiliationNotFoundException;

}