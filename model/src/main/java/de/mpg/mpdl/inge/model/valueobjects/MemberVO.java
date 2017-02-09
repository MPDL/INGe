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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((memberId == null) ? 0 : memberId.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;

    if (obj == null)
      return false;

    if (getClass() != obj.getClass())
      return false;

    MemberVO other = (MemberVO) obj;

    if (memberId == null) {
      if (other.memberId != null)
        return false;
    } else if (!memberId.equals(other.memberId))
      return false;

    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;

    return true;
  }

}
