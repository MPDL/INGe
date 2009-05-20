
package de.mpg.escidoc.services.common.valueobjects.intelligent.usergroup;

import de.mpg.escidoc.services.common.valueobjects.intelligent.IntelligentVO;


/** 
 * 
 <create>discarded</create>
 <update>discarded</update>
 
 * 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:element xmlns:ns="http://www.escidoc.de/schemas/commontypes/0.4" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="selector">
 *   &lt;xs:complexType>
 *     &lt;xs:simpleContent>
 *       &lt;xs:extension base="xs:string">
 *         &lt;xs:attribute type="xs:string" name="name"/>
 *         &lt;xs:attribute type="xs:string" name="type">
 *           &lt;xs:simpleType>
 *             &lt;xs:restriction base="xs:string">
 *               &lt;xs:enumeration value="internal"/>
 *               &lt;xs:enumeration value="external"/>
 *             &lt;/xs:restriction>
 *           &lt;/xs:simpleType>
 *         &lt;/xs:attribute>
 *         &lt;xs:attributeGroup ref="ns:eSciDocResourceIdentityAttributes"/>
 *       &lt;/xs:extension>
 *     &lt;/xs:simpleContent>
 *   &lt;/xs:complexType>
 * &lt;/xs:element>
 * 
 * &lt;xs:attributeGroup xmlns:ns="http://www.escidoc.de/schemas/commontypes/0.4" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="eSciDocResourceIdentityAttributes">
 *   &lt;xs:attribute type="xs:string" name="objid"/>
 * &lt;/xs:attributeGroup>
 * </pre>
 */
public class Selector extends IntelligentVO
{
    private String string;
    private String name;
    private Type type;
    private String objid;

    /** 
     * Get the extension value.
     * 
     * @return value
     */
    public String getString() {
        return string;
    }

    /** 
     * Set the extension value.
     * 
     * @param string
     */
    public void setString(String string) {
        this.string = string;
    }

    /** 
     * Get the 'name' attribute value. 
     <create>discarded</create>
     <update>discarded</update>
     
     * 
     * @return value
     */
    public String getName() {
        return name;
    }

    /** 
     * Set the 'name' attribute value. 
     <create>discarded</create>
     <update>discarded</update>
     
     * 
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /** 
     * Get the 'type' attribute value. 
     <create>discarded</create>
     <update>discarded</update>
     
     * 
     * @return value
     */
    public Type getType() {
        return type;
    }

    /** 
     * Set the 'type' attribute value. 
     <create>discarded</create>
     <update>discarded</update>
     
     * 
     * @param type
     */
    public void setType(Type type) {
        this.type = type;
    }

    /** 
     * Get the 'objid' attribute value. <create>discarded</create><update>discarded</update>
     * 
     * @return value
     */
    public String getObjid() {
        return objid;
    }

    /** 
     * Set the 'objid' attribute value. <create>discarded</create><update>discarded</update>
     * 
     * @param objid
     */
    public void setObjid(String objid) {
        this.objid = objid;
    }
    
    /** 
     * Schema fragment(s) for this class:
     * <pre>
     * &lt;xs:simpleType xmlns:xs="http://www.w3.org/2001/XMLSchema">
     *   &lt;xs:restriction base="xs:string">
     *     &lt;xs:enumeration value="internal"/>
     *     &lt;xs:enumeration value="external"/>
     *   &lt;/xs:restriction>
     * &lt;/xs:simpleType>
     * </pre>
     */
    public static enum Type {
        INTERNAL("internal"), EXTERNAL("external");
        private final String value;

        private Type(String value) {
            this.value = value;
        }

        public String toString() {
            return value;
        }

        public static Type convert(String value) {
            for (Type inst : values()) {
                if (inst.toString().equals(value)) {
                    return inst;
                }
            }
            return null;
        }
    }

}
