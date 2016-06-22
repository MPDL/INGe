package de.mpg.mpdl.inge.model.valueobjects.metadata;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import de.mpg.mpdl.inge.model.valueobjects.ValueObject;

@JsonInclude(value = Include.NON_NULL)
public class FundingOrganizationVO extends ValueObject {

  private String title;
  private List<IdentifierVO> identifiers = new ArrayList<IdentifierVO>();

  public FundingOrganizationVO() {
    super();
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public List<IdentifierVO> getIdentifiers() {
    return identifiers;
  }

  public void setIdentifiers(List<IdentifierVO> identifiers) {
    this.identifiers = identifiers;
  }

  public FundingOrganizationVO clone() {
    FundingOrganizationVO clonedFundingOrganization = new FundingOrganizationVO();
    clonedFundingOrganization.setTitle(this.getTitle());
    for (IdentifierVO id : identifiers) {
      clonedFundingOrganization.getIdentifiers().add((IdentifierVO) id.clone());

    }

    return clonedFundingOrganization;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null || !(getClass().isAssignableFrom(obj.getClass()))) {
      return false;
    }
    FundingOrganizationVO other = (FundingOrganizationVO) obj;
    return equals(getIdentifiers(), other.getIdentifiers()) && equals(getTitle(), other.getTitle());
  }

}