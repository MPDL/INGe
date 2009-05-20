
package de.mpg.escidoc.services.common.valueobjects.intelligent.usergroup;

import de.mpg.escidoc.services.common.valueobjects.intelligent.IntelligentVO;
import de.mpg.escidoc.services.common.valueobjects.intelligent.commontypes.ReadOnlyLink;

/** 
 * 
 <create>discarded</create>
 <update>discarded</update>
 
 * 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:element xmlns:ns="http://www.escidoc.de/schemas/commontypes/0.4" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="resources">
 *   &lt;xs:complexType>
 *     &lt;xs:sequence>
 *       &lt;xs:element type="ns:readOnlyLink" name="current-grants"/>
 *     &lt;/xs:sequence>
 *     &lt;xs:attribute type="xs:string" name="last-modification-date"/>
 *   &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 */
public class Resources extends IntelligentVO
{
    private ReadOnlyLink currentGrants;
    private String lastModificationDate;

    /** 
     * Get the 'current-grants' element value. 
     <create>discarded</create>
     <update>discarded</update>
     
     * 
     * @return value
     */
    public ReadOnlyLink getCurrentGrants() {
        return currentGrants;
    }

    /** 
     * Set the 'current-grants' element value. 
     <create>discarded</create>
     <update>discarded</update>
     
     * 
     * @param currentGrants
     */
    public void setCurrentGrants(ReadOnlyLink currentGrants) {
        this.currentGrants = currentGrants;
    }

    /** 
     * Get the 'last-modification-date' attribute value. 
     <create>discarded</create>
     <update>discarded (see note 3.)</update>
     <comment>
     3. This attribute is contained if the
     resources element is retrieved as a sub
     resource. It is not contained in the
     resources element within the user-group
     element.
     </comment>
     
     * 
     * @return value
     */
    public String getLastModificationDate() {
        return lastModificationDate;
    }

    /** 
     * Set the 'last-modification-date' attribute value. 
     <create>discarded</create>
     <update>discarded (see note 3.)</update>
     <comment>
     3. This attribute is contained if the
     resources element is retrieved as a sub
     resource. It is not contained in the
     resources element within the user-group
     element.
     </comment>
     
     * 
     * @param lastModificationDate
     */
    public void setLastModificationDate(String lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }
}
