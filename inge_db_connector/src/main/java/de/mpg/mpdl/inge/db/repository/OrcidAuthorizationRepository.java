package de.mpg.mpdl.inge.db.repository;

import de.mpg.mpdl.inge.model.db.valueobjects.OrcidAuthorizationDbVO;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrcidAuthorizationRepository extends JpaRepository<OrcidAuthorizationDbVO, Integer> {
  OrcidAuthorizationDbVO findBySecret(String secret);

  OrcidAuthorizationDbVO findByConeIdAuthor(String coneIdAuthor);

  List<OrcidAuthorizationDbVO> findByVerifiedTrue();

  List<OrcidAuthorizationDbVO> findByVerified(Boolean verified);
}
