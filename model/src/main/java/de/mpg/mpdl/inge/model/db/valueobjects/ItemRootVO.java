package de.mpg.mpdl.inge.model.db.valueobjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
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

import de.mpg.mpdl.inge.model.db.hibernate.StringListJsonUserType;



@Entity(name = "PubItemObjectVO")
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


  @ManyToOne(fetch = FetchType.EAGER, targetEntity=ContextDbVO.class)
  @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "context")
  private ContextDbRO context;

  private Date creationDate;

  // @MapsId("objectId")
  @OneToOne(fetch = FetchType.EAGER, targetEntity = ItemVersionVO.class, optional = true)
  @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "item")
  // @JoinColumns({@JoinColumn(name="objectId", referencedColumnName="objectId"),
  // @JoinColumn(name="latestRelease_versionNumber", referencedColumnName="versionNumber")})
  private ItemVersionRO latestRelease;

  // @MapsId("objectId")
  @OneToOne(fetch = FetchType.EAGER, targetEntity = ItemVersionVO.class, optional = true)
  @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "item")
  // @JoinColumns({@JoinColumn(name="objectId", referencedColumnName="objectId"),
  // @JoinColumn(name="latestVersion_versionNumber", referencedColumnName="versionNumber")})
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

}
