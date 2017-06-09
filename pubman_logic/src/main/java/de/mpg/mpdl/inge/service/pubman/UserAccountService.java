package de.mpg.mpdl.inge.service.pubman;

import java.util.Date;

import de.mpg.mpdl.inge.inge_validation.exception.ItemInvalidException;
import de.mpg.mpdl.inge.model.exception.IngeServiceException;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.service.exceptions.AaException;

public interface UserAccountService extends GenericService<AccountUserVO> {


  public AccountUserVO get(String authenticationToken) throws IngeServiceException, AaException;

  public String login(String username, String password) throws IngeServiceException, AaException;

  public AccountUserVO removeGrants(String userId, Date modificationDate, GrantVO[] grants,
      String authenticationToken) throws IngeServiceException, AaException;

  public AccountUserVO addGrants(String userId, Date modificationDate, GrantVO[] grants,
      String authenticationToken) throws IngeServiceException, AaException;

  public void changePassword(String userId, Date modificationDate, String newPassword,
      String authenticationToken) throws IngeServiceException, AaException;
}
