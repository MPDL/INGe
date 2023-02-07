package de.mpg.mpdl.inge.db.repository;

import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mpg.mpdl.inge.db.spring.JPAConfiguration;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.AffiliationDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JPAConfiguration.class})
public class RepositoryTest_ {

  @Autowired
  AuditRepository auditRepository;
  @Autowired
  ContextRepository contextRepository;
  @Autowired
  IdentifierProviderServiceImpl identifierProviderServiceImpl;
  @Autowired
  ItemObjectRepository itemObjectRepository;
  @Autowired
  ItemRepository itemRepository;
  @Autowired
  OrganizationRepository organizationRepository;
  @Autowired
  UserAccountRepository userAccountRepository;
  @Autowired
  UserLoginRepository userLoginRepository;

  @Test
  public void repositoryObjects() {
    assertTrue(auditRepository != null);
    assertTrue(contextRepository != null);
    assertTrue(identifierProviderServiceImpl != null);
    assertTrue(itemObjectRepository != null);
    assertTrue(itemRepository != null);
    assertTrue(organizationRepository != null);
    assertTrue(userAccountRepository != null);
    assertTrue(userLoginRepository != null);
  }

  @Test
  public void findAll() {
    List<AffiliationDbVO> ous = organizationRepository.findAll();
    assertTrue(ous.size() == 3);

    List<ContextDbVO> contexts = contextRepository.findAll();
    assertTrue(contexts.size() == 2);

    List<AccountUserDbVO> userAccounts = userAccountRepository.findAll();
    assertTrue(userAccounts.size() == 5);
  }

  @Test
  public void findOneAccountUserDbVO() {
    AccountUserDbVO accountUserDbVO = userAccountRepository.findById("user_3000056").orElse(null);
    assertTrue(accountUserDbVO != null);
  }

  @Test
  public void findOneContextDbVO() {
    ContextDbVO contextDbVO = contextRepository.findById("ctx_persistent3").orElse(null);
    assertTrue(contextDbVO != null);
  }

  @Test
  public void findOneAffiliationDbVO() {
    AffiliationDbVO affiliationDbVO = organizationRepository.findById("ou_persistent13").orElse(null);
    assertTrue(affiliationDbVO != null);
  }

  @Test
  public void updateLogin() throws Exception {
    String encodedPassword = "$2a$10$3g.zbUZBGwty2tKCvdk97eitmg6ua2pmpMlh4y2Frmq3dZEssaHMu";
    userLoginRepository.updateLogin("test_depositor", encodedPassword, LocalDate.now(), true);

    String password = userLoginRepository.findPassword("test_depositor");

    assertTrue(password != null && password.equals(encodedPassword));
  }

  @Test(expected = Exception.class)
  public void updateLoginWrongLoginname() throws Exception {
    String encodedPassword = "$2a$10$3g.zbUZBGwty2tKCvdk97eitmg6ua2pmpMlh4y2Frmq3dZEssaHMu";
    userLoginRepository.updateLogin("xxxxxxxxxxx", encodedPassword, LocalDate.now(), true);
  }

}
