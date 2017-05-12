package de.mpg.mpdl.inge.es.exception;

/**
 * Exception if a service fails
 * 
 * @author walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class IngeEsServiceException extends Exception {

  private static final long serialVersionUID = -2755845075749766737L;

  public IngeEsServiceException() {}

  public IngeEsServiceException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public IngeEsServiceException(final String message) {
    super(message);
  }

  public IngeEsServiceException(final Throwable cause) {
    super(cause);
  }

}
