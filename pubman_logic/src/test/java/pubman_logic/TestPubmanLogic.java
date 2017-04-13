package pubman_logic;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mpg.mpdl.inge.es.spring.AppConfig;
import de.mpg.mpdl.inge.model.referenceobjects.ContextRO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.service.identifier.IdentifierProviderServiceImpl;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.pubman.UserAccountService;
import de.mpg.mpdl.inge.service.spring.AppConfigPubmanLogic;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfigPubmanLogic.class)
public class TestPubmanLogic {

  @Autowired
  private PubItemService pubItemService;

  @Autowired
  private IdentifierProviderServiceImpl idProvider;

  @Autowired
  private UserAccountService userAccountService;

  @Test
  public void test() throws Exception {

    String token = userAccountService.login("boosen", "boosen");


    PubItemVO pubItemVO = new PubItemVO();
    pubItemVO.setContext(new ContextRO("pure_28054"));
    MdsPublicationVO mds = new MdsPublicationVO();
    mds.setFreeKeywords("xyz");
    mds.setTitle("First Test of Service");
    pubItemVO.setMetadata(mds);

    pubItemService.create(pubItemVO, token);
    pubItemService.create(pubItemVO, token);



  }

  @Test
  @Ignore
  public void testIdentifierProvider() {
    System.out.println(idProvider.getNewId());
  }

}
