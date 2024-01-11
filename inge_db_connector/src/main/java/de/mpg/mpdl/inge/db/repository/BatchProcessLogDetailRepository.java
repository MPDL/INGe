package de.mpg.mpdl.inge.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogDetailDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogHeaderDbVO;

public interface BatchProcessLogDetailRepository extends JpaRepository<BatchProcessLogDetailDbVO, String> {

  public BatchProcessLogDetailDbVO findByLogHeaderAndItem(BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO, String itemObjectId);
}
