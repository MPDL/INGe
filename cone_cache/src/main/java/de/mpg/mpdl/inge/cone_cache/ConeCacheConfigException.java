package de.mpg.mpdl.inge.cone_cache;

@SuppressWarnings("serial")
public class ConeCacheConfigException extends Exception {

  public ConeCacheConfigException() {}

  public ConeCacheConfigException(String message) {
    super(message);
  }

  public ConeCacheConfigException(Throwable cause) {
    super(cause);
  }

  public ConeCacheConfigException(String message, Throwable cause) {
    super(message, cause);
  }
}
