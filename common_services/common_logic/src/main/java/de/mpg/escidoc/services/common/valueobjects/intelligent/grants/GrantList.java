
package de.mpg.escidoc.services.common.valueobjects.intelligent.grants;

import de.escidoc.www.services.aa.UserAccountHandler;
import de.escidoc.www.services.aa.UserGroupHandler;
import de.mpg.escidoc.services.common.valueobjects.intelligent.IntelligentVO;
import de.mpg.escidoc.services.common.valueobjects.intelligent.grants.Grant;
import de.mpg.escidoc.services.framework.ServiceLocator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/** 
 * 
 <para> Following you will find information which elements and
 attributes are "required", "optional", "not-allowed" or
 will be "discarded" in the input XML-stream when creating or
 updating such an object. </para>
 <para> In "create" the rule for creating a new object is defined.
 </para>
 <para> In "update" the rule for updating an object is defined.
 </para>
 <para> Possible values are: required | optional | not-allowed |
 discarded </para>
 <para> required: this element or attribute has to be delivered
 </para>
 <para> optional: this element or attribute can be delivered and
 will be kept </para>
 <para> not-allowed: this element or attribute is not allowed in
 delivery and will cause an exception </para>
 <para> discarded: this element or attribute can be delivered but
 will not be used </para>
 
 <comment>This is a read-only resource representing a list of the
 current grants of the user account. Neither create nor update
 is supported for this list.</comment>
 
 * 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:element xmlns:ns="http://www.escidoc.de/schemas/commontypes/0.4" xmlns:ns1="http://www.escidoc.de/schemas/grants/0.4" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="current-grants">
 *   &lt;xs:complexType>
 *     &lt;xs:sequence>
 *       &lt;xs:element ref="ns1:grant" minOccurs="0" maxOccurs="unbounded"/>
 *     &lt;/xs:sequence>
 *     &lt;xs:attributeGroup ref="ns:eSciDocRootElementAttributes"/>
 *   &lt;/xs:complexType>
 * &lt;/xs:element>
 * 
 * &lt;xs:attributeGroup xmlns:ns="http://www.escidoc.de/schemas/commontypes/0.4" xmlns:ns1="http://www.escidoc.de/schemas/grants/0.4" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="eSciDocRootElementAttributes">
 *   &lt;xs:attribute type="xs:dateTime" name="last-modification-date"/>
 * &lt;/xs:attributeGroup>
 * </pre>
 */
public class GrantList  extends IntelligentVO
{
    private List<Grant> grantList = new ArrayList<Grant>();
    private Date lastModificationDate;
    private int numberOfRecords;
    private int limit;
    private int offset;
    
   
    
    public GrantList(String userHandle, String filter)
    {
        GrantList gl = Factory.retrieveGrants(userHandle, filter);
        copyFieldsIn(gl);
    }
    
    public GrantList()
    {
        
    }

    public int getNumberOfRecords()
    {
        return numberOfRecords;
    }

    public void setNumberOfRecords(int numberOfRecords)
    {
        this.numberOfRecords = numberOfRecords;
    }

    public int getLimit()
    {
        return limit;
    }

    public void setLimit(int limit)
    {
        this.limit = limit;
    }

    public int getOffset()
    {
        return offset;
    }

    public void setOffset(int offset)
    {
        this.offset = offset;
    }

    /** 
     * Get the list of 'grant' element items.
     * 
     * @return list
     */
    public List<Grant> getGrants() {
        return grantList;
    }

    /** 
     * Set the list of 'grant' element items.
     * 
     * @param list
     */
    public void setGrants(List<Grant> list) {
        grantList = list;
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
    
    
    public void revokeGrants(String userHandle, String revocationComment)
    {
        Factory.revokeGrants(userHandle, getGrants(), revocationComment);
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
         * Retrieves the current grants the given user or user group owns.
         * @param userHandle A user handle for authentication in the coreservice.
         * @param userGroupId The id of the user or user group.
         * @return The list of grants for the user / user group.
         * @throws Exception If an error occurs in coreservice or during marshalling/unmarshalling.
         */
        public static GrantList retrieveCurrentGrantsForUser(String userHandle, String userGroupId) throws RuntimeException
        {
            try
            {
                UserGroupHandler ugh = ServiceLocator.getUserGroupHandler(userHandle);
                String grantListXml = ugh.retrieveCurrentGrants(userGroupId);
                GrantList currentGrants = (GrantList) IntelligentVO.unmarshal(grantListXml, GrantList.class);
                return currentGrants;
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        
        
        /**
         * Retrieves the grants for a given escidoc object and a given role
         * @param userHandle A user handle for authentication in the coreservice.
         * @param objectId The id of the object for which the grants should be retrieved
         * @param roleId The id of the role that a user should have on that object.
         * @return The list of grants.
         * @throws Exception If an error occurs in coreservice or during marshalling/unmarshalling.
         */
        public static GrantList retrieveGrantsForObject(String userHandle, String objectId, String roleId) throws RuntimeException
        {
            try
            {
                UserAccountHandler uah = ServiceLocator.getUserAccountHandler(userHandle);
                String filter = "<param><filter name=\"objectId\">" + objectId + "</filter><filter name=\"roleId\">" + roleId + "</filter></param>";
                String grantListXml = uah.retrieveGrants(filter);
                GrantList currentGrants = (GrantList) IntelligentVO.unmarshal(grantListXml, GrantList.class);
                   return currentGrants;
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        
        /**
         * Retrieves the grants for a given filter
         * @param userHandle A user handle for authentication in the coreservice.
         * @param filter The filter
         * @return The list of grants.
         * @throws Exception If an error occurs in coreservice or during marshalling/unmarshalling.
         */
        private static GrantList retrieveGrants(String userHandle, String filter) throws RuntimeException
        {
            try
            {
                UserAccountHandler uah = ServiceLocator.getUserAccountHandler(userHandle);
                String grantListXml = uah.retrieveGrants(filter);
                GrantList currentGrants = (GrantList) IntelligentVO.unmarshal(grantListXml, GrantList.class);
                   return currentGrants;
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        
        
        /**
         * Revokes a list of grants in the coreservice.
         * @param userHandle A user handle for authentication in the coreservice.
         * @param grants The list of grants to be revoked.
         * @param comment The revocation comment
         * @throws Exception If an error occurs in coreservice or during marshalling/unmarshalling.
         */
        private static void revokeGrants(String userHandle, List<Grant> grants, String comment) throws RuntimeException
        {
            try
            {
                if (grants==null || grants.size()==0)
                {
                    throw new IllegalArgumentException("The grant list is empty.");
                }
                UserAccountHandler uah = ServiceLocator.getUserAccountHandler(userHandle);
                String param = "<param><filter name=\"http://purl.org/dc/elements/1.1/identifier}\">";
                
                for (Grant grant : grants)
                {
                    param += "<id>" + grant.getObjid() + "</id>";
                    
                }
                param += "</filter><revocation-remark>" + comment + "</revocation-remark>";
                param += "</param>";
                uah.revokeGrants(grants.get(0).getGrantedTo(), param);
            
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
            
        }
    }
}
