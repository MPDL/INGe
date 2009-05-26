
package de.mpg.escidoc.services.common.valueobjects.intelligent.usergroup;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.bind.DatatypeConverter;
import javax.xml.rpc.ServiceException;

import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.UserGroupNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.www.services.aa.UserGroupHandler;
import de.mpg.escidoc.services.common.valueobjects.intelligent.IntelligentVO;
import de.mpg.escidoc.services.framework.ServiceLocator;

/** 
 * 
 <para>
 Following you will find information which elements
 and attributes are "required", "optional",
 "not-allowed" or will be "discarded" in the input
 XML-stream when creating or updating such an object.
 </para>
 <para>
 In "create" the rule for creating a new object is
 defined.
 </para>
 <para>
 In "update" the rule for updating an object is
 defined.
 </para>
 <para>
 Possible values are: required | optional |
 not-allowed | discarded
 </para>
 <para>
 required: this element or attribute has to be
 delivered
 </para>
 <para>
 optional: this element or attribute can be delivered
 and will be kept
 </para>
 <para>
 not-allowed: this element or attribute is not
 allowed in delivery and will cause an exception
 </para>
 <para>
 discarded: this element or attribute can be
 delivered but will not be used
 </para>
 
 <create>required</create>
 <update>required</update>
 
 * 
 * Schema fragment(s) for this class:
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
 *       &lt;xs:element ref="ns2:selectors" minOccurs="0"/>
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
public class UserGroup extends IntelligentVO
{
    private Date creationDate;
    private String createdBy;
    private String modifiedBy;
    private String email;
    private String name;
    private String label;
    private String description;
    private String type;
    private boolean active;
    private Selectors selectors;
    private Resources resources;
    private String objid;
    private Date lastModificationDate;

    
    /**
     * Retrieves a user group from the coreservice.
     * @param id the id of the user group.
     * @param userHandle A user handle for authentication in the coreservice.
     * @throws Exception If an error occurs in coreservice or during marshalling/unmarshalling.
     */
    public UserGroup(String escidocId, String userHandle) throws RuntimeException
    {
        UserGroup newUg = Factory.retrieve(escidocId, userHandle);
        copyInFields(newUg);
    }
    
    public UserGroup()
    {
        
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
     * @param creationDate
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
     * @param createdBy
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /** 
     * Get the 'modified-by' element value. 
     <create>discarded</create>
     <update>discarded</update>
     
     * 
     * @return value
     */
    public String getModifiedBy() {
        return modifiedBy;
    }

    /** 
     * Set the 'modified-by' element value. 
     <create>discarded</create>
     <update>discarded</update>
     
     * 
     * @param modifiedBy
     */
    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    /** 
     * Get the 'email' element value. 
     <create>optional</create>
     <update>optional</update>
     
     * 
     * @return value
     */
    public String getEmail() {
        return email;
    }

    /** 
     * Set the 'email' element value. 
     <create>optional</create>
     <update>optional</update>
     
     * 
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /** 
     * Get the 'name' element value. 
     <create>required</create>
     <update>required</update>
     
     * 
     * @return value
     */
    public String getName() {
        return name;
    }

    /** 
     * Set the 'name' element value. 
     <create>required</create>
     <update>required</update>
     
     * 
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /** 
     * Get the 'label' element value. 
     <create>required</create>
     <update>required</update>
     
     * 
     * @return value
     */
    public String getLabel() {
        return label;
    }

    /** 
     * Set the 'label' element value. 
     <create>required</create>
     <update>required</update>
     
     * 
     * @param label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /** 
     * Get the 'description' element value. 
     <create>optional</create>
     <update>optional</update>
     
     * 
     * @return value
     */
    public String getDescription() {
        return description;
    }

    /** 
     * Set the 'description' element value. 
     <create>optional</create>
     <update>optional</update>
     
     * 
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /** 
     * Get the 'type' element value. 
     <create>optional</create>
     <update>optional</update>
     
     * 
     * @return value
     */
    public String getType() {
        return type;
    }

    /** 
     * Set the 'type' element value. 
     <create>optional</create>
     <update>optional</update>
     
     * 
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /** 
     * Get the 'active' element value. 
     <create>
     discarded (see note 1.)
     </create>
     <update>
     discarded (see note 1.)
     </update>
     <comment>
     1. A created user group is
     always active. It is not
     possible to activate or
     deactivate a user group
     using the update method.
     This can only be done by
     using the activate() and
     deactivate() methods.
     </comment>
     
     * 
     * @return value
     */
    public boolean getActive() {
        return active;
    }

    /** 
     * Set the 'active' element value. 
     <create>
     discarded (see note 1.)
     </create>
     <update>
     discarded (see note 1.)
     </update>
     <comment>
     1. A created user group is
     always active. It is not
     possible to activate or
     deactivate a user group
     using the update method.
     This can only be done by
     using the activate() and
     deactivate() methods.
     </comment>
     
     * 
     * @param active
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /** 
     * Get the 'selectors' element value.
     * 
     * @return value
     */
    public Selectors getSelectors() {
        return selectors;
    }

    /** 
     * Set the 'selectors' element value.
     * 
     * @param selectors
     */
    public void setSelectors(Selectors selectors) {
        this.selectors = selectors;
    }

    /** 
     * Get the 'resources' element value.
     * 
     * @return value
     */
    public Resources getResources() {
        return resources;
    }

    /** 
     * Set the 'resources' element value.
     * 
     * @param resources
     */
    public void setResources(Resources resources) {
        this.resources = resources;
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
    
    /**
     * Creates this user group in the coreservice.
     * @param userHandle A user handle for authentication in the coreservice.
     * @throws Exception If an error occurs in coreservice or during marshalling/unmarshalling.
     */
    public void createInCoreservice(String userHandle) throws RuntimeException
    {
        Factory.create(this, userHandle);
    }
    
    /**
     * Updates this user group in the coreservice.
     * @param userHandle A user handle for authentication in the coreservice.
     * @throws Exception If an error occurs in coreservice or during marshalling/unmarshalling.
     */
    public void updateInCoreservice(String userHandle) throws RuntimeException
    {
        Factory.update(this, userHandle);
    }
    
    /**
     * Deletes this user group in the coreservice.
     * @param userHandle A user handle for authentication in the coreservice.
     * @throws Exception If an error occurs in coreservice or during marshalling/unmarshalling.
     */
    public void deleteInCoreservice(String userHandle) throws RuntimeException
    {
        Factory.delete(this, userHandle);
    }
    
    /**
     * Activates this user group in the coreservice.
     * @param userHandle A user handle for authentication in the coreservice.
     * @throws Exception If an error occurs in coreservice or during marshalling/unmarshalling.
     */
    public void activateInCoreservice(String userHandle) throws RuntimeException
    {
        Factory.activate(this, userHandle);
    }
    
    /**
     * Deactivates this user group in the coreservice.
     * @param userHandle A user handle for authentication in the coreservice.
     * @throws Exception If an error occurs in coreservice or during marshalling/unmarshalling.
     */
    public void deactivateInCoreservice(String userHandle) throws RuntimeException
    {
        Factory.deactivate(this, userHandle);
    }
    
    /**
     * Adds new selectors to this user group in the coreservice.
     * @param selectors The list of selectors to be added.
     * @param userHandle A user handle for authentication in the coreservice.
     * @throws Exception If an error occurs in coreservice or during marshalling/unmarshalling.
     */
    public void addNewSelectorsInCoreservice(Selectors selectors, String userHandle) throws RuntimeException
    {
        Factory.addSelectors(selectors, this, userHandle);
    }
    
    /**
     * Removes selectors from this user group in the coreservice.
     * @param selectors The list of selectors to be removed.
     * @param userHandle A user handle for authentication in the coreservice.
     * @throws Exception If an error occurs in coreservice or during marshalling/unmarshalling.
     */
    public void removeSelectorsInCoreservice(Selectors selectors, String userHandle) throws RuntimeException
    {
        Factory.removeSelectors(selectors, this, userHandle);
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
         * Retrieves a user group from the coreservice.
         * @param id the id of the user group.
         * @param userHandle A user handle for authentication in the coreservice.
         * @return The User Group object that was retrieved.
         * @throws Exception If an error occurs in coreservice or during marshalling/unmarshalling.
         */
        private static UserGroup retrieve(String id, String userHandle) throws RuntimeException
        {
            try
            {
                UserGroupHandler ugh = ServiceLocator.getUserGroupHandler(userHandle);
                String ugXml = ugh.retrieve(id);
                UserGroup ugn = (UserGroup) IntelligentVO.unmarshal(ugXml, UserGroup.class);
                return ugn;
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
           
        }
        
        /**
         * Updates the given User group in the coreservice.
         * @param userGroup The user group VO to be updated.
         * @param userHandle A user handle for authentication in the coreservice.
         * @return The updated user group VO.
         * @throws Exception If an error occurs in coreservice or during marshalling/unmarshalling.
         */
        private static UserGroup update(UserGroup userGroup, String userHandle) throws RuntimeException
        {
            try
            {
                UserGroupHandler ugh = ServiceLocator.getUserGroupHandler(userHandle);
                String userGroupXml = IntelligentVO.marshal(userGroup, UserGroup.class);
                
                String updatedUserGroupXml = ugh.update(userGroup.getObjid(), userGroupXml);
                
                UserGroup updatedUserGroup = (UserGroup) IntelligentVO.unmarshal(updatedUserGroupXml, UserGroup.class);
    
                userGroup.copyInFields(updatedUserGroup);
                return userGroup;
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        
        /**
         * Creates the given user group in the coreservice.
         * @param userGroup The user group to be created.
         * @param userHandle A user handle for authentication in the coreservice.
         * @return The created User Group.
         * @throws Exception If an error occurs in coreservice or during marshalling/unmarshalling.
         */
        private static UserGroup create(UserGroup userGroup, String userHandle) throws RuntimeException
        {
            try
            {
                UserGroupHandler ugh = ServiceLocator.getUserGroupHandler(userHandle);
                String userGroupXml = IntelligentVO.marshal(userGroup, UserGroup.class);
                String createdUgXml = ugh.create(userGroupXml);
                UserGroup createdUg = (UserGroup) IntelligentVO.unmarshal(createdUgXml, UserGroup.class);
                userGroup.copyInFields(createdUg);
                
                return createdUg;
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        
        /**
         * Deletes the given user group in the coreservice.
         * @param userGroup The user group to be deleted
         * @param userHandle A user handle for authentication in the coreservice.
         * @throws Exception If an error occurs in coreservice or during marshalling/unmarshalling.
         */
        private static void delete(UserGroup userGroup, String userHandle) throws RuntimeException
        {
            try
            {
                UserGroupHandler ugh = ServiceLocator.getUserGroupHandler(userHandle);
                ugh.delete(userGroup.getObjid());
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        
        /**
         * Activates the user group in the coreservice.
         * @param userGroup The user group to be activated.
         * @param userHandle A user handle for authentication in the coreservice.
         * @throws Exception If an error occurs in coreservice or during marshalling/unmarshalling.
         */
        private static void activate(UserGroup userGroup, String userHandle) throws RuntimeException
        {
            try
            {
                UserGroupHandler ugh = ServiceLocator.getUserGroupHandler(userHandle);
                Calendar cal = new GregorianCalendar();
                cal.setTime(userGroup.getLastModificationDate());
                
                ugh.activate(userGroup.getObjid(), "<param last-modification-date=\"" + DatatypeConverter.printDateTime(cal) + "\" >");
                UserGroup updatedUserGroup = userGroup = retrieve(userGroup.getObjid(), userHandle);
                userGroup.copyInFields(updatedUserGroup);
                
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        
        /**
         * Deactivates the user group in the coreservice.
         * @param userGroup The user group to be deactivated.
         * @param userHandle A user handle for authentication in the coreservice.
         * @throws Exception If an error occurs in coreservice or during marshalling/unmarshalling.
         */
        private static void deactivate(UserGroup userGroup, String userHandle) throws RuntimeException
        {
            try
            {
                UserGroupHandler ugh = ServiceLocator.getUserGroupHandler(userHandle);
                Calendar cal = new GregorianCalendar();
                cal.setTime(userGroup.getLastModificationDate());
                ugh.deactivate(userGroup.getObjid(), "<param last-modification-date=\""+DatatypeConverter.printDateTime(cal) + "\" >");
                
                UserGroup updatedUserGroup = retrieve(userGroup.getObjid(), userHandle);
                userGroup.copyInFields(updatedUserGroup);
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        
        /**
         * Adds selectors to a given user group.
         * @param selectors The selectors to be added.
         * @param userGroup The user group to which the selectors should be added.
         * @param userHandle A user handle for authentication in the coreservice.
         * @throws Exception If an error occurs in coreservice or during marshalling/unmarshalling.
         */
        private static void addSelectors(Selectors selectors, UserGroup userGroup, String userHandle) throws RuntimeException
        {
            try
            {
                UserGroupHandler ugh = ServiceLocator.getUserGroupHandler(userHandle);
                Calendar cal = new GregorianCalendar();
                cal.setTime(userGroup.getLastModificationDate());
                
                String param = "<param last-modification-date=\"" + DatatypeConverter.printDateTime(cal) + "\">";
                for (Selector selector : selectors.getSelectors())
                {
                    param += "<selector name=\"" + selector.getName() + "\" type=\""+selector.getType() + "\" >" + selector.getString() + "</selector>";
                }
                param += "</param>";
                
                ugh.addSelectors(userGroup.getObjid(), param);
                UserGroup updatedUserGroup = retrieve(userGroup.getObjid(), userHandle);
                userGroup.copyInFields(updatedUserGroup);
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        
        /**
         * Removes selectors from a given user group.
         * @param selectors The selectors to be added.
         * @param userGroup The user group to which the selectors should be added.
         * @param userHandle A user handle for authentication in the coreservice.
         * @throws Exception If an error occurs in coreservice or during marshalling/unmarshalling.
         */
        private static void removeSelectors(Selectors selectors, UserGroup userGroup, String userHandle) throws RuntimeException
        {
            try
            {
                
                UserGroupHandler ugh = ServiceLocator.getUserGroupHandler(userHandle);
                Calendar cal = new GregorianCalendar();
                cal.setTime(userGroup.getLastModificationDate());
                
                String param = "<param last-modification-date=\"" + DatatypeConverter.printDateTime(cal) + "\">";
                for (Selector selector : selectors.getSelectors())
                {
                    param += "<selector name=\"" + selector.getName() + "\" type=\"" + selector.getType()+"\" >" + selector.getString() + "</selector>";
                }
                param += "</param>";
                
                ugh.removeSelectors(userGroup.getObjid(), param);
                UserGroup updatedUserGroup = retrieve(userGroup.getObjid(), userHandle);
                userGroup.copyInFields(updatedUserGroup);
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        
 
        
        
    }
}
