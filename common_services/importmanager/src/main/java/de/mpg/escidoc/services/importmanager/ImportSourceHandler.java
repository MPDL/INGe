package de.mpg.escidoc.services.importmanager;

import java.util.Vector;

import de.mpg.escidoc.services.importmanager.valueobjects.ImportSourceVO;

public interface ImportSourceHandler {
	
	/**
	 * This methods reads in the xml description of all available import sources
	 * @return vector of ImportSource objects
	 */
	public Vector<ImportSourceVO> getSources () throws Exception;
	
	/**
	 * This methods reads in the xml description of all import sources which can be transformed to the given format by the Metadatahandler
	 * @param format: return all import sources where a MD transformation to the given format is provided by MetadataHandler
	 * @return vector of ImportSource objects
	 */
	public Vector<ImportSourceVO> getSources (String format) throws Exception;
	
	/**
	 * This methods reads in the xml description of a specific source, identified by its name
	 * @return ImportSource object
	 */
	public ImportSourceVO getSourceByName (String name);
	
	/**
	 * This methods reads in the xml description of a specific source, identified by its identifier
	 * @return ImportSource object
	 */
	public ImportSourceVO getSourceByIdentifier (String name);

}
