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
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class ElementsWithZIPDFFiles extends Elements<ItemVO>
{

    private static final String SEARCH_QUERY = "escidoc.objid=\"escidoc:740902\" or escidoc.objid=\"escidoc:740124\" or escidoc.objid=\"escidoc:740404\" or escidoc.objid=\"escidoc:742082\" or escidoc.objid=\"escidoc:737869\" or escidoc.objid=\"escidoc:739914\" or escidoc.objid=\"escidoc:741664\" or escidoc.objid=\"escidoc:739999\" or escidoc.objid=\"escidoc:738779\" or escidoc.objid=\"escidoc:739385\" or escidoc.objid=\"escidoc:741974\" or escidoc.objid=\"escidoc:737483\" or escidoc.objid=\"escidoc:736914\" or escidoc.objid=\"escidoc:742039\" or escidoc.objid=\"escidoc:739897\" or escidoc.objid=\"escidoc:742025\" or escidoc.objid=\"escidoc:741123\" or escidoc.objid=\"escidoc:738698\" or escidoc.objid=\"escidoc:739408\" or escidoc.objid=\"escidoc:737205\" or escidoc.objid=\"escidoc:739438\" or escidoc.objid=\"escidoc:739152\" or escidoc.objid=\"escidoc:738517\" or escidoc.objid=\"escidoc:741769\" or escidoc.objid=\"escidoc:740153\" or escidoc.objid=\"escidoc:741140\" or escidoc.objid=\"escidoc:740773\" or escidoc.objid=\"escidoc:739549\" or escidoc.objid=\"escidoc:741469\" or escidoc.objid=\"escidoc:738810\" or escidoc.objid=\"escidoc:737839\" or escidoc.objid=\"escidoc:739772\" or escidoc.objid=\"escidoc:742061\" or escidoc.objid=\"escidoc:740630\" or escidoc.objid=\"escidoc:739135\" or escidoc.objid=\"escidoc:740746\" or escidoc.objid=\"escidoc:742057\" or escidoc.objid=\"escidoc:741107\" or escidoc.objid=\"escidoc:741272\" or escidoc.objid=\"escidoc:738365\" or escidoc.objid=\"escidoc:740419\" or escidoc.objid=\"escidoc:739731\" or escidoc.objid=\"escidoc:738001\" or escidoc.objid=\"escidoc:740366\" or escidoc.objid=\"escidoc:741125\" or escidoc.objid=\"escidoc:742027\" or escidoc.objid=\"escidoc:741996\"";

    public ElementsWithZIPDFFiles(String[] args)
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
