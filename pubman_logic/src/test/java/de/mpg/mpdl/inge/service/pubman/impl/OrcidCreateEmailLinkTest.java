package de.mpg.mpdl.inge.service.pubman.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.mpg.mpdl.inge.util.PropertyReader;
import java.net.URI;
import java.util.UUID;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for testing {@link OrcidServiceImpl#createEmailLink(String, String)}.
 */
public class OrcidCreateEmailLinkTest {

  private OrcidServiceImpl orcidService;

  private static final String TEST_ORCID_URL = "https://sandbox.orcid.org";
  private static final String TEST_ORCID_AUTHORIZE_URL = "/oauth/authorize";
  private static final String TEST_ORCID_SCOPE = "/read-limited%20/person/update%20/activities/update%20/authenticate";
  private static final String TEST_ORCID_RESPONSE_TYPE = "code";
  private static final String TEST_ORCID_REDIRECT_URL_REST = "https://qa.pure.mpdl.mpg.de/rest";
  private static final String TEST_ORCID_REDIRECT_URL_URL = "/orcid/createOrcidAuthentication";
  private static final String TEST_ORCID_CLIENT_ID = "APP-Z8FEHSJVQHMWCTA3";

  @Before
  public void setUp() {
    System.setProperty(PropertyReader.ORCID_URL, TEST_ORCID_URL);
    System.setProperty(PropertyReader.ORCID_AUTHORIZE_URL, TEST_ORCID_AUTHORIZE_URL);
    System.setProperty(PropertyReader.ORCID_SCOPE, TEST_ORCID_SCOPE);
    System.setProperty(PropertyReader.ORCID_RESPONSE_TYPE, TEST_ORCID_RESPONSE_TYPE);
    System.setProperty(PropertyReader.ORCID_REDIRECT_URL_REST, TEST_ORCID_REDIRECT_URL_REST);
    System.setProperty(PropertyReader.ORCID_REDIRECT_URL_URL, TEST_ORCID_REDIRECT_URL_URL);
    System.setProperty(PropertyReader.ORCID_CLIENT_ID, TEST_ORCID_CLIENT_ID);

    orcidService = new OrcidServiceImpl(null, null, null);
  }

  @After
  public void tearDown() {
    System.clearProperty(PropertyReader.ORCID_URL);
    System.clearProperty(PropertyReader.ORCID_AUTHORIZE_URL);
    System.clearProperty(PropertyReader.ORCID_SCOPE);
    System.clearProperty(PropertyReader.ORCID_RESPONSE_TYPE);
    System.clearProperty(PropertyReader.ORCID_REDIRECT_URL_REST);
    System.clearProperty(PropertyReader.ORCID_REDIRECT_URL_URL);
    System.clearProperty(PropertyReader.ORCID_CLIENT_ID);
  }

  @Test
  public void testCreateEmailLinkFlow() {
    String scopeRequested = PropertyReader.getProperty(PropertyReader.ORCID_SCOPE);
    String genSecret = orcidService.generateSecret();
    String redirectUri = PropertyReader.getProperty(PropertyReader.ORCID_REDIRECT_URL_REST)
        + PropertyReader.getProperty(PropertyReader.ORCID_REDIRECT_URL_URL) + "?secret=" + genSecret;

    String emailLink = orcidService.createEmailLink(scopeRequested, redirectUri);
    System.out.println(emailLink);

    assertNotNull(emailLink);
    // Verify valid HTTP URI
    URI uri = URI.create(emailLink);
    assertNotNull(uri);
    assertEquals("https", uri.getScheme());
    assertEquals("sandbox.orcid.org", uri.getHost());

    assertTrue(emailLink.startsWith(TEST_ORCID_URL + TEST_ORCID_AUTHORIZE_URL + "?"));
    assertTrue(emailLink.contains("scope=" + TEST_ORCID_SCOPE));
    assertTrue(emailLink.contains("response_type=" + TEST_ORCID_RESPONSE_TYPE));
    assertTrue(emailLink.contains("redirect_uri=" + redirectUri));
    assertTrue(emailLink.contains("client_id=" + TEST_ORCID_CLIENT_ID));

    String expectedLink = TEST_ORCID_URL + TEST_ORCID_AUTHORIZE_URL + "?" + "scope=" + TEST_ORCID_SCOPE + "&response_type="
        + TEST_ORCID_RESPONSE_TYPE + "&redirect_uri=" + redirectUri + "&client_id=" + TEST_ORCID_CLIENT_ID;

    assertEquals(expectedLink, emailLink);
  }

  @Test
  public void testCreateEmailLinkWithUnencodedScope() {
    String scopeWithSpaces = "/read-limited /person/update /activities/update /authenticate";
    String redirectUri = "https://qa.pure.mpdl.mpg.de/rest/orcid/createOrcidAuthentication?secret=test-secret";

    String emailLink = orcidService.createEmailLink(scopeWithSpaces, redirectUri);
    System.out.println(emailLink);

    assertNotNull(emailLink);
    // Verify valid HTTP URI without spaces
    URI uri = URI.create(emailLink);
    assertNotNull(uri);

    String expectedScope = "/read-limited%20/person/update%20/activities/update%20/authenticate";
    assertTrue(emailLink.contains("scope=" + expectedScope));
  }

  @Test
  public void testGenerateSecret() {
    String secret = orcidService.generateSecret();
    System.out.println(secret);

    assertNotNull(secret);
    // Verify valid UUID
    UUID uuid = UUID.fromString(secret);
    assertNotNull(uuid);
  }
}
