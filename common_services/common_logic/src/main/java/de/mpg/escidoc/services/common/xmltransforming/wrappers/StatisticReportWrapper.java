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
 * Copyright 2006-2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */
package de.mpg.escidoc.services.common.xmltransforming.wrappers;

import java.io.Serializable;
import java.util.List;

import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportRecordVO;

/**
 * This class is used by the XML transforming classes to wrap a statistic report which consists of a list of report-records. The reason for this is that JiBX
 * cannot bind directly to ArrayLists.
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class StatisticReportWrapper implements Serializable
{
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
    protected List<StatisticReportRecordVO> reportRecordVOList;

    /**
     * The id of the report definition this report belongs to
     */
    protected String reportDefinitionId;
    
    /**
     * Unwraps the list of StatisticReportRecordVOs.
     * 
     * @return The list of StatisticReportRecordVOs
     */
    public List<StatisticReportRecordVO> getStatisticReportRecordVOList()
    {
        return reportRecordVOList;
    }

    /**
     * Wraps a list of StatisticReportRecordVOs.
     * 
     * @param statisticReportRecordVOList The list of StatisticReportRecordVOs to wrap
     */
    public void setStatisticReportRecordVOList(List<StatisticReportRecordVO> reportRecordList)
    {
        this.reportRecordVOList = reportRecordList;
    }

    public String getReportDefinitionId()
    {
        return reportDefinitionId;
    }

    public void setReportDefinitionId(String reportDefinitionId)
    {
        this.reportDefinitionId = reportDefinitionId;
    }
}
