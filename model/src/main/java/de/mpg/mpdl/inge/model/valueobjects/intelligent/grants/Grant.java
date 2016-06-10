package de.mpg.mpdl.inge.model.valueobjects.intelligent.grants;

import java.util.Date;

import de.escidoc.www.services.aa.UserAccountHandler;
import de.escidoc.www.services.aa.UserGroupHandler;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.mpdl.inge.model.referenceobjects.ReferenceObject;
import de.mpg.mpdl.inge.model.util.Util;
import de.mpg.mpdl.inge.model.valueobjects.intelligent.IntelligentVO;
import de.mpg.mpdl.inge.model.valueobjects.intelligent.grants.CurrentGrants.UserType;

/**
 * 
 <create>required</create> <update>not-allowed (see note 1.)</update> <comment>1. Update is not
 * defined for a grant.</comment>
 * 
 * 
 * Schema fragment(s) for this class:
 * 
 * <pre>
 * &lt;xs:element xmlns:ns="http://escidoc.de/core/01/structural-relations/" xmlns:ns1="http://www.escidoc.de/schemas/commontypes/0.4" xmlns:ns2="http://escidoc.de/core/01/properties/" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="grant">
 *   &lt;xs:complexType>
 *     &lt;xs:sequence>
 *       &lt;xs:element name="properties">
 *         &lt;xs:complexType>
 *           &lt;xs:sequence>
 *             &lt;xs:element ref="ns2:creation-date" minOccurs="0"/>
 *             &lt;xs:element ref="ns:created-by" minOccurs="0"/>
 *             &lt;xs:element ref="ns2:revocation-date" minOccurs="0"/>
 *             &lt;xs:element ref="ns:revoked-by" minOccurs="0"/>
 *             &lt;xs:element ref="ns2:grant-remark" minOccurs="0"/>
 *             &lt;xs:element ref="ns2:revocation-remark" minOccurs="0"/>
 *             &lt;xs:element ref="ns:role"/>
 *             &lt;xs:element ref="ns:assigned-on" minOccurs="0"/>
 *           &lt;/xs:sequence>
 *         &lt;/xs:complexType>
 *       &lt;/xs:element>
 *     &lt;/xs:sequence>
 *     &lt;xs:attributeGroup ref="ns1:eSciDocResourceIdentityAttributes"/>
 *     &lt;xs:attributeGroup ref="ns1:eSciDocRootElementAttributes"/>
 *   &lt;/xs:complexType>
 * &lt;/xs:element>
 * 
 * &lt;xs:attributeGroup xmlns:ns="http://escidoc.de/core/01/structural-relations/" xmlns:ns1="http://www.escidoc.de/schemas/commontypes/0.4" xmlns:ns2="http://escidoc.de/core/01/properties/" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="eSciDocResourceIdentityAttributes">
 *   &lt;xs:attribute type="xs:string" name="objid"/>
 * &lt;/xs:attributeGroup>
 * 
 * &lt;xs:attributeGroup xmlns:ns="http://escidoc.de/core/01/structural-relations/" xmlns:ns1="http://www.escidoc.de/schemas/commontypes/0.4" xmlns:ns2="http://escidoc.de/core/01/properties/" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="eSciDocRootElementAttributes">
 *   &lt;xs:attribute type="xs:dateTime" name="last-modification-date"/>
 * &lt;/xs:attributeGroup>
 * </pre>
 */
public class Grant extends IntelligentVO {
  private Date creationDate;
  private String grantedTo;
  private String grantType;
  private String createdBy;
  private Date revocationDate;
  private String revokedBy;
  private String grantRemark;
  private String revocationRemark;
  private String role;
  private String assignedOn;
  private String objid;
  private Date lastModificationDate;


  public enum CoreserviceRole {
    DEPOSITOR("escidoc:role-depositor"), MODERATOR("escidoc:role-moderator"), REPORTER(
        "escidoc:role-reporter"), AUDIENCE("escidoc:role-audience"), COLLABORATOR_VIEWER(
        "escidoc:role-collaborator"), COLLABORATOR_MODIFIER("escidoc:role-collaborator-modifier");

    private String roleId;


    private CoreserviceRole(String roleId) {
      this.setRoleId(roleId);
    }


    public void setRoleId(String roleId) {
      this.roleId = roleId;
    }


    public String getRoleId() {
      return roleId;
    }

  }

  /**
   * Retrieves a grant from the coreservice.
   * 
   * @param grantId the id of the grant.
   * @param userHandle A user handle for authentication in the coreservice.
   * @param userId The id of the user or user group that owns this grant.
   * @throws Exception If an error occurs in coreservice or during marshalling/unmarshalling.
   */
  public Grant(String userHandle, String userId, String grantId, UserType userType)
      throws RuntimeException {}


  public Grant() {

  }

  /**
   * Clone constructor
   */
  public Grant(Grant toBeCloned) {
    super(toBeCloned);
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
   * @param CreationDate
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
   * @param CreatedBy
   */
  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  /**
   * Get the 'revocation-date' element value. <create>discarded</create> <update>not-allowed (see
   * note 1.)</update>
   * 
   * 
   * @return value
   */
  public Date getRevocationDate() {
    return revocationDate;
  }

  /**
   * Set the 'revocation-date' element value. <create>discarded</create> <update>not-allowed (see
   * note 1.)</update>
   * 
   * 
   * @param RevocationDate
   */
  public void setRevocationDate(Date revocationDate) {
    this.revocationDate = revocationDate;
  }

  /**
   * Get the 'revoked-by' element value. <create>discarded</create> <update>not-allowed (see note
   * 1.)</update>
   * 
   * 
   * @return value
   */
  public String getRevokedBy() {
    return revokedBy;
  }

  /**
   * Set the 'revoked-by' element value. <create>discarded</create> <update>not-allowed (see note
   * 1.)</update>
   * 
   * 
   * @param RevokedBy
   */
  public void setRevokedBy(String revokedBy) {
    this.revokedBy = revokedBy;
  }

  /**
   * Get the 'grant-remark' element value. <create>optional</create> <update>not-allowed (see note
   * 1.)</update>
   * 
   * 
   * @return value
   */
  public String getGrantRemark() {
    return grantRemark;
  }

  /**
   * Set the 'grant-remark' element value. <create>optional</create> <update>not-allowed (see note
   * 1.)</update>
   * 
   * 
   * @param GrantRemark
   */
  public void setGrantRemark(String grantRemark) {
    this.grantRemark = grantRemark;
  }

  /**
   * Get the 'revocation-remark' element value. <create>discarded</create> <update>not-allowed (see
   * note 1.)</update>
   * 
   * 
   * @return value
   */
  public String getRevocationRemark() {
    return revocationRemark;
  }

  /**
   * Set the 'revocation-remark' element value. <create>discarded</create> <update>not-allowed (see
   * note 1.)</update>
   * 
   * 
   * @param RevocationRemark
   */
  public void setRevocationRemark(String revocationRemark) {
    this.revocationRemark = revocationRemark;
  }

  /**
   * Get the 'role' element value. <create>required</create> <update>not-allowed (see note
   * 1.)</update>
   * 
   * 
   * @return value
   */
  public String getRole() {
    return role;
  }

  /**
   * Set the 'role' element value. <create>required</create> <update>not-allowed (see note
   * 1.)</update>
   * 
   * 
   * @param Role
   */
  public void setRole(String role) {
    this.role = role;
  }

  /**
   * Get the 'assigned-on' element value. <create>optional (see note 2.)</create>
   * <update>not-allowed (see note 1.)</update> <comment>2. The referenced resource must be of type
   * container, content-model, context, item, organizational unit, or scope. Granting roles on other
   * resource types is not supported, yet.</comment>
   * 
   * 
   * @return value
   */
  public String getAssignedOn() {
    return assignedOn;
  }

  /**
   * Set the 'assigned-on' element value. <create>optional (see note 2.)</create>
   * <update>not-allowed (see note 1.)</update> <comment>2. The referenced resource must be of type
   * container, content-model, context, item, organizational unit, or scope. Granting roles on other
   * resource types is not supported, yet.</comment>
   * 
   * 
   * @param AssignedOn
   */
  public void setAssignedOn(String assignedOn) {
    this.assignedOn = assignedOn;
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


  public void setGrantedTo(String grantedTo) {
    this.grantedTo = grantedTo;
  }

  public String getGrantedTo() {
    return grantedTo;
  }



  /**
   * Delivers true if the granted role is of type 'depositor' for the given object (normally a
   * PubCollection).
   */
  public boolean isDepositor(ReferenceObject objRef) {
    if (objRef == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":isModerator:objectRef is null");
    }
    return (CoreserviceRole.DEPOSITOR.getRoleId().equals(role) && this.assignedOn.equals(objRef
        .getObjectId()));
  }

  /**
   * Delivers true if the granted role is of type 'moderator' for the given object (normally a
   * PubCollection).
   */
  public boolean isModerator(ReferenceObject objRef) {
    if (objRef == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":isModerator:objectRef is null");
    }
    return (CoreserviceRole.MODERATOR.getRoleId().equals(role) && this.assignedOn.equals(objRef
        .getObjectId()));
  }



  public void setGrantType(String grantType) {
    this.grantType = grantType;
  }

  public String getGrantType() {
    return grantType;
  }
}
