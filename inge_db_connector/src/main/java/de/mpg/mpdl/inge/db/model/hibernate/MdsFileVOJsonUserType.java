package de.mpg.mpdl.inge.db.model.hibernate;

import com.fasterxml.jackson.databind.type.TypeFactory;

import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsFileVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PublicationAdminDescriptorVO;

public class MdsFileVOJsonUserType extends StringJsonUserType {

  public MdsFileVOJsonUserType() {
    super(TypeFactory.defaultInstance().constructType(MdsFileVO.class));
  }

}
