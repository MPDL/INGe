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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import de.mpg.mpdl.inge.db.repository.ContextRepository;
import de.mpg.mpdl.inge.db.repository.IdentifierProviderServiceImpl;
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
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.aa.Principal;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.ReindexListener;
import de.mpg.mpdl.inge.service.pubman.UserAccountService;
import de.mpg.mpdl.inge.service.util.UserAccountLoginAttemptsCacheUtil;
import de.mpg.mpdl.inge.util.PropertyReader;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class UserAccountServiceImpl extends GenericServiceImpl<AccountUserDbVO, String> implements UserAccountService, ReindexListener {

  private static final Logger logger = LogManager.getLogger(UserAccountServiceImpl.class);

  private static final int TOKEN_MAX_AGE_HOURS = 24;

  public static final String INDEX_MODIFICATION_DATE = "lastModificationDate";
  public static final String INDEX_NAME = "name";
  public static final String INDEX_LOGINNAME = "loginname";
  public static final String INDEX_OBJECTID = "objectId";
  public static final String INDEX_AFFIlIATION_OBJECTID = "affiliation.objectId";

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

  private final Algorithm jwtAlgorithmKey;

  private final String jwtIssuer;

  private final JWTVerifier jwtVerifier;

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
    if (null == key || key.trim().isEmpty()) {
      logger.warn("No 'inge.jwt.shared-secret' is set. Generating a random secret, which might not be secure.");
      key = UUID.randomUUID().toString();
    }
    key = Base64.getEncoder().encodeToString(key.getBytes());
    this.jwtAlgorithmKey = Algorithm.HMAC512(key);

    this.jwtIssuer = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_URL);

    this.jwtVerifier = JWT.require(this.jwtAlgorithmKey).withIssuer(this.jwtIssuer).build();

  }

  @Transactional(rollbackFor = Throwable.class)
  @Override
  public AccountUserDbVO create(AccountUserDbVO givenUser, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    AccountUserDbVO accountUser = super.create(givenUser, authenticationToken);
    validatePassword(givenUser.getPassword());
    try {
      this.userLoginRepository.insertLogin(accountUser.getLoginname(), this.passwordEncoder.encode(givenUser.getPassword()),
          LocalDate.now(), false);
    } catch (DataAccessException e) {
      handleDBException(e);
    }
    if (null != givenUser.getGrantList() && !givenUser.getGrantList().isEmpty()) {
      accountUser = this.addGrants(accountUser.getObjectId(), accountUser.getLastModificationDate(),
          givenUser.getGrantList().toArray(new GrantVO[] {}), authenticationToken);
    }

    return accountUser;
  }

  @Transactional(rollbackFor = Throwable.class)
  @Override
  public void delete(String userId, String authenticationToken)
      throws IngeTechnicalException, IngeApplicationException, AuthenticationException, AuthorizationException {
    AccountUserDbVO accountUserDbVO = this.userAccountRepository.findById(userId).orElse(null);

    try {
      this.userLoginRepository.removeLogin(accountUserDbVO.getLoginname());
    } catch (DataAccessException e) {
      handleDBException(e);
    }

    super.delete(userId, authenticationToken);
  }

  @Transactional(rollbackFor = Throwable.class)
  @Override
  public AccountUserDbVO changePassword(String userId, Date modificationDate, String newPassword, boolean passwordChangeFlag,
      String authenticationToken) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    Principal principal = this.aaService.checkLoginRequired(authenticationToken);
    validatePassword(newPassword);
    AccountUserDbVO userDbToUpdated = this.userAccountRepository.findById(userId).orElse(null);

    if (null == userDbToUpdated) {
      throw new IngeApplicationException("Object with given id not found.");
    }

    checkEqualModificationDate(modificationDate, getModificationDate(userDbToUpdated));

    checkAa("changePassword", principal, userDbToUpdated);
    this.userLoginRepository.updateLogin(userDbToUpdated.getLoginname(), this.passwordEncoder.encode(newPassword), LocalDate.now(),
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
    Principal principal = this.aaService.checkLoginRequired(authenticationToken);
    AccountUserDbVO objectToBeUpdated = getDbRepository().findById(userId).orElse(null);
    if (null == objectToBeUpdated) {
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

      if (null != grantToBeAdded.getObjectRef()) {
        if (grantToBeAdded.getObjectRef().startsWith(IdentifierProviderServiceImpl.ID_PREFIX.CONTEXT.getPrefix())) {
          ContextDbVO referencedContext = this.contextRepository.findById(grantToBeAdded.getObjectRef()).orElse(null);
          if (null != referencedContext) {
            referencedObject = EntityTransformer.transformToOld(referencedContext);
          }
        } else if (grantToBeAdded.getObjectRef().startsWith(IdentifierProviderServiceImpl.ID_PREFIX.OU.getPrefix())) {
          AffiliationDbVO referencedOu = this.organizationRepository.findById(grantToBeAdded.getObjectRef()).orElse(null);
          if (null != referencedOu) {
            referencedObject = EntityTransformer.transformToOld(referencedOu);
          }
        }

        if (null == referencedObject) {
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
    Principal principal = this.aaService.checkLoginRequired(authenticationToken);
    AccountUserDbVO objectToBeUpdated = getDbRepository().findById(userId).orElse(null);
    if (null == objectToBeUpdated) {
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

      if (null == grantToBeRemoved) {
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
  public void logout(String authenticationToken, HttpServletRequest request, HttpServletResponse response) {

    removeTokenCookie(request, response);

  }

  public static void removeTokenCookie(HttpServletRequest request, HttpServletResponse response) {
    // Delete cookie
    if (null != request && null != request.getCookies()) {
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
      throws IngeTechnicalException, AuthenticationException {
    if (null == username || username.trim().isEmpty() || null == password || password.trim().isEmpty()) {
      throw new AuthenticationException("Could not login, Please provide correct username and password!");
    }
    return loginUserOrAnonymous(username, password, request, response, false);
  }

  @Override
  public Principal login(String username, String password) throws IngeTechnicalException, AuthenticationException {
    if (null == username || username.trim().isEmpty() || null == password || password.trim().isEmpty()) {
      throw new AuthenticationException("Could not login, Please provide correct username and password!");
    }
    return loginUserOrAnonymous(username, password, null, null, false);
  }

  public Principal login(HttpServletRequest request, HttpServletResponse response) throws IngeTechnicalException, AuthenticationException {

    return loginUserOrAnonymous(null, null, request, response, false);
  }

  public Principal loginForPasswordChange(String username, String password) throws IngeTechnicalException, AuthenticationException {
    if (null == username || username.trim().isEmpty() || null == password || password.trim().isEmpty()) {
      throw new AuthenticationException("Could not login, Please provide correct username and password!");
    }
    return loginUserOrAnonymous(username, password, null, null, true);
  }

  private Principal loginUserOrAnonymous(String username, String password, HttpServletRequest request, HttpServletResponse response,
      boolean passwordChangeRequest) throws IngeTechnicalException, AuthenticationException {

    Principal principal = null;

    if (null != username) {

      // Helper to login as any user if you are sysadmin
      if (username.contains("#")) {
        String[] parts = username.split("#");
        AccountUserDbVO userAccountSysadmin = this.userAccountRepository.findByLoginname(parts[0]);
        String encodedPassword = this.userLoginRepository.findPassword(parts[0]);

        if (null != userAccountSysadmin && userAccountSysadmin.isActive() && null != encodedPassword
            && this.passwordEncoder.matches(password, encodedPassword)) {
          this.loginAttemptsCache.loginSucceeded(username);
          for (GrantVO grant : userAccountSysadmin.getGrantList()) {
            if (GrantVO.PredefinedRoles.SYSADMIN.frameworkValue().contentEquals(grant.getRole())) {
              AccountUserDbVO userAccountToLogin = this.userAccountRepository.findByLoginname(parts[1]);
              String token = createToken(userAccountToLogin, request);
              principal = new Principal(userAccountToLogin, token);
            }
          }
        }

        if (null == principal || null == principal.getUserAccount() || !principal.getUserAccount().isActive()) {
          this.loginAttemptsCache.loginFailed(username);
          throw new AuthenticationException("Could not login, incorrect username and password provided or user is deactivated!");
        }
      } else {
        AccountUserDbVO userAccount = this.userAccountRepository.findByLoginname(username);
        String encodedPassword = this.userLoginRepository.findPassword(username);

        if (null != userAccount && userAccount.isActive() && null != encodedPassword
            && this.passwordEncoder.matches(password, encodedPassword)) {
          if (this.loginAttemptsCache.isBlocked(username)) {
            throw new AuthenticationException(
                username + " is blocked for " + this.loginAttemptsCache.ATTEMPT_TIMER + " since last attempt");
          } else if (!this.userLoginRepository.findPasswordChangeFlag(username) && !passwordChangeRequest) {
            throw new AuthenticationException(username + " needs to change password first");
          }
          this.loginAttemptsCache.loginSucceeded(username);
          String token = createToken(userAccount, request);
          principal = new Principal(userAccount, token);
        } else {
          this.loginAttemptsCache.loginFailed(username);
          throw new AuthenticationException("Could not login, incorrect username and password provided or user is deactivated!");
        }
      }

      // Set Cookie
      if (null != principal && null != response) {
        Cookie cookie = new Cookie("inge_auth_token", principal.getJwToken());
        cookie.setPath("/");
        cookie.setMaxAge(TOKEN_MAX_AGE_HOURS * 3600);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
      }
    }

    // Login anonymous, ip-based user
    else if (null != request && null != request.getHeader("X-Forwarded-For")) {
      String token = createToken(null, request);
      principal = new Principal(null, token);
    } else if (null != request && null != request.getRemoteAddr()) {
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
    if (!id.startsWith(IdentifierProviderServiceImpl.ID_PREFIX.USER.getPrefix())) {
      AccountUserDbVO user = this.userAccountRepository.findByLoginname(id);
      if (null != user) {
        userId = user.getObjectId();
      }
    }
    return super.get(userId, authenticationToken);

  }

  @Override
  public AccountUserDbVO get(String authenticationToken) throws AuthenticationException {
    DecodedJWT jwt = verifyToken(authenticationToken);
    String userId = jwt.getSubject();
    return this.userAccountRepository.findByLoginname(userId);
  }

  public DecodedJWT verifyToken(String authenticationToken) throws AuthenticationException {
    try {
      DecodedJWT jwt = this.jwtVerifier.verify(authenticationToken);
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

      JWTCreator.Builder jwtBuilder = JWT.create().withIssuedAt(issueDate).withIssuer(this.jwtIssuer).withExpiresAt(expirationDate);

      if (null != user) {
        jwtBuilder.withClaim("id", user.getObjectId()).withSubject(user.getLoginname());
      }

      // Write ip adress as header in token
      if (null != request && null != request.getHeader("X-Forwarded-For")) {
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("ip", request.getHeader("X-Forwarded-For"));
        jwtBuilder.withHeader(headerMap);
      } else if (null != request && null != request.getRemoteAddr()) {
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("ip", request.getRemoteAddr());
        jwtBuilder.withHeader(headerMap);
      }

      return jwtBuilder.sign(this.jwtAlgorithmKey);
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

    if (null == givenUser.getName() || givenUser.getName().trim().isEmpty() || null == givenUser.getLoginname()
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
      tobeUpdatedUser.setObjectId(this.idProviderService.getNewId(IdentifierProviderServiceImpl.ID_PREFIX.USER));
    }
    return null;

  }

  @Override
  protected JpaRepository<AccountUserDbVO, String> getDbRepository() {
    return this.userAccountRepository;
  }

  @Override
  protected GenericDaoEs<AccountUserDbVO> getElasticDao() {
    return this.userAccountDao;
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
    if (null == loginname || loginname.trim().isEmpty()) {
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
    Principal principal = this.aaService.checkLoginRequired(authenticationToken);
    AccountUserDbVO accountToBeUpdated = this.userAccountRepository.findById(id).orElse(null);
    if (null == accountToBeUpdated) {
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
      accountToBeUpdated = this.userAccountRepository.saveAndFlush(accountToBeUpdated);
    } catch (DataAccessException e) {
      handleDBException(e);
    }

    this.userAccountDao.createImmediately(accountToBeUpdated.getObjectId(), accountToBeUpdated);
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

    RuleResult result = validator.validate(new PasswordData(password));
    if (!result.isValid()) {
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
        new CharacterRule(new MyCharacterData(), 1));
    PasswordGenerator generator = new PasswordGenerator();

    // Generated password is 12 characters long, which complies with policy
    return generator.generatePassword(12, rules);
  }

  private static class MyCharacterData implements CharacterData {
    @Override
    public String getErrorCode() {
      return "ERR_SPACE";
    }

    @Override
    public String getCharacters() {
      return "!\\\"#$%&'()*+,-./:;<=>?@[\\\\]^_`{|}~";
    }
  }
}
