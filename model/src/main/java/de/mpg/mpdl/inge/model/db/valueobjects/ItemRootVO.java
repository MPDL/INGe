package de.mpg.mpdl.inge.model.db.valueobjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;



@SuppressWarnings("serial")
@Entity
@Table(name = "item_object")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "item-root")
//@TypeDef(name = "StringListJsonUserType", typeClass = StringListJsonUserType.class)
public class ItemRootVO implements Serializable {

  @Id
  private String objectId;

  /**
   * The date of the last modification of the referenced item.
   */
  private Date lastModificationDate;

  @Enumerated(EnumType.STRING)
  private ItemVersionRO.State publicState;

  @Column(name = "objectPid")
  private String objectPid;

  @Embedded
  @AttributeOverrides({@AttributeOverride(name = "objectId", column = @Column(name = "creator_objectId")),
      @AttributeOverride(name = "name", column = @Column(name = "creator_name"))})
  private AccountUserDbRO creator;


  @ManyToOne(fetch = FetchType.EAGER, targetEntity = ContextDbVO.class)
  @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "context")
  @JsonSerialize(as = ContextDbRO.class)
  private ContextDbRO context;

  private Date creationDate;

  // @MapsId("objectId")
  @OneToOne(fetch = FetchType.EAGER, targetEntity = ItemVersionVO.class)
  // @JoinColumns({@JoinColumn(name="objectId", referencedColumnName="objectId"),
  // @JoinColumn(name="latestRelease_versionNumber", referencedColumnName="versionNumber")})
  @JsonSerialize(as = ItemVersionRO.class)
  private ItemVersionRO latestRelease;

  // @MapsId("objectId")
  @OneToOne(fetch = FetchType.EAGER, targetEntity = ItemVersionVO.class)
  // @JoinColumns({@JoinColumn(name="objectId", referencedColumnName="objectId"),
  // @JoinColumn(name="latestVersion_versionNumber", referencedColumnName="versionNumber")})
  @JsonSerialize(as = ItemVersionRO.class)
  private ItemVersionRO latestVersion;



  //@Type(type = "StringListJsonUserType")
  @JdbcTypeCode(SqlTypes.JSON)
  private List<String> localTags = new ArrayList<>();


  /**
   * Sets the technical objectId-attribute of corresponding ValueObject.
   *
   * @param objectId
   */
  public void setObjectId(String objectId) {
    this.objectId = objectId;
  }

  /**
   * Delivers the technical objectId-attribute of corresponding ValueObject.
   */
  public String getObjectId() {
    return this.objectId;
  }

  /**
   * Helper method for JiBX transformations.
   */
  boolean hasPID() {
    return (null != this.objectPid);
  }



  /**
   * Delivers the persistent identifier of the item.
   */
  public String getObjectPid() {
    return this.objectPid;
  }



  /**
   * Sets the persistent identifier of the item.
   *
   * @param newVal
   */
  public void setObjectPid(String newVal) {
    this.objectPid = newVal;
  }



  /**
   * Delivers the date when the item was created.
   */
  public java.util.Date getCreationDate() {
    return this.creationDate;
  }


  /**
   * Sets the date when the item was created.
   *
   * @param newVal
   */
  public void setCreationDate(java.util.Date newVal) {
    this.creationDate = newVal;
  }



  public ItemVersionRO getLatestVersion() {
    return this.latestVersion;
  }

  public void setLatestVersion(ItemVersionRO latestVersion) {
    this.latestVersion = latestVersion;
  }

  public ItemVersionRO getLatestRelease() {
    return this.latestRelease;
  }

  public void setLatestRelease(ItemVersionRO latestRelease) {
    this.latestRelease = latestRelease;
  }



  public ItemVersionRO.State getPublicState() {
    return this.publicState;
  }

  public void setPublicState(ItemVersionRO.State publicStatus) {
    this.publicState = publicStatus;
  }


  public java.util.List<String> getLocalTags() {
    return this.localTags;
  }

  /*
   * @Override public int hashCode() { final int prime = 31; int result = 1; result = prime * result
   * + ((baseUrl == null) ? 0 : baseUrl.hashCode()); result = prime * result + ((contentModel ==
   * null) ? 0 : contentModel.hashCode()); result = prime * result + ((contextRO == null) ? 0 :
   * contextRO.hashCode()); result = prime * result + ((creationDate == null) ? 0 :
   * creationDate.hashCode()); //result = prime * result + ((latestRelease == null) ? 0 :
   * latestRelease.hashCode()); //result = prime * result + ((latestVersion == null) ? 0 :
   * latestVersion.hashCode()); result = prime * result + ((lockStatus == null) ? 0 :
   * lockStatus.hashCode()); result = prime * result + ((owner == null) ? 0 : owner.hashCode());
   * result = prime * result + ((pid == null) ? 0 : pid.hashCode()); result = prime * result +
   * ((publicStatus == null) ? 0 : publicStatus.hashCode()); result = prime * result +
   * ((publicStatusComment == null) ? 0 : publicStatusComment.hashCode()); return result; }
   */
  public Date getLastModificationDate() {
    return this.lastModificationDate;
  }

  public void setLastModificationDate(Date lastModificationDate) {
    this.lastModificationDate = lastModificationDate;
  }


  public ContextDbRO getContext() {
    return this.context;
  }

  public void setContext(ContextDbRO context) {
    this.context = context;
  }

  public void setLocalTags(List<String> localTags) {
    this.localTags = localTags;
  }

  public AccountUserDbRO getCreator() {
    return this.creator;
  }

  public void setCreator(AccountUserDbRO creator) {
    this.creator = creator;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((null == this.context) ? 0 : this.context.hashCode());
    result = prime * result + ((null == this.creationDate) ? 0 : this.creationDate.hashCode());
    result = prime * result + ((null == this.creator) ? 0 : this.creator.hashCode());
    result = prime * result + ((null == this.lastModificationDate) ? 0 : this.lastModificationDate.hashCode());
    result = prime * result + ((null == this.latestRelease) ? 0 : this.latestRelease.hashCode());
    result = prime * result + ((null == this.latestVersion) ? 0 : this.latestVersion.hashCode());
    result = prime * result + ((null == this.localTags) ? 0 : this.localTags.hashCode());
    result = prime * result + ((null == this.objectId) ? 0 : this.objectId.hashCode());
    result = prime * result + ((null == this.objectPid) ? 0 : this.objectPid.hashCode());
    result = prime * result + ((null == this.publicState) ? 0 : this.publicState.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (null == obj)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ItemRootVO other = (ItemRootVO) obj;
    if (null == this.context) {
      if (null != other.context)
        return false;
    } else if (!this.context.equals(other.context))
      return false;
    if (null == this.creationDate) {
      if (null != other.creationDate)
        return false;
    } else if (!this.creationDate.equals(other.creationDate))
      return false;
    if (null == this.creator) {
      if (null != other.creator)
        return false;
    } else if (!this.creator.equals(other.creator))
      return false;
    if (null == this.lastModificationDate) {
      if (null != other.lastModificationDate)
        return false;
    } else if (!this.lastModificationDate.equals(other.lastModificationDate))
      return false;
    if (null == this.latestRelease) {
      if (null != other.latestRelease)
        return false;
    } else if (!this.latestRelease.equals(other.latestRelease))
      return false;
    if (null == this.latestVersion) {
      if (null != other.latestVersion)
        return false;
    } else if (!this.latestVersion.equals(other.latestVersion))
      return false;
    if (null == this.localTags) {
      if (null != other.localTags)
        return false;
    } else if (!this.localTags.equals(other.localTags))
      return false;
    if (null == this.objectId) {
      if (null != other.objectId)
        return false;
    } else if (!this.objectId.equals(other.objectId))
      return false;
    if (null == this.objectPid) {
      if (null != other.objectPid)
        return false;
    } else if (!this.objectPid.equals(other.objectPid))
      return false;
    if (this.publicState != other.publicState)
      return false;
    return true;
  }

}
