package de.mpg.mpdl.inge.service.exceptions;

import de.mpg.mpdl.inge.model.exception.PubManException;

@SuppressWarnings("serial")
public class AuthorizationException extends PubManException {

  public AuthorizationException() {}

  public AuthorizationException(String message, Throwable cause) {
    super(message, cause);
  }

  public AuthorizationException(String message) {
    super(message);
  }

  public AuthorizationException(Throwable cause) {
    super(cause);
  }

  public AuthorizationException(String message, Reason reason) {
    super(message, reason);
  }

  public AuthorizationException(String message, Throwable cause, Reason reason) {
    super(message, cause, reason);
  }
}
