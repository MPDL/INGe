package de.mpg.escidoc.services.importmanager;

import java.util.Vector;

public interface ImportSourceHandler {
	
	/**
	 * This methods reads in the xml description of all available import sources
	 * @return vector of ImportSource objects
	 */
	public Vector<ImportSourceVO> getSources () throws Exception;
	
	
	/**
	 * This methods reads in the xml description of a specific source
	 * @return ImportSource object
	 */
	public ImportSourceVO getSourceByName (String name);

}
