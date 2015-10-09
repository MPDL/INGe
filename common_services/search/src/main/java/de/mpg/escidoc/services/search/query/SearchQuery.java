/**
 * 
 */
package de.mpg.escidoc.services.search.query;

import java.io.IOException;
import java.io.Serializable;
import java.util.StringTokenizer;

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
    /** Sorting Definition. */
    public enum SortingOrder 
    {
        /** Ascending order. */
        ASCENDING,
        /** Descending order */
        DESCENDING
    };
    /** Serial version identifier. */
    private static final long serialVersionUID = 1L;
    /** Defines where the offset for the search result will be. */
    private PositiveInteger startRecord = null;
    /** Defines how many results shall be retrieved. */
    private NonNegativeInteger maximumRecords = null;
    /** Sorting keys. */
    private String sortKeys = null;
    /** Sorting order */
    private SortingOrder sortingOrder = null;
    /** Maximum number of results. */
    // changed to 1000 due to java heap space problems
    private static final int DEFAULT_MAXIMUM_RECORDS = 5000;
    /** Cql definition for a descending order of the search result */
    private static final String CQL_DESCENDING_DEFINITION = ",,0";

    /**
     *  Default constructor. 
     */
    public SearchQuery()
    {
        this.maximumRecords = new NonNegativeInteger(DEFAULT_MAXIMUM_RECORDS + "");
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

    public void setStartRecord(String startRecord)
    {
        try
        {
            PositiveInteger i = new PositiveInteger(startRecord);
            this.startRecord = i;
        } 
        catch (Exception e)
        {
            // trying to set an invalid value, restoring to default
        }
    }

    public NonNegativeInteger getMaximumRecords()
    {
        return maximumRecords;
    }

    public void setMaximumRecords(NonNegativeInteger maximumRecords)
    {
        if (maximumRecords.compareTo(this.maximumRecords) < 0)
        {
            this.maximumRecords = maximumRecords;
        }
    }

    public void setMaximumRecords(String maximumRecords)
    {
        NonNegativeInteger i = new NonNegativeInteger(maximumRecords);
        setMaximumRecords(i);
    }
    
    /**
     * Returns the Cql query for the sorting of the search result.  
     * @return cql query defintion 
     */
    public String getCqlSortingQuery() 
    {
        if(this.sortKeys == null || this.sortingOrder == null  || this.sortKeys.contains(CQL_DESCENDING_DEFINITION))
        {
            return this.sortKeys;
        }
        else
        {
            if(this.sortingOrder == SortingOrder.ASCENDING)
            {
                return this.sortKeys;
            }
            else
            {
                // if several sorting keys are given, append the sort order to each one
                if(this.sortKeys != null) 
                {
                    StringTokenizer t = new StringTokenizer(this.sortKeys);
                    StringBuffer sortResult = new StringBuffer();
                    while (t.hasMoreTokens())
                    {
                        String singleSortKey = t.nextToken();
                        sortResult.append(singleSortKey);
                        sortResult.append(CQL_DESCENDING_DEFINITION);
                        sortResult.append(" ");
                    }
                    return sortResult.toString();
                }
                else return null;
            }
        }
    }

    /**
     * Setter for the sort keys.
     * 
     * @param keys the sorting key
     */
    public void setSortKeys(String keys)
    {
        this.sortKeys = keys;
    }
    
    /**
     * Set the sortKeys and the order.
     * 
     * @param keys the sorting key
     * @param order the sorting order
     */
    public void setSortKeysAndOrder(String keys, SortingOrder order)
    {
        this.sortKeys = keys;
        this.sortingOrder = order;
    }
    
    /**
     * Set the sort order.
     *
     * @param order the sorting order
     */
    public void setSortOrder(SortingOrder order)
    {
        this.sortingOrder = order;
    }
}
