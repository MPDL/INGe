package de.mpg.escidoc.services.common.valueobjects.metadata;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import de.mpg.escidoc.services.common.valueobjects.ValueObject;

@JsonInclude(value = Include.NON_NULL)
public class FundingProgramVO extends ValueObject {
  private String title;
  private List<IdentifierVO> identifiers = new ArrayList<IdentifierVO>();

  public FundingProgramVO() {
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

  public FundingProgramVO clone() {
    FundingProgramVO clonedFundingProgram = new FundingProgramVO();
    clonedFundingProgram.setTitle(this.getTitle());
    for (IdentifierVO id : identifiers) {
      clonedFundingProgram.getIdentifiers().add((IdentifierVO) id.clone());

    }

    return clonedFundingProgram;
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
    FundingProgramVO other = (FundingProgramVO) obj;
    return equals(getIdentifiers(), other.getIdentifiers()) && equals(getTitle(), other.getTitle());
  }

}
