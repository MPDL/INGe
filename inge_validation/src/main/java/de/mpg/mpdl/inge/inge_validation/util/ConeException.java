package de.mpg.mpdl.inge.inge_validation.util;

@SuppressWarnings("serial")
public class ConeException extends Exception {
  private int statusCode;

  public ConeException() {
    super();
  }

  public ConeException(String message) {
    super(message);
  }

  public ConeException(Throwable cause) {
    super(cause);
  }

  public ConeException(String message, Throwable cause) {
    super(message, cause);
  }

  public ConeException(int statusCode) {
    super();
    this.statusCode = statusCode;
  }

  public ConeException(int statusCode, String message) {
    super(message);
    this.statusCode = statusCode;
  }

  public ConeException(int statusCode, Throwable cause) {
    super(cause);
    this.statusCode = statusCode;
  }

  public ConeException(int statusCode, String message, Throwable cause) {
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
