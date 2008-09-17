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
 * @author endres
 *
 */
public interface StandardSearchQuery  {
	public IndexDatabaseSelector getIndexSelector();
	
	/**
	 * Get the Cql query of a standard search query
	 * @return cql query
	 * @throws CQLParseException
	 * @throws IOException
	 * @throws TechnicalException
	 * @throws ParseException
	 */
	public String getCqlQuery() throws CQLParseException, IOException, TechnicalException, ParseException;
}
