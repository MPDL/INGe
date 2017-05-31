package de.mpg.mpdl.inge.db.model.hibernate;

import com.fasterxml.jackson.databind.type.TypeFactory;

import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsOrganizationalUnitDetailsVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PublicationAdminDescriptorVO;

public class MdsOrganizationalUnitVOJsonUserType extends StringJsonUserType {

  public MdsOrganizationalUnitVOJsonUserType() {
    super(TypeFactory.defaultInstance().constructType(MdsOrganizationalUnitDetailsVO.class));
  }

}
