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

import gov.loc.www.zing.srw.RecordType;
import gov.loc.www.zing.srw.SearchRetrieveRequestType;
import gov.loc.www.zing.srw.SearchRetrieveResponseType;
import gov.loc.www.zing.srw.StringOrXmlFragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.axis.message.MessageElement;
import org.apache.axis.types.NonNegativeInteger;

import de.mpg.escidoc.services.batchprocess.BatchProcessReport.ReportEntryStatusType;
import de.mpg.escidoc.services.batchprocess.helper.CoreServiceHelper;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.interfaces.SearchResultElement;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;
import de.mpg.escidoc.services.framework.AdminHelper;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

public class ElementsWithNonPublicFiles extends Elements<ItemVO>
{
    
    private XmlTransforming xmlTransforming = new XmlTransformingBean();
    
    public ElementsWithNonPublicFiles(String[] args)
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

//    private static final String QUERY = "((escidoc.objecttype=\"item\") and ((escidoc.context.objid=\"escidoc:644575\") and (( escidoc.component.creation-date>\"''\") and (( escidoc.component.visibility=\"private\" ) not (escidoc.component.visibility=\"audience\"))))) and (escidoc.content-model.objid=\"escidoc:persistent4\")";
    private static final String QUERY = "escidoc.objid=\"escidoc:720006\" or escidoc.objid=\"escidoc:734486\" or escidoc.objid=\"escidoc:734497\" or escidoc.objid=\"escidoc:735552\" or escidoc.objid=\"escidoc:735776\" or escidoc.objid=\"escidoc:735864\" or escidoc.objid=\"escidoc:735926\" or escidoc.objid=\"escidoc:736009\" or escidoc.objid=\"escidoc:736055\" or escidoc.objid=\"escidoc:736261\" or escidoc.objid=\"escidoc:736327\" or escidoc.objid=\"escidoc:736646\" or escidoc.objid=\"escidoc:736766\" or escidoc.objid=\"escidoc:736774\" or escidoc.objid=\"escidoc:736835\" or escidoc.objid=\"escidoc:736858\" or escidoc.objid=\"escidoc:736875\" or escidoc.objid=\"escidoc:736901\" or escidoc.objid=\"escidoc:736919\" or escidoc.objid=\"escidoc:736923\" or escidoc.objid=\"escidoc:736930\" or escidoc.objid=\"escidoc:736932\" or escidoc.objid=\"escidoc:736934\" or escidoc.objid=\"escidoc:736936\" or escidoc.objid=\"escidoc:736938\" or escidoc.objid=\"escidoc:736940\" or escidoc.objid=\"escidoc:736942\" or escidoc.objid=\"escidoc:736944\" or escidoc.objid=\"escidoc:737027\" or escidoc.objid=\"escidoc:737041\" or escidoc.objid=\"escidoc:737055\" or escidoc.objid=\"escidoc:737095\" or escidoc.objid=\"escidoc:737192\" or escidoc.objid=\"escidoc:737278\" or escidoc.objid=\"escidoc:737290\" or escidoc.objid=\"escidoc:737332\" or escidoc.objid=\"escidoc:737449\" or escidoc.objid=\"escidoc:737460\" or escidoc.objid=\"escidoc:737562\" or escidoc.objid=\"escidoc:737698\" or escidoc.objid=\"escidoc:737763\" or escidoc.objid=\"escidoc:737855\" or escidoc.objid=\"escidoc:738268\" or escidoc.objid=\"escidoc:738297\" or escidoc.objid=\"escidoc:741105\"";
    
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

    public List<ItemVO> getElements()
    {
        return elements;
    }

    @Override
    public CoreServiceObjectType getObjectType()
    {
        return CoreServiceObjectType.ITEM;
    }
    
    private List<SearchResultElement> transformToSearchResultList(
            SearchRetrieveResponseType searchResult) throws TechnicalException
    {

        ArrayList<SearchResultElement> resultList = new ArrayList<SearchResultElement>();
        if (searchResult.getRecords() != null)
        {
            for (RecordType record : searchResult.getRecords().getRecord())
            {
                StringOrXmlFragment data = record.getRecordData();
                MessageElement[] messages = data.get_any();
                // Data is in the first record
                if (messages.length == 1)
                {
                    String searchResultItem = null;
                    try
                    {
                        searchResultItem = messages[0].getAsString();
                    }
                    catch (Exception e) 
                    {
                        throw new TechnicalException("Error getting search result message.", e);
                    }
                    SearchResultElement itemResult = xmlTransforming.transformToSearchResult(searchResultItem);
                    resultList.add(itemResult);
                }
            }
        }
        return resultList;
    }
}
