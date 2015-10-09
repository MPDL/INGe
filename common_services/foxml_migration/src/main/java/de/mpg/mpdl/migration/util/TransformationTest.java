package de.mpg.mpdl.migration.util;

import java.io.File;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.xml.serialize.OutputFormat;

public class TransformationTest
{
    public static final String XML_IN = "xml_old/escidoc_101939";
    public static final String XML_OUT = "xml_old/transformed_101939";
    public static final String XSL = "xsl/foxml_pubItem.xsl";
    public static final String DIR = "/home/frank/data/faces_albums/transformed";
    public static final String TEI = "/home/frank/data/DARIAH/izidor/tei/spoti-2007-10-08.xml";
    public static final String DTD = "/home/frank/data/DARIAH/izidor/tei/tei2.dtd";


    public static void main(String[] args)
    {
        /*
        File in = new File(XML_IN);
        File out = new File(XML_OUT);
        File xsl = new File(XSL);
        transform(in, out, xsl);
        */
        File in = new File(TEI);
        File dtd = new File(DTD);
        validateDTD(in, null, dtd);
        
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
            t.setParameter("cone_url", MigrationProperties.get("cone.persons.url"));
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
    
    public static void validateDTD(File in, File out, File dtd)
    {
        TransformerFactory factory = TransformerFactory.newInstance();
        Source xmlIn = new StreamSource(in);
        Result xmlOut = new StreamResult(System.out);
        
            Transformer t;
            try
            {
                t = factory.newTransformer();
                t.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, dtd.getName());
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
