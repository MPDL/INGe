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

package de.mpg.escidoc.services.batchprocess.elements;

import de.escidoc.www.services.om.ItemHandler;
import de.mpg.escidoc.services.batchprocess.BatchProcessReport.ReportEntryStatusType;
import de.mpg.escidoc.services.batchprocess.helper.CoreServiceHelper;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
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
public class BPCImportElements extends Elements<ItemVO>
{

    //private static final String LOCAL_TAG = "BPC IMPORT PART1 2010-12-16 09:30";
    //private static final String LOCAL_TAG = "BPC IMPORT PART2 2010-12-21 11:15";
    //private static final String LOCAL_TAG = "BPC IMPORT PART3 2010-12-21 14:11";
    //private static final String LOCAL_TAG = "BPC IMPORT PART4 2010-12-21 15:44";
    //private static final String LOCAL_TAG = "BPC IMPORT PART5 2010-12-21 16:36";
    //private static final String LOCAL_TAG = "BPC IMPORT PART6 2010-12-21 17:37";
    //private static final String LOCAL_TAG = "Nachimport_BPC_1 2011-01-18 09:39";
    private static final String LOCAL_TAG = "Nachimport_BPC%";

    public BPCImportElements(String[] args)
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
            elements.addAll(CoreServiceHelper.transformSearchResultXmlToListOfItemVO(seachResultXml));
            report.addEntry("retrieveElements", "Get Data", ReportEntryStatusType.FINE);
            System.out.println(elements.size() + " items found");
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error initializing BPCImportElements: ", e);
        }
    }

    @Override
    public CoreServiceObjectType getObjectType()
    {
        return CoreServiceObjectType.ITEM;
    }
    
}
