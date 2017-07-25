package de.mpg.mpdl.inge.db.repository;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mpg.mpdl.inge.db.model.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.db.model.valueobjects.AffiliationDbVO;
import de.mpg.mpdl.inge.db.model.valueobjects.AuditDbVO;
import de.mpg.mpdl.inge.db.model.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.db.model.valueobjects.PubItemObjectDbVO;
import de.mpg.mpdl.inge.db.repository.AuditRepository;
import de.mpg.mpdl.inge.db.repository.ContextRepository;
import de.mpg.mpdl.inge.db.repository.IdentifierProviderServiceImpl;
import de.mpg.mpdl.inge.db.repository.ItemObjectRepository;
import de.mpg.mpdl.inge.db.repository.ItemRepository;
import de.mpg.mpdl.inge.db.repository.OrganizationRepository;
import de.mpg.mpdl.inge.db.repository.UserAccountRepository;
import de.mpg.mpdl.inge.db.repository.UserLoginRepository;
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
  public void test() {
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
  public void findObjects() {
    List<AffiliationDbVO> ous = organizationRepository.findAll();
    assertTrue(ous.size() == 2);

    List<ContextDbVO> contexts = contextRepository.findAll();
    assertTrue(contexts.size() == 2);

    List<AccountUserDbVO> userAccounts = userAccountRepository.findAll();
    assertTrue(userAccounts.size() == 3);


  }
}
