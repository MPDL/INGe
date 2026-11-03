package de.mpg.mpdl.inge.rest.web.controller;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

import org.junit.Test;
import org.springframework.http.ResponseEntity;

import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.rest.web.model.IpAccountUserDbVO;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.pubman.UserAccountService;

import jakarta.persistence.Transient;

/**
 * Tests the read-only information exposed by the login endpoints.
 */
public class LoginRestControllerReadOnlyTest {

  @Test
  public void whoExposesGlobalReadOnlyMode() throws Exception {
    LoginRestController controller =
        new LoginRestController(userAccountServiceReturning(user("user_1")), null, new StubAuthorizationService(true));

    ResponseEntity<AccountUserDbVO> response = controller.getUser(null, null);

    assertNotNull(response.getBody());
    assertTrue(response.getBody() instanceof IpAccountUserDbVO);
    assertTrue(((IpAccountUserDbVO) response.getBody()).isReadOnlyMode());
  }

  @Test
  public void whoDoesNotReportReadOnlyModeWhenDisabled() throws Exception {
    LoginRestController controller =
        new LoginRestController(userAccountServiceReturning(user("user_1")), null, new StubAuthorizationService(false));

    ResponseEntity<AccountUserDbVO> response = controller.getUser(null, null);

    assertNotNull(response.getBody());
    assertFalse(((IpAccountUserDbVO) response.getBody()).isReadOnlyMode());
  }

  @Test
  public void readOnlyModeIsNeverPersisted() throws Exception {
    Field field = IpAccountUserDbVO.class.getDeclaredField("readOnlyMode");
    assertNotNull(field.getAnnotation(Transient.class));
  }

  private static AccountUserDbVO user(String objectId) {
    AccountUserDbVO user = new AccountUserDbVO();
    user.setObjectId(objectId);
    return user;
  }

  private static UserAccountService userAccountServiceReturning(AccountUserDbVO user) {
    return (UserAccountService) Proxy.newProxyInstance(UserAccountService.class.getClassLoader(), new Class<?>[] {UserAccountService.class},
        (proxy, method, args) -> {
          if ("get".equals(method.getName()) && 1 == method.getParameterCount()) {
            return user;
          }
          throw new UnsupportedOperationException(method.getName());
        });
  }

  private static final class StubAuthorizationService extends AuthorizationService {
    private final boolean readOnlyMode;

    private StubAuthorizationService(boolean readOnlyMode) {
      this.readOnlyMode = readOnlyMode;
    }

    @Override
    public boolean isReadOnlyMode() {
      return this.readOnlyMode;
    }
  }
}
