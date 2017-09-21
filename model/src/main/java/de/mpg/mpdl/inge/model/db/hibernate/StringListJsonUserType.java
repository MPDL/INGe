package de.mpg.mpdl.inge.model.db.hibernate;

import java.util.List;

import com.fasterxml.jackson.databind.type.TypeFactory;

public class StringListJsonUserType extends StringJsonUserType {

  public StringListJsonUserType() {
    super(TypeFactory.defaultInstance().constructCollectionType(List.class, String.class));
  }

}
