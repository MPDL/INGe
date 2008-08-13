package de.mpg.escidoc.services.importmanager.webservice;

import java.io.FileNotFoundException;

import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.metadata.IdentifierNotRecognisedException;
import de.mpg.escidoc.services.importmanager.SourceNotAvailableException;


public interface Unapi{

	/**
	 * This operation gives back a description of all available sources for this unapi interface
	 * @return sourcesXML
	 */
	public String unapi();
	
	/**
	 * This operation gives back a description of all available formats for an identifier
	 * @param identifier
	 * @return formatsXML
	 */
	public String unapi (String identifier);
	
	/**
	 * This operation fetches the item (identifier) in the specified format (format)
	 * @param identifier
	 * @param format
	 */
	
	public String fetchMD (String identifier, String format)throws IdentifierNotRecognisedException, SourceNotAvailableException, TechnicalException;
	
	public byte[] fetchFT (String identifier, String format)throws FileNotFoundException;
	
}
