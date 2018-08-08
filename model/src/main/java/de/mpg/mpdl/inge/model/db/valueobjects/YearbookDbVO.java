package de.mpg.mpdl.inge.model.db.valueobjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import de.mpg.mpdl.inge.model.db.hibernate.StringListJsonUserType;

@SuppressWarnings("serial")
@Entity(name = "YearbookDbVO")
@Table(name = "yearbook", uniqueConstraints = @UniqueConstraint(columnNames = {"organization", "year"}))
@TypeDef(name = "StringListJsonUserType", typeClass = StringListJsonUserType.class)
public class YearbookDbVO extends BasicDbRO implements Serializable {

  public enum State
  {
    CREATED,
    SUBMITTED,
    RELEASED;
  }

  private int year;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "organization")
  /* Ignore some properties to avoid huge index entries */
  @JsonIgnoreProperties({"parentAffiliation", "predecessorAffiliations", "hasChildren", "hasPredecessors", "metadata"})
  private AffiliationDbVO organization;

  @Enumerated(EnumType.STRING)
  private State state = State.CREATED;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "yearbook_item")
  private Set<String> itemIds = new HashSet<>();

  @Type(type = "StringListJsonUserType")
  private List<String> contextIds = new ArrayList<>();



  public int getYear() {
    return year;
  }

  public void setYear(int year) {
    this.year = year;
  }

  public AffiliationDbVO getOrganization() {
    return organization;
  }

  public void setOrganization(AffiliationDbVO organization) {
    this.organization = organization;
  }

  public State getState() {
    return state;
  }

  public void setState(State state) {
    this.state = state;
  }

  public Set<String> getItemIds() {
    return itemIds;
  }

  public void setItemIds(Set<String> itemIds) {
    this.itemIds = itemIds;
  }

  public List<String> getContextIds() {
    return contextIds;
  }

  public void setContextIds(List<String> contextIds) {
    this.contextIds = contextIds;
  }

  @Override
  public String getName() {
    return getYear() + " - " + getOrganization().getName();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((contextIds == null) ? 0 : contextIds.hashCode());
    result = prime * result + ((itemIds == null) ? 0 : itemIds.hashCode());
    result = prime * result + ((organization == null) ? 0 : organization.hashCode());
    result = prime * result + ((state == null) ? 0 : state.hashCode());
    result = prime * result + year;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    YearbookDbVO other = (YearbookDbVO) obj;
    if (contextIds == null) {
      if (other.contextIds != null)
        return false;
    } else if (!contextIds.equals(other.contextIds))
      return false;
    if (itemIds == null) {
      if (other.itemIds != null)
        return false;
    } else if (!itemIds.equals(other.itemIds))
      return false;
    if (organization == null) {
      if (other.organization != null)
        return false;
    } else if (!organization.equals(other.organization))
      return false;
    if (state != other.state)
      return false;
    if (year != other.year)
      return false;
    return true;
  }

}
