package de.mpg.mpdl.inge.db.model.hibernate;

import java.util.List;

public class StringListJsonUserType extends StringJsonUserType<List> {

  public StringListJsonUserType() {
    super(List.class);
  }

}
