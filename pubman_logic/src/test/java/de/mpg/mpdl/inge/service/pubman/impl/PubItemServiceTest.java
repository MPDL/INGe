package de.mpg.mpdl.inge.service.pubman.impl;

import static org.junit.Assert.assertTrue;

import jakarta.persistence.EntityManager;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionRO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO.CreatorRole;
import de.mpg.mpdl.inge.model.valueobjects.metadata.OrganizationVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PersonVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.ContextService;
import de.mpg.mpdl.inge.service.pubman.OrganizationService;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.spring.AppConfigPubmanLogicTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfigPubmanLogicTest.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class PubItemServiceTest extends TestBase {

  private static String CTX_SIMPLE = "ctx_2322554";
  private static String CTX_STANDARD = "ctx_persistent3";

  @Autowired
  PubItemService pubItemService;

  @Autowired
  OrganizationService organizationService;

  @Autowired
  ContextService contextService;

  @Autowired
  EntityManager em;

  @Before
  public void setUp() throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    String authenticationToken = loginAdmin();

    organizationService.reindexAll(authenticationToken);
    userAccountService.reindexAll(authenticationToken);
    contextService.reindexAll(authenticationToken);
  }

  @Test
  public void createByDepositor() throws Exception {

    super.logMethodName();

    String authenticationToken = loginDepositor();

    ItemVersionVO itemVersionVO = getItemVersionVO(CTX_SIMPLE);
    itemVersionVO = pubItemService.create(itemVersionVO, authenticationToken);

    assertTrue("Objectid expected after create", itemVersionVO.getObject().getLatestVersion().getObjectId() != null
        && !"".equals(itemVersionVO.getObject().getLatestVersion().getObjectId()));
    assertTrue("Create ItemVersionVO failed", itemVersionVO != null);
    assertTrue("Creation date missing in ItemVersionVO", itemVersionVO.getObject().getCreationDate() != null);
    assertTrue("Context missing or wrong  context id",
        itemVersionVO.getObject().getContext() != null && itemVersionVO.getObject().getContext().getObjectId().equals(CTX_SIMPLE));
    assertTrue("Expected 1 creator in ItemVersionVO - found <" + itemVersionVO.getMetadata().getCreators().size() + ">",
        itemVersionVO.getMetadata().getCreators().size() == 1);
    assertTrue(itemVersionVO.getMetadata().getCreators().get(0) != null);
    assertTrue("Modification date missing in ItemVersionVO", itemVersionVO.getObject().getLatestVersion().getModificationDate() != null);
    assertTrue("Expected VersionStatus PENDING - found <" + itemVersionVO.getObject().getLatestVersion().getVersionState() + ">",
        itemVersionVO.getObject().getLatestVersion().getVersionState().equals(ItemVersionVO.State.PENDING));
    assertTrue("Expected PublicStatus PENDING - found <" + itemVersionVO.getObject().getPublicState() + ">",
        itemVersionVO.getObject().getPublicState().equals(ItemVersionVO.State.PENDING));
    assertTrue("Wrong owner", itemVersionVO.getObject().getCreator().getObjectId().equals(DEPOSITOR_OBJECTID));
  }

  @Test(expected = AuthorizationException.class)
  public void createByModerator() throws Exception {

    String authenticationToken = loginModerator();

    ItemVersionVO itemVersionVO = getItemVersionVO(CTX_SIMPLE);
    itemVersionVO = pubItemService.create(itemVersionVO, authenticationToken);
  }

  @Test
  public void createAndDeleteByDepositor() throws Exception {

    super.logMethodName();

    String authenticationToken = loginDepositor();

    ItemVersionVO itemVersionVO = getItemVersionVO(CTX_SIMPLE);
    itemVersionVO = pubItemService.create(itemVersionVO, authenticationToken);

    pubItemService.delete(itemVersionVO.getObjectId(), authenticationToken);

    itemVersionVO = pubItemService.get(itemVersionVO.getObjectId(), authenticationToken);

    assertTrue("Found item even though it has been deleted in state PENDING!", itemVersionVO == null);
  }


  @Test
  @Ignore
  public void createAndDeleteByAdmin() throws Exception {

    super.logMethodName();

    String authenticationToken = loginAdmin();

    ItemVersionVO itemVersionVO = getItemVersionVO(CTX_SIMPLE);
    itemVersionVO = pubItemService.create(itemVersionVO, authenticationToken);

    itemVersionVO =
        pubItemService.submitPubItem(itemVersionVO.getObjectId(), itemVersionVO.getModificationDate(), "test submit", authenticationToken);
    itemVersionVO = pubItemService.releasePubItem(itemVersionVO.getObjectId(), itemVersionVO.getModificationDate(), "test release",
        authenticationToken);

    itemVersionVO = pubItemService.update(itemVersionVO, authenticationToken);

    itemVersionVO =
        pubItemService.submitPubItem(itemVersionVO.getObjectId(), itemVersionVO.getModificationDate(), "test submit", authenticationToken);
    itemVersionVO = pubItemService.releasePubItem(itemVersionVO.getObjectId(), itemVersionVO.getModificationDate(), "test release",
        authenticationToken);

    itemVersionVO = pubItemService.update(itemVersionVO, authenticationToken);


    pubItemService.delete(itemVersionVO.getObjectId(), authenticationToken);


    itemVersionVO = pubItemService.get(itemVersionVO.getObjectId(), authenticationToken);


    assertTrue("Found item even though it has been deleted!", itemVersionVO == null);


  }

  @Test(expected = AuthorizationException.class)
  public void createByDepositorAndDeleteByModerator() throws Exception {

    super.logMethodName();

    String authenticationTokenDepositor = loginDepositor();
    String authenticationTokenModerator = loginModerator();

    ItemVersionVO itemVersionVO = getItemVersionVO(CTX_SIMPLE);
    itemVersionVO = pubItemService.create(itemVersionVO, authenticationTokenDepositor);

    pubItemService.delete(itemVersionVO.getObjectId(), authenticationTokenModerator);
  }

  @Test
  public void submitCreatedItemByDepositorStandardWorkflow() throws Exception {
    super.logMethodName();

    String authenticationToken = loginDepositor();

    ItemVersionVO itemVersionVO = getItemVersionVO(CTX_STANDARD);
    itemVersionVO = pubItemService.create(itemVersionVO, authenticationToken);

    assertTrue(itemVersionVO.getObject().getLatestVersion().getVersionState().equals(ItemVersionVO.State.PENDING));

    itemVersionVO = pubItemService.submitPubItem(itemVersionVO.getObjectId(), itemVersionVO.getModificationDate(), "testing a submit",
        authenticationToken);

    assertTrue("Expected VersionStatus SUBMITTED - found <" + itemVersionVO.getObject().getLatestVersion().getVersionState() + ">",
        itemVersionVO.getObject().getLatestVersion().getVersionState().equals(ItemVersionVO.State.SUBMITTED));
    assertTrue("Expected PublicStatus SUBMITTED - found <" + itemVersionVO.getObject().getPublicState() + ">",
        itemVersionVO.getObject().getPublicState().equals(ItemVersionVO.State.SUBMITTED));
    assertTrue("Wrong owner", itemVersionVO.getObject().getCreator().getObjectId().equals(DEPOSITOR_OBJECTID));
    assertTrue(itemVersionVO.getObject().getLatestVersion().getModifier() != null);
    assertTrue(itemVersionVO.getObject().getLatestVersion().getVersionNumber() == 1);
  }

  @Test(expected = AuthorizationException.class)
  public void submitCreatedItemByDepositorSimpleWorkflow() throws Exception {
    super.logMethodName();

    String authenticationToken = loginDepositor();

    ItemVersionVO itemVersionVO = getItemVersionVO(CTX_SIMPLE);
    itemVersionVO = pubItemService.create(itemVersionVO, authenticationToken);

    assertTrue(itemVersionVO.getObject().getLatestVersion().getVersionState().equals(ItemVersionVO.State.PENDING));

    itemVersionVO = pubItemService.submitPubItem(itemVersionVO.getObjectId(), itemVersionVO.getModificationDate(), "testing a submit",
        authenticationToken);
  }

  @Test(expected = AuthorizationException.class)
  public void submitCreatedItemByModeratorSimpleWorkflow() throws Exception {
    super.logMethodName();

    String authenticationToken = loginModerator();

    ItemVersionVO itemVersionVO = getItemVersionVO(CTX_SIMPLE);
    itemVersionVO = pubItemService.create(itemVersionVO, authenticationToken);

    assertTrue(itemVersionVO.getObject().getLatestVersion().getVersionState().equals(ItemVersionVO.State.PENDING));

    itemVersionVO = pubItemService.submitPubItem(itemVersionVO.getObjectId(), itemVersionVO.getModificationDate(), "testing a submit",
        authenticationToken);
  }

  @Test(expected = AuthorizationException.class)
  public void submitInRevisionItemByModeratorStandardWorkflow() throws Exception {
    super.logMethodName();

    String authenticationToken = loginModerator();

    ItemVersionVO itemVersionVO = createInRevisionItemStandardWorkflow();

    itemVersionVO = pubItemService.submitPubItem(itemVersionVO.getObjectId(), itemVersionVO.getModificationDate(),
        "testing submit of an item of state IN_REVISION", authenticationToken);

    assertTrue(itemVersionVO.getObject().getPublicState().equals(ItemVersionVO.State.SUBMITTED));
    assertTrue(itemVersionVO.getObject().getLatestVersion().getVersionState().equals(ItemVersionVO.State.SUBMITTED));
    assertTrue(itemVersionVO.getObject().getLatestVersion().getVersionNumber() == 1);
  }

  @Test
  public void submitInRevisionItemByDepositorStandardWorkflow() throws Exception {
    super.logMethodName();

    String authenticationTokenDepositor = loginDepositor();

    ItemVersionVO itemVersionVO = createInRevisionItemStandardWorkflow();

    itemVersionVO = pubItemService.submitPubItem(itemVersionVO.getObjectId(), itemVersionVO.getModificationDate(),
        "testing submit of an item of state IN_REVISION", authenticationTokenDepositor);

    assertTrue(itemVersionVO.getObject().getPublicState().equals(ItemVersionVO.State.SUBMITTED));
    assertTrue(itemVersionVO.getObject().getLatestVersion().getVersionState().equals(ItemVersionVO.State.SUBMITTED));
    assertTrue(itemVersionVO.getObject().getLatestVersion().getVersionNumber() == 1);
  }

  @Test
  public void updateCreatedItemByDepositorSimpleWorkflow() throws Exception {
    super.logMethodName();

    String authenticationToken = loginDepositor();

    ItemVersionVO itemVersionVO = getItemVersionVO(CTX_SIMPLE);
    itemVersionVO = pubItemService.create(itemVersionVO, authenticationToken);

    assertTrue(itemVersionVO.getObject().getLatestVersion().getVersionState().equals(ItemVersionVO.State.PENDING));

    itemVersionVO.getMetadata().setTitle("Der neue Titel");

    itemVersionVO = pubItemService.update(itemVersionVO, authenticationToken);

    assertTrue(itemVersionVO.getMetadata().getTitle().equals("Der neue Titel"));
    assertTrue(itemVersionVO.getObject().getPublicState().equals(ItemVersionVO.State.PENDING));
    assertTrue(itemVersionVO.getObject().getLatestVersion().getVersionState().equals(ItemVersionVO.State.PENDING));
    assertTrue(itemVersionVO.getObject().getLatestVersion().getVersionNumber() == 1);
  }

  @Test
  public void updateCreatedItemByDepositorStandardWorkflow() throws Exception {
    super.logMethodName();

    String authenticationToken = loginDepositor();

    ItemVersionVO itemVersionVO = getItemVersionVO(CTX_STANDARD);
    itemVersionVO = pubItemService.create(itemVersionVO, authenticationToken);

    assertTrue(itemVersionVO.getObject().getLatestVersion().getVersionState().equals(ItemVersionVO.State.PENDING));

    itemVersionVO.getMetadata().setTitle("Der neue Titel");

    itemVersionVO = pubItemService.update(itemVersionVO, authenticationToken);

    assertTrue(itemVersionVO.getMetadata().getTitle().equals("Der neue Titel"));
    assertTrue(itemVersionVO.getObject().getPublicState().equals(ItemVersionVO.State.PENDING));
    assertTrue(itemVersionVO.getObject().getLatestVersion().getVersionState().equals(ItemVersionVO.State.PENDING));
    assertTrue(itemVersionVO.getObject().getLatestVersion().getVersionNumber() == 1);
  }

  @Test(expected = AuthorizationException.class)
  public void updateCreatedItemByModeratorSimpleWorkflow() throws Exception {
    super.logMethodName();

    String authenticationTokenDepositor = loginDepositor();
    String authenticationTokenModerator = loginModerator();

    ItemVersionVO itemVersionVO = getItemVersionVO(CTX_SIMPLE);
    itemVersionVO = pubItemService.create(itemVersionVO, authenticationTokenDepositor);

    assertTrue(itemVersionVO.getObject().getLatestVersion().getVersionState().equals(ItemVersionVO.State.PENDING));

    itemVersionVO.getMetadata().setTitle("Der neue Titel");

    itemVersionVO = pubItemService.update(itemVersionVO, authenticationTokenModerator);
  }

  @Test(expected = AuthorizationException.class)
  public void updateCreatedItemByModeratorStandardWorkflow() throws Exception {
    super.logMethodName();

    String authenticationTokenDepositor = loginDepositor();
    String authenticationTokenModerator = loginModerator();

    ItemVersionVO itemVersionVO = getItemVersionVO(CTX_STANDARD);
    itemVersionVO = pubItemService.create(itemVersionVO, authenticationTokenDepositor);

    assertTrue(itemVersionVO.getObject().getLatestVersion().getVersionState().equals(ItemVersionVO.State.PENDING));

    itemVersionVO.getMetadata().setTitle("Der neue Titel");

    itemVersionVO = pubItemService.update(itemVersionVO, authenticationTokenModerator);
  }

  @Test(expected = AuthorizationException.class)
  public void updateSubmittedItemByDepositorStandardWorkflow() throws Exception {
    super.logMethodName();

    String authenticationToken = loginDepositor();

    ItemVersionVO itemVersionVO = createSubmittedItemStandardWorkflow();

    assertTrue(itemVersionVO.getObject().getLatestVersion().getVersionState().equals(ItemVersionRO.State.SUBMITTED));

    itemVersionVO.getMetadata().setTitle("Der neue Titel");

    itemVersionVO = pubItemService.update(itemVersionVO, authenticationToken);
  }

  @Test
  public void updateSubmittedItemByModeratorSimpleWorkflow() throws Exception {
    super.logMethodName();

    String authenticationTokenModerator = loginModerator();

    ItemVersionVO itemVersionVO = createSubmittedItemSimpleWorkflow();

    itemVersionVO.getMetadata().setTitle("Der neue Titel");

    itemVersionVO = pubItemService.update(itemVersionVO, authenticationTokenModerator);

    assertTrue(itemVersionVO.getMetadata().getTitle().equals("Der neue Titel"));
    assertTrue(itemVersionVO.getObject().getPublicState().equals(ItemVersionVO.State.RELEASED));
    assertTrue(itemVersionVO.getObject().getLatestVersion().getVersionState().equals(ItemVersionVO.State.SUBMITTED));
    assertTrue("Expected version number <2> - got <" + itemVersionVO.getVersionNumber() + ">", itemVersionVO.getVersionNumber() == 2);
  }

  @Test
  public void updateSubmittedItemByModeratorStandardWorkflow() throws Exception {
    super.logMethodName();

    String authenticationTokenModerator = loginModerator();

    ItemVersionVO itemVersionVO = createSubmittedItemStandardWorkflow();

    itemVersionVO.getMetadata().setTitle("Der neue Titel");

    itemVersionVO = pubItemService.update(itemVersionVO, authenticationTokenModerator);

    assertTrue(itemVersionVO.getMetadata().getTitle().equals("Der neue Titel"));
    assertTrue(itemVersionVO.getObject().getPublicState().equals(ItemVersionVO.State.SUBMITTED));
    assertTrue(itemVersionVO.getObject().getLatestVersion().getVersionState().equals(ItemVersionVO.State.SUBMITTED));
    assertTrue("Expected version number <1> - got <" + itemVersionVO.getVersionNumber() + ">", itemVersionVO.getVersionNumber() == 1);
  }

  @Test
  public void updateReleasedItemByDepositorSimpleWorkflow() throws Exception {
    super.logMethodName();

    String authenticationToken = loginDepositor();

    ItemVersionVO itemVersionVO = createReleasedItemSimpleWorkflow();

    itemVersionVO.getMetadata().setTitle("Der neue Titel");

    itemVersionVO = pubItemService.update(itemVersionVO, authenticationToken);

    assertTrue(itemVersionVO.getMetadata().getTitle().equals("Der neue Titel"));
    assertTrue(itemVersionVO.getObject().getLatestVersion().getVersionState().equals(ItemVersionVO.State.PENDING));
    assertTrue(itemVersionVO.getObject().getPublicState().equals(ItemVersionVO.State.RELEASED));
    assertTrue(itemVersionVO.getObject().getLatestRelease()
        .getVersionNumber() == itemVersionVO.getObject().getLatestVersion().getVersionNumber() - 1);
  }

  @Test
  public void updateReleasedItemByDepositorStandardWorkflow() throws Exception {
    super.logMethodName();

    String authenticationToken = loginDepositor();

    ItemVersionVO itemVersionVO = createReleasedItemStandardWorkflow();

    itemVersionVO.getMetadata().setTitle("Der neue Titel");

    itemVersionVO = pubItemService.update(itemVersionVO, authenticationToken);

    assertTrue(itemVersionVO.getMetadata().getTitle().equals("Der neue Titel"));
    assertTrue(itemVersionVO.getObject().getLatestVersion().getVersionState().equals(ItemVersionVO.State.PENDING));
    assertTrue(itemVersionVO.getObject().getPublicState().equals(ItemVersionVO.State.RELEASED));
    assertTrue(itemVersionVO.getObject().getLatestRelease()
        .getVersionNumber() == itemVersionVO.getObject().getLatestVersion().getVersionNumber() - 1);
  }

  @Test
  public void updateReleasedItemByModeratorSimpleWorkflow() throws Exception {
    super.logMethodName();

    String authenticationToken = loginModerator();

    ItemVersionVO itemVersionVO = createReleasedItemSimpleWorkflow();

    itemVersionVO.getMetadata().setTitle("Der neue Titel");

    itemVersionVO = pubItemService.update(itemVersionVO, authenticationToken);

    assertTrue(itemVersionVO.getMetadata().getTitle().equals("Der neue Titel"));
    assertTrue(itemVersionVO.getObject().getLatestVersion().getVersionState().equals(ItemVersionVO.State.SUBMITTED));
    assertTrue(itemVersionVO.getObject().getPublicState().equals(ItemVersionVO.State.RELEASED));
    assertTrue(itemVersionVO.getObject().getLatestRelease()
        .getVersionNumber() == itemVersionVO.getObject().getLatestVersion().getVersionNumber() - 1);
  }

  @Test
  public void updateReleasedItemByModeratorStandardWorkflow() throws Exception {
    super.logMethodName();

    String authenticationToken = loginModerator();

    ItemVersionVO itemVersionVO = createReleasedItemStandardWorkflow();

    itemVersionVO.getMetadata().setTitle("Der neue Titel");

    itemVersionVO = pubItemService.update(itemVersionVO, authenticationToken);

    assertTrue(itemVersionVO.getMetadata().getTitle().equals("Der neue Titel"));
    assertTrue(itemVersionVO.getObject().getPublicState().equals(ItemVersionVO.State.RELEASED));
    assertTrue(itemVersionVO.getObject().getLatestVersion().getVersionState().equals(ItemVersionVO.State.SUBMITTED));
    assertTrue(itemVersionVO.getObject().getLatestRelease()
        .getVersionNumber() == itemVersionVO.getObject().getLatestVersion().getVersionNumber() - 1);
  }

  @Test(expected = AuthorizationException.class)
  public void updateInRevisionItemByModeratorStandardWorkflow() throws Exception {
    super.logMethodName();

    String authenticationToken = loginModerator();

    ItemVersionVO itemVersionVO = createInRevisionItemStandardWorkflow();

    itemVersionVO.getMetadata().setTitle("Der neue Titel");
    itemVersionVO = pubItemService.update(itemVersionVO, authenticationToken);
  }

  @Test
  public void updateInRevisionItemByDepositorStandardWorkflow() throws Exception {
    super.logMethodName();

    String authenticationToken = loginDepositor();

    ItemVersionVO itemVersionVO = createInRevisionItemStandardWorkflow();

    itemVersionVO.getMetadata().setTitle("Der neue Titel");
    itemVersionVO = pubItemService.update(itemVersionVO, authenticationToken);

    assertTrue(itemVersionVO.getMetadata().getTitle().equals("Der neue Titel"));
    assertTrue(itemVersionVO.getObject().getPublicState().equals(ItemVersionVO.State.SUBMITTED));
    assertTrue(itemVersionVO.getObject().getLatestVersion().getVersionState().equals(ItemVersionVO.State.IN_REVISION));
  }

  @Test
  public void releasePubItemStandardWorkflow() throws Exception {

    super.logMethodName();

    ItemVersionVO itemVersionVO = createSubmittedItemStandardWorkflow();

    String authenticationTokenModerator = loginModerator();

    itemVersionVO = pubItemService.releasePubItem(itemVersionVO.getObjectId(), itemVersionVO.getModificationDate(), "testing a release",
        authenticationTokenModerator);

    assertTrue(itemVersionVO.getObject().getLatestRelease().getVersionPid() != null);
    assertTrue(itemVersionVO.getObject().getObjectPid() != null);
    assertTrue(itemVersionVO.getVersionState().equals(ItemVersionVO.State.RELEASED));
    assertTrue(itemVersionVO.getVersionNumber() == 1);
    assertTrue("Expected PublicStatus RELEASED - found <" + itemVersionVO.getObject().getPublicState() + ">",
        itemVersionVO.getObject().getPublicState().equals(ItemVersionVO.State.RELEASED));
    assertTrue(itemVersionVO.getObject().getLatestRelease().equals(itemVersionVO.getObject().getLatestVersion()));
    assertTrue(itemVersionVO.getObject().getLatestVersion().getModifier().getObjectId().equals(MODERATOR_OBJECTID));
  }

  @Test
  public void releasePubItemSimpleWorkflow() throws Exception {

    super.logMethodName();

    ItemVersionVO itemVersionVO = createReleasedItemSimpleWorkflow();

    assertTrue(itemVersionVO.getObject().getLatestRelease().getVersionPid() != null);
    assertTrue(itemVersionVO.getObject().getObjectPid() != null);
    assertTrue(itemVersionVO.getVersionState().equals(ItemVersionVO.State.RELEASED));
    assertTrue(itemVersionVO.getVersionNumber() == 1);
    assertTrue("Expected PublicStatus RELEASED - found <" + itemVersionVO.getObject().getPublicState() + ">",
        itemVersionVO.getObject().getPublicState().equals(ItemVersionVO.State.RELEASED));
    assertTrue(itemVersionVO.getObject().getLatestRelease().equals(itemVersionVO.getObject().getLatestVersion()));
    assertTrue(itemVersionVO.getObject().getLatestVersion().getModifier().getObjectId().equals(DEPOSITOR_OBJECTID));
  }

  @Test
  public void withdrawPubItemByModeratorStandardWorkflow() throws Exception {

    super.logMethodName();

    ItemVersionVO itemVersionVO = createReleasedItemStandardWorkflow();

    String authenticationTokenModerator = loginModerator();

    itemVersionVO = pubItemService.withdrawPubItem(itemVersionVO.getObjectId(), itemVersionVO.getModificationDate(), "Weg damit",
        authenticationTokenModerator);

    assertTrue("Expected state WITHDRAWN", itemVersionVO.getObject().getPublicState().equals(ItemVersionVO.State.WITHDRAWN));
    assertTrue("Wrong or missing withdrawl comment", itemVersionVO.getMessage().equals("Weg damit"));
    assertTrue("Expected state RELEASED",
        itemVersionVO.getObject().getLatestVersion().getVersionState().equals(ItemVersionVO.State.RELEASED));
    assertTrue("Expected state RELEASED",
        itemVersionVO.getObject().getLatestRelease().getVersionState().equals(ItemVersionVO.State.RELEASED));
    assertTrue("Expected version number <1> - got <" + itemVersionVO.getVersionNumber() + ">", itemVersionVO.getVersionNumber() == 1);
  }

  @Test
  public void withdrawPubItemByModeratorSimpleWorkflow() throws Exception {

    super.logMethodName();

    ItemVersionVO itemVersionVO = createReleasedItemSimpleWorkflow();

    String authenticationTokenModerator = loginModerator();

    itemVersionVO = pubItemService.withdrawPubItem(itemVersionVO.getObjectId(), itemVersionVO.getModificationDate(), "Weg damit",
        authenticationTokenModerator);

    assertTrue("Expected state WITHDRAWN", itemVersionVO.getObject().getPublicState().equals(ItemVersionVO.State.WITHDRAWN));
    assertTrue("Wrong or missing withdrawl comment", itemVersionVO.getMessage().equals("Weg damit"));
    assertTrue("Expected state RELEASED",
        itemVersionVO.getObject().getLatestVersion().getVersionState().equals(ItemVersionVO.State.RELEASED));
    assertTrue("Expected state RELEASED",
        itemVersionVO.getObject().getLatestRelease().getVersionState().equals(ItemVersionVO.State.RELEASED));
    assertTrue("Expected version number <1> - got <" + itemVersionVO.getVersionNumber() + ">", itemVersionVO.getVersionNumber() == 1);
  }

  @Test
  public void withdrawPubItemByDepositorSimpleWorkflow() throws Exception {

    super.logMethodName();

    ItemVersionVO itemVersionVO = createReleasedItemSimpleWorkflow();

    String authenticationTokenDepositor = loginDepositor();

    itemVersionVO = pubItemService.withdrawPubItem(itemVersionVO.getObjectId(), itemVersionVO.getModificationDate(), "Weg damit",
        authenticationTokenDepositor);

    assertTrue("Expected state WITHDRAWN", itemVersionVO.getObject().getPublicState().equals(ItemVersionVO.State.WITHDRAWN));
    assertTrue("Wrong or missing withdrawl comment", itemVersionVO.getMessage().equals("Weg damit"));
    assertTrue("Expected state RELEASED",
        itemVersionVO.getObject().getLatestVersion().getVersionState().equals(ItemVersionVO.State.RELEASED));
    assertTrue("Expected state RELEASED",
        itemVersionVO.getObject().getLatestRelease().getVersionState().equals(ItemVersionVO.State.RELEASED));
    assertTrue("Expected version number <1> - got <" + itemVersionVO.getVersionNumber() + ">", itemVersionVO.getVersionNumber() == 1);
  }

  @Test(expected = AuthorizationException.class)
  public void withdrawPubItemByDepositorStandardWorkflow() throws Exception {

    super.logMethodName();

    ItemVersionVO itemVersionVO = createReleasedItemStandardWorkflow();

    String authenticationTokenDepositor = loginDepositor();

    itemVersionVO = pubItemService.withdrawPubItem(itemVersionVO.getObjectId(), itemVersionVO.getModificationDate(), "Weg damit",
        authenticationTokenDepositor);
  }

  @Test
  public void revisePubItemByModeratorStandardWorkflow() throws Exception {

    super.logMethodName();

    ItemVersionVO itemVersionVO = createSubmittedItemStandardWorkflow();

    String authenticationTokenModerator = loginModerator();

    itemVersionVO = pubItemService.revisePubItem(itemVersionVO.getObjectId(), itemVersionVO.getModificationDate(), "Schrott",
        authenticationTokenModerator);

    assertTrue("Expected state IN_REVISION",
        itemVersionVO.getObject().getLatestVersion().getVersionState().equals(ItemVersionVO.State.IN_REVISION));
    assertTrue("Expected state SUBMITTED", itemVersionVO.getObject().getPublicState().equals(ItemVersionVO.State.SUBMITTED));
    assertTrue("Expected version number <2> - got <" + itemVersionVO.getVersionNumber() + ">", itemVersionVO.getVersionNumber() == 1);
  }

  @Test
  public void deleteInRevisionItemByDepositorStandardWorkflow() throws Exception {
    super.logMethodName();

    String authenticationToken = loginDepositor();

    ItemVersionVO itemVersionVO = createInRevisionItemStandardWorkflow();

    pubItemService.delete(itemVersionVO.getObject().getLatestVersion().getObjectId(), authenticationToken);

    assertTrue(pubItemService.get(itemVersionVO.getObject().getLatestVersion().getObjectId(), authenticationToken) == null);
  }

  @Test
  public void deleteInRevisionItemByModeratorStandardWorkflow() throws Exception {
    super.logMethodName();

    String authenticationToken = loginModerator();

    ItemVersionVO itemVersionVO = createInRevisionItemStandardWorkflow();

    pubItemService.delete(itemVersionVO.getObject().getLatestVersion().getObjectId(), authenticationToken);

    assertTrue(pubItemService.get(itemVersionVO.getObject().getLatestVersion().getObjectId(), authenticationToken) == null);
  }

  // --------------------------------------------------------------------- helper methods
  // --------------------------------------------------------------



  private ItemVersionVO getItemVersionVO(String contextId) {
    ItemVersionVO itemVersionVO = new ItemVersionVO();
    CreatorVO creatorVO = new CreatorVO();
    PersonVO personVO = new PersonVO();

    creatorVO.setRole(CreatorRole.AUTHOR);
    personVO.setCompleteName("Hans Meier");
    personVO.setFamilyName("Meier");
    personVO.setGivenName("Hans");
    creatorVO.setPerson(personVO);

    OrganizationVO organizationVO = new OrganizationVO();
    organizationVO.setName("Test Orga");
    creatorVO.setOrganization(organizationVO);

    ContextDbRO context = new ContextDbRO();
    context.setObjectId(contextId);

    itemVersionVO.getObject().setContext(context);
    itemVersionVO.getObject().setLatestRelease(new ItemVersionRO());
    itemVersionVO.getObject().setLatestVersion(new ItemVersionRO());

    MdsPublicationVO mdsPublicationVO = new MdsPublicationVO();
    mdsPublicationVO.setGenre(MdsPublicationVO.Genre.BOOK);
    mdsPublicationVO.setTitle("Der Inn");
    mdsPublicationVO.setDateAccepted("2017");
    mdsPublicationVO.getCreators().add(creatorVO);

    itemVersionVO.setMetadata(mdsPublicationVO);

    return itemVersionVO;
  }

  private ItemVersionVO createReleasedItemStandardWorkflow() throws Exception {

    ItemVersionVO itemVersionVO = createSubmittedItemStandardWorkflow();

    String authenticationTokenModerator = loginModerator();

    itemVersionVO = pubItemService.releasePubItem(itemVersionVO.getObjectId(), itemVersionVO.getModificationDate(), "testing a release",
        authenticationTokenModerator);

    return itemVersionVO;
  }

  private ItemVersionVO createReleasedItemSimpleWorkflow() throws Exception {

    ItemVersionVO itemVersionVO = getItemVersionVO(CTX_SIMPLE);

    String authenticationToken = loginDepositor();
    itemVersionVO = pubItemService.create(itemVersionVO, authenticationToken);
    itemVersionVO = pubItemService.releasePubItem(itemVersionVO.getObjectId(), itemVersionVO.getModificationDate(), "testing a release",
        authenticationToken);

    return itemVersionVO;
  }

  private ItemVersionVO createSubmittedItemStandardWorkflow() throws Exception {
    String authenticationTokenDepositor = loginDepositor();

    ItemVersionVO itemVersionVO = getItemVersionVO(CTX_STANDARD);
    itemVersionVO = pubItemService.create(itemVersionVO, authenticationTokenDepositor);

    assertTrue(itemVersionVO.getObject().getLatestVersion().getVersionState().equals(ItemVersionVO.State.PENDING));
    assertTrue(itemVersionVO.getObject().getCreationDate() != null);
    assertTrue(itemVersionVO.getObject().getLatestVersion().getModifier() != null);

    itemVersionVO = pubItemService.submitPubItem(itemVersionVO.getObjectId(), itemVersionVO.getModificationDate(), "testing a submit",
        authenticationTokenDepositor);

    assertTrue(itemVersionVO.getObject().getLatestVersion().getVersionState().equals(ItemVersionVO.State.SUBMITTED));

    return itemVersionVO;
  }

  // Creating an item in version state SUBMITTED in an simple workflow environment can only be done
  // by making a detour over a released item.
  private ItemVersionVO createSubmittedItemSimpleWorkflow() throws Exception {
    String authenticationTokenModerator = loginModerator();

    ItemVersionVO itemVersionVO = createReleasedItemSimpleWorkflow();

    itemVersionVO.getMetadata().setTitle("Der neue Titel");

    itemVersionVO = pubItemService.update(itemVersionVO, authenticationTokenModerator);

    assertTrue(itemVersionVO.getObject().getPublicState().equals(ItemVersionRO.State.RELEASED));
    assertTrue(itemVersionVO.getObject().getLatestVersion().getVersionState().equals(ItemVersionVO.State.SUBMITTED));

    return itemVersionVO;
  }


  private ItemVersionVO createInRevisionItemStandardWorkflow() throws Exception {
    String authenticationToken = loginModerator();

    ItemVersionVO itemVersionVO = createSubmittedItemStandardWorkflow();

    itemVersionVO = pubItemService.revisePubItem(itemVersionVO.getObject().getLatestVersion().getObjectId(),
        itemVersionVO.getModificationDate(), "To Revision", authenticationToken);

    assertTrue(itemVersionVO.getObject().getLatestVersion().getVersionState().equals(ItemVersionVO.State.IN_REVISION));

    return itemVersionVO;
  }

}
