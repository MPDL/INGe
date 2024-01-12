package de.mpg.mpdl.inge.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogDetailDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogHeaderDbVO;

public interface BatchProcessLogDetailRepository extends JpaRepository<BatchProcessLogDetailDbVO, String> {

  public BatchProcessLogDetailDbVO findByBatchProcessLogHeaderDbVOAndItemObjectId(BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO,
      String itemObjectId);

  public List<BatchProcessLogDetailDbVO> findByBatchProcessLogHeaderDbVO(BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO);
}
