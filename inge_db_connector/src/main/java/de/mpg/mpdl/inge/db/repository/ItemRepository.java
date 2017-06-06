package de.mpg.mpdl.inge.db.repository;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import de.mpg.mpdl.inge.db.model.valueobjects.PubItemVersionDbVO;
import de.mpg.mpdl.inge.db.model.valueobjects.VersionableId;

// @NoRepositoryBean
public interface ItemRepository extends GenericRepository<PubItemVersionDbVO, VersionableId> {

  @Query("SELECT item FROM PubItemVersionVO item WHERE item.objectId=:objectId AND item.versionNumber=(SELECT MAX(item.versionNumber) FROM PubItemVersionVO item WHERE item.objectId=:objectId)")
  @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
  public PubItemVersionDbVO findLatestVersion(@Param("objectId") String objectId);

  @Query("SELECT item FROM PubItemVersionVO item WHERE item.objectId=:objectId AND item.versionNumber=(SELECT MAX(item.versionNumber) FROM PubItemVersionVO item WHERE item.objectId=:objectId AND item.state='RELEASED')")
  @QueryHints(@QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true"))
  public PubItemVersionDbVO findLatestRelease(@Param("objectId") String objectId);
}
