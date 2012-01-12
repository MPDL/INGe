
package de.mpg.escidoc.services.common.valueobjects.intelligent.usergroup;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.escidoc.www.services.aa.UserGroupHandler;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.SearchRetrieveRecordVO;
import de.mpg.escidoc.services.common.valueobjects.SearchRetrieveResponseVO;
import de.mpg.escidoc.services.common.valueobjects.intelligent.IntelligentVO;
import de.mpg.escidoc.services.common.valueobjects.intelligent.grants.Grant;
import de.mpg.escidoc.services.common.valueobjects.intelligent.grants.GrantList;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;
import de.mpg.escidoc.services.framework.ProxyHelper;
import de.mpg.escidoc.services.framework.ServiceLocator;

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
     * Retrieves a list of User Groups.
     * @param filter The filter for the user group list.
     * @param userHandle A user handle for authentication in the coreservice.
     * @throws Exception If an error occurs in coreservice or during marshalling/unmarshalling.
     */
    public UserGroupList(HashMap<String, String[]> filter, String userHandle) throws RuntimeException
    {
        UserGroupList ugl = Factory.retrieveUserGroups(filter, userHandle);
        copyInFields(ugl);
    }
    
    public UserGroupList()
    {
        
    }
    
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
        private static UserGroupList retrieveUserGroups(HashMap<String, String[]> filter, String userHandle) throws RuntimeException
        {
            UserGroupList ugld;
            try
            {
                UserGroupHandler ugh = ServiceLocator.getUserGroupHandler(userHandle);
                String uglXml = ugh.retrieveUserGroups(filter);
                SearchRetrieveResponseVO resp = (SearchRetrieveResponseVO)IntelligentVO.unmarshal(uglXml, SearchRetrieveResponseVO.class);
                
                List<SearchRetrieveRecordVO> results = resp.getRecords(); 
                List<UserGroup> userGroupList = new ArrayList<UserGroup>();
                ugld = new UserGroupList();
                ugld.setUserGroupLists(userGroupList);
                if(results!=null)
                {
                	 for(SearchRetrieveRecordVO rec : results)
                     { 
                     	UserGroup userGroupVO = (UserGroup)rec.getData();
                     	userGroupList.add(userGroupVO);
                     }
                }
               
            }
            catch (Exception e)
            {
               throw new RuntimeException(e);
            }
            
            
            return ugld;
        }
        
        /**
         * Retrieves all active user groups.
         * @param userHandle A user handle for authentication in the coreservice.
         * @return The list of User Groups.
         * @throws Exception If an error occurs in coreservice or during marshalling/unmarshalling.
         */
        public static UserGroupList retrieveActiveUserGroups(String userHandle) throws RuntimeException
        {
            try
            {
                UserGroupHandler ugh = ServiceLocator.getUserGroupHandler(userHandle);

                HashMap<String, String[]> filter = new HashMap<String, String[]>();
                filter.put("operation", new String[]{"searchRetrieve"});
                filter.put("version", new String[]{"1.1"});
             
                String uglXml = ugh.retrieveUserGroups(filter);
                System.out.println(uglXml);
               
                SearchRetrieveResponseVO res = new XmlTransformingBean().transformToSearchRetrieveResponseUserGroup(uglXml);
                UserGroupList userGroupList = new UserGroupList();
                List<UserGroup> userGroupArray = new ArrayList<UserGroup>();
                for (int index = 0; index < res.getNumberOfRecords(); index++)
                {
                	userGroupArray.add((UserGroup) res.getRecords().get(index).getData());
                }
                userGroupList.setUserGroupLists(userGroupArray);
                
                return userGroupList;
            }
            catch (Exception e)
            {
               throw new RuntimeException(e);
            }
        }
    }
}
