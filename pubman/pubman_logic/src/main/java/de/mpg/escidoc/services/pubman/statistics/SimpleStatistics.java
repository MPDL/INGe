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
package de.mpg.escidoc.services.pubman.statistics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

import org.apache.log4j.Logger;
import org.jboss.annotation.ejb.RemoteBinding;

import de.escidoc.www.services.sm.ReportDefinitionHandler;
import de.escidoc.www.services.sm.ReportHandler;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.logging.LogMethodDurationInterceptor;
import de.mpg.escidoc.services.common.logging.LogStartEndInterceptor;
import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportDefinitionVO;
import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportParamsVO;
import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportRecordDecimalParamValueVO;
import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportRecordParamVO;
import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportRecordStringParamValueVO;
import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportRecordVO;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.services.pubman.PubItemSimpleStatistics;
import de.mpg.escidoc.services.pubman.QualityAssurance;
import de.mpg.escidoc.services.pubman.util.AdminHelper;



/**
 *
 * Implementation of PubItemSimpleStatistics
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */

@Remote
@RemoteBinding(jndiBinding = PubItemSimpleStatistics.SERVICE_NAME)
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@Interceptors( { LogStartEndInterceptor.class, LogMethodDurationInterceptor.class })
public class SimpleStatistics implements PubItemSimpleStatistics
{
    private static final Logger logger = Logger.getLogger(SimpleStatistics.class);
    
    protected static final String REPORTDEFINITION_FILE = "report-definition-list.xml";
    //protected static final String REPORTDEFINITION_PROPERTIES_FILE = "report-definitions.properties";
    
    //private HashMap<String, String> reportDefinitionMap;
    
    /**
     * A XmlTransforming instance.
     */
    @EJB
    private XmlTransforming xmlTransforming;
    
   
    
    /**
     * {@inheritDoc}
     */
    public String getNumberOfItemOrFileRequests(String reportDefinitionType, String objectId, String userHandle) throws Exception{
        

        if (reportDefinitionType == null || objectId == null)
        {
            throw new IllegalArgumentException("Arguments are null!");
        }
        
        /*
        Properties repDefProps = new Properties();
        URL url = SimpleStatistics.class.getClassLoader().getResource(REPORTDEFINITION_PROPERTIES_FILE);
        if (url == null)
        {
            throw new FileNotFoundException(REPORTDEFINITION_PROPERTIES_FILE);
        }
        repDefProps.load(new FileInputStream(new File(url.toURI())));
        */
        
        //String repDefId = repDefProps.getProperty(reportDefinitionType);
        
       
       // EntityManager em = emf.createEntityManager();
        String repDefId = ReportDefinitionStorage.getInstance().getReportDefinitionMap().get(reportDefinitionType);
        
        if (repDefId==null) throw new Exception("Reportdefinition does not exist: "+objectId);
        
        StatisticReportParamsVO repParams = new StatisticReportParamsVO();
        repParams.setReportDefinitionId(repDefId);
        
        StatisticReportRecordParamVO param = new StatisticReportRecordParamVO("object_id", new StatisticReportRecordStringParamValueVO(objectId));
        repParams.getParamList().add(param);
        
        String xmlParams = xmlTransforming.transformToStatisticReportParameters(repParams);
        
        
        ReportHandler repHandler = ServiceLocator.getReportHandler(userHandle);
        String xmlReport = repHandler.retrieve(xmlParams);
        
        List<StatisticReportRecordVO> reportRecordList = xmlTransforming.transformToStatisticReportRecordList(xmlReport);
        

       int requests = 0;
       
       //Search for parameter with name "itemrequests" or "filerequests" in records
       //go through records and accumulate requests for different versions
       for (StatisticReportRecordVO record : reportRecordList)
       {
           List<StatisticReportRecordParamVO> parameterList = record.getParamList();
           
           for (StatisticReportRecordParamVO parameter : parameterList)
           {
               if (parameter.getName().equals("itemrequests") || parameter.getName().equals("filerequests"))
               {
                   StatisticReportRecordDecimalParamValueVO decimalvalue = (StatisticReportRecordDecimalParamValueVO) parameter.getParamValue();
                   requests+=decimalvalue.getDecimalValue();
               }
           }
          
       }

      return String.valueOf(requests);
        
    }
    
    

    /**
     * {@inheritDoc}
     */
    public void initReportDefinitionsInFramework()
    {
       
        logger.info("Initializing Report Definitions in framework database");
        
        try 
        {
            
            ReportDefinitionHandler repDefHandler = ServiceLocator.getReportDefinitionHandler(AdminHelper.getAdminUserHandle());
            //EntityManager em = emf.createEntityManager();
            String repDefFrameworkListXML = repDefHandler.retrieveReportDefinitions();
            List<StatisticReportDefinitionVO> repDefFrameworkList = xmlTransforming.transformToStatisticReportDefinitionList(repDefFrameworkListXML);
            
            
            List<StatisticReportDefinitionVO> repDefFileList = retrieveReportDefinitionListFromFile();
            
            //Creating a Hash Map with ReportDefinitions from Framework and sql as key
            HashMap<String, StatisticReportDefinitionVO> repDefFrameworkMap = new HashMap<String, StatisticReportDefinitionVO>();
            
            for (StatisticReportDefinitionVO repDef : repDefFrameworkList)
            {
                repDefFrameworkMap.put(repDef.getSql(), repDef);
            }
            
            
            for (StatisticReportDefinitionVO repDefFile : repDefFileList)
            {
                StatisticReportDefinitionVO repDefFW = repDefFrameworkMap.get(repDefFile.getSql());
                
                //Report Definition already existing
                if(repDefFW != null) 
                {
                    //set Property
                    ReportDefinitionStorage.getInstance().getReportDefinitionMap().put(repDefFW.getSql(), repDefFW.getObjectId());
                    
                }
                //Report Definition does not exist yet
                else 
                {
                  //create and set
                    String repDefFileXML = xmlTransforming.transformToStatisticReportDefinition(repDefFile);
                    String repDefFWXMLNew = repDefHandler.create(repDefFileXML);
                    StatisticReportDefinitionVO repDefFWNew = xmlTransforming.transformToStatisticReportDefinition(repDefFWXMLNew);
                    ReportDefinitionStorage.getInstance().getReportDefinitionMap().put(repDefFWNew.getSql(), repDefFWNew.getObjectId());
                    
                }
            }
            
           
        }
        
        catch (Exception e)
        {
            logger.error("Statistic report definitions could not be initialized! Statistic system may not work properly. ", e);
        }
 
    }


    
    private List<StatisticReportDefinitionVO> retrieveReportDefinitionListFromFile() throws Exception
    {
        String repDefListXML = ResourceUtil.getResourceAsString(REPORTDEFINITION_FILE);
       
        List<StatisticReportDefinitionVO> repDefVOList =  xmlTransforming.transformToStatisticReportDefinitionList(repDefListXML);
        return repDefVOList;
        
    }




   

    
}
