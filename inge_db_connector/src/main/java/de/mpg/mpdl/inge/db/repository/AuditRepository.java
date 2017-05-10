package de.mpg.mpdl.inge.db.repository;

import java.util.List;

import org.hibernate.annotations.TypeDef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import de.mpg.mpdl.inge.db.model.hibernate.MdsFileVOJsonUserType;
import de.mpg.mpdl.inge.db.model.hibernate.MdsPublicationVOJsonUserType;
import de.mpg.mpdl.inge.db.model.valueobjects.AuditDbVO;
import de.mpg.mpdl.inge.db.model.valueobjects.PubItemObjectDbVO;
import de.mpg.mpdl.inge.db.model.valueobjects.PubItemVersionDbVO;
import de.mpg.mpdl.inge.db.model.valueobjects.VersionableId;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;



public interface AuditRepository extends JpaRepository<AuditDbVO, String> {


  public List<AuditDbVO> findDistinctAuditByPubItemObjectIdOrderByModificationDateDesc(
      String objectId);

}
