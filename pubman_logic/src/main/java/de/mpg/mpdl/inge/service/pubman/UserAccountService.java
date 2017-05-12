package de.mpg.mpdl.inge.service.pubman;

import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.service.exceptions.AaException;
import de.mpg.mpdl.inge.es.exception.IngeEsServiceException;

public interface UserAccountService extends GenericService<AccountUserVO> {
  public AccountUserVO get(String authenticationToken) throws IngeEsServiceException, AaException;

  public String login(String username, String password) throws IngeEsServiceException, AaException;
}
