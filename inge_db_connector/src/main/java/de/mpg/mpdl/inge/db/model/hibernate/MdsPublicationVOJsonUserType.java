package de.mpg.mpdl.inge.db.model.hibernate;

import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;

public class MdsPublicationVOJsonUserType extends StringJsonUserType<MdsPublicationVO> {

  public MdsPublicationVOJsonUserType() {
    super(MdsPublicationVO.class);
  }

}
