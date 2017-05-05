package de.mpg.mpdl.inge.model_new.hibernate;

import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PublicationAdminDescriptorVO;


public class ContextAdminDescriptorJsonUserType extends
    StringJsonUserType<PublicationAdminDescriptorVO> {

  public ContextAdminDescriptorJsonUserType() {
    super(PublicationAdminDescriptorVO.class);
  }

}
