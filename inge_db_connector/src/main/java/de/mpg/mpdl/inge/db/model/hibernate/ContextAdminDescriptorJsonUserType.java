package de.mpg.mpdl.inge.db.model.hibernate;

import de.mpg.mpdl.inge.model.valueobjects.publication.PublicationAdminDescriptorVO;


public class ContextAdminDescriptorJsonUserType extends
    StringJsonUserType<PublicationAdminDescriptorVO> {

  public ContextAdminDescriptorJsonUserType() {
    super(PublicationAdminDescriptorVO.class);
  }

}
