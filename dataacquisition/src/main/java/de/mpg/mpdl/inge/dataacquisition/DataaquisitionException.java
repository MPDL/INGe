package de.mpg.mpdl.inge.dataacquisition;

@SuppressWarnings("serial")
public class DataaquisitionException extends Exception {

  public DataaquisitionException() {}

  public DataaquisitionException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public DataaquisitionException(final String message) {
    super(message);
  }

  public DataaquisitionException(final Throwable cause) {
    super(cause);
  }
}
