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

package de.mpg.escidoc.services.batchprocess.scripts;

import gov.loc.www.zing.srw.SearchRetrieveRequestType;
import gov.loc.www.zing.srw.SearchRetrieveResponseType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
public class Withdraw
{
    XmlTransforming xmlTransforming = new XmlTransformingBean();
    private static String SUBMIT_TASKPARAM = "<param last-modification-date=\"XXX_DATE_XXX\"><comment>Batch submit</comment></param>";
    private static String RELEASE_TASKPARAM = "<param last-modification-date=\"XXX_DATE_XXX\"><comment>Batch release</comment></param>";

    String SEARCH_QUERY;
    
    private static final String LOCAL_TAG = "LingLit Import 2010-04-01 10:10";

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception
    {
        new Withdraw();
    }
    
    public Withdraw() throws Exception
    {
        System.out.print("Getting handle...");
        System.out.println(PropertyReader.getProperty("escidoc.framework_access.login.url"));
        String handle = AdminHelper.loginUser(PropertyReader.getProperty("escidoc.user.name"), PropertyReader.getProperty("escidoc.user.password"));
        System.out.println("done!");
        
        BufferedReader bufferedReader = new BufferedReader(new FileReader(new File("c:/tmp/ids3.txt")));
        
        ItemHandler itemHandler = ServiceLocator.getItemHandler(handle);
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("HH:mm:ss.SSS");
       
        while ((SEARCH_QUERY = bufferedReader.readLine()) != null)
        {
        
            System.out.println("Getting items...");
            List<ItemVO> list = getListBySearch(10000, handle);
            System.out.println("...done!");
             
            for (ItemVO itemVO : list)
            {
                
                //String item = xmlTransforming.transformToItem(itemVO);
                Date lmd = itemVO.getModificationDate();
                
                System.out.println(itemVO.getVersion().getObjectId());
                
                itemHandler.withdraw(itemVO.getVersion().getObjectId(), "<param last-modification-date=\"" + dateFormat1.format(lmd) + "T" + dateFormat2.format(lmd) + "\">\n<comment>This item has been temporarily withdrawn from this repository until 9th April 2012 as part of an experiment within the Usage Research Study of the PEER project. \nThe article you are requesting is possibly available from other PEER repositories. Please find a listing at http://www.peerproject.eu/about/#repos \nWe apologise for any inconvenience. If you are interested, the reasons for the temporary withdrawal of this item can be found here: http://www.peerproject.eu/peer-research/#usage</comment></param>");
            }
        }
    }

    private List<ItemVO> getListBySearch(int maximumNumberOfElements, String userHandle) throws Exception
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
