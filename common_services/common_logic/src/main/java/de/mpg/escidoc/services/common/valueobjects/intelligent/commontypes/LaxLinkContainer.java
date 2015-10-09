
package de.mpg.escidoc.services.common.valueobjects.intelligent.commontypes;

import de.mpg.escidoc.services.common.valueobjects.intelligent.IntelligentVO;

/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:complexType xmlns:xs="http://www.w3.org/2001/XMLSchema" name="laxLinkContainer">
 *   &lt;xs:attribute type="xs:string" use="required" name="objid"/>
 * &lt;/xs:complexType>
 * </pre>
 */
public class LaxLinkContainer extends IntelligentVO
{
    private String objid;

    /** 
     * Get the 'objid' attribute value. 
     <create>
     <input>required</input>
     <output>kept</output>
     </create>
     <update>
     <input>required</input>
     <output>kept</output>
     </update>
     
     * 
     * @return value
     */
    public String getObjid() {
        return objid;
    }

    /** 
     * Set the 'objid' attribute value. 
     <create>
     <input>required</input>
     <output>kept</output>
     </create>
     <update>
     <input>required</input>
     <output>kept</output>
     </update>
     
     * 
     * @param objid
     */
    public void setObjid(String objid) {
        this.objid = objid;
    }
}
