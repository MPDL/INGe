package de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.wrappers;

import java.io.Serializable;
import java.util.List;

import de.mpg.mpdl.inge.model.valueobjects.ValueObject;

@SuppressWarnings("serial")
public class MemberListWrapper implements Serializable {
  protected List<? extends ValueObject> memberList;

  public List<? extends ValueObject> getMemberList() {
    return this.memberList;
  }

  public void setMemberList(List<? extends ValueObject> memberList) {
    this.memberList = memberList;
  }
}
