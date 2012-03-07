package de.mpg.escidoc.services.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.TransformerFactoryImpl;

import org.junit.Test;


public class dlcTest
{
    @Test
    public void xsltTransform()
    {        
        TransformerFactory factory = new TransformerFactoryImpl();
        StringWriter writer = new StringWriter();
        
        try
        {
            InputStream in = this.getResourceAsStream("src/main/java/de/mpg/escidoc/services/fledgeddata/transformations/oaidlc2zvddmets.xslt");
            Transformer transformer = factory.newTransformer(new StreamSource(in, "UTF-8"));
    
            StringReader xmlSource = new StringReader(this.getResourceAsString("src/test/java/de/mpg/escidoc/services/test/dlcItem.xml"));
            transformer.transform(new StreamSource(xmlSource), new StreamResult(writer));
        }
        catch (Exception e)
        {
           System.out.println(e.getMessage());
        }
        
        System.out.println(writer.toString());
    }
    
    /**
     * Gets a resource as String.
     *
     * @param fileName The path and name of the file relative from the working directory.
     * @return The resource as String.
     * @throws IOException Thrown if the resource cannot be located.
     */
    public String getResourceAsString(final String fileName) throws IOException
    {
        InputStream fileIn = getResourceAsStream(fileName);
        BufferedReader br = new BufferedReader(new InputStreamReader(fileIn, "UTF-8"));
        String line = null;
        StringBuilder result = new StringBuilder();
        while ((line = br.readLine()) != null)
        {
            result.append(line);
            result.append("\n");
        }
        return result.toString();
    }
    
    /**
     * Gets a resource as InputStream.
     *
     * @param fileName The path and name of the file relative from the working directory.
     * @return The resource as InputStream.
     * @throws FileNotFoundException Thrown if the resource cannot be located.
     */
    public InputStream getResourceAsStream(final String fileName) throws FileNotFoundException
    {
        
        InputStream fileIn;
        fileIn = dlcTest.class.getClassLoader().getResourceAsStream(resolveFileName(fileName));

        // Maybe it's in a WAR file
        if (fileIn == null)
        {
            fileIn = dlcTest.class.getClassLoader().getResourceAsStream(resolveFileName("WEB-INF/classes/" + fileName));
        }

        if (fileIn == null)
        {
            fileIn = new FileInputStream(resolveFileName(fileName));
        }

        return fileIn;
    }
    
    /**
     * This method resolves /.. in uris
     * @param name
     * @return
     */
    public String resolveFileName (String name)
    {
        if (name != null && (name.contains("/..") || name.contains("\\..")))
        {
            Pattern pattern1 = Pattern.compile("(\\\\|/)\\.\\.");
            Matcher matcher1 = pattern1.matcher(name);
            if (matcher1.find())
            {
                int pos1 = matcher1.start();
                Pattern pattern2 = Pattern.compile("(\\\\|/)[^\\\\/]*$");
                Matcher matcher2 = pattern2.matcher(name.substring(0, pos1));
                if (matcher2.find())
                {
                    int pos2 = matcher2.start();
                    return resolveFileName(name.substring(0, pos2) + name.substring(pos1 + 3));
                }
            }
        }
        
        return name;
    }

}