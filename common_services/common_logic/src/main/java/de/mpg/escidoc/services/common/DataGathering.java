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

package de.mpg.escidoc.services.common;

import de.mpg.escidoc.services.common.exceptions.AffiliationNotFoundException;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.referenceobjects.ItemRO;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;
import de.mpg.escidoc.services.common.valueobjects.RelationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;

/**
 * Interface for gathering data to fill VOs with.
 * 
 * @author Johannes Mueller (initial creation)
 * @revised by MuJ: 05.09.2007
 * @version $Revision: 611 $ $LastChangedDate: 2007-11-07 12:04:29 +0100 (Wed, 07 Nov 2007) $ by $Author: jmueller $
 */
public interface DataGathering
{  
    /**
	 * The name of the EJB service.
	 */
    public static String SERVICE_NAME = "ejb/de/mpg/escidoc/services/common/DataGathering";

    /**
	 * For a given affiliation, this method retrieves all affiliation paths. For every affiliation path,
	 * an OrganizationVO is created and filled with the names of the affiliations in the corresponding
	 * affiliation path. The list of OrganizationVOs is returned.
	 * 
     * @param userHandle The user handle of the authenticated user who uses this method
     * @param affiliation The affiliation for which the list of organizationVOs shall be created
     * @return The list of OrganizationVOs
     * @throws TechnicalException 
     * @throws AffiliationNotFoundException 
	 */
    public java.util.List<OrganizationVO> createOrganizationListFromAffiliation(java.lang.String userHandle, AffiliationVO affiliation)
	  throws TechnicalException, AffiliationNotFoundException;

    /**
     * Searches for the revisions of an item usind the semantic store service and the
     * relation isRevisionOf.
     * 
     * @param userHandle The user handle of the authenticated user who uses this method
     * @param pubItemRef The reference to an item for which the revisions will be searched.
     * @throws TechnicalException 
     */
    public java.util.List<RelationVO> findRevisionsOfItem(java.lang.String userHandle, ItemRO pubItemRef) throws TechnicalException;
}