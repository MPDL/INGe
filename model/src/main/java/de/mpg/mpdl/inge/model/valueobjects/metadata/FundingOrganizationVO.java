package de.mpg.mpdl.inge.model.valueobjects.metadata;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import de.mpg.mpdl.inge.model.valueobjects.ValueObject;

@SuppressWarnings("serial")
@JsonInclude(value = Include.NON_EMPTY)
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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((identifiers == null) ? 0 : identifiers.hashCode());
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

    FundingOrganizationVO other = (FundingOrganizationVO) obj;

    if (identifiers == null) {
      if (other.identifiers != null)
        return false;
    } else if (other.identifiers == null)
      return false;
    else if (!identifiers.containsAll(other.identifiers) //
        || !other.identifiers.containsAll(identifiers)) {
      return false;
    }

    if (title == null) {
      if (other.title != null)
        return false;
    } else if (!title.equals(other.title))
      return false;

    return true;
  }

}
