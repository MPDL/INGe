package pubman_logic;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mpg.mpdl.inge.model.referenceobjects.ContextRO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.service.pubman.ContextService;
import de.mpg.mpdl.inge.service.pubman.OrganizationService;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.pubman.UserAccountService;
import de.mpg.mpdl.inge.service.pubman.impl.ContextServiceDbImpl;
import de.mpg.mpdl.inge.service.pubman.impl.OrganizationServiceDbImpl;
import de.mpg.mpdl.inge.service.spring.AppConfigPubmanLogic;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfigPubmanLogic.class)
public class TestPubmanLogic {

  @Autowired
  private PubItemService pubItemService;

  @Autowired
  private ContextService contextService;

  @Autowired
  private OrganizationService organizationService;


  @Autowired
  private UserAccountService userAccountService;

  @Test
  @Ignore
  public void test() throws Exception {


    String token = userAccountService.login("boosen", "boosen");


    PubItemVO pubItemVO = new PubItemVO();
    pubItemVO.setContext(new ContextRO("pure_28054"));
    MdsPublicationVO mds = new MdsPublicationVO();
    mds.setFreeKeywords("xyz");
    mds.setTitle("First Test of Service");
    pubItemVO.setMetadata(mds);

    long start = System.currentTimeMillis();
    PubItemVO createdPubItem = pubItemService.create(pubItemVO, token);
    pubItemService.delete(createdPubItem.getVersion().getObjectId(), token);

    long time = System.currentTimeMillis() - start;

    System.out.println("Needed time " + time);
  }



  @Test
  @Ignore
  public void testSearch() throws Exception {
    String token = userAccountService.login("boosen", "boosen");

    System.out.println(token);
    QueryBuilder testQuery = QueryBuilders.matchQuery("metadata.title", "test");
    SearchRetrieveRequestVO<QueryBuilder> srr =
        new SearchRetrieveRequestVO<QueryBuilder>(testQuery);
    SearchRetrieveResponseVO<PubItemVO> resp = pubItemService.search(srr, null);
    System.out.println("Found: " + resp.getNumberOfRecords() + " records");
  }

  @Test
  @Ignore
  public void testReindexContext() throws Exception {

    ((ContextServiceDbImpl) contextService).reindex();

  }

  @Test
  @Ignore
  public void testReindexOu() throws Exception {

    ((OrganizationServiceDbImpl) organizationService).reindex();

  }

  @Test
  @Ignore
  public void testReindexItems() throws Exception {

    pubItemService.reindex();

  }

  @Test
  @Ignore
  public void testGet() throws Exception {
    pubItemService.get("item_3000005", null);
  }

}
