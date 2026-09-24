package de.mpg.mpdl.inge.service.pubman;

import de.mpg.mpdl.inge.model.db.valueobjects.OrcidAuthorizationDbVO;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;

public interface OrcidService {

  OrcidAuthorizationDbVO sendEmailLink(String token, String coneIdAuthor, String orcidAuthor, String nameAuthor, String emailBibo,
      String emailAuthor) throws AuthenticationException, IngeApplicationException, AuthorizationException, TechnicalException;

  OrcidAuthorizationDbVO createOrcidAuthorization(String secret, String code)
      throws AuthenticationException, IngeApplicationException, TechnicalException;
}
