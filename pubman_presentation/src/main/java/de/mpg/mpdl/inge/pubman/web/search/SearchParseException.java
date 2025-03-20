package de.mpg.mpdl.inge.pubman.web.search;

@SuppressWarnings("serial")
public class SearchParseException extends Exception {


  public SearchParseException() {}

  public SearchParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public SearchParseException(String message, Throwable cause) {
    super(message, cause);
  }

  public SearchParseException(Throwable cause) {
    super(cause);
  }

  public SearchParseException(String msg) {
    super(msg);
  }
}
