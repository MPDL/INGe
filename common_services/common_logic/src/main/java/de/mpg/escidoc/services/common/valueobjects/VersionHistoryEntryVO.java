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

package de.mpg.escidoc.services.common.valueobjects;

import java.util.List;

import de.mpg.escidoc.services.common.referenceobjects.ItemRO;

/**
 * Version information of a publication item.
 * 
 * @version $Revision: 611 $ $LastChangedDate: 2007-11-07 12:04:29 +0100 (Wed, 07 Nov 2007) $ by $Author: jmueller $
 * @created 17-Okt-2007 18:51:45
 */
public class VersionHistoryEntryVO extends ValueObject
{
    /**
     * Fixed serialVersionUID to prevent java.io.InvalidClassExceptions like
     * 'de.mpg.escidoc.services.common.valueobjects.ItemVO; local class incompatible: stream classdesc
     * serialVersionUID = 8587635524303981401, local class serialVersionUID = -2285753348501257286' that occur after
     * JiBX enhancement of VOs. Without the fixed serialVersionUID, the VOs have to be compiled twice for testing (once
     * for the Application Server, once for the local test).
     * 
     * @author Johannes Mueller
     */
 

    /**
     * Reference to the according item.
     */
    private ItemRO reference;
    /**
     * The modification date of the version.
     */
    private java.util.Date modificationDate;
    /**
     * The state of the item version.
     */
    private ItemVO.State state;
    /**
     * The list of events for this history entry.
     */
    private List<EventLogEntryVO> events;

    /**
     * @return Reference to the according item.
     */
    public ItemRO getReference()
    {
        return reference;
    }

    /**
     * @return The modification date of the version.
     */
    public java.util.Date getModificationDate()
    {
        return modificationDate;
    }

    /**
     * @return The state of the item version.
     */
    public ItemVO.State getState()
    {
        return state;
    }

    /**
     * Reference to the according item.
     * 
     * @param newVal
     */
    public void setReference(ItemRO newVal)
    {
        reference = newVal;
    }

    /**
     * The modification date of the version.
     * 
     * @param newVal
     */
    public void setModificationDate(java.util.Date newVal)
    {
        modificationDate = newVal;
    }

    /**
     * Sets the state of the item version.
     * 
     * @param newVal
     */
    public void setState(ItemVO.State newVal)
    {
        state = newVal;
    }

    public List<EventLogEntryVO> getEvents() {
        return events;
    }

    public void setEvents(List<EventLogEntryVO> events) {
        this.events = events;
    }

}