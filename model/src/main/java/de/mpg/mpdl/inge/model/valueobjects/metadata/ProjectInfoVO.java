package de.mpg.mpdl.inge.model.valueobjects.metadata;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import de.mpg.mpdl.inge.model.valueobjects.ValueObject;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO.IdType;

@SuppressWarnings("serial")
@JsonInclude(value = Include.NON_EMPTY)
public class ProjectInfoVO extends ValueObject implements Cloneable {
  private String title;
  private IdentifierVO grantIdentifier = new IdentifierVO(IdType.GRANT_ID, "");
  private FundingInfoVO fundingInfo = new FundingInfoVO();


  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }


  public FundingInfoVO getFundingInfo() {
    return fundingInfo;
  }

  public void setFundingInfo(FundingInfoVO fundingInfo) {
    this.fundingInfo = fundingInfo;
  }

  public IdentifierVO getGrantIdentifier() {
    return grantIdentifier;
  }

  public void setGrantIdentifier(IdentifierVO grantIdentifier) {
    this.grantIdentifier = grantIdentifier;
  }


  public ProjectInfoVO clone() {
    try {
      ProjectInfoVO clone = (ProjectInfoVO) super.clone();
      if (clone.fundingInfo != null) {
        clone.fundingInfo = this.fundingInfo.clone();
      }
      if (clone.grantIdentifier != null) {
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
    result = prime * result + ((fundingInfo == null) ? 0 : fundingInfo.hashCode());
    result = prime * result + ((grantIdentifier == null) ? 0 : grantIdentifier.hashCode());
    result = prime * result + ((title == null) ? 0 : title.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;

    if (obj == null)
      return false;

    if (getClass() != obj.getClass())
      return false;

    ProjectInfoVO other = (ProjectInfoVO) obj;

    if (fundingInfo == null) {
      if (other.fundingInfo != null)
        return false;
    } else if (!fundingInfo.equals(other.fundingInfo))
      return false;

    if (grantIdentifier == null) {
      if (other.grantIdentifier != null)
        return false;
    } else if (!grantIdentifier.equals(other.grantIdentifier))
      return false;

    if (title == null) {
      if (other.title != null)
        return false;
    } else if (!title.equals(other.title))
      return false;

    return true;
  }

}
