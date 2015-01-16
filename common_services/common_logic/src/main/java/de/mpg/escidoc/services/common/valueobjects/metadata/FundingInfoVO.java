package de.mpg.escidoc.services.common.valueobjects.metadata;

import de.mpg.escidoc.services.common.valueobjects.ValueObject;



public class FundingInfoVO extends ValueObject {
	
	
	private FundingOrganizationVO fundingOrganization = new FundingOrganizationVO();
	private FundingProgramVO fundingProgram = new FundingProgramVO();
	
	
	public FundingInfoVO()
	{
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
	
	
	public FundingInfoVO clone()
	{
		FundingInfoVO clonedFundingInfo = new FundingInfoVO();
		clonedFundingInfo.setFundingOrganization(this.getFundingOrganization().clone());
		clonedFundingInfo.setFundingProgram(this.getFundingProgram().clone());
		
		return clonedFundingInfo;
	}


	  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj)
  {
      if (obj == null || !(getClass().isAssignableFrom(obj.getClass())))
      {
          return false;
      }
      FundingInfoVO other = (FundingInfoVO)obj;
      return equals(getFundingOrganization(), other.getFundingOrganization()) && 
             equals(getFundingProgram(), other.getFundingProgram());
  }



}
