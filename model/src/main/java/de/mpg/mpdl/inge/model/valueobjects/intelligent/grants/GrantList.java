package de.mpg.mpdl.inge.model.valueobjects.intelligent.grants;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import de.escidoc.www.services.aa.UserAccountHandler;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.intelligent.IntelligentVO;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * 
 <para> Following you will find information which elements and attributes are "required",
 * "optional", "not-allowed" or will be "discarded" in the input XML-stream when creating or
 * updating such an object. </para> <para> In "create" the rule for creating a new object is
 * defined. </para> <para> In "update" the rule for updating an object is defined. </para> <para>
 * Possible values are: required | optional | not-allowed | discarded </para> <para> required: this
 * element or attribute has to be delivered </para> <para> optional: this element or attribute can
 * be delivered and will be kept </para> <para> not-allowed: this element or attribute is not
 * allowed in delivery and will cause an exception </para> <para> discarded: this element or
 * attribute can be delivered but will not be used </para>
 * 
 * <comment>This is a read-only resource representing a list of the current grants of the user
 * account. Neither create nor update is supported for this list.</comment>
 * 
 * 
 * Schema fragment(s) for this class:
 * 
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
public class GrantList extends CurrentGrants {
  private int numberOfRecords;
  private int limit;
  private int offset;


  public GrantList() {

  }

  /**
   * Retrieves the grants for a given filter
   * 
   * @param userHandle A user handle for authentication in the coreservice.
   * @param filter Map with filter conditions
   * @throws Exception If an error occurs in coreservice or during marshalling/unmarshalling.
   */
  public GrantList(String userHandle, HashMap<String, String[]> filter) throws RuntimeException {}

  public int getNumberOfRecords() {
    return numberOfRecords;
  }

  public void setNumberOfRecords(int numberOfRecords) {
    this.numberOfRecords = numberOfRecords;
  }

  public int getLimit() {
    return limit;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }

  public int getOffset() {
    return offset;
  }

  public void setOffset(int offset) {
    this.offset = offset;
  }


}
