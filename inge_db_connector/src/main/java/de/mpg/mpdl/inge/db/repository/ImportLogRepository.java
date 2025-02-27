package de.mpg.mpdl.inge.db.repository;

import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogDbVO;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ImportLogRepository extends JpaRepository<ImportLogDbVO, Integer> {

  @Query(
      value = "SELECT l.*, COUNT(i.id) as anzItems FROM import_log l LEFT JOIN import_log_item i ON i.parent = l.id AND i.item_id IS NOT NULL WHERE l.context = :contextId GROUP BY l.id",
      nativeQuery = true)
  List<Object[]> findAllByContextId(@Param("contextId") String contextId);

  @Query(
      value = "SELECT l.*, COUNT(i.id) as anzItems FROM import_log l LEFT JOIN import_log_item i ON i.parent = l.id AND i.item_id IS NOT NULL WHERE l.userid = :userId GROUP BY l.id",
      nativeQuery = true)
  List<Object[]> findAllByUserId(@Param("userId") String userId);

  @Query("SELECT l FROM ImportLogDbVO l WHERE l.status = 'PENDING' AND l.startDate < :criticalDate")
  List<ImportLogDbVO> findBrokenImports(@Param("criticalDate") Date criticalDate);

  @Query(
      value = "SELECT l.*, COUNT(i.id) as anzItems FROM import_log l LEFT JOIN import_log_item i ON i.parent = l.id AND i.item_id IS NOT NULL WHERE l.id = :importLogId GROUP BY l.id",
      nativeQuery = true)
  List<Object[]> findByIdWithAnzItems(@Param("importLogId") Integer importLogId);

}
