package de.mpg.mpdl.inge.rest.web.exceptions;

import static org.junit.Assert.assertEquals;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import de.mpg.mpdl.inge.model.exception.PubManException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;

/**
 * Tests that read-only write attempts are reported as {@code 403 Forbidden} with
 * {@code reason=READ_ONLY}.
 */
public class ReadOnlyErrorResponseTest {

  @Test
  public void readOnlyAuthorizationExceptionMapsToForbiddenWithReason() {
    AuthorizationException exception =
        new AuthorizationException("The system is in read-only maintenance mode.", PubManException.Reason.READ_ONLY);

    Map<String, Object> response = new LinkedHashMap<>();
    PubmanRestExceptionHandler.buildExceptionMessage(exception, response, HttpStatus.FORBIDDEN, null);

    assertEquals(HttpStatus.FORBIDDEN.value(), response.get("status"));
    assertEquals(PubManException.Reason.READ_ONLY.name(), response.get("reason"));
  }
}
