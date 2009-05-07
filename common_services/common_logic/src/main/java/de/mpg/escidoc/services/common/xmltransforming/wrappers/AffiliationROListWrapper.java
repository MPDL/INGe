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

package de.mpg.escidoc.services.common.xmltransforming.wrappers;

import java.io.Serializable;
import java.util.List;

import de.mpg.escidoc.services.common.referenceobjects.AffiliationRO;

/**
 * This class is used by the XML transforming classes to wrap a list of AffiliationROs.
 * The reason for this is that JiBX cannot bind directly to ArrayLists.
 *
 * @author Michael Franke (initial creation)
 * @version $Revision$ $LastChangedDate$ by $Author$
 */
public class AffiliationROListWrapper implements Serializable
{    
    
    public static final long serialVersionUID = 1L;
    
    /**
     * The wrapped list of AffiliationROs.
     */
    private List<AffiliationRO> affiliationROList;

    /**
     * Unwraps the list of AffiliationROs.
     * 
     * @return The list of AffiliationROs
     */
    public List<AffiliationRO> getAffiliationROList()
    {
        return affiliationROList;
    }

    /**
     * Wraps a list of AffiliationROs.
     * 
     * @param AffiliationROList The list of AffiliationROs to wrap
     */
    public void setAffiliationROList(List<AffiliationRO> AffiliationROList)
    {
        this.affiliationROList = AffiliationROList;
    }
}
