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

import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.Genre;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.SubjectClassification;
import de.mpg.mpdl.inge.service.aa.Principal;
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

    ContextDbVO contextDbVO = contextService.get("ctx_persistent3", authenticationToken);
    assertTrue(contextDbVO != null);

    switch (contextDbVO.getState()) {
      case OPENED:
        contextDbVO = contextService.close("ctx_persistent3", contextDbVO.getLastModificationDate(), authenticationToken);
        assertTrue(contextDbVO.getState().equals(ContextDbVO.State.CLOSED));
        break;
      case CLOSED:
      case CREATED:
        contextDbVO = contextService.open("ctx_persistent3", contextDbVO.getLastModificationDate(), authenticationToken);
        assertTrue(contextDbVO.getState().equals(ContextDbVO.State.OPENED));
      default:
        break;
    }
  }

  @Test(expected = AuthorizationException.class)
  public void openWhenAlreadyOpen() throws Exception {

    super.logMethodName();

    String authenticationToken = loginAdmin();
    assertTrue(authenticationToken != null);

    ContextDbVO contextDbVO = contextService.get("ctx_persistent3", authenticationToken);
    assertTrue(contextDbVO != null);
    assertTrue(contextDbVO.getState().equals(ContextVO.State.OPENED));

    contextDbVO = contextService.open("ctx_persistent3", contextDbVO.getLastModificationDate(), authenticationToken);
  }

  @Test(expected = AuthorizationException.class)
  public void closeWithoutAuthorization() throws Exception {

    super.logMethodName();

    String authenticationToken = loginDepositor();
    assertTrue(authenticationToken != null);

    ContextDbVO contextVO = contextService.get("ctx_persistent3", authenticationToken);
    assertTrue(contextVO != null);
    assertTrue(contextVO.getState().equals(ContextDbVO.State.OPENED));

    contextVO = contextService.close("ctx_persistent3", contextVO.getLastModificationDate(), authenticationToken);
  }

  @Test(expected = AuthenticationException.class)
  public void openWrongAuthentication() throws Exception {

    super.logMethodName();

    Principal principal = userAccountService.login(DEPOSITOR_LOGIN_NAME, "XXXXXXXXXXXXXX");
    assertTrue(principal.getJwToken() != null);

    ContextDbVO contextVO = contextService.get("ctx_persistent3", principal.getJwToken());
    assertTrue(contextVO != null);
    assertTrue(contextVO.getState().equals(ContextDbVO.State.OPENED));

    contextVO = contextService.open("ctx_persistent3", contextVO.getLastModificationDate(), principal.getJwToken());
  }

  @Test
  public void createAndDelete() throws Exception {

    super.logMethodName();

    String authenticationToken = loginAdmin();
    assertTrue(authenticationToken != null);

    ContextDbVO contextVO = contextService.create(getContextVO(), authenticationToken);

    String contextId = contextVO.getObjectId();
    assertTrue(contextVO != null);
    assertTrue(contextId != null);
    assertTrue(contextVO.getState().equals(ContextDbVO.State.CREATED));

    contextService.delete(contextVO.getObjectId(), authenticationToken);

    assertTrue(contextService.get(contextId, authenticationToken) == null);
  }

  @Test
  public void getInvalidId() throws Exception {

    super.logMethodName();

    String authenticationToken = loginAdmin();
    assertTrue(authenticationToken != null);

    ContextDbVO contextVO = contextService.get("srhrtshthsfh", authenticationToken);
    assertTrue(contextVO == null);
  }

  private ContextDbVO getContextVO() {
    ContextDbVO contextVO = new ContextDbVO();


    contextVO.getAllowedGenres().add(Genre.ARTICLE);
    contextVO.getAllowedGenres().add(Genre.BOOK);

    List<SubjectClassification> subjectClassification = new ArrayList<SubjectClassification>();
    contextVO.getAllowedSubjectClassifications().add(SubjectClassification.DDC);
    contextVO.getAllowedSubjectClassifications().add(SubjectClassification.ISO639_3);


    contextVO.setCreator(new AccountUserDbRO());
    contextVO.setName("Test Context");;

    return contextVO;
  }

}
