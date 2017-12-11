package de.mpg.mpdl.inge.model.db.hibernate;

import com.fasterxml.jackson.databind.type.TypeFactory;

import de.mpg.mpdl.inge.model.valueobjects.publication.PublicationAdminDescriptorVO;


public class ContextAdminDescriptorJsonUserType extends StringJsonUserType {

  public ContextAdminDescriptorJsonUserType() {
    super(TypeFactory.defaultInstance().constructType(PublicationAdminDescriptorVO.class));
  }

}
