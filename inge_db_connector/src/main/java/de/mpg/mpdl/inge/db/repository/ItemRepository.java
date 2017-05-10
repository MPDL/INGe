package de.mpg.mpdl.inge.db.repository;

import java.util.List;

import javax.persistence.QueryHint;

import org.hibernate.annotations.TypeDef;
import org.hibernate.cfg.annotations.QueryHintDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import de.mpg.mpdl.inge.db.model.hibernate.MdsFileVOJsonUserType;
import de.mpg.mpdl.inge.db.model.hibernate.MdsPublicationVOJsonUserType;
import de.mpg.mpdl.inge.db.model.valueobjects.PubItemVersionDbVO;
import de.mpg.mpdl.inge.db.model.valueobjects.VersionableId;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;


// @NoRepositoryBean
public interface ItemRepository extends GenericRepository<PubItemVersionDbVO, VersionableId> {


  @Query("SELECT item FROM PubItemVersionVO item WHERE item.objectId=:objectId AND item.versionNumber=(SELECT MAX(item.versionNumber) FROM PubItemVersionVO item WHERE item.objectId=:objectId)")
  @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
  public PubItemVersionDbVO findLatestVersion(@Param("objectId") String objectId);

  @Query("SELECT item FROM PubItemVersionVO item WHERE item.objectId=:objectId AND item.versionNumber=(SELECT MAX(item.versionNumber) FROM PubItemVersionVO item WHERE item.objectId=:objectId AND item.state='RELEASED')")
  @QueryHints(@QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true"))
  public PubItemVersionDbVO findLatestRelease(@Param("objectId") String objectId);


  /*
   * @Override
   * 
   * @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true")) PubItemVersionDbVO
   * findOne(VersionableId id);
   */

}
