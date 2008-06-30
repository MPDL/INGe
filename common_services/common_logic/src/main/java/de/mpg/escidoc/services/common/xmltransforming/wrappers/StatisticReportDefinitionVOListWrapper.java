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

import java.util.List;

import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportDefinitionVO;

/**
 * This class is used by the XML transforming classes to wrap a statistic report definition list which consists of a list report-definitions. The reason for this is that JiBX
 * cannot bind directly to ArrayLists.
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class StatisticReportDefinitionVOListWrapper
{
    
    private List<StatisticReportDefinitionVO> repDefList;

    public List<StatisticReportDefinitionVO> getRepDefList()
    {
        return repDefList;
    }

    public void setRepDefList(List<StatisticReportDefinitionVO> repDefList)
    {
        this.repDefList = repDefList;
    }
    
}
