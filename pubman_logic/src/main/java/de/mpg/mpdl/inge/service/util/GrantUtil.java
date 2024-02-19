package de.mpg.mpdl.inge.service.util;

import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;

public class GrantUtil {

  private GrantUtil() {}

  public static boolean hasRole(AccountUserDbVO userAccount, GrantVO.PredefinedRoles role) {
    return hasRole(userAccount, role, null);
  }

  public static boolean hasRole(AccountUserDbVO userAccount, GrantVO.PredefinedRoles role, String grantOn) {
    for (GrantVO grant : userAccount.getGrantList()) {
      if (null != grant && null != grant.getRole()) {
        if (grant.getRole().equals(role.frameworkValue()) && (null == grantOn || grantOn.equals(grant.getObjectRef()))) {
          return true;
        }
      }
    }

    return false;
  }
}
