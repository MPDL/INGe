package de.mpg.mpdl.inge.services;

/**
 * Exception if a service fails
 * 
 * @author walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class IngeServiceException extends Exception {

	private static final long serialVersionUID = -2755845075749766737L;

	public IngeServiceException() {
		super();
	}

	public IngeServiceException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public IngeServiceException(final String message) {
		super(message);
	}

	public IngeServiceException(final Throwable cause) {
		super(cause);
	}

}
