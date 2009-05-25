
package de.mpg.escidoc.services.common.valueobjects.intelligent.usergroup;

import de.escidoc.www.services.aa.UserGroupHandler;
import de.mpg.escidoc.services.common.valueobjects.intelligent.IntelligentVO;
import de.mpg.escidoc.services.common.valueobjects.intelligent.usergroup.UserGroup;
import de.mpg.escidoc.services.framework.ServiceLocator;

import java.util.ArrayList;
import java.util.List;

/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:element xmlns:ns="http://www.escidoc.de/schemas/usergroup/0.5" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="user-group-list">
 *   &lt;xs:complexType>
 *     &lt;xs:sequence>
 *       &lt;xs:element ref="ns:user-group" minOccurs="0" maxOccurs="unbounded"/>
 *     &lt;/xs:sequence>
 *   &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 */
public class UserGroupList extends IntelligentVO
{
    private List<UserGroup> userGroupListList = new ArrayList<UserGroup>();

    /** 
     * Get the list of 'user-group' element items.
     * 
     * @return list
     */
    public List<UserGroup> getUserGroupLists() {
        return userGroupListList;
    }

    /** 
     * Set the list of 'user-group' element items.
     * 
     * @param list
     */
    public void setUserGroupLists(List<UserGroup> list) {
        userGroupListList = list;
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
         * Retrieves a list of User Groups.
         * @param filter The filter for the user group list.
         * @param userHandle A user handle for authentication in the coreservice.
         * @return The list of User Groups.
         * @throws Exception If an error occurs in coreservice or during marshalling/unmarshalling.
         */
        public static UserGroupList retrieveUserGroups(String filter, String userHandle) throws Exception
        {
            UserGroupHandler ugh = ServiceLocator.getUserGroupHandler(userHandle);
            String uglXml = ugh.retrieveUserGroups(filter);
            UserGroupList ugld = (UserGroupList)IntelligentVO.unmarshal(uglXml, UserGroupList.class);
            
            return ugld;
        }
        
        /**
         * Retrieves all active user groups.
         * @param userHandle A user handle for authentication in the coreservice.
         * @return The list of User Groups.
         * @throws Exception If an error occurs in coreservice or during marshalling/unmarshalling.
         */
        public static UserGroupList retrieveActiveUserGroups(String userHandle) throws Exception
        {
            
            UserGroupHandler ugh = ServiceLocator.getUserGroupHandler(userHandle);
            /*
            String filter = "<param><filter name=\"/properties/active\">"+"true"+"</filter></param>";
            String uglXml = ugh.retrieveUserGroups(filter);
            UserGroupList ugl= (UserGroupList)Grant.Factory.unmarshal(uglXml, UserGroupList.class);
            */
            
            //workaround:
            UserGroup ug = UserGroup.Factory.retrieve("escidoc:121631", userHandle);
            UserGroupList ugl = new UserGroupList();
            List<UserGroup> uglList = new ArrayList<UserGroup>();
            uglList.add(ug);
            ugl.setUserGroupLists(uglList);
            
            
            return ugl;
        }
    }
}
