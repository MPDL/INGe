package de.mpg.mpdl.inge.model.db.valueobjects;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import de.mpg.mpdl.inge.model.db.hibernate.GrantVOListJsonUserType;
import de.mpg.mpdl.inge.model.util.MapperFactory;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;

@SuppressWarnings("serial")
@Entity
@Table(name = "user_account")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "user")
@TypeDef(name = "GrantVOListJsonUserType", typeClass = GrantVOListJsonUserType.class)
public class AccountUserDbVO extends BasicDbRO {

  private boolean active;

  @Column
  private String email;

  @NaturalId
  @Column(unique = true)
  private String loginname;


  @Type(type = "GrantVOListJsonUserType")
  private List<GrantVO> grantList = new ArrayList<GrantVO>();

  @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "organization")
  @ManyToOne(fetch = FetchType.EAGER, targetEntity = AffiliationDbVO.class)
  @JsonSerialize(as = AffiliationDbRO.class)
  private AffiliationDbRO affiliation;

  @Transient
  private String password;


  public AccountUserDbVO() {}

  public AccountUserDbVO(AccountUserDbVO other) {
    MapperFactory.getDozerMapper().map(other, this);
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getLoginname() {
    return loginname;
  }

  public void setLoginname(String loginname) {
    this.loginname = loginname;
  }

  public List<GrantVO> getGrantList() {
    return grantList;
  }

  public void setGrantList(List<GrantVO> grantList) {
    this.grantList = grantList;
  }

  public AffiliationDbRO getAffiliation() {
    return affiliation;
  }

  public void setAffiliation(AffiliationDbRO affiliation) {
    this.affiliation = affiliation;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (active ? 1231 : 1237);
    result = prime * result + ((affiliation == null) ? 0 : affiliation.hashCode());
    result = prime * result + ((email == null) ? 0 : email.hashCode());
    result = prime * result + ((grantList == null) ? 0 : grantList.hashCode());
    result = prime * result + ((loginname == null) ? 0 : loginname.hashCode());
    result = prime * result + ((password == null) ? 0 : password.hashCode());
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
    AccountUserDbVO other = (AccountUserDbVO) obj;
    if (active != other.active)
      return false;
    if (affiliation == null) {
      if (other.affiliation != null)
        return false;
    } else if (!affiliation.equals(other.affiliation))
      return false;
    if (email == null) {
      if (other.email != null)
        return false;
    } else if (!email.equals(other.email))
      return false;
    if (grantList == null) {
      if (other.grantList != null)
        return false;
    } else if (!grantList.equals(other.grantList))
      return false;
    if (loginname == null) {
      if (other.loginname != null)
        return false;
    } else if (!loginname.equals(other.loginname))
      return false;
    if (password == null) {
      if (other.password != null)
        return false;
    } else if (!password.equals(other.password))
      return false;
    return true;
  }


}
