package de.mpg.mpdl.inge.db.model.hibernate;

import java.util.List;

import com.fasterxml.jackson.databind.type.TypeFactory;

import de.mpg.mpdl.inge.model.valueobjects.publication.PublicationAdminDescriptorVO;


public class ContextAdminDescriptorJsonUserType extends StringJsonUserType {

  public ContextAdminDescriptorJsonUserType() {
    super(TypeFactory.defaultInstance().constructType(PublicationAdminDescriptorVO.class));
  }

}
