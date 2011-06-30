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

package de.mpg.escidoc.services.batchprocess.scripts;

import gov.loc.www.zing.srw.SearchRetrieveRequestType;
import gov.loc.www.zing.srw.SearchRetrieveResponseType;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.axis.types.NonNegativeInteger;

import de.escidoc.www.services.om.ItemHandler;
import de.mpg.escidoc.services.batchprocess.helper.CoreServiceHelper;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.PidTaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO.State;
import de.mpg.escidoc.services.common.xmltransforming.JiBXHelper;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;
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
public class SubmitAndRelease
{
    XmlTransforming xmlTransforming = new XmlTransformingBean();
    private static String SUBMIT_TASKPARAM = "<param last-modification-date=\"XXX_DATE_XXX\"><comment>Batch submit</comment></param>";
    private static String RELEASE_TASKPARAM = "<param last-modification-date=\"XXX_DATE_XXX\"><comment>Batch release</comment></param>";
    
    private static final String LOCAL_TAG = "LingLit Import 2010-04-01 10:10";
    private static final String SEARCH_QUERY = "escidoc.context.objid=\"escidoc:37005\" and escidoc.content-model.objid=\"escidoc:persistent4\"";

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception
    {
        new SubmitAndRelease();
    }
    
    public SubmitAndRelease() throws Exception
    {
        System.out.print("Getting handle...");
        String handle = AdminHelper.loginUser(PropertyReader.getProperty("escidoc.user.name"), PropertyReader.getProperty("escidoc.user.password"));
        System.out.println("done!");
        
        System.out.println("Getting items...");
        List<ItemVO> list = getList(10000, handle);
        System.out.println("...done!");
        
        ItemHandler itemHandler = ServiceLocator.getItemHandler(handle);
        
        for (ItemVO itemVO : list)
        {
            
            itemVO = xmlTransforming.transformToItem(itemHandler.retrieve(itemVO.getLatestVersion().getObjectIdAndVersion()));
            
            if (itemVO.getVersion().getState() == State.PENDING)
            {
                itemVO = submitItem(handle, itemVO);
            }
            
            if (itemVO.getVersion().getState() == State.SUBMITTED)
            {
                itemVO = assignVersionPid(handle, itemVO);
                itemVO = releaseItem(handle, itemVO);
            }
        }
    }

    
    
    private ItemVO assignVersionPid(String handle, ItemVO itemVO) throws Exception
    {
        ItemHandler ih = ServiceLocator.getItemHandler(handle);
        
        String modificationDate = JiBXHelper.serializeDate(itemVO.getModificationDate());

        PidTaskParamVO paramAssignation = new PidTaskParamVO(itemVO.getVersion().getModificationDate(),
                ServiceLocator.getFrameworkUrl() + "/ir/item/" + itemVO.getVersion().getObjectIdAndVersion());
        String paramXml = xmlTransforming.transformToPidTaskParam(paramAssignation);
        try
        {
            modificationDate = ih.assignVersionPid(itemVO.getVersion().getObjectId(), paramXml);
            Pattern pattern = Pattern.compile("last-modification-date=\"([^\"]+)\"");
            Matcher matcher = pattern.matcher(modificationDate);
            if (matcher.find())
            {
                modificationDate = matcher.group(1);
            }
            else
            {
                throw new Exception("No match: " + modificationDate);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        itemVO.getVersion().setModificationDate(JiBXHelper.deserializeDate(modificationDate));
        return itemVO;
    }

    private ItemVO submitItem(String handle, ItemVO itemVO) throws Exception
    {
        ItemHandler ih = ServiceLocator.getItemHandler(handle);

        System.out.print("Submitting item " + itemVO.getVersion().getObjectId() + "...");
        
        String xml = xmlTransforming.transformToItem(itemVO);
        ih.submit(itemVO.getVersion().getObjectId(), SUBMIT_TASKPARAM.replace("XXX_DATE_XXX", JiBXHelper
                .serializeDate(itemVO.getModificationDate())));
        xml = ih.retrieve(itemVO.getVersion().getObjectId());

        System.out.println("done");
        
        return xmlTransforming.transformToItem(xml);
    }

    private ItemVO releaseItem(String handle, ItemVO itemVO) throws Exception
    {
        ItemHandler ih = ServiceLocator.getItemHandler(handle);

        System.out.print("Releasing item " + itemVO.getVersion().getObjectId() + "...");

        String xml = xmlTransforming.transformToItem(itemVO);
        ih.release(itemVO.getVersion().getObjectId(), RELEASE_TASKPARAM.replace("XXX_DATE_XXX", JiBXHelper
                .serializeDate(itemVO.getModificationDate())));
        xml = ih.retrieve(itemVO.getVersion().getObjectId());

        System.out.println("done");
        
        return xmlTransforming.transformToItem(xml);
    }

    private List<ItemVO> getList(int maximumNumberOfElements, String userHandle) throws Exception
    {

        SearchRetrieveRequestType searchRetrieveRequest = new SearchRetrieveRequestType();
        searchRetrieveRequest.setVersion("1.1");
        searchRetrieveRequest.setQuery(SEARCH_QUERY);
        searchRetrieveRequest.setMaximumRecords(new NonNegativeInteger(maximumNumberOfElements + ""));
        searchRetrieveRequest.setRecordPacking("xml");
        SearchRetrieveResponseType searchResult = ServiceLocator.getSearchHandler("escidoc_all").searchRetrieveOperation(searchRetrieveRequest);

        List<ItemVO> list = CoreServiceHelper.transformSearchResultXmlToListOfItemVO(searchResult);
        List<ItemVO> elements = new ArrayList<ItemVO>();
        
        for (ItemVO itemVO : list)
        {
            if (!(itemVO.getLocalTags().contains(LOCAL_TAG)))
            {
                elements.add(itemVO);
            }
        }
        System.out.println(elements.size() + " items found");
        return elements;

    }
}
