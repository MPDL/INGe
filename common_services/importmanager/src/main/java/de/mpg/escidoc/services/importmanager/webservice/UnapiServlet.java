package de.mpg.escidoc.services.importmanager.webservice;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlString;
import org.purl.dc.elements.x11.SimpleLiteral;

import de.mpg.escidoc.metadataprofile.schema.x01.formats.FormatType;
import de.mpg.escidoc.metadataprofile.schema.x01.formats.FormatsDocument;
import de.mpg.escidoc.metadataprofile.schema.x01.formats.FormatsType;
import de.mpg.escidoc.metadataprofile.schema.x01.formats.SourceType;
import de.mpg.escidoc.metadataprofile.schema.x01.formats.SourcesDocument;
import de.mpg.escidoc.metadataprofile.schema.x01.formats.SourcesType;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.metadata.IdentifierNotRecognisedException;
import de.mpg.escidoc.services.importmanager.ImportHandlerBean;
import de.mpg.escidoc.services.importmanager.ImportSourceHandlerBean;
import de.mpg.escidoc.services.importmanager.exceptions.SourceNotAvailableException;
import de.mpg.escidoc.services.importmanager.valueobjects.FullTextVO;
import de.mpg.escidoc.services.importmanager.valueobjects.ImportSourceVO;
import de.mpg.escidoc.services.importmanager.valueobjects.MetadataVO;

/**
 * unapi response codes
 * 300 Multiple Choices for the UNAPI?id=IDENTIFIER function
 * 302 Found for responses to the UNAPI?id=IDENTIFIER&format=FORMAT function which redirect
 * 404 Not Found for requests for an identifier that is not available on the server
 * 406 Not Acceptable for requests for an identifier that is available on the server in a format that is not available for that identifier
*/

public class UnapiServlet extends HttpServlet implements Unapi {
	
	private final String FORMAT_TYPE_MD = "METADATA";
	private final String FORMAT_TYPE_FT = "FULLTEXT";
	private final String FORMAT_TYPE_URL = "URL";
	
	
	private ImportHandlerBean importHandler = new ImportHandlerBean();
	private ImportSourceHandlerBean sourceHandler = new ImportSourceHandlerBean();
	
	private String formatIntern = null;
	private String responseEncoding = null;
	
	private final static Logger logger = Logger.getLogger(UnapiServlet.class);

	public void doGet(HttpServletRequest request, HttpServletResponse response)
	  	throws ServletException, IOException {
		 this.doPost(request, response);
	}
	 
	 public void doPost(HttpServletRequest request, HttpServletResponse response)
	  	throws ServletException, IOException {

		 String identifier = null;
		 String format = null;
		 OutputStream outStream = response.getOutputStream();
		 
         // Retrieve the command from the location path
         String command = request.getPathInfo();
         if (command != null && command.length() > 0)
         {
             command = command.substring(1);
         }
		 
         // Handle Call
         if ("unapi".equals(command))
         {
             identifier = request.getParameter("id");
             format = request.getParameter("format");
             
             if (identifier == null || identifier.trim().equals(""))
             {
            	 //Gives back a description of all available sources
                 response.setStatus(200);
                 response.setContentType("application/xml");
            	 outStream.write(this.unapi());
             }
	         else 
	         {	        	 
	        	 if (format == null || format.trim().equals("")){
	        		//Gives back a description of all available formats for a source
	        		byte[] xml = this.unapi(identifier);
	        		if (xml != null){
	                    response.setStatus(200);
	                    response.setContentType("application/xml");
	        			outStream.write(xml);
	        		}
	        		else {
	        			response.sendError(404, "Identifier not recognized");
	        		}
	             }  
	        	 else 
	        	 {	        	 
	        		 String fetchingType = this.checkFormatType(identifier, format);

	        		 //fetching type not recognized
	        		 if (fetchingType == null){
	        			 //Identifier not recognized
	        			 response.sendError(406, "Format not recognized");
	        		 }	
	        		 else {
		        		 //Give back itemXML
		        		 if (fetchingType.equals(this.FORMAT_TYPE_MD)){
		        			 try {	        				 
			     				String md = this.fetchMD(identifier, this.formatIntern);		     					
				                response.setContentType("application/xml");
				                response.setHeader( "Content-Encoding ", this.responseEncoding );
				                response.setStatus(200);
				                outStream.write(md.getBytes());
		     				} catch (IdentifierNotRecognisedException e) {response.sendError(404, "Identifier not recognized");
		     				} catch (SourceNotAvailableException e) {e.printStackTrace();
		     				} catch (TechnicalException e) {e.printStackTrace();}
		        		 }
		        		 //Give back dataStream
		        		 if (fetchingType.equals(this.FORMAT_TYPE_FT)){
		        			 try{ 
			        			 byte[] content = this.fetchFT(identifier, this.formatIntern);

				                 response.setContentLength(content.length);
				                 response.setStatus(200);
				                 response.setContentType("application/zip");
				                 response.setHeader("Content-disposition", "attachment; filename=unapi");
				                 outStream.write(content);
		        			 }
		        			 catch(FileNotFoundException e){response.sendError(404, "Identifier not recognized");}
		        		 }
		        		 //fetch from url
		        		 if (fetchingType.equals(this.FORMAT_TYPE_URL)){
		        			 try {						
			        			 byte[] content = this.importHandler.fetchMetadatafromURL(new URL (identifier));	  
			        			 
				                 response.setContentLength(content.length);
				                 response.setStatus(200);
				                 response.setContentType("application/zip");
				                 response.setHeader("Content-disposition", "attachment; filename=unapi");
				                 outStream.write(content);
							 } 
		        			 catch (SourceNotAvailableException e) {e.printStackTrace();} 
		        			 catch (TechnicalException e) {e.printStackTrace();}
		        		 }	
	        		 }
	        	 }
	         }
         }
         else {
        	 response.sendError(404, "Unknown method");
         }
         outStream.flush();
         outStream.close();
	}
	 
	    

	public byte[] unapi(){

		Vector<ImportSourceVO> sources;
		
		try {
			sources = this.sourceHandler.getSources();
			SourcesDocument xmlSourceDoc = SourcesDocument.Factory.newInstance();
			SourcesType xmlSources = xmlSourceDoc.addNewSources();
	
			for (int i=0; i< sources.size(); i++){
				ImportSourceVO source = sources.get(i);				
				SourceType xmlSource = xmlSources.addNewSource();
				
				SimpleLiteral name = xmlSource.addNewName();
				XmlString sourceName =  XmlString.Factory.newInstance();
				sourceName.setStringValue(source.getName());
				name.set(sourceName);
				
				SimpleLiteral desc = xmlSource.addNewDescription();
				XmlString sourceDesc = XmlString.Factory.newInstance();
				sourceDesc.setStringValue(source.getDescription());
				desc.set(sourceDesc);
				
//				SimpleLiteral disclaim = xmlSource.addNewDisclaimer();
//				XmlString sourceDisclaim = XmlString.Factory.newInstance();
//				sourceDisclaim.setStringValue("Disclaimer will follow");
//				disclaim.set(sourceDisclaim);
			}
			
			return xmlSourceDoc.toString().getBytes();
		} 
		catch (Exception e)  {logger.error("Error writing unapi-sources.xml", e);}
		
		return null;
	}
	
	
	public byte[] unapi (String identifier){
		
		Vector <FullTextVO> v_ft = new Vector <FullTextVO>();
		Vector <MetadataVO> v_md = new Vector <MetadataVO>();

		String [] tmp = identifier.split(":");
		ImportSourceVO source = this.sourceHandler.getSourceByIdentifier(tmp[0]);
		//No source for this identifier
		if (source == null){return null;}
		
		FormatsDocument xmlFormatsDoc = FormatsDocument.Factory.newInstance();		
		FormatsType xmlFormats = xmlFormatsDoc.addNewFormats();

		v_ft = source.getFtFormats();
		v_md = source.getMdFormats();

			//Metadata formats
			for (int i =0; i< v_md.size(); i++){
				MetadataVO md = v_md.get(i);
				FormatType xmlFormat = xmlFormats.addNewFormat();
				
				xmlFormat.setName(md.getMdLabel());
				xmlFormat.setType(md.getMdMime());
				xmlFormat.setDocs(md.getMdDesc());			
			}
			
			//File formats
			for (int i =0; i< v_ft.size(); i++){
				FullTextVO ft = v_ft.get(i);
				FormatType xmlFormat = xmlFormats.addNewFormat();
				
				xmlFormat.setName(ft.getFtLabel());
				xmlFormat.setType(ft.getFtFormat());
			}
			
		return xmlFormatsDoc.toString().getBytes();
		//TODO
		//Get additional formats provided by internal transformations
	}
		
	public String fetchMD (String identifier, String format) throws IdentifierNotRecognisedException, SourceNotAvailableException, TechnicalException{	
		String [] tmp = identifier.split(":");
		String sourceId = tmp[0];
		String id = tmp [1];
		String sourceName = this.sourceHandler.getSourceNameByIdentifier(sourceId);

		return this.importHandler.fetchMetadata(sourceName, id, format);
	}
	
	public byte[] fetchFT (String identifier, String format) throws FileNotFoundException{
		String [] tmp = identifier.split(":");
		String sourceId = tmp[0];
		String id = tmp [1];
		String sourceName = this.sourceHandler.getSourceNameByIdentifier(sourceId);
		
		return this.importHandler.fetchData(sourceName, id, new String[]{format});	
	}
	
	/**
	 * This operation checks the type of the request. this unapi interface supports three kind of formats:
	 * 1: FORMAT_TYPE_URL - The identifier is an url, the interface has no informations about the source
	 * 2: FORMAT_TYPE_MD - The type of format to fetch is Metadata
	 * 3: FORMAT_TYPE_FT - The type of format to fetch is a file
	 * @param identifier
	 * @param format
	 */
	public String checkFormatType(String identifier, String format){

		if (identifier.startsWith("http")&& format.trim().toLowerCase().equals("url")){
			return this.FORMAT_TYPE_URL;
		}
		
		String [] tmp = identifier.split(":");
		ImportSourceVO source = null;
		source = this.sourceHandler.getSourceByIdentifier(tmp[0]);
		
		if (source != null){
		
			this.responseEncoding = source.getEncoding();
			
			Vector <MetadataVO> v_md = source.getMdFormats();
			MetadataVO md = null;
			
			for (int i=0; i< v_md.size(); i++){
				md = v_md.get(i);
				if (md.getMdLabel().trim().toLowerCase().equals(format.trim().toLowerCase())){
					this.formatIntern = md.getMdFormat();
					return this.FORMAT_TYPE_MD;
				}
			}

			Vector <FullTextVO> v_ft = source.getFtFormats();
			FullTextVO ft = null;
			
			for (int i=0; i< v_ft.size(); i++){
				ft = v_ft.get(i);
				if (ft.getFtLabel().trim().toLowerCase().equals(format.trim().toLowerCase())){
					this.formatIntern = ft.getFtLabel();
					return this.FORMAT_TYPE_FT;
				}
			}			
		}
		
		return null;
	}	
}
