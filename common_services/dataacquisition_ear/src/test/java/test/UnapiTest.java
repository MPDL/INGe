package test;

import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ProxyHelper;

/**
 * These tests check the unapi interface for the dataaquisition service.
 *
 * @author kleinfe1 (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class UnapiTest
{
    private Logger logger = Logger.getLogger(UnapiTest.class);
    
    private static String location;
    private String arxivId = "arXiv:0904.3933";
    private String pmcId = "pmc:PMC2043518";
    private String bmcId = "bmc:1472-6890-9-1";
    private String spiresId ="spires:hep-ph/0001001";

    @BeforeClass
    public static void Init() throws IOException, URISyntaxException
    {	
    	location = PropertyReader.getProperty("escidoc.dataacquisition.service.url") + "download/unapi";
    }
    
    @Test
    public void unapiTestAllSources() throws Exception
    {
        HttpClient client = new HttpClient();
        String url = "";
        int code = 0;
        
        //arxiv
        url = location + "?id=" + this.arxivId + "%26format=arxiv";
        GetMethod getMethod = new GetMethod(url); 
        
        
        code = ProxyHelper.executeMethod(client, getMethod);
        this.logger.info("Fetch: " + url + "    Response: " + code);
        Assert.assertEquals(200, code);
        
        //pmc
        url = location + "?id=" + this.pmcId + "%26format=pmc";
        getMethod = new GetMethod(url);            
        code = ProxyHelper.executeMethod(client, getMethod);
        this.logger.info("Fetch: " + url + "    Response: " + code);
        Assert.assertEquals(200, code);
        
        //bmc
        url = location + "?id=" + this.bmcId + "%26format=bmc";
        getMethod = new GetMethod(url);            
        code = ProxyHelper.executeMethod(client, getMethod);
        this.logger.info("Fetch: " + url + "    Response: " + code);
        Assert.assertEquals(200, code);
        
        //spires
        url = location + "?id=" + this.spiresId + "%26format=spires";
        getMethod = new GetMethod(url);            
        code = ProxyHelper.executeMethod(client, getMethod);
        this.logger.info("Fetch: " + url + "    Response: " + code);
        Assert.assertEquals(200, code);
    }
    
    @Test
    //On BioMed Central example
    
    public void unapiTestOneSourceFull() throws Exception
    {
        HttpClient client = new HttpClient();
        String url = "";
        List <String> formatsList = new ArrayList<String> ();
        
        //Get all formats for BioMed Central
        url = location + "?id=bmc";
        GetMethod getMethod = new GetMethod(url); 
        ProxyHelper.executeMethod(client, getMethod);
        String formatsXml = new String (getMethod.getResponseBody(), "UTF-8");
        formatsList = this.formatsHelper(formatsXml);
//        logger.info(url);
//        logger.info(formatsXml);

        
        //Retrieve item in all formats
        for (int i = 0; i < formatsList.size(); i ++)
        {
            url = location + "?id=" + this.bmcId + "%26format=" + formatsList.get(i);
            getMethod = new GetMethod(url);            
            int code = ProxyHelper.executeMethod(client, getMethod);
            this.logger.info("Fetch: " + url + "    Response: " + code);
            Assert.assertEquals(200, code);
        }
    }
    
    private List <String> formatsHelper(String formatsXml) throws Exception
    {
        List <String> formatsList = new ArrayList<String> ();
        
        //parse xml
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        
        InputSource inStream = new InputSource();
        inStream.setCharacterStream(new StringReader(formatsXml));
        Document doc = db.parse(inStream);
        doc.getDocumentElement().normalize();
        NodeList nodeLst = doc.getElementsByTagName("format");
        
        for (int i = 0; i < nodeLst.getLength(); i++) 
        {
            Node fstNode = nodeLst.item(i);
            Element fstElmnt = (Element) fstNode;
            Node attr = fstElmnt.getAttributeNode("name");
            formatsList.add(attr.getNodeValue());
        }
        
        return formatsList;
    }
    
}
