package pubman_logic;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.hibernate.query.Query;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import de.mpg.mpdl.inge.es.connector.ElasticSearchClientProvider;
import de.mpg.mpdl.inge.model.db.valueobjects.PubItemObjectDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.json.util.JsonObjectMapperFactory;
import de.mpg.mpdl.inge.model.referenceobjects.AffiliationRO;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO.PredefinedRoles;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsOrganizationalUnitDetailsVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.service.pubman.ContextService;
import de.mpg.mpdl.inge.service.pubman.FileService;
import de.mpg.mpdl.inge.service.pubman.OrganizationService;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.pubman.UserAccountService;
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl;
import de.mpg.mpdl.inge.service.spring.AppConfigPubmanLogic;
import de.mpg.mpdl.inge.util.PropertyReader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfigPubmanLogic.class)
public class PubmanLogicTest {

  @Autowired
  private PubItemService pubItemService;

  @Autowired
  private ContextService contextService;

  @Autowired
  private OrganizationService organizationService;


  @Autowired
  private UserAccountService userAccountService;

  @PersistenceContext
  private EntityManager entityManager;

  // @Autowired
  // private ItemRepository itemRepository;

  // @Autowired
  // private AuthorizationService authorizationService;

  // @Autowired
  // private PubItemDaoEs pubItemDao;

  // @Autowired
  // private OrganizationDaoEs orgDao;

  @Autowired
  ElasticSearchClientProvider client;

  @Autowired
  private FileService fileService;

  @Ignore
  @Test
  public void test() throws Exception {



    List<String> idList = organizationService.getIdPath("ou_1753285", null);

    System.out.println(idList);


    /*
     * 
     * Map<String, ElasticSearchIndexField> map = orgDao.getIndexFields(); for(Entry<String,
     * ElasticSearchIndexField> entry : map.entrySet()) {
     * System.out.println(entry.getValue().toString()); }
     */



    /*
     * PubItemVO item = pubItemService.get("item_3002712_1", null);
     * 
     * StringWriter w = new StringWriter();
     * JsonObjectMapperFactory.getObjectMapper().writerFor(PubItemVO.class).writeValue(w, item);
     * System.out.println(w.toString());
     */

    /*
     * String token = userAccountService.login("boosen", "boosen"); AccountUserVO userAccount =
     * authorizationService.checkLoginRequired(token);
     * 
     * AffiliationVO affv0 = organizationService.get("ou_1113557", token);
     * System.out.println("HasChildren :" + affv0.getHasChildren()); affv0 =
     * organizationService.get("ou_persistent13", token); System.out.println("HasChildren :" +
     * affv0.getHasChildren());
     * 
     * QueryBuilder testQuery = QueryBuilders.matchQuery("defaultMetadata.name", "test");
     * SearchRetrieveRequestVO srr = new SearchRetrieveRequestVO(testQuery);
     * SearchRetrieveResponseVO<AffiliationVO> resp = organizationService.search(srr, null);
     * System.out.println("Found: " + resp.getNumberOfRecords() + " records");
     */
    /*
     * 
     * QueryBuilder matchQuery = QueryBuilders.matchQuery("_all", "test"); QueryBuilder aaQuery =
     * authorizationService.modifyQueryForAa("de.mpg.mpdl.inge.service.pubman.PubItemService",
     * matchQuery, userAccount); System.out.println(aaQuery.toString());
     * 
     * pubItemService.get("item_3000007_5", token);
     * 
     * pubItemService.getVersionHistory("item_3000007", null);
     */

  }



  @Test
  @Ignore
  public void testSearch() throws Exception {
    String token = userAccountService.login("boosen", "boosen");

    System.out.println(token);
    QueryBuilder testQuery = QueryBuilders.matchQuery("metadata.title", "test");
    SearchRetrieveRequestVO srr = new SearchRetrieveRequestVO(testQuery);
    SearchRetrieveResponseVO<PubItemVO> resp = pubItemService.search(srr, null);
    System.out.println("Found: " + resp.getNumberOfRecords() + " records");
  }

  @Test
  @Ignore
  public void testReindexContext() throws Exception {

    contextService.reindex();

  }

  @Test
  @Ignore
  public void testReindexOu() throws Exception {

    organizationService.reindex();

  }

  @Test
  @Ignore
  public void testReindexItems() throws Exception {

    pubItemService.reindex();

  }

  @Test
  @Ignore
  public void testReindexUsers() throws Exception {

    userAccountService.reindex();

  }

  @Test
  @Ignore
  public void testGet() throws Exception {
    Query<de.mpg.mpdl.inge.model.db.valueobjects.PubItemObjectDbVO> query =
        (Query<de.mpg.mpdl.inge.model.db.valueobjects.PubItemObjectDbVO>) entityManager
            .createQuery("SELECT itemObject FROM PubItemObjectVO itemObject");
    // query.setHint("org.hibernate.cacheable", "true");
    // query.addQueryHint("org.hibernate.cacheable=true");
    query.setMaxResults(500);
    query.setReadOnly(true);
    query.setFetchSize(500);
    query.setCacheable(false);
    // ScrollableResults results = query.scroll(ScrollMode.FORWARD_ONLY);
    List<PubItemObjectDbVO> resultList = query.list();

    entityManager.clear();

    // while (results.next()) {
    for (PubItemObjectDbVO pi : resultList) {
      try {

        de.mpg.mpdl.inge.model.db.valueobjects.PubItemObjectDbVO object =
            (de.mpg.mpdl.inge.model.db.valueobjects.PubItemObjectDbVO) pi;

        try {
          long time = System.currentTimeMillis();
          pubItemService.get(object.getObjectId() + "_" + "1", null);
          System.out.println("time 1st findOne: " + (System.currentTimeMillis() - time));
          time = System.currentTimeMillis();

          pubItemService.get(object.getObjectId() + "_" + "1", null);
          System.out.println("time 2nd findOne: " + (System.currentTimeMillis() - time));
        } catch (Exception e) {
          System.out.println("Not allowed: " + e.getMessage());
        }


      } catch (Exception e) {

      }


    }
  }

  @Test
  @Ignore
  public void testOpenContext() throws Exception {

    String token = userAccountService.login("haarlaender", "demo");
    AccountUserVO userAccount = userAccountService.get("user_104231", token);

    StringWriter sw = new StringWriter();
    JsonObjectMapperFactory.getObjectMapper().writerFor(AccountUserVO.class)
        .writeValue(sw, userAccount);
    System.out.println(sw.toString());



    /*
     * String token = userAccountService.login("frank", "mlehliW"); AccountUserVO userAccount =
     * authorizationService.checkLoginRequired(token);
     * 
     * contextService.open("ctx_3000022", token);
     */


  }

  @Test
  @Ignore
  public void createUser() throws Exception {



    String adminUsername = PropertyReader.getProperty("framework.admin.username");
    String adminPass = PropertyReader.getProperty("framework.admin.password");
    String token = userAccountService.login(adminUsername, adminPass);



    // userAccountService.delete("ctx_3000055", token);

    AccountUserVO user = new AccountUserVO();
    user.setEmail("a@b.de");
    user.setName("Test Moderator");
    user.setUserid("efwfeewfwe");
    user.setPassword("michael");

    AffiliationRO aff = new AffiliationRO();
    // aff.setObjectId("ou_persistent25");
    aff.setObjectId("ou_blaaablub");
    user.getAffiliations().add(aff);

    GrantVO grant = new GrantVO();
    grant.setRole(PredefinedRoles.MODERATOR.frameworkValue());
    grant.setObjectRef("ctx_2322554");

    user.getGrants().add(grant);



    AccountUserVO userAccount = userAccountService.create(user, token);

    /*
     * 
     * 
     * 
     * userAccountService.addGrants(userAccount.getReference().getObjectId(), new GrantVO[] {grant},
     * token);
     */

  }


  private static void validateLoginname(String loginname) throws IngeTechnicalException {
    if (loginname == null || loginname.trim().isEmpty()) {
      throw new IngeTechnicalException("A loginname (userId) has to be provided");
    } else if (!loginname.matches("^[a-zA-Z0-9@_\\-\\.]{4,}$")) {
      throw new IngeTechnicalException(
          "Invalid loginname (userId). Loginname  must consist of an email adress or at least 4 characters, no whitespaces, no special characters");
    }

  }



  public static void main(String[] args) throws Exception {
    Date now = new Date();
    System.out.println(now.getTime());
    System.out.println(now.getTimezoneOffset());
    System.out.println(now.toGMTString());
    System.out.println(now.toString());
    System.out.println(now.toLocaleString());


    JavaType jt =
        TypeFactory.defaultInstance().constructRawMapLikeType(MdsOrganizationalUnitDetailsVO.class);
    System.out.println(jt.getRawClass());

    validateLoginname("mark");
  }

  @Test
  @Ignore
  public void testOai() throws Exception {
    QueryBuilder qb = QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_VERSION_STATE, "RELEASED");
    SearchRetrieveRequestVO srr = new SearchRetrieveRequestVO(qb, 50, 0, null);
    SearchRetrieveResponseVO<PubItemVO> response = pubItemService.search(srr, null);

    int size = response.getRecords().size();

    System.out.println("Anzahl: " + size);

    for (SearchRetrieveRecordVO<PubItemVO> searchRetrieveRecordVO : response.getRecords()) {
      PubItemVO pubItemVO = searchRetrieveRecordVO.getData();
      String s = XmlTransformingService.transformToItem(pubItemVO);
      this.fileService.createFile(new ByteArrayInputStream(s.getBytes()), pubItemVO.getVersion()
          .getObjectIdAndVersion() + ".xml");
    }
  }

  @Test
  @Ignore
  public void testOaiScrollable() throws Exception {
    int count = 0;
    // int countIntervall = 0;

    QueryBuilder qb = QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_VERSION_STATE, "RELEASED");

    SearchResponse scrollResp =
        this.client.getClient().prepareSearch(PropertyReader.getProperty("item_index_name"))
            .addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC) //
            .setScroll(new TimeValue(60000)) // 1 Minute for keeping search context alive
            .setQuery(qb) //
            .setSize(1000) // max of 1000 hits will be returned for each scroll
            .get();
    // Scroll until no hits are returned

    ObjectMapper mapper = JsonObjectMapperFactory.getObjectMapper();

    do {
      for (SearchHit hit : scrollResp.getHits().getHits()) {
        count++;
        PubItemVO pubItemVO = mapper.readValue(hit.getSourceAsString(), PubItemVO.class);
        String s = XmlTransformingService.transformToItem(pubItemVO);
        this.fileService.createFile(new ByteArrayInputStream(s.getBytes()), pubItemVO.getVersion()
            .getObjectIdAndVersion() + ".xml");
      }

      // countIntervall++;

      System.out.println(count);
      scrollResp = this.client.getClient().prepareSearchScroll(scrollResp.getScrollId()) //
          .setScroll(new TimeValue(60000)) //
          .execute() //
          .actionGet();
    } while (scrollResp.getHits().getHits().length != 0); // && countIntervall < 5);
  }
}
