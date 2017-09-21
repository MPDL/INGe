package de.mpg.mpdl.inge.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.mpg.mpdl.inge.model.db.valueobjects.PubItemObjectDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.YearbookDbVO;

public interface YearbookRepository extends JpaRepository<YearbookDbVO, String> {
}
