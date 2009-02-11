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

import de.mpg.escidoc.services.common.valueobjects.ContainerVO;

/**
 * This class is used by the XML transforming classes to wrap a list of ContainerVOs. The reason for this is that JiBX
 * cannot bind directly to ArrayLists.
 * 
 * @author Wilhelm Frank (initial creation)
 * @version 1.0
 */
public class ContainerVOListWrapper implements Serializable
{
    private String numberOfRecords;
    private String limit;
    private String offset;
    
    /**
     * Fixed serialVersionUID to prevent java.io.InvalidClassExceptions like
     * 'de.mpg.escidoc.services.common.valueobjects.ContainerVO; local class incompatible: stream classdesc
     * serialVersionUID = 8587635524303981401, local class serialVersionUID = -2285753348501257286' that occur after
     * JiBX enhancement of VOs. Without the fixed serialVersionUID, the VOs have to be compiled twice for testing (once
     * for the Application Server, once for the local test).
     */
    private static final long serialVersionUID = 1L;
    /**
     * The wrapped list of ContainerVOs.
     */
    protected List<? extends ContainerVO> containerVOList;

    /**
     * Unwraps the list of ContainerVOs.
     * 
     * @return The list of ContainerVOs
     */
    public List<? extends ContainerVO> getContainerVOList()
    {
        return containerVOList;
    }

    /**
     * Wraps a list of ContainerVOs.
     * 
     * @param containerVOList The list of ContainerVOs to wrap
     */
    public void setContainerVOList(List<? extends ContainerVO> containerVOList)
    {
        this.containerVOList = containerVOList;
    }

    public String getNumberOfRecords()
    {
        return numberOfRecords;
    }

    public void setNumberOfRecords(String numberOfRecords)
    {
        this.numberOfRecords = numberOfRecords;
    }

    public String getLimit()
    {
        return limit;
    }

    public void setLimit(String limit)
    {
        this.limit = limit;
    }

    public String getOffset()
    {
        return offset;
    }

    public void setOffset(String offset)
    {
        this.offset = offset;
    }
}
