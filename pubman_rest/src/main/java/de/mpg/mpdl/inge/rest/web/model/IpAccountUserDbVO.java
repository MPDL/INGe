package de.mpg.mpdl.inge.rest.web.model;

import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import jakarta.persistence.Transient;

public class IpAccountUserDbVO extends AccountUserDbVO {

  String ipAddress;
  String matchedIpName;
  String matchedIpId;

  /**
   * Global read-only maintenance mode. Informational only, never persisted.
   */
  @Transient
  boolean readOnlyMode;

  public IpAccountUserDbVO(AccountUserDbVO accountUserDbVO) {
    super(accountUserDbVO);
  }

  public IpAccountUserDbVO() {
    super();
  }

  public boolean isReadOnlyMode() {
    return readOnlyMode;
  }

  public void setReadOnlyMode(boolean readOnlyMode) {
    this.readOnlyMode = readOnlyMode;
  }

  public String getMatchedIpId() {
    return matchedIpId;
  }

  public void setMatchedIpId(String matchedIpId) {
    this.matchedIpId = matchedIpId;
  }

  public String getMatchedIpName() {
    return matchedIpName;
  }

  public void setMatchedIpName(String matchedIpName) {
    this.matchedIpName = matchedIpName;
  }



  public String getIpAddress() {
    return ipAddress;
  }

  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }


}
