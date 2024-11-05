package de.mpg.mpdl.inge.db.repository;

import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogDbVO;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ImportLogRepository extends JpaRepository<ImportLogDbVO, Integer> {

  @Query("select i from ImportLogDbVO i where i.userId = ?1")
  List<ImportLogDbVO> findAllByUserId(String userId);
}
