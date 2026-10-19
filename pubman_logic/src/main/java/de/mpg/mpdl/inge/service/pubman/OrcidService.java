package de.mpg.mpdl.inge.service.pubman;

import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.model.db.valueobjects.OrcidAuthorizationDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import java.util.Date;

public interface OrcidService {

  OrcidAuthorizationDbVO sendEmailLink(String token, String coneIdAuthor, String orcidAuthor, String nameAuthor, String emailBibo,
      String emailAuthor)
      throws AuthenticationException, IngeApplicationException, AuthorizationException, TechnicalException, ValidationException;

  OrcidAuthorizationDbVO createOrcidAuthorization(String secret, String code, String errorDescription)
      throws AuthenticationException, IngeApplicationException, TechnicalException;

  String doOrcidQueryAndConvert(String coneIdAuthor, Date dateModified, String token)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException;
}
