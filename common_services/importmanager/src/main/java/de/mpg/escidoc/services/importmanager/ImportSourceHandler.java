package de.mpg.escidoc.services.importmanager;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.purl.dc.elements.x11.SimpleLiteral;

import de.mpg.escidoc.metadataprofile.schema.x01.importSource.FTFetchSettingType;
import de.mpg.escidoc.metadataprofile.schema.x01.importSource.FTFetchSettingsType;
import de.mpg.escidoc.metadataprofile.schema.x01.importSource.ImportSourceType;
import de.mpg.escidoc.metadataprofile.schema.x01.importSource.ImportSourcesDocument;
import de.mpg.escidoc.metadataprofile.schema.x01.importSource.ImportSourcesType;

public class ImportSourceHandler {
	
    ImportSourcesDocument sourceDoc = null;
    ImportSourcesType sourceType = null;
    
    private final static Logger logger = Logger.getLogger(ImportHandlerBean.class);

	public ImportSourceHandler ()
	{
	
	}
	
	/**
	 * This methods reads in the xml description of all available import sources
	 * @return vector of ImportSource objects
	 */
	public Vector<ImportSourceVO> getSources () throws Exception
	{
		Vector<ImportSourceVO> sourceVec = new Vector<ImportSourceVO>();
		try{
			ClassLoader cl = this.getClass().getClassLoader();
			java.io.InputStream in = cl.getResourceAsStream("resources/import.xml"); 	
			this.sourceDoc = ImportSourcesDocument.Factory.parse(in);
		}
		catch(XmlException e){e.printStackTrace();}
		catch(IOException e){e.printStackTrace();}
	
        this.sourceType = this.sourceDoc.getImportSources();
        ImportSourceType[] sources = this.sourceType.getImportSourceArray();
        for (ImportSourceType source : sources)
        {
        	Vector<FullTextVO> fulltextVec = new Vector<FullTextVO>();
        	String status = simpleLiteralTostring(source.getStatus());
        	if (status.toLowerCase().trim().equals("published"))
        	{
        		//Source parameters
	        	ImportSourceVO sourceVO = new ImportSourceVO();      	
	        	sourceVO.setName(source.getName());
	        	sourceVO.setDescription(simpleLiteralTostring(source.getDescription()));
	        	sourceVO.setUrl(new URL (simpleLiteralTostring(source.getIdentifier())));
	        	sourceVO.setType(simpleLiteralTostring(source.getFormatArray(0)));
	        	sourceVO.setEncoding(simpleLiteralTostring(source.getFormatArray(1)));
	        	sourceVO.setHarvestProtocol(simpleLiteralTostring(source.getHarvestProtocol()));
	        	//TODO:sourceVO.setRetryAfter(new Date(source.getRetryAfter().toString()));
	        	sourceVO.setTimeout(Integer.parseInt(source.getTimeout().toString()));
	        	sourceVO.setNumberOfTries(Integer.parseInt(source.getNumberOfTries().toString()));
	        	sourceVO.setStatus(simpleLiteralTostring(source.getStatus()));
	        	
	        	//Metadata parameters
                sourceVO.setMdDesc(simpleLiteralTostring(source.getMDFetchSetting().getDescription()));
                sourceVO.setMdUrl(new URL (simpleLiteralTostring(source.getMDFetchSetting().getIdentifier())));
                sourceVO.setMdFormat(simpleLiteralTostring(source.getMDFetchSetting().getFormat()));
                sourceVO.setMdLabel(simpleLiteralTostring(source.getMDFetchSetting().getLabel()));    
           
	        	//Fulltext parameters
	            FTFetchSettingsType ftfs = source.getFTFetchSettings();
	            FTFetchSettingType[] ftfArray = ftfs.getFTFetchSettingArray();

	            for (FTFetchSettingType ftf : ftfArray)
	            {
	            	FullTextVO fulltextVO = new FullTextVO();
	            	fulltextVO.setFtDesc(simpleLiteralTostring(ftf.getDescription()));
	            	try {fulltextVO.setFtUrl(new URL (simpleLiteralTostring(ftf.getIdentifier())));} 
	            	catch (MalformedURLException e) {e.printStackTrace();}
	            	fulltextVO.setFtFormat(simpleLiteralTostring(ftf.getFormat()));
	            	fulltextVO.setFtDefault(ftf.getDefault());
	            	fulltextVO.setFtLabel(simpleLiteralTostring(ftf.getLabel()));
	            	fulltextVO.setFileType(simpleLiteralTostring(ftf.getFileType()));
	            	fulltextVec.add(fulltextVO);
	            }
	                 
	            sourceVO.setFtFormats(fulltextVec);
	            //this.printSourceXML(sourceVO);
	            sourceVec.add(sourceVO);
        	}
        	
        }      
        
		return sourceVec;
	}
	
	/**
	 * This methods reads in the xml description of a specific source
	 * @return ImportSource object
	 */
	public ImportSourceVO getSourceByName (String name)
	{ 
		ImportSourceVO sourceVO = new ImportSourceVO();
		Vector<FullTextVO> fulltextVec = new Vector<FullTextVO>();
		boolean found = false;
		
		try{
			ClassLoader cl = this.getClass().getClassLoader();
			java.io.InputStream in = cl.getResourceAsStream("resources/import.xml"); 	
			this.sourceDoc = ImportSourcesDocument.Factory.parse(in);
		}
		catch(XmlException e){e.printStackTrace();}
		catch(IOException e){e.printStackTrace();}
	
        this.sourceType = this.sourceDoc.getImportSources();
        ImportSourceType[] sources = this.sourceType.getImportSourceArray();
        for (ImportSourceType source : sources)
        {      	
        	if (!source.getName().trim().toLowerCase().equals(name.trim().toLowerCase())){continue;}
        	else{found = true;}
        	
        	sourceVO.setName(source.getName());      	
        	sourceVO.setDescription(simpleLiteralTostring(source.getDescription()));
        	try {sourceVO.setUrl(new URL (simpleLiteralTostring(source.getIdentifier())));} 
        	catch (MalformedURLException e1) {e1.printStackTrace();}
        	sourceVO.setType(simpleLiteralTostring(source.getFormatArray(0)));
        	sourceVO.setEncoding(simpleLiteralTostring(source.getFormatArray(1)));
        	sourceVO.setHarvestProtocol(simpleLiteralTostring(source.getHarvestProtocol()));
        	//TODO:sourceVO.setRetryAfter(new Date(source.getRetryAfter().toString()));
        	sourceVO.setTimeout(Integer.parseInt(source.getTimeout().toString()));
        	sourceVO.setNumberOfTries(Integer.parseInt(source.getNumberOfTries().toString()));
        	sourceVO.setStatus(simpleLiteralTostring(source.getStatus()));
        	
        	//Metadata parameters
            sourceVO.setMdDesc(simpleLiteralTostring(source.getMDFetchSetting().getDescription()));
            try {sourceVO.setMdUrl(new URL (simpleLiteralTostring(source.getMDFetchSetting().getIdentifier())));} 
            catch (MalformedURLException e1) {e1.printStackTrace();}
            sourceVO.setMdFormat(simpleLiteralTostring(source.getMDFetchSetting().getFormat()));
            sourceVO.setMdLabel(simpleLiteralTostring(source.getMDFetchSetting().getLabel()));    
       
        	//Fulltext parameters
            FTFetchSettingsType ftfs = source.getFTFetchSettings();
            FTFetchSettingType[] ftfArray = ftfs.getFTFetchSettingArray();

            for (FTFetchSettingType ftf : ftfArray)
            {
            	FullTextVO fulltextVO = new FullTextVO();
            	fulltextVO.setFtDesc(simpleLiteralTostring(ftf.getDescription()));
            	try {fulltextVO.setFtUrl(new URL (simpleLiteralTostring(ftf.getIdentifier())));} 
            	catch (MalformedURLException e) {e.printStackTrace();}
            	fulltextVO.setFtFormat(simpleLiteralTostring(ftf.getFormat()));
            	fulltextVO.setFtDefault(ftf.getDefault());
            	fulltextVO.setFtLabel(simpleLiteralTostring(ftf.getLabel()));
            	fulltextVO.setFileType(simpleLiteralTostring(ftf.getFileType()));
            	fulltextVec.add(fulltextVO);
            }
                 
            sourceVO.setFtFormats(fulltextVec);    
        }
        
        //this.printSourceXML(sourceVO);
        if (found){return sourceVO;}
        else {return null;}		
	}
	
	private String simpleLiteralTostring (SimpleLiteral sl){
		return sl.toString().substring(sl.toString().indexOf(">") + 1, sl.toString().lastIndexOf("<"));
	}
	
	/**
	 * Print out source values for debug purpose
	 * @param source
	 */
	public void printSourceXML(ImportSourceVO source){
		
		logger.info("____________________________________________________________________");
		logger.info("Source Name      	: " + source.getName());
		logger.info("Description      	: " + source.getDescription());
		logger.info("Main URL         	: " + java.net.URLDecoder.decode(source.getUrl().toString()));
		logger.info("Doc type     	  	: " + source.getType());
		logger.info("Doc encoding     	: " + source.getEncoding());
		logger.info("Harvest protocol 	: " + source.getHarvestProtocol());
		logger.info("Timeout		 	: " + source.getTimeout());
		logger.info("Retry after	 	: " + source.getRetryAfter());
		logger.info("Number of tries	: " + source.getNumberOfTries());
		logger.info("Status				: " + source.getStatus());
		logger.info("____________________________________________________________________");
		logger.info("MD description		: " + source.getMdDesc());
		logger.info("MD format			: " + source.getMdFormat());
		logger.info("MD label			: " + source.getMdLabel());
		logger.info("MD URL				: " + java.net.URLDecoder.decode(source.getMdUrl().toString()));
		for (int i =0; i< source.getFtFormats().size(); i++)
		{
			FullTextVO ft = source.getFtFormats().get(i);
			logger.info("____________________________________________________________________");
			logger.info("FT description		: " + ft.getFtDesc());
			logger.info("FT format			: " + ft.getFtFormat());
			logger.info("FT label			: " + ft.getFtLabel());
			logger.info("FT URL				: " + java.net.URLDecoder.decode(ft.getFtUrl().toString()));
			logger.info("FT default			: " + ft.isFtDefault());
			logger.info("____________________________________________________________________");
			
		}
		logger.info("____________________________________________________________________");		
	}
	
}