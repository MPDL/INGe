package de.mpg.mpdl.inge.model_new.hibernate;

import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsOrganizationalUnitDetailsVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;

public class MdsOrganizationalUnitVOJsonUserType extends
    StringJsonUserType<MdsOrganizationalUnitDetailsVO> {

  public MdsOrganizationalUnitVOJsonUserType() {
    super(MdsOrganizationalUnitDetailsVO.class);
  }

}
