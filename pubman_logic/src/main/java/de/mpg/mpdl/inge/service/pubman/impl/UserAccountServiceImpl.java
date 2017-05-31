package de.mpg.mpdl.inge.service.pubman.impl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import de.mpg.mpdl.inge.db.model.valueobjects.AccountUserDbRO;
import de.mpg.mpdl.inge.db.model.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.db.model.valueobjects.AffiliationDbRO;
import de.mpg.mpdl.inge.db.model.valueobjects.AffiliationDbVO;
import de.mpg.mpdl.inge.db.model.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.db.repository.ContextRepository;
import de.mpg.mpdl.inge.db.repository.IdentifierProviderServiceImpl;
import de.mpg.mpdl.inge.db.repository.OrganizationRepository;
import de.mpg.mpdl.inge.db.repository.IdentifierProviderServiceImpl.ID_PREFIX;
import de.mpg.mpdl.inge.db.repository.UserAccountRepository;
import de.mpg.mpdl.inge.db.repository.UserLoginRepository;
import de.mpg.mpdl.inge.es.dao.GenericDaoEs;
import de.mpg.mpdl.inge.es.dao.UserAccountDaoEs;
import de.mpg.mpdl.inge.inge_validation.exception.ItemInvalidException;
import de.mpg.mpdl.inge.model.exception.IngeServiceException;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO.PredefinedRoles;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.exceptions.AaException;
import de.mpg.mpdl.inge.service.pubman.ContextService;
import de.mpg.mpdl.inge.service.pubman.UserAccountService;
import de.mpg.mpdl.inge.service.util.EntityTransformer;
import de.mpg.mpdl.inge.util.PropertyReader;

@Service
public class UserAccountServiceImpl extends GenericServiceImpl<AccountUserVO, AccountUserDbVO>
    implements UserAccountService {

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
  private UserAccountDaoEs<QueryBuilder> userAccountDao;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private ContextRepository contextRepository;

  @Autowired
  private OrganizationRepository organizationRepository;


  private Algorithm jwtAlgorithmKey;

  private String jwtIssuer;


  public UserAccountServiceImpl() throws Exception {
    String key = PropertyReader.getProperty("inge.jwt.shared-secret");
    if (key == null) {
      logger
          .warn("No 'inge.jwt.shared-secret' is set. Generating a random secret, which might not be secure.");
      key = UUID.randomUUID().toString();
    }

    jwtAlgorithmKey = Algorithm.HMAC512(key);

    jwtIssuer = PropertyReader.getProperty("pubman.instance.url");

  }

  @Transactional
  @Override
  public AccountUserVO create(AccountUserVO givenUser, String authenticationToken)
      throws IngeServiceException, AaException, ItemInvalidException {

    AccountUserVO accountUser = super.create(givenUser, authenticationToken);
    userLoginRepository.insertLogin(accountUser.getUserid(),
        passwordEncoder.encode("default-password"));
    return accountUser;
  }


  @Transactional
  public AccountUserVO create(AccountUserVO givenUser, String password, String authenticationToken)
      throws IngeServiceException, AaException, ItemInvalidException {

    AccountUserVO accountUser = super.create(givenUser, authenticationToken);
    userLoginRepository.insertLogin(accountUser.getUserid(), passwordEncoder.encode(password));
    return accountUser;

  }

  @Transactional
  public AccountUserVO addGrant(String userId, GrantVO grant, String authenticationToken)
      throws IngeServiceException, AaException {
    AccountUserVO userAccount = aaService.checkLoginRequired(authenticationToken);
    AccountUserDbVO objectToBeUpdated = getDbRepository().findOne(userId);
    if (objectToBeUpdated == null) {
      throw new IngeServiceException("Object with given id not found.");
    }

    for (GrantVO existingGrant : objectToBeUpdated.getGrantList()) {
      if (Objects.equals(grant.getRole(), existingGrant.getRole())
          && Objects.equals(grant.getObjectRef(), existingGrant.getObjectRef())) {
        throw new IngeServiceException("Grant with given values already exists in user account "
            + objectToBeUpdated.getObjectId());
      }
    }

    grant.setGrantedTo(null);
    grant.setGrantType(null);
    grant.setReference(null);
    grant.setLastModificationDate(null);

    Object referencedObject = null;

    if (grant.getObjectRef().startsWith(ID_PREFIX.CONTEXT.getPrefix())) {
      ContextDbVO referencedContext = contextRepository.findOne(grant.getObjectRef());
      if (referencedContext != null) {
        referencedObject = EntityTransformer.transformToOld(referencedContext);
      }
    } else if (grant.getObjectRef().startsWith(ID_PREFIX.OU.getPrefix())) {
      AffiliationDbVO referencedOu = organizationRepository.findOne(grant.getObjectRef());
      if (referencedOu != null) {
        referencedObject = EntityTransformer.transformToOld(referencedOu);
      }
    }

    if (referencedObject == null) {
      throw new IngeServiceException("Unknown identifier reference: " + grant.getObjectRef());
    }


    checkAa("addGrant", userAccount, transformToOld(objectToBeUpdated), grant, referencedObject);
    objectToBeUpdated.getGrantList().add(grant);


    objectToBeUpdated = getDbRepository().save(objectToBeUpdated);

    AccountUserVO objectToReturn = transformToOld(objectToBeUpdated);
    getElasticDao().update(objectToBeUpdated.getObjectId(), objectToReturn);

    return objectToReturn;

  }

  @Transactional
  public AccountUserVO removeGrant(String userId, GrantVO grant, String authenticationToken)
      throws IngeServiceException, AaException {
    AccountUserVO userAccount = aaService.checkLoginRequired(authenticationToken);
    AccountUserDbVO objectToBeUpdated = getDbRepository().findOne(userId);
    if (objectToBeUpdated == null) {
      throw new IngeServiceException("Object with given id not found.");
    }

    GrantVO grantToBeRemoved = null;
    for (GrantVO existingGrant : objectToBeUpdated.getGrantList()) {
      if (Objects.equals(grant.getRole(), existingGrant.getRole())
          && Objects.equals(grant.getObjectRef(), existingGrant.getObjectRef())) {
        grantToBeRemoved = existingGrant;
      }
    }

    if (grantToBeRemoved == null) {
      throw new IngeServiceException("Grant with given values does not exist in user account "
          + objectToBeUpdated.getObjectId());
    }

    objectToBeUpdated.getGrantList().remove(grantToBeRemoved);

    checkAa("removeGrant", userAccount, transformToOld(objectToBeUpdated), grant);
    objectToBeUpdated = getDbRepository().save(objectToBeUpdated);

    AccountUserVO objectToReturn = transformToOld(objectToBeUpdated);
    getElasticDao().update(objectToBeUpdated.getObjectId(), objectToReturn);

    return objectToReturn;

  }



  @Override
  public String login(String username, String password) throws IngeServiceException, AaException {
    if (username == null || username.trim().isEmpty()) {
      throw new AaException("Could not login, Please provide correct username and password!");
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
      throw new AaException("Could not login, Please provide correct username and password!");
    }

    else {
      AccountUserVO userAccount = transformToOld(userAccountRepository.findByLoginname(username));
      String encodedPassword = userLoginRepository.findPassword(username);

      if (userAccount != null && encodedPassword != null
          && passwordEncoder.matches(password, encodedPassword)) {

        return createToken(userAccount);

      } else {
        throw new AaException("Could not login, Please provide correct username and password!");
      }
    }



  }



  @Override
  public AccountUserVO get(String authenticationToken) throws IngeServiceException, AaException {
    DecodedJWT jwt = verifyToken(authenticationToken);
    String userId = jwt.getSubject();
    return transformToOld(userAccountRepository.findByLoginname(userId));
  }


  public DecodedJWT verifyToken(String authenticationToken) throws AaException {
    try {
      JWTVerifier verifier = JWT.require(jwtAlgorithmKey).withIssuer(jwtIssuer).build();
      DecodedJWT jwt = verifier.verify(authenticationToken);
      return jwt;
    } catch (JWTVerificationException e) {
      throw new AaException("Could not verify token: " + e.getMessage(), e);
    }

  }

  private String createToken(AccountUserVO user) throws IngeServiceException {
    try {
      Instant now = Instant.now();
      Date issueDate = Date.from(now);
      Date expirationDate = Date.from(now.plus(2, ChronoUnit.HOURS));
      logger.info("Creating token with issue date: " + issueDate + " and expiration date "
          + expirationDate);

      return JWT.create().withSubject(user.getUserid()).withIssuedAt(issueDate)
          .withIssuer(jwtIssuer).withExpiresAt(expirationDate).sign(jwtAlgorithmKey);
    } catch (Exception e) {
      throw new IngeServiceException("Could not generate token " + e.getMessage(), e);
    }

  }



  @Override
  protected AccountUserDbVO createEmptyDbObject() {
    return new AccountUserDbVO();
  }

  @Override
  protected List<String> updateObjectWithValues(AccountUserVO givenUser,
      AccountUserDbVO tobeUpdatedUser, AccountUserVO callingUser, boolean create)
      throws IngeServiceException {
    Date currentDate = new Date();
    AccountUserDbRO mod = new AccountUserDbRO();
    mod.setName(callingUser.getName());
    mod.setObjectId(callingUser.getReference().getObjectId());

    if (givenUser.getName() == null || givenUser.getName().trim().isEmpty()
        || givenUser.getUserid() == null || givenUser.getUserid().trim().isEmpty()) {
      throw new IngeServiceException("A name and user id is required");
    }

    tobeUpdatedUser.setActive(true);

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

    tobeUpdatedUser.setLastModificationDate(currentDate);
    tobeUpdatedUser.setModifier(mod);

    // tobeUpdatedUser.setGrantList(givenUser.getGrants());



    if (create) {
      tobeUpdatedUser.setCreationDate(currentDate);
      tobeUpdatedUser.setCreator(mod);
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
  protected GenericDaoEs<AccountUserVO, QueryBuilder> getElasticDao() {
    return userAccountDao;
  }

  @Override
  protected String getObjectId(AccountUserVO object) {
    return object.getReference().getObjectId();
  }



}
