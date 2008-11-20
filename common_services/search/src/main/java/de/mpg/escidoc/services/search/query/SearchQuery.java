/**
 * 
 */
package de.mpg.escidoc.services.search.query;

import java.io.IOException;
import java.io.Serializable;

import org.apache.axis.types.NonNegativeInteger;
import org.apache.axis.types.PositiveInteger;
import org.z3950.zing.cql.CQLParseException;

import de.mpg.escidoc.services.search.parser.ParseException;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;

/**
 * This interface provides a ADT for standard search queries.
 * 
 * @author endres
 * 
 */
public abstract class SearchQuery implements Serializable
{
    /** Serial version identifier. */
    private static final long serialVersionUID = 1L;
    /** Defines where the offset for the search result will be.*/
    private PositiveInteger startRecord = null;
    /** Defines how many results shall be retrieved. */
    private NonNegativeInteger maximumRecords = null;
    
    private static final String DEFAULT_MAXIMUM_RECORDS = "10000";
    
    /**
     * 
     */
    public SearchQuery() 
    {
        this.maximumRecords = new NonNegativeInteger(DEFAULT_MAXIMUM_RECORDS);
        this.startRecord = null;
    }
    
    /**
     * Get the Cql query of a standard search query.
     * 
     * @return cql query
     * @throws CQLParseException
     *             a parse error occur, when building up the node tree
     * @throws IOException
     *             an io error occurs
     * @throws TechnicalException
     *             an internal error occurs
     * @throws ParseException
     *             a parse error occur, when parsing the search terms in the
     *             criteria
     */
    public abstract String getCqlQuery() throws CQLParseException, IOException, TechnicalException, ParseException;

    public PositiveInteger getStartRecord()
    {
        return startRecord;
    }

    public void setStartRecord(PositiveInteger startRecord)
    {
        this.startRecord = startRecord;
    }

    public NonNegativeInteger getMaximumRecords()
    {
        return maximumRecords;
    }

    public void setMaximumRecords(NonNegativeInteger maximumRecords)
    {
        this.maximumRecords = maximumRecords;
    }
}
