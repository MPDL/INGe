package de.mpg.mpdl.inge.model.valueobjects.metadata;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import de.mpg.mpdl.inge.model.valueobjects.ValueObject;

@SuppressWarnings("serial")
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class FundingOrganizationVO extends ValueObject implements Cloneable {

  private String title;
  private List<IdentifierVO> identifiers = new ArrayList<>();

  public FundingOrganizationVO() {}

  public String getTitle() {
    return this.title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public List<IdentifierVO> getIdentifiers() {
    return this.identifiers;
  }

  public void setIdentifiers(List<IdentifierVO> identifiers) {
    this.identifiers = identifiers;
  }

  public FundingOrganizationVO clone() {
    try {
      FundingOrganizationVO clone = (FundingOrganizationVO) super.clone();
      for (IdentifierVO identifier : this.identifiers) {
        clone.identifiers.add(identifier.clone());
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
    result = prime * result + ((null == this.identifiers) ? 0 : this.identifiers.hashCode());
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

    FundingOrganizationVO other = (FundingOrganizationVO) obj;

    if (null == this.identifiers) {
      if (null != other.identifiers)
        return false;
    } else if (null == other.identifiers)
      return false;
    else if (!new HashSet<>(this.identifiers).containsAll(other.identifiers) //
        || !new HashSet<>(other.identifiers).containsAll(this.identifiers)) {
      return false;
    }

    if (null == this.title) {
      if (null != other.title)
        return false;
    } else if (!this.title.equals(other.title))
      return false;

    return true;
  }

}
