
package de.mpg.escidoc.services.common.valueobjects.intelligent.grants;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;

import de.escidoc.www.services.aa.UserAccountHandler;
import de.escidoc.www.services.aa.UserGroupHandler;
import de.mpg.escidoc.services.common.referenceobjects.ReferenceObject;
import de.mpg.escidoc.services.common.valueobjects.GrantVO.PredefinedRoles;
import de.mpg.escidoc.services.common.valueobjects.intelligent.IntelligentVO;
import de.mpg.escidoc.services.common.valueobjects.intelligent.usergroup.UserGroup.Factory;
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
    private Date propertiesCreationDate;
    private String propertiesGrantedTo;
    private String propertiesGrantType;
    private String propertiesCreatedBy;
    private Date propertiesRevocationDate;
    private String propertiesRevokedBy;
    private String propertiesGrantRemark;
    private String propertiesRevocationRemark;
    private String propertiesRole;
    private String propertiesAssignedOn;
    private String objid;
    private Date lastModificationDate;

    /** 
     * Get the 'creation-date' element value. 
     <create>discarded</create>
     <update>discarded</update>
     
     * 
     * @return value
     */
    public Date getPropertiesCreationDate() {
        return propertiesCreationDate;
    }

    /** 
     * Set the 'creation-date' element value. 
     <create>discarded</create>
     <update>discarded</update>
     
     * 
     * @param propertiesCreationDate
     */
    public void setPropertiesCreationDate(Date propertiesCreationDate) {
        this.propertiesCreationDate = propertiesCreationDate;
    }

    /** 
     * Get the 'created-by' element value. 
     <create>discarded</create>
     <update>discarded</update>
     
     * 
     * @return value
     */
    public String getPropertiesCreatedBy() {
        return propertiesCreatedBy;
    }

    /** 
     * Set the 'created-by' element value. 
     <create>discarded</create>
     <update>discarded</update>
     
     * 
     * @param propertiesCreatedBy
     */
    public void setPropertiesCreatedBy(String propertiesCreatedBy) {
        this.propertiesCreatedBy = propertiesCreatedBy;
    }

    /** 
     * Get the 'revocation-date' element value. 
     <create>discarded</create>
     <update>not-allowed (see note
     1.)</update>
     
     * 
     * @return value
     */
    public Date getPropertiesRevocationDate() {
        return propertiesRevocationDate;
    }

    /** 
     * Set the 'revocation-date' element value. 
     <create>discarded</create>
     <update>not-allowed (see note
     1.)</update>
     
     * 
     * @param propertiesRevocationDate
     */
    public void setPropertiesRevocationDate(Date propertiesRevocationDate) {
        this.propertiesRevocationDate = propertiesRevocationDate;
    }

    /** 
     * Get the 'revoked-by' element value. 
     <create>discarded</create>
     <update>not-allowed (see note
     1.)</update>
     
     * 
     * @return value
     */
    public String getPropertiesRevokedBy() {
        return propertiesRevokedBy;
    }

    /** 
     * Set the 'revoked-by' element value. 
     <create>discarded</create>
     <update>not-allowed (see note
     1.)</update>
     
     * 
     * @param propertiesRevokedBy
     */
    public void setPropertiesRevokedBy(String propertiesRevokedBy) {
        this.propertiesRevokedBy = propertiesRevokedBy;
    }

    /** 
     * Get the 'grant-remark' element value. 
     <create>optional</create>
     <update>not-allowed (see note
     1.)</update>
     
     * 
     * @return value
     */
    public String getPropertiesGrantRemark() {
        return propertiesGrantRemark;
    }

    /** 
     * Set the 'grant-remark' element value. 
     <create>optional</create>
     <update>not-allowed (see note
     1.)</update>
     
     * 
     * @param propertiesGrantRemark
     */
    public void setPropertiesGrantRemark(String propertiesGrantRemark) {
        this.propertiesGrantRemark = propertiesGrantRemark;
    }

    /** 
     * Get the 'revocation-remark' element value. 
     <create>discarded</create>
     <update>not-allowed (see note
     1.)</update>
     
     * 
     * @return value
     */
    public String getPropertiesRevocationRemark() {
        return propertiesRevocationRemark;
    }

    /** 
     * Set the 'revocation-remark' element value. 
     <create>discarded</create>
     <update>not-allowed (see note
     1.)</update>
     
     * 
     * @param propertiesRevocationRemark
     */
    public void setPropertiesRevocationRemark(
            String propertiesRevocationRemark) {
        this.propertiesRevocationRemark = propertiesRevocationRemark;
    }

    /** 
     * Get the 'role' element value. 
     <create>required</create>
     <update>not-allowed (see note
     1.)</update>
     
     * 
     * @return value
     */
    public String getPropertiesRole() {
        return propertiesRole;
    }

    /** 
     * Set the 'role' element value. 
     <create>required</create>
     <update>not-allowed (see note
     1.)</update>
     
     * 
     * @param propertiesRole
     */
    public void setPropertiesRole(String propertiesRole) {
        this.propertiesRole = propertiesRole;
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
    public String getPropertiesAssignedOn() {
        return propertiesAssignedOn;
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
     * @param propertiesAssignedOn
     */
    public void setPropertiesAssignedOn(String propertiesAssignedOn) {
        this.propertiesAssignedOn = propertiesAssignedOn;
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


    public void setPropertiesGrantedTo(String propertiesGrantedTo)
    {
        this.propertiesGrantedTo = propertiesGrantedTo;
    }

    public String getPropertiesGrantedTo()
    {
        return propertiesGrantedTo;
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
        return (PredefinedRoles.DEPOSITOR.frameworkValue().equals(propertiesRole) && this.propertiesAssignedOn.equals(objRef.getObjectId()));
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
        return (PredefinedRoles.MODERATOR.frameworkValue().equals(propertiesRole) && this.propertiesAssignedOn.equals(objRef.getObjectId()));
    }

    
    
    public void setPropertiesGrantType(String propertiesGrantType)
    {
        this.propertiesGrantType = propertiesGrantType;
    }

    public String getPropertiesGrantType()
    {
        return propertiesGrantType;
    }
    
    public void revokeInCoreservice(String userHandle, String comment) throws Exception
    {
        Factory.revokeGrant(userHandle, this, comment);
    }
    
    public void createInCoreservice(String userHandle, String comment) throws Exception
    {
        Factory.createGrant(userHandle, this);
    }
    
  
   



    public static class Factory
    {
        
        public static String marshal(Object object, Class bindingClass) throws Exception
        {
            IBindingFactory bindingFactory = BindingDirectory.getFactory("binding", bindingClass);
            IMarshallingContext macxt = bindingFactory.createMarshallingContext();
            StringWriter sw = new StringWriter();
            macxt.marshalDocument(object, "UTF-8", null, sw);
            return sw.toString();
        }
        
        public static Object unmarshal(String xml, Class bindingClass) throws Exception
        {
            IBindingFactory bindingFactory = BindingDirectory.getFactory("binding", bindingClass);
            IUnmarshallingContext unmacxt = bindingFactory.createUnmarshallingContext();
            StringReader sr = new StringReader(xml);
            Object o = unmacxt.unmarshalDocument(sr, null);
            return o;
        }
        
        
        public static Grant retrieveGrant(String userHandle, String userId, String grantId) throws Exception
        {

            UserGroupHandler ugh = ServiceLocator.getUserGroupHandler(userHandle);
            String grantXml = ugh.retrieveGrant(userId, grantId);
            Grant grant = (Grant)unmarshal(grantXml, Grant.class);
            return grant;
            
        }
        
        public static Grant createGrant(String userHandle, Grant grant) throws Exception
        {
            
            
            UserGroupHandler ugh = ServiceLocator.getUserGroupHandler(userHandle);
            String grantXml = marshal(grant, Grant.class);
            String createdGrantXml = ugh.createGrant(grant.getPropertiesGrantedTo(), grantXml);
            Grant createdGrant = (Grant)unmarshal(createdGrantXml, Grant.class);
            grant = createdGrant;
            return createdGrant;
            
        }
        
        
        public static GrantList retrieveCurrentGrantsForUser(String userHandle, String userGroupId) throws Exception
        {
            UserGroupHandler ugh = ServiceLocator.getUserGroupHandler(userHandle);
            String grantListXml = ugh.retrieveCurrentGrants(userGroupId);
            GrantList currentGrants = (GrantList)unmarshal(grantListXml, GrantList.class);
            return currentGrants;
        }
        
        
        
        public static GrantList retrieveGrantsForObject(String userHandle, String objectId, String roleId) throws Exception
        {
            UserAccountHandler uah = ServiceLocator.getUserAccountHandler(userHandle);
            String filter = "<param><filter name=\"objectId\">" + objectId + "</filter><filter name=\"roleId\">"+roleId+"</filter></param>";
            String grantListXml = uah.retrieveGrants(filter);
            GrantList currentGrants = (GrantList)unmarshal(grantListXml, GrantList.class);
            return currentGrants;
        }
        
        public static void revokeGrant(String userHandle, Grant grant, String comment) throws Exception
        {
            UserAccountHandler uah = ServiceLocator.getUserAccountHandler(userHandle);
            Calendar cal = new GregorianCalendar();
            cal.setTime(grant.getLastModificationDate());
            String param = "<param last-modification-date=\""+ DatatypeConverter.printDateTime(cal) +"\" >";
            param+="<revocation-remark>"+comment+"</revocation-remark>";
            param+="</param>";
            uah.revokeGrant(grant.getPropertiesGrantedTo(), grant.getObjid(), param);
            
        }
        
        public static void revokeGrants(String userHandle, List<Grant> grants, String comment) throws Exception
        {
            if (grants==null || grants.size()==0)
            {
                throw new IllegalArgumentException("The grant list is empty.");
            }
            UserAccountHandler uah = ServiceLocator.getUserAccountHandler(userHandle);
            String param = "<param><filter name=\"http://purl.org/dc/elements/1.1/identifier}\">";
            
            for (Grant grant : grants)
            {
                param+="<id>"+grant.getObjid()+"</id>";
                
            }
            param+="</filter><revocation-remark>"+comment+"</revocation-remark>";
            param+="</param>";
            uah.revokeGrants(grants.get(0).getPropertiesGrantedTo(), param);
            
        }
    
    

    }
}
