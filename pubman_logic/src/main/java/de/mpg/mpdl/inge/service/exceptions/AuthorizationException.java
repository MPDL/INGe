package de.mpg.mpdl.inge.service.exceptions;

@SuppressWarnings("serial")
public class AuthorizationException extends Exception {

  public AuthorizationException() {}

  public AuthorizationException(String message, Throwable cause) {
    super(message, cause);
    // TODO Auto-generated constructor stub
  }

  public AuthorizationException(String message) {
    super(message);
    // TODO Auto-generated constructor stub
  }

  public AuthorizationException(Throwable cause) {
    super(cause);
    // TODO Auto-generated constructor stub
  }

}
