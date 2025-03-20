package de.mpg.mpdl.inge.service.exceptions;

@SuppressWarnings("serial")
public class AuthorizationException extends Exception {

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

}
