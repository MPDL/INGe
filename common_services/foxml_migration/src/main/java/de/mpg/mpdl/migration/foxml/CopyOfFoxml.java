package de.mpg.mpdl.migration.foxml;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

import de.mpg.mpdl.migration.util.XBeanUtils;
import fedora.fedoraSystemDef.foxml.DatastreamType;
import fedora.fedoraSystemDef.foxml.DatastreamVersionType;
import fedora.fedoraSystemDef.foxml.DigitalObjectDocument;
import fedora.fedoraSystemDef.foxml.XmlContentType;

public class CopyOfFoxml
{
    private static final String OLD_PUBLICATION = "de.mpg.escidoc.metadataprofile.schema.x01.PublicationDocument";
    private static final String OLD_FILE = "de.mpg.escidoc.metadataprofile.schema.x01.file.FileDocument";
    private static final String OLD_VIRRELEMENT = "de.mpg.escidoc.metadataprofile.schema.x01.virrelement.VirrelementDocument";
    private static final String OLD_ORGUNIT = "de.mpg.escidoc.metadataprofile.schema.x01.organization.OrganizationDetailsDocument";
    private static final String OLD_NS_URI = "de.mpg.escidoc.metadataprofile.schema.x01";
    static ArrayList<File> allFiles = new ArrayList<File>();
    static Logger logger = Logger.getLogger(CopyOfFoxml.class);
    static int publication = 0;
    static int file = 1;
    static int faces = 2;
    static int facesalbum = 3;
    static int orgunit = 4;

    public static void main(String[] args)
    {
        //migrate();
        transformFoxml();
    }

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

    public static String[] getResourceType(InputStream rdfStream, String resourceUri, int streamVersion)
    {
        String id = resourceUri.substring(resourceUri.indexOf("/") + 1);
        String resourceType = null;
        String objectPid = null;
        String versionPid = null;
        String contentPid = null;
        String context = null;
        Model model = ModelFactory.createDefaultModel();
        model.read(rdfStream, "");
        Resource about = model.getResource(resourceUri);
        resourceType = about.getRequiredProperty(RDF.type).getResource().getLocalName();
        if (about.getProperty(ESCIDOCPROPERTIES.pid) != null)
        {
            objectPid = about.getProperty(ESCIDOCPROPERTIES.pid).getString();
        }
        if (about.getProperty(ESCIDOCPROPERTIES.versionpid) != null)
        {
            versionPid = about.getProperty(ESCIDOCPROPERTIES.versionpid).getString();
        }
        if (resourceType.equalsIgnoreCase("item") || resourceType.equalsIgnoreCase("container"))
        {
            context = about.getProperty(ESCIDOCPROPERTIES.contexttitle).getString();
        }
        else
        {
            if (resourceType.equalsIgnoreCase("component"))
            {
                context = about.getProperty(ESCIDOCPROPERTIES.contentcategory).getString();
                if (about.getProperty(ESCIDOCPROPERTIES.pid) != null)
                {
                    contentPid = about.getProperty(ESCIDOCPROPERTIES.pid).getString();
                }
            }
        }
        return new String[] { id, resourceType, Integer.toString(streamVersion), objectPid, versionPid, contentPid,
                context, null };
    }

    public static DigitalObjectDocument migrate()
    {
        ArrayList<File> files = null;
        InputStream rdfStream = null;
        String resourceUri = null;
        String metadata = null;
        String transMD = null;
        String transformed = null;
        String[] props = null;
        XmlObject xo = null;
        boolean updateRequired = true;
        long time = -System.currentTimeMillis();
        files = fileList(new File(System.getenv("FEDORA_HOME") + "/data/objects/2008/1106"));
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
                        DatastreamVersionType[] streamVersions = stream.getDatastreamVersionArray();
                        for (DatastreamVersionType version : streamVersions)
                        {
                            XmlContentType xmlContent = version.getXmlContent();
                            xo = XmlObject.Factory.parse(xmlContent.xmlText());
                            System.out.println(xo.xmlText());
                            if (xo.schemaType().getFullJavaName() != null)
                            {
                                metadata = xo.schemaType().getFullJavaName();
                                if (metadata.startsWith(OLD_NS_URI))
                                {
                                    updateRequired = true;
                                    if (metadata.equalsIgnoreCase(OLD_PUBLICATION))
                                    {
                                        transformed = xsltTransformation(xo.newInputStream(), null, publication);
                                    }
                                    if (metadata.equalsIgnoreCase(OLD_FILE))
                                    {
                                        transformed = xsltTransformation(xo.newInputStream(), null, file);
                                    }
                                    if (metadata.equalsIgnoreCase(OLD_ORGUNIT))
                                    {
                                        transformed = xsltTransformation(xo.newInputStream(), null, orgunit);
                                    }
                                    if (metadata.equalsIgnoreCase(OLD_VIRRELEMENT))
                                    {
                                        transformed = reloadWithNewNamespace(xo);
                                    }
                                    if (transformed != null)
                                    {
                                        System.out.println(transformed);
                                        xo = XmlObject.Factory.parse(transformed);
                                        xmlContent.set(xo);
                                        //version.setXmlContent(xmlContent);
                                        if (xo.schemaType().getFullJavaName() != null)
                                        {
                                            transMD = xo.schemaType().getFullJavaName();
                                        }
                                        else
                                        {
                                            transMD = "unable to transform unknown metadata";
                                        }
                                        transformed = null;
                                    }
                                }
                                else
                                {
                                    transMD = "no need to transform anything!";
                                    updateRequired = false;
                                }
                                
                                logger.info("    original metadata in " + version.getID() + ": " + metadata);
                                //pidlogger.info("    ---------------------------------");
                                //pidlogger.info(xo.xmlText());
                                //pidlogger.info("    ---------------------------------");
                                logger.info("    transformed metadata in " + version.getID() + ": " + transMD);
                            }
                            else
                            {
                                metadata = "unknown metadata format";
                                if (props[6].equalsIgnoreCase("FACES context"))
                                {
                                    updateRequired = true;
                                    if (props[1].equalsIgnoreCase("Item"))
                                    {
                                        transformed = xsltTransformation(xo.newInputStream(), null, faces);
                                    }
                                    if (props[1].equalsIgnoreCase("Container"))
                                    {
                                        transformed = xsltTransformation(xo.newInputStream(), null, facesalbum);
                                    }
                                    
                                    if (transformed != null)
                                    {
                                        xo = XmlObject.Factory.parse(transformed);
                                        xmlContent.set(xo);
                                        if (xo.schemaType().getFullJavaName() != null)
                                        {
                                            transMD = xo.schemaType().getFullJavaName();
                                        }
                                        else
                                        {
                                            transMD = "unable to transform unknown metadata";
                                        }
                                        transformed = null;
                                    }
                                }
                                else
                                {
                                    transMD = "unable to transform unknown metadata";
                                    updateRequired = false;
                                }
                                logger.info("    original metadata in " + version.getID() + ": " + metadata);
                                logger.info("    transformed metadata in " + version.getID() + ": " + transMD);
                            }
                        }
                        if (updateRequired)
                        {
                            dodo.save(f);
                            logger.info("    saved " + f.getName());
                        }
                        else
                        {
                            logger.info("    no need to update " + f.getName());
                        }
                    }
                }
            }
            catch (XmlException e)
            {
                logger.info(e.getMessage());
                e.printStackTrace();
            }
            catch (IOException e)
            {
                logger.info(e.getMessage());
                e.printStackTrace();
            }
            
        }
        System.out.println(time + System.currentTimeMillis());
        return null;
    }

    public static String xsltTransformation(InputStream in, File file, int schematype)
    {
        ByteArrayOutputStream buf = null;
        String xsltFileName = null;
        switch (schematype)
        {
            case 0:
                //xsltFileName = "xml/transform_publication.xsl";
                xsltFileName = "xml/digitalobject.xsl";
                break;
            case 1:
                xsltFileName = "xml/transform_file.xsl";
                break;
            case 2:
                xsltFileName = "xml/transform_face.xsl";
                break;
            case 3:
                xsltFileName = "xml/transform_facesalbum.xsl";
                break;
            case 4:
                xsltFileName = "xml/transform_orgunit.xsl";
                break;
        }
        InputStream xsltSystemId = null;
        try
        {
            //xsltSystemId = new File(xsltFileName).toURL().toExternalForm();
            xsltSystemId = CopyOfFoxml.class.getClassLoader().getResourceAsStream(xsltFileName);
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer t = tFactory.newTransformer(new StreamSource(xsltSystemId));
            buf = new ByteArrayOutputStream();
            StreamResult result = new StreamResult(buf);
            if (in != null)
            {
                t.transform(new StreamSource(in), result);
            }
            else
            {
                t.transform(new StreamSource(file), result);
            }
            return buf.toString("UTF-8");
        }
        catch (TransformerConfigurationException e)
        {
            logger.info(e.getMessage());
            e.printStackTrace();
        }
        catch (TransformerException e)
        {
            logger.info(e.getMessage());
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e)
        {
            logger.info(e.getMessage());
            e.printStackTrace();
        }
        finally
        {
            try
            {
                xsltSystemId.close();
                buf.flush();
                buf.close();
            }
            catch (IOException e)
            {
                logger.info(e.getMessage());
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String reloadWithNewNamespace(XmlObject xo)
    {
        XmlOptions loadOptions = new XmlOptions();
        XmlOptions saveOptions = new XmlOptions();
        HashMap<String, String> otherNs = new HashMap<String, String>();
        otherNs.put("http://escidoc.mpg.de/metadataprofile/schema/0.1/virrelement",
                "http://purl.org/escidoc/metadata/profiles/0.1/virrelement");
        HashMap<String, String> prefixes = new HashMap<String, String>();
        prefixes.put("http://purl.org/escidoc/metadata/profiles/0.1/virrelement", "virr");
        loadOptions.setLoadSubstituteNamespaces(otherNs);
        saveOptions.setSavePrettyPrint();
        saveOptions.setSaveAggressiveNamespaces();
        saveOptions.setSaveSuggestedPrefixes(prefixes);
        saveOptions.setUseDefaultNamespace();
        XmlObject reloaded = null;
        try
        {
            reloaded = XmlObject.Factory.parse(xo.xmlText(), loadOptions);
            return reloaded.xmlText(saveOptions);
        }
        catch (XmlException e)
        {
            logger.info(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static void collectPids(String[] values)
    {
        String DB_URL = "jdbc:postgresql://localhost:5432/batch-updates";
        String DB_USER = "postgres";
        String DB_PASSWD = "postgres";
        String DB = "PostgreSQL";
        String DB_DRIVER_CLASS = "org.postgresql.Driver";
        String sql = "INSERT INTO existing_pids(id, type, \"rels-ext-version\", opid, vpid, cpid, context, metadata) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        try
        {
            Class.forName(DB_DRIVER_CLASS);
        }
        catch (ClassNotFoundException cnfe)
        {
            logger.info(cnfe.getMessage());
            cnfe.printStackTrace();
        }
        try
        {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWD);
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, values[0]);
            stmt.setString(2, values[1]);
            stmt.setInt(3, Integer.valueOf(values[2]));
            stmt.setString(4, values[3]);
            stmt.setString(5, values[4]);
            stmt.setString(6, values[5]);
            stmt.setString(7, values[6]);
            stmt.setString(8, values[7]);
            stmt.execute();
        }
        catch (SQLException sqle)
        {
            logger.info(sqle.getMessage());
            sqle.printStackTrace();
        }
        finally
        {
            try
            {
                stmt.close();
                conn.close();
            }
            catch (SQLException e)
            {
                logger.info(e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    public static void transformFoxml()
    {
        ArrayList<File> files = null;
        InputStream rdfStream = null;
        String resourceUri = null;
        String metadata = null;
        String transMD = null;
        String transformed = null;
        String[] props = null;
        XmlObject xo = null;
        FileWriter fw = null;
        boolean updateRequired = true;
        long time = -System.currentTimeMillis();
        files = fileList(new File(System.getenv("FEDORA_HOME") + "/data/objects/2008/1106"));
        logger.info("attempting to transform " + files.size() + " files");
        int filenum = 0;
        for (File f : files)
        {
            filenum = filenum + 1;
            DigitalObjectDocument dodo;
            try
            {
                dodo = DigitalObjectDocument.Factory.parse(f);
                transformed = xsltTransformation(null, f, publication);
                //System.out.println(transformed);
                fw = new FileWriter(f);
                fw.write(transformed);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                try
                {
                    fw.flush();
                    fw.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
