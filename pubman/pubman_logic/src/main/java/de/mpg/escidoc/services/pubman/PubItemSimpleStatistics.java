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
* Copyright 2006-2008 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/
package de.mpg.escidoc.services.pubman;

import java.util.List;

import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportRecordVO;


/**
 * Service Interface for the retrieval of simple statistic records according to fixed statistic record definitions. 
 * The statistic database system from the framework must contain the following report definitions. If they are not available, they
 * are automatically created by the <code>InitStatistic</code> class.
 * The report definitions retrieve statistic values for an item AND its different versions.
 * 
 * <?xml version="1.0" encoding="UTF-8"?>
<report-definition-list xmlns="http://www.escidoc.de/schemas/reportdefinitionlist/0.3">
    
    <report-definition xmlns="http://www.escidoc.de/schemas/reportdefinition/0.3" objid="1">
        <name>Successful Framework Requests</name>
        <scope objid="1"/>
        <sql>       select      handler, request, day, month, year, sum(requests)       from _1_request_statistics      group by handler, request, day, month, year;    </sql>
    </report-definition>
    
    <report-definition xmlns="http://www.escidoc.de/schemas/reportdefinition/0.3" objid="2">
        <name>Unsuccessful Framework Requests</name>
        <scope objid="1"/>
        <sql>       select *        from _2_error_statistics;   </sql>
    </report-definition>
    
    <report-definition xmlns="http://www.escidoc.de/schemas/reportdefinition/0.3" objid="3">
        <name>Successful Framework Requests by Month and Year</name>
        <scope objid="1"/>
        <sql>       select *        from _1_request_statistics      where month = {month} and year = {year};    </sql>
    </report-definition>
   
    <report-definition xmlns="http://www.escidoc.de/schemas/reportdefinition/0.3" objid="4">
        <name>Item retrievals, all users</name>
        <scope objid="2"/>
        <sql>select object_id as itemId, sum(requests) as itemRequests from _1_object_statistics where (object_id = {object_id} OR object_id LIKE {object_id} || ':%') and handler='de.escidoc.core.om.service.ItemHandler' and request='retrieve' group by object_id;</sql>
    </report-definition>

    <report-definition xmlns="http://www.escidoc.de/schemas/reportdefinition/0.3" objid="5">
        <name>File downloads per Item, all users</name>
        <scope objid="2"/>
        <sql>select parent_object_id as itemId, sum(requests) as fileRequests from _1_object_statistics where (parent_object_id = {object_id} OR parent_object_id LIKE {object_id} || ':%')  and handler='de.escidoc.core.om.service.ItemHandler' and request='retrieveContent' group by parent_object_id;</sql>
    </report-definition>
    
    <report-definition xmlns="http://www.escidoc.de/schemas/reportdefinition/0.3" objid="6">
        <name>File downloads, all users</name>
        <scope objid="2"/>
        <sql>select object_id as fileId, sum(requests) as fileRequests from _1_object_statistics where (object_id = {object_id} OR object_id LIKE {object_id} || ':%')  and handler='de.escidoc.core.om.service.ItemHandler' and request='retrieveContent' group by object_id;</sql>
    </report-definition>
    
    <report-definition xmlns="http://www.escidoc.de/schemas/reportdefinition/0.3" objid="7">
        <name>Item retrievals, anonymous users</name>
        <scope objid="2"/>
        <sql>select object_id as itemId, sum(requests) as itemRequests from _1_object_statistics where (object_id = {object_id} OR object_id LIKE {object_id} || ':%')  and handler='de.escidoc.core.om.service.ItemHandler' and request='retrieve' and user_id='' group by object_id;</sql>
    </report-definition>
    
    <report-definition xmlns="http://www.escidoc.de/schemas/reportdefinition/0.3" objid="8">
        <name>File downloads per Item, anonymous users</name>
        <scope objid="2"/>
        <sql>select parent_object_id as itemId, sum(requests) as fileRequests from _1_object_statistics where (parent_object_id = {object_id} OR parent_object_id LIKE {object_id} || ':%') and handler='de.escidoc.core.om.service.ItemHandler' and request='retrieveContent' and user_id='' group by parent_object_id;</sql>
    </report-definition>
    
    <report-definition xmlns="http://www.escidoc.de/schemas/reportdefinition/0.3" objid="9">
        <name>File downloads, anonymous users</name>
        <scope objid="2"/>
        <sql>select object_id as fileId, sum(requests) as fileRequests from _1_object_statistics where (object_id = {object_id} OR object_id LIKE {object_id} || ':%') and handler='de.escidoc.core.om.service.ItemHandler' and request='retrieveContent' and user_id='' group by object_id;</sql>
    </report-definition>
</report-definition-list>
 *
 *
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */

public interface PubItemSimpleStatistics
{
    /**
     * The service name
     */
    public static final String SERVICE_NAME = "ejb/de/mpg/escidoc/services/pubman/PubItemSimpleStatistics";
    
    
    
    public static final String REPORTDEFINITION_NUMBER_OF_ITEM_RETRIEVALS_ALL_USERS = "select month, year, sum(requests) as itemRequests from _1_object_statistics where (object_id = {object_id} OR object_id LIKE {object_id} || ':%') and handler='de.escidoc.core.om.service.ItemHandler' and request='retrieve' group by month, year;";
    public static final String REPORTDEFINITION_FILE_DOWNLOADS_PER_ITEM_ALL_USERS = "select parent_object_id as itemId, sum(requests) as fileRequests from _1_object_statistics where (parent_object_id = {object_id} OR parent_object_id LIKE {object_id} || ':%')  and handler='de.escidoc.core.om.service.ItemHandler' and request='retrieveContent' group by parent_object_id;";
    public static final String REPORTDEFINITION_FILE_DOWNLOADS_PER_FILE_ALL_USERS = "select object_id as fileId, sum(requests) as fileRequests from _1_object_statistics where (object_id = {object_id} OR object_id LIKE {object_id} || ':%')  and handler='de.escidoc.core.om.service.ItemHandler' and request='retrieveContent' group by object_id;";
    public static final String REPORTDEFINITION_NUMBER_OF_ITEM_RETRIEVALS_ANONYMOUS = "select month, year, sum(requests) as itemRequests from _1_object_statistics where (object_id = {object_id} OR object_id LIKE {object_id} || ':%')  and handler='de.escidoc.core.om.service.ItemHandler' and request='retrieve' and user_id='' group by month, year;";
    public static final String REPORTDEFINITION_FILE_DOWNLOADS_PER_ITEM_ANONYMOUS = "select parent_object_id as itemId, sum(requests) as fileRequests from _1_object_statistics where (parent_object_id = {object_id} OR parent_object_id LIKE {object_id} || ':%') and handler='de.escidoc.core.om.service.ItemHandler' and request='retrieveContent' and user_id='' group by parent_object_id;";
    public static final String REPORTDEFINITION_FILE_DOWNLOADS_PER_FILE_ANONYMOUS = "select object_id as fileId, sum(requests) as fileRequests from _1_object_statistics where (object_id = {object_id} OR object_id LIKE {object_id} || ':%') and handler='de.escidoc.core.om.service.ItemHandler' and request='retrieveContent' and user_id='' group by object_id;";

    
    /**Retrieves a statistic report for an item from the Framework according to the specified report definition type. Sums up the statistic data
     * from the different versions of the specified item/file and returns the accumulated result.
     * @param reportDefinitionType The id of the report definition
     * @param objectId The id of the item.
     * @return The result of the report. Summed up requests for an item with all its versions or the file.
     * @throws Exception
     */
    public String getNumberOfItemOrFileRequests(String reportDefinitionType, String objectId, AccountUserVO accountUser) throws Exception;
    
    /**
     * Initializes the statistic system in order to retrieve simple statistic records from the framework.
     * Checks if necessary report definitions are already available on the framework.
     * If not, it writes the report definitions into the framework database.
     * Adds the real report definitions IDs to a property file.
     */
    public void initReportDefinitionsInFramework();
    
    /**Retrieves a statistic report for an item from the Framework according to the specified report definition type
     * 
      * @param reportDefinitionType The id of the report definition
     * @param objectId The id of the item.
     * @return The statistic report
     * @throws Exception
     */
    public  List<StatisticReportRecordVO> getStatisticReportRecord(String reportDefinitionType, String objectId, AccountUserVO accountUser) throws Exception;
    
}
