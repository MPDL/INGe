package de.mpg.mpdl.inge.service.exceptions;

import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;

@SuppressWarnings("serial")
public class AuthenticationException extends Exception {

  public AuthenticationException() {
    super();
    // TODO Auto-generated constructor stub
  }

  public AuthenticationException(String message, Throwable cause) {
    super(message, cause);
    // TODO Auto-generated constructor stub
  }

  public AuthenticationException(String message) {
    super(message);
    // TODO Auto-generated constructor stub
  }

  public AuthenticationException(Throwable cause) {
    super(cause);
    // TODO Auto-generated constructor stub
  }

}
