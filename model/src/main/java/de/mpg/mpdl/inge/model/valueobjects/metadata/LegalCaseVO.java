/**
 *
 */
package de.mpg.mpdl.inge.model.valueobjects.metadata;

import com.fasterxml.jackson.annotation.JsonInclude;

import de.mpg.mpdl.inge.model.valueobjects.ValueObject;

/**
 * @author gerga
 *
 *         JUS-specific VO
 *
 */
@SuppressWarnings("serial")
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class LegalCaseVO extends ValueObject implements Cloneable {
  private String title;
  private String courtName;
  private String identifier;
  private String datePublished;

  public String getTitle() {
    return this.title;
  }

  public void setTitle(String newVal) {
    this.title = newVal;
  }

  public String getIdentifier() {
    return this.identifier;
  }

  public void setIdentifier(String newVal) {
    this.identifier = newVal;
  }

  public String getDatePublished() {
    return this.datePublished;
  }

  public void setDatePublished(String newVal) {
    this.datePublished = newVal;
  }

  public String getCourtName() {
    return this.courtName;
  }

  public void setCourtName(String newVal) {
    this.courtName = newVal;
  }

  public final LegalCaseVO clone() {
    try {
      LegalCaseVO clone = (LegalCaseVO) super.clone();
      return clone;
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((null == this.courtName) ? 0 : this.courtName.hashCode());
    result = prime * result + ((null == this.datePublished) ? 0 : this.datePublished.hashCode());
    result = prime * result + ((null == this.identifier) ? 0 : this.identifier.hashCode());
    result = prime * result + ((null == this.title) ? 0 : this.title.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;

    if (null == obj)
      return false;

    if (getClass() != obj.getClass())
      return false;

    LegalCaseVO other = (LegalCaseVO) obj;

    if (null == this.courtName) {
      if (null != other.courtName)
        return false;
    } else if (!this.courtName.equals(other.courtName))
      return false;

    if (null == this.datePublished) {
      if (null != other.datePublished)
        return false;
    } else if (!this.datePublished.equals(other.datePublished))
      return false;

    if (null == this.identifier) {
      if (null != other.identifier)
        return false;
    } else if (!this.identifier.equals(other.identifier))
      return false;

    if (null == this.title) {
      if (null != other.title)
        return false;
    } else if (!this.title.equals(other.title))
      return false;

    return true;
  }

}
