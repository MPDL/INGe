package de.mpg.escidoc.pubman.util.statistics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Collection;
import java.util.Properties;

import de.mpg.escidoc.pubman.util.statistics.ReportParameterVO.ParamType;
import de.mpg.escidoc.services.framework.ServiceLocator;



/**
 * Helper class for the retrieval of simple statistic records according to fixed statistic record definitions. 
 * The statistic database system from the framework must contain the following report definitions. If they are not available, they must
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
        <sql>select object_id as itemId, sum(requests) as itemRequests from _1_object_statistics where (object_id = {object_id} OR object_id LIKE {object_id} || ':%') and handler='ItemHandler' and request='retrieve' group by object_id;</sql>
    </report-definition>

    <report-definition xmlns="http://www.escidoc.de/schemas/reportdefinition/0.3" objid="5">
        <name>File downloads per Item, all users</name>
        <scope objid="2"/>
        <sql>select parent_object_id as itemId, sum(requests) as fileRequests from _1_object_statistics where (parent_object_id = {object_id} OR parent_object_id LIKE {object_id} || ':%')  and handler='ItemHandler' and request='retrieveContent' group by parent_object_id;</sql>
    </report-definition>
    
    <report-definition xmlns="http://www.escidoc.de/schemas/reportdefinition/0.3" objid="6">
        <name>File downloads, all users</name>
        <scope objid="2"/>
        <sql>select object_id as fileId, sum(requests) as fileRequests from _1_object_statistics where (object_id = {object_id} OR object_id LIKE {object_id} || ':%')  and handler='ItemHandler' and request='retrieveContent' group by object_id;</sql>
    </report-definition>
    
    <report-definition xmlns="http://www.escidoc.de/schemas/reportdefinition/0.3" objid="7">
        <name>Item retrievals, anonymous users</name>
        <scope objid="2"/>
        <sql>select object_id as itemId, sum(requests) as itemRequests from _1_object_statistics where (object_id = {object_id} OR object_id LIKE {object_id} || ':%')  and handler='ItemHandler' and request='retrieve' and user_id='' group by object_id;</sql>
    </report-definition>
    
    <report-definition xmlns="http://www.escidoc.de/schemas/reportdefinition/0.3" objid="8">
        <name>File downloads per Item, anonymous users</name>
        <scope objid="2"/>
        <sql>select parent_object_id as itemId, sum(requests) as fileRequests from _1_object_statistics where (parent_object_id = {object_id} OR parent_object_id LIKE {object_id} || ':%') and handler='ItemHandler' and request='retrieveContent' and user_id='' group by parent_object_id;</sql>
    </report-definition>
    
    <report-definition xmlns="http://www.escidoc.de/schemas/reportdefinition/0.3" objid="9">
        <name>File downloads, anonymous users</name>
        <scope objid="2"/>
        <sql>select object_id as fileId, sum(requests) as fileRequests from _1_object_statistics where (object_id = {object_id} OR object_id LIKE {object_id} || ':%') and handler='ItemHandler' and request='retrieveContent' and user_id='' group by object_id;</sql>
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
public class SimpleStatistics
{
    
    public static final String REPORTDEFINITION_NUMBER_OF_ITEM_RETRIEVALS_ALL_USERS = "select object_id as itemId, sum(requests) as itemRequests from _1_object_statistics where (object_id = {object_id} OR object_id LIKE {object_id} || ':%') and handler='ItemHandler' and request='retrieve' group by object_id;";
    public static final String REPORTDEFINITION_FILE_DOWNLOADS_PER_ITEM_ALL_USERS = "select parent_object_id as itemId, sum(requests) as fileRequests from _1_object_statistics where (parent_object_id = {object_id} OR parent_object_id LIKE {object_id} || ':%')  and handler='ItemHandler' and request='retrieveContent' group by parent_object_id;";
    public static final String REPORTDEFINITION_FILE_DOWNLOADS_PER_FILE_ALL_USERS = "select object_id as fileId, sum(requests) as fileRequests from _1_object_statistics where (object_id = {object_id} OR object_id LIKE {object_id} || ':%')  and handler='ItemHandler' and request='retrieveContent' group by object_id;";
    public static final String REPORTDEFINITION_NUMBER_OF_ITEM_RETRIEVALS_ANONYMOUS = "select object_id as itemId, sum(requests) as itemRequests from _1_object_statistics where (object_id = {object_id} OR object_id LIKE {object_id} || ':%')  and handler='ItemHandler' and request='retrieve' and user_id='' group by object_id;";
    public static final String REPORTDEFINITION_FILE_DOWNLOADS_PER_ITEM_ANONYMOUS = "select parent_object_id as itemId, sum(requests) as fileRequests from _1_object_statistics where (parent_object_id = {object_id} OR parent_object_id LIKE {object_id} || ':%') and handler='ItemHandler' and request='retrieveContent' and user_id='' group by parent_object_id;";
    public static final String REPORTDEFINITION_FILE_DOWNLOADS_PER_FILE_ANONYMOUS = "select object_id as fileId, sum(requests) as fileRequests from _1_object_statistics where (object_id = {object_id} OR object_id LIKE {object_id} || ':%') and handler='ItemHandler' and request='retrieveContent' and user_id='' group by object_id;";
    
    
    
    
    /**Retrieves a statistic report for an item from the Framework according to the specified report definition type. Sums up the statistic data
     * from the different versions of the specified item/file and returns the accumulated result.
     * @param reportDefinitionType The id of the report definition
     * @param objectId The id of the item.
     * @return The result of the report. Summed up requests for an item with all its versions or the file.
     * @throws Exception
     */
    public static String getSimpleStatisticValue(String reportDefinitionType, String objectId) throws Exception{
        
        Properties repDefProps = new Properties();
        URL url = InitStatistics.class.getClassLoader().getResource(InitStatistics.REPORTDEFINITION_PROPERTIES_FILE);
        if (url == null)
        {
            throw new FileNotFoundException(InitStatistics.REPORTDEFINITION_PROPERTIES_FILE);
        }
        repDefProps.load(new FileInputStream(new File(url.toURI())));
        
        String repDefId = repDefProps.getProperty(reportDefinitionType);
        
        if (repDefId==null) throw new Exception("Reportdefinition does not exist: "+objectId);
        
        ReportParamsVO repParams = new ReportParamsVO();
        repParams.setReportDefinitionObjID(repDefId);
        repParams.addReportParameter(new ReportParameterVO(ParamType.STRINGVALUE,"object_id", objectId));
        
       ReportVO report = StatisticReportsHandlingTemp.retrieveReport(repParams);
       
       Collection<ReportRecordVO> reportRecords = report.getReportRecords();
       
       
       int requests = 0;
       
       //Search for parameter with name "itemrequests" or "filerequests" in records
       //go through records and accumulate requests for different versions
       for (ReportRecordVO record : reportRecords)
       {
           ReportParameterVO itemRetrievals = record.getReportParameter("itemrequests");
          //if there are no "itemrequests" parameters, there must be an filerequests parameter
           if (itemRetrievals == null)
               itemRetrievals = record.getReportParameter("filerequests");
           
           requests += (Integer.parseInt(itemRetrievals.getValue()));
  
       }

      return String.valueOf(requests);
        
    }

    
}
