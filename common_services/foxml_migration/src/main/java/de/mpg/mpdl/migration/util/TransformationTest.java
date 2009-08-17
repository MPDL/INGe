package de.mpg.mpdl.migration.util;

import java.io.File;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class TransformationTest
{
    public static final String XML_IN = "xml/file2transform.xml";
    public static final String XML_OUT = "xml/transformed.xml";
    public static final String XSL = "xml/foxml_pubItem.xsl";

    public static void main(String[] args)
    {
        File in = new File(XML_IN);
        File out = new File(XML_OUT);
        File xsl = new File(XSL);
        transform(in, out, xsl);
    }
    
    public static void transform(File in, File out, File xsl)
    {
        TransformerFactory factory = TransformerFactory.newInstance();
        Source xmlIn = new StreamSource(in);
        Source xsl2use = new StreamSource(xsl);
        Result xmlOut = new StreamResult(out);
        try
        {
            Transformer t = factory.newTransformer(xsl2use);
            t.transform(xmlIn, xmlOut);
        }
        catch (TransformerConfigurationException e)
        {
            e.printStackTrace();
        }
        catch (TransformerException e)
        {
            e.printStackTrace();
        }
    }
}
