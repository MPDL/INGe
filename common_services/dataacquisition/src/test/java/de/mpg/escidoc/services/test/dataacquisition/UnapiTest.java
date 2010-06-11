package de.mpg.escidoc.services.test.dataacquisition;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * These tests check the unapi interface for the dataaquisition service.
 * Please make sure that dev-pubman is running!
 * (Otherwise change the 'location' path or skip this test)
 *
 * @author kleinfe1 (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class UnapiTest
{
    
    private String location = "http://dev-pubman.mpdl.mpg.de/dataacquisition/download/unapi";
    private String arxivId = "arXiv:0904.3933";
    private String pmcId = "pmc:PMC2043518";
    private String bmcId = "bmc:1472-6890-9-1";
    private String spiresId ="spires:hep-ph/0001001";
    
    @Test
    public void unapiTestAllSources() throws Exception
    {
        HttpClient client = new HttpClient();
        String url = "";
        int code = 0;
        
        //arxiv
        url = this.location + "?id=" + this.arxivId + "&format=arxiv";
        GetMethod getMethod = new GetMethod(url);            
        code = client.executeMethod(getMethod);
        System.out.println("Fetch: " + url + "    Response: " + code);
        Assert.assertEquals(200, code);
        
        //pmc
        url = this.location + "?id=" + this.pmcId + "&format=pmc";
        getMethod = new GetMethod(url);            
        code = client.executeMethod(getMethod);
        System.out.println("Fetch: " + url + "    Response: " + code);
        Assert.assertEquals(200, code);
        
        //bmc
        url = this.location + "?id=" + this.bmcId + "&format=bmc";
        getMethod = new GetMethod(url);            
        code = client.executeMethod(getMethod);
        System.out.println("Fetch: " + url + "    Response: " + code);
        Assert.assertEquals(200, code);
        
        //spires
        url = this.location + "?id=" + this.spiresId + "&format=spires";
        getMethod = new GetMethod(url);            
        code = client.executeMethod(getMethod);
        System.out.println("Fetch: " + url + "    Response: " + code);
        Assert.assertEquals(200, code);
    }
    
    @Test
    //On arxiv example
    public void unapiTestOneSourceFull() throws Exception
    {
        HttpClient client = new HttpClient();
        String url = "";
        List <String> formatsList = new ArrayList<String> ();
        
        //Get all formats for arxiv
        url = this.location + "?id=arxiv";
        GetMethod getMethod = new GetMethod(url);            
        client.executeMethod(getMethod);
        String formatsXml = new String (getMethod.getResponseBody(), "UTF-8");
        formatsList = this.formatsHelper(formatsXml);
        
        //Retrieve item in all formats
        for (int i = 0; i < formatsList.size(); i ++)
        {
            url = this.location + "?id=" + this.arxivId + "&format=" + formatsList.get(i);
            getMethod = new GetMethod(url);            
            int code = client.executeMethod(getMethod);
            System.out.println("Fetch: " + url + "    Response: " + code);
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
