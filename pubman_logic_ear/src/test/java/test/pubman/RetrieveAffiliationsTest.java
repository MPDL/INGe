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

package test.pubman;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import test.pubman.depositing.PubItemDepositingTest;
import de.escidoc.www.services.oum.OrganizationalUnitHandler;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.Filter;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Tests the retrieval of Affiliations.
 * 
 * @author Johannes Mueller (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class RetrieveAffiliationsTest
{
    /**
     * Logger for this class.
     */
    private static final Logger logger = Logger.getLogger(PubItemDepositingTest.class);

    private XmlTransformingBean xmlTransforming;

    @Before
    public void setUp()
    {
        xmlTransforming = new XmlTransformingBean();
    }

    @Test
    public void testRetrieveAllTopLevelAffiliations() throws Exception
    {
        logger.info("Using framework: " + ServiceLocator.getFrameworkUrl());

        FilterTaskParamVO filter = new FilterTaskParamVO();
        List<Filter> filterList = filter.getFilterList();
        filterList.add(filter.new TopLevelAffiliationFilter());
        String filterXml = xmlTransforming.transformToFilterTaskParam(filter);

        OrganizationalUnitHandler ouh = ServiceLocator.getOrganizationalUnitHandler();
        String ousXml = ouh.retrieveOrganizationalUnits(filterXml);
        logger.info(ousXml);

        List<AffiliationVO> affiliations = xmlTransforming.transformToAffiliationList(ousXml);
        logger.info("There are " + affiliations.size() + " top-level affiliations on " + ServiceLocator.getFrameworkUrl() + ": ");
        for (AffiliationVO affiliation:affiliations)
        {
            logger.info("MD: " + affiliation.getMetadataSets());
        }
    }

}
