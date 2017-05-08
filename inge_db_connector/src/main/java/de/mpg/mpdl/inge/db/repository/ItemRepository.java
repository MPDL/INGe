package de.mpg.mpdl.inge.db.repository;

import org.hibernate.annotations.TypeDef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import de.mpg.mpdl.inge.db.model.hibernate.MdsFileVOJsonUserType;
import de.mpg.mpdl.inge.db.model.hibernate.MdsPublicationVOJsonUserType;
import de.mpg.mpdl.inge.db.model.valueobjects.PubItemVersionDbVO;
import de.mpg.mpdl.inge.db.model.valueobjects.VersionableId;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;



public interface ItemRepository extends JpaRepository<PubItemVersionDbVO, VersionableId> {


  @Query("SELECT item FROM PubItemVersionVO item WHERE item.objectId=:objectId AND item.versionNumber=(SELECT MAX(item.versionNumber) FROM PubItemVersionVO item WHERE item.objectId=:objectId)")
  public PubItemVersionDbVO findLatestVersion(@Param("objectId") String objectId);

  @Query("SELECT item FROM PubItemVersionVO item WHERE item.objectId=:objectId AND item.state='RELEASED' AND item.versionNumber=(SELECT MAX(item.versionNumber) FROM PubItemVersionVO item WHERE item.objectId=:objectId)")
  public PubItemVersionDbVO findLatestRelease(@Param("objectId") String objectId);

}
