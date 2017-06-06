package de.mpg.mpdl.inge.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.mpg.mpdl.inge.db.model.valueobjects.PubItemObjectDbVO;

public interface ItemObjectRepository extends JpaRepository<PubItemObjectDbVO, String> {
}
