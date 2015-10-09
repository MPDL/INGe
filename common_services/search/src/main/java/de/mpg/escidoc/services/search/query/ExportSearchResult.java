/**
 * 
 */
package de.mpg.escidoc.services.search.query;

import java.io.Serializable;
import java.util.List;

import org.apache.axis.types.NonNegativeInteger;

import de.mpg.escidoc.services.common.valueobjects.interfaces.SearchResultElement;

/**
 * Search result for an export search query.
 * 
 * @author endres
 * 
 */
public class ExportSearchResult extends ItemContainerSearchResult implements Serializable
{
    /** Serializable identifier. */
    private static final long serialVersionUID = 1L;
    /** the output of the search in a binary form (pdf, etc.). */
    private byte[] exportedResults = null;

    /**
     * Create a export search result.
     * 
     * @param result
     *            the output of the search in a binary form (pdf, etc.).
     * @param cqlQuery
     *            cql query
     * @param totalNumberOfResults  total number of search results
     */
    public ExportSearchResult(List<SearchResultElement> results, String cqlQuery,
            NonNegativeInteger totalNumberOfResults)
    {
        super(results, cqlQuery, totalNumberOfResults);
    }

    /**
     * Getter for the exported search result.
     * 
     * @return result
     */
    public byte[] getExportedResults()
    {
        return exportedResults;
    }
    
    /**
     * Setter for the exported search result.
     * 
     * @return result
     */
    public void setExportedResults(byte[] exportedResults)
    {
        this.exportedResults = exportedResults;
    }

}
