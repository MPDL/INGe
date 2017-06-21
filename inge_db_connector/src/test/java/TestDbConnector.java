import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mpg.mpdl.inge.db.model.valueobjects.AccountUserDbRO;
import de.mpg.mpdl.inge.db.model.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.db.model.valueobjects.AffiliationDbRO;
import de.mpg.mpdl.inge.db.model.valueobjects.AffiliationDbVO;
import de.mpg.mpdl.inge.db.model.valueobjects.ContextDbRO;
import de.mpg.mpdl.inge.db.model.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.db.model.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.db.model.valueobjects.FileDbVO.ChecksumAlgorithm;
import de.mpg.mpdl.inge.db.model.valueobjects.FileDbVO.Storage;
import de.mpg.mpdl.inge.db.model.valueobjects.FileDbVO.Visibility;
import de.mpg.mpdl.inge.db.model.valueobjects.PubItemDbRO;
import de.mpg.mpdl.inge.db.model.valueobjects.PubItemObjectDbVO;
import de.mpg.mpdl.inge.db.model.valueobjects.PubItemVersionDbVO;
import de.mpg.mpdl.inge.db.model.valueobjects.VersionableId;
import de.mpg.mpdl.inge.db.repository.ContextRepository;
import de.mpg.mpdl.inge.db.repository.IdentifierProviderServiceImpl;
import de.mpg.mpdl.inge.db.repository.IdentifierProviderServiceImpl.ID_PREFIX;
import de.mpg.mpdl.inge.db.repository.ItemRepository;
import de.mpg.mpdl.inge.db.repository.OrganizationRepository;
import de.mpg.mpdl.inge.db.repository.UserAccountRepository;
import de.mpg.mpdl.inge.db.repository.UserLoginRepository;
import de.mpg.mpdl.inge.db.spring_config.JPAConfiguration;
import de.mpg.mpdl.inge.model.referenceobjects.AffiliationRO;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.UserAttributeVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.util.AdminHelper;
import de.mpg.mpdl.inge.util.PropertyReader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = JPAConfiguration.class)
public class TestDbConnector {

  @Autowired
  private ItemRepository itemRepository;

  @Autowired
  private OrganizationRepository orgRepository;

  @Autowired
  private ContextRepository contextRepository;

  @Autowired
  private UserAccountRepository userRepository;

  @Autowired
  private UserLoginRepository userLoginRepository;

  @Autowired
  private IdentifierProviderServiceImpl idProvider;

  @PersistenceContext
  private EntityManager entityManager;



  private Queue<AffiliationVO> updateLaterAffs = new LinkedList<AffiliationVO>();

  private HttpClient httpClientWithEscidocCookie;

  @Before
  public void setup() {
    String userHandle = AdminHelper.getAdminUserHandle();
    BasicCookieStore cookieStore = new BasicCookieStore();
    BasicClientCookie cookie = new BasicClientCookie("escidocCookie", userHandle);
    cookie.setDomain("qa-coreservice.mpdl.mpg.de");
    cookie.setPath("/");
    cookieStore.addCookie(cookie);
    httpClientWithEscidocCookie =
        HttpClientBuilder.create().setDefaultCookieStore(cookieStore).build();
  }


  @Test
  @Ignore
  public void testGetOu() throws Exception {

    AffiliationDbVO aff = orgRepository.findOne("ou_persistent13");
    System.out.println(aff.getHasChildren());

    aff = orgRepository.findOne("ou_2063313");
    System.out.println(aff.getHasChildren());

    aff = orgRepository.findOne("ou_2063313");
    System.out.println(aff.getHasChildren());

    aff = orgRepository.findOne("ou_2063313");
    System.out.println(aff.getHasChildren());
  }


  @Test
  @Ignore
  public void testRetrieve() throws Exception {

    while (true) {
      /*
       * SessionFactory sessionFactory =
       * entityManager.getEntityManagerFactory().unwrap(SessionFactory.class); long oldMissCount =
       * sessionFactory.getStatistics().getSecondLevelCacheStatistics("item").getMissCount(); long
       * oldHitCount =
       * sessionFactory.getStatistics().getSecondLevelCacheStatistics("item").getHitCount();
       */
      long time = System.currentTimeMillis();
      PubItemVersionDbVO returnedfindOne1 =
          itemRepository.findOne(new VersionableId("item_1000592", 1));
      // PubItemVersionDbVO returnedfindOne1 = entityManager.find(PubItemVersionDbVO.class, new
      // VersionableId("item_1000592", 1));
      System.out.println("1st findOne needed " + (System.currentTimeMillis() - time) + " ms");
      entityManager.clear();
      /*
       * long newMissCount =
       * sessionFactory.getStatistics().getSecondLevelCacheStatistics("item").getMissCount(); long
       * newHitCount =
       * sessionFactory.getStatistics().getSecondLevelCacheStatistics("item").getHitCount(); if
       * (oldHitCount + 1 == newHitCount && oldMissCount + 1 == newMissCount) {
       * System.out.println("came from DB"); } else if (oldHitCount + 1 == newHitCount &&
       * oldMissCount == newMissCount) { System.out.println("came from cache"); }
       * 
       * oldHitCount = newHitCount; oldMissCount = newMissCount;
       */
      time = System.currentTimeMillis();
      PubItemVersionDbVO returnedfindOne2 =
          itemRepository.findOne(new VersionableId("item_1000592", 1));
      // PubItemVersionDbVO returnedfindOne2 = entityManager.find(PubItemVersionDbVO.class, new
      // VersionableId("item_1000592", 1));
      System.out.println("2nd findOne needed " + (System.currentTimeMillis() - time) + " ms");
      entityManager.clear();

      time = System.currentTimeMillis();
      PubItemVersionDbVO returnedLatestRelease1 = itemRepository.findLatestRelease("item_1000592");
      System.out.println("1st findLastestRelease needed " + (System.currentTimeMillis() - time)
          + " ms");
      entityManager.clear();

      time = System.currentTimeMillis();
      PubItemVersionDbVO returnedLatestRelease2 = itemRepository.findLatestRelease("item_1000592");
      System.out.println("2nd findLatestRelease needed " + (System.currentTimeMillis() - time)
          + " ms ");
      entityManager.clear();
      /*
       * newMissCount =
       * sessionFactory.getStatistics().getSecondLevelCacheStatistics("item").getMissCount();
       * newHitCount =
       * sessionFactory.getStatistics().getSecondLevelCacheStatistics("item").getHitCount(); if
       * (oldHitCount + 1 == newHitCount && oldMissCount + 1 == newMissCount) {
       * System.out.println("came from DB"); } else if (oldHitCount + 1 == newHitCount &&
       * oldMissCount == newMissCount) { System.out.println("came from cache"); }
       */
    }



    /*
     * AffiliationVO affVo = findOu("ou_1113549"); findOu("ou_1113572"); findOu("ou_1113580");
     * 
     * 
     * findLatestVersion("item_1287545"); findLatestVersion("item_1287612");
     * findLatestVersion("item_1289637");
     * 
     * findLatestVersion("item_1289642"); findLatestVersion("item_1289664");
     * findLatestVersion("item_1291665");
     * 
     * 
     * findLatestVersion("item_1291713");
     * 
     * findVersion(new VersionableId("item_1420835", 1)); findVersion(new
     * VersionableId("item_108144", 1)); findVersion(new VersionableId("item_1287592", 1));
     * findVersion(new VersionableId("item_1291616", 1)); findVersion(new
     * VersionableId("item_1293631", 1)); findVersion(new VersionableId("item_1291713", 1));
     * findVersion(new VersionableId("item_1420835", 1)); findVersion(new VersionableId("xx", 1));
     */
  }

  private AffiliationDbVO findOu(String ouId) {
    long time = System.currentTimeMillis();

    AffiliationDbVO aff = orgRepository.findOne(ouId);

    System.out.println("OU took " + (System.currentTimeMillis() - time));
    return aff;
  }

  private void findLatestVersion(String objectId) throws Exception {
    long start = System.currentTimeMillis();
    PubItemDbRO item = itemRepository.findLatestVersion(objectId);
    long time = System.currentTimeMillis() - start;
    System.out.println("Took " + time + "  --  " + item.getObjectIdAndVersion());
  }

  private void findVersion(VersionableId id) throws Exception {
    long start = System.currentTimeMillis();
    PubItemDbRO item = itemRepository.findOne(id);
    long time = System.currentTimeMillis() - start;
    System.out.println("Took " + time + "  --  " + item.getObjectIdAndVersion());
  }



  @Test
  public void importObjects() throws Exception {
    // importAffs();
    // importContexts();
    // importPubItems();
    // importUsers();
    // importLogins();
  }

  @Test
  @Ignore
  public void test() throws Exception {

    AccountUserDbRO user = new AccountUserDbRO();
    user.setName("Martin Boosen");
    user.setObjectId("pure_boosen");

    Date now = new Date();
    PubItemVersionDbVO pubItem = new PubItemVersionDbVO();
    pubItem.setLastMessage("my last message");
    pubItem.setModificationDate(now);
    pubItem.setModifiedBy(user);
    pubItem.setObjectId("pure_1");
    pubItem.setState(PubItemVersionDbVO.State.PENDING);
    pubItem.setVersionNumber(1);
    pubItem.setVersionPid("version_pid");

    pubItem.getMetadata().setTitle("My first DB Item");

    PubItemObjectDbVO object = new PubItemObjectDbVO();
    ContextDbRO context = new ContextDbRO();
    context.setObjectId("pure_context_id");
    object.setContext(context);
    object.setCreationDate(now);
    object.setLastModificationDate(now);
    object.setLatestRelease(null);
    object.setLatestVersion(pubItem);
    object.setLocalTags(null);
    object.setObjectId("pure_1");

    object.setOwner(user);
    object.setPublicStatus(PubItemVersionDbVO.State.PENDING);
    object.setPublicStatusComment("Public status comment");

    pubItem.setObject(object);

    itemRepository.save(pubItem);

  }



  private void importContexts() throws Exception {
    URI uri =
        new URIBuilder("https://qa-coreservice.mpdl.mpg.de/ir/contexts").addParameter(
            "maximumRecords", "5000").build();
    System.out.println(uri.toString());
    String contextXml = Request.Get(uri).execute().returnContent().asString(StandardCharsets.UTF_8);

    // System.out.println(singleOu);

    try {
      SearchRetrieveResponseVO<SearchResponse, de.mpg.mpdl.inge.model.valueobjects.ContextVO> contextList =
          XmlTransformingService.transformToSearchRetrieveResponse(contextXml);


      for (SearchRetrieveRecordVO<de.mpg.mpdl.inge.model.valueobjects.ContextVO> rec : contextList
          .getRecords()) {
        saveContext(rec.getData());
      }



    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }


  private void saveContext(de.mpg.mpdl.inge.model.valueobjects.ContextVO context) throws Exception {
    try {
      ContextDbVO newVo = transformToNew(context);
      System.out.println("Saving " + newVo.getObjectId());
      contextRepository.save(newVo);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }



  private void importAffs() throws Exception {
    URI uri =
        new URIBuilder("https://qa-coreservice.mpdl.mpg.de/oum/organizational-units").addParameter(
            "query", "\"/id\"=\"e*\" not \"/parents/parent/id\">\"''\"").build();
    System.out.println(uri.toString());
    String ouXml = Request.Get(uri).execute().returnContent().asString(StandardCharsets.UTF_8);


    try {
      SearchRetrieveResponseVO<?, AffiliationVO> ouList =
          XmlTransformingService.transformToSearchRetrieveResponseOrganizationVO(ouXml);

      saveOuList(ouList);

      uri = new URIBuilder("https://qa-coreservice.mpdl.mpg.de/oum/organizational-units").build();
      System.out.println(uri.toString());
      ouXml = Request.Get(uri).execute().returnContent().asString(StandardCharsets.UTF_8);
      ouList = XmlTransformingService.transformToSearchRetrieveResponseOrganizationVO(ouXml);


      for (SearchRetrieveRecordVO<AffiliationVO> affRecord : ouList.getRecords()) {
        updateOUWithPredecessors(affRecord.getData());
      }



    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }


  private void importUsers() throws Exception {
    URI uri =
        new URIBuilder("https://qa-coreservice.mpdl.mpg.de/aa/user-accounts")
            .addParameter("maximumRecords", String.valueOf(5000))
            .addParameter("startRecord", String.valueOf(1)).build();
    final HttpGet request = new HttpGet(uri);
    HttpResponse response = httpClientWithEscidocCookie.execute(request);
    String xml = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
    // System.out.println(xml);

    SearchRetrieveResponseVO<SearchResponse, AccountUserVO> userList =
        XmlTransformingService.transformToSearchRetrieveResponseAccountUser(xml);
    for (SearchRetrieveRecordVO<AccountUserVO> accountUser : userList.getRecords()) {

      String objectId = accountUser.getData().getReference().getObjectId();
      objectId = objectId.substring(objectId.lastIndexOf("/") + 1, objectId.length());

      uri =
          new URIBuilder("https://qa-coreservice.mpdl.mpg.de/aa/user-account/" + objectId
              + "/resources/current-grants").addParameter("maximumRecords", String.valueOf(5000))
              .addParameter("startRecord", String.valueOf(1)).build();
      final HttpGet requestGrant = new HttpGet(uri);
      HttpResponse responseGrant = httpClientWithEscidocCookie.execute(requestGrant);
      String grantXml = EntityUtils.toString(responseGrant.getEntity(), StandardCharsets.UTF_8);
      List<GrantVO> grantList = XmlTransformingService.transformToGrantVOList(grantXml);

      uri =
          new URIBuilder("https://qa-coreservice.mpdl.mpg.de/aa/user-account/" + objectId
              + "/resources/attributes").addParameter("maximumRecords", String.valueOf(5000))
              .addParameter("startRecord", String.valueOf(1)).build();
      final HttpGet requestAttrs = new HttpGet(uri);
      HttpResponse responseAttrs = httpClientWithEscidocCookie.execute(requestAttrs);
      String attrXml = EntityUtils.toString(responseAttrs.getEntity(), StandardCharsets.UTF_8);
      List<UserAttributeVO> userAttrList =
          XmlTransformingService.transformToUserAttributesList(attrXml);



      // System.out.println(srrList);

      // List<GrantVO> grantList = srrList.getRecords().stream().map(srr ->
      // srr.getData()).collect(Collectors.toList());
      System.out.println("Saving user" + accountUser.getData().getName() + " - "
          + accountUser.getData().getReference().getObjectId());
      try {
        userRepository.save(transformToNew(accountUser.getData(), grantList, userAttrList));
      } catch (Exception e) {
        e.printStackTrace();
      }



    }

  }



  private void saveOuList(SearchRetrieveResponseVO<SearchResponse, AffiliationVO> srr)
      throws Exception {
    if (srr.getNumberOfRecords() > 0) {
      for (SearchRetrieveRecordVO<AffiliationVO> affRecord : srr.getRecords()) {

        String href = affRecord.getData().getReference().getObjectId();
        String objectId = href.substring(href.lastIndexOf("/") + 1, href.length());

        saveOuWithoutPredecessor(affRecord.getData());

        URI uri =
            new URIBuilder("https://qa-coreservice.mpdl.mpg.de/oum/organizational-units")
                .addParameter("query", "\"/parents/parent/id\"=\"" + objectId + "\"").build();
        String ouXml = Request.Get(uri).execute().returnContent().asString(StandardCharsets.UTF_8);

        SearchRetrieveResponseVO<SearchResponse, AffiliationVO> ouList =
            XmlTransformingService.transformToSearchRetrieveResponseOrganizationVO(ouXml);
        saveOuList(ouList);

      }
    }
  }


  private void saveOuWithoutPredecessor(AffiliationVO affVo) throws Exception {

    AffiliationDbVO newVo = transformToNew(affVo);
    newVo.getPredecessorAffiliations().clear();
    System.out.println("Saving " + newVo.getObjectId());
    orgRepository.save(newVo);
    updateLaterAffs.add(affVo);

  }

  private void updateOUWithPredecessors(AffiliationVO affVo) {
    String id = changeId("ou", affVo.getReference().getObjectId());
    if (!affVo.getPredecessorAffiliations().isEmpty()) {
      AffiliationDbVO newVo = orgRepository.findOne(id);

      // newVo.setPredecessorAffiliations(new ArrayList<AffiliationRO>());
      for (AffiliationRO oldAffRo : affVo.getPredecessorAffiliations()) {
        AffiliationDbRO newAffRo = new AffiliationDbRO();
        newAffRo.setObjectId(changeId("ou", oldAffRo.getObjectId()));
        newAffRo.setName(oldAffRo.getTitle());

        newVo.getPredecessorAffiliations().add(newAffRo);
      }
      System.out.println("Updating " + newVo.getObjectId());
      orgRepository.save(newVo);
    }


  }


  private void importPubItems() throws Exception
  {
    String contentModelId = "escidoc:persistent4";
    
 
    int limit = 5000;
    int startRecord = 1;
    int allRecords = Integer.MAX_VALUE;
    
    while(allRecords > startRecord + limit)
    {
      System.out.println("Searching from " + startRecord + " to " + (startRecord + limit));
      URI uri = new URIBuilder("https://qa-coreservice.mpdl.mpg.de/ir/items").addParameter("query", "\"/properties/content-model/id\"=\"" + contentModelId +  "\"").addParameter("maximumRecords", String.valueOf(limit)).addParameter("startRecord", String.valueOf(startRecord)).build();
      final HttpGet request = new HttpGet(uri);
      HttpResponse response = httpClientWithEscidocCookie.execute(request);
      String xml = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
      //System.out.println(xml);
      
      SearchRetrieveResponseVO<SearchResponse, PubItemVO> pubItemList = XmlTransformingService.transformToSearchRetrieveResponse(xml);
      
      allRecords = pubItemList.getNumberOfRecords();
      startRecord = startRecord + limit;
      System.out.println("Found " + allRecords + "items.");
      
      //pubItemList.getRecords().stream().map(i->i.getData()).collect(Collectors.toList()).stream().parallel().forEach(i -> sa);;
      pubItemList.getRecords().parallelStream().forEach(i -> saveAllVersionsOfPubItem(i.getData()));
      
      /*
      for(SearchRetrieveRecordVO<PubItemVO> rec : pubItemList.getRecords())
      {

      }
      */
      
    }

  }

  private void saveAllVersionsOfPubItem(PubItemVO pubItemVo) {
    int versionNumber = pubItemVo.getLatestVersion().getVersionNumber();

    for (int i = versionNumber; i > 0; i--) {
      try {
        String href = pubItemVo.getVersion().getObjectId() + ":" + i;
        String objectId = href.substring(href.lastIndexOf("/") + 1, href.length());
        System.out.println("Getting " + objectId);
        URI itemUri =
            new URIBuilder("https://qa-coreservice.mpdl.mpg.de/ir/item/" + objectId).build();
        final HttpGet requestItem = new HttpGet(itemUri);
        HttpResponse itemResponse = httpClientWithEscidocCookie.execute(requestItem);
        String itemXml = EntityUtils.toString(itemResponse.getEntity(), StandardCharsets.UTF_8);
        // System.out.println(itemXml);

        PubItemVO item = XmlTransformingService.transformToPubItem(itemXml);

        savePubItem(item);
      } catch (Exception e) {

        e.printStackTrace();
      }

    }

  }


  private void savePubItem(PubItemVO pubItem) throws Exception {
    try {
      PubItemVersionDbVO newVo = transformToNew(pubItem);
      System.out.println("Saving " + newVo.getObjectId() + "_" + newVo.getVersionNumber());
      itemRepository.save(newVo);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  private static AffiliationDbVO transformToNew(
      de.mpg.mpdl.inge.model.valueobjects.AffiliationVO affVo) {
    AccountUserDbRO owner = new AccountUserDbRO();
    AccountUserDbRO modifier = new AccountUserDbRO();

    owner.setObjectId(changeId("user", affVo.getCreator().getObjectId()));
    owner.setName(affVo.getCreator().getTitle());
    modifier.setObjectId(changeId("user", affVo.getModifiedBy().getObjectId()));
    modifier.setName(affVo.getModifiedBy().getTitle());

    AffiliationDbVO newAff = new AffiliationDbVO();
    newAff.setCreationDate(affVo.getCreationDate());

    newAff.setCreator(owner);
    newAff.setHasChildren(affVo.getHasChildren());
    newAff.setLastModificationDate(affVo.getLastModificationDate());
    newAff.setMetadata(affVo.getDefaultMetadata());
    newAff.setModifier(modifier);
    newAff.setName(affVo.getDefaultMetadata().getName());
    newAff.setObjectId(changeId("ou", affVo.getReference().getObjectId()));


    for (AffiliationRO oldAffRo : affVo.getPredecessorAffiliations()) {
      AffiliationDbRO newAffRo = new AffiliationDbRO();
      newAffRo.setObjectId(changeId("ou", oldAffRo.getObjectId()));
      newAffRo.setName(oldAffRo.getTitle());
      newAff.getPredecessorAffiliations().add(newAffRo);
    }
    for (AffiliationRO oldAffRo : affVo.getParentAffiliations()) {
      AffiliationDbRO newAffRo = new AffiliationDbRO();
      newAffRo.setObjectId(changeId("ou", oldAffRo.getObjectId()));
      newAffRo.setName(oldAffRo.getTitle());
      newAff.setParentAffiliation(newAffRo);
    }

    newAff.setPublicStatus(AffiliationDbVO.State.valueOf(affVo.getPublicStatus().toUpperCase()));
    return newAff;


  }



  private static ContextDbVO transformToNew(ContextVO contextVo) {
    AccountUserDbRO owner = new AccountUserDbRO();
    AccountUserDbRO modifier = new AccountUserDbRO();

    owner.setObjectId(changeId("user", contextVo.getCreator().getObjectId()));
    owner.setName(contextVo.getCreator().getTitle());

    modifier.setObjectId(changeId("user", contextVo.getModifiedBy().getObjectId()));
    modifier.setName(contextVo.getModifiedBy().getTitle());

    ContextDbVO newContext = new ContextDbVO();
    newContext.setCreator(owner);
    newContext.setCreationDate(contextVo.getCreationDate());
    newContext.setLastModificationDate(contextVo.getLastModificationDate());
    newContext.setModifier(modifier);
    newContext.setDescription(contextVo.getDescription());
    newContext.setName(contextVo.getName());
    newContext.setObjectId(changeId("ctx", contextVo.getReference().getObjectId()));

    newContext.setState(ContextDbVO.State.valueOf(contextVo.getState().name()));

    newContext.setType(contextVo.getType());

    newContext.setAdminDescriptor(contextVo.getAdminDescriptor());


    for (AffiliationRO oldAffRo : contextVo.getResponsibleAffiliations()) {
      AffiliationDbRO newAffRo = new AffiliationDbRO();
      newAffRo.setObjectId(changeId("ou", changeId("ou", oldAffRo.getObjectId())));
      newAffRo.setName(oldAffRo.getTitle());
      newContext.getResponsibleAffiliations().add(newAffRo);
    }



    return newContext;


  }


  private static PubItemVersionDbVO transformToNew(
      de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO itemVo) {
    AccountUserDbRO owner = new AccountUserDbRO();
    AccountUserDbRO modifier = new AccountUserDbRO();

    owner.setObjectId(changeId("user", itemVo.getOwner().getObjectId()));
    owner.setName(itemVo.getOwner().getTitle());

    modifier.setObjectId(changeId("user", itemVo.getVersion().getModifiedByRO().getObjectId()));
    modifier.setName(itemVo.getVersion().getModifiedByRO().getTitle());


    PubItemVersionDbVO newPubItem = new PubItemVersionDbVO();
    for (de.mpg.mpdl.inge.model.valueobjects.FileVO oldFile : itemVo.getFiles()) {

      AccountUserDbRO fileOwner = new AccountUserDbRO();
      // AccountUserRO fileModifier = new AccountUserRO();

      fileOwner.setObjectId(changeId("user", oldFile.getCreatedByRO().getObjectId()));
      fileOwner.setName(oldFile.getCreatedByRO().getTitle());

      // fileModifier.setObjectId(changeId("user", oldFile.getM.getObjectId()));
      // fileModifier.setName(itemVo.getVersion().getModifiedByRO().getTitle());

      FileDbVO file = new FileDbVO();
      file.setChecksum(oldFile.getChecksum());
      file.setChecksumAlgorithm(ChecksumAlgorithm.valueOf(oldFile.getChecksumAlgorithm().name()));
      file.setContent(oldFile.getContent());
      file.setContentCategory(oldFile.getContentCategory());
      file.setCreationDate(oldFile.getCreationDate());
      file.setCreator(fileOwner);
      file.setDescription(oldFile.getDescription());
      file.setLastModificationDate(oldFile.getLastModificationDate());
      file.setMetadata(oldFile.getDefaultMetadata());
      file.setMimeType(oldFile.getMimeType());
      // file.setModifier(oldFile.getM);
      file.setName(oldFile.getName());
      file.setObjectId(changeId("file", oldFile.getReference().getObjectId()));
      file.setPid(oldFile.getPid());
      file.setStorage(Storage.valueOf(oldFile.getStorage().name()));
      file.setVisibility(Visibility.valueOf(oldFile.getVisibility().name()));

      newPubItem.getFiles().add(file);
    }



    newPubItem.setLastMessage(itemVo.getVersion().getLastMessage());
    newPubItem.setMetadata(itemVo.getMetadata());
    newPubItem.setModificationDate(itemVo.getVersion().getModificationDate());
    newPubItem.setModifiedBy(owner);
    newPubItem.setObjectId(changeId("item", itemVo.getVersion().getObjectId()));
    newPubItem.setState(PubItemVersionDbVO.State.valueOf(itemVo.getVersion().getState().name()));
    newPubItem.setVersionNumber(itemVo.getVersion().getVersionNumber());
    newPubItem.setVersionPid(itemVo.getVersion().getPid());


    PubItemObjectDbVO pubItemObject = new PubItemObjectDbVO();
    newPubItem.setObject(pubItemObject);

    ContextDbRO context = new ContextDbRO();
    context.setObjectId(changeId("ctx", itemVo.getContext().getObjectId()));
    pubItemObject.setContext(context);

    pubItemObject.setCreationDate(itemVo.getCreationDate());
    pubItemObject.setLastModificationDate(itemVo.getLatestVersion().getModificationDate());


    if (itemVo.getLatestRelease() != null) {
      if (itemVo.getLatestRelease().getVersionNumber() == itemVo.getVersion().getVersionNumber()) {
        pubItemObject.setLatestRelease(newPubItem);
      } else if (itemVo.getLatestRelease().getVersionNumber() > itemVo.getVersion()
          .getVersionNumber()) {
        PubItemDbRO latestRelease = new PubItemDbRO();
        latestRelease.setObjectId(changeId(ID_PREFIX.ITEM.getPrefix(), itemVo.getLatestRelease()
            .getObjectId()));
        latestRelease.setVersionNumber(itemVo.getLatestRelease().getVersionNumber());
        pubItemObject.setLatestRelease(latestRelease);
      }


    }


    if (itemVo.getLatestVersion().getVersionNumber() == itemVo.getVersion().getVersionNumber()) {
      pubItemObject.setLatestVersion(newPubItem);
    } else {
      PubItemDbRO latestVersion = new PubItemDbRO();
      latestVersion.setObjectId(changeId(ID_PREFIX.ITEM.getPrefix(), itemVo.getLatestVersion()
          .getObjectId()));
      latestVersion.setVersionNumber(itemVo.getLatestVersion().getVersionNumber());
      pubItemObject.setLatestVersion(latestVersion);
    }


    pubItemObject.setLocalTags(itemVo.getLocalTags());
    pubItemObject.setObjectId(changeId("item", itemVo.getVersion().getObjectId()));
    pubItemObject.setOwner(owner);
    pubItemObject.setPid(itemVo.getPid());
    pubItemObject
        .setPublicStatus(PubItemVersionDbVO.State.valueOf(itemVo.getPublicStatus().name()));
    pubItemObject.setPublicStatusComment(itemVo.getPublicStatusComment());

    return newPubItem;


  }


  private AccountUserDbVO transformToNew(AccountUserVO oldAccountUserVO, List<GrantVO> grants,
      List<UserAttributeVO> attributes) {

    AccountUserDbRO owner = new AccountUserDbRO();
    AccountUserDbRO modifier = new AccountUserDbRO();

    owner.setObjectId(changeId("user", oldAccountUserVO.getCreator().getObjectId()));
    owner.setName(oldAccountUserVO.getCreator().getTitle());

    modifier.setObjectId(changeId("user", oldAccountUserVO.getModifiedBy().getObjectId()));
    modifier.setName(oldAccountUserVO.getModifiedBy().getTitle());

    AccountUserDbVO newAccountUser = new AccountUserDbVO();

    newAccountUser.setActive(oldAccountUserVO.isActive());
    if (oldAccountUserVO.getAffiliations() != null && oldAccountUserVO.getAffiliations().size() > 0) {
      AffiliationDbRO affRO = new AffiliationDbRO();
      affRO.setObjectId(changeId("ou", oldAccountUserVO.getAffiliations().get(0).getObjectId()));
      newAccountUser.setAffiliation(affRO);
    }


    newAccountUser.setCreationDate(oldAccountUserVO.getCreationDate());
    newAccountUser.setCreator(owner);
    newAccountUser.setEmail(oldAccountUserVO.getEmail());

    if (grants != null) {


      for (GrantVO grant : grants) {
        grant.setGrantedTo(null);
        grant.setLastModificationDate(null);
        grant.setReference(null);
        if (grant.getObjectRef() != null) {
          if (grant.getObjectRef().contains("context")) {
            grant.setObjectRef(changeId("ctx", grant.getObjectRef()));
          } else if (grant.getObjectRef().contains("organizational-unit")) {
            grant.setObjectRef(changeId("ou", grant.getObjectRef()));
          } else {
            System.out.println("Unknown grant object: " + grant.getObjectRef());
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
          System.out.println("Unknown role: " + grant.getRole());
        }


      }
    }

    if (attributes != null) {
      for (UserAttributeVO attr : attributes) {

        if (attr.getName().equals("o")) {
          AffiliationDbRO affRO = new AffiliationDbRO();
          affRO.setObjectId(changeId("ou", attr.getValue()));
          newAccountUser.setAffiliation(affRO);
        }

        if (attr.getName().equals("email")) {
          newAccountUser.setEmail(attr.getValue());
        }

      }
    }

    newAccountUser.setGrantList(grants);
    newAccountUser.setLastModificationDate(oldAccountUserVO.getLastModificationDate());
    newAccountUser.setLoginname(oldAccountUserVO.getUserid());
    newAccountUser.setModifier(modifier);
    newAccountUser.setName(oldAccountUserVO.getName());
    newAccountUser.setObjectId(changeId("user", oldAccountUserVO.getReference().getObjectId()));

    return newAccountUser;


  }

  private void importLogins() throws Exception {

    PGSimpleDataSource dataSource = new PGSimpleDataSource();
    dataSource.setUser("postgres");
    dataSource.setPassword(PropertyReader.getProperty("inge.database.user.password"));
    dataSource.setDatabaseName("escidoc-core");
    dataSource.setServerName("srv02.mpdl.mpg.de");
    dataSource.setPortNumber(5432);

    Connection conn = dataSource.getConnection();

    ResultSet res =
        conn.createStatement().executeQuery("SELECT loginname,password FROM aa.user_account;");

    PasswordEncoder pe = new BCryptPasswordEncoder();

    while (res.next()) {

      System.out.println("Saving " + res.getString(1));
      try {
        userLoginRepository.insertLogin(res.getString(1), pe.encode(res.getString(2)));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }


  }



  private static String changeId(String prefix, String href) {
    return href.substring(href.lastIndexOf("/") + 1, href.length())
        .replaceAll("escidoc:", prefix + "_").replaceAll(":", "_");
  }


}
