package de.mpg.escidoc.services.importmanager.webservice;

import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.metadata.IdentifierNotRecognisedException;
import de.mpg.escidoc.services.importmanager.exceptions.FormatNotRecognizedException;
import de.mpg.escidoc.services.importmanager.exceptions.SourceNotAvailableException;

/**
 * UNAPI Interface for the DataAquisition Service.
 * @author Friederike Kleinfercher (initial creation) 
 */
public interface Unapi
{

	/**
	 * This operation gives back a description of all available sources for this unapi interface.
	 * @return sourcesXML
	 */
	public byte[] unapi();
	
	/**
	 * This operation gives back a description of all available formats for an identifier.
	 * @param identifier
	 * @return formatsXML
	 */
	public byte[] unapi (String identifier);
	

	/**
	 * This operation fetches the format from the specified identifier.
	 * @param identifier
	 * @param format
	 * @return data as byte[]
	 */
	public byte[] unapi (String identifier, String format)throws IdentifierNotRecognisedException,
																 SourceNotAvailableException,
																 TechnicalException,
																 FormatNotRecognizedException;
	
}
