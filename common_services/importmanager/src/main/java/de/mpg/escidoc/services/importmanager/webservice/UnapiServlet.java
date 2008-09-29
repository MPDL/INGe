package de.mpg.escidoc.services.importmanager.webservice;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
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
import de.mpg.escidoc.services.importmanager.exceptions.FormatNotRecognizedException;
import de.mpg.escidoc.services.importmanager.exceptions.SourceNotAvailableException;
import de.mpg.escidoc.services.importmanager.valueobjects.FullTextVO;
import de.mpg.escidoc.services.importmanager.valueobjects.ImportSourceVO;
import de.mpg.escidoc.services.importmanager.valueobjects.MetadataVO;

/**
 * This class provides the implementation of the {@link Unapi} interface.
 * 
 * @author Friederike Kleinfercher (initial creation)
 */
public class UnapiServlet extends HttpServlet implements Unapi 
{

	private static final long serialVersionUID = 1L;

	private final String ID_TYPE_URI = "URI";
	private final String ID_TYPE_URL = "URL";
	private final String ID_TYPE_ESCIDOC = "ESCIDOC";
	private final String ID_TYPE_UNKNOWN = "UNKNOWN";

	private ImportHandlerBean importHandler = new ImportHandlerBean();
	private ImportSourceHandlerBean sourceHandler = new ImportSourceHandlerBean();

	private String filename = "unapi";
	
	private boolean zotero = false;

	private final static Logger logger = Logger.getLogger(UnapiServlet.class);

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException 
	{
		this.doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException 
	{

		String identifier = null;
		String format = null;
		OutputStream outStream = response.getOutputStream();

		// Retrieve the command from the location path
		String command = request.getPathInfo();		
		if (command != null && command.length() > 0) 
		{
			command = command.substring(1);
		}
		
		if (request.getRequestURL().toString().contains("zotero"))
		{
			this.zotero = true;
		}

		// Handle Call
		if ("unapi".equals(command)) 
		{
			identifier = request.getParameter("id");
			format = request.getParameter("format");

			if (identifier == null) 
			{
				// Gives back a description of escidoc formats as default
				response.setStatus(200);
				response.setContentType("application/xml");
				outStream.write(this.unapi(this.ID_TYPE_ESCIDOC));
			} 
			else 
			{
				if (format == null) 
				{
					// Gives back a description of all available formats for a
					// source
					byte[] xml = this.unapi(identifier);
					if (xml != null) 
					{
						response.setStatus(200);
						response.setContentType("application/xml");
						outStream.write(xml);
					} 
					else 
					{
						response.sendError(404, "Identifier not recognized");
					}
				} 
				else 
				{ // Fetch data
					try 
					{
						byte[] data = this.unapi(identifier, format);
						if (data == null) 
						{
							response.sendError(404, "Identifier not recognized");
						} 
						else 
						{
							response.setContentType(this.importHandler.getContentType());
							if(!this.zotero)
							{
								response.setHeader("Content-disposition", "attachment; filename="
											+ this.filename
											+ this.importHandler.getFileEnding());
							}
							response.setStatus(200);
							outStream.write(data);
						}
					} catch (IdentifierNotRecognisedException e){						
						response.sendError(404, "Identifier not recognized");
					} catch (SourceNotAvailableException e) 
					{
						response.sendError(404, "Source not available");
					} catch (TechnicalException e) 
					{
						response.sendError(404, "Technical problems occurred");
					} catch (FormatNotRecognizedException e) 
					{
						response.sendError(406, "Format not recognized");
					}
				}
			}
		} 
		else 
		{
			// Gives back a description of all available sources
			response.setStatus(200);
			response.setContentType("application/xml");
			outStream.write(this.unapi());
		}
		outStream.flush();
		outStream.close();
		this.resetValues();
	}

	/**
	 * {@inheritDoc}
	 */
	public byte[] unapi() 
	{

		Vector<ImportSourceVO> sources;

		try 
		{
			sources = this.sourceHandler.getSources();
			SourcesDocument xmlSourceDoc = SourcesDocument.Factory
					.newInstance();
			SourcesType xmlSources = xmlSourceDoc.addNewSources();

			for (int i = 0; i < sources.size(); i++) 
			{
				ImportSourceVO source = sources.get(i);
				SourceType xmlSource = xmlSources.addNewSource();

				SimpleLiteral name = xmlSource.addNewName();
				XmlString sourceName = XmlString.Factory.newInstance();
				sourceName.setStringValue(source.getName());
				name.set(sourceName);

				SimpleLiteral desc = xmlSource.addNewDescription();
				XmlString sourceDesc = XmlString.Factory.newInstance();
				sourceDesc.setStringValue(source.getDescription());
				desc.set(sourceDesc);

				// SimpleLiteral disclaim = xmlSource.addNewDisclaimer();
				// XmlString sourceDisclaim = XmlString.Factory.newInstance();
				// sourceDisclaim.setStringValue("Disclaimer will follow");
				// disclaim.set(sourceDisclaim);
			}

			return xmlSourceDoc.toString().getBytes();
		} catch (Exception e) {
			logger.error("Error writing unapi-sources.xml", e);
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public byte[] unapi(String identifier) 
	{

		Vector<FullTextVO> v_ft = new Vector<FullTextVO>();
		Vector<MetadataVO> v_md = new Vector<MetadataVO>();

		String[] tmp = identifier.split(":");
		ImportSourceVO source = this.sourceHandler
				.getSourceByIdentifier(tmp[0]);
		
		if (identifier.startsWith("http")&& identifier.contains("escidoc"))
		{
			source = this.sourceHandler.getSourceByIdentifier("escidoc");
		}
		
		// No source for this identifier
		if (source == null) 
		{
			return null;
		}
		
		FormatsDocument xmlFormatsDoc = FormatsDocument.Factory.newInstance();
		FormatsType xmlFormats = xmlFormatsDoc.addNewFormats();

		v_ft = source.getFtFormats();
		v_md = source.getMdFormats();

		// Metadata formats
		for (int i = 0; i < v_md.size(); i++) 
		{
			MetadataVO md = v_md.get(i);
			FormatType xmlFormat = xmlFormats.addNewFormat();

			xmlFormat.setName(md.getMdLabel().toLowerCase());
			xmlFormat.setType(md.getMdFormat());
			if (md.getMdDesc() != null) {
				xmlFormat.setDocs(md.getMdDesc());
			}
		}

		// File formats
		for (int i = 0; i < v_ft.size(); i++) 
		{
			FullTextVO ft = v_ft.get(i);
			FormatType xmlFormat = xmlFormats.addNewFormat();

			xmlFormat.setName(ft.getFtLabel());
			xmlFormat.setType(ft.getFtFormat());
		}

		return xmlFormatsDoc.toString().getBytes();
		// TODO
		// Get additional formats provided by internal transformations
	}

	/**
	 * {@inheritDoc}
	 */
	public byte[] unapi(String identifier, String format)
			throws IdentifierNotRecognisedException,
			SourceNotAvailableException, TechnicalException,
			FormatNotRecognizedException 
	{
		this.filename = identifier;
		String[] tmp = identifier.split(":");
		String sourceId = tmp[0];
		String id = tmp[1];
		String sourceName = this.sourceHandler
				.getSourceNameByIdentifier(sourceId);
		String idType = this.checkIdentifier(identifier, format);

		try 
		{
			if (idType.equals(this.ID_TYPE_URI)) 
			{
				if (sourceName != null) 
				{
					return this.importHandler.doFetch(sourceName, id, format);
				}
			}
			if (idType.equals(this.ID_TYPE_URL)) 
			{
				return this.importHandler.fetchMetadatafromURL(new URL(
						identifier));
			}
			if (idType.equals(this.ID_TYPE_ESCIDOC)) 
			{
				id = this.setEsciDocIdentifier(identifier);
				sourceName = this.sourceHandler
						.getSourceNameByIdentifier("escidoc");
				this.filename = id;
				return this.importHandler.doFetch(sourceName, id, format);
			}
			if (idType.equals(this.ID_TYPE_UNKNOWN) || sourceName == null) 
			{
				throw new IdentifierNotRecognisedException();
			}
		} catch (FileNotFoundException e) 
		{
			throw new TechnicalException();
		} catch (IdentifierNotRecognisedException e) 
		{
			throw new IdentifierNotRecognisedException();
		} catch (SourceNotAvailableException e) 
		{
			throw new SourceNotAvailableException();
		} catch (TechnicalException e) 
		{
			throw new TechnicalException();
		} catch (MalformedURLException e) 
		{
			throw new TechnicalException();
		} catch (FormatNotRecognizedException e) 
		{
			throw new FormatNotRecognizedException();
		}

		return null;
	}

	private String checkIdentifier(String identifier, String format)
	{
		identifier = identifier.toLowerCase().trim();
		if (identifier.contains("escidoc:")) 
		{
			return this.ID_TYPE_ESCIDOC;
		}
		if (identifier.startsWith("http")) 
		{
			return this.ID_TYPE_URL;
		}
//		if (identifier.startsWith("http")
//				&& !identifier.contains("escidoc")) 
//		{
//			return this.ID_TYPE_UNKNOWN;
//		}

		return this.ID_TYPE_URI;
	}

	/**
	 * EsciDoc Identifier can consist of the citation URL, like.
	 * http://test-pubman.mpdl.mpg.de:8080/pubman/item/escidoc:1048:3. This
	 * methods extracts the identifier from the URL
	 */
	private String setEsciDocIdentifier(String Identifier) 
	{
		String[] extracts = Identifier.split("/");
		return extracts[extracts.length - 1];
	}
	
	private void resetValues()
	{
		this.importHandler.setContentType("");
		this.importHandler.setFileEnding("");
		this.filename = ("");
	}

}
