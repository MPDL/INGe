package de.mpg.mpdl.inge.db.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.mpg.mpdl.inge.model.db.valueobjects.StagedFileDbVO;

public interface StagedFileRepository extends JpaRepository<StagedFileDbVO, Integer> {


  //@Query("SELECT stagefile FROM StagedFileDbVO stagefile WHERE stagefile.creationDate < :criticalDate")
  List<StagedFileDbVO> findByCreationDateBefore(Date criticalDate);

}
