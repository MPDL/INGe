package de.mpg.mpdl.inge.db.model.hibernate;

import com.fasterxml.jackson.databind.type.TypeFactory;

import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PublicationAdminDescriptorVO;

public class MdsPublicationVOJsonUserType extends StringJsonUserType {

  public MdsPublicationVOJsonUserType() {
    super(TypeFactory.defaultInstance().constructType(MdsPublicationVO.class));
  }

}
