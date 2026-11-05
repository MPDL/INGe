package de.mpg.mpdl.inge.service.aa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.AffiliationDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.model.exception.PubManException;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * Tests the global read-only maintenance mode guard in {@link AuthorizationService}.
 */
public class AuthorizationServiceReadOnlyTest {

  private static final String PUB_ITEM_SERVICE = "de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl";
  private static final String CONTEXT_SERVICE = "de.mpg.mpdl.inge.service.pubman.impl.ContextServiceDbImpl";
  private static final String SAVED_SEARCH_SERVICE = "de.mpg.mpdl.inge.service.pubman.impl.SavedSearchServiceImpl";
  private static final String USER_SERVICE = "de.mpg.mpdl.inge.service.pubman.impl.UserAccountServiceImpl";

  private AuthorizationService aaService;

  @Before
  public void setUp() {
    System.clearProperty(PropertyReader.INGE_READONLY_MODE);
    this.aaService = new AuthorizationService();
  }

  @After
  public void tearDown() {
    System.clearProperty(PropertyReader.INGE_READONLY_MODE);
  }

  private void setReadOnly(boolean readOnly) {
    System.setProperty(PropertyReader.INGE_READONLY_MODE, Boolean.toString(readOnly));
  }

  private AccountUserDbVO user(String objectId, GrantVO.PredefinedRoles role, String objectRef) {
    AccountUserDbVO user = new AccountUserDbVO();
    user.setObjectId(objectId);
    if (null != role) {
      user.getGrantList().add(new GrantVO(role.frameworkValue(), objectRef));
    }
    return user;
  }

  private Principal principal(AccountUserDbVO user) {
    return new Principal(user, "token");
  }

  @Test
  public void readOnlyModeIsReadFromProperty() {
    assertFalse(this.aaService.isReadOnlyMode());

    setReadOnly(true);
    assertTrue(this.aaService.isReadOnlyMode());
  }

  @Test
  public void nonSysAdminWriteIsRejectedWithReadOnlyReason() {
    setReadOnly(true);
    Principal user = principal(user("user_1", null, null));

    try {
      this.aaService.checkAuthorization(PUB_ITEM_SERVICE, "create", user);
      fail("Expected an AuthorizationException");
    } catch (Exception e) {
      assertTrue(e instanceof AuthorizationException);
      assertEquals(PubManException.Reason.READ_ONLY, ((AuthorizationException) e).getReason());
    }
  }

  @Test
  public void writeWithoutReadOnlyModeUsesNormalAuthorization() {
    Principal user = principal(user("user_1", null, null));

    try {
      this.aaService.checkAuthorization(PUB_ITEM_SERVICE, "create", user);
      fail("Expected an AuthorizationException");
    } catch (Exception e) {
      assertTrue(e instanceof AuthorizationException);
      assertNotEquals(PubManException.Reason.READ_ONLY, ((AuthorizationException) e).getReason());
    }
  }

  @Test
  public void sysAdminWriteIsAllowedInReadOnlyMode() throws Exception {
    setReadOnly(true);
    Principal sysAdmin = principal(user("user_admin", GrantVO.PredefinedRoles.SYSADMIN, null));

    this.aaService.checkAuthorization(PUB_ITEM_SERVICE, "create", sysAdmin);
  }

  @Test
  public void localAdminWriteIsRejectedInReadOnlyMode() throws Exception {
    ContextDbVO context = new ContextDbVO();
    AffiliationDbRO affiliation = new AffiliationDbRO();
    affiliation.setObjectId("ctx_1");
    context.setResponsibleAffiliations(new ArrayList<>(List.of(affiliation)));

    Principal localAdmin = principal(user("user_local", GrantVO.PredefinedRoles.LOCAL_ADMIN, "ctx_1"));

    // Without read-only mode the LOCAL_ADMIN rule authorizes the write.
    this.aaService.checkAuthorization(CONTEXT_SERVICE, "create", localAdmin, context);

    setReadOnly(true);
    try {
      this.aaService.checkAuthorization(CONTEXT_SERVICE, "create", localAdmin, context);
      fail("Expected an AuthorizationException");
    } catch (AuthorizationException e) {
      assertEquals(PubManException.Reason.READ_ONLY, e.getReason());
    }
  }

  @Test
  public void readMethodsRemainAllowedInReadOnlyMode() throws Exception {
    setReadOnly(true);

    Principal moderator = principal(user("user_mod", GrantVO.PredefinedRoles.MODERATOR, null));
    this.aaService.checkAuthorization(SAVED_SEARCH_SERVICE, "getAll", moderator);
    this.aaService.checkAuthorization(SAVED_SEARCH_SERVICE, "get", (Object) null);
  }

  @Test
  public void checkWritableAllowsSysAdminAndRejectsAnonymous() throws Exception {
    setReadOnly(true);

    this.aaService.checkWritable(principal(user("user_admin", GrantVO.PredefinedRoles.SYSADMIN, null)));

    try {
      this.aaService.checkWritable((Principal) null);
      fail("Expected an AuthorizationException");
    } catch (AuthorizationException e) {
      assertEquals(PubManException.Reason.READ_ONLY, e.getReason());
    }
  }
}
