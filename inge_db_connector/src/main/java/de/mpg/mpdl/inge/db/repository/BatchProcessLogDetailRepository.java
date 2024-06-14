package de.mpg.mpdl.inge.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogDetailDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogHeaderDbVO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BatchProcessLogDetailRepository extends JpaRepository<BatchProcessLogDetailDbVO, String> {

  BatchProcessLogDetailDbVO findByBatchProcessLogHeaderDbVOAndItemObjectId(BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO,
      String itemObjectId);

  List<BatchProcessLogDetailDbVO> findByBatchProcessLogHeaderDbVO(BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO);

  @Query(
      value = "SELECT count(*) FROM batch_process_log_detail WHERE batch_process_log_header_id = :batchProcessLogHeaderId and state != 'INITIALIZED'",
      nativeQuery = true)
  Integer countProcessedItems(@Param("batchProcessLogHeaderId") long batchProcessLogHeaderId);
}
