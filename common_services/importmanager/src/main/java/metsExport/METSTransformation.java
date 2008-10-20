package metsExport;


import gov.loc.mods.v3.ModsDocument;
import gov.loc.mods.v3.ModsType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.w3c.dom.Document;

import de.escidoc.schemas.container.x07.ContainerDocument;
import de.escidoc.schemas.container.x07.ContainerDocument.Container;
import de.escidoc.schemas.metadatarecords.x04.MdRecordDocument.MdRecord;
import de.escidoc.schemas.tableofcontent.x01.DivDocument.Div;
import de.escidoc.schemas.tableofcontent.x01.PtrDocument.Ptr;
import de.escidoc.schemas.toc.x06.TocDocument;
import de.mpg.escidoc.services.common.valueobjects.ContainerVO;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * This class provides METS transformation for a escidoc objects.
 * @author kleinfe1
 *
 */
public class METSTransformation extends XmlIO{

	private WriteMETSData writeMETS = new WriteMETSData();
	private String baseURL = null;
	
	private Logger logger = Logger.getLogger(getClass());
	
	Login login = new Login();
    
	public METSTransformation()
	{
		
	}
	
	/**
	 * transform To METS.
	 * @param eScidocId the id of the escidoc object which has to be transformed
	 * @return MetsDocument
	 */
    public byte[] transformToMETS (String eScidocId) throws Exception
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream in = null;

        try
        {
	        String escidocToc = ServiceLocator.getTocHandler(this.login.loginSysAdmin()).retrieve(eScidocId);
	        //Create mets id out of escidoc id
	        String metsId = eScidocId.replace("escidoc", "mets");
	        
	        this.getBaseUrl();
	        
	        //Create different METS sections
	        this.createDmdSec(escidocToc);
	        this.createAmdSec(escidocToc);
	        this.createPhysicals(escidocToc);
	        
	        //this.createLogicals(escidocToc);
	        
	        //Create METS document out of these sections
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
	        while ((c = in.read()) != -1) {
	        	baos.write((char) c);
	        }
        }
        catch (Exception e)
        {
        	this.logger.error("Creation of METS document failed ", e);
        	throw new Exception();
        }
        
        return baos.toByteArray();
    }

    private void getBaseUrl()
    {
    	try {
			this.baseURL = PropertyReader.getProperty("escidoc.framework_access.framework.url");
		} 
    	catch (IOException e) 
    	{e.printStackTrace();} 
    	catch (URISyntaxException e) 
    	{e.printStackTrace();}
    }
    
    /**
     * Gets the escidoc:toc values for mets dmd section (from the book container).
     * @param escidocToc
     */
	private void createDmdSec(String escidocToc)
	{
		String dmdId = "dmd1";
		String containerId = null;
		ModsType mods = null;
	
		try 
		{
			TocDocument escidocTocDoc = TocDocument.Factory.parse(escidocToc);
			
			//Dummy root div
			Div div = escidocTocDoc.getToc().getToc().getDiv();
			Div[] children = div.getDivArray();
			
			for (int i =0; i< children.length; i++)
			{
				if (children[i].getTYPE().equals(this.writeMETS.getType_LOGICAL()))
				{
					containerId = children[i].getDivArray(0).getPtrArray(0).getHref();
					//Id is returned as a href => extract id from link
					int le = containerId.split("/").length-1;
					containerId = containerId.split("/")[le];
				}
			}
			
			String xml = ServiceLocator.getContainerHandler(this.login.loginSysAdmin()).retrieve(containerId);			
            ContainerDocument cDoc = ContainerDocument.Factory.parse(xml);
            Container container = cDoc.getContainer();
	
            MdRecord[] mdrecords = container.getMdRecords().getMdRecordArray();

            for (MdRecord mdr : mdrecords)
            {
            	if (mdr.getName().equals("escidoc")){
	                XmlCursor modsCursor = mdr.newCursor();
	                modsCursor.selectPath("./*/*");                           
	                modsCursor.toNextSelection();               
	                
	                ModsDocument modsDoc = ModsDocument.Factory.parse(modsCursor.xmlText());
	                mods = modsDoc.getMods();
            	}
            }

			this.writeMETS.createDmdSec(mods, dmdId);
		} 
		catch (Exception e) 
		{this.logger.error("Creation of dmdSec for METS document failed ", e);}
	}
	
    /**
     * Gets the escidoc:toc values for mets amd section.
     * @param escidocToc
     */
	private void createAmdSec(String escidocToc)
	{
		String owner;
		String logo;
		String url;
		String reference;
		String amdId ="amd1";
		
		//TODO: Read this from properties
		owner = "Max Planck Institute for European Legal History ";
		logo = "http://www.mpier.uni-frankfurt.de/images/minerva_logo.gif "; 
		url = "http://www.mpier.uni-frankfurt.de ";
		reference ="http://virr.mpdl.mpg.de/";
			
		this.writeMETS.createAmdSec(amdId, owner, logo, url, reference);
	}
	
    /**
     * Gets the escidoc:toc values for mets file section and physical structMap.
     * @param escidocToc
     */
	private void createPhysicals(String escidocToc)
	{
		int divId=1;
		
		this.writeMETS.createStructMap(this.writeMETS.getType_PHYSICAL(), null);
		this.writeMETS.createFileSec();
		this.writeMETS.createFileGroup(this.writeMETS.getFileGrp_DEFAULT());
		this.writeMETS.createFileGroup(this.writeMETS.getFileGrp_MIN());
		this.writeMETS.createFileGroup(this.writeMETS.getFileGrp_MAX());
		
		try {
			TocDocument escidocTocDoc = TocDocument.Factory.parse(escidocToc);
			
			//Dummy root div
			Div div = escidocTocDoc.getToc().getToc().getDiv();
			Div[] children = div.getDivArray();
			Div physical = null;
			
			for (int i =0; i< children.length; i++)
			{
				if (children[i].getTYPE().equals(this.writeMETS.getType_PHYSICAL()))
				{
					physical = children[i];
				}
			}
			
			Div[] phys_childs = physical.getDivArray();
			
			for (int x=0; x< phys_childs.length; x++)
			{
				Div page = phys_childs[x];
				Ptr[] pagePointers = page.getPtrArray();
				String[] ptrIds = new String[3];
				for (int y =0; y< pagePointers.length; y++)
				{
					Ptr pointer = pagePointers[y];
					if (pointer.getUSE() != null)
					{
						if (pointer.getUSE().equals(this.writeMETS.getFileGrp_MIN()))
						{
							this.writeMETS.addFiletoFileGroup(this.writeMETS.getFileGrp_MIN(), this.baseURL+pointer.getHref(), pointer.getID(), pointer.getMIMETYPE());
							ptrIds[0] = pointer.getID();
						}
						if (pointer.getUSE().equals(this.writeMETS.getFileGrp_DEFAULT()))
						{
							this.writeMETS.addFiletoFileGroup(this.writeMETS.getFileGrp_DEFAULT(), this.baseURL+pointer.getHref(), pointer.getID(), pointer.getMIMETYPE());
							ptrIds[1] = pointer.getID();
						}
						if (pointer.getUSE().equals(this.writeMETS.getFileGrp_MAX()))
						{
							this.writeMETS.addFiletoFileGroup(this.writeMETS.getFileGrp_MAX(), this.baseURL+pointer.getHref(), pointer.getID(), pointer.getMIMETYPE());
							ptrIds[2] = pointer.getID();
						}
					}
				}
				
				int order = Integer.parseInt(page.getORDER().toString());
				this.writeMETS.addToStructMap(this.writeMETS.getType_PHYSICAL(), ptrIds, order+"",page.getORDERLABEL(), page.getID(), page.getTYPE(), false);
				divId++;
			}
		} 
		catch (XmlException e) 
		{this.logger.error("Creation of physical parts for METS document failed ", e);}
	}
	
    /**
     * Gets the escidoc:toc values for mets logical structMap.
     * @param escidocToc
     */
	public void createLogicals (String escidocToc)
	{
		int divId =1;
		
		Div currentDiv;
		Div[] currentChilds;
		Div childX;
		Div[] childChildren;
		
		TocDocument escidocTocDoc;
		try {
			escidocTocDoc = TocDocument.Factory.parse(escidocToc);
			
			//Dummy root div
			Div div = escidocTocDoc.getToc().getToc().getDiv();
			Div[] children = div.getDivArray();
			Div logical = null;
			
			for (int i =0; i< children.length; i++)
			{
				if (children[i].getTYPE().equals(this.writeMETS.getType_LOGICAL()))
				{
					logical = children[i];
				}
			}
			
			//Create the root element for the logical structMap
			Div log_root = logical.getDivArray()[0];
			this.writeMETS.createStructMap(this.writeMETS.getType_LOGICAL(), log_root.getTYPE());
			this.writeMETS.createStructLink();
			
			currentDiv = log_root;
			currentChilds = currentDiv.getDivArray();
			
			for(int i=0; i< currentChilds.length; i++){
				childX = currentChilds[i];
				//Add all divs to the structMap
				this.writeMETS.addToStructMap(this.writeMETS.getType_LOGICAL(), new String[]{childX.getID()}, childX.getORDER()+"", childX.getORDERLABEL(), "log"+divId, childX.getTYPE(), false);
				if (childX.getPtrArray().length >0)
				{
					//create a structLink
					for (int y=0; y<childX.getPtrArray().length;y++){
						System.out.println(childX.getPtrArray()[y].getHref());
						this.writeMETS.addStructLink("log"+divId, childX.getPtrArray()[y].getHref());
					}
				}

				divId ++;
				//Add all childs to the structMap
				while(childX.getDivArray().length > 0)
				{
					childChildren = childX.getDivArray();
					for (int x=0; x < childChildren.length; x++ ){
						this.writeMETS.addToStructMap(this.writeMETS.getType_LOGICAL(), new String[]{childChildren[x].getID()}, childChildren[x].getORDER()+"", childChildren[x].getORDERLABEL(), divId+"", childChildren[x].getTYPE(), true);
						if (childX.getPtrArray().length >0)
						{
							//create a structLink
							for (int y=0; y<childX.getPtrArray().length;y++){
								System.out.println(childX.getPtrArray()[y].getHref());
								this.writeMETS.addStructLink("log"+divId, childX.getPtrArray()[y].getHref());
							}
						}
						childX =childChildren[x];
						divId ++;
					}
				}
			}
		} 
		catch (XmlException e) 
		{this.logger.error("Creation of logical parts for METS document failed ", e);}

	}	
}
