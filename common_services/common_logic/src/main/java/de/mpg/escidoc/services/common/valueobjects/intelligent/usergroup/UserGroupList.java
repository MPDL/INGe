
package de.mpg.escidoc.services.common.valueobjects.intelligent.usergroup;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.escidoc.www.services.aa.UserGroupHandler;
import de.mpg.escidoc.services.common.valueobjects.intelligent.IntelligentVO;
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
    public UserGroupList(String filter, String userHandle) throws RuntimeException
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
        private static UserGroupList retrieveUserGroups(String filter, String userHandle) throws RuntimeException
        {
            UserGroupList ugld;
            try
            {
                UserGroupHandler ugh = ServiceLocator.getUserGroupHandler(userHandle);
                String uglXml = ugh.retrieveUserGroups(filter);
                ugld = (UserGroupList)IntelligentVO.unmarshal(uglXml, UserGroupList.class);
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
            
            UserGroupList ugl;
            try
            {
                UserGroupHandler ugh = ServiceLocator.getUserGroupHandler(userHandle);
                
                String filter = "<param><filter></filter></param>";
                /*
                String uglXml = ugh.retrieveUserGroups(filter);
                UserGroupList ugl= (UserGroupList)Grant.Factory.unmarshal(uglXml, UserGroupList.class);
                */
                
                //workaround---------------------------------------------------------
                HttpClient httpClient = new HttpClient();
                PostMethod method = new PostMethod(ServiceLocator.getFrameworkUrl() + "/aa/user-groups/filter");

                method.addRequestHeader("Cookie", "escidocCookie=" + userHandle);

                method.setRequestEntity(new StringRequestEntity(filter));
                httpClient.executeMethod(method);
                if (method.getStatusCode() != 200)
                {
                    throw new RuntimeException("Error: " + method.getResponseBodyAsString());
                }
                      
                
                String resp = method.getResponseBodyAsString();
                
                DocumentBuilderFactory dbf = new net.sf.saxon.dom.DocumentBuilderFactoryImpl();
                DocumentBuilder db = dbf.newDocumentBuilder();  
                Document doc = db.parse(method.getResponseBodyAsStream() );
                
                NodeList childnodes = doc.getDocumentElement().getChildNodes();
                
                if (childnodes.getLength()==0)
                {
                    return new UserGroupList();
                }
                
                List<String> ids = new ArrayList<String>();
                for(int i = 0; i<childnodes.getLength(); i++)
                {
                    Node n = childnodes.item(i); 
                    if(childnodes.item(i).getNodeType() == Node.ELEMENT_NODE)
                    {
                        //n.getAttributes().getNamedItemNS("xlink","href");
                        String href = n.getAttributes().getNamedItemNS("http://www.w3.org/1999/xlink", "href").getNodeValue();
                        String objid  = href.substring(href.lastIndexOf("/")+1, href.length());
                        ids.add(objid);
                        //System.out.println(objid);
                    }
                }

                ugl = new UserGroupList();
                List<UserGroup> uglList = new ArrayList<UserGroup>();
                for(String id : ids)
                {
                    UserGroup ug = new UserGroup(id, userHandle);
                    uglList.add(ug);
                }

                ugl.setUserGroupLists(uglList);
                
                //workaround end--------------------------------------------------------------
            }
            catch (Exception e)
            {
               throw new RuntimeException(e);
            }

            return ugl;
        }
    }
}
