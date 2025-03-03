package de.mpg.mpdl.inge.db.repository;

import de.mpg.mpdl.inge.model.db.valueobjects.SavedSearchDbVO;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * JPA repository for BatchLogDbVO
 *
 * @author walter
 *
 */
public interface SavedSearchRepository extends JpaRepository<SavedSearchDbVO, String> {

  List<SavedSearchDbVO> findByCreatorObjectIdOrderByLastModificationDateDesc(String creatorObjectId);
}
