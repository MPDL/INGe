package de.mpg.mpdl.inge.service.exceptions;

import de.mpg.mpdl.inge.model.exception.PubManException;

@SuppressWarnings("serial")
public class IngeApplicationException extends PubManException {

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

  public IngeApplicationException(String message, Reason reason) {
    super(message, reason);
  }

  public IngeApplicationException(String message, Throwable cause, Reason reason) {
    super(message, cause, reason);
  }
}
