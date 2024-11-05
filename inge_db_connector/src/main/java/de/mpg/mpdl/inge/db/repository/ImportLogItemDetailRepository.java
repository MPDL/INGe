package de.mpg.mpdl.inge.db.repository;

import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogItemDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogItemDetailDbVO;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ImportLogItemDetailRepository extends JpaRepository<ImportLogItemDetailDbVO, Integer> {
  @Query("select i from ImportLogItemDetailDbVO i where i.parent = ?1")
  List<ImportLogItemDetailDbVO> findByImportLogItem(ImportLogItemDbVO importLogItemDbVO);
}
