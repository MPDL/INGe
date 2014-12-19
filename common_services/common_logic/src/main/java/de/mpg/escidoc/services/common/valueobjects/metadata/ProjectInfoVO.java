package de.mpg.escidoc.services.common.valueobjects.metadata;

import java.util.ArrayList;
import java.util.List;

import de.mpg.escidoc.services.common.valueobjects.ValueObject;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO.IdType;

public class ProjectInfoVO extends ValueObject {
	
	
	
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
	
	
	public ProjectInfoVO clone()
	{
		ProjectInfoVO clonedProjectInfo = new ProjectInfoVO();
		clonedProjectInfo.setFundingInfo((FundingInfoVO)this.getFundingInfo().clone());
		clonedProjectInfo.setGrantIdentifier((IdentifierVO)this.getGrantIdentifier().clone());
		clonedProjectInfo.setTitle(this.getTitle());
		return clonedProjectInfo;
	}

	

}
