package de.mpg.mpdl.inge.model.db.hibernate;

import com.fasterxml.jackson.databind.type.TypeFactory;

import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;

public class MdsPublicationVOJsonUserType extends StringJsonUserType {

  public MdsPublicationVOJsonUserType() {
    super(TypeFactory.defaultInstance().constructType(MdsPublicationVO.class));
  }

}
