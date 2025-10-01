package de.mpg.mpdl.inge.rest.web.exceptions;

import de.mpg.mpdl.inge.model.exception.PubManException;

@SuppressWarnings("serial")
public class NotFoundException extends PubManException {
  public NotFoundException() {}

  public NotFoundException(String message) {
    super(message);
  }

  public NotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public NotFoundException(String message, Reason reason) {
    super(message, reason);
  }

  public NotFoundException(String message, Throwable cause, Reason reason) {
    super(message, cause, reason);
  }

  public NotFoundException(Throwable cause) {
    super(cause);
  }

  public NotFoundException(Throwable cause, Reason reason) {
    super(cause, reason);
  }

  public NotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
