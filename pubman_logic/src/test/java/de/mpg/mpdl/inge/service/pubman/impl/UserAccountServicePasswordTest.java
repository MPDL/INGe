package de.mpg.mpdl.inge.service.pubman.impl;

import static org.junit.Assert.*;

import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import java.lang.reflect.Method;

public class UserAccountServicePasswordTest {

  private static final Logger logger = LogManager.getLogger(UserAccountServicePasswordTest.class);

  private UserAccountServiceImpl userAccountService;

  @Before
  public void setUp() throws Exception {
    try {
      userAccountService = new UserAccountServiceImpl();
    } catch (Exception e) {
      // constructor might fail but field level initialization remains
    }
    if (userAccountService == null) {
      userAccountService = createServiceManually();
    }
  }

  private void invokeValidatePassword(String password) throws Exception {
    Method method = UserAccountServiceImpl.class.getDeclaredMethod("validatePassword", String.class);
    method.setAccessible(true);
    try {
      method.invoke(userAccountService, password);
    } catch (java.lang.reflect.InvocationTargetException e) {
      if (e.getCause() instanceof Exception) {
        throw (Exception) e.getCause();
      }
      throw e;
    }
  }

  @Test
  public void testGenerateRandomPassword() throws Exception {
    for (int i = 0; i < 100; i++) {
      String password = userAccountService.generateRandomPassword();
      logger.info("Generated password {}: {}", i + 1, password);
      assertNotNull(password);
      assertEquals(12, password.length());
      // Should be validated by validatePassword
      try {
        invokeValidatePassword(password);
      } catch (IngeApplicationException e) {
        fail("Generated password '" + password + "' failed validation: " + e.getMessage());
      }
    }
  }

  private UserAccountServiceImpl createServiceManually() throws Exception {
    return new UserAccountServiceImpl() {};
  }

  @Test
  public void testValidatePasswordSuccess() throws Exception {
    invokeValidatePassword("Abcde123!");
    invokeValidatePassword("Password123#");
  }

  @Test(expected = IngeApplicationException.class)
  public void testValidatePasswordTooShort() throws Exception {
    invokeValidatePassword("Ab1!");
  }

  @Test(expected = IngeApplicationException.class)
  public void testValidatePasswordNoUpper() throws Exception {
    invokeValidatePassword("abcde123!");
  }

  @Test(expected = IngeApplicationException.class)
  public void testValidatePasswordNoLower() throws Exception {
    invokeValidatePassword("ABCDE123!");
  }

  @Test(expected = IngeApplicationException.class)
  public void testValidatePasswordNoDigit() throws Exception {
    invokeValidatePassword("Abcdefgh!");
  }

  @Test(expected = IngeApplicationException.class)
  public void testValidatePasswordNoSpecial() throws Exception {
    invokeValidatePassword("Abcdefg1");
  }

  @Test(expected = IngeApplicationException.class)
  public void testValidatePasswordWithColon() throws Exception {
    invokeValidatePassword("Abcde123:");
  }

  @Test(expected = IngeApplicationException.class)
  public void testValidatePasswordWithWhitespace() throws Exception {
    invokeValidatePassword("Abcde 123!");
  }
}
