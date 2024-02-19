package de.mpg.mpdl.inge.model.db.valueobjects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.NaturalId;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import de.mpg.mpdl.inge.model.util.MapperFactory;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@SuppressWarnings("serial")
@Entity
@Table(name = "user_account")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "user")
//@TypeDef(name = "GrantVOListJsonUserType", typeClass = GrantVOListJsonUserType.class)
public class AccountUserDbVO extends BasicDbRO {

  private boolean active;

  @Column
  private String email;

  @NaturalId
  @Column(unique = true)
  private String loginname;


  //@Type(type = "GrantVOListJsonUserType")
  @JdbcTypeCode(SqlTypes.JSON)
  private List<GrantVO> grantList = new ArrayList<>();

  @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "organization")
  @ManyToOne(fetch = FetchType.EAGER, targetEntity = AffiliationDbVO.class)
  @JsonSerialize(as = AffiliationDbRO.class)
  private AffiliationDbRO affiliation;

  @Transient
  private String password;


  public AccountUserDbVO() {}

  public AccountUserDbVO(AccountUserDbVO other) {

    MapperFactory.STRUCT_MAP_MAPPER.updateAccountUserDbVO(other, this);
  }

  public boolean isActive() {
    return this.active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getLoginname() {
    return this.loginname;
  }

  public void setLoginname(String loginname) {
    this.loginname = loginname;
  }

  public List<GrantVO> getGrantList() {
    return this.grantList;
  }

  public void setGrantList(List<GrantVO> grantList) {
    this.grantList = grantList;
  }

  public AffiliationDbRO getAffiliation() {
    return this.affiliation;
  }

  public void setAffiliation(AffiliationDbRO affiliation) {
    this.affiliation = affiliation;
  }

  public String getPassword() {
    return this.password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (this.active ? 1231 : 1237);
    result = prime * result + ((null == this.affiliation) ? 0 : this.affiliation.hashCode());
    result = prime * result + ((null == this.email) ? 0 : this.email.hashCode());
    result = prime * result + ((null == this.grantList) ? 0 : this.grantList.hashCode());
    result = prime * result + ((null == this.loginname) ? 0 : this.loginname.hashCode());
    result = prime * result + ((null == this.password) ? 0 : this.password.hashCode());
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
    if (this.active != other.active)
      return false;
    if (null == this.affiliation) {
      if (null != other.affiliation)
        return false;
    } else if (!this.affiliation.equals(other.affiliation))
      return false;
    if (null == this.email) {
      if (null != other.email)
        return false;
    } else if (!this.email.equals(other.email))
      return false;
    if (null == this.grantList) {
      if (null != other.grantList)
        return false;
    } else if (!this.grantList.equals(other.grantList))
      return false;
    if (null == this.loginname) {
      if (null != other.loginname)
        return false;
    } else if (!this.loginname.equals(other.loginname))
      return false;
    if (null == this.password) {
      if (null != other.password)
        return false;
    } else if (!this.password.equals(other.password))
      return false;
    return true;
  }


}
