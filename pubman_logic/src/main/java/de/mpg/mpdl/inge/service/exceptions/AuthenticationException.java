package de.mpg.mpdl.inge.service.exceptions;

@SuppressWarnings("serial")
public class AuthenticationException extends Exception {


  boolean passwordChangeRequired = false;

  public AuthenticationException() {}

  public AuthenticationException(String message, Throwable cause) {
    super(message, cause);
  }

  public AuthenticationException(String message) {
    super(message);
  }

  public AuthenticationException(String message, boolean passwordChangeRequired) {
    super(message);
    this.passwordChangeRequired = passwordChangeRequired;
  }

  public AuthenticationException(Throwable cause) {
    super(cause);
  }

  public boolean isPasswordChangeRequired() {
    return passwordChangeRequired;
  }

  public void setPasswordChangeRequired(boolean passwordChangeRequired) {
    this.passwordChangeRequired = passwordChangeRequired;
  }

}
