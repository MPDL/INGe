package de.mpg.mpdl.inge.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.db.valueobjects.VersionableId;
import jakarta.persistence.QueryHint;

// @NoRepositoryBean
public interface ItemRepository extends GenericRepository<ItemVersionVO, VersionableId> {

  @Query("SELECT item FROM ItemVersionVO item WHERE item.objectId=:objectId AND item.versionNumber=(SELECT MAX(item.versionNumber) FROM ItemVersionVO item WHERE item.objectId=:objectId)")
  @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
  public ItemVersionVO findLatestVersion(@Param("objectId") String objectId);

  @Query("SELECT item FROM ItemVersionVO item WHERE item.objectId=:objectId AND item.versionNumber=(SELECT MAX(item.versionNumber) FROM ItemVersionVO item WHERE item.objectId=:objectId AND item.versionState='RELEASED')")
  @QueryHints(@QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true"))
  public ItemVersionVO findLatestRelease(@Param("objectId") String objectId);


  @Query(value = "SELECT itemVersionVO_objectId FROM item_version_file WHERE files_objectId = :fileObjectId", nativeQuery = true)
  public List<String> findItemsForFile(@Param("fileObjectId") String fileObjectId);



}
