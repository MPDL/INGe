package de.mpg.mpdl.inge.dataacquisition;

@SuppressWarnings("serial")
public class DataacquisitionException extends Exception {

  public DataacquisitionException() {}

  public DataacquisitionException(String message, Throwable cause) {
    super(message, cause);
  }

  public DataacquisitionException(String message) {
    super(message);
  }

  public DataacquisitionException(Throwable cause) {
    super(cause);
  }
}
