package de.mpg.mpdl.inge.rest.web.model;

import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;

public class IpAccountUserDbVO extends AccountUserDbVO {

  String ipAddress;
  String matchedName;
  String matchedId;

  public IpAccountUserDbVO(AccountUserDbVO accountUserDbVO) {
    super(accountUserDbVO);
  }

  public IpAccountUserDbVO() {
    super();
  }

  public String getMatchedId() {
    return matchedId;
  }

  public void setMatchedId(String matchedId) {
    this.matchedId = matchedId;
  }

  public String getMatchedName() {
    return matchedName;
  }

  public void setMatchedName(String matchedName) {
    this.matchedName = matchedName;
  }



  public String getIpAddress() {
    return ipAddress;
  }

  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }


}
