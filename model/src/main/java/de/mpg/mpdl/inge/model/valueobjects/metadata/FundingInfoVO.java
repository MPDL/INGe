package de.mpg.mpdl.inge.model.valueobjects.metadata;

import com.fasterxml.jackson.annotation.JsonInclude;

import de.mpg.mpdl.inge.model.valueobjects.ValueObject;


@SuppressWarnings("serial")
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class FundingInfoVO extends ValueObject implements Cloneable {


  private FundingOrganizationVO fundingOrganization = new FundingOrganizationVO();
  private FundingProgramVO fundingProgram = new FundingProgramVO();


  public FundingInfoVO() {}

  public FundingProgramVO getFundingProgram() {
    return this.fundingProgram;
  }

  public void setFundingProgram(FundingProgramVO fundingProgram) {
    this.fundingProgram = fundingProgram;
  }

  public FundingOrganizationVO getFundingOrganization() {
    return this.fundingOrganization;
  }

  public void setFundingOrganization(FundingOrganizationVO fundingOrganization) {
    this.fundingOrganization = fundingOrganization;
  }


  public final FundingInfoVO clone() {
    try {
      FundingInfoVO clone = (FundingInfoVO) super.clone();
      if (null != clone.fundingOrganization) {
        clone.fundingOrganization = this.fundingOrganization.clone();
      }
      if (null != this.fundingProgram) {
        clone.fundingProgram = this.fundingProgram.clone();
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
    result = prime * result + ((null == this.fundingOrganization) ? 0 : this.fundingOrganization.hashCode());
    result = prime * result + ((null == this.fundingProgram) ? 0 : this.fundingProgram.hashCode());
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

    FundingInfoVO other = (FundingInfoVO) obj;

    if (null == this.fundingOrganization) {
      if (null != other.fundingOrganization)
        return false;
    } else if (!this.fundingOrganization.equals(other.fundingOrganization))
      return false;

    if (null == this.fundingProgram) {
      if (null != other.fundingProgram)
        return false;
    } else if (!this.fundingProgram.equals(other.fundingProgram))
      return false;

    return true;
  }

}
