package de.mpg.mpdl.inge.db.model.hibernate;

import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsOrganizationalUnitDetailsVO;

public class MdsOrganizationalUnitVOJsonUserType extends
    StringJsonUserType<MdsOrganizationalUnitDetailsVO> {

  public MdsOrganizationalUnitVOJsonUserType() {
    super(MdsOrganizationalUnitDetailsVO.class);
  }

}
