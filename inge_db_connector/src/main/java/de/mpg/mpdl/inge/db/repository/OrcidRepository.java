package de.mpg.mpdl.inge.db.repository;

import de.mpg.mpdl.inge.model.db.valueobjects.OrcidDbVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrcidRepository extends JpaRepository<OrcidDbVO, Integer> {

  @Query("SELECT o FROM OrcidDbVO o WHERE o.orcidAuthorizationDbVO.coneIdAuthor = :coneIdAuthor AND o.itemId = :itemId")
  OrcidDbVO findByConeIdAuthorAndItemId(@Param("coneIdAuthor") String coneIdAuthor, @Param("itemId") String itemId);
}
