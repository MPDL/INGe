package de.mpg.mpdl.inge.dataacquisition;

@SuppressWarnings("serial")
public class DataacquisitionException extends Exception {

  public DataacquisitionException() {}

  public DataacquisitionException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public DataacquisitionException(final String message) {
    super(message);
  }

  public DataacquisitionException(final Throwable cause) {
    super(cause);
  }
}
