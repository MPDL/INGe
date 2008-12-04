package de.mpg.escidoc.services.dataacquisition.webservice;

import java.rmi.AccessException;

import de.mpg.escidoc.services.dataacquisition.exceptions.FormatNotAvailableException;
import de.mpg.escidoc.services.dataacquisition.exceptions.FormatNotRecognisedException;
import de.mpg.escidoc.services.dataacquisition.exceptions.IdentifierNotRecognisedException;
import de.mpg.escidoc.services.dataacquisition.exceptions.SourceNotAvailableException;

/**
 * UNAPI Interface for the DataAquisition Service.
 * 
 * @author Friederike Kleinfercher (initial creation)
 */
public interface Unapi
{
    /**
     * This operation gives back a description of all available sources for this unapi interface.
     * 
     * @return sourcesXML
     * @throws RuntimeException
     */
    public byte[] unapi() throws RuntimeException;

    /**
     * This operation gives back a description of all available formats for an identifier.
     * 
     * @param identifier
     * @param show defines if the identifier is visible in the formats xml
     * @return formatsXML
     * @throws RuntimeException
     */
    public byte[] unapi(String identifier, boolean show) throws RuntimeException;

    /**
     * This operation fetches the format from the specified identifier.
     * 
     * @param identifier
     * @param format
     * @return data as byte[]
     * @throws IdentifierNotRecognisedException
     * @throws SourceNotAvailableException
     * @throws RuntimeException
     * @throws FormatNotRecognisedException
     * @throws AccessException (Restricted access to the source)
     */
    public byte[] unapi(String identifier, String format) throws IdentifierNotRecognisedException,
            SourceNotAvailableException, FormatNotRecognisedException, RuntimeException, AccessException, FormatNotAvailableException;
}
