package de.mpg.mpdl.inge.service.pubman.impl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auth0.jwt.JWT;
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
import de.mpg.mpdl.inge.model.db.valueobjects.AffiliationDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.AffiliationDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO.PredefinedRoles;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.ReindexListener;
import de.mpg.mpdl.inge.service.pubman.UserAccountService;
import de.mpg.mpdl.inge.service.util.EntityTransformer;
import de.mpg.mpdl.inge.util.PropertyReader;

@Service
public class UserAccountServiceImpl extends
    GenericServiceImpl<AccountUserVO, AccountUserDbVO, String> implements UserAccountService,
    ReindexListener {

  private static Logger logger = LogManager.getLogger(UserAccountServiceImpl.class);

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


  private Algorithm jwtAlgorithmKey;

  private String jwtIssuer;

  // private final static String PASSWORD_REGEX =
  // "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
  private final static String PASSWORD_REGEX = "^(?=.*[A-Za-z0-9])(?=\\S+$).{6,}$";

  /**
   * Loginname must consist of at least 4 characters of a-z, A-Z, 0-9, @, _, -, .
   */
  private final static String LOGINNAME_REGEX = "^[A-Za-z0-9@_\\-\\.]{4,}$";


  public UserAccountServiceImpl() throws Exception {
    String key = PropertyReader.getProperty("inge.jwt.shared-secret");
    if (key == null || key.trim().isEmpty()) {
      logger
          .warn("No 'inge.jwt.shared-secret' is set. Generating a random secret, which might not be secure.");
      key = UUID.randomUUID().toString();
    }

    jwtAlgorithmKey = Algorithm.HMAC512(key);

    jwtIssuer = PropertyReader.getProperty("pubman.instance.url");

  }

  @Transactional(rollbackFor = Throwable.class)
  @Override
  public AccountUserVO create(AccountUserVO givenUser, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException,
      IngeApplicationException {

    AccountUserVO accountUser = super.create(givenUser, authenticationToken);
    validatePassword(givenUser.getPassword());
    try {
      userLoginRepository.insertLogin(accountUser.getUserid(),
          passwordEncoder.encode(givenUser.getPassword()));
    } catch (DataAccessException e) {
      handleDBException(e);
    }
    if (givenUser.getGrants() != null && !givenUser.getGrants().isEmpty()) {
      accountUser =
          this.addGrants(accountUser.getReference().getObjectId(), accountUser
              .getLastModificationDate(), givenUser.getGrants().toArray(new GrantVO[] {}),
              authenticationToken);
    }


    return accountUser;
  }



  @Transactional(rollbackFor = Throwable.class)
  @Override
  public AccountUserVO changePassword(String userId, Date modificationDate, String newPassword,
      String authenticationToken) throws IngeTechnicalException, AuthenticationException,
      AuthorizationException, IngeApplicationException {

    AccountUserVO userAccount = aaService.checkLoginRequired(authenticationToken);
    validatePassword(newPassword);
    AccountUserDbVO userDbToUpdated = userAccountRepository.findOne(userId);

    if (userDbToUpdated == null) {
      throw new IngeApplicationException("Object with given id not found.");
    }

    AccountUserVO userVoToUpdated = EntityTransformer.transformToOld(userDbToUpdated);

    checkEqualModificationDate(modificationDate, getModificationDate(userVoToUpdated));

    checkAa("changePassword", userAccount, userVoToUpdated);
    userLoginRepository.updateLogin(userVoToUpdated.getUserid(),
        passwordEncoder.encode(newPassword));

    updateWithTechnicalMetadata(userDbToUpdated, userAccount, false);

    try {
      userDbToUpdated = getDbRepository().saveAndFlush(userDbToUpdated);
    } catch (DataAccessException e) {
      handleDBException(e);
    }

    AccountUserVO objectToReturn = transformToOld(userDbToUpdated);
    getElasticDao().updateImmediately(userDbToUpdated.getObjectId(), objectToReturn);
    return objectToReturn;

  }


  @Transactional(rollbackFor = Throwable.class)
  public AccountUserVO addGrants(String userId, Date modificationDate, GrantVO[] grants,
      String authenticationToken) throws IngeTechnicalException, AuthenticationException,
      AuthorizationException, IngeApplicationException {
    AccountUserVO userAccount = aaService.checkLoginRequired(authenticationToken);
    AccountUserDbVO objectToBeUpdated = getDbRepository().findOne(userId);
    if (objectToBeUpdated == null) {
      throw new IngeApplicationException("Object with given id not found.");
    }

    AccountUserVO userVoToUpdated = EntityTransformer.transformToOld(objectToBeUpdated);

    checkEqualModificationDate(modificationDate, getModificationDate(userVoToUpdated));

    for (GrantVO grantToBeAdded : grants) {

      for (GrantVO existingGrant : objectToBeUpdated.getGrantList()) {
        if (Objects.equals(grantToBeAdded.getRole(), existingGrant.getRole())
            && Objects.equals(grantToBeAdded.getObjectRef(), existingGrant.getObjectRef())) {
          throw new IngeApplicationException("Grant with given value [role="
              + grantToBeAdded.getRole() + ", objectRef= " + grantToBeAdded.getObjectRef()
              + "] already exists in user account " + objectToBeUpdated.getObjectId());
        }
      }

      grantToBeAdded.setGrantedTo(null);
      grantToBeAdded.setGrantType(null);
      grantToBeAdded.setReference(null);
      grantToBeAdded.setLastModificationDate(null);

      Object referencedObject = null;

      if (grantToBeAdded.getObjectRef() != null) {
        if (grantToBeAdded.getObjectRef().startsWith(ID_PREFIX.CONTEXT.getPrefix())) {
          ContextDbVO referencedContext = contextRepository.findOne(grantToBeAdded.getObjectRef());
          if (referencedContext != null) {
            referencedObject = EntityTransformer.transformToOld(referencedContext);
          }
        } else if (grantToBeAdded.getObjectRef().startsWith(ID_PREFIX.OU.getPrefix())) {
          AffiliationDbVO referencedOu =
              organizationRepository.findOne(grantToBeAdded.getObjectRef());
          if (referencedOu != null) {
            referencedObject = EntityTransformer.transformToOld(referencedOu);
          }
        }

        if (referencedObject == null) {
          throw new IngeApplicationException("Unknown identifier reference: "
              + grantToBeAdded.getObjectRef());
        }
      }



      checkAa("addGrants", userAccount, transformToOld(objectToBeUpdated), grantToBeAdded,
          referencedObject);


    }
    objectToBeUpdated.getGrantList().addAll(Arrays.asList(grants));
    updateWithTechnicalMetadata(objectToBeUpdated, userAccount, false);


    try {
      objectToBeUpdated = getDbRepository().saveAndFlush(objectToBeUpdated);
    } catch (DataAccessException e) {
      handleDBException(e);
    }
    AccountUserVO objectToReturn = transformToOld(objectToBeUpdated);
    getElasticDao().updateImmediately(objectToBeUpdated.getObjectId(), objectToReturn);

    return objectToReturn;

  }

  @Transactional(rollbackFor = Throwable.class)
  public AccountUserVO removeGrants(String userId, Date modificationDate, GrantVO[] grants,
      String authenticationToken) throws IngeTechnicalException, AuthenticationException,
      AuthorizationException, IngeApplicationException {
    AccountUserVO userAccount = aaService.checkLoginRequired(authenticationToken);
    AccountUserDbVO objectToBeUpdated = getDbRepository().findOne(userId);
    if (objectToBeUpdated == null) {
      throw new IngeApplicationException("Object with given id not found.");
    }

    AccountUserVO userVoToUpdated = EntityTransformer.transformToOld(objectToBeUpdated);

    checkEqualModificationDate(modificationDate, getModificationDate(userVoToUpdated));

    for (GrantVO givenGrant : grants) {
      GrantVO grantToBeRemoved = null;
      for (GrantVO existingGrant : objectToBeUpdated.getGrantList()) {
        if (Objects.equals(givenGrant.getRole(), existingGrant.getRole())
            && Objects.equals(givenGrant.getObjectRef(), existingGrant.getObjectRef())) {
          grantToBeRemoved = existingGrant;
        }
      }

      if (grantToBeRemoved == null) {
        throw new IngeApplicationException("Grant with given values [role=" + givenGrant.getRole()
            + ", objectRef= " + givenGrant.getObjectRef() + "] does not exist in user account "
            + objectToBeUpdated.getObjectId());
      }


      checkAa("removeGrants", userAccount, transformToOld(objectToBeUpdated), givenGrant);
      objectToBeUpdated.getGrantList().remove(grantToBeRemoved);
    }
    updateWithTechnicalMetadata(objectToBeUpdated, userAccount, false);

    try {
      objectToBeUpdated = getDbRepository().saveAndFlush(objectToBeUpdated);
    } catch (DataAccessException e) {
      handleDBException(e);
    }
    AccountUserVO objectToReturn = transformToOld(objectToBeUpdated);
    getElasticDao().updateImmediately(objectToBeUpdated.getObjectId(), objectToReturn);

    return objectToReturn;

  }



  @Override
  public String login(String username, String password, HttpServletRequest request,
      HttpServletResponse response) throws IngeTechnicalException, AuthenticationException,
      AuthorizationException, IngeApplicationException {
    String token = login(username, password);
    Cookie cookie = new Cookie("inge_auth_token", token);
    cookie.setPath("/");
    response.addCookie(cookie);
    return token;
  }


  @Override
  public void logout(String authenticationToken, HttpServletRequest request,
      HttpServletResponse response) throws IngeTechnicalException, AuthenticationException,
      AuthorizationException, IngeApplicationException {

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
  public String login(String username, String password) throws IngeTechnicalException,
      AuthenticationException {
    if (username == null || username.trim().isEmpty()) {
      throw new AuthenticationException(
          "Could not login, Please provide correct username and password!");
    }

    // Helper to login as any user if you are sysadmin
    if (username.contains("#")) {
      String[] parts = username.split("#");
      AccountUserDbVO userAccountSysadmin = userAccountRepository.findByLoginname(parts[0]);
      String encodedPassword = userLoginRepository.findPassword(parts[0]);

      if (userAccountSysadmin != null && encodedPassword != null
          && passwordEncoder.matches(password, encodedPassword)) {
        for (GrantVO grant : userAccountSysadmin.getGrantList()) {
          if (grant.getRole().equals(PredefinedRoles.SYSADMIN.frameworkValue())) {
            AccountUserVO userAccountToLogin =
                transformToOld(userAccountRepository.findByLoginname(parts[1]));
            return createToken(userAccountToLogin);
          }
        }
      }
      throw new AuthenticationException(
          "Could not login, Please provide correct username and password!");
    }

    else {
      AccountUserVO userAccount = transformToOld(userAccountRepository.findByLoginname(username));
      String encodedPassword = userLoginRepository.findPassword(username);

      if (userAccount != null && encodedPassword != null
          && passwordEncoder.matches(password, encodedPassword)) {

        return createToken(userAccount);

      } else {
        throw new AuthenticationException(
            "Could not login, Please provide correct username and password!");
      }
    }



  }

  @Transactional(readOnly = true)
  @Override
  public AccountUserVO get(String id, String authenticationToken) throws IngeTechnicalException,
      AuthenticationException, AuthorizationException, IngeApplicationException {

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
  public AccountUserVO get(String authenticationToken) throws IngeTechnicalException,
      AuthenticationException {
    DecodedJWT jwt = verifyToken(authenticationToken);
    String userId = jwt.getSubject();
    return transformToOld(userAccountRepository.findByLoginname(userId));
  }


  public DecodedJWT verifyToken(String authenticationToken) throws AuthenticationException {
    try {
      JWTVerifier verifier = JWT.require(jwtAlgorithmKey).withIssuer(jwtIssuer).build();
      DecodedJWT jwt = verifier.verify(authenticationToken);
      return jwt;
    } catch (JWTVerificationException e) {
      throw new AuthenticationException("Could not verify token: " + e.getMessage(), e);
    }

  }

  private String createToken(AccountUserVO user) throws IngeTechnicalException {
    try {
      Instant now = Instant.now();
      Date issueDate = Date.from(now);
      Date expirationDate = Date.from(now.plus(2, ChronoUnit.HOURS));
      logger.info("Creating token with issue date: " + issueDate + " and expiration date "
          + expirationDate);

      return JWT.create().withClaim("id", user.getReference().getObjectId())
          .withSubject(user.getUserid()).withIssuedAt(issueDate).withIssuer(jwtIssuer)
          .withExpiresAt(expirationDate).sign(jwtAlgorithmKey);
    } catch (Exception e) {
      throw new IngeTechnicalException("Could not generate token " + e.getMessage(), e);
    }

  }



  @Override
  protected AccountUserDbVO createEmptyDbObject() {
    return new AccountUserDbVO();
  }

  @Override
  protected List<String> updateObjectWithValues(AccountUserVO givenUser,
      AccountUserDbVO tobeUpdatedUser, AccountUserVO callingUser, boolean create)
      throws IngeApplicationException {


    if (givenUser.getName() == null || givenUser.getName().trim().isEmpty()
        || givenUser.getUserid() == null || givenUser.getUserid().trim().isEmpty()) {
      throw new IngeApplicationException("A name and user id is required");
    }

    validateLoginname(givenUser.getUserid());

    if (create) {
      tobeUpdatedUser.setActive(true);
    }


    if (givenUser.getAffiliations() == null || givenUser.getAffiliations().size() == 0) {
      tobeUpdatedUser.setAffiliation(null);
    } else {
      AffiliationDbRO affRo = new AffiliationDbRO();
      affRo.setObjectId(givenUser.getAffiliations().get(0).getObjectId());
      tobeUpdatedUser.setAffiliation(affRo);
    }


    tobeUpdatedUser.setEmail(givenUser.getEmail());
    tobeUpdatedUser.setLoginname(givenUser.getUserid());
    tobeUpdatedUser.setName(givenUser.getName());
    // tobeUpdatedUser.setPassword(givenUser.getPassword());


    // tobeUpdatedUser.setGrantList(givenUser.getGrants());



    if (create) {
      tobeUpdatedUser.setObjectId(idProviderService.getNewId(ID_PREFIX.USER));
    }
    return null;

  }

  @Override
  protected AccountUserVO transformToOld(AccountUserDbVO dbObject) {
    return EntityTransformer.transformToOld(dbObject);
  }

  @Override
  protected JpaRepository<AccountUserDbVO, String> getDbRepository() {
    return userAccountRepository;
  }

  @Override
  protected GenericDaoEs<AccountUserVO> getElasticDao() {
    return userAccountDao;
  }

  @Override
  protected String getObjectId(AccountUserVO object) {
    return object.getReference().getObjectId();
  }

  private void validatePassword(String password) throws IngeApplicationException {
    if (password == null || password.trim().isEmpty()) {
      throw new IngeApplicationException("A password has to be provided");
    } else if (!password.matches(PASSWORD_REGEX)) {
      throw new IngeApplicationException(
          "Password  must consist of at least 6 characters, no whitespaces");
    }

  }

  private void validateLoginname(String loginname) throws IngeApplicationException {
    if (loginname == null || loginname.trim().isEmpty()) {
      throw new IngeApplicationException("A loginname (userId) has to be provided");
    } else if (!loginname.matches(LOGINNAME_REGEX)) {
      throw new IngeApplicationException(
          "Invalid loginname (userId). Loginname  must consist of an email adress or at least 4 characters, no whitespaces, no special characters");
    }

  }

  @Override
  protected Date getModificationDate(AccountUserVO object) {
    return object.getLastModificationDate();
  }


  @Override
  @Transactional(rollbackFor = Throwable.class)
  public AccountUserVO activate(String id, Date modificationDate, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException,
      IngeApplicationException {
    return changeState(id, modificationDate, authenticationToken, true);
  }


  @Override
  @Transactional(rollbackFor = Throwable.class)
  public AccountUserVO deactivate(String id, Date modificationDate, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException,
      IngeApplicationException {
    return changeState(id, modificationDate, authenticationToken, false);
  }

  private AccountUserVO changeState(String id, Date modificationDate, String authenticationToken,
      boolean active) throws IngeTechnicalException, AuthenticationException,
      AuthorizationException, IngeApplicationException {
    AccountUserVO userAccount = aaService.checkLoginRequired(authenticationToken);
    AccountUserDbVO accountToBeUpdated = userAccountRepository.findOne(id);
    if (accountToBeUpdated == null) {
      throw new IngeTechnicalException("User account with given id " + id + " not found.");
    }

    AccountUserVO userVoToBeUpdated = transformToOld(accountToBeUpdated);

    checkEqualModificationDate(modificationDate, getModificationDate(userVoToBeUpdated));

    checkAa((active ? "activate" : "deactivate"), userAccount, userVoToBeUpdated);

    accountToBeUpdated.setActive(active);
    updateWithTechnicalMetadata(accountToBeUpdated, userAccount, false);

    try {
      accountToBeUpdated = userAccountRepository.saveAndFlush(accountToBeUpdated);
    } catch (DataAccessException e) {
      handleDBException(e);
    }
    AccountUserVO userToReturn = EntityTransformer.transformToOld(accountToBeUpdated);
    userAccountDao.updateImmediately(accountToBeUpdated.getObjectId(), userToReturn);
    return userToReturn;
  }

  @Override
  @JmsListener(containerFactory = "queueContainerFactory", destination = "reindex-AccountUserVO")
  public void reindexListener(String id) throws IngeTechnicalException {
    reindex(id, false);

  }



}
