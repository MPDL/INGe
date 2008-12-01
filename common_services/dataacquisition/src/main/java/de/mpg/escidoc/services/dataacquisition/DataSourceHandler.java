package de.mpg.escidoc.services.dataacquisition;

import java.util.Vector;

import de.mpg.escidoc.services.dataacquisition.valueobjects.DataSourceVO;

/**
 * interface for methods regarding the import sources description.
 * 
 * @author kleinfe1
 */
public interface DataSourceHandler
{
    /**
     * This methods reads in the xml description of all available import sources.
     * 
     * @return vector of ImportSource objects
     * @throws RuntimeException
     */
    public Vector<DataSourceVO> getSources() throws RuntimeException;

    /**
     * This methods reads in the xml description of all import sources which can be transformed to the given format by
     * the Metadatahandler.
     * 
     * @param format return all import sources where a MD transformation to the given format is provided by
     *            MetadataHandler
     * @return vector of ImportSource objects
     * @throws RuntimeException
     */
    public Vector<DataSourceVO> getSources(String format) throws RuntimeException;

    /**
     * This methods reads in the xml description of a specific source, identified by its name.
     * 
     * @param name the name of the source
     * @return ImportSource object
     * @throws RuntimeException
     */
    public DataSourceVO getSourceByName(String name) throws RuntimeException;

    /**
     * This methods reads in the xml description of a specific source, identified by its identifier.
     * 
     * @param identifier the identifier of the source
     * @return ImportSource object
     * @throws RuntimeException
     */
    public DataSourceVO getSourceByIdentifier(String identifier) throws RuntimeException;
}
