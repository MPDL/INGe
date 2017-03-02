package de.mpg.mpdl.inge.inge_validation.exception;

@SuppressWarnings("serial")
public class ValidationException extends Exception {

  public ValidationException() {}

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
