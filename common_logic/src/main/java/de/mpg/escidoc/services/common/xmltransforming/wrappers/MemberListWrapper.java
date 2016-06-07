package de.mpg.escidoc.services.common.xmltransforming.wrappers;

import java.io.Serializable;
import java.util.List;

import de.mpg.mpdl.inge.model.valueobjects.ValueObject;

public class MemberListWrapper implements Serializable {
  private static final long serialVersionUID = 1L;

  protected List<? extends ValueObject> memberList;


  public List<? extends ValueObject> getMemberList() {
    return memberList;
  }

  public void setMemberList(List<? extends ValueObject> memberList) {
    this.memberList = memberList;
  }


}
