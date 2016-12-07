package de.mpg.mpdl.inge.inge_validation.util;

@SuppressWarnings("serial")
public class ValidationException extends Exception {
  private int statusCode;

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

  public ValidationException(int statusCode) {
    super();
    this.statusCode = statusCode;
  }

  public ValidationException(int statusCode, String message) {
    super(message);
    this.statusCode = statusCode;
  }

  public ValidationException(int statusCode, Throwable cause) {
    super(cause);
    this.statusCode = statusCode;
  }

  public ValidationException(int statusCode, String message, Throwable cause) {
    super(message, cause);
    this.statusCode = statusCode;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
  }
}
