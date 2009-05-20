
package de.mpg.escidoc.services.common.valueobjects.intelligent.usergroup;

import de.mpg.escidoc.services.common.valueobjects.intelligent.IntelligentVO;
import de.mpg.escidoc.services.common.valueobjects.intelligent.usergroup.UserGroup;

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
}
