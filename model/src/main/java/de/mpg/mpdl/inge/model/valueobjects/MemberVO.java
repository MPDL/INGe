package de.mpg.mpdl.inge.model.valueobjects;

public class MemberVO {
  private String memberId;
  private String name;

  /**
   * Get the extension value.
   * 
   * @return value
   */
  public String getMemberId() {
    return memberId;
  }

  /**
   * Set the extension value.
   * 
   * @param memberId
   */
  public void setMemberId(String string) {
    this.memberId = string;
  }

  /**
   * Get the 'name' attribute value. <create>discarded</create> <update>discarded</update>
   * 
   * 
   * @return value
   */
  public String getName() {
    return name;
  }

  /**
   * Set the 'name' attribute value. <create>discarded</create> <update>discarded</update>
   * 
   * 
   * @param name
   */
  public void setName(String name) {
    this.name = name;
  }

}
