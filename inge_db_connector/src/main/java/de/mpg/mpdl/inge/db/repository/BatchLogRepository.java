package de.mpg.mpdl.inge.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogDbVO;


/**
 * JPA repository for BatchLogDbVO
 * 
 * @author walter
 * 
 */
public interface BatchLogRepository extends JpaRepository<BatchProcessLogDbVO, String> {
}
