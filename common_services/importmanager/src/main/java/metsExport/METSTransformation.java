package metsExport;


import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import de.escidoc.schemas.tableofcontent.x01.DivDocument.Div;
import de.escidoc.schemas.tableofcontent.x01.PtrDocument.Ptr;
import de.escidoc.schemas.toc.x06.TocDocument;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * This class provides METS transformation for a escidoc objects.
 * @author kleinfe1
 *
 */
public class METSTransformation extends XmlIO{

	private WriteMETSData writeMETS = new WriteMETSData();
	private String baseURL = "http://dev-coreservice.mpdl.mpg.de:8080";
	
	private Logger logger = Logger.getLogger(getClass());
    
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
        	Login login = new Login();
	        String escidocToc = ServiceLocator.getTocHandler(login.loginSysAdmin()).retrieve(eScidocId);
	        //Create mets id out of escidoc id
	        String metsId = eScidocId.replace("escidoc", "mets");
	        
	        //Create different METS sections
	        this.createDmdSec(escidocToc);
	        this.createAmdSec(escidocToc);
	        this.createPhysicals(escidocToc);
	        this.createLogicals(escidocToc);
	        
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
    
	/**
	 * retrieves the baseURL for accessing the item.
	 * @param escidocToc
	 */
	private void getBaseURL(String escidocToc)
	{
		//TODO
		Document tocDoc;
		try {
			tocDoc = getDocument(escidocToc, false);
//			Node attr = tocDoc.getElementsByTagName("escidocToc:toc").item(0).getAttributes().getNamedItem("xml:base");
//			System.out.println(attr);
//			this.baseURL = attr.getLocalName();
			
			NamedNodeMap a = tocDoc.getElementsByTagName("escidocToc:toc").item(0).getAttributes();
			Node xml = a.getNamedItem("xml:base");
			this.baseURL = xml.getTextContent();
			System.out.println("Base: " + this.baseURL);
			
			TocDocument escidocTocDoc = TocDocument.Factory.parse(escidocToc);
		} 
		catch (Exception e) {e.printStackTrace();}
		
	}
    
    /**
     * Gets the escidoc:toc values for mets dmd section.
     * @param escidocToc
     */
	private void createDmdSec(String escidocToc)
	{
		String title ="Keine Angabe";
		String author ="Keine Angabe";
		String place ="Keine Angabe";
		String year ="Keine Angabe";
		String dmdId = "dmd1";
	
		try 
		{
			Document tocDoc = getDocument(escidocToc, false);
			title = tocDoc.getElementsByTagName("dc:title").item(0).getTextContent();
//			author = tocDoc.getElementsByTagName("escidoc:complete-name").item(0).getTextContent();
//			tocDoc.getElementsByTagName("dc:place").item(0).getTextContent();
//			tocDoc.getElementsByTagName("dc:date").item(0).getTextContent();

			this.writeMETS.createDmdSec(dmdId, title, author, place, year);
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
		owner = "Max Planck Digital Library";
		logo = "http://www.mpdl.mpg.de/mpdl/sys/css/screen/images/logo.gif"; 
		url = "http://mpdl.mpg.de";
		reference ="TODO";
			
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
				
				//Tmp +1 till we fixed that order starts at 0
				this.writeMETS.addToStructMap(this.writeMETS.getType_PHYSICAL(), ptrIds, page.getORDER().toString()+1,page.getORDERLABEL(), divId+"", page.getTYPE(), false);
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
		String[] id = new String [1];
		
		Div child;
		Div[] childs;
		Div[] childsChildren;
		
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
			
			//Create structured METS divs for the child elements
			childs = log_root.getDivArray();
			for (int x= 0; x< childs.length; x++ )
			{
				child = childs[x];
				this.writeMETS.addToStructMap(this.writeMETS.getType_LOGICAL(), new String[]{child.getID()}, null, null, divId+"", child.getTYPE(), false);
				divId ++;
				
				childsChildren = child.getDivArray();
			}
		} 
		catch (XmlException e) 
		{this.logger.error("Creation of logical parts for METS document failed ", e);}

	}	
}
