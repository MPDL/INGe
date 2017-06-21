package de.mpg.mpdl.inge.inge_validation.exception;

@SuppressWarnings("serial")
public class ValidationServiceException extends Exception {

  public ValidationServiceException() {}

  public ValidationServiceException(String message) {
    super(message);
  }

  public ValidationServiceException(Throwable cause) {
    super(cause);
  }

  public ValidationServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}
