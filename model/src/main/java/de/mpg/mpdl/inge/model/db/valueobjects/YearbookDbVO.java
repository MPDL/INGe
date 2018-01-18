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

}
