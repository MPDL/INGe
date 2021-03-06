package de.mpg.mpdl.inge.model.db.hibernate;

import java.util.List;

import com.fasterxml.jackson.databind.type.TypeFactory;

import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.SubjectClassification;

public class SubjectClassificationListJsonUserType extends StringJsonUserType {

  public SubjectClassificationListJsonUserType() {
    super(TypeFactory.defaultInstance().constructCollectionType(List.class, SubjectClassification.class));
  }

}
