/**
 *
 */
package de.mpg.mpdl.inge.model.valueobjects.metadata;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import de.mpg.mpdl.inge.model.valueobjects.ValueObject;

/**
 * @author gerga
 *
 *         JUS-specific VO
 *
 */
@SuppressWarnings("serial")
@JsonInclude(value = Include.NON_EMPTY)
public class LegalCaseVO extends ValueObject implements Cloneable {
  private String title;
  private String courtName;
  private String identifier;
  private String datePublished;

  public String getTitle() {
    return title;
  }

  public void setTitle(String newVal) {
    this.title = newVal;
  }

  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String newVal) {
    this.identifier = newVal;
  }

  public String getDatePublished() {
    return datePublished;
  }

  public void setDatePublished(String newVal) {
    this.datePublished = newVal;
  }

  public String getCourtName() {
    return courtName;
  }

  public void setCourtName(String newVal) {
    this.courtName = newVal;
  }

  public LegalCaseVO clone() {
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
    result = prime * result + ((courtName == null) ? 0 : courtName.hashCode());
    result = prime * result + ((datePublished == null) ? 0 : datePublished.hashCode());
    result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
    result = prime * result + ((title == null) ? 0 : title.hashCode());
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

    LegalCaseVO other = (LegalCaseVO) obj;

    if (courtName == null) {
      if (other.courtName != null)
        return false;
    } else if (!courtName.equals(other.courtName))
      return false;

    if (datePublished == null) {
      if (other.datePublished != null)
        return false;
    } else if (!datePublished.equals(other.datePublished))
      return false;

    if (identifier == null) {
      if (other.identifier != null)
        return false;
    } else if (!identifier.equals(other.identifier))
      return false;

    if (title == null) {
      if (other.title != null)
        return false;
    } else if (!title.equals(other.title))
      return false;

    return true;
  }

}
