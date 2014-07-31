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
* or http://www.escidoc.org/license.
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

import gov.loc.www.zing.srw.SearchRetrieveRequestType;
import gov.loc.www.zing.srw.SearchRetrieveResponseType;

import java.util.List;

import org.apache.axis.types.NonNegativeInteger;

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
 * TODO Class retrieves Elements via search by ID
 *
 * @author walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class ElementsById extends Elements<ItemVO>
{

    private static final String SEARCH_QUERY = "escidoc.objid=\"escidoc:1756460\" or escidoc.objid=\"escidoc:1756608\" or escidoc.objid=\"escidoc:1756777\" or escidoc.objid=\"escidoc:1756890\" or escidoc.objid=\"escidoc:1757220\" or escidoc.objid=\"escidoc:1757228\" or escidoc.objid=\"escidoc:1757321\" or escidoc.objid=\"escidoc:1757341\" or escidoc.objid=\"escidoc:1757342\" or escidoc.objid=\"escidoc:1757343\" or escidoc.objid=\"escidoc:1757390\" or escidoc.objid=\"escidoc:1757391\" or escidoc.objid=\"escidoc:1757468\" or escidoc.objid=\"escidoc:1757546\" or escidoc.objid=\"escidoc:1757563\" or escidoc.objid=\"escidoc:1757644\" or escidoc.objid=\"escidoc:1757650\" or escidoc.objid=\"escidoc:1757651\" or escidoc.objid=\"escidoc:1757656\" or escidoc.objid=\"escidoc:1757658\" or escidoc.objid=\"escidoc:1757669\" or escidoc.objid=\"escidoc:1757671\" or escidoc.objid=\"escidoc:1757674\" or escidoc.objid=\"escidoc:1757717\" or escidoc.objid=\"escidoc:1757730\" or escidoc.objid=\"escidoc:1757741\" or escidoc.objid=\"escidoc:1757780\" or escidoc.objid=\"escidoc:1757781\" or escidoc.objid=\"escidoc:1757782\" or escidoc.objid=\"escidoc:1757783\" or escidoc.objid=\"escidoc:1757784\" or escidoc.objid=\"escidoc:1757789\" or escidoc.objid=\"escidoc:1757886\" or escidoc.objid=\"escidoc:1757958\" or escidoc.objid=\"escidoc:1757959\" or escidoc.objid=\"escidoc:1757960\" or escidoc.objid=\"escidoc:1757961\" or escidoc.objid=\"escidoc:1757962\" or escidoc.objid=\"escidoc:1757963\" or escidoc.objid=\"escidoc:1757964\" or escidoc.objid=\"escidoc:1757965\" or escidoc.objid=\"escidoc:1757966\" or escidoc.objid=\"escidoc:1758038\" or escidoc.objid=\"escidoc:1758039\" or escidoc.objid=\"escidoc:1758061\" or escidoc.objid=\"escidoc:1758065\" or escidoc.objid=\"escidoc:1758067\" or escidoc.objid=\"escidoc:1758091\" or escidoc.objid=\"escidoc:1758093\" or escidoc.objid=\"escidoc:1758095\" or escidoc.objid=\"escidoc:1758096\" or escidoc.objid=\"escidoc:1758107\" or escidoc.objid=\"escidoc:1758109\" or escidoc.objid=\"escidoc:1758116\" or escidoc.objid=\"escidoc:1758118\" or escidoc.objid=\"escidoc:1758130\" or escidoc.objid=\"escidoc:1758186\" or escidoc.objid=\"escidoc:1758189\" or escidoc.objid=\"escidoc:1758191\" or escidoc.objid=\"escidoc:1758193\" or escidoc.objid=\"escidoc:1758194\" or escidoc.objid=\"escidoc:1758200\" or escidoc.objid=\"escidoc:1758201\" or escidoc.objid=\"escidoc:1758203\" or escidoc.objid=\"escidoc:1758205\" or escidoc.objid=\"escidoc:1758206\" or escidoc.objid=\"escidoc:1758208\" or escidoc.objid=\"escidoc:1758210\" or escidoc.objid=\"escidoc:1758212\" or escidoc.objid=\"escidoc:1758280\" or escidoc.objid=\"escidoc:1758285\" or escidoc.objid=\"escidoc:1758286\" or escidoc.objid=\"escidoc:1758287\" or escidoc.objid=\"escidoc:1758288\" or escidoc.objid=\"escidoc:1758334\" or escidoc.objid=\"escidoc:1758337\" or escidoc.objid=\"escidoc:1758338\" or escidoc.objid=\"escidoc:1758339\" or escidoc.objid=\"escidoc:1758340\" or escidoc.objid=\"escidoc:1758341\" or escidoc.objid=\"escidoc:1758342\" or escidoc.objid=\"escidoc:1758343\"";

    public ElementsById(String[] args)
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

            SearchRetrieveRequestType searchRetrieveRequest = new SearchRetrieveRequestType();
            searchRetrieveRequest.setVersion("1.1");
            searchRetrieveRequest.setQuery(SEARCH_QUERY);
            searchRetrieveRequest.setMaximumRecords(new NonNegativeInteger(maximumNumberOfElements + ""));
            searchRetrieveRequest.setRecordPacking("xml");
            SearchRetrieveResponseType searchResult = ServiceLocator.getSearchHandler("escidoc_all").searchRetrieveOperation(searchRetrieveRequest);

            elements.addAll(CoreServiceHelper.transformSearchResultXmlToListOfItemVO(searchResult));
            report.addEntry("retrieveElements", "Get Data", ReportEntryStatusType.FINE);
            System.out.println(elements.size() + " items found");
            
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error initializing LingLitAllElements.java: ", e);
        }
    }

    @Override
    public CoreServiceObjectType getObjectType()
    {
        return CoreServiceObjectType.ITEM;
    }
    
}

