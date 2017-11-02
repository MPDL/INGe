package de.mpg.mpdl.inge.service.pubman.impl;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.referenceobjects.ContextRO;
import de.mpg.mpdl.inge.model.referenceobjects.ItemRO;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO.CreatorRole;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PersonVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.OrganizationService;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.spring.AppConfigPubmanLogicTest;
import de.mpg.mpdl.inge.util.PropertyReader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfigPubmanLogicTest.class})
public class PubItemServiceTest extends TestBase {

  private static String CTX_SIMPLE = "ctx_2322554";
  private static String CTX_STANDARD = "ctx_persistent3";

  @Autowired
  PubItemService pubItemService;

  @Autowired
  OrganizationService organizationService;

  @Test
  public void createByDepositor() throws Exception {

    super.logMethodName();

    String authenticationToken = loginDepositor();

    PubItemVO pubItemVO = getPubItemVO(CTX_SIMPLE);
    pubItemVO = pubItemService.create(pubItemVO, authenticationToken);

    assertTrue("Objectid expected after create", pubItemVO.getLatestVersion().getObjectId() != null && !"".equals(pubItemVO.getLatestVersion().getObjectId()));
    assertTrue("Create PubItemVO failed", pubItemVO != null);
    assertTrue("Creation date missing in PubItemVO", pubItemVO.getCreationDate() != null);
    assertTrue("Context missing or wrong  context id", pubItemVO.getContext() != null
        && pubItemVO.getContext().getObjectId().equals(CTX_SIMPLE));
    assertTrue("Expected 1 creator in PubItemVO - found <"
        + pubItemVO.getMetadata().getCreators().size() + ">", pubItemVO.getMetadata().getCreators()
        .size() == 1);
    assertTrue(pubItemVO.getMetadata().getCreators().get(0) != null);
    assertTrue("Modification date missing in PubItemVO", pubItemVO.getLatestVersion()
        .getModificationDate() != null);
    assertTrue("Expected VersionStatus PENDING - found <" + pubItemVO.getLatestVersion().getState()
        + ">", pubItemVO.getLatestVersion().getState().equals(ItemVO.State.PENDING));
    assertTrue("Expected PublicStatus PENDING - found <" + pubItemVO.getPublicStatus() + ">",
        pubItemVO.getPublicStatus().equals(ItemVO.State.PENDING));
    assertTrue("Wrong owner", pubItemVO.getOwner().getObjectId().equals(USER_OBJECTID_DEPOSITOR));
  }

  @Test(expected = AuthorizationException.class)
  public void createByModerator() throws Exception {

    String authenticationToken = loginModerator();

    PubItemVO pubItemVO = getPubItemVO(CTX_SIMPLE);
    pubItemVO = pubItemService.create(pubItemVO, authenticationToken);
  }

  @Test
  public void createAndDeleteByDepositor() throws Exception {

    super.logMethodName();

    String authenticationToken = loginDepositor();

    PubItemVO pubItemVO = getPubItemVO(CTX_SIMPLE);
    pubItemVO = pubItemService.create(pubItemVO, authenticationToken);

    pubItemService.delete(pubItemVO.getVersion().getObjectId(), authenticationToken);

    pubItemVO = pubItemService.get(pubItemVO.getVersion().getObjectId(), authenticationToken);

    assertTrue("Found item even though it has been deleted in state PENDING!", pubItemVO == null);
  }

  @Test(expected = AuthorizationException.class)
  public void createByDepositorAndDeleteByModerator() throws Exception {

    super.logMethodName();

    String authenticationTokenDepositor = loginDepositor();
    String authenticationTokenModerator = loginModerator();

    PubItemVO pubItemVO = getPubItemVO(CTX_SIMPLE);
    pubItemVO = pubItemService.create(pubItemVO, authenticationTokenDepositor);

    pubItemService.delete(pubItemVO.getVersion().getObjectId(), authenticationTokenModerator);
  }

  @Test
  public void submitByDepositorStandardWorkflow() throws Exception {
    super.logMethodName();

    String authenticationToken = loginDepositor();

    PubItemVO pubItemVO = getPubItemVO(CTX_STANDARD);
    pubItemVO = pubItemService.create(pubItemVO, authenticationToken);

    assertTrue(pubItemVO.getLatestVersion().getState().equals(ItemVO.State.PENDING));

    pubItemVO =
        pubItemService.submitPubItem(pubItemVO.getVersion().getObjectId(),
            pubItemVO.getModificationDate(), "testing a submit", authenticationToken);

    assertTrue("Expected VersionStatus SUBMITTED - found <"
        + pubItemVO.getLatestVersion().getState() + ">", pubItemVO.getLatestVersion().getState()
        .equals(ItemVO.State.SUBMITTED));
    assertTrue("Expected PublicStatus SUBMITTED - found <" + pubItemVO.getPublicStatus() + ">",
        pubItemVO.getPublicStatus().equals(ItemVO.State.SUBMITTED));
    assertTrue("Wrong owner", pubItemVO.getOwner().getObjectId().equals(USER_OBJECTID_DEPOSITOR));
    assertTrue(pubItemVO.getLatestVersion().getModifiedByRO() != null);
    assertTrue(pubItemVO.getLatestVersion().getVersionNumber() == 1);
  }

  @Test(expected = AuthorizationException.class)
  public void submitByDepositorSimpleWorkflow() throws Exception {
    super.logMethodName();

    String authenticationToken = loginDepositor();

    PubItemVO pubItemVO = getPubItemVO(CTX_SIMPLE);
    pubItemVO = pubItemService.create(pubItemVO, authenticationToken);

    assertTrue(pubItemVO.getLatestVersion().getState().equals(ItemVO.State.PENDING));

    pubItemVO =
        pubItemService.submitPubItem(pubItemVO.getVersion().getObjectId(),
            pubItemVO.getModificationDate(), "testing a submit", authenticationToken);
  }

  @Test(expected = AuthorizationException.class)
  public void submitByModerator() throws Exception {
    super.logMethodName();

    String authenticationToken = loginModerator();

    PubItemVO pubItemVO = getPubItemVO(CTX_SIMPLE);
    pubItemVO = pubItemService.create(pubItemVO, authenticationToken);

    assertTrue(pubItemVO.getLatestVersion().getState().equals(ItemVO.State.PENDING));

    pubItemVO =
        pubItemService.submitPubItem(pubItemVO.getVersion().getObjectId(),
            pubItemVO.getModificationDate(), "testing a submit", authenticationToken);
  }

  @Test
  public void updateByDepositorSimpleWorkflow() throws Exception {
    super.logMethodName();

    String authenticationToken = loginDepositor();

    PubItemVO pubItemVO = getPubItemVO(CTX_SIMPLE);
    pubItemVO = pubItemService.create(pubItemVO, authenticationToken);

    assertTrue(pubItemVO.getLatestVersion().getState().equals(ItemVO.State.PENDING));

    pubItemVO.getMetadata().setTitle("Der neue Titel");

    pubItemVO = pubItemService.update(pubItemVO, authenticationToken);

    assertTrue(pubItemVO.getMetadata().getTitle().equals("Der neue Titel"));
    assertTrue(pubItemVO.getLatestVersion().getState().equals(ItemVO.State.PENDING));
    assertTrue(pubItemVO.getLatestVersion().getVersionNumber() == 1);
  }

  @Test
  public void updateByDepositorStandardWorkflow() throws Exception {
    super.logMethodName();

    String authenticationToken = loginDepositor();

    PubItemVO pubItemVO = getPubItemVO(CTX_STANDARD);
    pubItemVO = pubItemService.create(pubItemVO, authenticationToken);

    assertTrue(pubItemVO.getLatestVersion().getState().equals(ItemVO.State.PENDING));

    pubItemVO.getMetadata().setTitle("Der neue Titel");

    pubItemVO = pubItemService.update(pubItemVO, authenticationToken);

    assertTrue(pubItemVO.getMetadata().getTitle().equals("Der neue Titel"));
    assertTrue(pubItemVO.getLatestVersion().getState().equals(ItemVO.State.PENDING));
    assertTrue(pubItemVO.getLatestVersion().getVersionNumber() == 1);
  }

  @Test(expected = AuthorizationException.class)
  public void updateByModeratorSimpleWorkflow() throws Exception {
    super.logMethodName();

    String authenticationTokenDepositor = loginDepositor();
    String authenticationTokenModerator = loginModerator();

    PubItemVO pubItemVO = getPubItemVO(CTX_SIMPLE);
    pubItemVO = pubItemService.create(pubItemVO, authenticationTokenDepositor);

    assertTrue(pubItemVO.getLatestVersion().getState().equals(ItemVO.State.PENDING));

    pubItemVO.getMetadata().setTitle("Der neue Titel");

    pubItemVO = pubItemService.update(pubItemVO, authenticationTokenModerator);
  }

  @Test(expected = AuthorizationException.class)
  public void updateByModeratorStandardWorkflow() throws Exception {
    super.logMethodName();

    String authenticationTokenDepositor = loginDepositor();
    String authenticationTokenModerator = loginModerator();

    PubItemVO pubItemVO = getPubItemVO(CTX_STANDARD);
    pubItemVO = pubItemService.create(pubItemVO, authenticationTokenDepositor);

    assertTrue(pubItemVO.getLatestVersion().getState().equals(ItemVO.State.PENDING));

    pubItemVO.getMetadata().setTitle("Der neue Titel");

    pubItemVO = pubItemService.update(pubItemVO, authenticationTokenModerator);
  }
  
  @Test(expected = AuthorizationException.class)
  public void updateSubmittedItemByDepositorStandardWorkflow() throws Exception {
    super.logMethodName();
    
    String authenticationToken = loginDepositor();
    
    PubItemVO pubItemVO = createSubmittedItemStandardWorkflow();
    
    pubItemVO.getMetadata().setTitle("Der neue Titel");

    pubItemVO = pubItemService.update(pubItemVO, authenticationToken);
  }
  
  @Test
  public void updateSubmittedItemByModeratorStandardWorkflow() throws Exception {
    super.logMethodName();
    
    String authenticationTokenModerator = loginModerator();
    
    PubItemVO pubItemVO = createSubmittedItemStandardWorkflow();
    
    pubItemVO.getMetadata().setTitle("Der neue Titel");

    pubItemVO = pubItemService.update(pubItemVO, authenticationTokenModerator);

    assertTrue(pubItemVO.getMetadata().getTitle().equals("Der neue Titel"));
    assertTrue(pubItemVO.getLatestVersion().getState().equals(ItemVO.State.SUBMITTED));
    assertTrue("Expected version number <1> - got <" + pubItemVO.getVersion().getVersionNumber()
        + ">", pubItemVO.getVersion().getVersionNumber() == 1);
  }

  @Test
  public void releasePubItemStandardWorkflow() throws Exception {

    super.logMethodName();

    PubItemVO pubItemVO = createSubmittedItemStandardWorkflow();

    String authenticationTokenModerator = loginModerator();

    pubItemVO =
        pubItemService.releasePubItem(pubItemVO.getVersion().getObjectId(),
            pubItemVO.getModificationDate(), "testing a release", authenticationTokenModerator);

    assertTrue(pubItemVO.getLatestRelease().getPid() != null);
    assertTrue(pubItemVO.getPid() != null);
    assertTrue(pubItemVO.getVersion().getState().equals(ItemVO.State.RELEASED));
    assertTrue(pubItemVO.getVersion().getVersionNumber() == 1);
    assertTrue("Expected PublicStatus RELEASED - found <" + pubItemVO.getPublicStatus() + ">",
        pubItemVO.getPublicStatus().equals(ItemVO.State.RELEASED));
    assertTrue(pubItemVO.getLatestRelease().equals(pubItemVO.getLatestVersion()));
    assertTrue(pubItemVO.getLatestVersion().getModifiedByRO().getObjectId()
        .equals(USER_OBJECTID_MODERATOR));
  }
  
  @Test
  public void releasePubItemSimpleWorkflow() throws Exception {

    super.logMethodName();

    PubItemVO pubItemVO = createReleasedItemSimpleWorkflow();

    assertTrue(pubItemVO.getLatestRelease().getPid() != null);
    assertTrue(pubItemVO.getPid() != null);
    assertTrue(pubItemVO.getVersion().getState().equals(ItemVO.State.RELEASED));
    assertTrue(pubItemVO.getVersion().getVersionNumber() == 1);
    assertTrue("Expected PublicStatus RELEASED - found <" + pubItemVO.getPublicStatus() + ">",
        pubItemVO.getPublicStatus().equals(ItemVO.State.RELEASED));
    assertTrue(pubItemVO.getLatestRelease().equals(pubItemVO.getLatestVersion()));
    assertTrue(pubItemVO.getLatestVersion().getModifiedByRO().getObjectId()
        .equals(USER_OBJECTID_DEPOSITOR));
  }

  @Test
  public void withdrawPubItemStandardWorkflow() throws Exception {

    super.logMethodName();

    PubItemVO pubItemVO = createReleasedItemStandardWorkflow();

    String authenticationTokenModerator = loginModerator();

    pubItemVO =
        pubItemService.withdrawPubItem(pubItemVO.getVersion().getObjectId(), pubItemVO.getVersion()
            .getModificationDate(), "Weg damit", authenticationTokenModerator);

    assertTrue("Expected state WITHDRAWN",
        pubItemVO.getPublicStatus().equals(ItemVO.State.WITHDRAWN));
    assertTrue("Wrong or missing withdrawl comment",
        pubItemVO.getWithdrawalComment().equals("Weg damit"));
    assertTrue("Expected state WITHDRAWN",
        pubItemVO.getLatestVersion().getState().equals(ItemVO.State.WITHDRAWN));
    assertTrue("Expected version number <1> - got <" + pubItemVO.getVersion().getVersionNumber()
        + ">", pubItemVO.getVersion().getVersionNumber() == 1);
  }

  @Test
  public void revisePubItemStandardWorkflow() throws Exception {

    super.logMethodName();
    
    PubItemVO pubItemVO = createSubmittedItemStandardWorkflow();

    String authenticationTokenModerator = loginModerator();

    pubItemVO =
        pubItemService.revisePubItem(pubItemVO.getVersion().getObjectId(), pubItemVO.getVersion()
            .getModificationDate(), "Schrott", authenticationTokenModerator);

    assertTrue("Expected state IN_REVISION",
        pubItemVO.getLatestVersion().getState().equals(ItemVO.State.IN_REVISION));
    assertTrue("Expected state SUBMITTED",
        pubItemVO.getPublicStatus().equals(ItemVO.State.SUBMITTED));
    assertTrue("Expected version number <2> - got <" + pubItemVO.getVersion().getVersionNumber()
        + ">", pubItemVO.getVersion().getVersionNumber() == 1);
  }

  // --------------------------------------------------------------------- helper methods
  // --------------------------------------------------------------

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

  private PubItemVO getPubItemVO(String contextId) {
    PubItemVO pubItemVO = new PubItemVO();
    CreatorVO creatorVO = new CreatorVO();
    PersonVO personVO = new PersonVO();

    creatorVO.setRole(CreatorRole.AUTHOR);
    personVO.setCompleteName("Hans Meier");
    personVO.setFamilyName("Meier");
    personVO.setGivenName("Hans");
    creatorVO.setPerson(personVO);

    pubItemVO.setContext(new ContextRO(contextId));
    pubItemVO.setLatestRelease(new ItemRO());
    pubItemVO.setLatestVersion(new ItemRO());

    MdsPublicationVO mdsPublicationVO = new MdsPublicationVO();
    mdsPublicationVO.setGenre(MdsPublicationVO.Genre.BOOK);
    mdsPublicationVO.setTitle("Der Inn");
    mdsPublicationVO.setDateAccepted("2017");
    mdsPublicationVO.getCreators().add(creatorVO);

    pubItemVO.setMetadata(mdsPublicationVO);

    return pubItemVO;
  }

  private PubItemVO createReleasedItemStandardWorkflow() throws Exception {

    PubItemVO pubItemVO = createSubmittedItemStandardWorkflow();

    String authenticationTokenModerator = loginModerator();

    pubItemVO =
        pubItemService.releasePubItem(pubItemVO.getVersion().getObjectId(),
            pubItemVO.getModificationDate(), "testing a release", authenticationTokenModerator);

    return pubItemVO;
  }
  
  private PubItemVO createReleasedItemSimpleWorkflow() throws Exception {

    PubItemVO pubItemVO = getPubItemVO(CTX_SIMPLE);

    String authenticationToken = loginDepositor();
    pubItemVO = pubItemService.create(pubItemVO, authenticationToken);
    pubItemVO =
        pubItemService.releasePubItem(pubItemVO.getVersion().getObjectId(),
            pubItemVO.getModificationDate(), "testing a release", authenticationToken);

    return pubItemVO;
  }
  
  private PubItemVO createSubmittedItemStandardWorkflow() throws Exception {
    String authenticationTokenDepositor = loginDepositor();

    PubItemVO pubItemVO = getPubItemVO(CTX_STANDARD);
    pubItemVO = pubItemService.create(pubItemVO, authenticationTokenDepositor);

    assertTrue(pubItemVO.getLatestVersion().getState().equals(ItemVO.State.PENDING));
    assertTrue(pubItemVO.getCreationDate() != null);
    assertTrue(pubItemVO.getLatestVersion().getModifiedByRO() != null);

    pubItemVO =
        pubItemService.submitPubItem(pubItemVO.getVersion().getObjectId(),
            pubItemVO.getModificationDate(), "testing a submit", authenticationTokenDepositor);

    assertTrue(pubItemVO.getLatestVersion().getState().equals(ItemVO.State.SUBMITTED));

    return pubItemVO;
  }

}
