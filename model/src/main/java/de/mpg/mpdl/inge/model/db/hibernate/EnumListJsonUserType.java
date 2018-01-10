package de.mpg.mpdl.inge.model.db.hibernate;

import java.util.List;

import com.fasterxml.jackson.databind.type.TypeFactory;

import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.Genre;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.SubjectClassification;

public class EnumListJsonUserType extends StringJsonUserType {

  public EnumListJsonUserType() {
    super(TypeFactory.defaultInstance().constructCollectionType(List.class, Enum.class));
  }

}
