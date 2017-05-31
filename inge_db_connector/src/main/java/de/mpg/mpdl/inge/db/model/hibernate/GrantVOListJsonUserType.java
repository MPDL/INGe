package de.mpg.mpdl.inge.db.model.hibernate;

import java.util.List;

import com.fasterxml.jackson.databind.type.TypeFactory;

import de.mpg.mpdl.inge.model.valueobjects.GrantVO;

public class GrantVOListJsonUserType extends StringJsonUserType {

  public GrantVOListJsonUserType() {
    super(TypeFactory.defaultInstance().constructCollectionType(List.class, GrantVO.class));
  }

}
