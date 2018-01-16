package de.mpg.mpdl.inge.service.util;

import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO.PredefinedRoles;

public class GrantUtil {


  public static boolean hasRole(AccountUserDbVO userAccount, PredefinedRoles role) {

    return hasRole(userAccount, role, null);
  }

  public static boolean hasRole(AccountUserDbVO userAccount, PredefinedRoles role, String grantOn) {

    for (GrantVO grant : userAccount.getGrantList()) {

      if (grant.getRole().equals(role.frameworkValue()) && (grantOn == null || grantOn.equals(grant.getObjectRef()))) {
        return true;
      }
    }
    return false;
  }
}
