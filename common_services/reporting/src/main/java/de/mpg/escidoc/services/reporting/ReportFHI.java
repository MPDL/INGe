/*
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
* Copyright 2006-2010 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

package de.mpg.escidoc.services.reporting;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;
import javax.xml.rpc.ServiceException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.query.JRXPathQueryExecuterFactory;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRXmlUtils;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import de.mpg.escidoc.services.common.emailhandling.EmailHandlingBean;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.framework.AdminHelper;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ProxyHelper;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
*	Monthly report for FHI  
*
 * @author Vlad Makarenko (initial creation)
 * @author $Author: vmakarenko $ (last modification)
 * @version $Revision:$ $LastChangedDate: $
 *
 */ 

public class ReportFHI {


	private final static Logger logger = Logger.getLogger(ReportFHI.class);

	private static TransformerFactory tf = new net.sf.saxon.TransformerFactoryImpl();

	private static EmailHandlingBean emailHandler = new EmailHandlingBean(); 
	
	private static final int FLAGS = Pattern.CASE_INSENSITIVE | Pattern.DOTALL;  
	
	private static final Pattern AMPS_ALONE = Pattern.compile(
			"\\&(?!\\w+?;)",
			FLAGS
	);

	
	//number of the last months to be processed
	
	private static String adminHandler;

	private static Properties rprops; 
	
	
	public static String USER_NAME; 
	public static String USER_PASSWD;
	public static String emailSenderProp;
	public static String emailServernameProp;
	public static String emailWithAuthProp;
	public static String emailAuthUserProp ;
	public static String emailAuthPwdProp;

	
	
	public ReportFHI() throws IOException, URISyntaxException, ServiceException
	{
		USER_NAME = PropertyReader.getProperty("framework.admin.username");
		USER_PASSWD = PropertyReader.getProperty("framework.admin.password");
		emailSenderProp = PropertyReader.getProperty("escidoc.pubman_presentation.email.sender");
        emailServernameProp = PropertyReader.getProperty("escidoc.pubman_presentation.email.mailservername");
        emailWithAuthProp = PropertyReader.getProperty("escidoc.pubman_presentation.email.withauthentication");
        emailAuthUserProp = PropertyReader.getProperty("escidoc.pubman_presentation.email.authenticationuser");
        emailAuthPwdProp = PropertyReader.getProperty("escidoc.pubman_presentation.email.authenticationpwd");
        
        adminHandler = AdminHelper.loginUser(USER_NAME, USER_PASSWD);

        rprops = loadReportProperties();
      

        
	}
	

    //Generate time range query
    //Take all docs from the last months
    public static String[] getStartEndDateOfQuery()
    {
		
    	SimpleDateFormat dateformatter = new SimpleDateFormat("yyyy-MM-");
    	int months;
		try 
		{
			months = new Integer(rprops.getProperty("FHI.report.months.range")).intValue();
		} 
		catch (Exception e) 
		{
			throw new RuntimeException("Cannot read/convert FHI.report.months.range:", e);		
		};
		
    	//from
    	Calendar fromMonth = GregorianCalendar.getInstance();
		fromMonth.add(Calendar.MONTH, - months);
		String fromYearMonth = dateformatter.format(fromMonth.getTime());
		
		//to
    	Calendar toMonth = GregorianCalendar.getInstance();
		toMonth.add(Calendar.MONTH, - 1);
		String toYearMonth = dateformatter.format(toMonth.getTime());
		
		return new String[] {
				fromYearMonth +
				String.format("%02d", fromMonth.getActualMinimum(Calendar.DAY_OF_MONTH) ),
				toYearMonth + 
				toMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
		};
    }
    
    //Generate query for time range
    private static String getTimeRangeQuery()
    {
    	String[] dd = getStartEndDateOfQuery(); 
    	return 	"(\"/properties/creation-date\">=\"" + dd[0] 
    	        + "\"" +
    	        "%20and%20\"/properties/creation-date\"<=\"" + dd[1] 
    	        + "\")";
    }
    
    public static String getItemListFromFramework()
    {
    	
//    	Publications of the test context
//		Time range: previous month
    	
        String itemList = null;
        GetMethod method;
		try {
			method = new GetMethod(ServiceLocator.getFrameworkUrl() + "/ir/items");
	        method.setRequestHeader("Cookie", "escidocCookie=" + adminHandler);
	        String query = "operation=searchRetrieve&maximumRecords=1000&query=" + rprops.getProperty("FHI.query") +
                    "%20and%20" +
                    getTimeRangeQuery() +
                    rprops.getProperty("FHI.sort.by");
	        logger.info("query " + query);
	        method.setQueryString(query);
	        HttpClient client = new HttpClient();
			ProxyHelper.executeMethod(client, method);
			logger.info("URI:" + method.getURI()  + "\nStatus code:" + method.getStatusCode());
	        if (method.getStatusCode() == HttpServletResponse.SC_OK)
	        {         
	        	itemList = method.getResponseBodyAsString();
	        	
	        	//escape all alone &, otherwise filler throws an exception 
	        	itemList = replaceAllTotal(itemList, AMPS_ALONE, "&amp;");
	    		
	        	if (logger.isDebugEnabled())
	        		writeToFile("target/search-res.xml", itemList.getBytes("UTF-8"));
	        	logger.debug(itemList);
	        	
	        }
		} catch (Exception e) 
		{
			throw new RuntimeException("Cannot get item-list from framework:", e);
		} 
        
		
		return itemList;
    }
    
	/**
	 * Converts item-list XML to JasperReports DataSource
	 * @return Document  
	 */
	private static Document getXmlDataSource()
	{
		//getFilter from framework, FHI specific
		Document document = null;
		
		StringWriter sw = new StringWriter();

		
		//get item-list from framework
		Transformer transformer;
		try 
		{
			//resolution of containers, like authors and source names
			transformer = tf.newTemplates(
				new StreamSource(
						JRLoader.getLocationInputStream("schemas/make-containers.xsl")
				)
			).newTransformer();
			transformer.transform(new StreamSource(
					new StringReader(getItemListFromFramework())	
				), new StreamResult(sw)
			);

		} 
		catch (Exception e) 
		{
			throw new RuntimeException("Cannot transform item-list XML containers:", e);
		}
		
//		logger.debug(sw.toString());
		
		
		try 
		{
			document = JRXmlUtils.parse(new InputSource(new StringReader(sw.toString())));
		}
		catch (Exception e) 
		{
			throw new RuntimeException("Cannot convert item-list XML to JasperReport DataSource:", e);
		}
		
		return document;
		
		
	}    
    
	
	/**
	 * Generate month report files (formats are specified in properties)
	 * @throws JRException
	 * @return list of paths to the generated reports 
	 */
	public static String[] generateReport() throws JRException
	{
		
		String[] formats = rprops.getProperty("FHI.report.formats").split(",");
		
		//GET REPORT FROM JRXMLs
    	//compile subreports
//    	JasperCompileManager.compileReportToFile("src/main/resources/subreport_creators.jrxml");
		
    	//get main report
    	JasperReport jr = null;
	   	JasperDesign jd;
		jd = JRXmlLoader.load(JRLoader.getLocationInputStream("FHI_Bibilothek_report.jrxml"));
	   	jr = JasperCompileManager.compileReport(jd);
    	if ( jr == null )
    	{
			throw new RuntimeException("Compiled report is null: " + "FHI_Bibilothek_report.jrxml");
    	}
    	
    	Document doc = getXmlDataSource();
    	
    	Map<String, Object> params = new HashMap<String, Object>();
		params.put(JRXPathQueryExecuterFactory.PARAMETER_XML_DATA_DOCUMENT, doc);
		
		//fill report in memory
		JasperPrint jasperPrint;
		jasperPrint = JasperFillManager.fillReport(
				jr,
				params,
				new JRXmlDataSource(doc, jr.getQuery().getText())
		);
		
		ArrayList<String> atts = new ArrayList<String>();
		String fn;
		//save in files in formats
		for (String f: formats)
		{
			if ("pdf".equalsIgnoreCase(f))
			{
				JRPdfExporter pdfExp = new JRPdfExporter();
				pdfExp.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
				fn = "FHI_Bibilothek_report.pdf";
				pdfExp.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, fn);
				pdfExp.exportReport();
				atts.add(fn);
			}
			else if ("rtf".equalsIgnoreCase(f))
			{
				JRRtfExporter rtfExp = new JRRtfExporter();
				rtfExp.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
				fn = "FHI_Bibilothek_report.rtf";
				rtfExp.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, fn);
				rtfExp.exportReport();
				atts.add(fn);
			}
		}
		
		return atts.toArray(new String[atts.size()]);
    	 
    }	
	
	/**
	 * Send report per email(s)
	 * @param attFileNames - array of paths to report files 
	 */
	public static void sendReport(String[] attFileNames)
	{
		// send email with attachments 
		String toEmails = rprops.getProperty("FHI.recipients.addresses");
		if (toEmails != null && !toEmails.trim().equals(""))
		{
			String[] timeRange = getStartEndDateOfQuery();
			try 
			{
				emailHandler.sendMail(
						emailServernameProp, 
						emailWithAuthProp, 
						emailAuthUserProp, 
						emailAuthPwdProp, 
						rprops.getProperty("FHI.sender.address"), 
						toEmails.split(","), 
						rprops.getProperty("FHI.recipients.cc.addresses").split(","),
						rprops.getProperty("FHI.recipients.bcc.addresses").split(","),
						rprops.getProperty("FHI.reply.to.addresses").split(","),
						rprops.getProperty("FHI.subject") + ", von " + timeRange[0] + " bis " + timeRange[1], 
						new String(rprops.getProperty("FHI.body")),
						attFileNames
				);
			}
			catch (TechnicalException e) 
			{
				// TODO Auto-generated catch block
				throw new RuntimeException("Cannot send email:", e);
			}
		}
		
	}

	
	/**
	 * Generate month report and send it per email 
	 * @throws JRException
	 */
	public static void generateAndSendReport() throws JRException
	{
		sendReport(generateReport());
    }		
	
    /**
     * Load report properties
     * @return
     */
    public static Properties loadReportProperties() 
    {
    	InputStream is;
    	Properties props = null;
		try 
		{
			is = JRLoader.getLocationInputStream("reporting.properties");
	    	props = new Properties();
			props.load(is);

		}
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			throw new RuntimeException("Cannot read report props:", e);
		} 
		
		return props;
    }	
	
	
	/**
	 * Joins the elements of the provided array into a single String containing the provided list of elements.
	 * Separator will be put between the not null/empty elements  
	 * @param arr is the list of the elements.
	 * @param delimiter 
	 * @return joined string
	 */
	public static String join(String[] arr, String delimiter)
	{
		if ( arr==null || arr.length == 0 ) return null;
		StringBuffer sb = new StringBuffer();
		if (delimiter==null) delimiter="";
		for (int i=0, n=arr.length; i<n; i++ )
		{
			if (arr[i]==null || arr[i].trim().equals(""))
				continue;
			sb.append(arr[i]);
			if (i<n-1) sb.append(delimiter);
		}
		String str = sb.toString().replaceAll(Pattern.quote(delimiter)+"$", "");
		
		return str;
	}	
	
	
    public static String replaceAllTotal(String what, String expr, String replacement)
    {
	    return 
	    	Pattern
	    		.compile(expr, Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
	    		.matcher(what)
	    		.replaceAll(replacement);
    }

    public static String replaceAllTotal(String what, Pattern p, String replacement)
    {
    	return p.matcher(what).replaceAll(replacement);
    }    
    

	
    protected static void writeToFile(String fileName, byte[] content) throws IOException
    {
    	FileOutputStream fos = new FileOutputStream(fileName);
    	fos.write(content);
    	fos.close();
    }    

    protected static String readFromFile(String fileName) throws IOException
    {
    	int ch;
    	StringBuffer buff = new StringBuffer();
    	FileInputStream fis = new FileInputStream(fileName);
    	while( (ch = fis.read()) != -1)
    		buff.append((char)ch);
    	fis.close();
    	return buff.toString();
    }    
    
    
/*	
	public static void main(String args[]) throws JRException, IOException, URISyntaxException, ServiceException 
	{
	
		ReportFHI rep = new ReportFHI();
		String formats = null;
		String emails = null;
		

        if(args.length == 0)
        {
            usage();
            return;
        }

        int k = 0; 
        while ( args.length > k )
        {
            if ( args[k].startsWith("-F") )
                formats = args[k].substring(2);
            if ( args[k].startsWith("-S") )
                emails = args[k].substring(2);
            k++;
        }
		
        if ( formats == null || formats.length() == 0 || emails == null || emails.length() == 0)
        {
        	usage();
        }
        else
        {
        	generateReport();
        }
        
		
	}
	
    private static void usage()
    {
        System.out.println( "ReportFHI usage:" );
        System.out.println( "\tjava ReportFHI" );
    }	
    
*/

}	