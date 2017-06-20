package de.mpg.mpdl.inge.service.exceptions;

import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;

@SuppressWarnings("serial")
public class AuthorizationException extends Exception {

  public AuthorizationException() {
    super();
    // TODO Auto-generated constructor stub
  }

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
