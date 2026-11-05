package de.mpg.mpdl.inge.rest.web.controller;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.aa.Principal;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;

/**
 * Tests that the OAI initialisation endpoint is reserved for SYSADMIN.
 */
public class OaiRestControllerAuthorizationTest {

  @Test
  public void initRequiresSysAdminRole() throws Exception {
    OaiRestController controller = new OaiRestController();
    RecordingAuthorizationService aaService = new RecordingAuthorizationService();
    controller.authorizationService = aaService;

    try {
      controller.init("token", 1);
      fail("Expected an AuthenticationException");
    } catch (AuthenticationException e) {
      assertArrayEquals(new String[] {"SYSADMIN"}, aaService.roles);
    }
  }

  private static final class RecordingAuthorizationService extends AuthorizationService {
    private String[] roles;

    @Override
    public Principal checkLoginRequiredWithRole(String authenticationToken, String... roles) throws AuthenticationException {
      this.roles = roles;
      throw new AuthenticationException("Authentication as admin user required");
    }
  }
}
