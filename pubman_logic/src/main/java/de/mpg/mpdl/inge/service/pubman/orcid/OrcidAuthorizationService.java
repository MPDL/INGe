package de.mpg.mpdl.inge.service.pubman.orcid;

import de.mpg.mpdl.inge.model.db.valueobjects.OrcidAuthorizationDbVO;

public interface OrcidAuthorizationService {

  String generateSecret();

  String createEmailLink(String scopeRequested, String redirectUri);

  OrcidAuthorizationDbVO createOrcidAuthorizationDbVO(String userIdBibo, String coneIdAuthor, String orcidAuthor, String nameAuthor,
      String secret, String scopeRequested, String emailLink, String emailBibo, String emailAuthor, String redirectUri);

  OrcidAuthorizationDbVO finishAuthorizationDbVO(OrcidAuthorizationDbVO orcidAuthorizationDbVO);

  OrcidAuthorizationDbVO updateAuthorizationDbVO(OrcidAuthorizationDbVO orcidAuthorizationDbVO, String code);
}
