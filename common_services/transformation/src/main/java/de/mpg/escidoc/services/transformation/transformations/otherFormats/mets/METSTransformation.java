package de.mpg.escidoc.services.transformation.transformations.otherFormats.mets;

import gov.loc.mets.DivType;
import gov.loc.mods.v3.ModsDocument;
import gov.loc.mods.v3.ModsType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;

import de.escidoc.schemas.container.x07.ContainerDocument;
import de.escidoc.schemas.container.x07.ContainerDocument.Container;
import de.escidoc.schemas.item.x07.ItemDocument;
import de.escidoc.schemas.metadatarecords.x04.MdRecordDocument.MdRecord;
import de.escidoc.schemas.tableofcontent.x01.TocDocument;
import de.escidoc.schemas.tableofcontent.x01.DivDocument.Div;
import de.escidoc.schemas.tableofcontent.x01.PtrDocument.Ptr;
import de.mpg.escidoc.metadataprofile.schema.x01.virrelement.VirrelementDocument;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * This class provides METS transformation for a escidoc objects.
 * 
 * @author kleinfe1
 */
public class METSTransformation
{
    private WriteMETSData writeMETS = new WriteMETSData();
    private String baseURL = null;
    private Logger logger = Logger.getLogger(getClass());
    private Login login = new Login();
    
    //idCounters
    private int dmdIdCounter = 0;
    private int divCounter = 1;
    private String currentLogicalMetsDivId;

    /**
     * Public Constructor METSTransformation.
     */
    public METSTransformation()
    {
    }

    /**
     * transform To METS.
     * 
     * @param eScidocId the id of the escidoc object which has to be transformed
     * @return MetsDocument
     * @throws RuntimeException
     */
    public byte[] transformToMETS(String escidocTocItem) throws RuntimeException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream in = null;
        try
        {
            // Create mets id out of escidoc id
            ItemDocument itemDoc = ItemDocument.Factory.parse(escidocTocItem);
            String metsId = itemDoc.getItem().getObjid();
            this.getBaseUrl();
            
            // Create different METS sections
            TocDocument tocDoc = getTocDoc(itemDoc);
            this.createDmdSec(tocDoc);
            this.createAmdSec(tocDoc);
            this.createPhysicals(tocDoc);
            this.createLogicals(tocDoc);
            // Create METS document out of these sections
            XmlOptions xOpts = new XmlOptions();
            xOpts.setSavePrettyPrint();
            xOpts.setSavePrettyPrintIndent(4);
            Map<String, String> namespaces = new HashMap<String, String>();
            namespaces.put("http://www.loc.gov/mods/v3", "mods");
            namespaces.put("http://www.w3.org/1999/xlink", "xlink");
            namespaces.put("http://dfg-viewer.de/", "dv");
            xOpts.setSaveSuggestedPrefixes(namespaces);
            in = this.writeMETS.getMetsDoc(metsId).newInputStream(xOpts);
            int c;
            while ((c = in.read()) != -1)
            {
                baos.write((char) c);
            }
        }
        catch (Exception e)
        {
            this.logger.error("Creation of METS document failed.", e);
            throw new RuntimeException(e);
        }
        return baos.toByteArray();
    }

    private void getBaseUrl()
    {
        try
        {
            this.baseURL = PropertyReader.getProperty("escidoc.framework_access.framework.url");
        }
        catch (Exception e)
        {
            this.logger.error("An error occurred while reading the framework URL from properties.", e);
            throw new RuntimeException(e);
        }
    }
    
   

    /**
     * Gets the escidoc:toc values for the first mets dmd section (from the book container).
     * 
     * @param escidocTocItem
     * @throws RuntimeException
     */
    private void createDmdSec(TocDocument tocDoc) throws RuntimeException
    {
        String containerId = null;
        ModsType mods = null;
        try
        {
            // Dummy root div
            Div div = tocDoc.getToc().getDiv();
            Div[] children = div.getDivArray();
            for (int i = 0; i < children.length; i++)
            {
                if (children[i].getTYPE().equals(this.writeMETS.getTypeLOGICAL()))
                {
                    containerId = children[i].getDivArray(0).getPtrArray(0).getHref();
                    // Id is returned as a href => extract id from link
                    int le = containerId.split("/").length - 1;
                    containerId = containerId.split("/")[le];
                }
            }
            String xml = ServiceLocator.getContainerHandler(this.login.loginSysAdmin()).retrieve(containerId);
            ContainerDocument cDoc = ContainerDocument.Factory.parse(xml);
            Container container = cDoc.getContainer();
            MdRecord[] mdrecords = container.getMdRecords().getMdRecordArray();
            for (MdRecord mdr : mdrecords)
            {
                if (mdr.getName().equals("escidoc"))
                {
//                    XmlCursor modsCursor = mdr.newCursor();
//                    String nsuri = "http://www.loc.gov/mods/v3";
//                    String namespace = "declare namespace mods='" + nsuri + "';";
//                    //modsCursor.selectPath(namespace+"./*/*");
//                    modsCursor.selectPath(namespace + "./mods:virr-book/mods:mods");
//                    modsCursor.toNextSelection();
//                    System.out.println(modsCursor.xmlText());        
//                    ModsDocument modsDoc = ModsDocument.Factory.parse(modsCursor.xmlText());
//                    System.out.println(mdr.xmlText());
                    VirrelementDocument virrElementDoc= VirrelementDocument.Factory.parse(mdr.xmlText());
                    //ModsDocument modsDoc = ModsDocument.Factory.parse(mdr.xmlText());
                    mods = virrElementDoc.getVirrelement().getMods();
                }
            }
            this.writeMETS.createDmdSec(mods, "dmd" + String.valueOf(dmdIdCounter++));
        }
        catch (Exception e)
        {
            this.logger.error("Creation of dmdSec for METS document failed.", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the escidoc:toc values for mets amd section.
     * 
     * @param escidocToc
     */
    private void createAmdSec(TocDocument tocDoc)
    {
        String owner;
        String logo;
        String url;
        String reference;
        String amdId = "amd1";
        owner = "Max Planck Institute for European Legal History ";
        logo = "http://www.mpier.uni-frankfurt.de/images/minerva_logo.gif ";
        url = "http://www.mpier.uni-frankfurt.de ";
        reference = "http://virr.mpdl.mpg.de/";
        this.writeMETS.createAmdSec(amdId, owner, logo, url, reference);
    }

    /**
     * Gets the escidoc:toc values for mets file section and physical structMap.
     * 
     * @param escidocToc
     * @throws RuntimeException
     */
    private void createPhysicals(TocDocument tocDoc) throws RuntimeException
    {
        int divId = 1;
        this.writeMETS.createStructMap(this.writeMETS.getTypePHYSICAL(), null);
        this.writeMETS.createFileSec();
        this.writeMETS.createFileGroup(this.writeMETS.getFileGrpDEFAULT());
        this.writeMETS.createFileGroup(this.writeMETS.getFileGrpMIN());
        this.writeMETS.createFileGroup(this.writeMETS.getFileGrpMAX());
        try
        {
            
            // Dummy root div
            Div div = tocDoc.getToc().getDiv();
            Div[] children = div.getDivArray();
            Div physical = null;
            for (int i = 0; i < children.length; i++)
            {
                if (children[i].getTYPE().equals(this.writeMETS.getTypePHYSICAL()))
                {
                    physical = children[i];
                }
            }
            Div[] physChilds = physical.getDivArray();
            for (int x = 0; x < physChilds.length; x++)
            {
                Div page = physChilds[x];
                Ptr[] pagePointers = page.getPtrArray();
                String[] ptrIds = new String[3];
                for (int y = 0; y < pagePointers.length; y++)
                {
                    Ptr pointer = pagePointers[y];
                    if (pointer.getUSE() != null)
                    {
                        if (pointer.getUSE().equals(this.writeMETS.getFileGrpMIN()))
                        {
                            this.writeMETS.addFiletoFileGroup(this.writeMETS.getFileGrpMIN(), this.baseURL
                                    + pointer.getHref(), pointer.getID(), pointer.getMIMETYPE());
                            ptrIds[0] = pointer.getID();
                        }
                        if (pointer.getUSE().equals(this.writeMETS.getFileGrpDEFAULT()))
                        {
                            this.writeMETS.addFiletoFileGroup(this.writeMETS.getFileGrpDEFAULT(), this.baseURL
                                    + pointer.getHref(), pointer.getID(), pointer.getMIMETYPE());
                            ptrIds[1] = pointer.getID();
                        }
                        if (pointer.getUSE().equals(this.writeMETS.getFileGrpMAX()))
                        {
                            this.writeMETS.addFiletoFileGroup(this.writeMETS.getFileGrpMAX(), this.baseURL
                                    + pointer.getHref(), pointer.getID(), pointer.getMIMETYPE());
                            ptrIds[2] = pointer.getID();
                        }
                    }
                }
                int order = Integer.parseInt(page.getORDER().toString());
                this.writeMETS.addToStructMap(this.writeMETS.getTypePHYSICAL(), ptrIds, order + "", page
                        .getORDERLABEL(), page.getID(), page.getTYPE(), false);
                divId++;
            }
        }
        catch (Exception e)
        {
            this.logger.error("Creation of physical parts for METS document failed.", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates the logical part of the struct map
     * 
     * @param escidocToc @ throws RuntimeException
     * @throws RuntimeException
     */
    private void createLogicals(TocDocument tocDoc) throws RuntimeException
    {
        
        try
        {
            // Dummy root div
            Div div = tocDoc.getToc().getDiv();
            Div[] children = div.getDivArray();
            Div logical = null;
            for (int i = 0; i < children.length; i++)
            {
                if (children[i].getTYPE().equals(this.writeMETS.getTypeLOGICAL()))
                {
                    logical = children[i];
                }
            }
            // Create the root element for the logical structMap
            Div logRoot = logical.getDivArray(0);
            this.writeMETS.createStructMap(this.writeMETS.getTypeLOGICAL(), logRoot.getTYPE());
            this.writeMETS.createStructLink();
            
            for(Div childDiv : logRoot.getDivArray())
            {
                createLogicalStructMapRec(childDiv, this.writeMETS.getCurrentDiv());
            }
        }
        catch (Exception e)
        {
            this.logger.error("Creation of logical parts for METS document failed.", e);
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Recursive helper method in order to create the logical part of the METS struct map from the escidoc TOC.
     * @param tocDiv
     * @param metsDivParent
     */
    private void createLogicalStructMapRec(Div tocDiv, DivType metsDivParent)
    {
        if(tocDiv.getTYPE().equals("structural-element"))
        {
            
            XmlObject[] metadataChildren = tocDiv.selectChildren(new QName("http://escidoc.mpg.de/metadataprofile/schema/0.1/virrelement","virrelement"));
            if (metadataChildren.length>0)
            {
                DivType metsDivNew = metsDivParent.addNewDiv();
                currentLogicalMetsDivId = "logstruct" + divCounter++;
                metsDivNew.setID(currentLogicalMetsDivId);
                try
                {
                    XmlObject virrElementChild = metadataChildren[0];
                    VirrelementDocument virrMetadataDoc = VirrelementDocument.Factory.parse(virrElementChild.getDomNode());
                    ModsType mods = virrMetadataDoc.getVirrelement().getMods();
                    String dmdId = "dmd" + String.valueOf(dmdIdCounter++);
                    this.writeMETS.createDmdSec(mods, dmdId);
                    List<String> dmdList = new ArrayList<String>();
                    dmdList.add(dmdId);
                    metsDivNew.setDMDID(dmdList);
                    metsDivNew.setTYPE(mods.getGenreArray(0).getStringValue());
                    
                    if (mods.getTitleInfoArray().length > 0 && mods.getTitleInfoArray(0).getTitleArray().length > 0)
                    {
                        metsDivNew.setLABEL(mods.getTitleInfoArray(0).getTitleArray(0));
                    }
                       
                }
                catch (XmlException e)
                {
                    logger.error("Error while parsing structural metadata of TOC div",e);
                }
                
                
                for(Div childDiv : tocDiv.getDivArray())
                {
                    createLogicalStructMapRec(childDiv, metsDivNew);
                }
            }
        }
        else if(tocDiv.getTYPE().equals("page"))
        {
            this.writeMETS.addStructLink(currentLogicalMetsDivId, tocDiv.getPtrArray(0).getHref());
            
        }
        
        
        
    }
    
    
    /**
     * Retrieves the toc as the component of the item
     * @return
     * @throws URISyntaxException 
     * @throws ServiceException 
     * @throws ServiceException
     * @throws URISyntaxException
     * @throws IOException 
     * @throws XmlException 
     * @throws XmlException
     * @throws IOException
     */
    private TocDocument getTocDoc(ItemDocument itemTocDoc)
    {
        try
        {
            String tocHref = itemTocDoc.getItem().getComponents().getComponentArray(0).getContent().getHref();
            URL tocUrl = new URL(ServiceLocator.getFrameworkUrl() + tocHref);
            TocDocument toc = TocDocument.Factory.parse(tocUrl);
            return toc;
        }
        catch (Exception e)
        {
            this.logger.error("Could not retrieve or parse TOC Document from component.", e);
            throw new RuntimeException(e);
        }
       
    }
}
