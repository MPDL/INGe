package de.mpg.mpdl.inge.util;

import org.junit.Test;
import static org.junit.Assert.*;

public class UrlValidatorTest {

    @Test
    public void testValidateRelativeUrl() {
        // This should not throw an exception if we want to support relative URLs like /cone/
        UrlValidator.validate("/cone/");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateInvalidUrl() {
        UrlValidator.validate("http://malicious.com");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateProtocolRelativeUrl() {
        // Protocol-relative URLs (//example.com) should NOT be allowed as they are unsafe
        UrlValidator.validate("//malicious.com");
    }

    @Test
    public void testValidateAbsoluteUrl() {
        // Assuming localhost is always allowed or needs to be in properties.
        // The current implementation allows localhost.
        UrlValidator.validate("http://localhost:8080/cone/");
    }
}
