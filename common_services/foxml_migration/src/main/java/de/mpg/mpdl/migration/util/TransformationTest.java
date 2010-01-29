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
    public static final String XML_IN = "/home/frank/data/faces_albums/escidoc_";
    public static final String XML_OUT = "xml_old/transformed_faces_album";
    public static final String XSL = "xsl/foxml_facesAlbum.xsl";
    public static final String DIR = "/home/frank/data/faces_albums/transformed";

    public static void main(String[] args)
    {
        
        File files = new File(DIR);
        for (File f : files.listFiles())
        {
            f.renameTo(new File(DIR + "/" + f.getName().replace("_transformed", "")));
            //File out = new File(f.getAbsolutePath().concat("_transformed"));
            //File xsl = new File(XSL);
            //transform(f, out, xsl);
        }
        
        
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
}
