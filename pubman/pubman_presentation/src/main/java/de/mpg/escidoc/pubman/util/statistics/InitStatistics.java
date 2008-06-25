package de.mpg.escidoc.pubman.util.statistics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Initializes the statistic system in order to retrieve simple statistic records from the framework.
 * Checks if necessary report definitions are already available on the framework.
 * If not, it writes the report definitions into the framework database.
 * Adds the real report definitions IDs to a property file.
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class InitStatistics
{
    
    private static Logger logger = Logger.getLogger(InitStatistics.class);
    
    //protected static final String REPORTDEFINITION_FILE = "src/main/resources/report-definition-list.xml";
    //protected static final String REPORTDEFINITION_PROPERTIES = "src/main/resources/report-definitions.properties";
    
    protected static final String REPORTDEFINITION_FILE = "report-definition-list.xml";
    protected static final String REPORTDEFINITION_PROPERTIES_FILE = "report-definitions.properties";
      
    
    
    public void init()
    {
       
        logger.info("Initializing Report Definitions in framework database");
        
        try 
        {
            List<ReportDefinitionVO> repDefFrameworkList = StatisticReportsHandlingTemp.retrieveReportDefinitionListFromFramework();
            List<ReportDefinitionVO> repDefFileList = retrieveReportDefinitionListFromFile();
            
            //Creating a Hash Map with ReportDefinitions from Framework and sql as key
            HashMap<String, ReportDefinitionVO> repDefFrameworkMap = new HashMap<String, ReportDefinitionVO>();
            for (ReportDefinitionVO repDef : repDefFrameworkList)
            {
                repDefFrameworkMap.put(repDef.getSql(), repDef);
            }
            
            Properties repDefProps = new Properties();
            
            for (ReportDefinitionVO repDefFile : repDefFileList)
            {
                ReportDefinitionVO repDefFW = repDefFrameworkMap.get(repDefFile.getSql());
                
                //Report Definition already existing
                if(repDefFW != null) 
                {
                    //set Property
                    repDefProps.setProperty(repDefFW.getSql(), repDefFW.getObjectId());
                }
                //Report Definition does not exist yet
                else 
                {
                  //create and set
                    repDefFW = StatisticReportsHandlingTemp.createReportDefinition(repDefFile);
                    repDefProps.setProperty(repDefFW.getSql(), repDefFW.getObjectId());
                    
                }
            }
            
            //save properties
            URL url = InitStatistics.class.getClassLoader().getResource(REPORTDEFINITION_PROPERTIES_FILE);
            if (url == null)
            {
                throw new FileNotFoundException(REPORTDEFINITION_PROPERTIES_FILE);
            }
            repDefProps.store(new FileOutputStream(new File(url.toURI())), null);
        }
        
        catch (Exception e)
        {
            logger.error("Statistic report definitions could not be initialized! Statistic system may not work properly. ", e);
        }
 
    }


    
    protected String readFile(URL fileURL) throws IOException, FileNotFoundException, URISyntaxException
    {
        boolean isFileNameNull = (fileURL == null);
        StringBuffer fileBuffer;
        String fileString = null;
        String line;
        if (!isFileNameNull)
        {
           
                File file = new File(fileURL.toURI());
                FileReader in = new FileReader(file);
                BufferedReader dis = new BufferedReader(in);
                fileBuffer = new StringBuffer();
                while ((line = dis.readLine()) != null)
                {
                    fileBuffer.append(line + "\n");
                }
                in.close();
                fileString = fileBuffer.toString();
           
        }
        return fileString;
    }
    
    
    protected List<ReportDefinitionVO> retrieveReportDefinitionListFromFile() throws Exception
    {
       
        URL url = InitStatistics.class.getClassLoader().getResource(REPORTDEFINITION_FILE);
        if (url == null)
        {
            throw new FileNotFoundException(REPORTDEFINITION_FILE);
        }
        
        String repDefListXML = readFile(url);
        List<ReportDefinitionVO> repDefVOList =  StatisticReportsHandlingTemp.transformToReportDefinitionVOList(repDefListXML);
        return repDefVOList;
        
    }
    
               
}
