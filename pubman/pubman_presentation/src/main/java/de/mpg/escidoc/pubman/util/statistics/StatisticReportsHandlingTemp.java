package de.mpg.escidoc.pubman.util.statistics;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.xml.rpc.ServiceException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axis.encoding.Base64;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.util.statistics.ReportParameterVO.ParamType;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.xmltransforming.exceptions.UnmarshallingException;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.services.pubman.util.AdminHelper;

/**
 * Temporary Service class for retrieving statistic reports from the framework. Uses an retrieve request on the REST report system.
 * A report-definition with the specified id (in ReportParamsVO) must exist in the framework (autmotaically initialized by <code>initStatistics</code>.
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class StatisticReportsHandlingTemp {

	private static Logger logger = Logger.getLogger(StatisticReportsHandlingTemp.class);
	
	
    
    
    /**Executes a retrieve request to the REST Report system in order to retrieve a statistic report.
     * @param xml A String containing the report parameters in xml according to 
     * "http://www.escidoc-project.de/schemas/soap/report/0.3/report-parameters.xsd" (report_parameters.xsd).
     */
	private static String postRESTReport(String xml) throws Exception
    {
        
	    logger.debug("FW URL: " + ServiceLocator.getFrameworkUrl());
	    
        PostMethod pm = new PostMethod(ServiceLocator.getFrameworkUrl() + "/statistic/report");
        pm.setRequestHeader(new Header("Content-Type","text/xml"));
        pm.setRequestHeader("Cookie", "escidocCookie=" + AdminHelper.getAdminUserHandle());
        pm.setRequestEntity(new StringRequestEntity(xml));
        HttpClient client = new HttpClient();
        client.executeMethod(pm);
        String result = pm.getResponseBodyAsString();
        return result;
    }
	
	
	/**Tries to create a new Report Definition on the server
	 * 
	 * @param repDef The <code>ReportDefinitionVO</code> top be created
	 * @return A <code>ReportDefinitionVO</code> object containing the assigned object id (which can differ from the sent <code>ReportDefinitionVO</code>)
	 * @throws Exception
	 */
	public static ReportDefinitionVO createReportDefinition(ReportDefinitionVO repDef) throws Exception{
        PutMethod pm = new PutMethod(ServiceLocator.getFrameworkUrl()+"/statistic/report-definition");
        pm.setRequestHeader(new Header("Content-Type","text/xml"));
        pm.setRequestHeader("Cookie", "escidocCookie=" + AdminHelper.getAdminUserHandle());
        
        String xml = transformToReportDefinition(repDef);
        
        pm.setRequestEntity(new StringRequestEntity(xml));
        HttpClient client = new HttpClient();
        client.executeMethod(pm);
        
        String result = pm.getResponseBodyAsString();
        
        ReportDefinitionVO repDefVOResult = transformToReportDefinitionVO(result);
        
        if (repDefVOResult==null) throw new ServiceException(result);
        
        return repDefVOResult;
    }
	
	/**Tries to get the list of available report definitions from the server
	 * 
	 * @return A <code>List</code> of <code>ReportDefinitonVO</code> objects
	 * @throws Exception
	 */
	 
	public static List<ReportDefinitionVO> retrieveReportDefinitionListFromFramework() throws Exception{
	    GetMethod gm = new GetMethod(ServiceLocator.getFrameworkUrl()+"/statistic/report-definition");
        HttpClient client = new HttpClient();
        client.executeMethod(gm);
        String result = gm.getResponseBodyAsString();
        
        return transformToReportDefinitionVOList(result);
        
    }
	
	/**Helper method that logs in a user in order to retrieve statistic reports */
    private static String loginUser(String userid, String password) throws HttpException, IOException, ServiceException, URISyntaxException
    {
        // post the login data
    	
    	//logger.info("FW URl:" + "http://192.129.1.85:8080");
    	
        PostMethod postMethod = new PostMethod(ServiceLocator.getFrameworkUrl() + "/aa/login");
        postMethod.addParameter("survey", "LoginResults");
        postMethod.addParameter("target", ServiceLocator.getFrameworkUrl());
        postMethod.addParameter("login", userid);
        postMethod.addParameter("password", password);
        HttpClient client = new HttpClient();
        client.executeMethod(postMethod);
        if (HttpServletResponse.SC_SEE_OTHER != postMethod.getStatusCode())
        {
            throw new HttpException("Wrong status code: " + postMethod.getStatusCode());
        }
        String response = postMethod.getResponseBodyAsString();
        String userHandle = null;
        Header headers[] = postMethod.getResponseHeaders();
        for (int i = 0; i < headers.length; ++i)
        {
            if ("Location".equals(headers[i].getName()))
            {
                String location = headers[i].getValue();
                int index = location.indexOf('=');
                userHandle = new String(Base64.decode(location.substring(index + 1, location.length())));
            }
        }
        if (userHandle == null)
        {
            throw new ServiceException("User not logged in.");
        }
        //logger.info("User logged in: " + userHandle);
        return userHandle;
    }
    
    
    /**
     * Temporary transforming method that transforms a given <code>ReportParamsVO</code>
     * to corresponding XML. Valid according to "http://www.escidoc.de/schemas/reportparameters/0.3" (report_parameters.xsd).
     * 
     * @param repParamsVO A <code>ReportParamsVO></code>
     * @return Corresponding XML that is valid according to "http://www.escidoc.de/schemas/reportparameters/0.3"
     * @throws TechnicalException
     */
 
    public static String transformToReportParams(ReportParamsVO repParams) throws XMLStreamException {
        XMLOutputFactory fact = XMLOutputFactory.newInstance();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XMLStreamWriter writer = fact.createXMLStreamWriter(out, "UTF-8");
        
        
        writer.writeStartDocument();
        writer.writeStartElement("report-parameters");
        writer.writeDefaultNamespace("http://www.escidoc.de/schemas/reportparameters/0.3");
        writer.writeEmptyElement("report-definition");
        writer.writeAttribute("objid", repParams.getReportDefinitionObjID());
        
        ArrayList<ReportParameterVO> paramsList = repParams.getReportParameters();
        for (ReportParameterVO rp:paramsList)
        {
            writer.writeStartElement("parameter");
            writer.writeAttribute("name", rp.getName());

            switch(rp.getParamType()) {
                case STRINGVALUE :      {writer.writeStartElement("stringvalue");break;}
                case DATEVALUE :        {writer.writeStartElement("datevalue");break;}
                case DECIMALVALUE :     {writer.writeStartElement("decimalvalue");break;}
                
            }
            
            writer.writeCharacters(rp.getValue());
            writer.writeEndElement();
            
            writer.writeEndElement();
            
        }
         
        writer.writeEndElement();
        writer.writeEndDocument();
        
        writer.flush();
        writer.close();
        
        return out.toString();
        
        
    }
    
    /**
     * Transforms an XML String that is valid according to "http://www.escidoc.de/schemas/reportparameters/0.3"
     * (report.xsd) to the corresponding <code>ReportVO</code>.
     * 
     * @param xml XML String that is valid according to
     *            "http://www.escidoc.de/schemas/reportparameters/0.3" (report.xsd)
     * @return The corresponding <code>ReportVO</code>
     * @throws TechnicalException
     * @throws UnmarshallingException
     */
    public static ReportVO transformToReportVO(String xml) throws XMLStreamException
    {
        String reportNamespace = "http://www.escidoc.de/schemas/report/0.3";
        XMLInputFactory inpfac = XMLInputFactory.newInstance();
        XMLStreamReader reader = inpfac.createXMLStreamReader(new StringReader(xml));
        
        ReportVO report = new ReportVO();
        ReportRecordVO currentRecord = new ReportRecordVO();
        while(reader.hasNext()) {
           int next = reader.next();
           if (next==XMLStreamReader.START_ELEMENT) 
           {
               String tagName = reader.getLocalName();
               
               if (tagName.equals("report-definition"))
               {
                   String objectID = reader.getAttributeValue(null, "objid");
                   report.setReportDefinitionObjID(objectID);
               }
               
               else if (tagName.equals("report-record"))
               {
                   currentRecord = new ReportRecordVO();
                   report.addRecord(currentRecord);
                   
                   
               }
                   
               else if (tagName.equals("parameter"))
               {
                   String paramName = reader.getAttributeValue(null, "name");
                   //set Reader to value element
                   reader.nextTag();
                   String valueType = reader.getLocalName();
                   
                   ReportParameterVO repParam = new ReportParameterVO();
                   repParam.setName(paramName);
                   
                   if (valueType.equals("stringvalue"))
                   {
                       repParam.setParamType(ParamType.STRINGVALUE);
                   }
                   else if (valueType.equals("datevalue"))
                   {
                       repParam.setParamType(ParamType.DATEVALUE);
                   }
                   else if (valueType.equals("decimalvalue"))
                   {
                       repParam.setParamType(ParamType.DECIMALVALUE);
                   }
                   
                   String paramValue = reader.getElementText();
                   repParam.setValue(paramValue);
                   currentRecord.addReportParameter(repParam);
                   
               }

           }
        }
    
        return report;
    }
    
    /** Retrieves a statistic report from the framework
     * 
     * @param repParams The report parameters to be sent
     * @return The statistic report
     * @throws Exception
     */
    public static ReportVO retrieveReport(ReportParamsVO repParams) throws Exception{
        
        logger.debug("Retrieving statistic record with ID: " + repParams.getReportDefinitionObjID());
        String reportParamsXML = transformToReportParams(repParams);
        String reportXML = StatisticReportsHandlingTemp.postRESTReport(reportParamsXML);
        logger.debug(reportXML);
        ReportVO report = StatisticReportsHandlingTemp.transformToReportVO(reportXML);
        
        return report;
    }
    
    /**
     * Temporary transforming method that transforms a given <code>ReportDefinitionVO</code>
     * to corresponding XML. Valid according to "http://www.escidoc.de/schemas/report-definition/0.3" (report-definition.xsd).
     * 
     * @param repDefVO A <code>ReportDefinitionVO</code>
     * @return Corresponding XML that is valid according to "http://www.escidoc.de/schemas/reportparameters/0.3"
     * @throws TechnicalException
     */
    public static String transformToReportDefinition(ReportDefinitionVO repDefVO) throws Exception
    {
        XMLOutputFactory fact = XMLOutputFactory.newInstance();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XMLStreamWriter writer = fact.createXMLStreamWriter(out, "UTF-8");
        
        
        writer.writeStartDocument();
        writer.writeStartElement("report-definition");
        writer.writeDefaultNamespace("http://www.escidoc.de/schemas/reportdefinition/0.3");
        writer.writeAttribute("objid", repDefVO.getObjectId());
        
        
        writer.writeStartElement("name");        
        writer.writeCharacters(repDefVO.getName());
        writer.writeEndElement();
        
        writer.writeStartElement("scope");        
        writer.writeAttribute("objid", repDefVO.getScopeID());
        writer.writeEndElement();
        
        writer.writeStartElement("sql");        
        writer.writeCharacters(repDefVO.getSql());
        writer.writeEndElement();
 
        writer.writeEndElement();
        writer.writeEndDocument();
        
        writer.flush();
        writer.close();
        
        return out.toString();
    
    }
    
    /**
     * Transforms an XML String that is valid according to "http://www.escidoc/schemas/report-definition/0.3"
     * (report-definition.xsd) to the corresponding <code>ReportDefinitionVO</code>.
     * 
     * @param xml XML String that is valid according to
     *            "http://www.escidoc-project.de/schemas/soap/report/0.3" (report.xsd)
     * @return The corresponding <code>ReportDefinitionVO</code>
     * @throws TechnicalException
     * @throws UnmarshallingException
     */
    public static ReportDefinitionVO transformToReportDefinitionVO(String xml) throws Exception{
        List<ReportDefinitionVO> repDefList = transformToReportDefinitionVOList(xml);
        if (repDefList.size()>0) return repDefList.get(0);
        else return null;
    }
    
    /**
     * Transforms an XML String that is valid according to "http://www.escidoc/schemas/report-definition-list/0.3"
     * (report-definition-list.xsd) to the corresponding <code>List<ReportDefinitionVO></code>.
     * 
     * @param xml XML String that is valid according to
     *            "http://www.escidoc/schemas/report-definition-list/0.3"
     * @return A <code>List</code> containing corresponding <code>ReportVO</code>
     * @throws TechnicalException
     * @throws UnmarshallingException
     */
    public static List<ReportDefinitionVO> transformToReportDefinitionVOList(String xml) throws Exception{
        
        ArrayList<ReportDefinitionVO> repDefVOList = new ArrayList<ReportDefinitionVO>();
        
        XMLInputFactory inpfac = XMLInputFactory.newInstance();
        XMLStreamReader reader = inpfac.createXMLStreamReader(new StringReader(xml));
               
        
        while(reader.hasNext()) 
        {
            int next = reader.next();
            
            if(next==XMLStreamReader.START_ELEMENT)
            {
                String tagName = reader.getLocalName();
                
                if (tagName.equals("report-definition"))
                {
                    ReportDefinitionVO repDefVO = new ReportDefinitionVO();
                    
                    String objectID = reader.getAttributeValue(null, "objid");
                    
                    repDefVO.setObjectId(objectID);
                    
                    //process child elements of report-definition until end tag of report-definition is reached
                    int innerNext = -1;
                    while( !(( (innerNext = reader.next()) == XMLStreamReader.END_ELEMENT) && reader.getLocalName().equals("report-definition")))
                    {
                        if(innerNext==XMLStreamReader.START_ELEMENT)
                        {
                            String innerTagName = reader.getLocalName();
                            
                            if (innerTagName.equals("name"))
                            {
                                repDefVO.setName(reader.getElementText());
                            }
                            else if (innerTagName.equals("sql"))
                            {
                                repDefVO.setSql(reader.getElementText());
                            }
                            else if (innerTagName.equals("scope"))
                            {
                                repDefVO.setScopeID(reader.getAttributeValue(null, "objid"));
                            }
                        }
                    }
                    
                    repDefVOList.add(repDefVO);
                }

            }
        }
        
        return repDefVOList;
    }
}
