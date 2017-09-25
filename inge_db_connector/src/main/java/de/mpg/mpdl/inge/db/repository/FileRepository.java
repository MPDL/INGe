package de.mpg.mpdl.inge.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.mpg.mpdl.inge.db.model.valueobjects.FileDbVO;

/**
 * JPA repository for FileDbVO
 * 
 * @author walter
 * 
 */
public interface FileRepository extends JpaRepository<FileDbVO, String> {
}
