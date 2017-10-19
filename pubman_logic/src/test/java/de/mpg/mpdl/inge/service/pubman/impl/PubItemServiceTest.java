package de.mpg.mpdl.inge.service.pubman.impl;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.referenceobjects.ContextRO;
import de.mpg.mpdl.inge.model.referenceobjects.ItemRO;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.pubman.UserAccountService;
import de.mpg.mpdl.inge.service.spring.AppConfigPubmanLogicTest;
import de.mpg.mpdl.inge.util.PropertyReader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfigPubmanLogicTest.class})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PubItemServiceTest {

  private static String CTX_MPG_ID = "ctx_2322554";

  @Autowired
  UserAccountService userAccountService;

  @Autowired
  PubItemService pubItemService;

  @Test
  public void createByDepositor() {

    String authenticationToken = loginDepositor();

    PubItemVO pubItemVO = getPubItemVO();
    try {
      pubItemVO = pubItemService.create(pubItemVO, authenticationToken);
    } catch (IngeTechnicalException | AuthenticationException | AuthorizationException
        | IngeApplicationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    assertTrue(pubItemVO != null);
    assertTrue(pubItemVO.getCreationDate() != null);
    assertTrue(pubItemVO.getCreationDate() != null);
    assertTrue(pubItemVO.getContext() != null
        && pubItemVO.getContext().getObjectId().equals(CTX_MPG_ID));
    assertTrue(pubItemVO.getLatestVersion().getModificationDate() != null);
    assertTrue(pubItemVO.getLatestVersion().getState().equals(ItemVO.State.PENDING));
  }

  @Test(expected = AuthorizationException.class)
  public void createByModerator() throws Exception {

    String authenticationToken = loginModerator();

    PubItemVO pubItemVO = getPubItemVO();
    pubItemVO = pubItemService.create(pubItemVO, authenticationToken);
  }

  @Test
  public void submitPubItem() {
    fail("Not yet implemented");
  }

  @Test
  public void releasePubItem() {
    fail("Not yet implemented");
  }

  @Test
  public void withdrawPubItem() {
    fail("Not yet implemented");
  }

  @Test
  public void revisePubItem() {
    fail("Not yet implemented");
  }

  @Test
  public void getVersionHistory() {
    fail("Not yet implemented");
  }

  private String loginDepositor() {
    String username = PropertyReader.getProperty("inge.depositor.loginname");
    String password = PropertyReader.getProperty("inge.depositor.password");
    String token = null;
    try {
      token = userAccountService.login(username, password);
    } catch (IngeTechnicalException | AuthenticationException | AuthorizationException
        | IngeApplicationException e) {
      e.printStackTrace();
      fail("Caugh exception <" + e.getClass().getSimpleName() + ">");
    }
    return token;
  }

  private String loginModerator() {
    String username = PropertyReader.getProperty("inge.moderator.loginname");
    String password = PropertyReader.getProperty("inge.moderator.password");
    String token = null;
    try {
      token = userAccountService.login(username, password);
    } catch (IngeTechnicalException | AuthenticationException | AuthorizationException
        | IngeApplicationException e) {
      e.printStackTrace();
      fail("Caugh exception <" + e.getClass().getSimpleName() + ">");
    }
    return token;
  }

  private PubItemVO getPubItemVO() {
    PubItemVO pubItemVO = new PubItemVO();

    pubItemVO.setContext(new ContextRO(CTX_MPG_ID));
    pubItemVO.setLatestRelease(new ItemRO());
    pubItemVO.setLatestVersion(new ItemRO());
    MdsPublicationVO mdsPublicationVO = new MdsPublicationVO();
    mdsPublicationVO.setGenre(MdsPublicationVO.Genre.BOOK);
    mdsPublicationVO.setTitle("Der Inn");

    pubItemVO.setMetadata(mdsPublicationVO);

    return pubItemVO;
  }

}
