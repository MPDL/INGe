package de.mpg.mpdl.inge.model.db.hibernate;

import java.util.List;

import com.fasterxml.jackson.databind.type.TypeFactory;

import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.Genre;

public class GenreListJsonUserType extends StringJsonUserType {

  public GenreListJsonUserType() {
    super(TypeFactory.defaultInstance().constructCollectionType(List.class, Genre.class));
  }

}
