package de.mpg.escidoc.services.transformation.transformations.otherFormats.mets;

import gov.loc.mets.DivType;
import gov.loc.mods.v3.ModsDocument;
import gov.loc.mods.v3.ModsType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.purl.escidoc.metadata.profiles.x01.virrelement.VirrelementDocument;

import de.escidoc.schemas.container.x08.ContainerDocument;
import de.escidoc.schemas.container.x08.ContainerDocument.Container;
import de.escidoc.schemas.item.x09.ItemDocument;
import de.escidoc.schemas.metadatarecords.x05.MdRecordDocument.MdRecord;
import de.escidoc.schemas.tableofcontent.x01.DivDocument.Div;
import de.escidoc.schemas.tableofcontent.x01.PtrDocument.Ptr;
import de.escidoc.schemas.tableofcontent.x01.TocDocument;
import de.mpg.escidoc.services.common.DataGathering;
import de.mpg.escidoc.services.common.valueobjects.RelationVO;
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
    
    @EJB
    private DataGathering dataGathering;
    private Div physicalRootDiv;
    private ModsType volumeMods;

    /**
     * Public Constructor METSTransformation.
     */
    public METSTransformation()
    {
        try
        {
            InitialContext initialContext = new InitialContext();
            this.dataGathering = (DataGathering) initialContext.lookup(DataGathering.SERVICE_NAME);
        }
        catch (NamingException e)
        {
            this.logger.error("could not find data gathering service",e);
        }
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
        String volumeContainerId = null;
        try
        {
            // Dummy root div
            Div div = tocDoc.getToc().getDiv();
            Div[] children = div.getDivArray();
            for (int i = 0; i < children.length; i++)
            {
                if (children[i].getTYPE().equals("logical"))
                {
                    volumeContainerId = children[i].getDivArray(0).getPtrArray(0).getHref();
                    // Id is returned as a href => extract id from link
                    int le = volumeContainerId.split("/").length - 1;
                    volumeContainerId = volumeContainerId.split("/")[le];
                }
            }
            
            volumeMods = retrieveMods(volumeContainerId);
            
            List<RelationVO> multiVolumeContainerRel = this.dataGathering.findParentContainer(this.login.loginSysAdmin(), volumeContainerId);
            if (multiVolumeContainerRel!=null && multiVolumeContainerRel.size()>0)
            {
                String multiVolContainerId = multiVolumeContainerRel.get(0).getSourceItemRef().getObjectId();
                ModsType multiVolMods = retrieveMods(multiVolContainerId);
                
                //Set missing mods records from multivolume to volume
                if (volumeMods.getTitleInfoArray().length == 0)
                {
                    volumeMods.setTitleInfoArray(multiVolMods.getTitleInfoArray());
                }
                if (volumeMods.getNameArray().length == 0)
                {
                    volumeMods.setNameArray(multiVolMods.getNameArray());
                }
                if (volumeMods.getSubjectArray().length == 0)
                {
                    volumeMods.setSubjectArray(multiVolMods.getSubjectArray());
                }
                if (volumeMods.getNoteArray().length == 0)
                {
                    volumeMods.setNoteArray(multiVolMods.getNoteArray());
                }
                if (volumeMods.getOriginInfoArray().length == 0)
                {
                    volumeMods.setOriginInfoArray(multiVolMods.getOriginInfoArray());
                }
                if (volumeMods.getOriginInfoArray().length == 0)
                {
                    volumeMods.setOriginInfoArray(multiVolMods.getOriginInfoArray());
                }
                
            }
            this.writeMETS.createDmdSec(volumeMods, "dmd" + String.valueOf(this.dmdIdCounter++));
        }
        catch (Exception e)
        {
            this.logger.error("Creation of dmdSec for METS document failed.", e);
            throw new RuntimeException(e);
        }
    }
    
    
    private ModsType retrieveMods(String containerId)
    {
        ModsType mods = null;
        try
        {
            String xml = ServiceLocator.getContainerHandler().retrieve(containerId);
            ContainerDocument cDoc = ContainerDocument.Factory.parse(xml);
            Container container = cDoc.getContainer();
            MdRecord[] mdrecords = container.getMdRecords().getMdRecordArray();
            for (MdRecord mdr : mdrecords)
            {
                if (mdr.getName().equals("escidoc"))
                {
//                XmlCursor modsCursor = mdr.newCursor();
//                String nsuri = "http://www.loc.gov/mods/v3";
//                String namespace = "declare namespace mods='" + nsuri + "';";
//                //modsCursor.selectPath(namespace+"./*/*");
//                modsCursor.selectPath(namespace + "./mods:virr-book/mods:mods");
//                modsCursor.toNextSelection();
//                System.out.println(modsCursor.xmlText());        
//                ModsDocument modsDoc = ModsDocument.Factory.parse(modsCursor.xmlText());
//                System.out.println(mdr.xmlText());
                    
                	
                	XmlCursor modsCursor = mdr.newCursor();
                	modsCursor.toFirstChild();
                	modsCursor.toFirstChild();
                	ModsDocument modsDoc = ModsDocument.Factory.parse(modsCursor.xmlText());
                	modsCursor.dispose();
                	//VirrelementDocument virrElementDoc= VirrelementDocument.Factory.parse(mdr.xmlText());
                    //ModsDocument modsDoc = ModsDocument.Factory.parse(mdr.xmlText());
                    mods = modsDoc.getMods();
                }
            }
        }
        catch (Exception e)
        {
            this.logger.error("Could not retrieve MODS metadata from container "+containerId, e);
        }
        return mods;
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
        this.writeMETS.createStructMap(this.writeMETS.getTypePHYSICAL(), null, null);
        this.writeMETS.createFileSec();
        this.writeMETS.createFileGroup(this.writeMETS.getFileGrpDEFAULT());
        this.writeMETS.createFileGroup(this.writeMETS.getFileGrpMIN());
        this.writeMETS.createFileGroup(this.writeMETS.getFileGrpMAX());
        try
        {
            
            // Dummy root div
            Div div = tocDoc.getToc().getDiv();
            Div[] children = div.getDivArray();
            physicalRootDiv = null;
            for (int i = 0; i < children.length; i++)
            {
                if (children[i].getTYPE().equals("physical"))
                {
                    physicalRootDiv = children[i];
                }
            }
            Div[] physChilds = physicalRootDiv.getDivArray();
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
                if (children[i].getTYPE().equals("logical"))
                {
                    logical = children[i];
                }
            }
            // Create the root element for the logical structMap
            Div logRoot = logical.getDivArray(0);
            
            String label = null;
            try
            {
                label = volumeMods.getTitleInfoArray(0).getTitleArray(0);
            }
            catch (Exception e)
            {
                logger.info("No title for volume found", e);
            }

            //create struct map for book root element and set volume title as label 
            this.writeMETS.createStructMap(this.writeMETS.getTypeLOGICAL(), logRoot.getTYPE(), label);
            
            this.writeMETS.createStructLink();
            
            //create a struct link for the book element, linked with first page (required by DFG-Viewer)
            this.writeMETS.addStructLink("logstruct0", physicalRootDiv.getDivArray(0).getID());
            
            
            
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
        if(tocDiv.getTYPE().equals("structural-element") || tocDiv.getTYPE().equals("book"))
        {
            
            XmlObject[] metadataChildren = tocDiv.selectChildren(new QName("http://purl.org/escidoc/metadata/profiles/0.1/virrelement","virrelement"));
            if (metadataChildren.length>0)
            {
                DivType metsDivNew = metsDivParent.addNewDiv();
                this.currentLogicalMetsDivId = "logstruct" + this.divCounter++;
                metsDivNew.setID(this.currentLogicalMetsDivId);
                try
                {
                    XmlObject virrElementChild = metadataChildren[0];
                    VirrelementDocument virrMetadataDoc = VirrelementDocument.Factory.parse(virrElementChild.getDomNode());
                    ModsType mods = virrMetadataDoc.getVirrelement().getMods();
                    String dmdId = "dmd" + String.valueOf(this.dmdIdCounter++);
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
                    this.logger.error("Error while parsing structural metadata of TOC div",e);
                }
                
                
                for(Div childDiv : tocDiv.getDivArray())
                {
                    createLogicalStructMapRec(childDiv, metsDivNew);
                }
            }
        }
        else if(tocDiv.getTYPE().equals("page"))
        {
            this.writeMETS.addStructLink(this.currentLogicalMetsDivId, tocDiv.getPtrArray(0).getHref());
            
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
            TocDocument toc = null;
            
            //workaroound due to framework bug: login as sysadmin. Currently only logged-in users can retrieve
            //components that have a latest-version pendings
            /*
            String userHandle = this.login.loginSysAdmin();
            if (this.login.loginSysAdmin() != null)
            {
                GetMethod get = new GetMethod(tocUrl.toString());
                get.setFollowRedirects(false);
                get.setRequestHeader("Cookie", "escidocCookie=" + userHandle);
                HttpClient client = new HttpClient();
                ProxyHelper.executeMethod(client, get);
                if (get.getStatusCode() == HttpServletResponse.SC_OK)
                {
                    toc = TocDocument.Factory.parse(get.getResponseBodyAsStream());
                }
            }
            else
            {
                toc = TocDocument.Factory.parse(tocUrl);
            }
            */
            
            toc = TocDocument.Factory.parse(tocUrl);
            
            
            return toc;
        }
        catch (Exception e)
        {
            this.logger.error("Could not retrieve or parse TOC Document from component.", e);
            throw new RuntimeException(e);
        }
       
    }
    
    
}
