package de.mpg.mpdl.inge.service.pubman.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordGenerator;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.passay.WhitespaceRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import de.mpg.mpdl.inge.db.repository.ContextRepository;
import de.mpg.mpdl.inge.db.repository.IdentifierProviderServiceImpl;
import de.mpg.mpdl.inge.db.repository.IdentifierProviderServiceImpl.ID_PREFIX;
import de.mpg.mpdl.inge.db.repository.OrganizationRepository;
import de.mpg.mpdl.inge.db.repository.UserAccountRepository;
import de.mpg.mpdl.inge.db.repository.UserLoginRepository;
import de.mpg.mpdl.inge.es.dao.GenericDaoEs;
import de.mpg.mpdl.inge.es.dao.UserAccountDaoEs;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.AffiliationDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.util.EntityTransformer;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO.PredefinedRoles;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.aa.Principal;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.ReindexListener;
import de.mpg.mpdl.inge.service.pubman.UserAccountService;
import de.mpg.mpdl.inge.service.util.UserAccountLoginAttemptsCacheUtil;
import de.mpg.mpdl.inge.util.PropertyReader;

@Service
public class UserAccountServiceImpl extends GenericServiceImpl<AccountUserDbVO, String> implements UserAccountService, ReindexListener {

  private static final Logger logger = Logger.getLogger(UserAccountServiceImpl.class);

  private static final int TOKEN_MAX_AGE_HOURS = 24;

  public static String INDEX_MODIFICATION_DATE = "lastModificationDate";
  public static String INDEX_NAME = "name";
  public static String INDEX_LOGINNAME = "loginname";
  public static String INDEX_OBJECTID = "objectId";
  public static String INDEX_AFFIlIATION_OBJECTID = "affiliation.objectId";

  @Autowired
  private AuthorizationService aaService;

  @Autowired
  private IdentifierProviderServiceImpl idProviderService;

  @Autowired
  private UserAccountRepository userAccountRepository;

  @Autowired
  private UserLoginRepository userLoginRepository;

  @Autowired
  private UserAccountDaoEs userAccountDao;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private ContextRepository contextRepository;

  @Autowired
  private OrganizationRepository organizationRepository;

  @Autowired
  private UserAccountLoginAttemptsCacheUtil loginAttemptsCache;

  private Algorithm jwtAlgorithmKey;

  private String jwtIssuer;

  private JWTVerifier jwtVerifier;

  // private static final String PASSWORD_REGEX =
  // "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
  // private static final String PASSWORD_REGEX =
  // "^(?=.*[A-Za-z0-9])(?=\\S+$).{6,}$";
  // private static final String PASSWORD_REGEX =
  // "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\.:@$!%*?&])[A-Za-z\\d\\.:@$!%*?&]{8,}$";

  /**
   * Loginname must consist of at least 4 characters of a-z, A-Z, 0-9, @, _, -, .
   */
  private static final String LOGINNAME_REGEX = "^[A-Za-z0-9@_\\-\\.]{4,}$";

  public UserAccountServiceImpl() throws Exception {
    String key = PropertyReader.getProperty(PropertyReader.INGE_JWT_SHARED_SECRET);
    if (key == null || key.trim().isEmpty()) {
      logger.warn("No 'inge.jwt.shared-secret' is set. Generating a random secret, which might not be secure.");
      key = UUID.randomUUID().toString();
    }
    key = Base64.getEncoder().encodeToString(key.getBytes());
    jwtAlgorithmKey = Algorithm.HMAC512(key);

    jwtIssuer = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_URL);

    jwtVerifier = JWT.require(jwtAlgorithmKey).withIssuer(jwtIssuer).build();

  }

  @Transactional(rollbackFor = Throwable.class)
  @Override
  public AccountUserDbVO create(AccountUserDbVO givenUser, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    AccountUserDbVO accountUser = super.create(givenUser, authenticationToken);
    validatePassword(givenUser.getPassword());
    try {
      userLoginRepository.insertLogin(accountUser.getLoginname(), passwordEncoder.encode(givenUser.getPassword()), LocalDate.now(), false);
    } catch (DataAccessException e) {
      handleDBException(e);
    }
    if (givenUser.getGrantList() != null && !givenUser.getGrantList().isEmpty()) {
      accountUser = this.addGrants(accountUser.getObjectId(), accountUser.getLastModificationDate(),
          givenUser.getGrantList().toArray(new GrantVO[] {}), authenticationToken);
    }

    return accountUser;
  }

  @Transactional(rollbackFor = Throwable.class)
  @Override
  public void delete(String userId, String authenticationToken)
      throws IngeTechnicalException, IngeApplicationException, AuthenticationException, AuthorizationException {
    AccountUserDbVO accountUserDbVO = userAccountRepository.findById(userId).orElse(null);

    try {
      userLoginRepository.removeLogin(accountUserDbVO.getLoginname());
    } catch (DataAccessException e) {
      handleDBException(e);
    }

    super.delete(userId, authenticationToken);
  }

  @Transactional(rollbackFor = Throwable.class)
  @Override
  public AccountUserDbVO changePassword(String userId, Date modificationDate, String newPassword, boolean passwordChangeFlag,
      String authenticationToken) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    Principal principal = aaService.checkLoginRequired(authenticationToken);
    validatePassword(newPassword);
    AccountUserDbVO userDbToUpdated = userAccountRepository.findById(userId).orElse(null);

    if (userDbToUpdated == null) {
      throw new IngeApplicationException("Object with given id not found.");
    }

    checkEqualModificationDate(modificationDate, getModificationDate(userDbToUpdated));

    checkAa("changePassword", principal, userDbToUpdated);
    userLoginRepository.updateLogin(userDbToUpdated.getLoginname(), passwordEncoder.encode(newPassword), LocalDate.now(),
        passwordChangeFlag);

    updateWithTechnicalMetadata(userDbToUpdated, principal.getUserAccount(), false);

    try {
      userDbToUpdated = getDbRepository().saveAndFlush(userDbToUpdated);
    } catch (DataAccessException e) {
      handleDBException(e);
    }

    getElasticDao().createImmediately(userDbToUpdated.getObjectId(), userDbToUpdated);
    return userDbToUpdated;

  }

  @Transactional(rollbackFor = Throwable.class)
  public AccountUserDbVO addGrants(String userId, Date modificationDate, GrantVO[] grants, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    Principal principal = aaService.checkLoginRequired(authenticationToken);
    AccountUserDbVO objectToBeUpdated = getDbRepository().findById(userId).orElse(null);
    if (objectToBeUpdated == null) {
      throw new IngeApplicationException("Object with given id not found.");
    }

    checkEqualModificationDate(modificationDate, getModificationDate(objectToBeUpdated));

    for (GrantVO grantToBeAdded : grants) {

      for (GrantVO existingGrant : objectToBeUpdated.getGrantList()) {
        if (Objects.equals(grantToBeAdded.getRole(), existingGrant.getRole())
            && Objects.equals(grantToBeAdded.getObjectRef(), existingGrant.getObjectRef())) {
          throw new IngeApplicationException("Grant with given value [role=" + grantToBeAdded.getRole() + ", objectRef= "
              + grantToBeAdded.getObjectRef() + "] already exists in user account " + objectToBeUpdated.getObjectId());
        }
      }

      grantToBeAdded.setGrantedTo(null);
      grantToBeAdded.setGrantType(null);
      grantToBeAdded.setReference(null);
      grantToBeAdded.setLastModificationDate(null);

      Object referencedObject = null;

      if (grantToBeAdded.getObjectRef() != null) {
        if (grantToBeAdded.getObjectRef().startsWith(ID_PREFIX.CONTEXT.getPrefix())) {
          ContextDbVO referencedContext = contextRepository.findById(grantToBeAdded.getObjectRef()).orElse(null);
          if (referencedContext != null) {
            referencedObject = EntityTransformer.transformToOld(referencedContext);
          }
        } else if (grantToBeAdded.getObjectRef().startsWith(ID_PREFIX.OU.getPrefix())) {
          AffiliationDbVO referencedOu = organizationRepository.findById(grantToBeAdded.getObjectRef()).orElse(null);
          if (referencedOu != null) {
            referencedObject = EntityTransformer.transformToOld(referencedOu);
          }
        }

        if (referencedObject == null) {
          throw new IngeApplicationException("Unknown identifier reference: " + grantToBeAdded.getObjectRef());
        }
      }

      checkAa("addGrants", principal, objectToBeUpdated, grantToBeAdded, referencedObject);

    }
    objectToBeUpdated.getGrantList().addAll(Arrays.asList(grants));
    updateWithTechnicalMetadata(objectToBeUpdated, principal.getUserAccount(), false);

    try {
      objectToBeUpdated = getDbRepository().saveAndFlush(objectToBeUpdated);
    } catch (DataAccessException e) {
      handleDBException(e);
    }
    getElasticDao().createImmediately(objectToBeUpdated.getObjectId(), objectToBeUpdated);

    return objectToBeUpdated;

  }

  @Transactional(rollbackFor = Throwable.class)
  public AccountUserDbVO removeGrants(String userId, Date modificationDate, GrantVO[] grants, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    Principal principal = aaService.checkLoginRequired(authenticationToken);
    AccountUserDbVO objectToBeUpdated = getDbRepository().findById(userId).orElse(null);
    if (objectToBeUpdated == null) {
      throw new IngeApplicationException("Object with given id not found.");
    }

    checkEqualModificationDate(modificationDate, getModificationDate(objectToBeUpdated));

    for (GrantVO givenGrant : grants) {
      GrantVO grantToBeRemoved = null;
      for (GrantVO existingGrant : objectToBeUpdated.getGrantList()) {
        if (Objects.equals(givenGrant.getRole(), existingGrant.getRole())
            && Objects.equals(givenGrant.getObjectRef(), existingGrant.getObjectRef())) {
          grantToBeRemoved = existingGrant;
        }
      }

      if (grantToBeRemoved == null) {
        throw new IngeApplicationException("Grant with given values [role=" + givenGrant.getRole() + ", objectRef= "
            + givenGrant.getObjectRef() + "] does not exist in user account " + objectToBeUpdated.getObjectId());
      }

      checkAa("removeGrants", principal, objectToBeUpdated, givenGrant);
      objectToBeUpdated.getGrantList().remove(grantToBeRemoved);
    }
    updateWithTechnicalMetadata(objectToBeUpdated, principal.getUserAccount(), false);

    try {
      objectToBeUpdated = getDbRepository().saveAndFlush(objectToBeUpdated);
    } catch (DataAccessException e) {
      handleDBException(e);
    }
    getElasticDao().createImmediately(objectToBeUpdated.getObjectId(), objectToBeUpdated);

    return objectToBeUpdated;

  }

  @Override
  public void logout(String authenticationToken, HttpServletRequest request, HttpServletResponse response)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    removeTokenCookie(request, response);

  }

  public static void removeTokenCookie(HttpServletRequest request, HttpServletResponse response) {
    // Delete cookie
    if (request != null && request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if ("inge_auth_token".equals(cookie.getName())) {
          cookie.setValue("");
          cookie.setMaxAge(0);
          cookie.setPath("/");
          response.addCookie(cookie);
        }
      }
    }
  }

  @Override
  public Principal login(String username, String password, HttpServletRequest request, HttpServletResponse response)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
      throw new AuthenticationException("Could not login, Please provide correct username and password!");
    }
    return loginUserOrAnonymous(username, password, request, response, false);
  }

  @Override
  public Principal login(String username, String password) throws IngeTechnicalException, AuthenticationException {
    if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
      throw new AuthenticationException("Could not login, Please provide correct username and password!");
    }
    return loginUserOrAnonymous(username, password, null, null, false);
  }

  public Principal login(HttpServletRequest request, HttpServletResponse response)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    return loginUserOrAnonymous(null, null, request, response, false);
  }

  public Principal loginForPasswordChange(String username, String password) throws IngeTechnicalException, AuthenticationException {
    if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
      throw new AuthenticationException("Could not login, Please provide correct username and password!");
    }
    return loginUserOrAnonymous(username, password, null, null, true);
  }

  private Principal loginUserOrAnonymous(String username, String password, HttpServletRequest request, HttpServletResponse response,
      boolean passwordChangeRequest) throws IngeTechnicalException, AuthenticationException {

    Principal principal = null;

    if (username != null) {

      // Helper to login as any user if you are sysadmin
      if (username.contains("#")) {
        String[] parts = username.split("#");
        AccountUserDbVO userAccountSysadmin = userAccountRepository.findByLoginname(parts[0]);
        String encodedPassword = userLoginRepository.findPassword(parts[0]);

        if (userAccountSysadmin != null && userAccountSysadmin.isActive() && encodedPassword != null
            && passwordEncoder.matches(password, encodedPassword)) {
          loginAttemptsCache.loginSucceeded(username);
          for (GrantVO grant : userAccountSysadmin.getGrantList()) {
            if (PredefinedRoles.SYSADMIN.frameworkValue().contentEquals(grant.getRole())) {
              AccountUserDbVO userAccountToLogin = userAccountRepository.findByLoginname(parts[1]);
              String token = createToken(userAccountToLogin, request);
              principal = new Principal(userAccountToLogin, token);
            }
          }
        }

        if (principal == null || principal.getUserAccount() == null || !principal.getUserAccount().isActive()) {
          loginAttemptsCache.loginFailed(username);
          throw new AuthenticationException("Could not login, incorrect username and password provided or user is deactivated!");
        }
      } else {
        AccountUserDbVO userAccount = userAccountRepository.findByLoginname(username);
        String encodedPassword = userLoginRepository.findPassword(username);

        if (userAccount != null && userAccount.isActive() && encodedPassword != null
            && passwordEncoder.matches(password, encodedPassword)) {
          if (loginAttemptsCache.isBlocked(username)) {
            throw new AuthenticationException(username + " is blocked for " + loginAttemptsCache.ATTEMPT_TIMER + " since last attempt");
          } else if (!userLoginRepository.findPasswordChangeFlag(username) && !passwordChangeRequest) {
            throw new AuthenticationException(username + " needs to change password first");
          }
          loginAttemptsCache.loginSucceeded(username);
          String token = createToken(userAccount, request);
          principal = new Principal(userAccount, token);
        } else {
          loginAttemptsCache.loginFailed(username);
          throw new AuthenticationException("Could not login, incorrect username and password provided or user is deactivated!");
        }
      }

      // Set Cookie
      if (principal != null && response != null) {
        Cookie cookie = new Cookie("inge_auth_token", principal.getJwToken());
        cookie.setPath("/");
        cookie.setMaxAge(TOKEN_MAX_AGE_HOURS * 3600);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
      }
    }

    // Login anonymous, ip-based user
    else if (request != null && request.getHeader("X-Forwarded-For") != null) {
      String token = createToken(null, request);
      principal = new Principal(null, token);
    } else if (request != null && request.getRemoteAddr() != null) {
      String token = createToken(null, request);
      principal = new Principal(null, token);
    }

    return principal;

  }

  @Transactional(readOnly = true)
  @Override
  public AccountUserDbVO get(String id, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    String userId = id;
    if (!id.startsWith(ID_PREFIX.USER.getPrefix())) {
      AccountUserDbVO user = userAccountRepository.findByLoginname(id);
      if (user != null) {
        userId = user.getObjectId();
      }
    }
    return super.get(userId, authenticationToken);

  }

  @Override
  public AccountUserDbVO get(String authenticationToken) throws IngeTechnicalException, AuthenticationException {
    DecodedJWT jwt = verifyToken(authenticationToken);
    String userId = jwt.getSubject();
    return userAccountRepository.findByLoginname(userId);
  }

  public DecodedJWT verifyToken(String authenticationToken) throws AuthenticationException {
    try {
      DecodedJWT jwt = jwtVerifier.verify(authenticationToken);
      return jwt;
    } catch (JWTVerificationException e) {
      throw new AuthenticationException("Could not verify token: " + e.getMessage(), e);
    }

  }

  private String createToken(AccountUserDbVO user, HttpServletRequest request) throws IngeTechnicalException {
    try {
      Instant now = Instant.now();
      Date issueDate = Date.from(now);
      Date expirationDate = Date.from(now.plus(TOKEN_MAX_AGE_HOURS, ChronoUnit.HOURS));
      logger.debug("Creating token with issue date: " + issueDate + " and expiration date " + expirationDate);

      Builder jwtBuilder = JWT.create().withIssuedAt(issueDate).withIssuer(jwtIssuer).withExpiresAt(expirationDate);

      if (user != null) {
        jwtBuilder.withClaim("id", user.getObjectId()).withSubject(user.getLoginname());
      }

      // Write ip adress as header in token
      if (request != null && request.getHeader("X-Forwarded-For") != null) {
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("ip", request.getHeader("X-Forwarded-For"));
        jwtBuilder.withHeader(headerMap);
      } else if (request != null && request.getRemoteAddr() != null) {
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("ip", request.getRemoteAddr());
        jwtBuilder.withHeader(headerMap);
      }

      return jwtBuilder.sign(jwtAlgorithmKey);
    } catch (Exception e) {
      throw new IngeTechnicalException("Could not generate token " + e.getMessage(), e);
    }

  }

  @Override
  protected AccountUserDbVO createEmptyDbObject() {
    return new AccountUserDbVO();
  }

  @Override
  protected List<String> updateObjectWithValues(AccountUserDbVO givenUser, AccountUserDbVO tobeUpdatedUser, AccountUserDbVO callingUser,
      boolean create) throws IngeApplicationException {

    if (givenUser.getName() == null || givenUser.getName().trim().isEmpty() || givenUser.getLoginname() == null
        || givenUser.getLoginname().trim().isEmpty()) {
      throw new IngeApplicationException("A name and user id is required");
    }

    validateLoginname(givenUser.getLoginname());

    //    if (create) {
    //      tobeUpdatedUser.setActive(true);
    //    }

    tobeUpdatedUser.setActive(givenUser.isActive());
    tobeUpdatedUser.setAffiliation(givenUser.getAffiliation());
    tobeUpdatedUser.setEmail(givenUser.getEmail());
    tobeUpdatedUser.setLoginname(givenUser.getLoginname());
    tobeUpdatedUser.setName(givenUser.getName());

    if (create) {
      tobeUpdatedUser.setObjectId(idProviderService.getNewId(ID_PREFIX.USER));
    }
    return null;

  }

  @Override
  protected JpaRepository<AccountUserDbVO, String> getDbRepository() {
    return userAccountRepository;
  }

  @Override
  protected GenericDaoEs<AccountUserDbVO> getElasticDao() {
    return userAccountDao;
  }

  @Override
  protected String getObjectId(AccountUserDbVO object) {
    return object.getObjectId();
  }

  // private void validatePassword(String password) throws
  // IngeApplicationException {
  // if (password == null || password.trim().isEmpty()) {
  // throw new IngeApplicationException("A password has to be provided");
  // } else if (!password.matches(PASSWORD_REGEX)) {
  // throw new IngeApplicationException(
  // "Password must consist of at least 8 characters, no whitespaces and contain
  // at least one upper case letter, one lower case letter, a number and a special
  // character");
  // }
  // }

  private void validateLoginname(String loginname) throws IngeApplicationException {
    if (loginname == null || loginname.trim().isEmpty()) {
      throw new IngeApplicationException("A loginname (userId) has to be provided");
    } else if (!loginname.matches(LOGINNAME_REGEX)) {
      throw new IngeApplicationException(
          "Invalid loginname (userId). Loginname  must consist of an email adress or at least 4 characters, no whitespaces, no special characters");
    }

  }

  @Override
  protected Date getModificationDate(AccountUserDbVO object) {
    return object.getLastModificationDate();
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public AccountUserDbVO activate(String id, Date modificationDate, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    return changeState(id, modificationDate, authenticationToken, true);
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public AccountUserDbVO deactivate(String id, Date modificationDate, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    return changeState(id, modificationDate, authenticationToken, false);
  }

  private AccountUserDbVO changeState(String id, Date modificationDate, String authenticationToken, boolean active)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    Principal principal = aaService.checkLoginRequired(authenticationToken);
    AccountUserDbVO accountToBeUpdated = userAccountRepository.findById(id).orElse(null);
    if (accountToBeUpdated == null) {
      throw new IngeTechnicalException("User account with given id " + id + " not found.");
    }

    if (accountToBeUpdated.isActive() == active) {
      throw new IngeApplicationException("Account [" + accountToBeUpdated.getObjectId() + "] is already in state " + active);
    }

    checkEqualModificationDate(modificationDate, getModificationDate(accountToBeUpdated));

    checkAa((active ? "activate" : "deactivate"), principal, accountToBeUpdated);

    accountToBeUpdated.setActive(active);
    updateWithTechnicalMetadata(accountToBeUpdated, principal.getUserAccount(), false);

    try {
      accountToBeUpdated = userAccountRepository.saveAndFlush(accountToBeUpdated);
    } catch (DataAccessException e) {
      handleDBException(e);
    }

    userAccountDao.createImmediately(accountToBeUpdated.getObjectId(), accountToBeUpdated);
    return accountToBeUpdated;
  }

  @Override
  @JmsListener(containerFactory = "queueContainerFactory", destination = "reindex-AccountUserDbVO")
  public void reindexListener(String id) throws IngeTechnicalException {
    reindex(id, false);

  }

  private void validatePassword(String password) throws IngeApplicationException {
    PasswordValidator validator = new PasswordValidator(
        // length between 8 and 32 characters
        new LengthRule(8, 32),
        // at least one upper-case character
        new CharacterRule(EnglishCharacterData.UpperCase, 1),
        // at least one lower-case character
        new CharacterRule(EnglishCharacterData.LowerCase, 1),
        // at least one digit character
        new CharacterRule(EnglishCharacterData.Digit, 1),
        // at least one symbol (special character)
        new CharacterRule(EnglishCharacterData.Special, 1),
        // no whitespace
        new WhitespaceRule());

    RuleResult result = validator.validate(new PasswordData(new String(password)));
    if (!result.isValid()) {
      StringBuilder sb = new StringBuilder();
      for (String msg : validator.getMessages(result)) {
        sb.append(msg);
        sb.append("\n");
      }

      throw new IngeApplicationException(
          "Password must have a minimum length of 8 characters, no whitespaces allowed, at least one upper case letter, one lower case letter, a number and a special character");
    }
  }

  public String generateRandomPassword() {
    List<CharacterRule> rules = Arrays.asList(
        // at least one upper-case character
        new CharacterRule(EnglishCharacterData.UpperCase, 4),
        // at least one lower-case character
        new CharacterRule(EnglishCharacterData.LowerCase, 4),
        // at least one digit character
        new CharacterRule(EnglishCharacterData.Digit, 3),
        // at least one special character
        new CharacterRule(new CharacterData() {
          @Override
          public String getErrorCode() {
            return "ERR_SPACE";
          }

          @Override
          public String getCharacters() {
            return "!\\\"#$%&'()*+,-./:;<=>?@[\\\\]^_`{|}~";
          }
        }, 1));
    PasswordGenerator generator = new PasswordGenerator();

    // Generated password is 12 characters long, which complies with policy
    return generator.generatePassword(12, rules);
  }
}
