package de.mpg.mpdl.inge.service.pubman.impl;

import static org.junit.Assert.assertTrue;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.PubItemVersionDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.PubItemVersionDbVO;
import de.mpg.mpdl.inge.model.referenceobjects.ContextRO;
import de.mpg.mpdl.inge.model.referenceobjects.ItemRO;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO.CreatorRole;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PersonVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
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

    PubItemVersionDbVO pubItemVO = getPubItemVersionDbVO(CTX_SIMPLE);
    pubItemVO = pubItemService.create(pubItemVO, authenticationToken);

    assertTrue("Objectid expected after create",
        pubItemVO.getObject().getLatestVersion().getObjectId() != null && !"".equals(pubItemVO.getObject().getLatestVersion().getObjectId()));
    assertTrue("Create PubItemVersionDbVO failed", pubItemVO != null);
    assertTrue("Creation date missing in PubItemVersionDbVO", pubItemVO.getObject().getCreationDate() != null);
    assertTrue("Context missing or wrong  context id",
        pubItemVO.getObject().getContext() != null && pubItemVO.getObject().getContext().getObjectId().equals(CTX_SIMPLE));
    assertTrue("Expected 1 creator in PubItemVersionDbVO - found <" + pubItemVO.getMetadata().getCreators().size() + ">",
        pubItemVO.getMetadata().getCreators().size() == 1);
    assertTrue(pubItemVO.getMetadata().getCreators().get(0) != null);
    assertTrue("Modification date missing in PubItemVersionDbVO", pubItemVO.getObject().getLatestVersion().getModificationDate() != null);
    assertTrue("Expected VersionStatus PENDING - found <" + pubItemVO.getObject().getLatestVersion().getVersionState() + ">",
        pubItemVO.getObject().getLatestVersion().getVersionState().equals(PubItemVersionDbVO.State.PENDING));
    assertTrue("Expected PublicStatus PENDING - found <" + pubItemVO.getObject().getPublicState() + ">",
        pubItemVO.getObject().getPublicState().equals(PubItemVersionDbVO.State.PENDING));
    assertTrue("Wrong owner", pubItemVO.getObject().getCreator().getObjectId().equals(DEPOSITOR_OBJECTID));
  }

  @Test(expected = AuthorizationException.class)
  public void createByModerator() throws Exception {

    String authenticationToken = loginModerator();

    PubItemVersionDbVO pubItemVO = getPubItemVersionDbVO(CTX_SIMPLE);
    pubItemVO = pubItemService.create(pubItemVO, authenticationToken);
  }

  @Test
  public void createAndDeleteByDepositor() throws Exception {

    super.logMethodName();

    String authenticationToken = loginDepositor();

    PubItemVersionDbVO pubItemVO = getPubItemVersionDbVO(CTX_SIMPLE);
    pubItemVO = pubItemService.create(pubItemVO, authenticationToken);

    pubItemService.delete(pubItemVO.getObjectId(), authenticationToken);

    pubItemVO = pubItemService.get(pubItemVO.getObjectId(), authenticationToken);

    assertTrue("Found item even though it has been deleted in state PENDING!", pubItemVO == null);
  }


  @Test
  public void createAndDeleteByAdmin() throws Exception {

    super.logMethodName();

    String authenticationToken = loginAdmin();

    PubItemVersionDbVO pubItemVO = getPubItemVersionDbVO(CTX_SIMPLE);
    pubItemVO = pubItemService.create(pubItemVO, authenticationToken);

    pubItemVO = pubItemService.submitPubItem(pubItemVO.getObjectId(), pubItemVO.getModificationDate(), "test submit",
        authenticationToken);
    pubItemVO = pubItemService.releasePubItem(pubItemVO.getObjectId(), pubItemVO.getModificationDate(), "test release",
        authenticationToken);

    pubItemVO = pubItemService.update(pubItemVO, authenticationToken);

    pubItemVO = pubItemService.submitPubItem(pubItemVO.getObjectId(), pubItemVO.getModificationDate(), "test submit",
        authenticationToken);
    pubItemVO = pubItemService.releasePubItem(pubItemVO.getObjectId(), pubItemVO.getModificationDate(), "test release",
        authenticationToken);

    pubItemVO = pubItemService.update(pubItemVO, authenticationToken);


    pubItemService.delete(pubItemVO.getObjectId(), authenticationToken);


    pubItemVO = pubItemService.get(pubItemVO.getObjectId(), authenticationToken);


    assertTrue("Found item even though it has been deleted!", pubItemVO == null);


  }

  @Test
  public void getInvalidIdWithAuthentication() throws Exception {

    super.logMethodName();

    String authenticationToken = loginAdmin();
    PubItemVersionDbVO pubItemVO = pubItemService.get("item_xyc", authenticationToken);

    assertTrue(pubItemVO == null);
  }

  @Test
  public void getInvalidIdWithoutAuthentication() throws Exception {

    super.logMethodName();

    PubItemVersionDbVO pubItemVO = pubItemService.get("item_xyc", null);

    assertTrue(pubItemVO == null);
  }

  @Test(expected = AuthorizationException.class)
  public void createByDepositorAndDeleteByModerator() throws Exception {

    super.logMethodName();

    String authenticationTokenDepositor = loginDepositor();
    String authenticationTokenModerator = loginModerator();

    PubItemVersionDbVO pubItemVO = getPubItemVersionDbVO(CTX_SIMPLE);
    pubItemVO = pubItemService.create(pubItemVO, authenticationTokenDepositor);

    pubItemService.delete(pubItemVO.getObjectId(), authenticationTokenModerator);
  }

  @Test
  public void submitCreatedItemByDepositorStandardWorkflow() throws Exception {
    super.logMethodName();

    String authenticationToken = loginDepositor();

    PubItemVersionDbVO pubItemVO = getPubItemVersionDbVO(CTX_STANDARD);
    pubItemVO = pubItemService.create(pubItemVO, authenticationToken);

    assertTrue(pubItemVO.getObject().getLatestVersion().getVersionState().equals(PubItemVersionDbVO.State.PENDING));

    pubItemVO = pubItemService.submitPubItem(pubItemVO.getObjectId(), pubItemVO.getModificationDate(), "testing a submit",
        authenticationToken);

    assertTrue("Expected VersionStatus SUBMITTED - found <" + pubItemVO.getObject().getLatestVersion().getVersionState() + ">",
        pubItemVO.getObject().getLatestVersion().getVersionState().equals(PubItemVersionDbVO.State.SUBMITTED));
    assertTrue("Expected PublicStatus SUBMITTED - found <" + pubItemVO.getObject().getPublicState() + ">",
        pubItemVO.getObject().getPublicState().equals(PubItemVersionDbVO.State.SUBMITTED));
    assertTrue("Wrong owner", pubItemVO.getObject().getCreator().getObjectId().equals(DEPOSITOR_OBJECTID));
    assertTrue(pubItemVO.getObject().getLatestVersion().getModifiedBy() != null);
    assertTrue(pubItemVO.getObject().getLatestVersion().getVersionNumber() == 1);
  }

  @Test(expected = AuthorizationException.class)
  public void submitCreatedItemByDepositorSimpleWorkflow() throws Exception {
    super.logMethodName();

    String authenticationToken = loginDepositor();

    PubItemVersionDbVO pubItemVO = getPubItemVersionDbVO(CTX_SIMPLE);
    pubItemVO = pubItemService.create(pubItemVO, authenticationToken);

    assertTrue(pubItemVO.getObject().getLatestVersion().getVersionState().equals(PubItemVersionDbVO.State.PENDING));

    pubItemVO = pubItemService.submitPubItem(pubItemVO.getObjectId(), pubItemVO.getModificationDate(), "testing a submit",
        authenticationToken);
  }

  @Test(expected = AuthorizationException.class)
  public void submitCreatedItemByModeratorSimpleWorkflow() throws Exception {
    super.logMethodName();

    String authenticationToken = loginModerator();

    PubItemVersionDbVO pubItemVO = getPubItemVersionDbVO(CTX_SIMPLE);
    pubItemVO = pubItemService.create(pubItemVO, authenticationToken);

    assertTrue(pubItemVO.getObject().getLatestVersion().getVersionState().equals(PubItemVersionDbVO.State.PENDING));

    pubItemVO = pubItemService.submitPubItem(pubItemVO.getObjectId(), pubItemVO.getModificationDate(), "testing a submit",
        authenticationToken);
  }

  @Test(expected = AuthorizationException.class)
  public void submitInRevisionItemByModeratorStandardWorkflow() throws Exception {
    super.logMethodName();

    String authenticationToken = loginModerator();

    PubItemVersionDbVO pubItemVO = createInRevisionItemStandardWorkflow();

    pubItemVO = pubItemService.submitPubItem(pubItemVO.getObjectId(), pubItemVO.getModificationDate(),
        "testing submit of an item of state IN_REVISION", authenticationToken);

    assertTrue(pubItemVO.getObject().getPublicState().equals(PubItemVersionDbVO.State.SUBMITTED));
    assertTrue(pubItemVO.getObject().getLatestVersion().getVersionState().equals(PubItemVersionDbVO.State.SUBMITTED));
    assertTrue(pubItemVO.getObject().getLatestVersion().getVersionNumber() == 1);
  }

  @Test
  public void submitInRevisionItemByDepositorStandardWorkflow() throws Exception {
    super.logMethodName();

    String authenticationTokenDepositor = loginDepositor();

    PubItemVersionDbVO pubItemVO = createInRevisionItemStandardWorkflow();

    pubItemVO = pubItemService.submitPubItem(pubItemVO.getObjectId(), pubItemVO.getModificationDate(),
        "testing submit of an item of state IN_REVISION", authenticationTokenDepositor);

    assertTrue(pubItemVO.getObject().getPublicState().equals(PubItemVersionDbVO.State.SUBMITTED));
    assertTrue(pubItemVO.getObject().getLatestVersion().getVersionState().equals(PubItemVersionDbVO.State.SUBMITTED));
    assertTrue(pubItemVO.getObject().getLatestVersion().getVersionNumber() == 1);
  }

  @Test
  public void updateCreatedItemByDepositorSimpleWorkflow() throws Exception {
    super.logMethodName();

    String authenticationToken = loginDepositor();

    PubItemVersionDbVO pubItemVO = getPubItemVersionDbVO(CTX_SIMPLE);
    pubItemVO = pubItemService.create(pubItemVO, authenticationToken);

    assertTrue(pubItemVO.getObject().getLatestVersion().getVersionState().equals(PubItemVersionDbVO.State.PENDING));

    pubItemVO.getMetadata().setTitle("Der neue Titel");

    pubItemVO = pubItemService.update(pubItemVO, authenticationToken);

    assertTrue(pubItemVO.getMetadata().getTitle().equals("Der neue Titel"));
    assertTrue(pubItemVO.getObject().getPublicState().equals(PubItemVersionDbVO.State.PENDING));
    assertTrue(pubItemVO.getObject().getLatestVersion().getVersionState().equals(PubItemVersionDbVO.State.PENDING));
    assertTrue(pubItemVO.getObject().getLatestVersion().getVersionNumber() == 1);
  }

  @Test
  public void updateCreatedItemByDepositorStandardWorkflow() throws Exception {
    super.logMethodName();

    String authenticationToken = loginDepositor();

    PubItemVersionDbVO pubItemVO = getPubItemVersionDbVO(CTX_STANDARD);
    pubItemVO = pubItemService.create(pubItemVO, authenticationToken);

    assertTrue(pubItemVO.getObject().getLatestVersion().getVersionState().equals(PubItemVersionDbVO.State.PENDING));

    pubItemVO.getMetadata().setTitle("Der neue Titel");

    pubItemVO = pubItemService.update(pubItemVO, authenticationToken);

    assertTrue(pubItemVO.getMetadata().getTitle().equals("Der neue Titel"));
    assertTrue(pubItemVO.getObject().getPublicState().equals(PubItemVersionDbVO.State.PENDING));
    assertTrue(pubItemVO.getObject().getLatestVersion().getVersionState().equals(PubItemVersionDbVO.State.PENDING));
    assertTrue(pubItemVO.getObject().getLatestVersion().getVersionNumber() == 1);
  }

  @Test(expected = AuthorizationException.class)
  public void updateCreatedItemByModeratorSimpleWorkflow() throws Exception {
    super.logMethodName();

    String authenticationTokenDepositor = loginDepositor();
    String authenticationTokenModerator = loginModerator();

    PubItemVersionDbVO pubItemVO = getPubItemVersionDbVO(CTX_SIMPLE);
    pubItemVO = pubItemService.create(pubItemVO, authenticationTokenDepositor);

    assertTrue(pubItemVO.getObject().getLatestVersion().getVersionState().equals(PubItemVersionDbVO.State.PENDING));

    pubItemVO.getMetadata().setTitle("Der neue Titel");

    pubItemVO = pubItemService.update(pubItemVO, authenticationTokenModerator);
  }

  @Test(expected = AuthorizationException.class)
  public void updateCreatedItemByModeratorStandardWorkflow() throws Exception {
    super.logMethodName();

    String authenticationTokenDepositor = loginDepositor();
    String authenticationTokenModerator = loginModerator();

    PubItemVersionDbVO pubItemVO = getPubItemVersionDbVO(CTX_STANDARD);
    pubItemVO = pubItemService.create(pubItemVO, authenticationTokenDepositor);

    assertTrue(pubItemVO.getObject().getLatestVersion().getVersionState().equals(PubItemVersionDbVO.State.PENDING));

    pubItemVO.getMetadata().setTitle("Der neue Titel");

    pubItemVO = pubItemService.update(pubItemVO, authenticationTokenModerator);
  }

  @Test
  public void updateSubmittedItemByDepositorStandardWorkflow() throws Exception {
    super.logMethodName();

    String authenticationToken = loginDepositor();

    PubItemVersionDbVO pubItemVO = createSubmittedItemStandardWorkflow();

    pubItemVO.getMetadata().setTitle("Der neue Titel");

    pubItemVO = pubItemService.update(pubItemVO, authenticationToken);

    assertTrue(pubItemVO.getMetadata().getTitle().equals("Der neue Titel"));
    assertTrue(pubItemVO.getObject().getPublicState().equals(PubItemVersionDbVO.State.SUBMITTED));
    assertTrue(pubItemVO.getObject().getLatestVersion().getVersionState().equals(PubItemVersionDbVO.State.SUBMITTED));
  }

  @Test
  public void updateSubmittedItemByModeratorSimpleWorkflow() throws Exception {
    super.logMethodName();

    String authenticationTokenModerator = loginModerator();

    PubItemVersionDbVO pubItemVO = createSubmittedItemSimpleWorkflow();

    pubItemVO.getMetadata().setTitle("Der neue Titel");

    pubItemVO = pubItemService.update(pubItemVO, authenticationTokenModerator);

    assertTrue(pubItemVO.getMetadata().getTitle().equals("Der neue Titel"));
    assertTrue(pubItemVO.getObject().getPublicState().equals(PubItemVersionDbVO.State.RELEASED));
    assertTrue(pubItemVO.getObject().getLatestVersion().getVersionState().equals(PubItemVersionDbVO.State.SUBMITTED));
    assertTrue("Expected version number <2> - got <" + pubItemVO.getVersionNumber() + ">",
        pubItemVO.getVersionNumber() == 2);
  }

  @Test
  public void updateSubmittedItemByModeratorStandardWorkflow() throws Exception {
    super.logMethodName();

    String authenticationTokenModerator = loginModerator();

    PubItemVersionDbVO pubItemVO = createSubmittedItemStandardWorkflow();

    pubItemVO.getMetadata().setTitle("Der neue Titel");

    pubItemVO = pubItemService.update(pubItemVO, authenticationTokenModerator);

    assertTrue(pubItemVO.getMetadata().getTitle().equals("Der neue Titel"));
    assertTrue(pubItemVO.getObject().getPublicState().equals(PubItemVersionDbVO.State.SUBMITTED));
    assertTrue(pubItemVO.getObject().getLatestVersion().getVersionState().equals(PubItemVersionDbVO.State.SUBMITTED));
    assertTrue("Expected version number <1> - got <" + pubItemVO.getVersionNumber() + ">",
        pubItemVO.getVersionNumber() == 1);
  }

  @Test
  public void updateReleasedItemByDepositorSimpleWorkflow() throws Exception {
    super.logMethodName();

    String authenticationToken = loginDepositor();

    PubItemVersionDbVO pubItemVO = createReleasedItemSimpleWorkflow();

    pubItemVO.getMetadata().setTitle("Der neue Titel");

    pubItemVO = pubItemService.update(pubItemVO, authenticationToken);

    assertTrue(pubItemVO.getMetadata().getTitle().equals("Der neue Titel"));
    assertTrue(pubItemVO.getObject().getLatestVersion().getVersionState().equals(PubItemVersionDbVO.State.PENDING));
    assertTrue(pubItemVO.getObject().getPublicState().equals(PubItemVersionDbVO.State.RELEASED));
    assertTrue(pubItemVO.getObject().getLatestRelease().getVersionNumber() == pubItemVO.getObject().getLatestVersion().getVersionNumber() - 1);
  }

  @Test
  public void updateReleasedItemByDepositorStandardWorkflow() throws Exception {
    super.logMethodName();

    String authenticationToken = loginDepositor();

    PubItemVersionDbVO pubItemVO = createReleasedItemStandardWorkflow();

    pubItemVO.getMetadata().setTitle("Der neue Titel");

    pubItemVO = pubItemService.update(pubItemVO, authenticationToken);

    assertTrue(pubItemVO.getMetadata().getTitle().equals("Der neue Titel"));
    assertTrue(pubItemVO.getObject().getLatestVersion().getVersionState().equals(PubItemVersionDbVO.State.PENDING));
    assertTrue(pubItemVO.getObject().getPublicState().equals(PubItemVersionDbVO.State.RELEASED));
    assertTrue(pubItemVO.getObject().getLatestRelease().getVersionNumber() == pubItemVO.getObject().getLatestVersion().getVersionNumber() - 1);
  }

  @Test
  public void updateReleasedItemByModeratorSimpleWorkflow() throws Exception {
    super.logMethodName();

    String authenticationToken = loginModerator();

    PubItemVersionDbVO pubItemVO = createReleasedItemSimpleWorkflow();

    pubItemVO.getMetadata().setTitle("Der neue Titel");

    pubItemVO = pubItemService.update(pubItemVO, authenticationToken);

    assertTrue(pubItemVO.getMetadata().getTitle().equals("Der neue Titel"));
    assertTrue(pubItemVO.getObject().getLatestVersion().getVersionState().equals(PubItemVersionDbVO.State.SUBMITTED));
    assertTrue(pubItemVO.getObject().getPublicState().equals(PubItemVersionDbVO.State.RELEASED));
    assertTrue(pubItemVO.getObject().getLatestRelease().getVersionNumber() == pubItemVO.getObject().getLatestVersion().getVersionNumber() - 1);
  }

  @Test
  public void updateReleasedItemByModeratorStandardWorkflow() throws Exception {
    super.logMethodName();

    String authenticationToken = loginModerator();

    PubItemVersionDbVO pubItemVO = createReleasedItemStandardWorkflow();

    pubItemVO.getMetadata().setTitle("Der neue Titel");

    pubItemVO = pubItemService.update(pubItemVO, authenticationToken);

    assertTrue(pubItemVO.getMetadata().getTitle().equals("Der neue Titel"));
    assertTrue(pubItemVO.getObject().getPublicState().equals(PubItemVersionDbVO.State.RELEASED));
    assertTrue(pubItemVO.getObject().getLatestVersion().getVersionState().equals(PubItemVersionDbVO.State.SUBMITTED));
    assertTrue(pubItemVO.getObject().getLatestRelease().getVersionNumber() == pubItemVO.getObject().getLatestVersion().getVersionNumber() - 1);
  }

  @Test(expected = AuthorizationException.class)
  public void updateInRevisionItemByModeratorStandardWorkflow() throws Exception {
    super.logMethodName();

    String authenticationToken = loginModerator();

    PubItemVersionDbVO pubItemVO = createInRevisionItemStandardWorkflow();

    pubItemVO.getMetadata().setTitle("Der neue Titel");
    pubItemVO = pubItemService.update(pubItemVO, authenticationToken);
  }

  @Test
  public void updateInRevisionItemByDepositorStandardWorkflow() throws Exception {
    super.logMethodName();

    String authenticationToken = loginDepositor();

    PubItemVersionDbVO pubItemVO = createInRevisionItemStandardWorkflow();

    pubItemVO.getMetadata().setTitle("Der neue Titel");
    pubItemVO = pubItemService.update(pubItemVO, authenticationToken);

    assertTrue(pubItemVO.getMetadata().getTitle().equals("Der neue Titel"));
    assertTrue(pubItemVO.getObject().getPublicState().equals(PubItemVersionDbVO.State.SUBMITTED));
    assertTrue(pubItemVO.getObject().getLatestVersion().getVersionState().equals(PubItemVersionDbVO.State.IN_REVISION));
  }

  @Test
  public void releasePubItemStandardWorkflow() throws Exception {

    super.logMethodName();

    PubItemVersionDbVO pubItemVO = createSubmittedItemStandardWorkflow();

    String authenticationTokenModerator = loginModerator();

    pubItemVO = pubItemService.releasePubItem(pubItemVO.getObjectId(), pubItemVO.getModificationDate(), "testing a release",
        authenticationTokenModerator);

    assertTrue(pubItemVO.getObject().getLatestRelease().getVersionPid() != null);
    assertTrue(pubItemVO.getObject().getObjectPid() != null);
    assertTrue(pubItemVO.getVersionState().equals(PubItemVersionDbVO.State.RELEASED));
    assertTrue(pubItemVO.getVersionNumber() == 1);
    assertTrue("Expected PublicStatus RELEASED - found <" + pubItemVO.getObject().getPublicState() + ">",
        pubItemVO.getObject().getPublicState().equals(PubItemVersionDbVO.State.RELEASED));
    assertTrue(pubItemVO.getObject().getLatestRelease().equals(pubItemVO.getObject().getLatestVersion()));
    assertTrue(pubItemVO.getObject().getLatestVersion().getModifiedBy().getObjectId().equals(MODERATOR_OBJECTID));
  }

  @Test
  public void releasePubItemSimpleWorkflow() throws Exception {

    super.logMethodName();

    PubItemVersionDbVO pubItemVO = createReleasedItemSimpleWorkflow();

    assertTrue(pubItemVO.getObject().getLatestRelease().getVersionPid() != null);
    assertTrue(pubItemVO.getObject().getObjectPid() != null);
    assertTrue(pubItemVO.getVersionState().equals(PubItemVersionDbVO.State.RELEASED));
    assertTrue(pubItemVO.getVersionNumber() == 1);
    assertTrue("Expected PublicStatus RELEASED - found <" + pubItemVO.getObject().getPublicState() + ">",
        pubItemVO.getObject().getPublicState().equals(PubItemVersionDbVO.State.RELEASED));
    assertTrue(pubItemVO.getObject().getLatestRelease().equals(pubItemVO.getObject().getLatestVersion()));
    assertTrue(pubItemVO.getObject().getLatestVersion().getModifiedBy().getObjectId().equals(DEPOSITOR_OBJECTID));
  }

  @Test
  public void withdrawPubItemByModeratorStandardWorkflow() throws Exception {

    super.logMethodName();

    PubItemVersionDbVO pubItemVO = createReleasedItemStandardWorkflow();

    String authenticationTokenModerator = loginModerator();

    pubItemVO = pubItemService.withdrawPubItem(pubItemVO.getObjectId(), pubItemVO.getModificationDate(),
        "Weg damit", authenticationTokenModerator);

    assertTrue("Expected state WITHDRAWN", pubItemVO.getObject().getPublicState().equals(PubItemVersionDbVO.State.WITHDRAWN));
    assertTrue("Wrong or missing withdrawl comment", pubItemVO.getMessage().equals("Weg damit"));
    assertTrue("Expected state RELEASED", pubItemVO.getObject().getLatestVersion().getVersionState().equals(PubItemVersionDbVO.State.RELEASED));
    assertTrue("Expected state RELEASED", pubItemVO.getObject().getLatestRelease().getVersionState().equals(PubItemVersionDbVO.State.RELEASED));
    assertTrue("Expected version number <1> - got <" + pubItemVO.getVersionNumber() + ">",
        pubItemVO.getVersionNumber() == 1);
  }

  @Test
  public void withdrawPubItemByModeratorSimpleWorkflow() throws Exception {

    super.logMethodName();

    PubItemVersionDbVO pubItemVO = createReleasedItemSimpleWorkflow();

    String authenticationTokenModerator = loginModerator();

    pubItemVO = pubItemService.withdrawPubItem(pubItemVO.getObjectId(), pubItemVO.getModificationDate(),
        "Weg damit", authenticationTokenModerator);

    assertTrue("Expected state WITHDRAWN", pubItemVO.getObject().getPublicState().equals(PubItemVersionDbVO.State.WITHDRAWN));
    assertTrue("Wrong or missing withdrawl comment", pubItemVO.getMessage().equals("Weg damit"));
    assertTrue("Expected state RELEASED", pubItemVO.getObject().getLatestVersion().getVersionState().equals(PubItemVersionDbVO.State.RELEASED));
    assertTrue("Expected state RELEASED", pubItemVO.getObject().getLatestRelease().getVersionState().equals(PubItemVersionDbVO.State.RELEASED));
    assertTrue("Expected version number <1> - got <" + pubItemVO.getVersionNumber() + ">",
        pubItemVO.getVersionNumber() == 1);
  }

  @Test
  public void withdrawPubItemByDepositorSimpleWorkflow() throws Exception {

    super.logMethodName();

    PubItemVersionDbVO pubItemVO = createReleasedItemSimpleWorkflow();

    String authenticationTokenDepositor = loginDepositor();

    pubItemVO = pubItemService.withdrawPubItem(pubItemVO.getObjectId(), pubItemVO.getModificationDate(),
        "Weg damit", authenticationTokenDepositor);

    assertTrue("Expected state WITHDRAWN", pubItemVO.getObject().getPublicState().equals(PubItemVersionDbVO.State.WITHDRAWN));
    assertTrue("Wrong or missing withdrawl comment", pubItemVO.getMessage().equals("Weg damit"));
    assertTrue("Expected state RELEASED", pubItemVO.getObject().getLatestVersion().getVersionState().equals(PubItemVersionDbVO.State.RELEASED));
    assertTrue("Expected state RELEASED", pubItemVO.getObject().getLatestRelease().getVersionState().equals(PubItemVersionDbVO.State.RELEASED));
    assertTrue("Expected version number <1> - got <" + pubItemVO.getVersionNumber() + ">",
        pubItemVO.getVersionNumber() == 1);
  }

  @Test(expected = AuthorizationException.class)
  public void withdrawPubItemByDepositorStandardWorkflow() throws Exception {

    super.logMethodName();

    PubItemVersionDbVO pubItemVO = createReleasedItemStandardWorkflow();

    String authenticationTokenDepositor = loginDepositor();

    pubItemVO = pubItemService.withdrawPubItem(pubItemVO.getObjectId(), pubItemVO.getModificationDate(),
        "Weg damit", authenticationTokenDepositor);
  }

  @Test
  public void revisePubItemByModeratorStandardWorkflow() throws Exception {

    super.logMethodName();

    PubItemVersionDbVO pubItemVO = createSubmittedItemStandardWorkflow();

    String authenticationTokenModerator = loginModerator();

    pubItemVO = pubItemService.revisePubItem(pubItemVO.getObjectId(), pubItemVO.getModificationDate(), "Schrott",
        authenticationTokenModerator);

    assertTrue("Expected state IN_REVISION", pubItemVO.getObject().getLatestVersion().getVersionState().equals(PubItemVersionDbVO.State.IN_REVISION));
    assertTrue("Expected state SUBMITTED", pubItemVO.getObject().getPublicState().equals(PubItemVersionDbVO.State.SUBMITTED));
    assertTrue("Expected version number <2> - got <" + pubItemVO.getVersionNumber() + ">",
        pubItemVO.getVersionNumber() == 1);
  }

  @Test
  public void deleteInRevisionItemByDepositorStandardWorkflow() throws Exception {
    super.logMethodName();

    String authenticationToken = loginDepositor();

    PubItemVersionDbVO pubItemVO = createInRevisionItemStandardWorkflow();

    pubItemService.delete(pubItemVO.getObject().getLatestVersion().getObjectId(), authenticationToken);

    assertTrue(pubItemService.get(pubItemVO.getObject().getLatestVersion().getObjectId(), authenticationToken) == null);
  }

  @Test(expected = AuthorizationException.class)
  public void deleteInRevisionItemByModeratorStandardWorkflow() throws Exception {
    super.logMethodName();

    String authenticationToken = loginModerator();

    PubItemVersionDbVO pubItemVO = createInRevisionItemStandardWorkflow();

    pubItemService.delete(pubItemVO.getObject().getLatestVersion().getObjectId(), authenticationToken);
  }

  // --------------------------------------------------------------------- helper methods
  // --------------------------------------------------------------



  private PubItemVersionDbVO getPubItemVersionDbVO(String contextId) {
    PubItemVersionDbVO pubItemVO = new PubItemVersionDbVO();
    CreatorVO creatorVO = new CreatorVO();
    PersonVO personVO = new PersonVO();

    creatorVO.setRole(CreatorRole.AUTHOR);
    personVO.setCompleteName("Hans Meier");
    personVO.setFamilyName("Meier");
    personVO.setGivenName("Hans");
    creatorVO.setPerson(personVO);

    ContextDbRO context = new ContextDbRO();
    context.setObjectId(contextId);
    pubItemVO.getObject().setLatestRelease(new PubItemVersionDbRO());
    pubItemVO.getObject().setLatestVersion(new PubItemVersionDbRO());

    MdsPublicationVO mdsPublicationVO = new MdsPublicationVO();
    mdsPublicationVO.setGenre(MdsPublicationVO.Genre.BOOK);
    mdsPublicationVO.setTitle("Der Inn");
    mdsPublicationVO.setDateAccepted("2017");
    mdsPublicationVO.getCreators().add(creatorVO);

    pubItemVO.setMetadata(mdsPublicationVO);

    return pubItemVO;
  }

  private PubItemVersionDbVO createReleasedItemStandardWorkflow() throws Exception {

    PubItemVersionDbVO pubItemVO = createSubmittedItemStandardWorkflow();

    String authenticationTokenModerator = loginModerator();

    pubItemVO = pubItemService.releasePubItem(pubItemVO.getObjectId(), pubItemVO.getModificationDate(), "testing a release",
        authenticationTokenModerator);

    return pubItemVO;
  }

  private PubItemVersionDbVO createReleasedItemSimpleWorkflow() throws Exception {

    PubItemVersionDbVO pubItemVO = getPubItemVersionDbVO(CTX_SIMPLE);

    String authenticationToken = loginDepositor();
    pubItemVO = pubItemService.create(pubItemVO, authenticationToken);
    pubItemVO = pubItemService.releasePubItem(pubItemVO.getObjectId(), pubItemVO.getModificationDate(), "testing a release",
        authenticationToken);

    return pubItemVO;
  }

  private PubItemVersionDbVO createSubmittedItemStandardWorkflow() throws Exception {
    String authenticationTokenDepositor = loginDepositor();

    PubItemVersionDbVO pubItemVO = getPubItemVersionDbVO(CTX_STANDARD);
    pubItemVO = pubItemService.create(pubItemVO, authenticationTokenDepositor);

    assertTrue(pubItemVO.getObject().getLatestVersion().getVersionState().equals(PubItemVersionDbVO.State.PENDING));
    assertTrue(pubItemVO.getObject().getCreationDate() != null);
    assertTrue(pubItemVO.getObject().getLatestVersion().getModifiedBy() != null);

    pubItemVO = pubItemService.submitPubItem(pubItemVO.getObjectId(), pubItemVO.getModificationDate(), "testing a submit",
        authenticationTokenDepositor);

    assertTrue(pubItemVO.getObject().getLatestVersion().getVersionState().equals(PubItemVersionDbVO.State.SUBMITTED));

    return pubItemVO;
  }

  // Creating an item in version state SUBMITTED in an simple workflow environment can only be done
  // by making a detour over a released item.
  private PubItemVersionDbVO createSubmittedItemSimpleWorkflow() throws Exception {
    String authenticationTokenModerator = loginModerator();

    PubItemVersionDbVO pubItemVO = createReleasedItemSimpleWorkflow();

    pubItemVO.getMetadata().setTitle("Der neue Titel");

    pubItemVO = pubItemService.update(pubItemVO, authenticationTokenModerator);

    assertTrue(pubItemVO.getObject().getPublicState().equals(PubItemVersionDbRO.State.RELEASED));
    assertTrue(pubItemVO.getObject().getLatestVersion().getVersionState().equals(PubItemVersionDbVO.State.SUBMITTED));

    return pubItemVO;
  }


  private PubItemVersionDbVO createInRevisionItemStandardWorkflow() throws Exception {
    String authenticationToken = loginModerator();

    PubItemVersionDbVO pubItemVO = createSubmittedItemStandardWorkflow();

    pubItemVO = pubItemService.revisePubItem(pubItemVO.getObject().getLatestVersion().getObjectId(), pubItemVO.getModificationDate(), "To Revision",
        authenticationToken);

    assertTrue(pubItemVO.getObject().getLatestVersion().getVersionState().equals(PubItemVersionDbVO.State.IN_REVISION));

    return pubItemVO;
  }

}
