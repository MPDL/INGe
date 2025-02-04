package de.mpg.mpdl.inge.db.repository;

import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogItemDbVO;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ImportLogItemRepository extends JpaRepository<ImportLogItemDbVO, Integer> {
  @Query("select count(*) from ImportLogItemDbVO i where i.parent = ?1 and i.itemId is not null")
  int countByParentAndItemId(ImportLogDbVO importLogDbVO);

  @Query(
      value = "SELECT i.*, COUNT(d.id) as anzDetails FROM import_log_item i LEFT JOIN import_log_item_detail d on d.parent = i.id WHERE i.parent = :importLogId GROUP BY i.id",
      nativeQuery = true)
  List<Object[]> findByParent(@Param("importLogId") Integer importLogId);

  @Query("select i from ImportLogItemDbVO i where i.parent = ?1 and i.itemId is not null")
  List<ImportLogItemDbVO> findByParentAndItemId(ImportLogDbVO importLogDbVO);
}
