package de.mpg.mpdl.inge.model.valueobjects.intelligent.usergroup;

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
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.intelligent.IntelligentVO;
import de.mpg.mpdl.inge.model.valueobjects.intelligent.grants.Grant;
import de.mpg.mpdl.inge.model.valueobjects.intelligent.grants.GrantList;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.services.util.ProxyHelper;

/**
 * Schema fragment(s) for this class:
 * 
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
public class UserGroupList extends IntelligentVO {
  private List<UserGroup> userGroupListList = new ArrayList<UserGroup>();

  /**
   * Retrieves a list of User Groups.
   * 
   * @param filter The filter for the user group list.
   * @param userHandle A user handle for authentication in the coreservice.
   * @throws Exception If an error occurs in coreservice or during marshalling/unmarshalling.
   */
  public UserGroupList(HashMap<String, String[]> filter, String userHandle) throws RuntimeException {}

  public UserGroupList() {

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


}
