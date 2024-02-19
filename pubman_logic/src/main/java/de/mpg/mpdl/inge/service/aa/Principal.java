package de.mpg.mpdl.inge.service.aa;

import java.io.Serializable;

import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;

@SuppressWarnings("serial")
public class Principal implements Serializable {

  private AccountUserDbVO userAccount;

  private String jwToken;


  public Principal(AccountUserDbVO userAccount, String jwToken) {
    this.userAccount = userAccount;
    this.jwToken = jwToken;
  }

  public AccountUserDbVO getUserAccount() {
    return this.userAccount;
  }

  public void setUserAccount(AccountUserDbVO userAccount) {
    this.userAccount = userAccount;
  }



  public String getJwToken() {
    return this.jwToken;
  }

  public void setJwToken(String jwToken) {
    this.jwToken = jwToken;
  }

}
