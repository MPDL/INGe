package de.mpg.mpdl.inge.model.valueobjects.metadata;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import de.mpg.mpdl.inge.model.valueobjects.ValueObject;


@SuppressWarnings("serial")
@JsonInclude(value = Include.NON_EMPTY)
public class FundingInfoVO extends ValueObject implements Cloneable {


  private FundingOrganizationVO fundingOrganization = new FundingOrganizationVO();
  private FundingProgramVO fundingProgram = new FundingProgramVO();


  public FundingInfoVO() {
    super();
  }

  public FundingProgramVO getFundingProgram() {
    return fundingProgram;
  }

  public void setFundingProgram(FundingProgramVO fundingProgram) {
    this.fundingProgram = fundingProgram;
  }

  public FundingOrganizationVO getFundingOrganization() {
    return fundingOrganization;
  }

  public void setFundingOrganization(FundingOrganizationVO fundingOrganization) {
    this.fundingOrganization = fundingOrganization;
  }


  public FundingInfoVO clone() {
    FundingInfoVO clonedFundingInfo = new FundingInfoVO();
    if (this.getFundingOrganization() != null) {
      clonedFundingInfo.setFundingOrganization(this.getFundingOrganization().clone());
    }
    if (this.getFundingProgram() != null) {
      clonedFundingInfo.setFundingProgram(this.getFundingProgram().clone());
    }


    return clonedFundingInfo;
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fundingOrganization == null) ? 0 : fundingOrganization.hashCode());
    result = prime * result + ((fundingProgram == null) ? 0 : fundingProgram.hashCode());
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

    FundingInfoVO other = (FundingInfoVO) obj;

    if (fundingOrganization == null) {
      if (other.fundingOrganization != null)
        return false;
    } else if (!fundingOrganization.equals(other.fundingOrganization))
      return false;

    if (fundingProgram == null) {
      if (other.fundingProgram != null)
        return false;
    } else if (!fundingProgram.equals(other.fundingProgram))
      return false;

    return true;
  }

}
