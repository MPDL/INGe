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
public class IngeTechnicalException extends PubManException {
  public IngeTechnicalException() {}

  public IngeTechnicalException(String message, Throwable cause) {
    super(message, cause);
  }

  public IngeTechnicalException(String message) {
    super(message);
  }

  public IngeTechnicalException(String message, Throwable cause, PubManException.Reason reason) {
    super(message, cause, reason);
  }

  public IngeTechnicalException(String message, PubManException.Reason reason) {
    super(message, reason);
  }

  public IngeTechnicalException(Throwable cause) {
    super(cause);
  }

  public IngeTechnicalException(Throwable cause, Reason reason) {
    super(cause, reason);
  }
}
