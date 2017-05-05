package de.mpg.mpdl.inge.model_new.hibernate;

import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;

public class MdsPublicationVOJsonUserType extends StringJsonUserType<MdsPublicationVO> {

  public MdsPublicationVOJsonUserType() {
    super(MdsPublicationVO.class);
  }

}
