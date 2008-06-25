package de.mpg.escidoc.pubman.util.statistics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Collection;
import java.util.Properties;

import de.mpg.escidoc.pubman.util.statistics.ReportParameterVO.ParamType;



/**
 *
 * Implementation of PubItemSimpleStatistics
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class SimpleStatistics implements PubItemSimpleStatistics
{
    
    /**Retrieves a statistic report for an item from the Framework according to the specified report definition type. Sums up the statistic data
     * from the different versions of the specified item/file and returns the accumulated result.
     * @param reportDefinitionType The id of the report definition
     * @param objectId The id of the item.
     * @return The result of the report. Summed up requests for an item with all its versions or the file.
     * @throws Exception
     */
    public String getSimpleStatisticValue(String reportDefinitionType, String objectId) throws Exception{
        
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
