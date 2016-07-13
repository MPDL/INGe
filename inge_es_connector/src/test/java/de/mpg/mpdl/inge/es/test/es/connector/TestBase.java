package de.mpg.mpdl.inge.es.test.es.connector;

import de.mpg.mpdl.inge.model.referenceobjects.AccountUserRO;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsOrganizationalUnitDetailsVO;

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

}
