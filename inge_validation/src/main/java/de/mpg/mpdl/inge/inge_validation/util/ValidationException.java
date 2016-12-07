package de.mpg.mpdl.inge.inge_validation.util;

@SuppressWarnings("serial")
public class ValidationException extends Exception {

  public ValidationException() {
    super();
  }

  public ValidationException(String message) {
    super(message);
  }

  public ValidationException(Throwable cause) {
    super(cause);
  }

  public ValidationException(String message, Throwable cause) {
    super(message, cause);
  }

}
