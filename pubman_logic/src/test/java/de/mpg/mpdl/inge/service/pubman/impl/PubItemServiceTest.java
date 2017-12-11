package de.mpg.mpdl.inge.service.pubman.impl;

import static org.junit.Assert.assertTrue;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mpg.mpdl.inge.model.referenceobjects.ContextRO;
import de.mpg.mpdl.inge.model.referenceobjects.ItemRO;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO.CreatorRole;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PersonVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.pubman.OrganizationService;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.spring.AppConfigPubmanLogicTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfigPubmanLogicTest.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class PubItemServiceTest extends TestBase {

  private static String CTX_SIMPLE = "ctx_2322554";
  private static String CTX_STANDARD = "ctx_persistent3";

  @Autowired
  PubItemService pubItemService;

  @Autowired
  OrganizationService organizationService;

  @Autowired
  EntityManager em;

  @Test
  public void createByDepositor() throws Exception {

    super.logMethodName();

    String authenticationToken = loginDepositor();

    PubItemVO pubItemVO = getPubItemVO(CTX_SIMPLE);
    pubItemVO = pubItemService.create(pubItemVO, authenticationToken);

    assertTrue("Objectid expected after create",
        pubItemVO.getLatestVersion().getObjectId() != null && !"".equals(pubItemVO.getLatestVersion().getObjectId()));
    assertTrue("Create PubItemVO failed", pubItemVO != null);
    assertTrue("Creation date missing in PubItemVO", pubItemVO.getCreationDate() != null);
    assertTrue("Context missing or wrong  context id",
        pubItemVO.getContext() != null && pubItemVO.getContext().getObjectId().equals(CTX_SIMPLE));
    assertTrue("Expected 1 creator in PubItemVO - found <" + pubItemVO.getMetadata().getCreators().size() + ">",
        pubItemVO.getMetadata().getCreators().size() == 1);
    assertTrue(pubItemVO.getMetadata().getCreators().get(0) != null);
    assertTrue("Modification date missing in PubItemVO", pubItemVO.getLatestVersion().getModificationDate() != null);
    assertTrue("Expected VersionStatus PENDING - found <" + pubItemVO.getLatestVersion().getState() + ">",
        pubItemVO.getLatestVersion().getState().equals(ItemVO.State.PENDING));
    assertTrue("Expected PublicStatus PENDING - found <" + pubItemVO.getPublicStatus() + ">",
        pubItemVO.getPublicStatus().equals(ItemVO.State.PENDING));
    assertTrue("Wrong owner", pubItemVO.getOwner().getObjectId().equals(DEPOSITOR_OBJECTID));
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


  @Test
  public void createAndDeleteByAdmin() throws Exception {

    super.logMethodName();

    String authenticationToken = loginAdmin();

    PubItemVO pubItemVO = getPubItemVO(CTX_SIMPLE);
    pubItemVO = pubItemService.create(pubItemVO, authenticationToken);

    pubItemVO = pubItemService.submitPubItem(pubItemVO.getVersion().getObjectId(), pubItemVO.getModificationDate(), "test submit",
        authenticationToken);
    pubItemVO = pubItemService.releasePubItem(pubItemVO.getVersion().getObjectId(), pubItemVO.getModificationDate(), "test release",
        authenticationToken);

    pubItemVO = pubItemService.update(pubItemVO, authenticationToken);

    pubItemVO = pubItemService.submitPubItem(pubItemVO.getVersion().getObjectId(), pubItemVO.getModificationDate(), "test submit",
        authenticationToken);
    pubItemVO = pubItemService.releasePubItem(pubItemVO.getVersion().getObjectId(), pubItemVO.getModificationDate(), "test release",
        authenticationToken);

    pubItemVO = pubItemService.update(pubItemVO, authenticationToken);


    pubItemService.delete(pubItemVO.getVersion().getObjectId(), authenticationToken);


    pubItemVO = pubItemService.get(pubItemVO.getVersion().getObjectId(), authenticationToken);


    assertTrue("Found item even though it has been deleted!", pubItemVO == null);


  }

  @Test
  public void getInvalidIdWithAuthentication() throws Exception {

    super.logMethodName();

    String authenticationToken = loginAdmin();
    PubItemVO pubItemVO = pubItemService.get("item_xyc", authenticationToken);

    assertTrue(pubItemVO == null);
  }

  @Test
  public void getInvalidIdWithoutAuthentication() throws Exception {

    super.logMethodName();

    PubItemVO pubItemVO = pubItemService.get("item_xyc", null);

    assertTrue(pubItemVO == null);
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
  public void submitCreatedItemByDepositorStandardWorkflow() throws Exception {
    super.logMethodName();

    String authenticationToken = loginDepositor();

    PubItemVO pubItemVO = getPubItemVO(CTX_STANDARD);
    pubItemVO = pubItemService.create(pubItemVO, authenticationToken);

    assertTrue(pubItemVO.getLatestVersion().getState().equals(ItemVO.State.PENDING));

    pubItemVO = pubItemService.submitPubItem(pubItemVO.getVersion().getObjectId(), pubItemVO.getModificationDate(), "testing a submit",
        authenticationToken);

    assertTrue("Expected VersionStatus SUBMITTED - found <" + pubItemVO.getLatestVersion().getState() + ">",
        pubItemVO.getLatestVersion().getState().equals(ItemVO.State.SUBMITTED));
    assertTrue("Expected PublicStatus SUBMITTED - found <" + pubItemVO.getPublicStatus() + ">",
        pubItemVO.getPublicStatus().equals(ItemVO.State.SUBMITTED));
    assertTrue("Wrong owner", pubItemVO.getOwner().getObjectId().equals(DEPOSITOR_OBJECTID));
    assertTrue(pubItemVO.getLatestVersion().getModifiedByRO() != null);
    assertTrue(pubItemVO.getLatestVersion().getVersionNumber() == 1);
  }

  @Test(expected = AuthorizationException.class)
  public void submitCreatedItemByDepositorSimpleWorkflow() throws Exception {
    super.logMethodName();

    String authenticationToken = loginDepositor();

    PubItemVO pubItemVO = getPubItemVO(CTX_SIMPLE);
    pubItemVO = pubItemService.create(pubItemVO, authenticationToken);

    assertTrue(pubItemVO.getLatestVersion().getState().equals(ItemVO.State.PENDING));

    pubItemVO = pubItemService.submitPubItem(pubItemVO.getVersion().getObjectId(), pubItemVO.getModificationDate(), "testing a submit",
        authenticationToken);
  }

  @Test(expected = AuthorizationException.class)
  public void submitCreatedItemByModeratorSimpleWorkflow() throws Exception {
    super.logMethodName();

    String authenticationToken = loginModerator();

    PubItemVO pubItemVO = getPubItemVO(CTX_SIMPLE);
    pubItemVO = pubItemService.create(pubItemVO, authenticationToken);

    assertTrue(pubItemVO.getLatestVersion().getState().equals(ItemVO.State.PENDING));

    pubItemVO = pubItemService.submitPubItem(pubItemVO.getVersion().getObjectId(), pubItemVO.getModificationDate(), "testing a submit",
        authenticationToken);
  }

  @Test(expected = AuthorizationException.class)
  public void submitInRevisionItemByModeratorStandardWorkflow() throws Exception {
    super.logMethodName();

    String authenticationToken = loginModerator();

    PubItemVO pubItemVO = createInRevisionItemStandardWorkflow();

    pubItemVO = pubItemService.submitPubItem(pubItemVO.getVersion().getObjectId(), pubItemVO.getModificationDate(),
        "testing submit of an item of state IN_REVISION", authenticationToken);

    assertTrue(pubItemVO.getPublicStatus().equals(ItemVO.State.SUBMITTED));
    assertTrue(pubItemVO.getLatestVersion().getState().equals(ItemVO.State.SUBMITTED));
    assertTrue(pubItemVO.getLatestVersion().getVersionNumber() == 1);
  }

  @Test
  public void submitInRevisionItemByDepositorStandardWorkflow() throws Exception {
    super.logMethodName();

    String authenticationTokenDepositor = loginDepositor();

    PubItemVO pubItemVO = createInRevisionItemStandardWorkflow();

    pubItemVO = pubItemService.submitPubItem(pubItemVO.getVersion().getObjectId(), pubItemVO.getModificationDate(),
        "testing submit of an item of state IN_REVISION", authenticationTokenDepositor);

    assertTrue(pubItemVO.getPublicStatus().equals(ItemVO.State.SUBMITTED));
    assertTrue(pubItemVO.getLatestVersion().getState().equals(ItemVO.State.SUBMITTED));
    assertTrue(pubItemVO.getLatestVersion().getVersionNumber() == 1);
  }

  @Test
  public void updateCreatedItemByDepositorSimpleWorkflow() throws Exception {
    super.logMethodName();

    String authenticationToken = loginDepositor();

    PubItemVO pubItemVO = getPubItemVO(CTX_SIMPLE);
    pubItemVO = pubItemService.create(pubItemVO, authenticationToken);

    assertTrue(pubItemVO.getLatestVersion().getState().equals(ItemVO.State.PENDING));

    pubItemVO.getMetadata().setTitle("Der neue Titel");

    pubItemVO = pubItemService.update(pubItemVO, authenticationToken);

    assertTrue(pubItemVO.getMetadata().getTitle().equals("Der neue Titel"));
    assertTrue(pubItemVO.getPublicStatus().equals(ItemVO.State.PENDING));
    assertTrue(pubItemVO.getLatestVersion().getState().equals(ItemVO.State.PENDING));
    assertTrue(pubItemVO.getLatestVersion().getVersionNumber() == 1);
  }

  @Test
  public void updateCreatedItemByDepositorStandardWorkflow() throws Exception {
    super.logMethodName();

    String authenticationToken = loginDepositor();

    PubItemVO pubItemVO = getPubItemVO(CTX_STANDARD);
    pubItemVO = pubItemService.create(pubItemVO, authenticationToken);

    assertTrue(pubItemVO.getLatestVersion().getState().equals(ItemVO.State.PENDING));

    pubItemVO.getMetadata().setTitle("Der neue Titel");

    pubItemVO = pubItemService.update(pubItemVO, authenticationToken);

    assertTrue(pubItemVO.getMetadata().getTitle().equals("Der neue Titel"));
    assertTrue(pubItemVO.getPublicStatus().equals(ItemVO.State.PENDING));
    assertTrue(pubItemVO.getLatestVersion().getState().equals(ItemVO.State.PENDING));
    assertTrue(pubItemVO.getLatestVersion().getVersionNumber() == 1);
  }

  @Test(expected = AuthorizationException.class)
  public void updateCreatedItemByModeratorSimpleWorkflow() throws Exception {
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
  public void updateCreatedItemByModeratorStandardWorkflow() throws Exception {
    super.logMethodName();

    String authenticationTokenDepositor = loginDepositor();
    String authenticationTokenModerator = loginModerator();

    PubItemVO pubItemVO = getPubItemVO(CTX_STANDARD);
    pubItemVO = pubItemService.create(pubItemVO, authenticationTokenDepositor);

    assertTrue(pubItemVO.getLatestVersion().getState().equals(ItemVO.State.PENDING));

    pubItemVO.getMetadata().setTitle("Der neue Titel");

    pubItemVO = pubItemService.update(pubItemVO, authenticationTokenModerator);
  }

  @Test
  public void updateSubmittedItemByDepositorStandardWorkflow() throws Exception {
    super.logMethodName();

    String authenticationToken = loginDepositor();

    PubItemVO pubItemVO = createSubmittedItemStandardWorkflow();

    pubItemVO.getMetadata().setTitle("Der neue Titel");

    pubItemVO = pubItemService.update(pubItemVO, authenticationToken);

    assertTrue(pubItemVO.getMetadata().getTitle().equals("Der neue Titel"));
    assertTrue(pubItemVO.getPublicStatus().equals(ItemVO.State.SUBMITTED));
    assertTrue(pubItemVO.getLatestVersion().getState().equals(ItemVO.State.SUBMITTED));
  }

  @Test
  public void updateSubmittedItemByModeratorSimpleWorkflow() throws Exception {
    super.logMethodName();

    String authenticationTokenModerator = loginModerator();

    PubItemVO pubItemVO = createSubmittedItemSimpleWorkflow();

    pubItemVO.getMetadata().setTitle("Der neue Titel");

    pubItemVO = pubItemService.update(pubItemVO, authenticationTokenModerator);

    assertTrue(pubItemVO.getMetadata().getTitle().equals("Der neue Titel"));
    assertTrue(pubItemVO.getPublicStatus().equals(ItemVO.State.RELEASED));
    assertTrue(pubItemVO.getLatestVersion().getState().equals(ItemVO.State.SUBMITTED));
    assertTrue("Expected version number <2> - got <" + pubItemVO.getVersion().getVersionNumber() + ">",
        pubItemVO.getVersion().getVersionNumber() == 2);
  }

  @Test
  public void updateSubmittedItemByModeratorStandardWorkflow() throws Exception {
    super.logMethodName();

    String authenticationTokenModerator = loginModerator();

    PubItemVO pubItemVO = createSubmittedItemStandardWorkflow();

    pubItemVO.getMetadata().setTitle("Der neue Titel");

    pubItemVO = pubItemService.update(pubItemVO, authenticationTokenModerator);

    assertTrue(pubItemVO.getMetadata().getTitle().equals("Der neue Titel"));
    assertTrue(pubItemVO.getPublicStatus().equals(ItemVO.State.SUBMITTED));
    assertTrue(pubItemVO.getLatestVersion().getState().equals(ItemVO.State.SUBMITTED));
    assertTrue("Expected version number <1> - got <" + pubItemVO.getVersion().getVersionNumber() + ">",
        pubItemVO.getVersion().getVersionNumber() == 1);
  }

  @Test
  public void updateReleasedItemByDepositorSimpleWorkflow() throws Exception {
    super.logMethodName();

    String authenticationToken = loginDepositor();

    PubItemVO pubItemVO = createReleasedItemSimpleWorkflow();

    pubItemVO.getMetadata().setTitle("Der neue Titel");

    pubItemVO = pubItemService.update(pubItemVO, authenticationToken);

    assertTrue(pubItemVO.getMetadata().getTitle().equals("Der neue Titel"));
    assertTrue(pubItemVO.getLatestVersion().getState().equals(ItemVO.State.PENDING));
    assertTrue(pubItemVO.getPublicStatus().equals(ItemVO.State.RELEASED));
    assertTrue(pubItemVO.getLatestRelease().getVersionNumber() == pubItemVO.getLatestVersion().getVersionNumber() - 1);
  }

  @Test
  public void updateReleasedItemByDepositorStandardWorkflow() throws Exception {
    super.logMethodName();

    String authenticationToken = loginDepositor();

    PubItemVO pubItemVO = createReleasedItemStandardWorkflow();

    pubItemVO.getMetadata().setTitle("Der neue Titel");

    pubItemVO = pubItemService.update(pubItemVO, authenticationToken);

    assertTrue(pubItemVO.getMetadata().getTitle().equals("Der neue Titel"));
    assertTrue(pubItemVO.getLatestVersion().getState().equals(ItemVO.State.PENDING));
    assertTrue(pubItemVO.getPublicStatus().equals(ItemVO.State.RELEASED));
    assertTrue(pubItemVO.getLatestRelease().getVersionNumber() == pubItemVO.getLatestVersion().getVersionNumber() - 1);
  }

  @Test
  public void updateReleasedItemByModeratorSimpleWorkflow() throws Exception {
    super.logMethodName();

    String authenticationToken = loginModerator();

    PubItemVO pubItemVO = createReleasedItemSimpleWorkflow();

    pubItemVO.getMetadata().setTitle("Der neue Titel");

    pubItemVO = pubItemService.update(pubItemVO, authenticationToken);

    assertTrue(pubItemVO.getMetadata().getTitle().equals("Der neue Titel"));
    assertTrue(pubItemVO.getLatestVersion().getState().equals(ItemVO.State.SUBMITTED));
    assertTrue(pubItemVO.getPublicStatus().equals(ItemVO.State.RELEASED));
    assertTrue(pubItemVO.getLatestRelease().getVersionNumber() == pubItemVO.getLatestVersion().getVersionNumber() - 1);
  }

  @Test
  public void updateReleasedItemByModeratorStandardWorkflow() throws Exception {
    super.logMethodName();

    String authenticationToken = loginModerator();

    PubItemVO pubItemVO = createReleasedItemStandardWorkflow();

    pubItemVO.getMetadata().setTitle("Der neue Titel");

    pubItemVO = pubItemService.update(pubItemVO, authenticationToken);

    assertTrue(pubItemVO.getMetadata().getTitle().equals("Der neue Titel"));
    assertTrue(pubItemVO.getPublicStatus().equals(ItemVO.State.RELEASED));
    assertTrue(pubItemVO.getLatestVersion().getState().equals(ItemVO.State.SUBMITTED));
    assertTrue(pubItemVO.getLatestRelease().getVersionNumber() == pubItemVO.getLatestVersion().getVersionNumber() - 1);
  }

  @Test(expected = AuthorizationException.class)
  public void updateInRevisionItemByModeratorStandardWorkflow() throws Exception {
    super.logMethodName();

    String authenticationToken = loginModerator();

    PubItemVO pubItemVO = createInRevisionItemStandardWorkflow();

    pubItemVO.getMetadata().setTitle("Der neue Titel");
    pubItemVO = pubItemService.update(pubItemVO, authenticationToken);
  }

  @Test
  public void updateInRevisionItemByDepositorStandardWorkflow() throws Exception {
    super.logMethodName();

    String authenticationToken = loginDepositor();

    PubItemVO pubItemVO = createInRevisionItemStandardWorkflow();

    pubItemVO.getMetadata().setTitle("Der neue Titel");
    pubItemVO = pubItemService.update(pubItemVO, authenticationToken);

    assertTrue(pubItemVO.getMetadata().getTitle().equals("Der neue Titel"));
    assertTrue(pubItemVO.getPublicStatus().equals(ItemVO.State.SUBMITTED));
    assertTrue(pubItemVO.getLatestVersion().getState().equals(ItemVO.State.IN_REVISION));
  }

  @Test
  public void releasePubItemStandardWorkflow() throws Exception {

    super.logMethodName();

    PubItemVO pubItemVO = createSubmittedItemStandardWorkflow();

    String authenticationTokenModerator = loginModerator();

    pubItemVO = pubItemService.releasePubItem(pubItemVO.getVersion().getObjectId(), pubItemVO.getModificationDate(), "testing a release",
        authenticationTokenModerator);

    assertTrue(pubItemVO.getLatestRelease().getPid() != null);
    assertTrue(pubItemVO.getPid() != null);
    assertTrue(pubItemVO.getVersion().getState().equals(ItemVO.State.RELEASED));
    assertTrue(pubItemVO.getVersion().getVersionNumber() == 1);
    assertTrue("Expected PublicStatus RELEASED - found <" + pubItemVO.getPublicStatus() + ">",
        pubItemVO.getPublicStatus().equals(ItemVO.State.RELEASED));
    assertTrue(pubItemVO.getLatestRelease().equals(pubItemVO.getLatestVersion()));
    assertTrue(pubItemVO.getLatestVersion().getModifiedByRO().getObjectId().equals(MODERATOR_OBJECTID));
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
    assertTrue(pubItemVO.getLatestVersion().getModifiedByRO().getObjectId().equals(DEPOSITOR_OBJECTID));
  }

  @Test
  public void withdrawPubItemByModeratorStandardWorkflow() throws Exception {

    super.logMethodName();

    PubItemVO pubItemVO = createReleasedItemStandardWorkflow();

    String authenticationTokenModerator = loginModerator();

    pubItemVO = pubItemService.withdrawPubItem(pubItemVO.getVersion().getObjectId(), pubItemVO.getVersion().getModificationDate(),
        "Weg damit", authenticationTokenModerator);

    assertTrue("Expected state WITHDRAWN", pubItemVO.getPublicStatus().equals(ItemVO.State.WITHDRAWN));
    assertTrue("Wrong or missing withdrawl comment", pubItemVO.getWithdrawalComment().equals("Weg damit"));
    assertTrue("Expected state RELEASED", pubItemVO.getLatestVersion().getState().equals(ItemVO.State.RELEASED));
    assertTrue("Expected state RELEASED", pubItemVO.getLatestRelease().getState().equals(ItemVO.State.RELEASED));
    assertTrue("Expected version number <1> - got <" + pubItemVO.getVersion().getVersionNumber() + ">",
        pubItemVO.getVersion().getVersionNumber() == 1);
  }

  @Test
  public void withdrawPubItemByModeratorSimpleWorkflow() throws Exception {

    super.logMethodName();

    PubItemVO pubItemVO = createReleasedItemSimpleWorkflow();

    String authenticationTokenModerator = loginModerator();

    pubItemVO = pubItemService.withdrawPubItem(pubItemVO.getVersion().getObjectId(), pubItemVO.getVersion().getModificationDate(),
        "Weg damit", authenticationTokenModerator);

    assertTrue("Expected state WITHDRAWN", pubItemVO.getPublicStatus().equals(ItemVO.State.WITHDRAWN));
    assertTrue("Wrong or missing withdrawl comment", pubItemVO.getWithdrawalComment().equals("Weg damit"));
    assertTrue("Expected state RELEASED", pubItemVO.getLatestVersion().getState().equals(ItemVO.State.RELEASED));
    assertTrue("Expected state RELEASED", pubItemVO.getLatestRelease().getState().equals(ItemVO.State.RELEASED));
    assertTrue("Expected version number <1> - got <" + pubItemVO.getVersion().getVersionNumber() + ">",
        pubItemVO.getVersion().getVersionNumber() == 1);
  }

  @Test
  public void withdrawPubItemByDepositorSimpleWorkflow() throws Exception {

    super.logMethodName();

    PubItemVO pubItemVO = createReleasedItemSimpleWorkflow();

    String authenticationTokenDepositor = loginDepositor();

    pubItemVO = pubItemService.withdrawPubItem(pubItemVO.getVersion().getObjectId(), pubItemVO.getVersion().getModificationDate(),
        "Weg damit", authenticationTokenDepositor);

    assertTrue("Expected state WITHDRAWN", pubItemVO.getPublicStatus().equals(ItemVO.State.WITHDRAWN));
    assertTrue("Wrong or missing withdrawl comment", pubItemVO.getWithdrawalComment().equals("Weg damit"));
    assertTrue("Expected state RELEASED", pubItemVO.getLatestVersion().getState().equals(ItemVO.State.RELEASED));
    assertTrue("Expected state RELEASED", pubItemVO.getLatestRelease().getState().equals(ItemVO.State.RELEASED));
    assertTrue("Expected version number <1> - got <" + pubItemVO.getVersion().getVersionNumber() + ">",
        pubItemVO.getVersion().getVersionNumber() == 1);
  }

  @Test(expected = AuthorizationException.class)
  public void withdrawPubItemByDepositorStandardWorkflow() throws Exception {

    super.logMethodName();

    PubItemVO pubItemVO = createReleasedItemStandardWorkflow();

    String authenticationTokenDepositor = loginDepositor();

    pubItemVO = pubItemService.withdrawPubItem(pubItemVO.getVersion().getObjectId(), pubItemVO.getVersion().getModificationDate(),
        "Weg damit", authenticationTokenDepositor);
  }

  @Test
  public void revisePubItemByModeratorStandardWorkflow() throws Exception {

    super.logMethodName();

    PubItemVO pubItemVO = createSubmittedItemStandardWorkflow();

    String authenticationTokenModerator = loginModerator();

    pubItemVO = pubItemService.revisePubItem(pubItemVO.getVersion().getObjectId(), pubItemVO.getVersion().getModificationDate(), "Schrott",
        authenticationTokenModerator);

    assertTrue("Expected state IN_REVISION", pubItemVO.getLatestVersion().getState().equals(ItemVO.State.IN_REVISION));
    assertTrue("Expected state SUBMITTED", pubItemVO.getPublicStatus().equals(ItemVO.State.SUBMITTED));
    assertTrue("Expected version number <2> - got <" + pubItemVO.getVersion().getVersionNumber() + ">",
        pubItemVO.getVersion().getVersionNumber() == 1);
  }

  @Test
  public void deleteInRevisionItemByDepositorStandardWorkflow() throws Exception {
    super.logMethodName();

    String authenticationToken = loginDepositor();

    PubItemVO pubItemVO = createInRevisionItemStandardWorkflow();

    pubItemService.delete(pubItemVO.getLatestVersion().getObjectId(), authenticationToken);

    assertTrue(pubItemService.get(pubItemVO.getLatestVersion().getObjectId(), authenticationToken) == null);
  }

  @Test(expected = AuthorizationException.class)
  public void deleteInRevisionItemByModeratorStandardWorkflow() throws Exception {
    super.logMethodName();

    String authenticationToken = loginModerator();

    PubItemVO pubItemVO = createInRevisionItemStandardWorkflow();

    pubItemService.delete(pubItemVO.getLatestVersion().getObjectId(), authenticationToken);
  }

  // --------------------------------------------------------------------- helper methods
  // --------------------------------------------------------------



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

    pubItemVO = pubItemService.releasePubItem(pubItemVO.getVersion().getObjectId(), pubItemVO.getModificationDate(), "testing a release",
        authenticationTokenModerator);

    return pubItemVO;
  }

  private PubItemVO createReleasedItemSimpleWorkflow() throws Exception {

    PubItemVO pubItemVO = getPubItemVO(CTX_SIMPLE);

    String authenticationToken = loginDepositor();
    pubItemVO = pubItemService.create(pubItemVO, authenticationToken);
    pubItemVO = pubItemService.releasePubItem(pubItemVO.getVersion().getObjectId(), pubItemVO.getModificationDate(), "testing a release",
        authenticationToken);

    return pubItemVO;
  }

  private PubItemVO createSubmittedItemStandardWorkflow() throws Exception {
    String authenticationTokenDepositor = loginDepositor();

    PubItemVO pubItemVO = getPubItemVO(CTX_STANDARD);
    pubItemVO = pubItemService.create(pubItemVO, authenticationTokenDepositor);

    assertTrue(pubItemVO.getLatestVersion().getState().equals(ItemVO.State.PENDING));
    assertTrue(pubItemVO.getCreationDate() != null);
    assertTrue(pubItemVO.getLatestVersion().getModifiedByRO() != null);

    pubItemVO = pubItemService.submitPubItem(pubItemVO.getVersion().getObjectId(), pubItemVO.getModificationDate(), "testing a submit",
        authenticationTokenDepositor);

    assertTrue(pubItemVO.getLatestVersion().getState().equals(ItemVO.State.SUBMITTED));

    return pubItemVO;
  }

  // Creating an item in version state SUBMITTED in an simple workflow environment can only be done
  // by making a detour over a released item.
  private PubItemVO createSubmittedItemSimpleWorkflow() throws Exception {
    String authenticationTokenModerator = loginModerator();

    PubItemVO pubItemVO = createReleasedItemSimpleWorkflow();

    pubItemVO.getMetadata().setTitle("Der neue Titel");

    pubItemVO = pubItemService.update(pubItemVO, authenticationTokenModerator);

    assertTrue(pubItemVO.getPublicStatus().equals(ItemVO.State.RELEASED));
    assertTrue(pubItemVO.getLatestVersion().getState().equals(ItemVO.State.SUBMITTED));

    return pubItemVO;
  }


  private PubItemVO createInRevisionItemStandardWorkflow() throws Exception {
    String authenticationToken = loginModerator();

    PubItemVO pubItemVO = createSubmittedItemStandardWorkflow();

    pubItemVO = pubItemService.revisePubItem(pubItemVO.getLatestVersion().getObjectId(), pubItemVO.getModificationDate(), "To Revision",
        authenticationToken);

    assertTrue(pubItemVO.getLatestVersion().getState().equals(ItemVO.State.IN_REVISION));

    return pubItemVO;
  }

}
