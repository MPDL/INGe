package de.mpg.mpdl.inge.db.repository;

import de.mpg.mpdl.inge.model.db.valueobjects.AuditDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.SavedSearchDbVO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


/**
 * JPA repository for BatchLogDbVO
 * 
 * @author walter
 * 
 */
public interface SavedSearchRepository extends JpaRepository<SavedSearchDbVO, String> {

  List<SavedSearchDbVO> findByCreatorObjectIdOrderByLastModificationDateDesc(String creatorObjectId);
}
