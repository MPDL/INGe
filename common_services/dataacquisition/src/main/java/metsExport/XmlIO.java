package metsExport;

import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.xerces.dom.AttrImpl;
import net.sf.saxon.TransformerFactoryImpl;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

/**
 * Methods used to read xml.
 * 
 * @author Wilhelm Frank (initial creation)
 * @version $Revision:1 $ $LastChangedDate:2008-03-13 11:08:48 +0100 (Thu, 13 Mar 2008) $
 */

public class XmlIO
{

    /** Reads xml file and returns StringBuffer.
     * 
     * @param fileName
     * @return contents of the file as String.
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static String readFile(String fileName) throws IOException, FileNotFoundException
    {
        boolean isFileNameNull = (fileName == null);
        StringBuffer fileBuffer = null;
        String fileString = null;
        String line;
        if (!isFileNameNull)
        {
                File file = new File(fileName);
                //FileReader in = new FileReader(file);
                FileInputStream in = new FileInputStream(file);
                BufferedReader dis = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                fileBuffer = new StringBuffer();
                int c;
                while ((c = dis.read()) != -1)
                {
                    fileBuffer.append((char)c);
                }
                dis.close();
                in.close();
                
                fileString = fileBuffer.toString();
                
        }
        return fileString;
    }
    
    protected void writeFile(String fileName, StringBuffer fileBuffer) throws IOException, FileNotFoundException
    {
        boolean isFileNameNull = (fileName == null);
        if (!isFileNameNull)
        {
                File file = new File(fileName);
                //FileWriter out = new FileWriter(file);
                FileOutputStream out = new FileOutputStream(file);
                //BufferedWriter bw = new BufferedWriter(out);
                OutputStreamWriter bw = new OutputStreamWriter(out, "UTF-8");
                char[] buf = new char[fileBuffer.length()];
                fileBuffer.getChars(0, fileBuffer.length(), buf, 0);
                
                bw.write(buf);
                bw.close();
                out.close();
        }
    }


    /**
     * Parse xml to Document.
     * 
     * @param xml as String.
     * @return The Document.
     * @throws Exception
     */
    protected static Document getDocument(final String xml, final boolean namespaceAwareness) throws Exception
    {
        String encoding = "UTF-8";
        Document result = null;
        
    	// Use Saxon
        //System.setProperty("javax.xml.parsers.DocumentBuilder", "net.sf.saxon.TransformerFactoryImpl");
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setNamespaceAware(namespaceAwareness);
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        result = docBuilder.parse(new ByteArrayInputStream(xml.getBytes(encoding)));
        System.out.println(docBuilder);
        result.getDocumentElement().normalize();
        return result;
    }
    
    protected static NodeList getAllNodes(Document doc) throws Exception
    {
        NodeList nl = doc.getElementsByTagNameNS("*", "*");
        return nl;
    }


    /**
     * Gets the value of the attribute of the root element from the document.
     * 
     * @param document The document to retrieve the value from.
     * @param attributeName The name of the attribute to retrieve.
     * @return The attribute value.
     * @throws Exception
     * @throws TransformerException
     */
    protected static String getRootElementAttributeValue(final Document document, final String attributeName) throws Exception
    {
        String xPath;
        if (attributeName.startsWith("@"))
        {
            xPath = "/*/" + attributeName;
        }
        else
        {
            xPath = "/*/@" + attributeName;
        }
        String value = selectSingleNode(document, xPath).getTextContent();
        return value;
    }

    /**
     * Return the text value of the attribute NOT considering namespaces.
     * 
     * @param node The node.
     * @param xPath The xpath to select the node containing the attribute.
     * @param attributeName The name of the attribute.
     * @return The text value of the attribute.
     * @throws Exception
     */
    protected static String getAttributeValue(final Node node, final String xPath, final String attributeName) throws Exception
    {
        String result = null;
        Node singleNode = selectSingleNode(node, xPath);
        if (singleNode == null)
        {
            throw new Exception("Single node for path '" + xPath + "' not found.");
        }
        if (singleNode.hasAttributes())
        {
            result = singleNode.getAttributes().getNamedItem(attributeName).getTextContent();
        }
        return result;
    }

    /**
     * Return the text value of the attribute considering namespaces.
     * 
     * @param node The node.
     * @param xPath The xpath to the node containing the attribute.
     * @param attributeNamespaceURI The namespace URI of the attribute.
     * @param attributeLocalName The local name of the attribute.
     * @return The value for the attribute.
     * @throws Exception
     */
    protected static String getAttributeValueNS(final Node node, final String xPath, final String attributeNamespaceURI,
            final String attributeLocalName) throws Exception
    {
        String result = null;
        NodeList nodeList = selectNodeList(node, xPath);
        Node hitNode = nodeList.item(0);
        if (hitNode.hasAttributes())
        {
            NamedNodeMap nnm = hitNode.getAttributes();
            Node attrNode = nnm.getNamedItemNS(attributeNamespaceURI, attributeLocalName);
            result = attrNode.getTextContent();
        }
        return result;
    }

     /**
     * XML schema validation.
     * 
     * @param xml XML as String.
     * @param schemaFileName
     * @throws Exception
     */
    protected static void XMLValid(final String xml, final String schemaFileName) throws Exception
    {
        Schema schema = getSchema(schemaFileName);
        try
        {
            Validator validator = schema.newValidator();
            InputStream in = new ByteArrayInputStream(xml.getBytes("UTF-8"));
            validator.validate(new SAXSource(new InputSource(in)));
        }
        catch (SAXParseException e)
        {
            StringBuffer sb = new StringBuffer();
            sb.append("XML invalid at line:" + e.getLineNumber() + ", column:" + e.getColumnNumber() + "\n");
            sb.append("SAXParseException message: " + e.getMessage() + "\n");
            sb.append("Affected XML: \n"+xml);
        }
    }

    /**
     * Gets value from document for given xpath expression.
     * 
     * @param document The document.
     * @param xPath The xpath to the node containing the attribute.
     * @return The value for the xpath expression. 
     * @throws TransformerException
     */
    protected String getValue(Document document, String xpathExpression) throws TransformerException
    {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xPath = factory.newXPath();
        try
        {
            return xPath.evaluate(xpathExpression, document);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets <code>Schema</code> object for the <code>File</code>.
     * 
     * @param schemaFileName
     * @return The <code>Schema</code> object.
     * @throws Exception
     */
    private static Schema getSchema(final String schemaFileName) throws Exception
    {
        if (schemaFileName == null)
        {
            throw new Exception("No schema file provided");
        }
        File schemaFile = new File(schemaFileName);
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema theSchema = sf.newSchema(schemaFile);
        return theSchema;
    }

    /**
     * Return child of node selected via xPath.
     * 
     * @param node The node.
     * @param xPath xPath expression.
     * @return The child of node selected via xPath.
     * @throws TransformerException
     */
    protected static Node selectSingleNode(final Node node, final String xpathExpression) throws TransformerException
    {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xPath = factory.newXPath();
        try
        {
            return (Node)xPath.evaluate(xpathExpression, node, XPathConstants.NODE);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Return children of node selected via xPath.
     * 
     * @param node The node.
     * @param xPath The xPath expression.
     * @return The children of the node selected via xPath.
     * @throws TransformerException
     */
    protected static NodeList selectNodeList(final Node node, final String xpathExpression) throws TransformerException
    {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xPath = factory.newXPath();
        try
        {
            return (NodeList)xPath.evaluate(xpathExpression, node, XPathConstants.NODESET);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    protected String getId(String item)
    {
        String id = "";
        int index = item.indexOf("objid=\"");
        if (index > 0)
        {
            item = item.substring(index + 7);
            index = item.indexOf('\"');
            if (index > 0)
            {
                id = item.substring(0, index);
            }
        }
        return id;
    }
    
    protected String getModificationDate(String item)
    {
        String md = "";
        int index = item.indexOf("last-modification-date=\"");
        if (index > 0)
        {
            item = item.substring(index + 24);
            index = item.indexOf('\"');
            if (index > 0)
            {
                md = item.substring(0, index);
            }
        }
        return md;
    }
    
    protected String createModificationDate(String md)
    {
        return "<param last-modification-date=\"" + md + "\"/>";
    }
    
    protected static Object getService(String serviceName) throws NamingException
    {
        InitialContext context = new InitialContext();
        Object serviceInstance = context.lookup(serviceName);
        assertNotNull(serviceInstance);
        return serviceInstance;
    }
}
