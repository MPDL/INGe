package de.mpg.mpdl.inge.model_new.hibernate;

import java.util.List;

import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsOrganizationalUnitDetailsVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;

public class StringListJsonUserType extends StringJsonUserType<List> {

  public StringListJsonUserType() {
    super(List.class);
  }

}
