package de.mpg.mpdl.inge.db.repository;

import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogHeaderDbVO;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BatchProcessLogHeaderRepository extends JpaRepository<BatchProcessLogHeaderDbVO, String> {

  List<BatchProcessLogHeaderDbVO> findAllByUserAccountObjectId(String userAccountObjectId);
}
