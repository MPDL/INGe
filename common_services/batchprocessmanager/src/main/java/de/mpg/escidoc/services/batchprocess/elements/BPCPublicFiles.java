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

import gov.loc.www.zing.srw.SearchRetrieveRequestType;
import gov.loc.www.zing.srw.SearchRetrieveResponseType;

import org.apache.axis.types.NonNegativeInteger;

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
public class BPCPublicFiles extends Elements<ItemVO>
{


    public BPCPublicFiles(String[] args)
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
    
    private static final String QUERY = "escidoc.objid=\"escidoc:641589\" or escidoc.objid=\"escidoc:641599\" or escidoc.objid=\"escidoc:598347\" or escidoc.objid=\"escidoc:598345\" or escidoc.objid=\"escidoc:641614\" or escidoc.objid=\"escidoc:641608\" or escidoc.objid=\"escidoc:641627\" or escidoc.objid=\"escidoc:641648\" or escidoc.objid=\"escidoc:641689\" or escidoc.objid=\"escidoc:641619\" or escidoc.objid=\"escidoc:641625\" or escidoc.objid=\"escidoc:641621\" or escidoc.objid=\"escidoc:641631\" or escidoc.objid=\"escidoc:641633\" or escidoc.objid=\"escidoc:641686\" or escidoc.objid=\"escidoc:641638\" or escidoc.objid=\"escidoc:597657\" or escidoc.objid=\"escidoc:587999\" or escidoc.objid=\"escidoc:641716\" or escidoc.objid=\"escidoc:641642\" or escidoc.objid=\"escidoc:641646\" or escidoc.objid=\"escidoc:641680\" or escidoc.objid=\"escidoc:641652\" or escidoc.objid=\"escidoc:641644\" or escidoc.objid=\"escidoc:641656\" or escidoc.objid=\"escidoc:641654\" or escidoc.objid=\"escidoc:596006\" or escidoc.objid=\"escidoc:598970\" or escidoc.objid=\"escidoc:597650\" or escidoc.objid=\"escidoc:597648\" or escidoc.objid=\"escidoc:596002\" or escidoc.objid=\"escidoc:641676\" or escidoc.objid=\"escidoc:641660\" or escidoc.objid=\"escidoc:641712\" or escidoc.objid=\"escidoc:641674\" or escidoc.objid=\"escidoc:597847\" or escidoc.objid=\"escidoc:641668\" or escidoc.objid=\"escidoc:641710\" or escidoc.objid=\"escidoc:588131\" or escidoc.objid=\"escidoc:641706\" or escidoc.objid=\"escidoc:641666\" or escidoc.objid=\"escidoc:641704\" or escidoc.objid=\"escidoc:598939\" or escidoc.objid=\"escidoc:597612\" or escidoc.objid=\"escidoc:597560\" or escidoc.objid=\"escidoc:598672\"";
    
    @Override
    public void retrieveElements()
    {
        try
        {

            SearchRetrieveRequestType searchRetrieveRequest = new SearchRetrieveRequestType();
            searchRetrieveRequest.setVersion("1.1");
            searchRetrieveRequest.setQuery(QUERY);
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
