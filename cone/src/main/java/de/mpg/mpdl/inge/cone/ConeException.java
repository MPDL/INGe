package de.mpg.mpdl.inge.cone;

@SuppressWarnings("serial")
public class ConeException extends Exception {
  public ConeException() {}

  public ConeException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public ConeException(final String message) {
    super(message);
  }

  public ConeException(final Throwable cause) {
    super(cause);
  }
}
