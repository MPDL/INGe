/**
 * 
 */
package de.mpg.escidoc.services.search.query;

import java.io.Serializable;

/**
 * @author endres
 *
 */
public class ExportSearchResult extends SearchResult implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private byte[] result = null;
	
	public ExportSearchResult( byte[] result, String cqlQuery ) {
		super( cqlQuery );
		this.result = result;
	}

	public byte[] getResult() {
		return result;
	}

}
