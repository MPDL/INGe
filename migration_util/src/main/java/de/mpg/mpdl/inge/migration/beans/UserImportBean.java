package de.mpg.mpdl.inge.migration.beans;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import de.mpg.mpdl.inge.db.repository.UserAccountRepository;
import de.mpg.mpdl.inge.db.repository.UserLoginRepository;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.AffiliationDbRO;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.UserAttributeVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;

@Component
public class UserImportBean {

  static Logger log = Logger.getLogger(Migration.class.getName());

  @Value("${escidoc.url}")
  private String escidocUrl;
  @Value("${users.path}")
  private String usersPath;
  @Value("${user.path}")
  private String userPath;
  @Value("${db.pwd}")
  private String dbpwd;

  @Autowired
  private UserAccountRepository userRepository;
  @Autowired
  private UserLoginRepository userLoginRepository;
  @Autowired
  private MigrationUtilBean utils;

  public void importSingleUser(String id) throws Exception {
    HttpClient client = utils.setup();
    URI uri = new URIBuilder(escidocUrl + userPath + "/" + id).build();
    final HttpGet requestUser = new HttpGet(uri);
    HttpResponse responseUser = client.execute(requestUser);
    String userXml = EntityUtils.toString(responseUser.getEntity(), StandardCharsets.UTF_8);
    AccountUserVO accountUser = XmlTransformingService.transformToAccountUser(userXml);
    uri = new URIBuilder(escidocUrl + userPath + "/" + id + "/resources/current-grants").addParameter("maximumRecords", String.valueOf(500))
        .addParameter("startRecord", String.valueOf(1)).build();
    final HttpGet requestGrant = new HttpGet(uri);
    HttpResponse responseGrant = client.execute(requestGrant);
    String grantXml = EntityUtils.toString(responseGrant.getEntity(), StandardCharsets.UTF_8);
    List<GrantVO> grantList = XmlTransformingService.transformToGrantVOList(grantXml);

    uri = new URIBuilder(escidocUrl + userPath + "/" + id + "/resources/attributes").addParameter("maximumRecords", String.valueOf(500))
        .addParameter("startRecord", String.valueOf(1)).build();
    final HttpGet requestAttrs = new HttpGet(uri);
    HttpResponse responseAttrs = client.execute(requestAttrs);
    String attrXml = EntityUtils.toString(responseAttrs.getEntity(), StandardCharsets.UTF_8);
    List<UserAttributeVO> userAttrList = XmlTransformingService.transformToUserAttributesList(attrXml);

    log.info("Saving user" + accountUser.getName() + " - " + accountUser.getReference().getObjectId());
    try {
      userRepository.save(transformToNew(accountUser, grantList, userAttrList));
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public void importUsers() throws Exception {
    HttpClient client = utils.setup();
    URI uri = new URIBuilder(escidocUrl + usersPath).addParameter("maximumRecords", String.valueOf(5000))
        .addParameter("startRecord", String.valueOf(1)).build();
    final HttpGet request = new HttpGet(uri);
    HttpResponse response = client.execute(request);
    String xml = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

    SearchRetrieveResponseVO<AccountUserVO> userList = XmlTransformingService.transformToSearchRetrieveResponseAccountUser(xml);
    for (SearchRetrieveRecordVO<AccountUserVO> accountUser : userList.getRecords()) {

      String objectId = accountUser.getData().getReference().getObjectId();
      objectId = objectId.substring(objectId.lastIndexOf("/") + 1, objectId.length());

      uri = new URIBuilder(escidocUrl + userPath + "/" + objectId + "/resources/current-grants")
          .addParameter("maximumRecords", String.valueOf(5000)).addParameter("startRecord", String.valueOf(1)).build();
      final HttpGet requestGrant = new HttpGet(uri);
      HttpResponse responseGrant = client.execute(requestGrant);
      String grantXml = EntityUtils.toString(responseGrant.getEntity(), StandardCharsets.UTF_8);
      List<GrantVO> grantList = XmlTransformingService.transformToGrantVOList(grantXml);

      uri = new URIBuilder(escidocUrl + userPath + "/" + objectId + "/resources/attributes")
          .addParameter("maximumRecords", String.valueOf(5000)).addParameter("startRecord", String.valueOf(1)).build();
      final HttpGet requestAttrs = new HttpGet(uri);
      HttpResponse responseAttrs = client.execute(requestAttrs);
      String attrXml = EntityUtils.toString(responseAttrs.getEntity(), StandardCharsets.UTF_8);
      List<UserAttributeVO> userAttrList = XmlTransformingService.transformToUserAttributesList(attrXml);

      log.info("Saving user" + accountUser.getData().getName() + " - " + accountUser.getData().getReference().getObjectId());
      try {
        userRepository.save(transformToNew(accountUser.getData(), grantList, userAttrList));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private AccountUserDbVO transformToNew(AccountUserVO oldAccountUserVO, List<GrantVO> grants, List<UserAttributeVO> attributes) {

    AccountUserDbRO owner = new AccountUserDbRO();
    AccountUserDbRO modifier = new AccountUserDbRO();
    AffiliationDbRO affRO = new AffiliationDbRO();
    boolean usr_admin = false;
    boolean usr_group_admin = false;
    boolean yb_dep = false;

    owner.setObjectId(utils.changeId("user", oldAccountUserVO.getCreator().getObjectId()));
    // owner.setName(oldAccountUserVO.getCreator().getTitle());

    modifier.setObjectId(utils.changeId("user", oldAccountUserVO.getModifiedBy().getObjectId()));
    // modifier.setName(oldAccountUserVO.getModifiedBy().getTitle());

    AccountUserDbVO newAccountUser = new AccountUserDbVO();
    newAccountUser.setActive(oldAccountUserVO.isActive());

    /*
    if (oldAccountUserVO.getAffiliations() != null && oldAccountUserVO.getAffiliations().size() > 0) {
    	usr_affRO.setObjectId(utils.changeId("ou", oldAccountUserVO.getAffiliations().get(0).getObjectId()));
    	newAccountUser.setAffiliation(usr_affRO);
    }
    */

    newAccountUser.setCreationDate(oldAccountUserVO.getCreationDate());
    newAccountUser.setCreator(owner);
    newAccountUser.setEmail(oldAccountUserVO.getEmail());

    if (grants != null) {

      usr_admin = grants.removeIf((GrantVO vo) -> vo.getRole().contains("user-account-administrator"));
      usr_group_admin = grants.removeIf((GrantVO vo) -> vo.getRole().contains("user-group-administrator"));
      yb_dep =
          grants.removeIf((GrantVO vo) -> vo.getRole().contains("depositor") && vo.getObjectRef().equals("/ir/context/escidoc:1600138"));

      for (GrantVO grant : grants) {
        grant.setGrantedTo(null);
        grant.setLastModificationDate(null);
        grant.setReference(null);
        if (grant.getObjectRef() != null) {
          if (grant.getObjectRef().contains("context")) {
            grant.setObjectRef(utils.changeId("ctx", grant.getObjectRef()));
          } else if (grant.getObjectRef().contains("organizational-unit")) {
            grant.setObjectRef(utils.changeId("ou", grant.getObjectRef()));
          } else {
            log.info("Unknown grant object: " + grant.getObjectRef());
          }
        }

        if (grant.getRole().contains("depositor")) {
          grant.setRole("DEPOSITOR");
        } else if (grant.getRole().contains("moderator")) {
          grant.setRole("MODERATOR");
        } else if (grant.getRole().contains("system-administrator")) {
          grant.setRole("SYSADMIN");
        } else if (grant.getRole().contains("reporter")) {
          grant.setRole("REPORTER");
        } else if (grant.getRole().contains("cone-open")) {
          grant.setRole("CONE_OPEN_VOCABULARY_EDITOR");
        } else if (grant.getRole().contains("cone-closed")) {
          grant.setRole("CONE_CLOSED_VOCABULARY_EDITOR");
        } else {
          log.info("Unknown role: " + grant.getRole());
        }
      }
    }

    if (attributes != null) {
      for (UserAttributeVO attr : attributes) {

        if (attr.getName().equals("o")) {
          affRO.setObjectId(utils.changeId("ou", attr.getValue()));
          newAccountUser.setAffiliation(affRO);
        }

        if (attr.getName().equals("email")) {
          if (attr.getValue().isEmpty()) {
            newAccountUser.setEmail(null);
          } else {
            newAccountUser.setEmail(attr.getValue());
          }
        }
      }
    }

    if (usr_admin && usr_group_admin && yb_dep) {
      GrantVO ybe = new GrantVO();
      ybe.setRole("YEARBOOK-EDITOR");
      ybe.setObjectRef(affRO.getObjectId());
      grants.add(ybe);
    }

    newAccountUser.setGrantList(grants);
    newAccountUser.setLastModificationDate(oldAccountUserVO.getLastModificationDate());
    newAccountUser.setLoginname(oldAccountUserVO.getUserid());
    newAccountUser.setModifier(modifier);
    newAccountUser.setName(oldAccountUserVO.getName());
    newAccountUser.setObjectId(utils.changeId("user", oldAccountUserVO.getReference().getObjectId()));

    return newAccountUser;
  }

  public void importLogins() throws Exception {

    PGSimpleDataSource dataSource = new PGSimpleDataSource();
    dataSource.setUser("postgres");
    dataSource.setPassword(dbpwd);
    dataSource.setDatabaseName("escidoc-core");
    dataSource.setServerName("srv02.mpdl.mpg.de");
    dataSource.setPortNumber(5432);

    Connection conn = dataSource.getConnection();

    ResultSet res = conn.createStatement().executeQuery("SELECT loginname,password FROM aa.user_account;");

    PasswordEncoder pe = new BCryptPasswordEncoder();

    while (res.next()) {

      log.info("Saving " + res.getString(1));
      try {
        userLoginRepository.insertLogin(res.getString(1), pe.encode(res.getString(2)));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

  }
}
