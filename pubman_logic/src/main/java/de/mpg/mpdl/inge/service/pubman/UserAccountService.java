package de.mpg.mpdl.inge.service.pubman;

import de.mpg.mpdl.inge.model.exception.IngeServiceException;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.service.exceptions.AaException;

public interface UserAccountService extends GenericService<AccountUserVO> {
  public AccountUserVO get(String authenticationToken) throws IngeServiceException, AaException;

  public String login(String username, String password) throws IngeServiceException, AaException;
}
