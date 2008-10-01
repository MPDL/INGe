/**
 * 
 */
package de.mpg.escidoc.services.search.query;

import java.io.Serializable;

/**
 * Search result for an export search query.
 * 
 * @author endres
 * 
 */
public class ExportSearchResult extends SearchResult implements Serializable
{
    /** Serializable identifier. */
    private static final long serialVersionUID = 1L;
    /** the output of the search in a binary form (pdf, etc.). */
    private byte[] result = null;

    /**
     * Create a export search result.
     * 
     * @param result
     *            the output of the search in a binary form (pdf, etc.).
     * @param cqlQuery
     *            cql query
     */
    public ExportSearchResult(byte[] result, String cqlQuery)
    {
        super(cqlQuery);
        this.result = result;
    }

    /**
     * Getter for the binary search result.
     * 
     * @return result
     */
    public byte[] getResult()
    {
        return result;
    }

}
