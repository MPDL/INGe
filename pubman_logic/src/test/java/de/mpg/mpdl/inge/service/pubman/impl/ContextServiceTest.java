package de.mpg.mpdl.inge.service.pubman.impl;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mpg.mpdl.inge.model.referenceobjects.AccountUserRO;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.Genre;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.SubjectClassification;
import de.mpg.mpdl.inge.model.valueobjects.publication.PublicationAdminDescriptorVO;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.pubman.ContextService;
import de.mpg.mpdl.inge.service.spring.AppConfigPubmanLogicTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfigPubmanLogicTest.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ContextServiceTest extends TestBase {

  @Autowired
  ContextService contextService;

  @Test
  public void objects() {

    super.logMethodName();

    assertTrue(contextService != null);
  }

  @Test
  public void openAndClose() throws Exception {

    super.logMethodName();


    String authenticationToken = loginAdmin();
    assertTrue(authenticationToken != null);

    ContextVO contextVO = contextService.get("ctx_persistent3", authenticationToken);
    assertTrue(contextVO != null);

    switch (contextVO.getState()) {
      case OPENED:
        contextVO = contextService.close("ctx_persistent3", contextVO.getLastModificationDate(), authenticationToken);
        assertTrue(contextVO.getState().equals(ContextVO.State.CLOSED));
        break;
      case CLOSED:
      case CREATED:
        contextVO = contextService.open("ctx_persistent3", contextVO.getLastModificationDate(), authenticationToken);
        assertTrue(contextVO.getState().equals(ContextVO.State.OPENED));
      default:
        break;
    }
  }

  @Test(expected = AuthorizationException.class)
  public void openWhenAlreadyOpen() throws Exception {

    super.logMethodName();

    String authenticationToken = loginAdmin();
    assertTrue(authenticationToken != null);

    ContextVO contextVO = contextService.get("ctx_persistent3", authenticationToken);
    assertTrue(contextVO != null);
    assertTrue(contextVO.getState().equals(ContextVO.State.OPENED));

    contextVO = contextService.open("ctx_persistent3", contextVO.getLastModificationDate(), authenticationToken);
  }

  @Test(expected = AuthorizationException.class)
  public void closeWithoutAuthorization() throws Exception {

    super.logMethodName();

    String authenticationToken = loginDepositor();
    assertTrue(authenticationToken != null);

    ContextVO contextVO = contextService.get("ctx_persistent3", authenticationToken);
    assertTrue(contextVO != null);
    assertTrue(contextVO.getState().equals(ContextVO.State.OPENED));

    contextVO = contextService.close("ctx_persistent3", contextVO.getLastModificationDate(), authenticationToken);
  }

  @Test(expected = AuthenticationException.class)
  public void openWrongAuthentication() throws Exception {

    super.logMethodName();

    String authenticationToken = userAccountService.login(DEPOSITOR_LOGIN_NAME, "XXXXXXXXXXXXXX");
    assertTrue(authenticationToken != null);

    ContextVO contextVO = contextService.get("ctx_persistent3", authenticationToken);
    assertTrue(contextVO != null);
    assertTrue(contextVO.getState().equals(ContextVO.State.OPENED));

    contextVO = contextService.open("ctx_persistent3", contextVO.getLastModificationDate(), authenticationToken);
  }

  @Test
  public void createAndDelete() throws Exception {

    super.logMethodName();

    String authenticationToken = loginAdmin();
    assertTrue(authenticationToken != null);

    ContextVO contextVO = contextService.create(getContextVO(), authenticationToken);

    String contextId = contextVO.getReference().getObjectId();
    assertTrue(contextVO != null);
    assertTrue(contextId != null);
    assertTrue(contextVO.getState().equals(ContextVO.State.CREATED));

    contextService.delete(contextVO.getReference().getObjectId(), authenticationToken);

    assertTrue(contextService.get(contextId, authenticationToken) == null);
  }

  @Test
  public void getInvalidId() throws Exception {

    super.logMethodName();

    String authenticationToken = loginAdmin();
    assertTrue(authenticationToken != null);

    ContextVO contextVO = contextService.get("srhrtshthsfh", authenticationToken);
    assertTrue(contextVO == null);
  }

  private ContextVO getContextVO() {
    ContextVO contextVO = new ContextVO();

    PublicationAdminDescriptorVO adminDescriptorVO = new PublicationAdminDescriptorVO();
    List<Genre> genres = new ArrayList<Genre>();
    genres.add(Genre.ARTICLE);
    genres.add(Genre.BOOK);

    List<SubjectClassification> subjectClassification = new ArrayList<SubjectClassification>();
    subjectClassification.add(SubjectClassification.DDC);
    subjectClassification.add(SubjectClassification.ISO639_3);

    adminDescriptorVO.setAllowedGenres(new ArrayList<Genre>());
    adminDescriptorVO.setAllowedSubjectClassifications(subjectClassification);
    adminDescriptorVO.setValidationSchema("xxxx");

    contextVO.setAdminDescriptor(adminDescriptorVO);
    contextVO.setCreator(new AccountUserRO());
    contextVO.setName("Test Context");;

    return contextVO;
  }

}
