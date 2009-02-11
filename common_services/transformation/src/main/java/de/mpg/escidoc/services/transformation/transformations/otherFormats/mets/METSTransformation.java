package de.mpg.escidoc.services.transformation.transformations.otherFormats.mets;

import gov.loc.mods.v3.ModsDocument;
import gov.loc.mods.v3.ModsType;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;

import de.escidoc.schemas.container.x07.ContainerDocument;
import de.escidoc.schemas.container.x07.ContainerDocument.Container;
import de.escidoc.schemas.metadatarecords.x04.MdRecordDocument.MdRecord;
import de.escidoc.schemas.tableofcontent.x01.DivDocument.Div;
import de.escidoc.schemas.tableofcontent.x01.PtrDocument.Ptr;
import de.escidoc.schemas.toc.x06.TocDocument;
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
    public byte[] transformToMETS(String escidocToc) throws RuntimeException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream in = null;
        try
        {
            // Create mets id out of escidoc id
            String metsId = this.getItemIdentifier(escidocToc);
            this.getBaseUrl();
            // Create different METS sections
            this.createDmdSec(escidocToc);
            this.createAmdSec(escidocToc);
            this.createPhysicals(escidocToc);
            this.createLogicals(escidocToc);
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
    
    private String getItemIdentifier(String escidocToc)
    {
        String id = null;
        
        try
        {
            TocDocument escidocTocDoc = TocDocument.Factory.parse(escidocToc);
            id = escidocTocDoc.getToc().getObjid();
        }
        catch (XmlException e)
        {
            this.logger.error("Creation of TOC document failed.", e);
            throw new RuntimeException(e);
        }
        
        return id;
    }

    /**
     * Gets the escidoc:toc values for mets dmd section (from the book container).
     * 
     * @param escidocToc
     * @throws RuntimeException
     */
    private void createDmdSec(String escidocToc) throws RuntimeException
    {
        String dmdId = "dmd1";
        String containerId = null;
        ModsType mods = null;
        try
        {
            TocDocument escidocTocDoc = TocDocument.Factory.parse(escidocToc);
            // Dummy root div
            Div div = escidocTocDoc.getToc().getToc().getDiv();
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
                    ModsDocument modsDoc = ModsDocument.Factory.parse(mdr.xmlText());
                    mods = modsDoc.getMods();
                }
            }
            this.writeMETS.createDmdSec(mods, dmdId);
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
    private void createAmdSec(String escidocToc)
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
    private void createPhysicals(String escidocToc) throws RuntimeException
    {
        int divId = 1;
        this.writeMETS.createStructMap(this.writeMETS.getTypePHYSICAL(), null);
        this.writeMETS.createFileSec();
        this.writeMETS.createFileGroup(this.writeMETS.getFileGrpDEFAULT());
        this.writeMETS.createFileGroup(this.writeMETS.getFileGrpMIN());
        this.writeMETS.createFileGroup(this.writeMETS.getFileGrpMAX());
        try
        {
            TocDocument escidocTocDoc = TocDocument.Factory.parse(escidocToc);
            // Dummy root div
            Div div = escidocTocDoc.getToc().getToc().getDiv();
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
        catch (XmlException e)
        {
            this.logger.error("Creation of physical parts for METS document failed.", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the escidoc:toc values for mets logical structMap.
     * 
     * @param escidocToc @ throws RuntimeException
     * @throws RuntimeException
     */
    public void createLogicals(String escidocToc) throws RuntimeException
    {
        int divId = 1;
        Div currentDiv;
        Div[] currentChilds;
        Div childX;
        Div[] childChildren;
        TocDocument escidocTocDoc;
        try
        {
            escidocTocDoc = TocDocument.Factory.parse(escidocToc);
            // Dummy root div
            Div div = escidocTocDoc.getToc().getToc().getDiv();
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
            Div logRoot = logical.getDivArray()[0];
            this.writeMETS.createStructMap(this.writeMETS.getTypeLOGICAL(), logRoot.getTYPE());
            // this.writeMETS.createStructLink();
            //
            // currentDiv = log_root;
            // currentChilds = currentDiv.getDivArray();
            //
            // for(int i=0; i< currentChilds.length; i++){
            // childX = currentChilds[i];
            // //Add all divs to the structMap
            // this.writeMETS.addToStructMap(this.writeMETS.getType_LOGICAL(), new String[]{childX.getID()},
            // childX.getORDER()+"", childX.getORDERLABEL(), "log"+divId, childX.getTYPE(), false);
            // if (childX.getPtrArray().length >0)
            // {
            // //create a structLink
            // for (int y=0; y<childX.getPtrArray().length;y++){
            // System.out.println(childX.getPtrArray()[y].getHref());
            // this.writeMETS.addStructLink("log"+divId, childX.getPtrArray()[y].getHref());
            // }
            // }
            //
            // divId ++;
            // //Add all childs to the structMap
            // while(childX.getDivArray().length > 0)
            // {
            // childChildren = childX.getDivArray();
            // for (int x=0; x < childChildren.length; x++ ){
            // this.writeMETS.addToStructMap(this.writeMETS.getType_LOGICAL(), new String[]{childChildren[x].getID()},
            // childChildren[x].getORDER()+"", childChildren[x].getORDERLABEL(), divId+"", childChildren[x].getTYPE(),
            // true);
            // if (childX.getPtrArray().length >0)
            // {
            // //create a structLink
            // for (int y=0; y<childX.getPtrArray().length;y++){
            // System.out.println(childX.getPtrArray()[y].getHref());
            // this.writeMETS.addStructLink("log"+divId, childX.getPtrArray()[y].getHref());
            // }
            // }
            // childX =childChildren[x];
            // divId ++;
            // }
            // }
            // }
        }
        catch (XmlException e)
        {
            this.logger.error("Creation of logical parts for METS document failed.", e);
            throw new RuntimeException(e);
        }
    }
}
