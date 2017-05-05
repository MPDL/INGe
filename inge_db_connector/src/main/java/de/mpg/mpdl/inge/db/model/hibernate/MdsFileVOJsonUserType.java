package de.mpg.mpdl.inge.db.model.hibernate;

import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsFileVO;

public class MdsFileVOJsonUserType extends StringJsonUserType<MdsFileVO> {

  public MdsFileVOJsonUserType() {
    super(MdsFileVO.class);
  }

}
