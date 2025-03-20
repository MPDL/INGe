package de.mpg.mpdl.inge.service.exceptions;

@SuppressWarnings("serial")
public class IngeApplicationException extends Exception {

  public IngeApplicationException() {}

  public IngeApplicationException(String message, Throwable cause) {
    super(message, cause);
  }

  public IngeApplicationException(String message) {
    super(message);
  }

  public IngeApplicationException(Throwable cause) {
    super(cause);
  }

}
