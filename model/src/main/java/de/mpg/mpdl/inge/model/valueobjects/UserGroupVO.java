package de.mpg.mpdl.inge.model.valueobjects;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 
 * <para> Following you will find information which elements and attributes are "required",
 * "optional", "not-allowed" or will be "discarded" in the input XML-stream when creating or
 * updating such an object. </para> <para> In "create" the rule for creating a new object is
 * defined. </para> <para> In "update" the rule for updating an object is defined. </para> <para>
 * Possible values are: required | optional | not-allowed | discarded </para> <para> required: this
 * element or attribute has to be delivered </para> <para> optional: this element or attribute can
 * be delivered and will be kept </para> <para> not-allowed: this element or attribute is not
 * allowed in delivery and will cause an exception </para> <para> discarded: this element or
 * attribute can be delivered but will not be used </para>
 * 
 * <create>required</create> <update>required</update>
 * 
 * 
 * Schema fragment(s) for this class:
 * 
 * <pre>
 * &lt;xs:element xmlns:ns="http://escidoc.de/core/01/structural-relations/" xmlns:ns1="http://www.escidoc.de/schemas/commontypes/0.4" xmlns:ns2="http://www.escidoc.de/schemas/usergroup/0.5" xmlns:ns3="http://escidoc.de/core/01/properties/" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="user-group">
 *   &lt;xs:complexType>
 *     &lt;xs:sequence>
 *       &lt;xs:element name="properties">
 *         &lt;xs:complexType>
 *           &lt;xs:sequence>
 *             &lt;xs:element ref="ns3:creation-date" minOccurs="0"/>
 *             &lt;xs:element ref="ns:created-by" minOccurs="0"/>
 *             &lt;xs:element ref="ns:modified-by" minOccurs="0"/>
 *             &lt;xs:element ref="ns3:email" minOccurs="0"/>
 *             &lt;xs:element ref="ns3:name"/>
 *             &lt;xs:element ref="ns3:label"/>
 *             &lt;xs:element ref="ns3:description" minOccurs="0"/>
 *             &lt;xs:element ref="ns3:type" minOccurs="0"/>
 *             &lt;xs:element ref="ns3:active" minOccurs="0"/>
 *           &lt;/xs:sequence>
 *         &lt;/xs:complexType>
 *       &lt;/xs:element>
 *       &lt;xs:element ref="ns2:members" minOccurs="0"/>
 *       &lt;xs:element ref="ns2:resources" minOccurs="0"/>
 *     &lt;/xs:sequence>
 *     &lt;xs:attributeGroup ref="ns1:eSciDocResourceIdentityAttributes"/>
 *     &lt;xs:attributeGroup ref="ns1:eSciDocRootElementAttributes"/>
 *   &lt;/xs:complexType>
 * &lt;/xs:element>
 * 
 * &lt;xs:attributeGroup xmlns:ns="http://escidoc.de/core/01/structural-relations/" xmlns:ns1="http://www.escidoc.de/schemas/commontypes/0.4" xmlns:ns2="http://www.escidoc.de/schemas/usergroup/0.5" xmlns:ns3="http://escidoc.de/core/01/properties/" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="eSciDocResourceIdentityAttributes">
 *   &lt;xs:attribute type="xs:string" name="objid"/>
 * &lt;/xs:attributeGroup>
 * 
 * &lt;xs:attributeGroup xmlns:ns="http://escidoc.de/core/01/structural-relations/" xmlns:ns1="http://www.escidoc.de/schemas/commontypes/0.4" xmlns:ns2="http://www.escidoc.de/schemas/usergroup/0.5" xmlns:ns3="http://escidoc.de/core/01/properties/" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="eSciDocRootElementAttributes">
 *   &lt;xs:attribute type="xs:dateTime" name="last-modification-date"/>
 * &lt;/xs:attributeGroup>
 * </pre>
 */
@SuppressWarnings("serial")
@JsonInclude(value = Include.NON_EMPTY)
public class UserGroupVO extends ValueObject {
  private Date creationDate;
  private String createdBy;
  private String modifiedBy;
  private String email;
  private String name;
  private String label;
  private String description;
  private String type;
  private boolean active;
  private List<MemberVO> members;
  private List<GrantVO> grants;
  private String objid;
  private Date lastModificationDate;


  /**
   * Retrieves a user group from the coreservice.
   * 
   * @param id the id of the user group.
   * @param userHandle A user handle for authentication in the coreservice.
   * @throws Exception If an error occurs in coreservice or during marshalling/unmarshalling.
   */
  public UserGroupVO(String escidocId, String userHandle) throws RuntimeException {}

  public UserGroupVO() {

  }



  /**
   * Get the 'creation-date' element value. <create>discarded</create> <update>discarded</update>
   * 
   * 
   * @return value
   */
  public Date getCreationDate() {
    return creationDate;
  }

  /**
   * Set the 'creation-date' element value. <create>discarded</create> <update>discarded</update>
   * 
   * 
   * @param creationDate
   */
  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  /**
   * Get the 'created-by' element value. <create>discarded</create> <update>discarded</update>
   * 
   * 
   * @return value
   */
  public String getCreatedBy() {
    return createdBy;
  }

  /**
   * Set the 'created-by' element value. <create>discarded</create> <update>discarded</update>
   * 
   * 
   * @param createdBy
   */
  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  /**
   * Get the 'modified-by' element value. <create>discarded</create> <update>discarded</update>
   * 
   * 
   * @return value
   */
  public String getModifiedBy() {
    return modifiedBy;
  }

  /**
   * Set the 'modified-by' element value. <create>discarded</create> <update>discarded</update>
   * 
   * 
   * @param modifiedBy
   */
  public void setModifiedBy(String modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  /**
   * Get the 'email' element value. <create>optional</create> <update>optional</update>
   * 
   * 
   * @return value
   */
  public String getEmail() {
    return email;
  }

  /**
   * Set the 'email' element value. <create>optional</create> <update>optional</update>
   * 
   * 
   * @param email
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Get the 'name' element value. <create>required</create> <update>required</update>
   * 
   * 
   * @return value
   */
  public String getName() {
    return name;
  }

  /**
   * Set the 'name' element value. <create>required</create> <update>required</update>
   * 
   * 
   * @param name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Get the 'label' element value. <create>required</create> <update>required</update>
   * 
   * 
   * @return value
   */
  public String getLabel() {
    return label;
  }

  /**
   * Set the 'label' element value. <create>required</create> <update>required</update>
   * 
   * 
   * @param label
   */
  public void setLabel(String label) {
    this.label = label;
  }

  /**
   * Get the 'description' element value. <create>optional</create> <update>optional</update>
   * 
   * 
   * @return value
   */
  public String getDescription() {
    return description;
  }

  /**
   * Set the 'description' element value. <create>optional</create> <update>optional</update>
   * 
   * 
   * @param description
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Get the 'type' element value. <create>optional</create> <update>optional</update>
   * 
   * 
   * @return value
   */
  public String getType() {
    return type;
  }

  /**
   * Set the 'type' element value. <create>optional</create> <update>optional</update>
   * 
   * 
   * @param type
   */
  public void setType(String type) {
    this.type = type;
  }

  /**
   * Get the 'active' element value. <create> discarded (see note 1.) </create> <update> discarded
   * (see note 1.) </update> <comment> 1. A created user group is always active. It is not possible
   * to activate or deactivate a user group using the update method. This can only be done by using
   * the activate() and deactivate() methods. </comment>
   * 
   * 
   * @return value
   */
  public boolean getActive() {
    return active;
  }

  /**
   * Set the 'active' element value. <create> discarded (see note 1.) </create> <update> discarded
   * (see note 1.) </update> <comment> 1. A created user group is always active. It is not possible
   * to activate or deactivate a user group using the update method. This can only be done by using
   * the activate() and deactivate() methods. </comment>
   * 
   * 
   * @param active
   */
  public void setActive(boolean active) {
    this.active = active;
  }

  /**
   * Get the 'members' element value.
   * 
   * @return value
   */
  public List<MemberVO> getMembers() {
    return members;
  }

  /**
   * Set the 'members' element value.
   * 
   * @param members
   */
  public void setMembers(List<MemberVO> members) {
    this.members = members;
  }


  /**
   * Get the 'objid' attribute value. <create>discarded</create><update>discarded</update>
   * 
   * @return value
   */
  public String getObjid() {
    return objid;
  }

  /**
   * Set the 'objid' attribute value. <create>discarded</create><update>discarded</update>
   * 
   * @param objid
   */
  public void setObjid(String objid) {
    this.objid = objid;
  }

  /**
   * Get the 'last-modification-date' attribute value. <create>discarded</create>
   * <update>required</update> <comment>required only in root element on update</comment>
   * 
   * @return value
   */
  public Date getLastModificationDate() {
    return lastModificationDate;
  }

  /**
   * Set the 'last-modification-date' attribute value. <create>discarded</create>
   * <update>required</update> <comment>required only in root element on update</comment>
   * 
   * @param lastModificationDate
   */
  public void setLastModificationDate(Date lastModificationDate) {
    this.lastModificationDate = lastModificationDate;
  }

  public List<GrantVO> getGrants() {
    return grants;
  }

  public void setGrants(List<GrantVO> grants) {
    this.grants = grants;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (active ? 1231 : 1237);
    result = prime * result + ((createdBy == null) ? 0 : createdBy.hashCode());
    result = prime * result + ((creationDate == null) ? 0 : creationDate.hashCode());
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + ((email == null) ? 0 : email.hashCode());
    result = prime * result + ((grants == null) ? 0 : grants.hashCode());
    result = prime * result + ((label == null) ? 0 : label.hashCode());
    result = prime * result + ((lastModificationDate == null) ? 0 : lastModificationDate.hashCode());
    result = prime * result + ((members == null) ? 0 : members.hashCode());
    result = prime * result + ((modifiedBy == null) ? 0 : modifiedBy.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((objid == null) ? 0 : objid.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
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

    UserGroupVO other = (UserGroupVO) obj;

    if (active != other.active)
      return false;

    if (createdBy == null) {
      if (other.createdBy != null)
        return false;
    } else if (!createdBy.equals(other.createdBy))
      return false;

    if (creationDate == null) {
      if (other.creationDate != null)
        return false;
    } else if (!creationDate.equals(other.creationDate))
      return false;

    if (description == null) {
      if (other.description != null)
        return false;
    } else if (!description.equals(other.description))
      return false;

    if (email == null) {
      if (other.email != null)
        return false;
    } else if (!email.equals(other.email))
      return false;

    if (grants == null) {
      if (other.grants != null)
        return false;
    } else if (other.grants == null)
      return false;
    else if (!grants.containsAll(other.grants) //
        || !other.grants.containsAll(grants)) {
      return false;
    }

    if (label == null) {
      if (other.label != null)
        return false;
    } else if (!label.equals(other.label))
      return false;

    if (lastModificationDate == null) {
      if (other.lastModificationDate != null)
        return false;
    } else if (!lastModificationDate.equals(other.lastModificationDate))
      return false;

    if (members == null) {
      if (other.members != null)
        return false;
    } else if (other.members == null)
      return false;
    else if (!members.containsAll(other.members) //
        || !other.members.containsAll(members)) {
      return false;
    }

    if (modifiedBy == null) {
      if (other.modifiedBy != null)
        return false;
    } else if (!modifiedBy.equals(other.modifiedBy))
      return false;

    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;

    if (objid == null) {
      if (other.objid != null)
        return false;
    } else if (!objid.equals(other.objid))
      return false;

    if (type == null) {
      if (other.type != null)
        return false;
    } else if (!type.equals(other.type))
      return false;

    return true;
  }

}
