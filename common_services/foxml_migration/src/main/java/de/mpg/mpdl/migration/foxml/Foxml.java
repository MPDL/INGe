package de.mpg.mpdl.migration.foxml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

import fedora.fedoraSystemDef.foxml.DatastreamType;
import fedora.fedoraSystemDef.foxml.DigitalObjectDocument;
import fedora.fedoraSystemDef.foxml.XmlContentType;


/**
 * 
 * TODO Description.
 *
 * @author frank (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class Foxml implements MigrationConstants
{
    
    private static ArrayList<File> allFiles = new ArrayList<File>();
    private static Logger logger = Logger.getLogger("migration");
    private static int publication = 0;
    private static int file = 1;
    private static int faces = 2;
    private static int facesalbum = 3;
    private static int orgunit = 4;
    private static int virrelement = 5;

    private Foxml()
    {
        
    }
    
    /**
     * 
     * @param args {@link String[]}
     */
    public static void main(String[] args)
    {
        migrate();
    }

    /**
     * 
     * @param baseDir {@link File}
     * @return {@link ArrayList}
     */
    public static ArrayList<File> fileList(File baseDir)
    {
        if (baseDir.isDirectory())
        {
            File[] files = baseDir.listFiles();
            for (File file : files)
            {
                if (file.isFile() && file.getName().matches("escidoc_\\w*"))
                {
                    allFiles.add(file);
                }
                else
                {
                    fileList(file);
                }
            }
        }
        return allFiles;
    }

    /**
     * 
     * @param rdfStream {@link InputStream}
     * @param resourceUri {@link String}
     * @param streamVersion {@value int}
     * @return {@link String[]}
     */
    public static String[] getResourceType(InputStream rdfStream, String resourceUri, int streamVersion)
    {
        String id = resourceUri.substring(resourceUri.indexOf("/") + 1);
        String resourceType = null;
        String context = null;
        Model model = ModelFactory.createDefaultModel();
        model.read(rdfStream, "");
        Resource about = model.getResource(resourceUri);
        resourceType = about.getRequiredProperty(RDF.type).getResource().getLocalName();
        
        if (resourceType.equalsIgnoreCase("item") || resourceType.equalsIgnoreCase("container"))
        {
            context = about.getProperty(ESCIDOCPROPERTIES.contexttitle).getString();
        }
        else
        {
            if (resourceType.equalsIgnoreCase("component"))
            {
                context = about.getProperty(ESCIDOCPROPERTIES.contentcategory).getString();
            }
        }
        return new String[] {id, resourceType, Integer.toString(streamVersion), context};
    }

    /**
     * 
     * @return {@link DigitalObjectDocument}
     */
    public static DigitalObjectDocument migrate()
    {
        ArrayList<File> files = null;
        InputStream rdfStream = null;
        String resourceUri = null;
        String metadata = null;
        String transMD = null;
        File transformed = null;
        String[] props = null;
        XmlObject xo = null;
        boolean updateRequired = true;
        long time = -System.currentTimeMillis();
        files = fileList(new File(System.getenv("FEDORA_HOME") + "/data/objects"));
        logger.info("attempting to transform " + files.size() + " files");
        int filenum = 0;
        for (File f : files)
        {
            filenum = filenum + 1;
            DigitalObjectDocument dodo;
            try
            {
                dodo = DigitalObjectDocument.Factory.parse(f);
                DatastreamType[] streams = dodo.getDigitalObject().getDatastreamArray();
                for (DatastreamType stream : streams)
                {
                    if (stream.getID().equals("RELS-EXT"))
                    {
                        int lastRelsExt = stream.sizeOfDatastreamVersionArray() - 1;
                        XmlContentType rdf = stream.getDatastreamVersionArray(lastRelsExt).getXmlContent();
                        rdfStream = rdf.newInputStream();
                        resourceUri = "info:fedora/escidoc:"
                                + f.getName().substring(f.getName().indexOf("_") + 1, f.getName().length());
                        props = getResourceType(rdfStream, resourceUri, lastRelsExt);
                        logger.info(">>> " + filenum + " " + Arrays.asList(props));
                    }
                    if (stream.getID().equals("escidoc"))
                    {
                        int lastEscidoc = stream.sizeOfDatastreamVersionArray() - 1;
                        XmlContentType xmlContent = stream.getDatastreamVersionArray(lastEscidoc).getXmlContent();
                        xo = XmlObject.Factory.parse(xmlContent.xmlText());
                        if (xo.schemaType().getFullJavaName() != null)
                        {
                            metadata = xo.schemaType().getFullJavaName();
                            if (metadata.startsWith(OLD_NS_URI))
                            {
                                updateRequired = true;
                                if (metadata.equalsIgnoreCase(OLD_PUBLICATION))
                                {
                                    transformed = xsltTransformation(f, publication);
                                }
                                if (metadata.equalsIgnoreCase(OLD_FILE))
                                {
                                    transformed = xsltTransformation(f, file);
                                }
                                if (metadata.equalsIgnoreCase(OLD_ORGUNIT))
                                {
                                    transformed = xsltTransformation(f, orgunit);
                                }
                                if (metadata.equalsIgnoreCase(OLD_VIRRELEMENT))
                                {
                                    transformed = xsltTransformation(f, virrelement);
                                }
                                if (transformed != null)
                                {
                                    dodo = DigitalObjectDocument.Factory.parse(transformed);
                                    DatastreamType[] transformedStreams = dodo.getDigitalObject().getDatastreamArray();
                                    for (DatastreamType transformedStream : transformedStreams)
                                    {
                                        if (transformedStream.getID().equalsIgnoreCase("escidoc"))
                                        {
                                            int lastStream = transformedStream.sizeOfDatastreamVersionArray() - 1;
                                            XmlContentType transformedContent = transformedStream
                                                    .getDatastreamVersionArray(lastStream).getXmlContent();
                                            xo = XmlObject.Factory.parse(transformedContent.xmlText());
                                        }
                                    }
                                    if (xo.schemaType().getFullJavaName() != null)
                                    {
                                        transMD = xo.schemaType().getFullJavaName();
                                    }
                                    else
                                    {
                                        transMD = "unable to determine new metadata";
                                    }
                                }
                            }
                            else
                            {
                                transMD = "no need to transform anything!";
                                updateRequired = false;
                            }
                            logger.info("    original metadata in "
                                    + stream.getDatastreamVersionArray(lastEscidoc).getID() + ": " + metadata);
                            logger.info("    transformed metadata in "
                                    + stream.getDatastreamVersionArray(lastEscidoc).getID() + ": " + transMD);
                            metadata = null;
                            transMD = null;
                        }
                        else
                        {
                            metadata = "unknown metadata format";
                            if (props[3].equalsIgnoreCase("FACES context"))
                            {
                                updateRequired = true;
                                if (props[1].equalsIgnoreCase("Item"))
                                {
                                    transformed = xsltTransformation(f, faces);
                                }
                                if (props[1].equalsIgnoreCase("Container"))
                                {
                                    transformed = xsltTransformation(f, facesalbum);
                                }
                                if (transformed != null)
                                {
                                    dodo = DigitalObjectDocument.Factory.parse(transformed);
                                    DatastreamType[] transformedStreams = dodo.getDigitalObject().getDatastreamArray();
                                    for (DatastreamType transformedStream : transformedStreams)
                                    {
                                        if (transformedStream.getID().equalsIgnoreCase("escidoc"))
                                        {
                                            int lastStream = transformedStream.sizeOfDatastreamVersionArray() - 1;
                                            XmlContentType transformedContent = transformedStream
                                                    .getDatastreamVersionArray(lastStream).getXmlContent();
                                            xo = XmlObject.Factory.parse(transformedContent.xmlText());
                                        }
                                    }
                                    if (xo.schemaType().getFullJavaName() != null)
                                    {
                                        transMD = xo.schemaType().getFullJavaName();
                                    }
                                    else
                                    {
                                        transMD = "unable to determine new metadata";
                                    }
                                }
                            }
                            else
                            {
                                transMD = "unable to transform unknown metadata";
                                updateRequired = false;
                            }
                            logger.info("    original metadata in "
                                    + stream.getDatastreamVersionArray(lastEscidoc).getID() + ": " + metadata);
                            logger.info("    transformed metadata in "
                                    + stream.getDatastreamVersionArray(lastEscidoc).getID() + ": " + transMD);
                            metadata = null;
                            transMD = null;
                        }
                    }
                }
                if (updateRequired)
                {
                    if (transformed != null)
                    {
                        f.delete();
                        transformed.renameTo(f);
                        logger.info("    saved " + f.getName());
                        transformed = null;
                    }
                    else
                    {
                        logger.info("    " + f.getName() + " contains no escidoc metadata to transform!");
                    }
                    updateRequired = false;
                }
                else
                {
                    logger.info("    no need to update " + f.getName());
                }
            }
            catch (XmlException e)
            {
                logger.info(e.toString());
                e.printStackTrace();
            }
            catch (IOException e)
            {
                logger.info(e.toString());
                e.printStackTrace();
            }
        }
        System.out.println(time + System.currentTimeMillis());
        return null;
    }

    /**
     * 
     * @param source {@link File}
     * @param schematype {@value int}
     * @return {@link File}
     */
    public static File xsltTransformation(File source, int schematype)
    {
        String xsltFileName = null;
        File xslt = null;
        File result = null;
        switch (schematype)
        {
            case 0:
                xsltFileName = "xml/foxml_pubItem.xsl";
                break;
            case 1:
                xsltFileName = "xml/foxml_file.xsl";
                break;
            case 2:
                xsltFileName = "xml/foxml_face.xsl";
                break;
            case 3:
                xsltFileName = "xml/foxml_facesAlbum.xsl";
                break;
            case 4:
                xsltFileName = "xml/foxml_orgUnit.xsl";
                break;
            case 5:
                xsltFileName = "xml/foxml_virrElement.xsl";
                break;
            default:
                break;
        }
        xslt = new File(xsltFileName);
        try
        {
            result = File.createTempFile(source.getName(), ".tmp", source.getParentFile());
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer t = tFactory.newTransformer(new StreamSource(xslt));
            t.transform(new StreamSource(source), new StreamResult(result));
            return result;
        }
        catch (TransformerConfigurationException e)
        {
            logger.info(e.toString());
            result.delete();
            e.printStackTrace();
        }
        catch (TransformerException e)
        {
            logger.info(e.toString());
            result.delete();
            e.printStackTrace();
        }
        catch (IOException e)
        {
            logger.info(e.toString());
            e.printStackTrace();
        }
        return null;
    }
}
