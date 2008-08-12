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

import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.metadata.IdentifierNotRecognisedException;
import de.mpg.escidoc.services.importmanager.FullTextVO;
import de.mpg.escidoc.services.importmanager.ImportHandlerBean;
import de.mpg.escidoc.services.importmanager.ImportSourceHandlerBean;
import de.mpg.escidoc.services.importmanager.ImportSourceVO;
import de.mpg.escidoc.services.importmanager.SourceNotAvailableException;

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
            	 outStream.write(this.unapi().getBytes());
             }
	         else 
	         {	        	 
	        	 if (format == null || format.trim().equals("")){
	        		//Gives back a description of all available formats for a source
	        		String xml = this.unapi(identifier);
	        		if (xml != null){
	        			outStream.write(xml.getBytes());
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
	 
	    

	public String unapi(){
		
		Vector <ImportSourceVO> sources = new Vector <ImportSourceVO> ();
		String unapiSources = "";
		try {
			sources = this.sourceHandler.getSources();
			
			unapiSources = "<?xml version='1.0' encoding='UTF-8'?> \n";
			unapiSources += "<sources> \n";
			
			for (int i=0; i< sources.size(); i++){
				ImportSourceVO source = sources.get(i);
				unapiSources += "<source> \n" +
									"<name>" + source.getName() + "</name> \n" + 
									"<description>" +source.getDescription()+ "</description> \n"+
								"</source>\n";
			}
			
			unapiSources += "</sources>";
		} 
		catch (Exception e) {e.printStackTrace();}		
		return unapiSources;		
	}
	
	public String unapi (String identifier){
		Vector <FullTextVO> v_ft = new Vector <FullTextVO>();
		FullTextVO ft = null;
		ImportSourceVO source = null;
		
		String formats ="<?xml version='1.0' encoding='UTF-8'?> \n";
		String [] tmp = identifier.split(":");
		source = this.sourceHandler.getSourceByName(tmp[0]);
		
		//No source for this identifier
		if (source == null){return null;}
		v_ft = source.getFtFormats();
		String [] additionalFormats = null;
		
		formats += "<formats id ='"+source.getName()+"'>";
			formats += "<format name ='" +source.getMdLabel()+ "' type ='application/xml' docs='"+ source.getMdDesc()+"'/>";
			for (int i =0; i< v_ft.size(); i++){
				ft = v_ft.get(i);
				formats += "<format name ='" +ft.getFtLabel()+ "' type ='"+ft.getFtFormat()+"'/>";
			}
			
		//TODO
		//Get additional formats provided by internal transformations
//		additionalFormats = this.mdHandler.getTransformations(source.getMdLabel());
//		if (additionalFormats!=null){
//			for (int i=0; i< additionalFormats.length; i++){
//				String newFormat = additionalFormats[i];
//				//TODO Dynamic type
//				formats += "<format name ='" +newFormat+ "' type ='application/xml'/>";
//			}
//		}
		formats += "</formats>";

		//TODO: Add transformation formats, bring in id
		return formats;	
	}
		
	public String fetchMD (String identifier, String format) throws IdentifierNotRecognisedException, SourceNotAvailableException, TechnicalException{	
		String [] tmp = identifier.split(":");
		String source = tmp[0];
		String id = tmp [1];

		return this.importHandler.fetchMetadata(source, id, format);
	}
	
	public byte[] fetchFT (String identifier, String format) throws FileNotFoundException{
		String [] tmp = identifier.split(":");
		String source = tmp[0];
		String id = tmp [1];
		
		return this.importHandler.fetchData(source, id, new String[]{format});	
	}
	
	public String checkFormatType(String identifier, String format){

		if (identifier.startsWith("http")&& format.trim().toLowerCase().equals("url")){
			return this.FORMAT_TYPE_URL;
		}
		
		String [] tmp = identifier.split(":");
		ImportSourceVO source = null;
		source = this.sourceHandler.getSourceByName(tmp[0]);
		
		if (source != null){
		
			this.responseEncoding = source.getEncoding();
			if (format.trim().toLowerCase().equals(source.getMdLabel().trim().toLowerCase())){
				this.formatIntern = source.getMdFormat();
				return this.FORMAT_TYPE_MD;
			}
			
			Vector <FullTextVO> v_ft = source.getFtFormats();
			FullTextVO ft = null;
			
			for (int i=0; i< v_ft.size(); i++){
				ft = v_ft.get(i);
				if (ft.getFtLabel().trim().toLowerCase().equals(format.trim().toLowerCase())){
					this.formatIntern = ft.getFtFormat();
					return this.FORMAT_TYPE_FT;
				}
			}			
		}
		
		return null;
	}	
}
