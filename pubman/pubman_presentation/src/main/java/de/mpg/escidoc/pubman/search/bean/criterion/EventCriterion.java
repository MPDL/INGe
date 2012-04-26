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
* Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman.search.bean.criterion;

import java.util.ArrayList;
import java.util.List;

import net.sf.saxon.event.MetaTagAdjuster;

import de.mpg.escidoc.pubman.search.bean.criterion.DateCriterion.DateType;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion.CriterionType;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion.LogicalOperator;

/**
 * event criterion vo for the advanced search.
 * @created 15-Mai-2007 15:46:31
 * @author NiH
 * @version 1.0
 * Revised by NiH: 13.09.2007
 */
public class EventCriterion extends Criterion
{
    private final static String  INVITATION_SEARCH = "invited";
    private boolean invitationStatus = false;
    
    /**
     * constructor.
     */
    public EventCriterion()
    {
        super();
    }
    
    public void setInvitationStatus(boolean invitationStatus)
    {
        this.invitationStatus = invitationStatus;
    }
    
    public boolean getInvitationStatus()
    {
        return this.invitationStatus;
    }
    
    /**
     * {@inheritDoc}
     */
    public ArrayList<MetadataSearchCriterion> createSearchCriterion() throws TechnicalException {
        ArrayList<MetadataSearchCriterion> criterions = new ArrayList<MetadataSearchCriterion>();
        MetadataSearchCriterion criterion = 
            new MetadataSearchCriterion( CriterionType.EVENT, getSearchString() );
        if (getInvitationStatus())
        {
            criterion.addSubCriteria(new MetadataSearchCriterion(CriterionType.EVENT_INVITATION_STATUS, EventCriterion.INVITATION_SEARCH, LogicalOperator.AND));
        }
        
        criterions.add( criterion );
           return criterions;
    }    
}