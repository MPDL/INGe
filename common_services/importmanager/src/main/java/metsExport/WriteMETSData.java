package metsExport;

import gov.loc.mets.AmdSecType;
import gov.loc.mets.DivType;
import gov.loc.mets.FileType;
import gov.loc.mets.MdSecType;
import gov.loc.mets.MetsDocument;
import gov.loc.mets.StructMapType;
import gov.loc.mets.DivType.Fptr;
import gov.loc.mets.FileType.FLocat;
import gov.loc.mets.MdSecType.MdWrap;
import gov.loc.mets.MdSecType.MdWrap.XmlData;
import gov.loc.mets.MetsDocument.Mets;
import gov.loc.mets.MetsType.FileSec;
import gov.loc.mets.MetsType.FileSec.FileGrp;
import gov.loc.mods.v3.DateType;
import gov.loc.mods.v3.ModsDocument;
import gov.loc.mods.v3.ModsType;
import gov.loc.mods.v3.NameType;
import gov.loc.mods.v3.OriginInfoType;
import gov.loc.mods.v3.PlaceTermType;
import gov.loc.mods.v3.PlaceType;
import gov.loc.mods.v3.TitleInfoType;
import gov.loc.mods.v3.VersionType;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlString;

/**
 * This class provides function for writing a METS document.
 * @author kleinfe1
 *
 */
public class WriteMETSData {
	

	//Metadata Documents
	private MetsDocument metsDoc = null;
	private ModsDocument modsDoc = null;

	//Mets sections
	private Mets mets = null;
	private ModsType mods = null;
	private FileSec fileSec = null;
	private MdSecType dmdSec = null;
	private AmdSecType amdSec = null;
	private StructMapType structMap = null;
	
	//Mets helpers
	private DivType physRoot = null;
	private DivType logRoot = null;
	private DivType currentChild = null;
	private XmlOptions xOpts = null;

	//Constants
	private String fileGrp_DEFAULT = "DEFAULT";
	private String fileGrp_MIN = "MIN";
	private String fileGrp_MAX = "MAX";
	
	private String type_PHYSICAL ="physical";
	private String type_LOGICAL  ="logical";

	
	
	public WriteMETSData()
	{
		this.metsDoc = MetsDocument.Factory.newInstance();
	    this.modsDoc = ModsDocument.Factory.newInstance();
	    this.mets = this.metsDoc.addNewMets();	        
	    this.mods = this.modsDoc.addNewMods();
	}

	/**
	 * Creates the descriptive metadata section of a mets document.
	 * @param id the id of this dmd element
	 * @param title metadata title
	 * @param author metadata author
	 * @param place metadata place
	 * @param year metadata year
	 * @throws IOException
	 * @throws XmlException
	 */
	public void createDmdSec(String id, String title, String author, String place, String year)
	            throws IOException, XmlException
	{
		this.dmdSec = this.mets.addNewDmdSec();
		this.dmdSec.setID(id);
		        
		//MODS Data
		MdWrap wrap = this.dmdSec.addNewMdWrap();
		wrap.setMIMETYPE("text/xml");
		wrap.setMDTYPE(MdWrap.MDTYPE.MODS);
		XmlData xml = wrap.addNewXmlData();
		this.mods.setVersion(VersionType.X_3_2);
		TitleInfoType tInfo = this.mods.addNewTitleInfo();
		XmlObject xtitle = tInfo.addNewTitle();
		XmlString tString = XmlString.Factory.newValue(title);
		xtitle.set(tString);
		tInfo.setTitleArray(0, title);
		this.mods.setTitleInfoArray(0, tInfo);
		NameType name = this.mods.addNewName();
		XmlObject xName = name.addNewDisplayForm();
		XmlString nString = XmlString.Factory.newValue(author);
		xName.set(nString);
		name.setDisplayFormArray(0, xName);
		this.mods.setNameArray(0, name);
		OriginInfoType oInfo = this.mods.addNewOriginInfo();
		PlaceType placeType = oInfo.addNewPlace();
		PlaceTermType ptt = placeType.addNewPlaceTerm();
		ptt.setStringValue(place);
		placeType.setPlaceTermArray(0, ptt);
		oInfo.setPlaceArray(0, placeType);
		DateType iDate = oInfo.addNewDateIssued();
		iDate.setStringValue(year);
		oInfo.setDateIssuedArray(0, iDate);
		this.mods.setOriginInfoArray(0, oInfo);
		this.modsDoc.setMods(this.mods);
		xml.set(this.modsDoc);
		wrap.setXmlData(xml);
		this.dmdSec.setMdWrap(wrap);
	}

	/**
	 * Creates the administrative metadata section of a mets document.
	 * @param id the id of this amdSec
	 * @param owner the rights owner of the item
	 * @param logo the logo of the organization
	 * @param url the url of the organization
	 * @param reference the items url
	 */
	public void createAmdSec(String id, String owner, String logo, String url, String reference)
	{
		this.amdSec = this.mets.addNewAmdSec();
	    this.amdSec.setID(id);
	        
	    //DVRights
	    MdSecType rightsMD = this.amdSec.addNewRightsMD();
	    rightsMD.setID("rights" + id);
	    
	    MdWrap wrap = rightsMD.addNewMdWrap();
	    wrap.setMIMETYPE("text/xml");
	    wrap.setMDTYPE(MdWrap.MDTYPE.OTHER);
	    wrap.setOTHERMDTYPE("DVRIGHTS");

	    XmlData xml = wrap.addNewXmlData();
	    XmlObject dvrights = XmlObject.Factory.newInstance();
	    XmlCursor cur = dvrights.newCursor();
	    cur.toNextToken();
	    cur.beginElement("rights", "http://dfg-viewer.de/");
	    cur.insertElementWithText("owner", "http://dfg-viewer.de/", owner);
	    cur.insertElementWithText("ownerLogo", "http://dfg-viewer.de/", logo);
	    cur.insertElementWithText("ownerSiteURL", "http://dfg-viewer.de/", url);
	    cur.dispose();
	    xml.set(dvrights);
	    wrap.setXmlData(xml);
	    rightsMD.setMdWrap(wrap);
	    this.amdSec.setRightsMDArray(0, rightsMD);
	        
	    //DVLinks
	    MdSecType digiprovMD = this.amdSec.addNewDigiprovMD();
	    digiprovMD.setID("digiprov" + id);
	    MdWrap dp_wrap = digiprovMD.addNewMdWrap();
	    dp_wrap.setMIMETYPE("text/xml");
	    dp_wrap.setMDTYPE(MdWrap.MDTYPE.OTHER);
	    dp_wrap.setOTHERMDTYPE("DVLINKS");
	    XmlData dp_xml = dp_wrap.addNewXmlData();
	    XmlObject dvdigiprov = XmlObject.Factory.newInstance();
	    XmlCursor dp_cur = dvdigiprov.newCursor();
	    dp_cur.toNextToken();
	    dp_cur.beginElement("links", "http://dfg-viewer.de/");
	    dp_cur.insertElementWithText("reference", "http://dfg-viewer.de/", reference);
	    dp_cur.insertElementWithText("presentation", "http://dfg-viewer.de/", "http://192.129.1.123:8080/virr_presentation/");
	    dp_cur.dispose();
	    dp_xml.set(dvdigiprov);
	    dp_wrap.setXmlData(dp_xml);
	    digiprovMD.setMdWrap(dp_wrap);
	    this.amdSec.setDigiprovMDArray(0, digiprovMD);
	}

	/**
	 * Creates the fileSec element of a mets document.
	 */
	public void createFileSec()
	{
	    this.fileSec = this.mets.addNewFileSec();
	}

	/**
	 * Creates a file Group element of a mets document.
	 * @param groupName the name of the file group
	 */
	public void createFileGroup (String groupName)
	{
		FileGrp fileGrp = this.fileSec.addNewFileGrp();
	    fileGrp.setUSE(groupName);
	}
	    
	/**
	 * Adds file metadata to a file group in a mets document.
	 * @param groupName the group where the metadata is to add
	 * @param url the file url
	 * @param title the file title
	 * @param mimetype the file mimetype
	 */
	public void addFiletoFileGroup(String groupName, String url, String title, String mimetype)
	{
		FileGrp currentGroup = null;
	    FileGrp[] fileGroups = this.fileSec.getFileGrpArray();
	    for (int i =0; i< fileGroups.length; i++)
	    {
	    	if(fileGroups[i].getUSE().equals(groupName))
	    	{
	    		currentGroup = fileGroups[i];
	    		break;
	    	}
	    }
	    	
	    FileType currentFile = currentGroup.addNewFile();
	    currentFile.setMIMETYPE(mimetype);
	    currentFile.setID(title);

	    FLocat floc = currentFile.addNewFLocat();
	    floc.setLOCTYPE(FLocat.LOCTYPE.URL);
	    floc.setHref(url);
	}

	/**
	 * Creates a structMap element of a mets document.
	 * @param type the type of the structMap (physical or logical)
	 * @param logElem the type of the element if it's logical, otherwise null
	 */
	public void createStructMap(String type, String logElem)
	{
		this.structMap = this.mets.addNewStructMap();
	    this.structMap.setTYPE(type);
	    if (type.equals(this.type_PHYSICAL))
	    {
	    	this.physRoot = this.structMap.addNewDiv();
	        this.physRoot.setID("physstruct1");
	        List<String> amd = new ArrayList<String>();
	        amd.add(this.amdSec.getID());
	        List<String> dmd = new ArrayList<String>();
	        dmd.add(this.dmdSec.getID());
	        this.physRoot.setADMID(amd);
	        this.physRoot.setDMDID(dmd);
	        //DFG Requirement
	        this.physRoot.setTYPE("physSequence");
	    }
	    else 
	    {
	    	this.logRoot = this.structMap.addNewDiv();
	        this.logRoot.setID("logstruct1");
	        List<String> amd = new ArrayList<String>();
	        amd.add(this.amdSec.getID());
	        List<String> dmd = new ArrayList<String>();
	        dmd.add(this.dmdSec.getID());
	        this.logRoot.setADMID(amd);
	        this.logRoot.setDMDID(dmd);
	        this.logRoot.setTYPE(logElem);
	        	
	        this.currentChild=this.logRoot;
	    }
	}
	    
	/**
	 * Adds elemets to the structMap of a mets document.
	 * @param mapType the type of the structMap (physical or logical)
	 * @param fileId
	 * @param order
	 * @param orderlabel
	 * @param divId
	 * @param structElem
	 * @param ischild 
	 */
	public void addToStructMap (String mapType, String[] fileId, String order, String orderlabel, String divId, String structElem, boolean ischild)
	{
		if (mapType.equals(this.type_PHYSICAL))
	    {
			DivType div = this.physRoot.addNewDiv();
		    div.setID("phys" + divId);
		    BigInteger o = new BigInteger(order);
		    div.setORDER(o);
		    if (orderlabel != null)
		    {
		    	div.setORDERLABEL(orderlabel);
		    }
		    div.setTYPE(structElem);
		    for (int i=0; i< fileId.length; i++)
		    {
		    	Fptr fptr = div.addNewFptr();
		        fptr.setFILEID(fileId[i]);
		    }
		}
		else 
	    {
			if (!ischild)
	    	{
				this.currentChild=this.logRoot;
	    	}
	    		
	    	DivType div = this.currentChild.addNewDiv();
		    div.setID("log" + divId);
		    BigInteger o = new BigInteger(order);
		    div.setORDER(o);
		    div.setTYPE(structElem);
	    }
	}
	   
	/**
	 * Creates a mets document out of the single sections.
	 * @param metsId the id of the mets document to create
	 * @return MetsDocument
	 * @throws IOException
	 */
	public MetsDocument getMetsDoc(String metsId) throws IOException
	{
		this.mets.setID(metsId);

	    this.mets.setDmdSecArray(0, this.dmdSec);
	    if (this.amdSec != null)
	    {
	    	this.mets.setAmdSecArray(0, this.amdSec);
	    }
	    if (this.fileSec != null)
	    {
	    	this.mets.setFileSec(this.fileSec);
	    }
	        
	    this.metsDoc.setMets(this.mets);
	    return this.metsDoc;
	}
	    
	public String getFileGrp_DEFAULT() 
	{
		return this.fileGrp_DEFAULT;
	}

	public void setFileGrp_DEFAULT(String fileGrp_DEFAULT) 
	{
		this.fileGrp_DEFAULT = fileGrp_DEFAULT;
	}

	public String getFileGrp_MIN() 
	{
		return this.fileGrp_MIN;
	}

	public void setFileGrp_MIN(String fileGrp_MIN) 
	{
		this.fileGrp_MIN = fileGrp_MIN;
	}

	public String getFileGrp_MAX() 
	{
		return this.fileGrp_MAX;
	}

	public void setFileGrp_MAX(String fileGrp_MAX) 
	{
		this.fileGrp_MAX = fileGrp_MAX;
	}

	public String getType_PHYSICAL() 
	{
		return this.type_PHYSICAL;
	}

	public void setType_PHYSICAL(String type_PHYSICAL) 
	{
		this.type_PHYSICAL = type_PHYSICAL;
	}

	public String getType_LOGICAL() 
	{
		return this.type_LOGICAL;
	}

	public void setType_LOGICAL(String type_LOGICAL) 
	{			
		this.type_LOGICAL = type_LOGICAL;
	}
}
