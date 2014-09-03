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

    private static final String SEARCH_QUERY = "escidoc.objid=\"escidoc:1296659\" or escidoc.objid=\"escidoc:1301991\" or escidoc.objid=\"escidoc:1693932\" or escidoc.objid=\"escidoc:921865\" or escidoc.objid=\"escidoc:921866\" or escidoc.objid=\"escidoc:922831\" or escidoc.objid=\"escidoc:922077\" or escidoc.objid=\"escidoc:922113\" or escidoc.objid=\"escidoc:922109\" or escidoc.objid=\"escidoc:922118\" or escidoc.objid=\"escidoc:922105\" or escidoc.objid=\"escidoc:923062\" or escidoc.objid=\"escidoc:923097\" or escidoc.objid=\"escidoc:923110\" or escidoc.objid=\"escidoc:923111\" or escidoc.objid=\"escidoc:1859359\" or escidoc.objid=\"escidoc:1478196\" or escidoc.objid=\"escidoc:1296613\" or escidoc.objid=\"escidoc:1296661\" or escidoc.objid=\"escidoc:1296704\" or escidoc.objid=\"escidoc:1301990\" or escidoc.objid=\"escidoc:1301744\" or escidoc.objid=\"escidoc:1686256\" or escidoc.objid=\"escidoc:1686257\" or escidoc.objid=\"escidoc:923587\" or escidoc.objid=\"escidoc:923599\" or escidoc.objid=\"escidoc:923647\" or escidoc.objid=\"escidoc:923627\" or escidoc.objid=\"escidoc:923593\" or escidoc.objid=\"escidoc:921929\" or escidoc.objid=\"escidoc:922278\" or escidoc.objid=\"escidoc:922232\" or escidoc.objid=\"escidoc:922300\" or escidoc.objid=\"escidoc:922247\" or escidoc.objid=\"escidoc:922303\" or escidoc.objid=\"escidoc:922674\" or escidoc.objid=\"escidoc:922663\" or escidoc.objid=\"escidoc:922690\" or escidoc.objid=\"escidoc:923448\" or escidoc.objid=\"escidoc:923439\" or escidoc.objid=\"escidoc:923440\" or escidoc.objid=\"escidoc:923455\" or escidoc.objid=\"escidoc:922833\" or escidoc.objid=\"escidoc:922106\" or escidoc.objid=\"escidoc:923095\" or escidoc.objid=\"escidoc:923061\" or escidoc.objid=\"escidoc:923098\" or escidoc.objid=\"escidoc:923067\" or escidoc.objid=\"escidoc:923226\" or escidoc.objid=\"escidoc:923222\" or escidoc.objid=\"escidoc:923668\" or escidoc.objid=\"escidoc:922523\" or escidoc.objid=\"escidoc:922581\" or escidoc.objid=\"escidoc:921702\" or escidoc.objid=\"escidoc:921700\" or escidoc.objid=\"escidoc:921805\" or escidoc.objid=\"escidoc:923338\" or escidoc.objid=\"escidoc:923268\" or escidoc.objid=\"escidoc:1877537\" or escidoc.objid=\"escidoc:1859358\" or escidoc.objid=\"escidoc:1867958\" or escidoc.objid=\"escidoc:1390559\" or escidoc.objid=\"escidoc:1390581\" or escidoc.objid=\"escidoc:1390573\" or escidoc.objid=\"escidoc:1390574\" or escidoc.objid=\"escidoc:1390584\" or escidoc.objid=\"escidoc:1390604\" or escidoc.objid=\"escidoc:1390599\" or escidoc.objid=\"escidoc:1390595\" or escidoc.objid=\"escidoc:1390568\" or escidoc.objid=\"escidoc:1390571\" or escidoc.objid=\"escidoc:1296612\" or escidoc.objid=\"escidoc:1296644\" or escidoc.objid=\"escidoc:1296693\" or escidoc.objid=\"escidoc:1296689\" or escidoc.objid=\"escidoc:1354647\" or escidoc.objid=\"escidoc:1302071\" or escidoc.objid=\"escidoc:1302067\" or escidoc.objid=\"escidoc:1302068\" or escidoc.objid=\"escidoc:1302072\" or escidoc.objid=\"escidoc:1301992\" or escidoc.objid=\"escidoc:1301989\" or escidoc.objid=\"escidoc:1301714\" or escidoc.objid=\"escidoc:1301715\" or escidoc.objid=\"escidoc:1301716\" or escidoc.objid=\"escidoc:1301713\" or escidoc.objid=\"escidoc:1301711\" or escidoc.objid=\"escidoc:1301746\" or escidoc.objid=\"escidoc:1301745\" or escidoc.objid=\"escidoc:1693928\" or escidoc.objid=\"escidoc:1693937\" or escidoc.objid=\"escidoc:1693939\" or escidoc.objid=\"escidoc:1693900\" or escidoc.objid=\"escidoc:1693881\" or escidoc.objid=\"escidoc:1693963\" or escidoc.objid=\"escidoc:1693901\" or escidoc.objid=\"escidoc:1693904\" or escidoc.objid=\"escidoc:1693961\" or escidoc.objid=\"escidoc:1739387\" or escidoc.objid=\"escidoc:1739376\" or escidoc.objid=\"escidoc:1739364\" or escidoc.objid=\"escidoc:1739313\" or escidoc.objid=\"escidoc:1739317\" or escidoc.objid=\"escidoc:1739969\" or escidoc.objid=\"escidoc:1739963\" or escidoc.objid=\"escidoc:1746532\" or escidoc.objid=\"escidoc:1746543\" or escidoc.objid=\"escidoc:1746533\" or escidoc.objid=\"escidoc:1746763\" or escidoc.objid=\"escidoc:1746767\" or escidoc.objid=\"escidoc:1739409\" or escidoc.objid=\"escidoc:1739453\" or escidoc.objid=\"escidoc:1739719\" or escidoc.objid=\"escidoc:1739771\" or escidoc.objid=\"escidoc:1739757\" or escidoc.objid=\"escidoc:1739750\" or escidoc.objid=\"escidoc:1798479\" or escidoc.objid=\"escidoc:1695436\" or escidoc.objid=\"escidoc:1745858\" or escidoc.objid=\"escidoc:1686344\" or escidoc.objid=\"escidoc:1686325\" or escidoc.objid=\"escidoc:1686320\" or escidoc.objid=\"escidoc:1686341\" or escidoc.objid=\"escidoc:1686260\" or escidoc.objid=\"escidoc:1686248\" or escidoc.objid=\"escidoc:1686255\" or escidoc.objid=\"escidoc:1686243\" or escidoc.objid=\"escidoc:1686184\" or escidoc.objid=\"escidoc:1686174\" or escidoc.objid=\"escidoc:1686291\" or escidoc.objid=\"escidoc:1686251\" or escidoc.objid=\"escidoc:1686181\" or escidoc.objid=\"escidoc:1686199\" or escidoc.objid=\"escidoc:1686217\" or escidoc.objid=\"escidoc:1686236\" or escidoc.objid=\"escidoc:1739858\" or escidoc.objid=\"escidoc:1739915\" or escidoc.objid=\"escidoc:1739863\" or escidoc.objid=\"escidoc:1739882\" or escidoc.objid=\"escidoc:1738023\" or escidoc.objid=\"escidoc:1738037\" or escidoc.objid=\"escidoc:1738055\" or escidoc.objid=\"escidoc:1738038\" or escidoc.objid=\"escidoc:1738019\" or escidoc.objid=\"escidoc:1738012\" or escidoc.objid=\"escidoc:1740088\" or escidoc.objid=\"escidoc:1740087\" or escidoc.objid=\"escidoc:1739202\" or escidoc.objid=\"escidoc:1739212\" or escidoc.objid=\"escidoc:1744168\" or escidoc.objid=\"escidoc:1744161\" or escidoc.objid=\"escidoc:1744157\" or escidoc.objid=\"escidoc:1744158\" or escidoc.objid=\"escidoc:1744111\" or escidoc.objid=\"escidoc:1744107\" or escidoc.objid=\"escidoc:1740106\" or escidoc.objid=\"escidoc:1740121\" or escidoc.objid=\"escidoc:1704745\" or escidoc.objid=\"escidoc:1739476\" or escidoc.objid=\"escidoc:1739469\" or escidoc.objid=\"escidoc:1739486\" or escidoc.objid=\"escidoc:1739489\" or escidoc.objid=\"escidoc:1690319\" or escidoc.objid=\"escidoc:923588\" or escidoc.objid=\"escidoc:923561\" or escidoc.objid=\"escidoc:923556\" or escidoc.objid=\"escidoc:923560\" or escidoc.objid=\"escidoc:923637\" or escidoc.objid=\"escidoc:921827\" or escidoc.objid=\"escidoc:921818\" or escidoc.objid=\"escidoc:921876\" or escidoc.objid=\"escidoc:921868\" or escidoc.objid=\"escidoc:921905\" or escidoc.objid=\"escidoc:921817\" or escidoc.objid=\"escidoc:921816\" or escidoc.objid=\"escidoc:921892\" or escidoc.objid=\"escidoc:921906\" or escidoc.objid=\"escidoc:922364\" or escidoc.objid=\"escidoc:922339\" or escidoc.objid=\"escidoc:922431\" or escidoc.objid=\"escidoc:922363\" or escidoc.objid=\"escidoc:922345\" or escidoc.objid=\"escidoc:922390\" or escidoc.objid=\"escidoc:922424\" or escidoc.objid=\"escidoc:922356\" or escidoc.objid=\"escidoc:922386\" or escidoc.objid=\"escidoc:922388\" or escidoc.objid=\"escidoc:922371\" or escidoc.objid=\"escidoc:922340\" or escidoc.objid=\"escidoc:922209\" or escidoc.objid=\"escidoc:922423\" or escidoc.objid=\"escidoc:922261\" or escidoc.objid=\"escidoc:922275\" or escidoc.objid=\"escidoc:922327\" or escidoc.objid=\"escidoc:922260\" or escidoc.objid=\"escidoc:922213\" or escidoc.objid=\"escidoc:922226\" or escidoc.objid=\"escidoc:922220\" or escidoc.objid=\"escidoc:922308\" or escidoc.objid=\"escidoc:922250\" or escidoc.objid=\"escidoc:922286\" or escidoc.objid=\"escidoc:922672\" or escidoc.objid=\"escidoc:922670\" or escidoc.objid=\"escidoc:922601\" or escidoc.objid=\"escidoc:922701\" or escidoc.objid=\"escidoc:922603\" or escidoc.objid=\"escidoc:922613\" or escidoc.objid=\"escidoc:922711\" or escidoc.objid=\"escidoc:922682\" or escidoc.objid=\"escidoc:922669\" or escidoc.objid=\"escidoc:922655\" or escidoc.objid=\"escidoc:922656\" or escidoc.objid=\"escidoc:923453\" or escidoc.objid=\"escidoc:923425\" or escidoc.objid=\"escidoc:923488\" or escidoc.objid=\"escidoc:923496\" or escidoc.objid=\"escidoc:923490\" or escidoc.objid=\"escidoc:922793\" or escidoc.objid=\"escidoc:923486\" or escidoc.objid=\"escidoc:922818\" or escidoc.objid=\"escidoc:922758\" or escidoc.objid=\"escidoc:922826\" or escidoc.objid=\"escidoc:922810\" or escidoc.objid=\"escidoc:922796\" or escidoc.objid=\"escidoc:922759\" or escidoc.objid=\"escidoc:922799\" or escidoc.objid=\"escidoc:922827\" or escidoc.objid=\"escidoc:922847\" or escidoc.objid=\"escidoc:922176\" or escidoc.objid=\"escidoc:922141\" or escidoc.objid=\"escidoc:922080\" or escidoc.objid=\"escidoc:922151\" or escidoc.objid=\"escidoc:922173\" or escidoc.objid=\"escidoc:922189\" or escidoc.objid=\"escidoc:922183\" or escidoc.objid=\"escidoc:922140\" or escidoc.objid=\"escidoc:922139\" or escidoc.objid=\"escidoc:922158\" or escidoc.objid=\"escidoc:922084\" or escidoc.objid=\"escidoc:922155\" or escidoc.objid=\"escidoc:922174\" or escidoc.objid=\"escidoc:922095\" or escidoc.objid=\"escidoc:922097\" or escidoc.objid=\"escidoc:922991\" or escidoc.objid=\"escidoc:923043\" or escidoc.objid=\"escidoc:923042\" or escidoc.objid=\"escidoc:923117\" or escidoc.objid=\"escidoc:923029\" or escidoc.objid=\"escidoc:923058\" or escidoc.objid=\"escidoc:923069\" or escidoc.objid=\"escidoc:923041\" or escidoc.objid=\"escidoc:922998\" or escidoc.objid=\"escidoc:923032\" or escidoc.objid=\"escidoc:923020\" or escidoc.objid=\"escidoc:923084\" or escidoc.objid=\"escidoc:923112\" or escidoc.objid=\"escidoc:922999\" or escidoc.objid=\"escidoc:922023\" or escidoc.objid=\"escidoc:921991\" or escidoc.objid=\"escidoc:921972\" or escidoc.objid=\"escidoc:922022\" or escidoc.objid=\"escidoc:921986\" or escidoc.objid=\"escidoc:921965\" or escidoc.objid=\"escidoc:921964\" or escidoc.objid=\"escidoc:922029\" or escidoc.objid=\"escidoc:923231\" or escidoc.objid=\"escidoc:921957\" or escidoc.objid=\"escidoc:923190\" or escidoc.objid=\"escidoc:923188\" or escidoc.objid=\"escidoc:923240\" or escidoc.objid=\"escidoc:923237\" or escidoc.objid=\"escidoc:923234\" or escidoc.objid=\"escidoc:923243\" or escidoc.objid=\"escidoc:923249\" or escidoc.objid=\"escidoc:923224\" or escidoc.objid=\"escidoc:923121\" or escidoc.objid=\"escidoc:922884\" or escidoc.objid=\"escidoc:922889\" or escidoc.objid=\"escidoc:922959\" or escidoc.objid=\"escidoc:922944\" or escidoc.objid=\"escidoc:922904\" or escidoc.objid=\"escidoc:922964\" or escidoc.objid=\"escidoc:922943\" or escidoc.objid=\"escidoc:922858\" or escidoc.objid=\"escidoc:922906\" or escidoc.objid=\"escidoc:922971\" or escidoc.objid=\"escidoc:922908\" or escidoc.objid=\"escidoc:923656\" or escidoc.objid=\"escidoc:923662\" or escidoc.objid=\"escidoc:923666\" or escidoc.objid=\"escidoc:923673\" or escidoc.objid=\"escidoc:923690\" or escidoc.objid=\"escidoc:923687\" or escidoc.objid=\"escidoc:922502\" or escidoc.objid=\"escidoc:922519\" or escidoc.objid=\"escidoc:922592\" or escidoc.objid=\"escidoc:922491\" or escidoc.objid=\"escidoc:922500\" or escidoc.objid=\"escidoc:922518\" or escidoc.objid=\"escidoc:922510\" or escidoc.objid=\"escidoc:922587\" or escidoc.objid=\"escidoc:922509\" or escidoc.objid=\"escidoc:922497\" or escidoc.objid=\"escidoc:922487\" or escidoc.objid=\"escidoc:922464\" or escidoc.objid=\"escidoc:922585\" or escidoc.objid=\"escidoc:922539\" or escidoc.objid=\"escidoc:922531\" or escidoc.objid=\"escidoc:921811\" or escidoc.objid=\"escidoc:921810\" or escidoc.objid=\"escidoc:921780\" or escidoc.objid=\"escidoc:921779\" or escidoc.objid=\"escidoc:921727\" or escidoc.objid=\"escidoc:921757\" or escidoc.objid=\"escidoc:921762\" or escidoc.objid=\"escidoc:921812\" or escidoc.objid=\"escidoc:921783\" or escidoc.objid=\"escidoc:921726\" or escidoc.objid=\"escidoc:921760\" or escidoc.objid=\"escidoc:921763\" or escidoc.objid=\"escidoc:923339\" or escidoc.objid=\"escidoc:923335\" or escidoc.objid=\"escidoc:923347\" or escidoc.objid=\"escidoc:923348\" or escidoc.objid=\"escidoc:923351\" or escidoc.objid=\"escidoc:923329\" or escidoc.objid=\"escidoc:923264\" or escidoc.objid=\"escidoc:923273\" or escidoc.objid=\"escidoc:923360\" or escidoc.objid=\"escidoc:923322\" or escidoc.objid=\"escidoc:923260\" or escidoc.objid=\"escidoc:923294\" or escidoc.objid=\"escidoc:923295\" or escidoc.objid=\"escidoc:923305\" or escidoc.objid=\"escidoc:923353\" or escidoc.objid=\"escidoc:923354\" or escidoc.objid=\"escidoc:1810369\" or escidoc.objid=\"escidoc:1877534\" or escidoc.objid=\"escidoc:1877535\" or escidoc.objid=\"escidoc:1877552\" or escidoc.objid=\"escidoc:1477788\" or escidoc.objid=\"escidoc:1477791\" or escidoc.objid=\"escidoc:1477992\" or escidoc.objid=\"escidoc:1478029\" or escidoc.objid=\"escidoc:1350639\" or escidoc.objid=\"escidoc:1877871\" or escidoc.objid=\"escidoc:1878235\" or escidoc.objid=\"escidoc:1878237\" or escidoc.objid=\"escidoc:1878236\" or escidoc.objid=\"escidoc:1900027\" or escidoc.objid=\"escidoc:1849974\" or escidoc.objid=\"escidoc:1851134\" or escidoc.objid=\"escidoc:1854810\" or escidoc.objid=\"escidoc:1854812\" or escidoc.objid=\"escidoc:1854817\" or escidoc.objid=\"escidoc:1859357\" or escidoc.objid=\"escidoc:1859373\" or escidoc.objid=\"escidoc:1859372\" or escidoc.objid=\"escidoc:1859378\" or escidoc.objid=\"escidoc:1859392\" or escidoc.objid=\"escidoc:1859379\" or escidoc.objid=\"escidoc:1867978\" or escidoc.objid=\"escidoc:1867977\" or escidoc.objid=\"escidoc:1867926\" or escidoc.objid=\"escidoc:1867965\" or escidoc.objid=\"escidoc:1867952\" or escidoc.objid=\"escidoc:1867949\" or escidoc.objid=\"escidoc:1867946\" or escidoc.objid=\"escidoc:1868045\" or escidoc.objid=\"escidoc:1868023\" or escidoc.objid=\"escidoc:1868021\" or escidoc.objid=\"escidoc:1868018\" or escidoc.objid=\"escidoc:1868084\" or escidoc.objid=\"escidoc:1868087\" or escidoc.objid=\"escidoc:1867988\" or escidoc.objid=\"escidoc:1868004\" or escidoc.objid=\"escidoc:1868010\" or escidoc.objid=\"escidoc:1868056\" or escidoc.objid=\"escidoc:1868057\" or escidoc.objid=\"escidoc:1868054\" or escidoc.objid=\"escidoc:1868042\" or escidoc.objid=\"escidoc:1868048\" or escidoc.objid=\"escidoc:1909957\" or escidoc.objid=\"escidoc:1913435\" or escidoc.objid=\"escidoc:1913842\" or escidoc.objid=\"escidoc:1921811\" or escidoc.objid=\"escidoc:1932928\" or escidoc.objid=\"escidoc:1932910\" or escidoc.objid=\"escidoc:1932918\" or escidoc.objid=\"escidoc:1933140\" or escidoc.objid=\"escidoc:1933141\" or escidoc.objid=\"escidoc:1976622\" or escidoc.objid=\"escidoc:1977156\" or escidoc.objid=\"escidoc:2019225\" or escidoc.objid=\"escidoc:2019231\" or escidoc.objid=\"escidoc:1877503\" or escidoc.objid=\"escidoc:1301712\" or escidoc.objid=\"escidoc:921931\" or escidoc.objid=\"escidoc:1921807\" or escidoc.objid=\"escidoc:1859387\" or escidoc.objid=\"escidoc:1921849\" or escidoc.objid=\"escidoc:1867936\" ";

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

