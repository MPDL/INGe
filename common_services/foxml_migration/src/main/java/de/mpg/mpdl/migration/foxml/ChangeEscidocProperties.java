package de.mpg.mpdl.migration.foxml;

import fedora.fedoraSystemDef.foxml.DatastreamType;
import fedora.fedoraSystemDef.foxml.DigitalObjectDocument;
import fedora.fedoraSystemDef.foxml.XmlContentType;
import gov.loc.mods.v3.ModsDocument;
import gov.loc.www.zing.srw.RecordType;
import gov.loc.www.zing.srw.SearchRetrieveRequestType;
import gov.loc.www.zing.srw.SearchRetrieveResponseType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;

import org.apache.axis.message.MessageElement;
import org.apache.axis.types.NonNegativeInteger;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.RDFVisitor;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;

import de.escidoc.schemas.item.x08.ItemDocument;
import de.escidoc.schemas.searchresult.x07.SearchResultRecordDocument;
import de.escidoc.schemas.searchresult.x07.SearchResultRecordDocument.SearchResultRecord;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.mpdl.migration.util.XBeanUtils;

public class ChangeEscidocProperties
{
    private static ArrayList<File> allFiles = new ArrayList<File>();
    private static ArrayList<String> fileNames = new ArrayList<String>();

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        try
        {
            //changeContentModel();
            view();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void changeContentModel() throws Exception
    {
        String qry2 = "escidoc.content-model.objid=escidoc:TOC";
        SearchRetrieveRequestType srrt = new SearchRetrieveRequestType();
        srrt.setVersion("1.1");
        srrt.setRecordPacking("xml");
        srrt.setMaximumRecords(new NonNegativeInteger("1000"));
        srrt.setQuery(qry2);
        SearchRetrieveResponseType result = ServiceLocator.getSearchHandler("escidoc_all",
                new URL("http://coreservice.mpdl.mpg.de")).searchRetrieveOperation(srrt);
        RecordType[] recs = result.getRecords().getRecord();
        for (RecordType rec : recs)
        {
            MessageElement[] elems = rec.getRecordData().get_any();
            for (MessageElement me : elems)
            {
                SearchResultRecordDocument srrDoc = SearchResultRecordDocument.Factory.parse(me.getAsString());
                SearchResultRecord srRec = srrDoc.getSearchResultRecord();
                XmlCursor srRecCur = srRec.newCursor();
                String nsuri = "http://www.escidoc.de/schemas/item/0.8";
                String namespace = "declare namespace item='" + nsuri + "';";
                srRecCur.selectPath(namespace + "./item:item");
                srRecCur.toNextSelection();
                ItemDocument item = ItemDocument.Factory.parse(srRecCur.xmlText());
                XmlCursor itemCur = item.getItem().getMdRecords().getMdRecordArray(0).newCursor();
                itemCur.toFirstChild();
                itemCur.toFirstChild();
                ModsDocument mods = ModsDocument.Factory.parse(itemCur.xmlText());
                System.out.println(item.getItem().getObjid() + " "
                        + mods.getMods().getTitleInfoArray(0).getTitleArray(0));
                fileNames.add(item.getItem().getObjid().replace(":", "_"));
                XBeanUtils.validation(item);
                srRecCur.dispose();
                itemCur.dispose();
                if (!item.validate())
                {
                    System.out.println("NO VALID ITEM !!!");
                }
            }
        }
        System.out.println("list contains " + fileNames.size() + " toc items");
    }

    public static void view()
    {
        ArrayList<File> files = null;
        //System.out.println(System.getenv("FEDORA_HOME"));
        //files = fileList(new File(System.getenv("FEDORA_HOME") + "/data/objects/2010"));
        files = fileList(new File("/opt/fedora/data/objects"));

        int filenum = 0;
        int pubitem = 0;
        for (File f : files)
        {
            //if (fileNames.contains(f.getName()))
            //{
                InputStream rdfStream = null;
                String resourceUri = null;
                String props = null;
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
                            props = getPubItems(rdfStream, resourceUri, lastRelsExt);
                            if (props != null)
                            {
                                //System.out.println(f.getName() + " has content model: " + props);
                                //XmlObject xo = XmlObject.Factory.parse(props);
                                //rdf.set(xo);
                                //System.out.println(rdf.xmlText());
                                pubitem++;
                            }
                        }
                    }
                }
                catch (XmlException e)
                {
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            //}
        }
        System.out.println("checking " + filenum + " files");
        System.out.println("number of pubitems: " + pubitem);
    }

    public static File getResources(InputStream rdfStream, String resourceUri, int streamVersion)
    {
        String id = resourceUri.substring(resourceUri.indexOf("/") + 1);
        String resourceType = null;
        String context = null;
        String cModel = null;
        Model model = ModelFactory.createDefaultModel();
        model.read(rdfStream, "");
        Resource about = model.getResource(resourceUri);
        resourceType = about.getRequiredProperty(RDF.type).getResource().getLocalName();
        if (resourceType.equalsIgnoreCase("item"))
        {
            about.removeAll(ESCIDOCPROPERTIES.contentmodel);
            about.removeAll(ESCIDOCPROPERTIES.contentmodeltitle);
            about.addProperty(ESCIDOCPROPERTIES.contentmodel, model.createResource("info:fedora/escidoc:1234567"));
            about.addProperty(ESCIDOCPROPERTIES.contentmodeltitle, "TOC 4 VIRR");
            
            cModel = about.getProperty(ESCIDOCPROPERTIES.contentmodel).asTriple().toString();
            File tmpFile = null;
            try
            {
                tmpFile = File.createTempFile("temp", "xml");
                model.write(new FileOutputStream(tmpFile));
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return tmpFile;
            
        }
        return null;
    }
    
    public static String getPubItems(InputStream rdfStream, String resourceUri, int streamVersion)
    {
        String id = resourceUri.substring(resourceUri.indexOf("/") + 1);
        String resourceType = null;
        String context = null;
        String cModel = null;
        Model model = ModelFactory.createDefaultModel();
        model.read(rdfStream, "");
        Resource about = model.getResource(resourceUri);
        resourceType = about.getRequiredProperty(RDF.type).getResource().getLocalName();
        if (resourceType.equalsIgnoreCase("item"))
        {
            Statement s = about.getProperty(ESCIDOCPROPERTIES.contentmodel);
            if (s.getObject().isResource() && s.getObject().toString().equals("info:fedora/escidoc:persistent4"))
            {
                return s.getObject().toString();
            }
        }
        return null;
    }

    public static ArrayList<File> fileList(File baseDir)
    {
        if (baseDir.isDirectory())
        {
            File[] files = baseDir.listFiles();
            for (File foxml : files)
            {
                if (foxml.isFile() && foxml.getName().matches("escidoc_\\w*"))
                {
                    allFiles.add(foxml);
                }
                else
                {
                    fileList(foxml);
                }
            }
        }
        return allFiles;
    }
}
