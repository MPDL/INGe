package de.mpg.mpdl.inge.util;

import org.junit.Test;

public class RedirectValidatorTest {

  @Test
  public void testValidateRelative() {
    RedirectValidator.validate("/cone/");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testValidateExternal() {
    RedirectValidator.validate("http://evil.com/malicious");
  }

  @Test
  public void testValidateLocalhost() {
    RedirectValidator.validate("http://localhost:8080/app");
  }

  @Test
  public void testValidateLocalhostWithoutPort() {
    RedirectValidator.validate("http://localhost/app");
  }

  @Test
  public void testValidateLoopbackIp() {
    RedirectValidator.validate("http://127.0.0.1:8080/app");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testValidateProtocolRelative() {
    RedirectValidator.validate("//evil.com");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testValidateBackslash() {
    // Manche Browser interpretieren \ als /
    RedirectValidator.validate("\\/evil.com");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testValidateUserInfo() {
    // Verhindere http://localhost@evil.com
    RedirectValidator.validate("http://localhost@evil.com");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testValidateJavascript() {
    RedirectValidator.validate("javascript:alert(1)");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testValidateControlCharacters() {
    // CRLF Injection Versuch
    RedirectValidator.validate("/home\r\nLocation: http://evil.com");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testValidateEncodedBackslash() {
    // Manche Filter werden durch URL-Encoding umgangen
    RedirectValidator.validate("/%5cevil.com");
  }
}
