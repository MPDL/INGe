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
package de.mpg.escidoc.services.pubman.statistics;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;
import org.jboss.annotation.ejb.RemoteBinding;

import de.escidoc.www.services.sm.AggregationDefinitionHandler;
import de.escidoc.www.services.sm.ReportDefinitionHandler;
import de.escidoc.www.services.sm.ReportHandler;
import de.mpg.escidoc.services.common.StatisticLogger;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.logging.LogMethodDurationInterceptor;
import de.mpg.escidoc.services.common.logging.LogStartEndInterceptor;
import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.ExportFormatVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO.Storage;
import de.mpg.escidoc.services.common.valueobjects.FileVO.Visibility;
import de.mpg.escidoc.services.common.valueobjects.ItemVO.ItemAction;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.statistics.AggregationDefinitionVO;
import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportDefinitionVO;
import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportParamsVO;
import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportRecordDecimalParamValueVO;
import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportRecordParamVO;
import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportRecordStringParamValueVO;
import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportRecordVO;
import de.mpg.escidoc.services.framework.AdminHelper;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.services.pubman.PubItemSimpleStatistics;



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
    
    
    public static String REPORTDEFINITION_NUMBER_OF_ITEM_RETRIEVALS_ALL_USERS;
    public static String REPORTDEFINITION_FILE_DOWNLOADS_PER_ITEM_ALL_USERS;
    public static String REPORTDEFINITION_FILE_DOWNLOADS_PER_FILE_ALL_USERS;
    public static String REPORTDEFINITION_NUMBER_OF_ITEM_RETRIEVALS_ANONYMOUS;
    public static String REPORTDEFINITION_FILE_DOWNLOADS_PER_ITEM_ANONYMOUS;
    public static String REPORTDEFINITION_FILE_DOWNLOADS_PER_FILE_ANONYMOUS;

    
    /**
     * A XmlTransforming instance.
     */
    @EJB
    private XmlTransforming xmlTransforming;
    
   
    public  List<StatisticReportRecordVO> getStatisticReportRecord(String reportDefinitionId, String objectId, AccountUserVO accountUser) throws Exception
    {
        if (reportDefinitionId == null || objectId == null)
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
        //String repDefId = ReportDefinitionStorage.getInstance().getReportDefinitionMap().get(reportDefinitionType);
        
        if (reportDefinitionId==null) throw new Exception("Reportdefinition does not exist: "+reportDefinitionId);
        
        StatisticReportParamsVO repParams = new StatisticReportParamsVO();
        repParams.setReportDefinitionId(reportDefinitionId);
        
        StatisticReportRecordParamVO param = new StatisticReportRecordParamVO("object_id", new StatisticReportRecordStringParamValueVO(objectId));
        repParams.getParamList().add(param);
        
        String xmlParams = xmlTransforming.transformToStatisticReportParameters(repParams);
        
        ReportHandler repHandler;
        if (accountUser == null || accountUser.getHandle()==null)
            repHandler = ServiceLocator.getReportHandler();
        else
            repHandler = ServiceLocator.getReportHandler(accountUser.getHandle());
        
        String xmlReport = repHandler.retrieve(xmlParams);
        
        List<StatisticReportRecordVO> reportRecordList = xmlTransforming.transformToStatisticReportRecordList(xmlReport);
        
        return reportRecordList;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getNumberOfItemOrFileRequests(String reportDefinitionId, String objectId, AccountUserVO accountUser) throws Exception{
        

        List<StatisticReportRecordVO> reportRecordList = getStatisticReportRecord(reportDefinitionId, objectId, accountUser);
        

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
       
    	try 
        {
    	
	    	logger.info("Initializing statistic aggregation definitions in framework database");
	    	AggregationDefinitionHandler adh = ServiceLocator.getAggregationDefinitionHandler(AdminHelper.getAdminUserHandle());
	    	String srwResponseXml = adh.retrieveAggregationDefinitions(new HashMap<String, String[]>());
	    	List<AggregationDefinitionVO> aggList = xmlTransforming.transformToStatisticAggregationDefinitionList(srwResponseXml);
	    	
	    	String aggregationTableName = null;
	    	
	    	for(AggregationDefinitionVO aggDef : aggList)
	    	{
	    		if(aggDef.getName().equals("pubman item statistics without version"))
	    		{
	    			logger.info("Pubman statistic aggregation definition already exists with id " + aggDef.getObjectId());
	    			
	    			aggregationTableName = aggDef.getAggregationTables().get(0).getName();
	    			logger.info("Pubman aggregated table name:" +  aggregationTableName);
	    			break;
	    		}
	    		
	    	}
	    	
	    	//No aggregation found, create one
	    	if(aggregationTableName == null)
	    	{
	    		logger.info("No pubman aggregation definition found, creating one");
	    		
	    		String aggregationDefinitionXml = ResourceUtil.getResourceAsString("pubman_object_stats_aggregation.xml");
	    		String createdAggDefXml = adh.create(aggregationDefinitionXml);
	    		AggregationDefinitionVO aggCreated = xmlTransforming.transformToStatisticAggregationDefinition(createdAggDefXml);
	    		logger.info("Pubman aggregation definition created with id " + aggCreated.getObjectId());
	    		aggregationTableName = aggCreated.getAggregationTables().get(0).getName();
	    		logger.info("Pubman aggregated table name:" +  aggregationTableName);
	    	}
    	
    	
	    	logger.info("Initializing statistical report definitions in framework database");
        
       
            
            ReportDefinitionHandler repDefHandler = ServiceLocator.getReportDefinitionHandler(AdminHelper.getAdminUserHandle());
            //EntityManager em = emf.createEntityManager();
            String repDefFrameworkListXML = repDefHandler.retrieveReportDefinitions(new HashMap<String, String[]>());
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
            	String sql = repDefFile.getSql().replaceAll("pubman_object_stats", aggregationTableName);
                StatisticReportDefinitionVO repDefFW = repDefFrameworkMap.get(sql);
                String repDefObjectId;
                
                //Report Definition already existing
                if(repDefFW != null) 
                {
                    //set Property
                    //Map<String, String> reportDefMap =   ReportDefinitionStorage.getInstance().getReportDefinitionMap();
                    //reportDefMap.put(repDefFW.getSql(), repDefFW.getObjectId());
                	repDefObjectId = repDefFW.getObjectId();
                	logger.info("Report Definition already  existing: " + repDefFW.getObjectId() + " --- " + repDefFW.getSql());
                	

                }
                //Report Definition does not exist yet
                else 
                {
                  //create and set
                    String repDefFileXML = xmlTransforming.transformToStatisticReportDefinition(repDefFile).replaceAll("pubman_object_stats", aggregationTableName);;
                    String repDefFWXMLNew = repDefHandler.create(repDefFileXML);
                    StatisticReportDefinitionVO repDefFWNew = xmlTransforming.transformToStatisticReportDefinition(repDefFWXMLNew);
                    //ReportDefinitionStorage.getInstance().getReportDefinitionMap().put(repDefFWNew.getSql(), repDefFWNew.getObjectId());
                    repDefObjectId = repDefFWNew.getObjectId();
                    logger.info("Created new report definition and added to Map: " + repDefFWNew.getObjectId() + " --- " + repDefFWNew.getSql());
                    
                }
                
                if(repDefFile.getName().equals("Item retrievals, all users"))
                {
                	REPORTDEFINITION_NUMBER_OF_ITEM_RETRIEVALS_ALL_USERS = repDefObjectId;
                }
                else if(repDefFile.getName().equals("File downloads per Item, all users"))
                {
                	REPORTDEFINITION_FILE_DOWNLOADS_PER_ITEM_ALL_USERS = repDefObjectId;
                }
              
                else if(repDefFile.getName().equals("File downloads, all users"))
                {
                	REPORTDEFINITION_FILE_DOWNLOADS_PER_FILE_ALL_USERS = repDefObjectId;
                }
                else if(repDefFile.getName().equals("Item retrievals, anonymous users"))
                {
                	REPORTDEFINITION_NUMBER_OF_ITEM_RETRIEVALS_ANONYMOUS = repDefObjectId;
                }
                else if(repDefFile.getName().equals("File downloads per Item, anonymous users"))
                {
                	REPORTDEFINITION_FILE_DOWNLOADS_PER_ITEM_ANONYMOUS = repDefObjectId;
                }
                else if(repDefFile.getName().equals("File downloads, anonymous users"))
                {
                	REPORTDEFINITION_FILE_DOWNLOADS_PER_FILE_ANONYMOUS = repDefObjectId;
                }
                
                
                
               
            }
            
            /*
            logger.info( ReportDefinitionStorage.getInstance().getReportDefinitionMap().size()
            +" Statistic report definitions are initialized! ");
           */
            
           
        }
        
        catch (Exception e)
        {
            logger.error("Statistic report definitions could not be initialized! Statistic system may not work properly. ", e);
        }
 
    }


    
    private List<StatisticReportDefinitionVO> retrieveReportDefinitionListFromFile() throws Exception
    {
        String repDefListXML = ResourceUtil.getResourceAsString(REPORTDEFINITION_FILE);
        String[] repDefs = repDefListXML.split("\n");
        List<StatisticReportDefinitionVO> repDefVOList = new ArrayList<StatisticReportDefinitionVO>();
        for(String repDefXml : repDefs)
        {
        	repDefVOList.add(xmlTransforming.transformToStatisticReportDefinition(repDefXml));
        	
        }
        return repDefVOList;
        
    }
    
    private void logPubItemAction(PubItemVO pubItem, String ip, String userAgent, ItemAction action, String sessionId,  boolean loggedIn, String referer, List<StatisticReportRecordParamVO> additionalParams) throws Exception
    {
        
        List<StatisticReportRecordParamVO> paramList = new ArrayList<StatisticReportRecordParamVO>();
        /*
        List<CreatorVO> creatorList = pubItem.getMetadata().getCreators();
        String persAffIds = "";
        for(CreatorVO creator : creatorList)
        {
            if (creator.getPerson()!=null && creator.getPerson().getIdentifier()!=null && creator.getPerson().getIdentifier().getId()!=null && !creator.getPerson().getIdentifier().getId().equals(""))
            {
               persAffIds += creator.getPerson().getIdentifier().getId() + ",";
            }
        }
        StatisticReportRecordParamVO persAffParam = new StatisticReportRecordParamVO();
        persAffParam.setName("persAffId");
        persAffParam.setParamValue(new StatisticReportRecordStringParamValueVO(persAffIds));
        paramList.add(persAffParam);
*/
        
        StatisticReportRecordParamVO oaParam = new StatisticReportRecordParamVO();
        oaParam.setName("hasOAComponent");
        oaParam.setParamValue(new StatisticReportRecordStringParamValueVO(String.valueOf(getHasOAComponent(pubItem))));
        paramList.add(oaParam);
        
       
        
        String authorIds = "{";
        String orgIds = "{";
        List<CreatorVO> creatorList = pubItem.getMetadata().getCreators();
        for(CreatorVO creator : creatorList)
        {
            if (creator.getPerson()!=null && creator.getPerson().getIdentifier()!=null && creator.getPerson().getIdentifier().getId()!=null && !creator.getPerson().getIdentifier().getId().equals(""))
            {
                //sl.logAuthorAction(sessionId, ip, creator.getPerson().getIdentifier().getId(), loggedIn, referer, "pubman", null, AdminHelper.getAdminUserHandle());
                authorIds +=  creator.getPerson().getIdentifier().getId() + ","; 
                /*
                StatisticReportRecordParamVO authorIdsParam = new StatisticReportRecordParamVO();
                authorIdsParam.setName("authorId");
                authorIdsParam.setParamValue(new StatisticReportRecordStringParamValueVO(creator.getPerson().getIdentifier().getId()));
                paramList.add(authorIdsParam);
                */
            }
            
            /*
            if(creator.getPerson()!=null && creator.getPerson().getOrganizationsSize()>0)
            {
                for(OrganizationVO org : creator.getPerson().getOrganizations())
                {
                    if(org.getIdentifier()!= null && !org.getIdentifier().equals(""))
                    {
                        //sl.logOrgAction(sessionId, ip, org.getIdentifier(), loggedIn, referer, "pubman", null, AdminHelper.getAdminUserHandle());
                        orgIds += org.getIdentifier() + ",";
                        
//                        StatisticReportRecordParamVO orgIdsParam = new StatisticReportRecordParamVO();
//                        orgIdsParam.setName("orgId");
//                        orgIdsParam.setParamValue(new StatisticReportRecordStringParamValueVO(org.getIdentifier()));
//                        paramList.add(orgIdsParam);
                        
                    }
                }
            }
            */
            if(creator.getOrganization()!=null && creator.getOrganization().getIdentifier()!= null && !creator.getOrganization().getIdentifier().equals(""))
            {
                //sl.logOrgAction(sessionId, ip, creator.getOrganization().getIdentifier(), loggedIn, referer, "pubman", null, AdminHelper.getAdminUserHandle());
                orgIds += creator.getOrganization().getIdentifier() + ",";
                
                /*
                StatisticReportRecordParamVO orgIdsParam = new StatisticReportRecordParamVO();
                orgIdsParam.setName("orgId");
                orgIdsParam.setParamValue(new StatisticReportRecordStringParamValueVO(creator.getOrganization().getIdentifier()));
                paramList.add(orgIdsParam);
                */
                
            }
            
        }
        
        for(OrganizationVO org : getAffiliatedOrganizations(pubItem))
        {
            if(org.getIdentifier()!=null && !org.getIdentifier().equals(""))
            {
                orgIds += org.getIdentifier() + ",";
            }
        }
        authorIds += "}";
        orgIds += "}";
        
        
        StatisticReportRecordParamVO authorIdsParam = new StatisticReportRecordParamVO();
        authorIdsParam.setName("authorIds");
        authorIdsParam.setParamValue(new StatisticReportRecordStringParamValueVO(authorIds));
        paramList.add(authorIdsParam);
        
        StatisticReportRecordParamVO orgIdsParam = new StatisticReportRecordParamVO();
        orgIdsParam.setName("orgIds");
        orgIdsParam.setParamValue(new StatisticReportRecordStringParamValueVO(orgIds));
        paramList.add(orgIdsParam);
        
        if (additionalParams!=null)
        {
            paramList.addAll(additionalParams);
        }
        
        InitialContext ic = new InitialContext();
        StatisticLogger sl = (StatisticLogger) ic.lookup(StatisticLogger.SERVICE_NAME);
        sl.logItemAction(sessionId, ip, userAgent, new PubItemVO(pubItem), action, loggedIn, referer, "pubman", paramList, AdminHelper.getAdminUserHandle());
    }
    
    public void logPubItemAction(PubItemVO pubItem, String ip, String userAgent, ItemAction action, String sessionId,  boolean loggedIn, String referer) throws Exception
    {
        this.logPubItemAction(pubItem, ip, userAgent, action, sessionId, loggedIn, referer, null);
    }
    
    public void logPubItemExport(PubItemVO pubItem, String ip, String userAgent, String sessionId,  boolean loggedIn, String referer, ExportFormatVO exportFormat) throws Exception
    {
        List<StatisticReportRecordParamVO> paramList = new ArrayList<StatisticReportRecordParamVO>();
        StatisticReportRecordParamVO exportFormatParam = new StatisticReportRecordParamVO();
        exportFormatParam.setName("exportFormat");
        exportFormatParam.setParamValue(new StatisticReportRecordStringParamValueVO(exportFormat.getName()));
        paramList.add(exportFormatParam);
        
        StatisticReportRecordParamVO exportFileFormatParam = new StatisticReportRecordParamVO();
        exportFileFormatParam.setName("exportFileFormat");
        exportFileFormatParam.setParamValue(new StatisticReportRecordStringParamValueVO(exportFormat.getSelectedFileFormat().getName()));
        paramList.add(exportFileFormatParam);
        
        this.logPubItemAction(pubItem, ip, userAgent, ItemAction.EXPORT, sessionId, loggedIn, referer, paramList);
    }
    
    private List<OrganizationVO> getAffiliatedOrganizations(PubItemVO pubItem)
    {
        List<CreatorVO> tempCreatorList;
        List<OrganizationVO> tempOrganizationList = new ArrayList<OrganizationVO>();
        List<OrganizationVO> sortOrganizationList = new ArrayList<OrganizationVO>();
        tempCreatorList = pubItem.getMetadata().getCreators();
        int affiliationPosition = 0;
        for (int i = 0; i < tempCreatorList.size(); i++)
        {
            CreatorVO creator = new CreatorVO();
            creator = tempCreatorList.get(i);
            if (creator.getPerson() != null)
            {
                if (creator.getPerson().getOrganizations().size() > 0)
                {
                    for (int listSize = 0; listSize < creator.getPerson().getOrganizations().size(); listSize++)
                    {
                        tempOrganizationList.add(creator.getPerson().getOrganizations().get(listSize));
                    }
                    for (int j = 0; j < tempOrganizationList.size(); j++)
                    {
                        // if the organization is not in the list already, put
                        // it in.
                        if (!sortOrganizationList.contains(tempOrganizationList.get(j)))
                        {
                            affiliationPosition++;
                            sortOrganizationList.add(tempOrganizationList.get(j));
                        }
                    }
                }
            }
        }
        // save the List in the backing bean for later use.
        return sortOrganizationList;
    }
    
    private boolean getHasOAComponent(PubItemVO pubItem)
    {
        if (pubItem.getFiles()!=null)
        {
            for(FileVO file : pubItem.getFiles())
            {
                if(file.getStorage()!=Storage.EXTERNAL_URL && file.getVisibility()==Visibility.PUBLIC)
                {
                    return true;   
                }
            }
        }
        return false;
    }
}
