
package de.mpg.escidoc.services.common.valueobjects.intelligent.grants;

import java.util.Date;

import de.escidoc.www.services.aa.UserAccountHandler;
import de.escidoc.www.services.aa.UserGroupHandler;
import de.mpg.escidoc.services.common.referenceobjects.ReferenceObject;
import de.mpg.escidoc.services.common.valueobjects.GrantVO.PredefinedRoles;
import de.mpg.escidoc.services.common.valueobjects.intelligent.IntelligentVO;
import de.mpg.escidoc.services.common.valueobjects.intelligent.grants.CurrentGrants.UserType;
import de.mpg.escidoc.services.common.xmltransforming.JiBXHelper;
import de.mpg.escidoc.services.framework.ServiceLocator;

/** 
 * 
 <create>required</create>
 <update>not-allowed (see note 1.)</update>
 <comment>1. Update is not defined for a grant.</comment>
 
 * 
 * Schema fragment(s) for this class:
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
public class Grant extends IntelligentVO
{
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

    
    public enum CoreserviceRole
    {
        DEPOSITOR ("escidoc:role-depositor"),
        MODERATOR ("escidoc:role-moderator"),
        MD_EDITOR ("escidoc:role-md-editor"),
        AUDIENCE ("escidoc:role-audience"),
        COLLABORATOR_VIEWER ("escidoc:role-collaborator"),
        COLLABORATOR_MODIFIER("escidoc:role-collaborator-modifier");
        
        private String roleId;
        
        
        private CoreserviceRole(String roleId)
        {
            this.setRoleId(roleId);
        }


        public void setRoleId(String roleId)
        {
            this.roleId = roleId;
        }


        public String getRoleId()
        {
            return roleId;
        }
        
    }
    
    /**
     * Retrieves a grant from the coreservice.
     * @param grantId the id of the grant.
     * @param userHandle A user handle for authentication in the coreservice.
     * @param userId The id of the user or user group that owns this grant.
     * @throws Exception If an error occurs in coreservice or during marshalling/unmarshalling.
     */
    public Grant(String userHandle, String userId, String grantId, UserType userType) throws RuntimeException
    {
        Grant grant = Factory.retrieveGrant(userHandle, userId, grantId, userType);
        copyInFields(grant);
    }
    
    
    public Grant()
    {
        
    }
    
    /**
     * Clone constructor
     */
    public Grant(Grant toBeCloned)
    {
        super(toBeCloned);
    }
    
    
    /** 
     * Get the 'creation-date' element value. 
     <create>discarded</create>
     <update>discarded</update>
     
     * 
     * @return value
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /** 
     * Set the 'creation-date' element value. 
     <create>discarded</create>
     <update>discarded</update>
     
     * 
     * @param CreationDate
     */
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    /** 
     * Get the 'created-by' element value. 
     <create>discarded</create>
     <update>discarded</update>
     
     * 
     * @return value
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /** 
     * Set the 'created-by' element value. 
     <create>discarded</create>
     <update>discarded</update>
     
     * 
     * @param CreatedBy
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /** 
     * Get the 'revocation-date' element value. 
     <create>discarded</create>
     <update>not-allowed (see note
     1.)</update>
     
     * 
     * @return value
     */
    public Date getRevocationDate() {
        return revocationDate;
    }

    /** 
     * Set the 'revocation-date' element value. 
     <create>discarded</create>
     <update>not-allowed (see note
     1.)</update>
     
     * 
     * @param RevocationDate
     */
    public void setRevocationDate(Date revocationDate) {
        this.revocationDate = revocationDate;
    }

    /** 
     * Get the 'revoked-by' element value. 
     <create>discarded</create>
     <update>not-allowed (see note
     1.)</update>
     
     * 
     * @return value
     */
    public String getRevokedBy() {
        return revokedBy;
    }

    /** 
     * Set the 'revoked-by' element value. 
     <create>discarded</create>
     <update>not-allowed (see note
     1.)</update>
     
     * 
     * @param RevokedBy
     */
    public void setRevokedBy(String revokedBy) {
        this.revokedBy = revokedBy;
    }

    /** 
     * Get the 'grant-remark' element value. 
     <create>optional</create>
     <update>not-allowed (see note
     1.)</update>
     
     * 
     * @return value
     */
    public String getGrantRemark() {
        return grantRemark;
    }

    /** 
     * Set the 'grant-remark' element value. 
     <create>optional</create>
     <update>not-allowed (see note
     1.)</update>
     
     * 
     * @param GrantRemark
     */
    public void setGrantRemark(String grantRemark) {
        this.grantRemark = grantRemark;
    }

    /** 
     * Get the 'revocation-remark' element value. 
     <create>discarded</create>
     <update>not-allowed (see note
     1.)</update>
     
     * 
     * @return value
     */
    public String getRevocationRemark() {
        return revocationRemark;
    }

    /** 
     * Set the 'revocation-remark' element value. 
     <create>discarded</create>
     <update>not-allowed (see note
     1.)</update>
     
     * 
     * @param RevocationRemark
     */
    public void setRevocationRemark(
            String revocationRemark) {
        this.revocationRemark = revocationRemark;
    }

    /** 
     * Get the 'role' element value. 
     <create>required</create>
     <update>not-allowed (see note
     1.)</update>
     
     * 
     * @return value
     */
    public String getRole() {
        return role;
    }

    /** 
     * Set the 'role' element value. 
     <create>required</create>
     <update>not-allowed (see note
     1.)</update>
     
     * 
     * @param Role
     */
    public void setRole(String role) {
        this.role = role;
    }

    /** 
     * Get the 'assigned-on' element value. 
     <create>optional (see note
     2.)</create>
     <update>not-allowed (see note
     1.)</update>
     <comment>2. The referenced resource
     must be of type container,
     content-model, context, item,
     organizational unit, or scope.
     Granting roles on other resource
     types is not supported,
     yet.</comment>
     
     * 
     * @return value
     */
    public String getAssignedOn() {
        return assignedOn;
    }

    /** 
     * Set the 'assigned-on' element value. 
     <create>optional (see note
     2.)</create>
     <update>not-allowed (see note
     1.)</update>
     <comment>2. The referenced resource
     must be of type container,
     content-model, context, item,
     organizational unit, or scope.
     Granting roles on other resource
     types is not supported,
     yet.</comment>
     
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
     <update>required</update>
     <comment>required only in root element on update</comment>
     * 
     * @return value
     */
    public Date getLastModificationDate() {
        return lastModificationDate;
    }

    /** 
     * Set the 'last-modification-date' attribute value. <create>discarded</create>
     <update>required</update>
     <comment>required only in root element on update</comment>
     * 
     * @param lastModificationDate
     */
    public void setLastModificationDate(Date lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }


    public void setGrantedTo(String grantedTo)
    {
        this.grantedTo = grantedTo;
    }

    public String getGrantedTo()
    {
        return grantedTo;
    }

    
    
    /**
     * Delivers true if the granted role is of type 'depositor' for the given object (normally a PubCollection).
     */
    public boolean isDepositor(ReferenceObject objRef)
    {
        if (objRef == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName()
                    + ":isModerator:objectRef is null");
        }
        return (PredefinedRoles.DEPOSITOR.frameworkValue().equals(role) && this.assignedOn.equals(objRef.getObjectId()));
    }

    /**
     * Delivers true if the granted role is of type 'moderator' for the given object (normally a PubCollection).
     */
    public boolean isModerator(ReferenceObject objRef)
    {
        if (objRef == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName()
                    + ":isModerator:objectRef is null");
        }
        return (PredefinedRoles.MODERATOR.frameworkValue().equals(role) && this.assignedOn.equals(objRef.getObjectId()));
    }

    
    
    public void setGrantType(String grantType)
    {
        this.grantType = grantType;
    }

    public String getGrantType()
    {
        return grantType;
    }
    
    /**
     * Revokes this grant in the coreservice.
     * @param userHandle userHandle
     * @param comment The revocation comment.
     * @throws Exception If an error occurs in coreservice or during marshalling/unmarshalling.
     */
    public void revokeInCoreservice(String userHandle, String comment) throws RuntimeException
    {
        Factory.revokeGrant(userHandle, this, comment);
    }
    
    /**
     * Creates this grant in the coreservice.
     * @param userHandle A user handle for authentication in the coreservice.
     * @param comment The creation comment.
     * @throws Exception If an error occurs in coreservice or during marshalling/unmarshalling.
     */
    public void createInCoreservice(String userHandle, String comment) throws RuntimeException
    {
        Factory.createGrant(userHandle, this);
    }
    
  
   


    /**
     * Inner factory class for communicating with coreservice and marshalling/unmarshalling this VO.
     *
     * @author Markus Haarlaender (initial creation)
     * @author $Author$ (last modification)
     * @version $Revision$ $LastChangedDate$
     *
     */
    public static class Factory
    {
        /**
         * Retrieves a grant from the coreservice.
         * @param grantId the id of the grant.
         * @param userHandle A user handle for authentication in the coreservice.
         * @param userId The id of the user or user group that owns this grant.
         * @return The Grant object that was retrieved.
         * @throws Exception If an error occurs in coreservice or during marshalling/unmarshalling.
         */
        private static Grant retrieveGrant(String userHandle, String userId, String grantId, UserType ut) throws RuntimeException
        {
            String grantXml = "";

            try
            {
                if (ut.equals(UserType.USER_GROUP))
                {
                    UserGroupHandler ugh = ServiceLocator.getUserGroupHandler(userHandle);
                    grantXml = ugh.retrieveGrant(userId, grantId);
                   
                }
                else if (ut.equals(UserType.USER_ACCOUNT))
                {
                    UserAccountHandler uah = ServiceLocator.getUserAccountHandler(userHandle);
                    grantXml = uah.retrieveGrant(userId, grantId);
                    
                }
                Grant grant = (Grant) IntelligentVO.unmarshal(grantXml, Grant.class);
                return grant;
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
            
        }
        
        /**
         * Creates the given grant in the coreservice.
         * @param grant The grant to be created.
         * @param userHandle A user handle for authentication in the coreservice.
         * @return The created Grant.
         * @throws Exception If an error occurs in coreservice or during marshalling/unmarshalling.
         */
        private static Grant createGrant(String userHandle, Grant grant) throws RuntimeException
        {
            try
            {
                String grantXml = IntelligentVO.marshal(grant, Grant.class);
                String createdGrantXml = "";
                
                if (grant.getGrantType().equals("user-account"))
                {
                    UserAccountHandler uah = ServiceLocator.getUserAccountHandler(userHandle);
                    createdGrantXml = uah.createGrant(grant.getGrantedTo(), grantXml);
                    
                }
                else if (grant.getGrantType().equals("user-group"));
                {
                    UserGroupHandler ugh = ServiceLocator.getUserGroupHandler(userHandle);
                    createdGrantXml = ugh.createGrant(grant.getGrantedTo(), grantXml);
                    
                }
                
                Grant createdGrant = (Grant) IntelligentVO.unmarshal(createdGrantXml, Grant.class);
                grant.copyInFields(createdGrant);
                return createdGrant;
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
            
        }
        

        /**
         * Revokes the given grant in the coreservice.
         * @param userHandle A user handle for authentication in the coreservice.
         * @param grant The grant to be revoked.
         * @param comment The revocation comment
         * @throws Exception If an error occurs in coreservice or during marshalling/unmarshalling.
         */
        private static void revokeGrant(String userHandle, Grant grant, String comment) throws RuntimeException
        {
            try
            {
                
                //workaround if last modification date is missing in a list
                if(grant.getLastModificationDate()==null)
                {
                    grant.setLastModificationDate(grant.getCreationDate());
                    //grant = new Grant(userHandle, grant.getGrantedTo(), grant.getObjid(), UserType.valueOf(grant.getGrantType()));
                }
                
                String param = "<param last-modification-date=\"" + JiBXHelper.serializeDate(grant.getLastModificationDate()) + "\" >";
                param += "<revocation-remark>" + comment + "</revocation-remark>";
                param += "</param>";
                
                
                if (grant.getGrantType().equals("user-account"))
                {
                    UserAccountHandler uah = ServiceLocator.getUserAccountHandler(userHandle);
                    uah.revokeGrant(grant.getGrantedTo(), grant.getObjid(), param);
                    
                }
                else if (grant.getGrantType().equals("user-group"));
                {
                        
                   UserGroupHandler ugh = ServiceLocator.getUserGroupHandler(userHandle);
                   ugh.revokeGrant(grant.getGrantedTo(), grant.getObjid(), param);
                   
                }
    
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
            
        }
        
        
    
    

    }
}
