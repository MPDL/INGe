package de.mpg.mpdl.inge.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.mpg.mpdl.inge.model.db.valueobjects.AuditDbVO;

public interface AuditRepository extends JpaRepository<AuditDbVO, String> {

  List<AuditDbVO> findDistinctAuditByPubItemObjectIdOrderByModificationDateDesc(String objectId);
}
