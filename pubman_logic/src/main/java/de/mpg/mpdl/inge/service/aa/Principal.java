package de.mpg.mpdl.inge.service.aa;

import java.io.Serializable;

import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;

@SuppressWarnings("serial")
public class Principal implements Serializable {

  private AccountUserDbVO userAccount;

  private String jwToken;


  public Principal(AccountUserDbVO userAccount, String jwToken) {
    super();
    this.userAccount = userAccount;
    this.jwToken = jwToken;
  }

  public AccountUserDbVO getUserAccount() {
    return userAccount;
  }

  public void setUserAccount(AccountUserDbVO userAccount) {
    this.userAccount = userAccount;
  }



  public String getJwToken() {
    return jwToken;
  }

  public void setJwToken(String jwToken) {
    this.jwToken = jwToken;
  }

}
