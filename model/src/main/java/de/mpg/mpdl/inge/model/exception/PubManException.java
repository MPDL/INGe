package de.mpg.mpdl.inge.model.exception;

public class PubManException extends Exception {

  public enum Reason
  {
        PASSWORD_CHANGE_REQUIRED,
        TOKEN_INVALID,
        LOGIN_INVALID,
        USER_NOT_FOUND,
        LOGIN_USER_BLOCKED,
        PASSWORD_PATTERN_INVALID,
        PERMISSION_DENIED,

        ITEM_NOT_FOUND,
        CONTEXT_NOT_FOUND,
        CONTEXT_CLOSED,
        PID_ERROR,
        DOI_TECHNICAL_ERROR,
        DOI_ITEM_NOT_VALID,
        FILE_UPLOAD_ERROR,
        VALIDATION_ERROR,
        TRANSFORMATION_ERROR,

        OPTIMISTIC_LOCKING_ERROR,
        GENERIC_NOT_FOUND,

    }



  private Reason reason;

  public PubManException() {}

  public PubManException(String message) {
    super(message);
  }

  public PubManException(String message, Throwable cause) {
    super(message, cause);
  }

  public PubManException(String message, Reason reason) {
    super(message);
    this.reason = reason;
  }

  public PubManException(String message, Throwable cause, Reason reason) {
    super(message, cause);
    this.reason = reason;
  }

  public PubManException(Throwable cause) {
    super(cause);
  }

  public PubManException(Throwable cause, Reason reason) {
    super(cause);
    this.reason = reason;
  }

  public PubManException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public Reason getReason() {
    return reason;
  }
}
