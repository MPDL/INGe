package de.mpg.mpdl.inge.db.repository;

import de.mpg.mpdl.inge.model.db.valueobjects.AuditDbVO;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AuditRepository extends JpaRepository<AuditDbVO, String> {

  //  @Query("select distinct a from audit a where a.pubItem.objectId = ?1 order by a.modificationDate DESC")
  //  List<AuditDbVO> findDistinctAuditByPubItemObjectIdOrderByModificationDateDesc(String objectId);

  // Aus Performanzgründen natives SQL -> JPA generiert für jedes Itemversion ein komplizierte SQL Statement
  @Query(
      value = "SELECT DISTINCT a.id, a.comment, a.event, a.modificationdate, a.modifier_name, a.modifier_objectid, a.pubitem_objectid, a.pubitem_versionnumber FROM audit_log a WHERE a.pubitem_objectid = ?1 ORDER BY a.modificationdate DESC",
      nativeQuery = true)
  List<Object[]> findDistinctAuditByPubItemObjectIdOrderByModificationDateDesc(String objectId);
}
