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
* Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.services.batchprocess.elements;

import java.util.List;

import de.escidoc.www.services.om.ItemHandler;
import de.mpg.escidoc.services.batchprocess.BatchProcessReport.ReportEntryStatusType;
import de.mpg.escidoc.services.batchprocess.helper.CoreServiceHelper;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.framework.AdminHelper;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class ElementsWithLocalTag extends Elements<ItemVO>
{

    private static final String LOCAL_TAG = "eDoc-Migration 2002-2007 2011-09-23 12:59";

    public ElementsWithLocalTag(String[] args)
    {
        super(args);
    }

    @Override
    public void init(String[] args)
    {
        try
        {
            setUserHandle(AdminHelper.loginUser(PropertyReader.getProperty("escidoc.user.name"), PropertyReader.getProperty("escidoc.user.password")));
        }
        catch (Exception e)
        {
            throw new RuntimeException("Login error. Please make sure the user credentials (escidoc.user.name, escidoc.user.password) are provided in your settings.xml file." + e);
        }
    }

    @Override
    public void retrieveElements()
    {
        try
        {
            ItemHandler ih = ServiceLocator.getItemHandler(this.getUserHandle());
            String seachResultXml = ih.retrieveItems(CoreServiceHelper.createBasicFilter(
                    "\"/properties/content-model-specific/local-tags/local-tag\"=\"" + LOCAL_TAG + "\"",
                    maximumNumberOfElements));
            List<ItemVO> list = CoreServiceHelper.transformSearchResultXmlToListOfItemVO(seachResultXml);
            for (ItemVO itemVO : list)
            {
                    elements.add(itemVO);
            }
            report.addEntry("retrieveElements", "Get Data", ReportEntryStatusType.FINE);
            System.out.println(elements.size() + " items found");
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error initializing Elements: ", e);
        }
    }

    @Override
    public CoreServiceObjectType getObjectType()
    {
        return CoreServiceObjectType.ITEM;
    }
    
}
