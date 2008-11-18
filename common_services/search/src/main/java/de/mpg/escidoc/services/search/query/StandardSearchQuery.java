/**
 * 
 */
package de.mpg.escidoc.services.search.query;

import java.io.IOException;

import org.z3950.zing.cql.CQLParseException;

import de.mpg.escidoc.services.search.parser.ParseException;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.search.ItemContainerSearch.IndexDatabaseSelector;

/**
 * This interface provides a ADT for standard search queries.
 * 
 * @author endres
 * 
 */
public interface StandardSearchQuery 
{
    /**
     * Getter for the index database selector.
     * @return  index database selector
     */
    public IndexDatabaseSelector getIndexSelector();

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
    public String getCqlQuery() throws CQLParseException, IOException, TechnicalException, ParseException;
}
