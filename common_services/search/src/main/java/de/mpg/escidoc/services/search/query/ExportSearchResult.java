/**
 * 
 */
package de.mpg.escidoc.services.search.query;

/**
 * @author endres
 *
 */
public class ExportSearchResult extends SearchResult {
	private byte[] result = null;
	
	public ExportSearchResult( byte[] result, String cqlQuery ) {
		super( cqlQuery );
		this.result = result;
	}

	public byte[] getResult() {
		return result;
	}

}
