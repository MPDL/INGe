package de.mpg.mpdl.inge.model.valueobjects.metadata;

import com.fasterxml.jackson.annotation.JsonInclude;

import de.mpg.mpdl.inge.model.valueobjects.ValueObject;

@SuppressWarnings("serial")
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ProjectInfoVO extends ValueObject implements Cloneable {
  private String title;
  private IdentifierVO grantIdentifier = new IdentifierVO(IdentifierVO.IdType.GRANT_ID, "");
  private FundingInfoVO fundingInfo = new FundingInfoVO();


  public String getTitle() {
    return this.title;
  }

  public void setTitle(String title) {
    this.title = title;
  }


  public FundingInfoVO getFundingInfo() {
    return this.fundingInfo;
  }

  public void setFundingInfo(FundingInfoVO fundingInfo) {
    this.fundingInfo = fundingInfo;
  }

  public IdentifierVO getGrantIdentifier() {
    return this.grantIdentifier;
  }

  public void setGrantIdentifier(IdentifierVO grantIdentifier) {
    this.grantIdentifier = grantIdentifier;
  }


  public final ProjectInfoVO clone() {
    try {
      ProjectInfoVO clone = (ProjectInfoVO) super.clone();
      if (null != clone.fundingInfo) {
        clone.fundingInfo = this.fundingInfo.clone();
      }
      if (null != clone.grantIdentifier) {
        clone.grantIdentifier = this.grantIdentifier.clone();
      }
      return clone;
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((null == this.fundingInfo) ? 0 : this.fundingInfo.hashCode());
    result = prime * result + ((null == this.grantIdentifier) ? 0 : this.grantIdentifier.hashCode());
    result = prime * result + ((null == this.title) ? 0 : this.title.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;

    if (null == obj)
      return false;

    if (getClass() != obj.getClass())
      return false;

    ProjectInfoVO other = (ProjectInfoVO) obj;

    if (null == this.fundingInfo) {
      if (null != other.fundingInfo)
        return false;
    } else if (!this.fundingInfo.equals(other.fundingInfo))
      return false;

    if (null == this.grantIdentifier) {
      if (null != other.grantIdentifier)
        return false;
    } else if (!this.grantIdentifier.equals(other.grantIdentifier))
      return false;

    if (null == this.title) {
      if (null != other.title)
        return false;
    } else if (!this.title.equals(other.title))
      return false;

    return true;
  }

}
