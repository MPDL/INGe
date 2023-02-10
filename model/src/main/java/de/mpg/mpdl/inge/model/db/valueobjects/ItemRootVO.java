package de.mpg.mpdl.inge.model.db.valueobjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import de.mpg.mpdl.inge.model.db.hibernate.StringListJsonUserType;



@SuppressWarnings("serial")
@Entity
@Table(name = "item_object")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "item")
@TypeDef(name = "StringListJsonUserType", typeClass = StringListJsonUserType.class)
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
  @OneToOne(fetch = FetchType.EAGER, targetEntity = ItemVersionVO.class, optional = true)
  @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "item")
  // @JoinColumns({@JoinColumn(name="objectId", referencedColumnName="objectId"),
  // @JoinColumn(name="latestRelease_versionNumber", referencedColumnName="versionNumber")})
  @JsonSerialize(as = ItemVersionRO.class)
  private ItemVersionRO latestRelease;

  // @MapsId("objectId")
  @OneToOne(fetch = FetchType.EAGER, targetEntity = ItemVersionVO.class, optional = true)
  @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "item")
  // @JoinColumns({@JoinColumn(name="objectId", referencedColumnName="objectId"),
  // @JoinColumn(name="latestVersion_versionNumber", referencedColumnName="versionNumber")})
  @JsonSerialize(as = ItemVersionRO.class)
  private ItemVersionRO latestVersion;



  @Type(type = "StringListJsonUserType")
  private List<String> localTags = new ArrayList<String>();


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
    return objectId;
  }

  /**
   * Helper method for JiBX transformations.
   */
  boolean hasPID() {
    return (this.objectPid != null);
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
    return publicState;
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
    return lastModificationDate;
  }

  public void setLastModificationDate(Date lastModificationDate) {
    this.lastModificationDate = lastModificationDate;
  }


  public ContextDbRO getContext() {
    return context;
  }

  public void setContext(ContextDbRO context) {
    this.context = context;
  }

  public void setLocalTags(List<String> localTags) {
    this.localTags = localTags;
  }

  public AccountUserDbRO getCreator() {
    return creator;
  }

  public void setCreator(AccountUserDbRO creator) {
    this.creator = creator;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((context == null) ? 0 : context.hashCode());
    result = prime * result + ((creationDate == null) ? 0 : creationDate.hashCode());
    result = prime * result + ((creator == null) ? 0 : creator.hashCode());
    result = prime * result + ((lastModificationDate == null) ? 0 : lastModificationDate.hashCode());
    result = prime * result + ((latestRelease == null) ? 0 : latestRelease.hashCode());
    result = prime * result + ((latestVersion == null) ? 0 : latestVersion.hashCode());
    result = prime * result + ((localTags == null) ? 0 : localTags.hashCode());
    result = prime * result + ((objectId == null) ? 0 : objectId.hashCode());
    result = prime * result + ((objectPid == null) ? 0 : objectPid.hashCode());
    result = prime * result + ((publicState == null) ? 0 : publicState.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ItemRootVO other = (ItemRootVO) obj;
    if (context == null) {
      if (other.context != null)
        return false;
    } else if (!context.equals(other.context))
      return false;
    if (creationDate == null) {
      if (other.creationDate != null)
        return false;
    } else if (!creationDate.equals(other.creationDate))
      return false;
    if (creator == null) {
      if (other.creator != null)
        return false;
    } else if (!creator.equals(other.creator))
      return false;
    if (lastModificationDate == null) {
      if (other.lastModificationDate != null)
        return false;
    } else if (!lastModificationDate.equals(other.lastModificationDate))
      return false;
    if (latestRelease == null) {
      if (other.latestRelease != null)
        return false;
    } else if (!latestRelease.equals(other.latestRelease))
      return false;
    if (latestVersion == null) {
      if (other.latestVersion != null)
        return false;
    } else if (!latestVersion.equals(other.latestVersion))
      return false;
    if (localTags == null) {
      if (other.localTags != null)
        return false;
    } else if (!localTags.equals(other.localTags))
      return false;
    if (objectId == null) {
      if (other.objectId != null)
        return false;
    } else if (!objectId.equals(other.objectId))
      return false;
    if (objectPid == null) {
      if (other.objectPid != null)
        return false;
    } else if (!objectPid.equals(other.objectPid))
      return false;
    if (publicState != other.publicState)
      return false;
    return true;
  }

}
