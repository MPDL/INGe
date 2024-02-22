package de.mpg.mpdl.inge.cone;

@SuppressWarnings("serial")
public class ConeException extends Exception {
  public ConeException() {}

  public ConeException(String message, Throwable cause) {
    super(message, cause);
  }

  public ConeException(String message) {
    super(message);
  }

  public ConeException(Throwable cause) {
    super(cause);
  }
}
