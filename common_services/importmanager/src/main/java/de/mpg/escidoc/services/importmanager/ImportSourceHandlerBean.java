package de.mpg.escidoc.services.importmanager;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import javax.naming.InitialContext;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.purl.dc.elements.x11.SimpleLiteral;

import de.mpg.escidoc.metadataprofile.schema.x01.importSource.FTFetchSettingType;
import de.mpg.escidoc.metadataprofile.schema.x01.importSource.FTFetchSettingsType;
import de.mpg.escidoc.metadataprofile.schema.x01.importSource.ImportSourceType;
import de.mpg.escidoc.metadataprofile.schema.x01.importSource.ImportSourcesDocument;
import de.mpg.escidoc.metadataprofile.schema.x01.importSource.ImportSourcesType;
import de.mpg.escidoc.metadataprofile.schema.x01.importSource.MDFetchSettingType;
import de.mpg.escidoc.metadataprofile.schema.x01.importSource.MDFetchSettingsType;
import de.mpg.escidoc.metadataprofile.schema.x01.transformations.MetadataformatType;
import de.mpg.escidoc.metadataprofile.schema.x01.transformations.MetadataformatsType;
import de.mpg.escidoc.metadataprofile.schema.x01.transformations.TransformationType;
import de.mpg.escidoc.metadataprofile.schema.x01.transformations.TransformationsDocument;
import de.mpg.escidoc.metadataprofile.schema.x01.transformations.TransformationsType;
import de.mpg.escidoc.services.common.MetadataHandler;
import de.mpg.escidoc.services.importmanager.valueobjects.FullTextVO;
import de.mpg.escidoc.services.importmanager.valueobjects.ImportSourceVO;
import de.mpg.escidoc.services.importmanager.valueobjects.MetadataVO;

/**
 * This class handles the import function from external sources.
 * @author kleinfe1
 *
 */
public class ImportSourceHandlerBean 
{
    private ImportSourcesDocument sourceDoc = null;
    private ImportSourcesType sourceType = null;
    
    private TransformationsDocument transformDoc = null;
    private TransformationsType transformType = null;
    
    // Metadata Service
    private MetadataHandler mdHandler = null;
    private InitialContext initialContext = null;
    
    private static final Logger LOGGER = Logger.getLogger(ImportHandlerBean.class);

    private String transformationFormat = null;
    
	public ImportSourceHandlerBean()
	{
	
	}
	
	public Vector<ImportSourceVO> getSources() throws Exception
	{
		Vector<ImportSourceVO> sourceVec = new Vector<ImportSourceVO>();
		try
		{
			ClassLoader cl = this.getClass().getClassLoader();
			java.io.InputStream in = cl.getResourceAsStream("resources/sources.xml"); 	
			this.sourceDoc = ImportSourcesDocument.Factory.parse(in);
		}
		catch(XmlException e)
		{e.printStackTrace();}
		catch(IOException e)
		{e.printStackTrace();}
		
		this.initialContext = new InitialContext();
		this.mdHandler = (MetadataHandler) this.initialContext.lookup(MetadataHandler.SERVICE_NAME);
	
        this.sourceType = this.sourceDoc.getImportSources();
        ImportSourceType[] sources = this.sourceType.getImportSourceArray();
        for (ImportSourceType source : sources)
        {
        	
        	Vector<FullTextVO> fulltextVec = new Vector<FullTextVO>();
        	Vector<MetadataVO> mdVec = new Vector<MetadataVO>();
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
	        	sourceVO.setIdentifier(simpleLiteralTostring(source.getSourceIdentifier()));
	        	
           
	        	//Metadata parameters
	            MDFetchSettingsType mdfs = source.getMDFetchSettings();
	            MDFetchSettingType[] mdfArray = mdfs.getMDFetchSettingArray();

	            for (MDFetchSettingType mdf : mdfArray)
	            {
	            	MetadataVO mdVO  = new MetadataVO();
	            	if (mdf.getDescription()!= null)
	            	{
	            		mdVO.setMdDesc(simpleLiteralTostring(mdf.getDescription()));
	            	}
	            	try 
	            	{
	            		mdVO.setMdUrl(new URL (simpleLiteralTostring(mdf.getIdentifier())));
	            	} 
	            	catch (MalformedURLException e) 
	            	{e.printStackTrace();}
	            	mdVO.setMdFormat(simpleLiteralTostring(mdf.getFormat()));
	            	mdVO.setMdDefault(mdf.getDefault());
	            	mdVO.setMdLabel(simpleLiteralTostring(mdf.getLabel()));
	            	mdVO.setFileType(simpleLiteralTostring(mdf.getFileType()));
	            	mdVec.add(mdVO);
	            }	                 
	            sourceVO.setMdFormats(mdVec);
                
	        	//Fulltext parameters
	            FTFetchSettingsType ftfs = source.getFTFetchSettings();
	            FTFetchSettingType[] ftfArray = ftfs.getFTFetchSettingArray();

	            for (FTFetchSettingType ftf : ftfArray)
	            {
	            	FullTextVO fulltextVO = new FullTextVO();
	            	fulltextVO.setFtDesc(simpleLiteralTostring(ftf.getDescription()));
	            	try 
	            	{
	            		fulltextVO.setFtUrl(new URL (simpleLiteralTostring(ftf.getIdentifier())));
	            	} 
	            	catch (MalformedURLException e) 
	            	{e.printStackTrace();}
	            	fulltextVO.setFtFormat(simpleLiteralTostring(ftf.getFormat()));
	            	fulltextVO.setFtDefault(ftf.getDefault());
	            	fulltextVO.setFtLabel(simpleLiteralTostring(ftf.getLabel()));
	            	fulltextVO.setFileType(simpleLiteralTostring(ftf.getFileType()));
	            	fulltextVec.add(fulltextVO);
	            }	                 
	            sourceVO.setFtFormats(fulltextVec);

	            //Check if a transformation for the default MD format is possible
	            //TODO: check via transformation.xml
	            if (this.transformationFormat != null)
	            {
	            	for (int i=0; i< sourceVO.getMdFormats().size(); i++)
	            	{
	            		MetadataVO md = sourceVO.getMdFormats().get(i);
	            		if (md.isMdDefault())
	            		{
			            	if (this.mdHandler.checkTransformation(md.getMdLabel(), this.transformationFormat)) 
			            	{
			            		sourceVec.add(sourceVO);
			            	}
		            	}
	            	}
	            }
	            //If no transformationFormat is given => all sources are read in
	            else 
	            {
	            	sourceVec.add(sourceVO);
	            }
        	}        	
        }              
		return sourceVec;
	}
	

	public Vector<ImportSourceVO> getSources(String format) throws Exception
	{
		this.transformationFormat = format;
		return this.getSources();
	}
	

	public ImportSourceVO getSourceByName(String name)
	{ 
		ImportSourceVO sourceVO = new ImportSourceVO();
		Vector<FullTextVO> fulltextVec = new Vector<FullTextVO>();
		Vector<MetadataVO> mdVec = new Vector<MetadataVO>();
		boolean found = false;
		
		try
		{
			ClassLoader cl = this.getClass().getClassLoader();
			java.io.InputStream in = cl.getResourceAsStream("resources/sources.xml"); 	
			this.sourceDoc = ImportSourcesDocument.Factory.parse(in);
		}
		catch(XmlException e)
		{e.printStackTrace();}
		catch(IOException e)
		{e.printStackTrace();}
	
        this.sourceType = this.sourceDoc.getImportSources();
        ImportSourceType[] sources = this.sourceType.getImportSourceArray();
        for (ImportSourceType source : sources)
        {
        	if (!source.getName().trim().toLowerCase().equals(name.trim().toLowerCase()))
        	{
        		continue;
        	}
        	else
        	{
        		found = true;
        	}
        	
        	sourceVO.setName(source.getName());      	
        	sourceVO.setDescription(simpleLiteralTostring(source.getDescription()));
        	try 
        	{
        		sourceVO.setUrl(new URL (simpleLiteralTostring(source.getIdentifier())));
        	}
        	catch (MalformedURLException e1) 
        	{e1.printStackTrace();}
        	sourceVO.setType(simpleLiteralTostring(source.getFormatArray(0)));
        	sourceVO.setEncoding(simpleLiteralTostring(source.getFormatArray(1)));
        	sourceVO.setHarvestProtocol(simpleLiteralTostring(source.getHarvestProtocol()));
        	//TODO:sourceVO.setRetryAfter(new Date(source.getRetryAfter().toString()));
        	sourceVO.setTimeout(Integer.parseInt(source.getTimeout().toString()));
        	sourceVO.setNumberOfTries(Integer.parseInt(source.getNumberOfTries().toString()));
        	sourceVO.setStatus(simpleLiteralTostring(source.getStatus()));
        	sourceVO.setIdentifier(simpleLiteralTostring(source.getSourceIdentifier()));
        	
        	//Metadata parameters
            MDFetchSettingsType mdfs = source.getMDFetchSettings();
            MDFetchSettingType[] mdfArray = mdfs.getMDFetchSettingArray();

            for (MDFetchSettingType mdf : mdfArray)
            {
            	MetadataVO mdVO  = new MetadataVO();
            	if (mdf.getDescription() != null)
            	{
            		mdVO.setMdDesc(simpleLiteralTostring(mdf.getDescription()));
            	}
            	try 
            	{mdVO.setMdUrl(new URL (simpleLiteralTostring(mdf.getIdentifier())));} 
            	catch (MalformedURLException e) 
            	{e.printStackTrace();}
            	mdVO.setMdFormat(simpleLiteralTostring(mdf.getFormat()));
            	mdVO.setMdDefault(mdf.getDefault());
            	mdVO.setMdLabel(simpleLiteralTostring(mdf.getLabel()));
            	mdVO.setFileType(simpleLiteralTostring(mdf.getFileType()));
            	mdVec.add(mdVO);
            }                
            sourceVO.setMdFormats(mdVec); 
       
        	//Fulltext parameters
            FTFetchSettingsType ftfs = source.getFTFetchSettings();
            FTFetchSettingType[] ftfArray = ftfs.getFTFetchSettingArray();

            for (FTFetchSettingType ftf : ftfArray)
            {
            	FullTextVO fulltextVO = new FullTextVO();
            	fulltextVO.setFtDesc(simpleLiteralTostring(ftf.getDescription()));
            	try 
            	{
            		fulltextVO.setFtUrl(new URL (simpleLiteralTostring(ftf.getIdentifier())));
            		} 
            	catch (MalformedURLException e) 
            	{e.printStackTrace();}
            	fulltextVO.setFtFormat(simpleLiteralTostring(ftf.getFormat()));
            	fulltextVO.setFtDefault(ftf.getDefault());
            	fulltextVO.setFtLabel(simpleLiteralTostring(ftf.getLabel()));
            	fulltextVO.setFileType(simpleLiteralTostring(ftf.getFileType()));
            	fulltextVec.add(fulltextVO);
            }                
            sourceVO.setFtFormats(fulltextVec);    
        }
        
        //this.printSourceXML(sourceVO);
        if (found)
        {return sourceVO;}
        else 
        {return null;}		
	}
	
	public ImportSourceVO getSourceByIdentifier(String id)
	{ 		
		try
		{
			ClassLoader cl = this.getClass().getClassLoader();
			java.io.InputStream in = cl.getResourceAsStream("resources/sources.xml"); 	
			this.sourceDoc = ImportSourcesDocument.Factory.parse(in);
		}
		catch(XmlException e)
		{e.printStackTrace();}
		catch(IOException e)
		{e.printStackTrace();}
	
        this.sourceType = this.sourceDoc.getImportSources();
        ImportSourceType[] sources = this.sourceType.getImportSourceArray();
        for (ImportSourceType source : sources)
        {      	
        	if (!simpleLiteralTostring(source.getSourceIdentifier()).trim().toLowerCase().equals(id.trim().toLowerCase()))
        	{continue;}
        	else
        	{return this.getSourceByName(source.getName());}
        }
        return null;
	}
	
	public String getSourceNameByIdentifier(String id)
	{
		try
		{
			ClassLoader cl = this.getClass().getClassLoader();
			java.io.InputStream in = cl.getResourceAsStream("resources/sources.xml"); 	
			this.sourceDoc = ImportSourcesDocument.Factory.parse(in);
		}
		catch(XmlException e)
		{e.printStackTrace();}
		catch(IOException e)
		{e.printStackTrace();}
	
        this.sourceType = this.sourceDoc.getImportSources();
        ImportSourceType[] sources = this.sourceType.getImportSourceArray();
        for (ImportSourceType source : sources)
        {      	
        	if (!simpleLiteralTostring(source.getSourceIdentifier()).trim().toLowerCase().equals(id.trim().toLowerCase()))
        	{continue;}
        	else
        	{return source.getName();}
        }
        return null;
    }
	
    /**
     * This operation returns the metadata informations to fetch.
     * If no format from was specified the default metadata informations are fetched
     * @param source
     * @param format the format of which the metadata will be retrieved
     * @return metadata informations
     */
    public MetadataVO getMdObjectfromSource(ImportSourceVO source, String format)
    {
    	MetadataVO md= null;

    	for (int i=0; i< source.getMdFormats().size(); i++)
    	{
        	md = source.getMdFormats().get(i);
        	if (md.getMdLabel().trim().toLowerCase().equals(format.trim().toLowerCase()))
        	{
        		return md;
            }
    	}

    	return null;
    }
	
    /**
     * This is the only source specific method, which has to be updated when a new source is specified for import.
     * @param sourceName
     * @param identifier
     * @return the trimedIdentifier as String
     */
    public String trimIdentifier(String sourceName, String identifier)
    {
    	//Trim the identifier source arXiv
    	if (sourceName.trim().toLowerCase().equals("arxiv")||sourceName.trim().toLowerCase().equals("arxiv(oai_dc)"))
    	{
    		if (identifier.toLowerCase().startsWith("oai:arxiv.org:", 0))
            {
                identifier = identifier.substring(14);
            }
            if (identifier.toLowerCase().startsWith("arxiv:", 0))
            {
                identifier = identifier.substring(6);
            }
    	}
    	return identifier.trim();
    }
    
    public MetadataVO getDefaultMdFormatFromSource(ImportSourceVO source)
    {
    	Vector <MetadataVO> mdv = source.getMdFormats();   	
    	for (int i=0; i< mdv.size(); i++)
    	{
			MetadataVO mdVO = source.getMdFormats().get(i);
    		if (mdVO.isMdDefault())
    		{
    			return mdVO;
        	}
    	}
    	return null;
    }
    
    /**
     * This operation updates a metadata information set of the importSource.
     * @param source
     * @param md the metadata object which will be updated
     * @return ImportSourceVO with updated metadata informations
     */
    public ImportSourceVO updateMdEntry(ImportSourceVO source, MetadataVO md)
    {
    	Vector <MetadataVO> mdv = source.getMdFormats();

    	if (md != null)
    	{
			for (int i=0; i< mdv.size(); i++)
			{
				MetadataVO mdVO = source.getMdFormats().get(i);
	    		if (mdVO.getMdLabel().trim().toLowerCase().equals(md.getMdLabel().trim().toLowerCase()))
	    		{
	    			mdv.setElementAt(md,i);
	        	}
			}
    	}
    	
		source.setMdFormats(mdv);
    	return source;
    }
    
	/**
	 * This operation gives back the name of all formats which can be transformed into the requested format.
	 * @param transformFormat the format in which the object should be transformed
	 * @return a list of formats which can be transformed into the requested format
	 * TODO: Bring this into MetadataHandler
	 */
	public Vector<String> getFormatsForTransformation(String transformFormat)
	{
	
		Vector <String> formats = new Vector<String>();
		try
		{
			ClassLoader cl = this.getClass().getClassLoader();
			java.io.InputStream in = cl.getResourceAsStream("resources/transformations.xml"); 	
			this.transformDoc = TransformationsDocument.Factory.parse(in);
		}
		catch(XmlException e)
		{e.printStackTrace();}
		catch(IOException e)
		{e.printStackTrace();}
	
        this.transformType = this.transformDoc.getTransformations();
        TransformationType[] transformations = this.transformType.getTransformationArray();
        for (TransformationType transformation : transformations)
        {
        	MetadataformatsType mdFormats = transformation.getMetadataFormats();
        	for (MetadataformatType mdFormat : mdFormats.getMetadataFormatArray())
        	{
        		String mdFormatStr = this.simpleLiteralTostring(mdFormat.getLabel());
        		if (mdFormatStr.trim().toLowerCase().equals(transformFormat.toLowerCase()))
        		{
        			
        			formats.add(this.simpleLiteralTostring(transformation.getFormat()));
        		}
        	}
        }
        
        return formats;
	}
	
	private String simpleLiteralTostring(SimpleLiteral sl)
	{
		return sl.toString().substring(sl.toString().indexOf(">") + 1, sl.toString().lastIndexOf("<"));
	}
	
	/**
	 * Print out source values for debug purpose
	 * @param source
	 */
	public void printSourceXML(ImportSourceVO source)
	{
		String seperator = "____________________________________________________________________";
		LOGGER.info(seperator);
		LOGGER.info("Source Name      	: " + source.getName());
		LOGGER.info("Description      	: " + source.getDescription());
		LOGGER.info("Main URL         	: " + java.net.URLDecoder.decode(source.getUrl().toString()));
		LOGGER.info("Doc type     	  	: " + source.getType());
		LOGGER.info("Doc encoding     	: " + source.getEncoding());
		LOGGER.info("Identfier			: " + source.getIdentifier());
		LOGGER.info("Harvest protocol 	: " + source.getHarvestProtocol());
		LOGGER.info("Timeout		 	: " + source.getTimeout());
		LOGGER.info("Retry after	 	: " + source.getRetryAfter());
		LOGGER.info("Number of tries	: " + source.getNumberOfTries());
		LOGGER.info("Status				: " + source.getStatus());
		LOGGER.info(seperator);

		for (int i =0; i< source.getMdFormats().size(); i++)
		{
			MetadataVO md = source.getMdFormats().get(i);
			LOGGER.info(seperator);
			LOGGER.info("MD description		: " + md.getMdDesc());
			LOGGER.info("MD format			: " + md.getMdFormat());
			LOGGER.info("MD label			: " + md.getMdLabel());
			LOGGER.info("MD URL				: " + java.net.URLDecoder.decode(md.getMdUrl().toString()));
			LOGGER.info("MD default			: " + md.isMdDefault());
			LOGGER.info(seperator);
			
		}
		
		for (int i =0; i< source.getFtFormats().size(); i++)
		{
			FullTextVO ft = source.getFtFormats().get(i);
			LOGGER.info(seperator);
			LOGGER.info("FT description		: " + ft.getFtDesc());
			LOGGER.info("FT format			: " + ft.getFtFormat());
			LOGGER.info("FT label			: " + ft.getFtLabel());
			LOGGER.info("FT URL				: " + java.net.URLDecoder.decode(ft.getFtUrl().toString()));
			LOGGER.info("FT default			: " + ft.isFtDefault());
			LOGGER.info(seperator);
			
		}
	}
}