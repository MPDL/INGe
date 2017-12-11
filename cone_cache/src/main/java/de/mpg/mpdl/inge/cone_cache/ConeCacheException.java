package de.mpg.mpdl.inge.cone_cache;

@SuppressWarnings("serial")
public class ConeCacheException extends Exception {

  public ConeCacheException() {}

  public ConeCacheException(String message) {
    super(message);
  }

  public ConeCacheException(Throwable cause) {
    super(cause);
  }

  public ConeCacheException(String message, Throwable cause) {
    super(message, cause);
  }

}
