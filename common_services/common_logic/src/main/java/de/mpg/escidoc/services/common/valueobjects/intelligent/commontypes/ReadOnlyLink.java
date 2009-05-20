
package de.mpg.escidoc.services.common.valueobjects.intelligent.commontypes;

import de.mpg.escidoc.services.common.valueobjects.intelligent.IntelligentVO;

/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:complexType xmlns:xs="http://www.w3.org/2001/XMLSchema" name="readOnlyLink">
 *   &lt;xs:attribute type="xs:string" name="objid"/>
 * &lt;/xs:complexType>
 * </pre>
 */
public class ReadOnlyLink extends IntelligentVO
{
    private String objid;

    /** 
     * Get the 'objid' attribute value. 
     <create>discarded</create>
     <update>discarded</update>
     
     * 
     * @return value
     */
    public String getObjid() {
        return objid;
    }

    /** 
     * Set the 'objid' attribute value. 
     <create>discarded</create>
     <update>discarded</update>
     
     * 
     * @param objid
     */
    public void setObjid(String objid) {
        this.objid = objid;
    }
}
