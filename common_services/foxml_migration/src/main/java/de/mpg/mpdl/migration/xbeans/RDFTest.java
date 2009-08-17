package de.mpg.mpdl.migration.xbeans;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

import de.mpg.mpdl.migration.foxml.ESCIDOCPROPERTIES;
import de.mpg.mpdl.migration.foxml.Foxml;
import de.mpg.mpdl.migration.foxml.GWDGPidService;
import de.mpg.mpdl.migration.foxml.MigrationConstants;
import fedora.fedoraSystemDef.foxml.DatastreamType;
import fedora.fedoraSystemDef.foxml.DatastreamVersionType;
import fedora.fedoraSystemDef.foxml.DigitalObjectDocument;
import fedora.fedoraSystemDef.foxml.XmlContentType;

public class RDFTest implements MigrationConstants
{
    static Logger pidlogger = Logger.getLogger("pidreplacement");
    static TreeMap<String, String> dummyPIDs = null;
    
    public static void main(String[] args)
    {
        replacePIDs();
    }
    
    public static void replacePIDs()
    {
        ArrayList<File> files = null;
        String resourceURI = null;
        String props[] = null;
        files = Foxml.fileList(new File(System.getenv("FEDORA_HOME") + "/data/objects/2008/1106"));
        pidlogger.info("attempting to replace PIDs in " + files.size() + " files");
        int filenum = 0;
        for (File f : files)
        {
            dummyPIDs = new TreeMap<String, String>();
            filenum = filenum + 1;
            DigitalObjectDocument dodo = null;
            try
            {
                dodo = DigitalObjectDocument.Factory.parse(f);
                DatastreamType[] streams = dodo.getDigitalObject().getDatastreamArray();
                for (DatastreamType stream : streams)
                {
                    if (stream.getID().equalsIgnoreCase("RELS-EXT"))
                    {
                        DatastreamVersionType[] versions = stream.getDatastreamVersionArray();
                        for (DatastreamVersionType version : versions)
                        {
                            XmlContentType rdf = version.getXmlContent();
                            resourceURI = "info:fedora/escidoc:"
                                + f.getName().substring(f.getName().indexOf("_") + 1, f.getName().length());
                            props = Foxml.getResourceType(rdf.newInputStream(), resourceURI, Integer.valueOf(version.getID().substring(version.getID().indexOf(".") + 1)));
                            //pidlogger.info(props[1] + "   " + props[0] + " in context " + props[3] + "   RELS-EXT." + props[2]);
                            getExistingPids(rdf.newInputStream(), resourceURI);
                            //XmlObject xo = XmlObject.Factory.parse(changed);
                            //rdf.set(xo);
                        }
                    }
                }
                //dodo.save(f);
                System.out.println(props[1] + "   " + props[0] + " in context " + props[3] + "   RELS-EXT." + props[2]);
                System.out.println(dummyPIDs.keySet().toString());
                
                for (String s : dummyPIDs.keySet())
                {
                    String id = s.substring(s.indexOf("escidoc:") + 8);
                    String handle = registerNewPID(id);
                    dummyPIDs.put(s, handle);
                }

                //String id = dummyPIDs.firstKey().substring(dummyPIDs.firstKey().indexOf("escidoc:") + 8);
                //String url = TEST_BASE_URL + id;
                //String handle = GWDGPidService.registerNewPID(url);
                //String handle = GWDGPidService.findHandle4URL(url);
                //System.out.println("registered handle: " + handle);
                System.out.println(dummyPIDs.toString());
                for (DatastreamType stream : streams)
                {
                    if (stream.getID().equalsIgnoreCase("RELS-EXT"))
                    {
                        DatastreamVersionType[] versions = stream.getDatastreamVersionArray();
                        for (DatastreamVersionType version : versions)
                        {
                            XmlContentType rdf = version.getXmlContent();
                            resourceURI = "info:fedora/escidoc:"
                                + f.getName().substring(f.getName().indexOf("_") + 1, f.getName().length());
                            props = Foxml.getResourceType(rdf.newInputStream(), resourceURI, Integer.valueOf(version.getID().substring(version.getID().indexOf(".") + 1)));
                            //pidlogger.info(props[1] + "   " + props[0] + " in context " + props[3] + "   RELS-EXT." + props[2]);
                            String changed = replaceExistingPids(rdf.newInputStream(), resourceURI);
                            System.out.println(changed);
                            XmlObject xo = XmlObject.Factory.parse(changed);
                            rdf.set(xo);
                        }
                    }
                }
                dodo.save(f);
            }
            catch (XmlException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    public static void getExistingPids(InputStream is, String uri)
    {
        String objectPid = null;
        String versionPid = null;
        String releasePid = null;
        String contentPid = null;
        Model model = ModelFactory.createDefaultModel();
        model.read(is, "");
        Resource about = model.getResource(uri);
        String resourceType = about.getRequiredProperty(RDF.type).getResource().getLocalName();
        if (resourceType.equalsIgnoreCase("item") || resourceType.equalsIgnoreCase("container"))
        {
            if (about.getProperty(ESCIDOCPROPERTIES.pid) != null)
            {
                objectPid = about.getProperty(ESCIDOCPROPERTIES.pid).getString();
                dummyPIDs.put(objectPid, null);
            }
            if (about.getProperty(ESCIDOCPROPERTIES.versionpid) != null)
            {
                versionPid = about.getProperty(ESCIDOCPROPERTIES.versionpid).getString();
                dummyPIDs.put(versionPid, null);
            }
            if (about.getProperty(ESCIDOCPROPERTIES.releasepid) != null)
            {
                releasePid = about.getProperty(ESCIDOCPROPERTIES.releasepid).getString();
                dummyPIDs.put(releasePid, null);
            }
        }
    }
    
    public static String replaceExistingPids(InputStream is, String uri)
    {
        String versionPid = null;
        String releasePid = null;
        Model model = ModelFactory.createDefaultModel();
        model.read(is, "");
        Resource about = model.getResource(uri);
        String resourceType = about.getRequiredProperty(RDF.type).getResource().getLocalName();
        if (resourceType.equalsIgnoreCase("item") || resourceType.equalsIgnoreCase("container"))
        {
            if (about.hasProperty(ESCIDOCPROPERTIES.pid))
            {
                String pid = dummyPIDs.get(dummyPIDs.firstKey());
                about.removeAll(ESCIDOCPROPERTIES.pid);
                about.addProperty(ESCIDOCPROPERTIES.pid, pid);
            }
            if (about.hasProperty(ESCIDOCPROPERTIES.versionpid))
            {
                versionPid = about.getProperty(ESCIDOCPROPERTIES.versionpid).getString();
                String pid = dummyPIDs.get(versionPid);
                about.removeAll(ESCIDOCPROPERTIES.versionpid);
                about.addProperty(ESCIDOCPROPERTIES.versionpid, pid);
            }
            if (about.hasProperty(ESCIDOCPROPERTIES.releasepid))
            {
                releasePid = about.getProperty(ESCIDOCPROPERTIES.releasepid).getString();
                String pid = dummyPIDs.get(releasePid);
                about.removeAll(ESCIDOCPROPERTIES.releasepid);
                about.addProperty(ESCIDOCPROPERTIES.releasepid, pid);
            }
        }
        
        //pidlogger.info("        " + objectPid + "        " + versionPid + "        " + releasePid + "        " + contentPid);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        model.write(out, "RDF/XML");
        return out.toString();
    }
    
    public static String registerNewPID(String id)
    {
        String newPID = null;
        newPID = TEST_BASE_URL + id;
        return newPID;
    }
}
