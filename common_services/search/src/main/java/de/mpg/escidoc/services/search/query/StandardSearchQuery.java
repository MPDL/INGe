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
 * @author endres
 *
 */
public interface StandardSearchQuery  {
	public IndexDatabaseSelector getIndexSelector();
	
	public String getCqlQuery() throws CQLParseException, IOException, TechnicalException, ParseException;
}
