package de.mpg.mpdl.inge.db.repository;

import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogItemDbVO;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ImportLogItemRepository extends JpaRepository<ImportLogItemDbVO, Integer> {
  @Query("select i from ImportLogItemDbVO i where i.parent = ?1")
  List<ImportLogItemDbVO> findByParent(ImportLogDbVO importLogDbVO);

  @Query("select i from ImportLogItemDbVO i where i.parent = ?1 and i.itemId is not null")
  List<ImportLogItemDbVO> findByParentAndItemId(ImportLogDbVO importLogDbVO);

}
