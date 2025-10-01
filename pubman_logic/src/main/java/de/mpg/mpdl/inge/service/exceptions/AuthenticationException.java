package de.mpg.mpdl.inge.service.exceptions;

import de.mpg.mpdl.inge.model.exception.PubManException;

@SuppressWarnings("serial")
public class AuthenticationException extends PubManException {


  public AuthenticationException() {}

  public AuthenticationException(String message, Throwable cause) {
    super(message, cause);
  }

  public AuthenticationException(String message) {
    super(message);
  }

  public AuthenticationException(String message, Throwable cause, PubManException.Reason reason) {
    super(message, cause, reason);
  }

  public AuthenticationException(String message, PubManException.Reason reason) {
    super(message, reason);
  }

  public AuthenticationException(Throwable cause) {
    super(cause);
  }

}
