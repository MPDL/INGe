package de.mpg.mpdl.inge.model.db.hibernate;

import com.fasterxml.jackson.databind.type.TypeFactory;

import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsFileVO;

public class MdsFileVOJsonUserType extends StringJsonUserType {

  public MdsFileVOJsonUserType() {
    super(TypeFactory.defaultInstance().constructType(MdsFileVO.class));
  }

}
