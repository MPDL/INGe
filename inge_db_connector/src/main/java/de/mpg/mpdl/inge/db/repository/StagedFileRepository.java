package de.mpg.mpdl.inge.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.mpg.mpdl.inge.model.db.valueobjects.PubItemObjectDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.StagedFileDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.YearbookDbVO;

public interface StagedFileRepository extends JpaRepository<StagedFileDbVO, Integer> {
}
