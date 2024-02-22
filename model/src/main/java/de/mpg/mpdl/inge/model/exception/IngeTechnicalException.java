package de.mpg.mpdl.inge.model.exception;

/**
 * Exception if a service fails
 *
 * @author walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
@SuppressWarnings("serial")
public class IngeTechnicalException extends Exception {
  public IngeTechnicalException() {}

  public IngeTechnicalException(String message, Throwable cause) {
    super(message, cause);
  }

  public IngeTechnicalException(String message) {
    super(message);
  }

  public IngeTechnicalException(Throwable cause) {
    super(cause);
  }
}
