package de.mpg.mpdl.inge.db.repository;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mpg.mpdl.inge.db.model.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.db.model.valueobjects.AffiliationDbVO;
import de.mpg.mpdl.inge.db.model.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.db.spring_config.JPAConfiguration;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JPAConfiguration.class})
public class RepositoryTest {

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
    assertTrue(ous.size() == 2);

    List<ContextDbVO> contexts = contextRepository.findAll();
    assertTrue(contexts.size() == 2);

    List<AccountUserDbVO> userAccounts = userAccountRepository.findAll();
    assertTrue(userAccounts.size() == 3);
  }

  @Test
  public void findOneAccountUserDbVO() {
    AccountUserDbVO accountUserDbVO = userAccountRepository.findOne("user_3000056");
    assertTrue(accountUserDbVO != null);
  }

  @Test
  public void findOneContextDbVO() {
    ContextDbVO contextDbVO = contextRepository.findOne("ctx_persistent3");
    assertTrue(contextDbVO != null);
  }

  @Test
  public void findOneAffiliationDbVO() {
    AffiliationDbVO affiliationDbVO = organizationRepository.findOne("ou_persistent13");
    assertTrue(affiliationDbVO != null);
  }
  
  @Test
  public void updateLogin() {
    String encodedPassword = "$2a$10$3g.zbUZBGwty2tKCvdk97eitmg6ua2pmpMlh4y2Frmq3dZEssaHMu";
    userLoginRepository.updateLogin("test_depositor", encodedPassword);
    
    String password = userLoginRepository.findPassword("test_depositor");
    
    assertTrue(password != null && password.equals(encodedPassword));
  }

}
