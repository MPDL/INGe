package de.mpg.escidoc.services.tools.scripts.csl_transformation_service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.List;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;

/**
 * Processor for Transforming CSL-XML-Files to one CoNE-RDF-XML
 */
public class CslConeTransformationProcessor
{
    public static void main(String[] args) throws Exception
    {
        // Settings
        if (args.length != 2)
        {
            System.out.println(
                    "usage: java CslConeTransformationProcessor <files-root-dir> <boolean-include-subdirectories>");
            System.out.println("sample: java CslConeTransformationProcessor /tmp/csl false");
        }
        else
        {
            // set Parameters
            File filesRoot = new File(args[0]);
            boolean includeSubdirectories = new Boolean(args[1]);
            // getting csl files
            String[] extensions = new String[] { "csl" };
            List<File> filesToTransform = (List<File>)FileUtils.listFiles(filesRoot, extensions, includeSubdirectories);
            System.out.println("Retrieved all csl files from <" + filesRoot + "> included subdirectories: <" + includeSubdirectories + ">");
            // prepare CoNE rdf
            StringBuffer sb = new StringBuffer();
            sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                    + "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" "
                    + "xmlns:dc=\"http://purl.org/dc/elements/1.1/\" "
                    + "xmlns:escidoc=\"http://purl.org/escidoc/metadata/terms/0.1/\">");
            // prepare XPath for parsing title and title-short
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();
            NamespaceContext nsContext = new NamespaceContextImpl();
            xPath.setNamespaceContext(nsContext);
            XPathExpression xPathExpressionTitle = xPath.compile("/csl:style/csl:info/csl:title");
            XPathExpression xPathExpressionTitleShort = xPath.compile("/csl:style/csl:info/csl:title-short");
            System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
                    "net.sf.saxon.dom.DocumentBuilderFactoryImpl");
            DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
            Document doc = null;
            InputStream is = null;
            String styleTitle = null;
            String styleTitleShort = null;
            String styleXml = null;
            // Parsing and adding csl files to CoNE rdf
            for (File file : filesToTransform)
            {
                System.out.println("Adding File <" + file.getCanonicalPath() + ">");
                is = new FileInputStream(file);
                dfactory.setNamespaceAware(true);
                doc = docBuilder.parse(is);
                styleTitle = xPathExpressionTitle.evaluate(doc);
                System.out.println("Style title: \"" + styleTitle + "\"");
                styleTitleShort = xPathExpressionTitleShort.evaluate(doc);
                System.out.println("Style title-short: \"" + styleTitleShort + "\"");
                is = new FileInputStream(file);
                styleXml = IOUtils.toString(is);
                sb.append("<rdf:Description>");
                sb.append("<dc:title>" + HtmlUtils.escapeHtml(styleTitle) + "</dc:title>");
                sb.append("<rdf:value>" + HtmlUtils.escapeHtml(styleXml) + "</rdf:value>");
                if (styleTitleShort != null && !"".equals(styleTitleShort))
                {
                    sb.append("<escidoc:abbreviation>" + HtmlUtils.escapeHtml(styleTitleShort) + "</escidoc:abbreviation>");
                }
                sb.append("</rdf:Description>");
            }
            sb.append("</rdf:RDF>");
            // write RDF
            File outputFile = new File("CoNE_CSL_RDF.xml");
            FileWriter fw = new FileWriter(outputFile);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(sb.toString());
            bw.close();
            System.out.println("Output written to File: " + outputFile.getCanonicalPath());
        }
    }
}
