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

import de.mpg.escidoc.services.common.valueobjects.ItemVO;

/**
 * This class is used by the XML transforming classes to wrap a list of PubItemVOs. The reason for this is that JiBX
 * cannot bind directly to ArrayLists.
 * 
 * @author Johannes Mueller (initial creation)
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @revised by MuJ: 13.08.2007
 */
public class ItemVOListWrapper implements Serializable
{
    private String numberOfRecords;
    private String limit;
    private String offset;
    
    /**
     * Fixed serialVersionUID to prevent java.io.InvalidClassExceptions like
     * 'de.mpg.escidoc.services.common.valueobjects.ItemVO; local class incompatible: stream classdesc
     * serialVersionUID = 8587635524303981401, local class serialVersionUID = -2285753348501257286' that occur after
     * JiBX enhancement of VOs. Without the fixed serialVersionUID, the VOs have to be compiled twice for testing (once
     * for the Application Server, once for the local test).
     * 
     * @author Johannes Mueller
     */
    private static final long serialVersionUID = 1L;
    /**
     * The wrapped list of PubItemVOs.
     */
    protected List<? extends ItemVO> itemVOList;

    /**
     * Unwraps the list of PubItemVOs.
     * 
     * @return The list of PubItemVOs
     */
    public List<? extends ItemVO> getItemVOList()
    {
        return itemVOList;
    }

    /**
     * Wraps a list of PubItemVOs.
     * 
     * @param itemVOList The list of PubItemVOs to wrap
     */
    public void setItemVOList(List<? extends ItemVO> itemVOList)
    {
        this.itemVOList = itemVOList;
    }

    public void setNumberOfRecords(String numberOfRecords)
    {
        this.numberOfRecords = numberOfRecords;
    }

    public String getNumberOfRecords()
    {
        return numberOfRecords;
    }

    public void setLimit(String limit)
    {
        this.limit = limit;
    }

    public String getLimit()
    {
        return limit;
    }

    public void setOffset(String offset)
    {
        this.offset = offset;
    }

    public String getOffset()
    {
        return offset;
    }
    
   
}
