package de.mpg.mpdl.inge.db.repository;

import org.hibernate.annotations.TypeDef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model_new.hibernate.MdsFileVOJsonUserType;
import de.mpg.mpdl.inge.model_new.hibernate.MdsPublicationVOJsonUserType;
import de.mpg.mpdl.inge.model_new.valueobjects.PubItemVersionVO;
import de.mpg.mpdl.inge.model_new.valueobjects.VersionableId;



public interface ItemRepository extends JpaRepository<PubItemVersionVO, VersionableId> {


  @Query("SELECT item FROM PubItemVersionVO item WHERE item.objectId=:objectId AND item.versionNumber=(SELECT MAX(item.versionNumber) FROM PubItemVersionVO item WHERE item.objectId=:objectId)")
  public PubItemVersionVO findLatestVersion(@Param("objectId") String objectId);

  @Query("SELECT item FROM PubItemVersionVO item WHERE item.objectId=:objectId AND item.state='RELEASED' AND item.versionNumber=(SELECT MAX(item.versionNumber) FROM PubItemVersionVO item WHERE item.objectId=:objectId)")
  public PubItemVersionVO findLatestRelease(@Param("objectId") String objectId);

}
