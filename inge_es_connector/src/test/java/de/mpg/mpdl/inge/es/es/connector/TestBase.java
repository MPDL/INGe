package de.mpg.mpdl.inge.es.es.connector;

import de.mpg.mpdl.inge.model.referenceobjects.AccountUserRO;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsOrganizationalUnitDetailsVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;

public class TestBase {

  public AffiliationVO test_ou() {
    AffiliationVO vo = new AffiliationVO();
    vo.setCreator(new AccountUserRO("/aa/user-account/escidoc:user42"));
    vo.setPublicStatus("opened");
    MdsOrganizationalUnitDetailsVO md = new MdsOrganizationalUnitDetailsVO();
    md.setCity("Munich");
    md.setName("TEST OU");
    md.setType("Test Institution");
    vo.setDefaultMetadata(md);

    return vo;
  }

  public ContextVO test_context() {
    ContextVO vo = new ContextVO();
    vo.setName("Test Context Name");
    vo.setType("Test Context Type");
    vo.setDescription("Test Context Description");

    return vo;
  }
  
  public PubItemVO test_item() {
    PubItemVO vo = new PubItemVO();
    
    return vo;
  }
}
